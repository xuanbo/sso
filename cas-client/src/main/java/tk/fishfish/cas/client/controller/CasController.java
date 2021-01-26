package tk.fishfish.cas.client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

/**
 * CAS控制器
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@Controller
@RequestMapping("/cas")
public class CasController {

    @Value("${cas.server-login-url}")
    private String serverLoginUrl;

    @Value("${server-logout-url}")
    private String serverLogoutUrl;

    /**
     * 单点登录
     *
     * @return 重定向到CAS服务
     */
    @GetMapping("/login")
    public RedirectView login() {
        return new RedirectView(serverLoginUrl);
    }

    /**
     * 单点退出
     *
     * @param session HttpSession
     * @return 重定向到CAS服务
     */
    @GetMapping("/logout")
    public RedirectView logout(HttpSession session) {
        session.invalidate();
        return new RedirectView(serverLogoutUrl);
    }

}
