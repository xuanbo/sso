package tk.fishfish.cas.client.controller;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Value("${cas.server-logout-url}")
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
     * 前后端分离项目，拿到平台token，最后跳转到前端
     *
     * @param session HttpSession
     * @param front   前端获取token地址
     * @return 跳转到前端获取token地址
     */
    @GetMapping("/ticket")
    public RedirectView ticket(HttpSession session, @RequestParam String front) {
        // CAS 账号
        Assertion assertion = (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
        String username = assertion.getPrincipal().getName();
        // TODO 平台做免密登录，拿到自己的平台的 oauth2 token，最后跳转到前端
        return new RedirectView(front + "?token=xxx");
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
