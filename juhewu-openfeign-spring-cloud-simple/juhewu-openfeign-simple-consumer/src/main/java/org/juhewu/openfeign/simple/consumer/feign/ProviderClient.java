package org.juhewu.openfeign.simple.consumer.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 服务提供者 feign
 * <p>
 * 通过该接口来调用服务提供者
 *
 * @author duanjw
 */
@FeignClient(name = "provider")
public interface ProviderClient {
    /**
     * echo 接口
     *
     * @param value value
     * @return 返回值
     */
    @GetMapping("echo/{value}")
    String hello(@PathVariable("value") String value, @RequestParam("value2") String value2);
}