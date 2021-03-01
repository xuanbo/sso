package tk.fishfish.cas.client.config;

/**
 * 单点登出处理
 *
 * @author 奔波儿灞
 * @version 1.0.0
 */
@FunctionalInterface
public interface SingleLogoutHandler {

    /**
     * 账号单点登出
     *
     * @param username 账号
     */
    void logout(String username);

}
