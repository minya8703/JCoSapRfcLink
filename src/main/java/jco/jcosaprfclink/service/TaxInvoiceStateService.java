package jco.jcosaprfclink.service;

import com.sap.conn.jco.JCoFunction;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 세금계산서 상태 관리를 위한 서비스 인터페이스
 */
public interface TaxInvoiceStateService {
    /**
     * 세금계산서 상태 정보를 처리합니다.
     * @param taxInvoiceStateList 세금계산서 상태 정보 목록
     */
    void processTaxInvoice(List<Map<String, Object>> taxInvoiceStateList);

    /**
     * 세금계산서 상태 정보를 비동기로 처리합니다.
     * @param taxInvoiceStateList 세금계산서 상태 정보 목록
     * @param function JCoFunction 객체
     * @return CompletableFuture<Void>
     */
    CompletableFuture<Void> processTaxInvoiceAsync(List<Map<String, Object>> taxInvoiceStateList, JCoFunction function);

    /**
     * 세금계산서 상태 정보를 조회합니다.
     * @param invoiceId 세금계산서 ID
     * @return 세금계산서 상태 정보
     */
    Map<String, Object> getTaxInvoiceState(String invoiceId);
}
