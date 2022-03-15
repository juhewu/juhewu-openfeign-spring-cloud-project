package org.juhewu.openfeign.spring.cloud.constant;

import lombok.experimental.UtilityClass;

/**
 * 常量
 *
 * @author duanjw
 * @since 2017/10/29
 */
@UtilityClass
public class FeignConstant {

    /**
     * 请求头-token请求头名称
     */
    public static final String REQUEST_HEADER_AUTHORIZATION = "Authorization";

    /**
     * url中token的key
     */
    public static final String ACCESS_TOKEN = "access_token";

    /**
     * token分割符
     */
    public static final String TOKEN_SPLIT = "Bearer ";

}
