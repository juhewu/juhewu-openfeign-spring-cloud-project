# 示例项目

1. 下载 & 启动 nacos

```shell
# 下载
wget https://github.com/alibaba/nacos/releases/download/1.3.2/nacos-server-1.3.2.zip

# 解压
unzip nacos-server-1.3.2.zip

# 启动方式一：适用于 Linux/Unix/Mac 操作系统
sh nacos/bin/startup.sh -m standalone

# 启动方式二：适用于 Windows 操作系统
cmd nacos/bin/startup.cmd -m standalone
```
2. 分别启动 `juhewu-openfeign-simple-consumer` 和 `juhewu-openfeign-simple-provider`
   
   运行 OpenFeignSimpleConsumerApplication.java 和 OpenFeignSimpleProviderApplication.java。  
   OpenFeignSimpleConsumerApplication 日志控制台可以看到：  
   ```shell
   ....
   main] o.j.o.s.c.c.OpenFeignAutoConfiguration   : 已开启将 openfeign 转换为非负载均衡。非负载均衡请求地址：localhost:8081，是否跳过前缀（服务名）：true
   ....
   main] o.j.o.s.c.c.OpenFeignAutoConfiguration   : 已开启将 openfeign 传递 header。会将 header 中的 [test1, test2] 传递
   main] o.j.o.s.c.c.OpenFeignAutoConfiguration   : 已开启将 openfeign 传递 token。会将 header 中的 Authorization 或者 url 中的 access_token 传递
   ....
   ```
   
3. 发送测试请求
   
   ```shell
   curl localhost:8080/openfeign\?access_token=1 -H 'Authorization: Bearer xxxx' -H 'Test: xxxx' -H 'Test1: xxxx'
   ```
   可以看到日志：
   ```shell
   [nio-8080-exec-2] j.o.s.c.i.FeignRequestHeadersInterceptor : openfeign 传递 header 中 key：[Test1]
   [nio-8080-exec-2] o.j.o.s.c.i.FeignRequestTokenInterceptor : openfeign 传递 header 中 key 为 Authorization 的值作为 token
   [nio-8080-exec-2] j.o.s.c.c.ConvertToNonLoadBalancerClient : openfeign 转换为非负载均衡请求，原请求地址：http://provider/echo/Openfeign?value2=%E5%BC%A0，新请求地址：http://localhost:8081/echo/Openfeign?value2=%E5%BC%A0
   ```
   
