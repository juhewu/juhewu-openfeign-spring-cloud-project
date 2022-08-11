package org.juhewu.openfeign.spring.cloud.config;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import feign.Client;
import feign.Request;
import feign.Response;

/**
 * 负载均衡转为非负载均衡
 *
 * @author duanjw
 */
public class ConvertToNonLoadBalancerClient implements Client {

    private final Client delegate;
    private final String url;
    private final boolean skipPrefix;

    ConvertToNonLoadBalancerClient(Client delegate, String url, boolean skipPrefix) {
        this.delegate = delegate;

        this.url = url;
        this.skipPrefix = skipPrefix;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {

        Request modifiedRequest = getModifyRequest(request);
        return this.delegate.execute(modifiedRequest, options);
    }

    /**
     * 请求转为 http 非负载均衡请求
     * 如：
     *
     * @param request 原 request
     * @return 修改后的 request
     */
    private Request getModifyRequest(Request request) {

        Map<String, Collection<String>> headers = new LinkedHashMap<>(request.headers());
        URI asUri = URI.create(request.url());
        String clientName = asUri.getHost();
        // 重构请求的 url
        String newUrl = url;

        // 默认使用 http
        String defaultHttpMethod = "http";
        if (!newUrl.startsWith(defaultHttpMethod)) {
            String doubleSlash = "://";
            newUrl = defaultHttpMethod + doubleSlash + newUrl;
        }
        String slash = "/";
        if (!newUrl.endsWith(slash)) {
            newUrl += slash;
        }

        // 如果不跳过前缀，追加前缀，前缀就是服务名
        if (!skipPrefix) {
            newUrl += clientName;
        }

        String path = asUri.getPath();

        // 如果有两个 //，删掉一个
        if (newUrl.endsWith(slash) && path.startsWith(slash)) {
            newUrl = newUrl.substring(0, newUrl.length() - 1);
        }
        newUrl += path;

        // 参数传递
        if (null != asUri.getRawQuery()) {
            // fix：url 有中文出现 http status 400 的 bug
            newUrl += "?" + asUri.getRawQuery();
        }

        request = Request.create(request.httpMethod(), newUrl, headers, request.body(), request.charset(), request.requestTemplate());
        return request;
    }

}
