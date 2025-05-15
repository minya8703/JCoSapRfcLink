package jco.jcosaprfclink.utils;

import lombok.extern.slf4j.Slf4j;
import jco.jcosaprfclink.exception.BusinessExceptionHandler;
import jco.jcosaprfclink.type.HttpMethod;
import org.springframework.util.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static jco.jcosaprfclink.type.ErrorCode.*;

/**
 * HTTP 요청 유틸리티 클래스
 * 
 * @author JCoSapRfcLink
 * @version 1.0
 */
@Slf4j
public class HttpUtil {
    private static final int DEFAULT_TIMEOUT = 5000;
    private static final int DEFAULT_RETRY_COUNT = 3;
    private static final int DEFAULT_RETRY_DELAY = 1000;
    private static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();
    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

    static {
        // SSL 인증서 검증 비활성화 (개발 환경용)
        if (Boolean.parseBoolean(System.getProperty("disable.ssl.verification", "false"))) {
            disableSSLVerification();
        }
    }

    /**
     * 범용 HTTP 요청 메서드
     *
     * @param apiUrl 요청할 URL
     * @param method HTTP 메서드 (GET, POST, PUT, DELETE)
     * @param body 요청 본문 (POST, PUT에서 사용, GET에서는 무시됨)
     * @param token 인증 토큰 (필요 없는 경우 null 또는 빈 문자열)
     * @return 응답 본문
     * @throws BusinessExceptionHandler HTTP 요청 실패 시
     * @throws IllegalArgumentException 잘못된 파라미터 입력 시
     */
    public static String sendHttpRequest(String apiUrl, HttpMethod method, String body, String token) {
        validateRequest(apiUrl, method, body);
        return sendHttpRequestWithRetry(apiUrl, method, body, token, DEFAULT_RETRY_COUNT);
    }

    /**
     * 비동기 HTTP 요청 메서드
     *
     * @param apiUrl 요청할 URL
     * @param method HTTP 메서드
     * @param body 요청 본문
     * @param token 인증 토큰
     * @return CompletableFuture<String> 응답 본문
     */
    public static CompletableFuture<String> sendAsyncRequest(String apiUrl, HttpMethod method, String body, String token) {
        return CompletableFuture.supplyAsync(() -> sendHttpRequest(apiUrl, method, body, token));
    }

    /**
     * 재시도 로직이 포함된 HTTP 요청 메서드
     */
    private static String sendHttpRequestWithRetry(String apiUrl, HttpMethod method, String body, String token, int retryCount) {
        int attempts = 0;
        while (attempts < retryCount) {
            try {
                return executeHttpRequest(apiUrl, method, body, token);
            } catch (BusinessExceptionHandler e) {
                attempts++;
                if (attempts == retryCount) {
                    throw e;
                }
                log.warn("HTTP 요청 실패 (시도 {}/{}): {}", attempts, retryCount, e.getMessage());
                try {
                    TimeUnit.MILLISECONDS.sleep(DEFAULT_RETRY_DELAY * attempts);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new BusinessExceptionHandler(IO_ERROR);
                }
            }
        }
        throw new BusinessExceptionHandler(IO_ERROR);
    }

    /**
     * HTTP 요청 실행
     */
    private static String executeHttpRequest(String apiUrl, HttpMethod method, String body, String token) {
        HttpURLConnection connection = null;
        try {
            // 1. URL 및 HttpURLConnection 설정
            connection = createConnection(apiUrl, method, token);

            // 2. 요청 본문 전송
            if (isRequestBodyRequired(method) && StringUtils.hasText(body)) {
                sendRequestBody(connection, body);
            }

            // 3. 응답 처리
            return handleResponse(connection);

        } catch (IOException e) {
            log.error("HTTP 요청 실패: {}", e.getMessage());
            throw new BusinessExceptionHandler(IO_ERROR);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * HTTP 연결 생성
     */
    private static HttpURLConnection createConnection(String apiUrl, HttpMethod method, String token) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // 기본 설정
        connection.setRequestMethod(method.name());
        connection.setRequestProperty("Content-Type", CONTENT_TYPE);
        connection.setConnectTimeout(DEFAULT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_TIMEOUT);
        
        // 인증 토큰 설정
        if (StringUtils.hasText(token)) {
            connection.setRequestProperty("Authorization", "Token " + token);
        }
        
        // 요청 본문 설정
        connection.setDoOutput(isRequestBodyRequired(method));
        
        return connection;
    }

    /**
     * 요청 본문 전송
     */
    private static void sendRequestBody(HttpURLConnection connection, String body) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(connection.getOutputStream(), DEFAULT_CHARSET))) {
            writer.write(body);
            writer.flush();
        }
    }

    /**
     * 응답 처리
     */
    private static String handleResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        handleResponseCode(responseCode);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    responseCode >= 200 && responseCode < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream(),
                    DEFAULT_CHARSET))) {
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    /**
     * HTTP 응답 코드 처리
     */
    private static void handleResponseCode(int responseCode) {
        switch (responseCode) {
            case 200, 201 -> log.info("✅ 성공: 응답 코드 {}", responseCode);
            case 400 -> {
                log.warn("❌ 400 Bad Request: 요청 오류");
                throw new BusinessExceptionHandler(INVALID_REQUEST);
            }
            case 401 -> {
                log.warn("❌ 401 Unauthorized: 인증 실패");
                throw new BusinessExceptionHandler(UNAUTHORIZED);
            }
            case 403 -> {
                log.warn("❌ 403 Forbidden: 접근 권한 없음");
                throw new BusinessExceptionHandler(FORBIDDEN);
            }
            case 404 -> {
                log.warn("❌ 404 Not Found: 리소스를 찾을 수 없음");
                throw new BusinessExceptionHandler(NOT_FOUND);
            }
            case 408 -> {
                log.warn("❌ 408 Request Timeout: 요청 시간 초과");
                throw new BusinessExceptionHandler(REQUEST_TIMEOUT);
            }
            case 500 -> {
                log.warn("❌ 500 Internal Server Error: 서버 오류");
                throw new BusinessExceptionHandler(INTERNAL_SERVER_ERROR);
            }
            case 503 -> {
                log.warn("❌ 503 Service Unavailable: 서비스 사용 불가");
                throw new BusinessExceptionHandler(SERVICE_UNAVAILABLE);
            }
            default -> log.info("응답 코드: {}", responseCode);
        }
    }

    /**
     * 요청 파라미터 유효성 검사
     */
    private static void validateRequest(String apiUrl, HttpMethod method, String body) {
        if (!StringUtils.hasText(apiUrl)) {
            throw new IllegalArgumentException("API URL cannot be null or empty");
        }
        if (method == null) {
            throw new IllegalArgumentException("HTTP method cannot be null");
        }
        if (isRequestBodyRequired(method) && !StringUtils.hasText(body)) {
            throw new IllegalArgumentException("Request body is required for " + method.name());
        }
    }

    /**
     * 요청 본문이 필요한 메서드인지 확인
     */
    private static boolean isRequestBodyRequired(HttpMethod method) {
        return method == HttpMethod.POST || method == HttpMethod.PUT;
    }

    /**
     * SSL 인증서 검증 비활성화 (개발 환경용)
     */
    private static void disableSSLVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            log.error("SSL 인증서 검증 비활성화 실패: {}", e.getMessage());
        }
    }
}