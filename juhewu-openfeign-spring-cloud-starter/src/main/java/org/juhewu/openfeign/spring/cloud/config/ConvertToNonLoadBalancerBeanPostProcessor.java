
package org.juhewu.openfeign.spring.cloud.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 负载均衡转为非负载均衡 BeanPostProcessor
 *
 * @author duanjw
 */
public class ConvertToNonLoadBalancerBeanPostProcessor implements BeanPostProcessor {

    private final ConvertToNonLoadBalancerFeignObjectWrapper convertToNonLoadBalancerFeignObjectWrapper;

    public ConvertToNonLoadBalancerBeanPostProcessor(ConvertToNonLoadBalancerFeignObjectWrapper convertToNonLoadBalancerFeignObjectWrapper) {
        this.convertToNonLoadBalancerFeignObjectWrapper = convertToNonLoadBalancerFeignObjectWrapper;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return this.convertToNonLoadBalancerFeignObjectWrapper.wrap(bean);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

}
