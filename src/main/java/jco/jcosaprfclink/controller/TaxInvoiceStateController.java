package jco.jcosaprfclink.controller;

import jco.jcosaprfclink.config.aop.TimeTrace;
import jco.jcosaprfclink.dto.ResponseResult;
import jco.jcosaprfclink.service.TaxInvoiceStateService;
import jco.jcosaprfclink.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/tax-invoice")
@RequiredArgsConstructor
public class TaxInvoiceStateController {
    private final TaxInvoiceStateService taxInvoiceStateService;

    /**
     * 세금계산서 상태 정보를 일괄 업데이트합니다.
     * @param taxInvoiceStateList 세금계산서 상태 정보 목록
     * @return 처리 결과
     */
    @PostMapping("/state")
    @TimeTrace
    public ResponseEntity<ResponseResult<Void>> updateTaxInvoiceState(
            @RequestBody List<Map<String, Object>> taxInvoiceStateList) {
        try {
            taxInvoiceStateService.processTaxInvoice(taxInvoiceStateList);
            return ResponseEntity.ok(ResponseResult.success());
        } catch (Exception e) {
            log.error("[Controller] 세금계산서 상태 업데이트 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ResponseResult.error("세금계산서 상태 업데이트 중 오류가 발생했습니다: " + e.getMessage(), 
                    ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * 세금계산서 상태 정보를 조회합니다.
     * @param invoiceId 세금계산서 ID
     * @return 세금계산서 상태 정보
     */
    @GetMapping("/state/{invoiceId}")
    @TimeTrace
    public ResponseEntity<ResponseResult<Map<String, Object>>> getTaxInvoiceState(
            @PathVariable String invoiceId) {
        try {
            Map<String, Object> state = taxInvoiceStateService.getTaxInvoiceState(invoiceId);
            return ResponseEntity.ok(ResponseResult.success(state));
        } catch (Exception e) {
            log.error("[Controller] 세금계산서 상태 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ResponseResult.error("세금계산서 상태 조회 중 오류가 발생했습니다: " + e.getMessage(), 
                    ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }
}
