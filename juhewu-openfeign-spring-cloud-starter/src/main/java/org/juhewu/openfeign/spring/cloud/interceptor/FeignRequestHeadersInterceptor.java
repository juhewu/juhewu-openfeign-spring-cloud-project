package org.juhewu.openfeign.spring.cloud.interceptor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
public class FeignRequestHeadersInterceptor implements RequestInterceptor {

    private final static Logger log = LoggerFactory.getLogger(FeignRequestHeadersInterceptor.class);

    private final OpenFeignProperties openFeignProperties;

    public FeignRequestHeadersInterceptor(OpenFeignProperties openFeignProperties) {
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
        Enumeration<String> headerNames = request.getHeaderNames();

        // 请求头中没有任何参数，不需要传递
        if (headerNames == null || openFeignProperties.getPassHeader().getKeys().isEmpty()) {
            return;
        }
        // 传递请求头中的参数
        headerPassKeys(requestTemplate, request);
    }

    /**
     * 请求头中的 key 继续传递
     *
     * @param requestTemplate requestTemplate
     * @param request request
     */
    private void headerPassKeys(RequestTemplate requestTemplate, HttpServletRequest request) {
        List<String> result = new ArrayList<>();
        for (String passHeadersKey : openFeignProperties.getPassHeader().getKeys()) {
            String header = request.getHeader(passHeadersKey);
            if (null != header) {
                requestTemplate.header(passHeadersKey, header);
                result.add(passHeadersKey);
            }
        }
        if (log.isDebugEnabled()) {
            if (result.isEmpty()) {
                log.warn("openfeign 不传递 header 中的任何 key。在请求头中没有匹配到需要传递的 key");
                return;
            }
            log.debug("openfeign 传递 header 中 key：{}", result);
        }
    }
}
