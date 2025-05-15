package jco.jcosaprfclink.config.saprfc;

import com.sap.conn.jco.JCoTable;
import org.springframework.stereotype.Component;

public interface JCoFieldConverter {
    Object convert(JCoTable table, String fieldName, int type);
}

@Component
class DefaultJCoFieldConverter implements JCoFieldConverter {
    @Override
    public Object convert(JCoTable table, String fieldName, int type) {
        return switch (type) {
            case 2 -> // float
                    table.getFloat(fieldName);
            case 1 -> // int
                    table.getInt(fieldName);
            // 필요시 더 다양한 타입 추가
            default -> table.getString(fieldName);
        };
    }
} 