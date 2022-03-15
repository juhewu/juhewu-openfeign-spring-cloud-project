package org.juhewu.openfeign.spring.cloud.config;

import feign.RequestInterceptor;

import org.juhewu.openfeign.spring.cloud.interceptor.FeignRequestInterceptor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * OpenFeign 自动配置
 *
 * @author duanjw
 * @since 2020-04-26
 **/
@Configuration(proxyBeanMethods = false)
@Import({ OpenFeignProperties.class })
public class OpenFeignAutoConfiguration {

    /**
     * 服务间调用，请求头信息传播
     */
    @Configuration(proxyBeanMethods = false)
    static class RequestHeaderPassKeysConfiguration {
        @Bean
        public RequestInterceptor getRequestInterceptor(OpenFeignProperties openFeignProperties) {
            return new FeignRequestInterceptor(openFeignProperties);
        }
    }

    /**
     * 负载均衡转成非负载均衡请求
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "juhewu.openfeign", name = "non-load-balancer.enable", havingValue = "true")
    static class ConvertToHttpConfiguration {
        @Bean
        ConvertToNonLoadBalancerFeignObjectWrapper convertHttpFeignObjectWrapper(BeanFactory beanFactory, OpenFeignProperties openFeignProperties) {
            OpenFeignProperties.NonLoadBalancer nonLoadBalancer = openFeignProperties.getNonLoadBalancer();
            return new ConvertToNonLoadBalancerFeignObjectWrapper(beanFactory, openFeignProperties.getNonLoadBalancer().getUrl(), nonLoadBalancer.isSkipPrefix());
        }

        @Bean
        ConvertToNonLoadBalancerBeanPostProcessor convertHttpBeanPostProcessor(
                ConvertToNonLoadBalancerFeignObjectWrapper convertToNonLoadBalancerFeignObjectWrapper) {
            return new ConvertToNonLoadBalancerBeanPostProcessor(convertToNonLoadBalancerFeignObjectWrapper);
        }
    }
}
