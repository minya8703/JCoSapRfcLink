package jco.jcosaprfclink.config.saprfc.interfaces;

import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerState;

/**
 * SAP 서버 이벤트 리스너 인터페이스
 */
public interface SAPServerEventListener {
    /**
     * 서버 상태 변경 이벤트 처리
     * @param oldState 이전 상태
     * @param newState 새로운 상태
     */
    void onStateChanged(JCoServerState oldState, JCoServerState newState);

    /**
     * 트랜잭션 시작 이벤트 처리
     * @param context 서버 컨텍스트
     * @param tid 트랜잭션 ID
     */
    void onTransactionStarted(JCoServerContext context, String tid);

    /**
     * 트랜잭션 완료 이벤트 처리
     * @param context 서버 컨텍스트
     * @param tid 트랜잭션 ID
     */
    void onTransactionCompleted(JCoServerContext context, String tid);
} 