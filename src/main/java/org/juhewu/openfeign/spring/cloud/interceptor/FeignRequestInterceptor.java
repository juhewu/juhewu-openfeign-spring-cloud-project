package org.juhewu.openfeign.spring.cloud.interceptor;

import static org.juhewu.openfeign.spring.cloud.constant.FeignConstant.REQUEST_HEADER_AUTHORIZATION;
import static org.juhewu.openfeign.spring.cloud.constant.FeignConstant.TOKEN_SPLIT;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.juhewu.openfeign.spring.cloud.config.OpenFeignProperties;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Feign 请求拦截器，传递 token 和请求头中的参数
 *
 * @author duanjw
 * @since 2019-11-06
 **/
public class FeignRequestInterceptor implements RequestInterceptor {

    private final OpenFeignProperties openFeignProperties;

    public FeignRequestInterceptor(OpenFeignProperties openFeignProperties) {
        this.openFeignProperties = openFeignProperties;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();

            // 传递 token
            passToken(requestTemplate, request);

            if (headerNames != null) {
                // 请求头中的 key 继续传递
                headerPassKeys(requestTemplate, request);
            }
        }
    }

    /**
     * 传递 token，请求头、url 中的 token 都可以传递
     * <p>
     * 1. 请求头中传递：Authorization: Bearer [token]
     * 2. url 中传递：access_token=[token]
     *
     * @param requestTemplate requestTemplate
     * @param request request
     */
    private void passToken(RequestTemplate requestTemplate, HttpServletRequest request) {
        OpenFeignProperties.Token tokenSetting = openFeignProperties.getToken();
        // 不传递 token
        if (!tokenSetting.isEnable()) {
            return;
        }

        // 传递 token，如果请求头中有 token，用 requestTemplate 传递
        final String header = request.getHeader(REQUEST_HEADER_AUTHORIZATION);
        if (header.startsWith(TOKEN_SPLIT)) {
            requestTemplate.header(REQUEST_HEADER_AUTHORIZATION, header);
            return;
        }

        // 传递 token，如果请求头没有 token，从 url 取出 token
        String urlTokenName = tokenSetting.getUrlTokenName();
        final String token = request.getParameter(urlTokenName);
        if (token != null) {
            requestTemplate.header(REQUEST_HEADER_AUTHORIZATION, String.format("%s %s", TOKEN_SPLIT, token));
        }
    }

    /**
     * 请求头中的 key 继续传递
     *
     * @param requestTemplate requestTemplate
     * @param request request
     */
    private void headerPassKeys(RequestTemplate requestTemplate, HttpServletRequest request) {
        for (String passHeadersKey : openFeignProperties.getHeader().getPassKeys()) {
            String header = request.getHeader(passHeadersKey);
            if (null != header) {
                requestTemplate.header(passHeadersKey, header);
            }
        }
    }
}
