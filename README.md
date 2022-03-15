## openfeign 功能扩展

不修改现有代码，通过配置文件扩展 OpenFeign 的功能，扩展的功能如下：

1. 支持服务间调用不走负载均衡
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

### 支持服务间调用不走负载均衡

以一个简单的微服务系统举例。包含注册中心、网关服务、A服务、B服务。

![paste image](http://cdn.duanjw.com/1647343412929x14cllmk.png?imageslim)

使用场景：你是开发者在本地开发 B 服务，注册中心、网关、A 服务部署在公共环境，并且是使用 `docker / k8s` 容器部署。
你现在需要用 B 服务通过 OpenFeign 调用 A 服务，如果不出意外，你是调不通的，因为 A 服务的 ip 是一个 docker 虚拟网络内的一个 ip，你是访问不到该 ip 的，
那怎么解决呢？  

既然 B 服务直接调用 A 服务不通，那我们可以找一个 A 服务能调得通的一个地址，比如：`B -> 前端 -> 网关 -> A`，通过引入组件并且稍加配置即可实现此功能：
```yaml
juhewu:
  openfeign:
    # 转换成非负载均衡
    non-load-balancer:
      # 是否启用，默认 false，不启用
      enable: true
      # 非负载均衡的 url。我们配置成前端的地址，前端针对 api 开头的请求会把 api 去掉并转发到网关
      url: 192.168.1.200:80/api
      # 是否跳过前缀，默认 false，不跳过。如果服务 A 有可以对外访问的地址，那么请求地址就不需要包括服务名，可以将此参数配置成 true
      skip-prefix: false
```

### 服务间调用传递 token
使用场景：服务间调用传递当前登录用户的 token。一般 Token 是放在请求头的`Authorization`中，所以如果请求头中有`Authorization`，则传递。
有时候为了方便我们可能需要直接把 token 直接放到请求 url 的参数中，所以如果请求 url 的参数中有 access_token，也会传递，可以这么配置：  

注意：如果使用 url 的参数传递 token，此 token 会同时写入到请求头的`Authorization`中。
```yaml
juhewu:
  openfeign:
    token:
      # 是否传递 token，默认是 true。说明：请求头（Authorization）或 url 中的 access_token 传递到下个服务
      enable: true
      # url 参数中 token 的名称，默认是 access_token，或者可以换成你实际的值，如：token
      url-token-name: access_token
```

### 服务间调用传递请求头中某些 key

使用场景：服务间用 OpenFeign 调用默认不会传递请求头中的参数，但是当我们需要统一传递一些请求头中的参数时，如：我需要服务间调用传递 test1 和 test2，则可以这么配置：

```yaml
juhewu:
  openfeign:
    header:
      # 请求头中需要传递的 keys
      pass-keys:
        - test1
        - test2
```



