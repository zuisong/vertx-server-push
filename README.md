# vertx-server-push

基于 stomp协议 的服务端推送

1. 运行项目(以下2种方式二选一)

- 执行 `mvn spring-boot:run`
- 打开 IDE 执行

2. 打开 http://localhost:13000/stomp-test.html

3. 发送以下请求看浏览器效果

```shell
curl 'http://localhost:13000/push?topic=%2Ftopic%2Fchat' -H 'content-type: application/json' -H 'accept: application/json, */*;q=0.5' -d '{"user":"test","message":"hello-world"}'
```
