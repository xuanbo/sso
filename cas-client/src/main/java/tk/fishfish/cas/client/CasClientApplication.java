package tk.fishfish.cas.client;

import org.jasig.cas.client.boot.configuration.EnableCasClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 客户端
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@EnableCasClient
@SpringBootApplication(scanBasePackages = {
        "tk.fishfish.cas.client"
})
public class CasClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(CasClientApplication.class, args);
    }

}
