package jco.jcosaprfclink.config.saprfc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "sap.connect")
public class SAPConnectionProperties {
    private String host;
    private String sysnr;
    private String client;
    private String user;
    private String passwd;
    private String lang;
} 