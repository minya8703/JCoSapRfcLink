package jco.jcosaprfclink.config.saprfc.interfaces;

import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerState;

/**
 * SAP 서버의 핵심 기능을 정의하는 인터페이스
 */
public interface SAPServer {
    /**
     * 서버 초기화
     */
    void initialize();

    /**
     * 서버 시작
     */
    void start();

    /**
     * 서버 중지
     */
    void stop();

    /**
     * 서버 상태 조회
     * @return 서버 상태
     */
    JCoServerState getState();

    /**
     * 함수 핸들러 등록
     * @param functionName 함수 이름
     * @param handler 함수 핸들러
     */
    void registerFunctionHandler(String functionName, SAPFunctionHandler handler);

    /**
     * 함수 핸들러 제거
     * @param functionName 함수 이름
     */
    void removeFunctionHandler(String functionName);

    /**
     * 트랜잭션 처리
     * @param context 서버 컨텍스트
     * @param tid 트랜잭션 ID
     */
    void handleTransaction(JCoServerContext context, String tid);
} 