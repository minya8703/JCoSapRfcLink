package jco.jcosaprfclink.config.saprfc;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterField;
import com.sap.conn.jco.JCoParameterFieldIterator;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecordField;
import com.sap.conn.jco.JCoRecordFieldIterator;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ConversionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SAPDataMapper {
    private final JCoFieldConverter fieldConverter;

    /**
     * import 파라미터 세팅
     */
    public void setRFCImport(List<Map<String, Object>> inputMapList, String inputTabName, JCoFunction jCoFunction)
            throws ConversionException {
        JCoParameterList jCoParameterList = jCoFunction.getTableParameterList();
        JCoParameterFieldIterator jCoParameterFieldIterator = jCoParameterList.getParameterFieldIterator();

        JCoParameterField jCoParameterField;
        while (jCoParameterFieldIterator.hasNextField()) {
            jCoParameterField = jCoParameterFieldIterator.nextParameterField();
            if (jCoParameterField.isTable() && jCoParameterField.getName().equals(inputTabName)) {
                JCoTable jCoTable = jCoParameterField.getTable();
                JCoRecordFieldIterator fieldIter = jCoTable.getRecordFieldIterator();
                JCoRecordField field = null;

                for (Map<String, Object> inputMap : inputMapList) {
                    jCoTable.appendRow();
                    while (fieldIter.hasNextField()) {
                        field = fieldIter.nextRecordField();
                        field.setValue(inputMap.get(field.getName()));
                    }
                    fieldIter.reset();
                }
            }
        }
    }

    /**
     * export 파라미터 세팅
     */
    public List<Map<String, Object>> setRFCExport(String outputTabName, JCoFunction jCoFunction) {
        List<Map<String, Object>> outMapList = new ArrayList<>();
        try {
            JCoTable jCoTable = jCoFunction.getTableParameterList().getTable(outputTabName);
            JCoRecordMetaData metaData = jCoFunction.getTableParameterList().getTable(outputTabName).getRecordMetaData();
            for (int i = 0; i < jCoTable.getNumRows(); i++) {
                jCoTable.setRow(i);
                Map<String, Object> outMap = new LinkedHashMap<>();
                for (int j = 0; j < metaData.getFieldCount(); j++) {
                    String fieldName = metaData.getName(j);
                    int type = metaData.getType(j);
                    try {
                        outMap.put(fieldName, fieldConverter.convert(jCoTable, fieldName, type));
                    } catch (Exception e) {
                        log.error("필드 변환 오류: {} (type: {})", fieldName, type, e);
                        outMap.put(fieldName, null); // 오류 시 null 처리
                    }
                }
                outMapList.add(outMap);
            }
        } catch (Exception e) {
            log.error("JCo 데이터 export 변환 오류: {}", e.getMessage(), e);
            throw e;
        }
        return outMapList;
    }
} 