package tk.fishfish.cas.client.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.validation.TicketValidationException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 异常过滤器
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class CasExceptionHandlerFilter extends OncePerRequestFilter {

    private final String serverLoginUrl;

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (ServletException e) {
            if (e.getCause() instanceof TicketValidationException) {
                log.warn("tickets验证失败: " + e.getMessage(), e);
                // tickets验证失败 跳转到 cas server
                response.sendRedirect(serverLoginUrl);
                return;
            }
            throw e;
        }
    }
}
