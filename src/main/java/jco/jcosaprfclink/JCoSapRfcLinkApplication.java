package jco.jcosaprfclink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class JCoSapRfcLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(JCoSapRfcLinkApplication.class, args);
    }

}
