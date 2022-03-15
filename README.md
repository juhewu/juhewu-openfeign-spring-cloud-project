## openfeign 功能扩展

不修改现有代码，通过配置文件扩展 OpenFeign 的功能，扩展的功能如下：

1. 支持服务之间调用不走负载均衡
2. 服务间调用传递 token，token 在请求头或者 url 参数中都可以
3. 服务间调用传递请求头中某些 key

## 快速开始

1. 引入依赖
```xml
        <dependency>
            <groupId>org.juhewu</groupId>
            <artifactId>juhewu-openfeign-spring-cloud-starter</artifactId>
            <version>1.0.0</version>
        </dependency>
```

2. 添加配置

```yaml
juhewu:
  openfeign:
    # 转换成非负载均衡
    non-load-balancer:
      # 是否启用，默认 false，不启用
      enable: true
      # 非负载均衡的 url
      url: localhost:9999
      # 是否跳过前缀，默认 false，不跳过。
      # true 时，会拼接成 http://{url}/{path}
      # false 或不写时，会拼接成 http://{url}/{service-id}/{path}
      skip-prefix: false
    header:
      # 请求头中需要传递的 keys
      pass-keys: 
        - test1
        - test2
    token:
      # 是否传递 token，默认是 true。说明：请求头（Authorization）或 url 中的 access_token 传递到下个服务
      enable: true
      # url 参数中 token 的名称，默认是 access_token
      url-token-name: access_token
```

## 功能说明

以一个简单的微服务系统举例。包含注册中心、网关服务、A服务、B服务。

### 支持服务之间调用不走负载均衡

使用场景：注册中心、网关、A服务部署在公共开发环境，并且是使用 `docker / k8s` 部署，你是开发者在本地开发 B 服务。
你现在需要通过 `FeignClient` 调用 A 服务，如果不出意外，你是调不通的，因为 A 服务的 ip 是一个 docker 虚拟网络内的一个 ip，你是访问不到该 ip 的，
那怎么解决呢？

