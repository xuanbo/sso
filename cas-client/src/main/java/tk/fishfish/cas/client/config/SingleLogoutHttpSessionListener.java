package tk.fishfish.cas.client.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.List;

/**
 * 单点登出监听器
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class SingleLogoutHttpSessionListener implements HttpSessionListener {

    private final List<SingleLogoutHandler> singleLogoutHandlers;

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        if (session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION) == null) {
            return;
        }
        Assertion assertion = (Assertion) event.getSession().getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
        String username = assertion.getPrincipal().getName();
        log.debug("监听到账号 {} 单点登出", username);
        if (CollectionUtils.isEmpty(singleLogoutHandlers)) {
            return;
        }
        // 处理单点登出
        singleLogoutHandlers.forEach(singleLogoutHandler -> {
            try {
                singleLogoutHandler.logout(username);
            } catch (Exception e) {
                log.error("单点登出处理器错误", e);
            }
        });
    }

}
