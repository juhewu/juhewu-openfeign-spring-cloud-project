package org.juhewu.openfeign.simple.consumer;

import org.juhewu.openfeign.simple.consumer.feign.ProviderClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消费者应用
 *
 * @author duanjw
 * @since 2022/08/31
 */
@RestController
@EnableFeignClients
@SpringBootApplication
public class OpenFeignSimpleConsumerApplication {

    private final ProviderClient providerClient;

    public OpenFeignSimpleConsumerApplication(ProviderClient providerClient) {
        this.providerClient = providerClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(OpenFeignSimpleConsumerApplication.class, args);
    }

    /**
     * 使用 openfeign 调用 openfeign 服务
     * <p>
     * 请求地址：curl localhost:8080/openfeign\?access_token=1 -H 'Authorization: Bearer xxxx' -H 'Test: xxxx' -H 'Test1: xxxx'
     *
     * @return
     */
    @GetMapping("openfeign")
    public String openfeign() {
        String value = "Openfeign";
        return providerClient.hello(value, "张");
    }
}
