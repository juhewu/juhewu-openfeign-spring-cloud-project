package org.juhewu.openfeign.spring.cloud.config;

import static org.juhewu.openfeign.spring.cloud.constant.FeignConstant.REQUEST_HEADER_AUTHORIZATION;

import feign.RequestInterceptor;

import org.juhewu.openfeign.spring.cloud.interceptor.FeignRequestHeadersInterceptor;
import org.juhewu.openfeign.spring.cloud.interceptor.FeignRequestTokenInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final static Logger log = LoggerFactory.getLogger(OpenFeignAutoConfiguration.class);

    /**
     * 服务间调用，请求拦截器
     */
    @Configuration(proxyBeanMethods = false)
    static class FeignRequestInterceptorConfiguration {

        /**
         * 传递请求头
         * @param openFeignProperties 配置文件
         * @return
         */
        @Bean
        @ConditionalOnProperty(prefix = "juhewu.openfeign", name = "pass-header.enable", havingValue = "true")
        public RequestInterceptor feignRequestHeadersInterceptor(OpenFeignProperties openFeignProperties) {
            if (log.isDebugEnabled()) {
                log.debug("已开启将 openfeign 传递 header。会将 header 中的 {} 传递", openFeignProperties.getPassHeader().getKeys());
            }
            return new FeignRequestHeadersInterceptor(openFeignProperties);
        }

        /**
         * 传递 token
         * @param openFeignProperties 配置文件
         * @return
         */
        @Bean
        @ConditionalOnProperty(prefix = "juhewu.openfeign", name = "pass-token.enable", havingValue = "true")
        public RequestInterceptor feignRequestTokenInterceptor(OpenFeignProperties openFeignProperties) {
            if (log.isDebugEnabled()) {
                log.debug("已开启将 openfeign 传递 token。会将 header 中的 {} 或者 url 中的 {} 传递", REQUEST_HEADER_AUTHORIZATION, openFeignProperties.getPassToken().getUrlTokenName());
            }
            return new FeignRequestTokenInterceptor(openFeignProperties);
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
            if(log.isDebugEnabled()) {
                log.debug("已开启将 openfeign 转换为非负载均衡。非负载均衡请求地址：{}，是否跳过前缀（服务名）：{}", nonLoadBalancer.getUrl(), nonLoadBalancer.isSkipPrefix());
            }
            return new ConvertToNonLoadBalancerFeignObjectWrapper(beanFactory, nonLoadBalancer.getUrl(), nonLoadBalancer.isSkipPrefix());
        }

        @Bean
        ConvertToNonLoadBalancerBeanPostProcessor convertHttpBeanPostProcessor(
                ConvertToNonLoadBalancerFeignObjectWrapper convertToNonLoadBalancerFeignObjectWrapper) {
            return new ConvertToNonLoadBalancerBeanPostProcessor(convertToNonLoadBalancerFeignObjectWrapper);
        }
    }
}
