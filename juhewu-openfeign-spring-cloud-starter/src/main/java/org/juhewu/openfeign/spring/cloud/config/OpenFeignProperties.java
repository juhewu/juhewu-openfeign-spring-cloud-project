package org.juhewu.openfeign.spring.cloud.config;

import java.util.List;

import lombok.Data;

import org.juhewu.openfeign.spring.cloud.constant.FeignConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置文件映射
 *
 * @author duanjw
 */
@Data
@ConfigurationProperties(prefix = "juhewu.openfeign")
public class OpenFeignProperties {

    /**
     * 非负载均衡
     */
    private NonLoadBalancer nonLoadBalancer;

    private PassToken passToken;
    /**
     * 请求头
     */
    private PassHeader passHeader;

    /**
     * 非负载均衡
     */
    @Data
    public static class NonLoadBalancer {

        /**
         * 是否启用
         */
        private Boolean enable;

        /**
         * http url
         * <p>
         * eg.
         * localhost
         * http://localhost
         * http://localhost:8888
         * http://localhost/test
         * http://localhost/test/
         */
        private String url;

        /**
         * 是否跳过前缀，前缀就是服务名
         * 当 skipPrefix 为 true 时，会拼接成 http://{url}/path
         * 当 skipPrefix 为 false 或不写时，会拼接成 http://{url}/{service-id}path
         */
        private boolean skipPrefix;
    }

    /**
     * Token
     */
    @Data
    public static class PassToken {

        /**
         * Token 是否传递
         */
        private boolean enable;
        /**
         * URL 中 Token 的名字
         */
        private String urlTokenName = FeignConstant.ACCESS_TOKEN;
    }
    /**
     * 请求头
     */
    @Data
    public static class PassHeader {
        /**
         * Header 是否传递
         */
        private boolean enable;

        /**
         * 请求头中需要传递的 keys
         */
        private List<String> keys;
    }
}
