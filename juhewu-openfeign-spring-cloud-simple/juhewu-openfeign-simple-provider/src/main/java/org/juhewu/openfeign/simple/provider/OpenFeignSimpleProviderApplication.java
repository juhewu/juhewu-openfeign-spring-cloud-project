package org.juhewu.openfeign.simple.provider;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供者应用
 *
 * @author duanjw
 * @since 2022/08/31
 */
@RestController
@SpringBootApplication
public class OpenFeignSimpleProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenFeignSimpleProviderApplication.class, args);
    }

    /**
     * echo 接口
     *
     * @param value
     * @return
     */
    @GetMapping("echo/{value}")
    String hello(@PathVariable("value") String value, @RequestParam("value2") String value2, HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        System.out.println("headerNames" + headerNames);
        do {
            System.out.println("请求头中的参数：" + request.getHeader(headerNames.nextElement()));
        } while (headerNames.hasMoreElements());

        return String.format("pathVariable: %s，requestParam：%s", value, value2);
    }
}
