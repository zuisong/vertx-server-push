package cn.mmooo.verticle

import io.vertx.core.*
import io.vertx.core.http.*
import io.vertx.ext.bridge.*
import io.vertx.ext.stomp.*
import io.vertx.ext.stomp.BridgeOptions
import io.vertx.ext.web.*
import io.vertx.ext.web.handler.*
import io.vertx.ext.web.handler.sockjs.*
import io.vertx.kotlin.coroutines.*


class StompBridgeVerticle : CoroutineVerticle() {

  override suspend fun start() {

    val server = createStompServer(vertx)

    val router = Router.router(vertx)
    router.route().handler(CorsHandler.create().allowCredentials(true))
    router.route().handler(ResponseTimeHandler.create())
    router.route().handler(BodyHandler.create())
    router.route().handler(LoggerHandler.create())
    router.post("/push")
      .handler { rc: RoutingContext ->
        val body = rc.bodyAsString
        val topic = rc.queryParam("topic").first()
        vertx.eventBus().publish(topic, body)
        rc.response()
          .setStatusCode(200)
          .end("ok")
      }
      .failureHandler { rc ->
        rc.response()
          .setStatusCode(500)
          .end(rc.failure().message)
      }

    vertx.createHttpServer(
      HttpServerOptions().apply {
        webSocketSubProtocols = listOf("v10.stomp", "v11.stomp")
      }
    )
      .webSocketHandler(server.webSocketHandler())
      .requestHandler(router)
      .listen(13000)
      .onSuccess {
        logger.info("websocket server listen at 13000")
      }

  }


  private fun createStompServer(vertx: Vertx): StompServer {
    return StompServer.create(
      vertx,
      StompServerOptions()
        .setPort(-1)
        .setWebsocketBridge(true)
        .setWebsocketPath("/stomp")

    ).handler(
      StompServerHandler.create(vertx).bridge(
        BridgeOptions()
          // 禁止网页向 eventbus 发消息
          .addInboundPermitted(PermittedOptions().setAddress("NO_PERMISSION"))
          .addOutboundPermitted(PermittedOptions())
      )
    )
  }

}
