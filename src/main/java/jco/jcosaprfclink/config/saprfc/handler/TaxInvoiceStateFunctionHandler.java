package jco.jcosaprfclink.config.saprfc.handler;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.server.JCoServerContext;
import jco.jcosaprfclink.config.saprfc.SAPDataMapper;
import jco.jcosaprfclink.config.saprfc.interfaces.SAPFunctionHandler;
import jco.jcosaprfclink.service.TaxInvoiceStateService;
import jco.jcosaprfclink.type.HttpMethod;
import jco.jcosaprfclink.utils.HttpUtil;
import jco.jcosaprfclink.utils.JsonParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaxInvoiceStateFunctionHandler implements SAPFunctionHandler {
    private final TaxInvoiceStateService taxInvoiceStateService;
    private final SAPDataMapper dataMapper;

    @Value("${api.dev_url}")
    private String apiUrl;

    @Override
    public void handle(JCoServerContext context, JCoFunction function) {
        try {
            List<Map<String, Object>> taxInvoiceStateList = dataMapper.setRFCExport("T_IF_ZTAXT020", function);
            
            // 1. 외부 API 호출을 위한 JSON 변환
            JSONArray jsonArray = new JSONArray();
            for (Map<String, Object> taxinvoiceStateMap : taxInvoiceStateList) {
                JSONObject info = JsonParserUtil.getJsonObjectFromMap(taxinvoiceStateMap);
                jsonArray.add(info);
            }
            log.info("[Handler] 외부 API 호출 데이터: {}", jsonArray.toJSONString());

            // 2. 외부 API 호출
            String result = HttpUtil.sendHttpRequest(
                apiUrl + "/resultTaxInvoice", 
                HttpMethod.POST, 
                jsonArray.toJSONString(), 
                null
            );
            log.info("[Handler] 외부 API 응답: {}", result);

            // 3. 응답 처리 및 SAP 테이블 업데이트
            JSONArray jsonArrayResult = JsonParserUtil.getJsonArrayFromString(result);
            JCoTable jCoTable = function.getTableParameterList().getTable("T_IF_ZTAXT020");
            
            for (int i = 0; i < jsonArrayResult.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArrayResult.get(i);
                jCoTable.setRow(i);
                
                if (!jsonObject.get("errCode").equals("KH_COM_0000")) {
                    log.error("[Handler] 에러 발생 - 코드: {}, 메시지: {}", 
                        jsonObject.get("errCode"), 
                        jsonObject.get("errMsg"));
                    setErrorResponse(jCoTable, jsonObject);
                } else {
                    setSuccessResponse(jCoTable, jsonObject);
                }
            }

            // 4. 데이터베이스 저장
            taxInvoiceStateService.processTaxInvoice(taxInvoiceStateList);
            
            // 5. SAP 응답 설정
            function.getImportParameterList().setValue("E_RETURN", "S");
            function.getImportParameterList().setValue("E_MESSAGE", "처리 완료");
            
            log.info("[Handler] 세금계산서 상태 처리 완료");
        } catch (Exception e) {
            log.error("[Handler] 세금계산서 처리 중 오류 발생: {}", e.getMessage(), e);
            handleError(context, function, e);
        }
    }

    @Override
    public String getFunctionName() {
        return "ZTAXT020";
    }

    private void setErrorResponse(JCoTable jCoTable, JSONObject jsonObject) {
        jCoTable.setValue("RESULT", jsonObject.get("result"));
        jCoTable.setValue("ERR_CODE", jsonObject.get("errCode"));
        jCoTable.setValue("ERR_MSG", jsonObject.get("errMsg"));
    }

    private void setSuccessResponse(JCoTable jCoTable, JSONObject jsonObject) {
        jCoTable.setValue("RESULT", jsonObject.get("result"));
        jCoTable.setValue("SEND_DD", jsonObject.get("issueDd"));
        jCoTable.setValue("APPR_NO", jsonObject.get("apprNo"));
        jCoTable.setValue("ERR_CODE", jsonObject.get("errCode"));
        jCoTable.setValue("ERR_MSG", "세금계산서 조회 완료");
        jCoTable.setValue("DOC_STATE", jsonObject.get("docState"));
        jCoTable.setValue("DOC_STATE_NM", jsonObject.get("docStateNm"));
    }

    private void handleError(JCoServerContext context, JCoFunction function, Throwable ex) {
        try {
            function.getImportParameterList().setValue("E_RETURN", "E");
            function.getImportParameterList().setValue("E_MESSAGE", ex.getMessage());
        } catch (Exception e) {
            log.error("[Handler] 오류 응답 전송 실패: {}", e.getMessage(), e);
        }
    }
} 