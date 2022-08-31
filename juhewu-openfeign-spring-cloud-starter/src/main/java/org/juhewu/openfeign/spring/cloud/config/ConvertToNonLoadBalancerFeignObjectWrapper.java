package org.juhewu.openfeign.spring.cloud.config;

import feign.Client;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;

/**
 * 负载均衡转为非负载均衡 包装器
 *
 * @author duanjw
 */
public class ConvertToNonLoadBalancerFeignObjectWrapper {

    private final BeanFactory beanFactory;
    private final String url;
    private final boolean skipPrefix;

    private CachingSpringLoadBalancerFactory cachingSpringLoadBalancerFactory;

    private SpringClientFactory springClientFactory;

    public ConvertToNonLoadBalancerFeignObjectWrapper(BeanFactory beanFactory, String url, boolean skipPrefix) {
        this.beanFactory = beanFactory;
        this.url = url;
        this.skipPrefix = skipPrefix;
    }

    Object wrap(Object bean) {
        if (bean instanceof Client && !(bean instanceof ConvertToNonLoadBalancerClient)) {
            if (bean instanceof LoadBalancerFeignClient) {
                LoadBalancerFeignClient client = ((LoadBalancerFeignClient) bean);
                return new ConvertToNonLoadBalancerFeignClient(client.getDelegate(), factory(),
                        clientFactory(), this);
            }

            return new ConvertToNonLoadBalancerClient((Client) bean, url, skipPrefix);
        }
        return bean;
    }

    CachingSpringLoadBalancerFactory factory() {
        if (this.cachingSpringLoadBalancerFactory == null) {
            this.cachingSpringLoadBalancerFactory = this.beanFactory
                    .getBean(CachingSpringLoadBalancerFactory.class);
        }
        return this.cachingSpringLoadBalancerFactory;
    }

    SpringClientFactory clientFactory() {
        if (this.springClientFactory == null) {
            this.springClientFactory = this.beanFactory
                    .getBean(SpringClientFactory.class);
        }
        return this.springClientFactory;
    }

}
