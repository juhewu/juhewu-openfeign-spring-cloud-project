package org.juhewu.openfeign.spring.cloud.interceptor;

import static org.juhewu.openfeign.spring.cloud.constant.FeignConstant.REQUEST_HEADER_AUTHORIZATION;
import static org.juhewu.openfeign.spring.cloud.constant.FeignConstant.TOKEN_SPLIT;

import javax.servlet.http.HttpServletRequest;

import org.juhewu.openfeign.spring.cloud.config.OpenFeignProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class FeignRequestTokenInterceptor implements RequestInterceptor {

    private final static Logger log = LoggerFactory.getLogger(FeignRequestTokenInterceptor.class);
    private final OpenFeignProperties openFeignProperties;

    public FeignRequestTokenInterceptor(OpenFeignProperties openFeignProperties) {
        this.openFeignProperties = openFeignProperties;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        // 获取不到 request，直接返回
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();

        // 传递 token
        passToken(requestTemplate, request);
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
        OpenFeignProperties.PassToken passTokenSetting = openFeignProperties.getPassToken();
        // 不传递 token
        if (null == passTokenSetting || !passTokenSetting.isEnable()) {
            return;
        }

        // 传递 token，如果请求头中有 token，用 requestTemplate 传递
        final String header = request.getHeader(REQUEST_HEADER_AUTHORIZATION);
        if (null != header && header.startsWith(TOKEN_SPLIT)) {
            requestTemplate.header(REQUEST_HEADER_AUTHORIZATION, header);
            if (log.isDebugEnabled()) {
                log.debug("openfeign 传递 header 中 key 为 {} 的值作为 token", REQUEST_HEADER_AUTHORIZATION);
            }
            return;
        }

        // 传递 token，如果请求头没有 token，从 url 取出 token
        String urlTokenName = passTokenSetting.getUrlTokenName();
        final String token = request.getParameter(urlTokenName);
        if (token != null) {
            if (log.isDebugEnabled()) {
                log.debug("openfeign 传递 url 中 key 为 {} 的值作为 token", urlTokenName);
            }
            requestTemplate.header(REQUEST_HEADER_AUTHORIZATION, String.format("%s%s", TOKEN_SPLIT, token));
            return;
        }

        if (log.isWarnEnabled()) {
            log.warn("openfeign 不传递 token。在请求头和 url 都没有获取到 token，");
        }
    }
}
