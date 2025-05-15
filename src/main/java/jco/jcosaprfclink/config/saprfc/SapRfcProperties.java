package jco.jcosaprfclink.config.saprfc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "sap.rfc")
public class SapRfcProperties {
    private List<FunctionDef> functions;

    @Getter @Setter
    public static class FunctionDef {
        private String name;
        private List<ParamDef> importParams;
        private List<ParamDef> exportParams;
        private List<ParamDef> tableParams;
    }
    @Getter @Setter
    public static class ParamDef {
        private String name;
        private String type;
        private int length;
    }
} 