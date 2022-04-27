package cn.mmooo.verticle

import io.vertx.core.*
import io.vertx.core.http.*
import io.vertx.ext.bridge.*
import io.vertx.ext.stomp.*
import io.vertx.ext.stomp.BridgeOptions
import io.vertx.ext.web.*
import io.vertx.ext.web.handler.*
import io.vertx.kotlin.coroutines.*
import mu.*

private val logger = KotlinLogging.logger { }

class StompBridgeVerticle : CoroutineVerticle() {

  override suspend fun start() {

    val port = System.getenv()["PUSH_PORT"]?.toIntOrNull() ?: 13000

    val server = createStompServer(vertx)

    val router = Router.router(vertx)
    router.route()
      .failureHandler { rc ->
        logger.error(rc.failure()) {}
        rc.response()
          .setStatusCode(500)
          .end(rc.failure().message)
      }
      .handler(TimeoutHandler.create(10 * 1000))
      .handler(BodyHandler.create())
      .handler(CorsHandler.create().allowCredentials(true))
      .handler(LoggerHandler.create())
      .handler(ResponseTimeHandler.create())

    router.get("/stomp-test.html").blockingHandler { ctx ->
      Vertx::class.java.getResourceAsStream("/webroot/stomp-test.html")?.use {
        it.bufferedReader().use {
          ctx.response().end(it.readText())
        }
      }
    }

    router.get("/health").handler { ctx ->
      ctx.response().end("ok")
    }
    router.post("/push")
      .handler { rc: RoutingContext ->
        val body = rc.getBodyAsString(Charsets.UTF_8.displayName())
        val topic = rc.queryParam("topic").first()
        vertx.eventBus().publish(topic, body)
        rc.response()
          .setStatusCode(200)
          .end("ok")
      }

    vertx.createHttpServer(
      HttpServerOptions().apply {
        webSocketSubProtocols = listOf("v10.stomp", "v11.stomp")
      }
    )
      .webSocketHandler(server.webSocketHandler())
      .requestHandler(router)
      .listen(port)
      .onSuccess {
        logger.info("websocket server listen at ${port}")
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
