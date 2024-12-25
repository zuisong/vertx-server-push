package cn.mmooo.verticle

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.stomp.BridgeOptions
import io.vertx.ext.stomp.StompServer
import io.vertx.ext.stomp.StompServerHandler
import io.vertx.ext.stomp.StompServerOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.*
import java.lang.System.Logger.Level.*


class StompBridgeVerticle : AbstractVerticle() {

    private val logger = System.getLogger(StompBridgeVerticle::class.java.simpleName)

    override fun start() {
        val port = System.getenv()["PUSH_PORT"]?.toIntOrNull() ?: 13000

        val server = createStompServer(vertx)

        val router = Router.router(vertx)
        router.route()

            .handler(LoggerHandler.create())
            .handler(ResponseTimeHandler.create())
            .handler(TimeoutHandler.create(10 * 1000))
            .handler(CorsHandler.create().allowCredentials(true))
            .handler(BodyHandler.create())
            .handler(StaticHandler.create())
            .failureHandler { rc ->
                logger.log(ERROR, "", rc.failure())
                rc.response()
                    .setStatusCode(500)
                    .end(rc.failure().message)
            }

        router.get("/health").handler { ctx ->
            ctx.response().end("ok")
        }
        router.get("/").handler { ctx ->
            ctx.redirect("/stomp-test.html")
        }

        router.post("/push")
            .handler { rc: RoutingContext ->
                val body = rc.body().asString(Charsets.UTF_8.displayName())
                val topic = rc.queryParam("topic").first()
                vertx.eventBus().publish(topic, body)
                logger.log(INFO, "OK")
                rc.response().end("ok")
            }


        vertx
            .createHttpServer(
                HttpServerOptions().also {
                    it.webSocketSubProtocols = listOf(
                        "v10.stomp", "v11.stomp", "v12.stomp",
                    )
                    it.isHttp2ClearTextEnabled = true
                }
            )
            .webSocketHandler(server.webSocketHandler())
            .requestHandler(router)
            .listen(port)
            .onSuccess {
                logger.log(INFO, "websocket server listen at {0}", port)
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
