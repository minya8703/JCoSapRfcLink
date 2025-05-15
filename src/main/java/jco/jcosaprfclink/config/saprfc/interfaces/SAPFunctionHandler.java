package jco.jcosaprfclink.config.saprfc.interfaces;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.server.JCoServerContext;

/**
 * SAP 함수 처리를 위한 핸들러 인터페이스
 */
public interface SAPFunctionHandler {
    /**
     * 함수 처리
     * @param context 서버 컨텍스트
     * @param function JCoFunction 객체
     */
    void handle(JCoServerContext context, JCoFunction function);

    /**
     * 함수 이름 조회
     * @return 함수 이름
     */
    String getFunctionName();
} 