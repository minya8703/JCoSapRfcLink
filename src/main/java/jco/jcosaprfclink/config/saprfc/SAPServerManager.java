package jco.jcosaprfclink.config.saprfc;

import jco.jcosaprfclink.config.saprfc.handler.TaxInvoiceStateFunctionHandler;
import jco.jcosaprfclink.config.saprfc.impl.JCoSAPServer;
import jco.jcosaprfclink.config.saprfc.interfaces.SAPFunctionHandler;
import jco.jcosaprfclink.config.saprfc.interfaces.SAPServer;
import jco.jcosaprfclink.config.saprfc.interfaces.SAPServerEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SAP 서버 관리 클래스
 * 
 * @author JCoSapRfcLink
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SAPServerManager {
    private final SAPServer server;
    private final Map<String, SAPFunctionHandler> functionHandlers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        server.initialize();
        registerDefaultFunctionHandlers();
        log.info("✅ SAP 서버 매니저 초기화 완료");
    }

    /**
     * 기본 함수 핸들러 등록
     */
    private void registerDefaultFunctionHandlers() {
        // 여기에 기본 함수 핸들러 등록

    }

    /**
     * 함수 핸들러 등록
     *
     * @param functionName 함수 이름
     * @param handler 함수 핸들러
     */
    public void registerFunctionHandler(String functionName, SAPFunctionHandler handler) {
        server.registerFunctionHandler(functionName, handler);
        functionHandlers.put(functionName, handler);
        log.info("✅ 함수 핸들러 등록 완료: {}", functionName);
    }

    /**
     * 함수 핸들러 제거
     *
     * @param functionName 함수 이름
     */
    public void removeFunctionHandler(String functionName) {
        server.removeFunctionHandler(functionName);
        functionHandlers.remove(functionName);
        log.info("✅ 함수 핸들러 제거 완료: {}", functionName);
    }

    /**
     * 등록된 모든 함수 핸들러 조회
     *
     * @return 등록된 함수 핸들러 맵
     */
    public Map<String, SAPFunctionHandler> getRegisteredFunctionHandlers() {
        return new ConcurrentHashMap<>(functionHandlers);
    }

    /**
     * 서버 시작
     */
    public void startServer() {
        server.start();
    }

    /**
     * 서버 중지
     */
    public void stopServer() {
        server.stop();
    }

    /**
     * 이벤트 리스너 등록
     *
     * @param listener 이벤트 리스너
     */
    public void addEventListener(SAPServerEventListener listener) {
        if (server instanceof JCoSAPServer) {
            ((JCoSAPServer) server).addEventListener(listener);
        }
    }

    /**
     * 이벤트 리스너 제거
     *
     * @param listener 이벤트 리스너
     */
    public void removeEventListener(SAPServerEventListener listener) {
        if (server instanceof JCoSAPServer) {
            ((JCoSAPServer) server).removeEventListener(listener);
        }
    }

    @PreDestroy
    public void destroy() {
        stopServer();
        log.info("✅ SAP 서버 매니저 종료 완료");
    }
} 