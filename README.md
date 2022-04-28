# vertx-server-push

> 基于 stomp协议 的服务端推送

1. 运行项目(以下几种方式选一种)  
    - docker **(推荐)** 执行 `docker-compose up -d`
    - shell 执行 `./gradlew run` (jdk >= 8)
    - ide  打开 IDEA 运行 Main.kt (jdk >= 8)

2. 打开 [http://localhost:13000/stomp-test.html](http://localhost:13000/stomp-test.html)

3. 发送以下请求看浏览器效果

```shell
curl 'http://localhost:13000/push?topic=/topic/chat' \
 -H 'content-type: application/json' \
 -d '{"user":"test","message":"hello-world"}'
```
