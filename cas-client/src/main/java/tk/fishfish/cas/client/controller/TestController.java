package tk.fishfish.cas.client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 测试
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@RestController
@RequestMapping("/v1/test")
public class TestController {

    @Value("${cas.client-host-url}")
    private String clientHostUrl;

    @GetMapping("/principal")
    public Principal me(Principal principal) {
        return principal;
    }

    @ResponseBody
    @GetMapping("/info")
    public String info() {
        return clientHostUrl;
    }

}
