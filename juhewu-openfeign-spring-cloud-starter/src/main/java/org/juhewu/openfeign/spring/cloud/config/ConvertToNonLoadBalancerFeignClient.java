package org.juhewu.openfeign.spring.cloud.config;

import java.io.IOException;

import feign.Client;
import feign.Request;
import feign.Response;

import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;

/**
 * 重写默认的 OpenFeign 负载均衡
 *
 * @author duanjw
 */
public class ConvertToNonLoadBalancerFeignClient extends LoadBalancerFeignClient {

    ConvertToNonLoadBalancerFeignClient(Client delegate,
            CachingSpringLoadBalancerFactory lbClientFactory,
            SpringClientFactory clientFactory,
            ConvertToNonLoadBalancerFeignObjectWrapper convertToNonLoadBalancerFeignObjectWrapper) {
        super((Client) convertToNonLoadBalancerFeignObjectWrapper.wrap(delegate), lbClientFactory,
                clientFactory);
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        return getDelegate().execute(request, options);
    }
}
