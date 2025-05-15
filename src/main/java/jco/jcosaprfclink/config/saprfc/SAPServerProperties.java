package jco.jcosaprfclink.config.saprfc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jco.server")
public class SAPServerProperties {
    private String connectionCount;
    private String threadCount;
    private String progid;
    private String repoDestination;
    private String gwserv;
    private String gwhost;
    private String programId;

    public String getProgramId() {
        return programId;
    }
} 