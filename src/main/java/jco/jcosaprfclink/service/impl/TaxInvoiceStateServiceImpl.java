package jco.jcosaprfclink.service.impl;

import com.sap.conn.jco.JCoFunction;
import jco.jcosaprfclink.domain.StateTaxinvoice;
import jco.jcosaprfclink.repository.TaxinvoiceStateRepository;
import jco.jcosaprfclink.service.TaxInvoiceStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaxInvoiceStateServiceImpl implements TaxInvoiceStateService {
    private final TaxinvoiceStateRepository taxinvoiceStateRepository;

    @Override
    @Transactional
    public void processTaxInvoice(List<Map<String, Object>> taxInvoiceStateList) {
        try {
            List<StateTaxinvoice> stateTaxinvoices = taxInvoiceStateList.stream()
                .map(this::convertToStateTaxinvoice)
                .collect(Collectors.toList());

            taxinvoiceStateRepository.saveAll(stateTaxinvoices);
            log.info("[Service] 세금계산서 상태 업데이트 완료: {}건", stateTaxinvoices.size());
        } catch (Exception e) {
            log.error("[Service] 세금계산서 상태 업데이트 실패: {}", e.getMessage(), e);
            throw new RuntimeException("세금계산서 상태 업데이트 실패", e);
        }
    }

    @Override
    @Async
    public CompletableFuture<Void> processTaxInvoiceAsync(List<Map<String, Object>> taxInvoiceStateList, JCoFunction function) {
        return CompletableFuture.runAsync(() -> {
            try {
                processTaxInvoice(taxInvoiceStateList);
                // SAP 시스템에 성공 응답 전송
                function.getImportParameterList().setValue("E_RETURN", "S");
                function.getImportParameterList().setValue("E_MESSAGE", "처리 완료");
            } catch (Exception e) {
                log.error("[Service] 비동기 세금계산서 처리 실패: {}", e.getMessage(), e);
                // SAP 시스템에 실패 응답 전송
                function.getImportParameterList().setValue("E_RETURN", "E");
                function.getImportParameterList().setValue("E_MESSAGE", e.getMessage());
                throw new RuntimeException("비동기 세금계산서 처리 실패", e);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTaxInvoiceState(String invoiceId) {
        try {
            StateTaxinvoice stateTaxinvoice = taxinvoiceStateRepository.findByInvoiceId(invoiceId)
                .orElseThrow(() -> new RuntimeException("세금계산서를 찾을 수 없습니다: " + invoiceId));

            return Map.of(
                "invoiceId", stateTaxinvoice.getInvoiceId(),
                "status", stateTaxinvoice.getStatus(),
                "updateDate", stateTaxinvoice.getUpdateDate()
            );
        } catch (Exception e) {
            log.error("[Service] 세금계산서 상태 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("세금계산서 상태 조회 실패", e);
        }
    }

    private StateTaxinvoice convertToStateTaxinvoice(Map<String, Object> data) {
        // Map을 StateTaxinvoice 엔티티로 변환하는 로직
        StateTaxinvoice stateTaxinvoice = new StateTaxinvoice();
        // 변환 로직 구현
        return stateTaxinvoice;
    }
} 