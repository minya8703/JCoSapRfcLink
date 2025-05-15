package jco.jcosaprfclink.config.saprfc.impl;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.server.JCoServerContext;
import jco.jcosaprfclink.config.saprfc.interfaces.SAPFunctionHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractSAPFunctionHandler implements SAPFunctionHandler {
    
    @Override
    public void handle(JCoServerContext context, JCoFunction function) {
        try {
            validateContext(context);
            processFunction(context, function);
        } catch (Exception e) {
            log.error("❌ 함수 처리 중 오류 발생: {}", e.getMessage());
            handleError(context, function, e);
        }
    }

    /**
     * 컨텍스트 유효성 검사
     * @param context 서버 컨텍스트
     */
    protected void validateContext(JCoServerContext context) {
        if (context == null) {
            throw new IllegalArgumentException("서버 컨텍스트가 null입니다.");
        }
    }

    /**
     * 함수 처리 로직
     * @param context 서버 컨텍스트
     * @param function JCoFunction 객체
     */
    protected abstract void processFunction(JCoServerContext context, JCoFunction function);

    /**
     * 오류 처리
     * @param context 서버 컨텍스트
     * @param function JCoFunction 객체
     * @param e 발생한 예외
     */
    protected void handleError(JCoServerContext context, JCoFunction function, Exception e) {
        // 기본 오류 처리 구현
        log.error("함수 처리 중 오류 발생: {}", e.getMessage());
    }

    /**
     * 함수 이름 조회
     * @return 함수 이름
     */
    @Override
    public abstract String getFunctionName();
} 