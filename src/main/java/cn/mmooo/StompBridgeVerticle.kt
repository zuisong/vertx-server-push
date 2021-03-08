package cn.mmooo

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.stomp.BridgeOptions
import io.vertx.ext.stomp.StompServer
import io.vertx.ext.stomp.StompServerHandler
import io.vertx.ext.stomp.StompServerOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle


class StompBridgeVerticle : CoroutineVerticle() {

    override suspend fun start() {

        val server = createStompServer(vertx)

        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
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

    }


    private fun createStompServer(vertx: Vertx): StompServer {
        val stompServer = StompServer.create(
            vertx,
            StompServerOptions()
                .setPort(-1)
                .setWebsocketBridge(true)
                .setWebsocketPath("/stomp")
        )
            .handler(StompServerHandler.create(vertx))
            .also { server ->
                server.stompHandler().bridge(
                    BridgeOptions()
                        // 禁止网页向 eventbus 发消息
                        .addInboundPermitted(PermittedOptions().setAddress("NO_PERMISSION"))
                        .addOutboundPermitted(PermittedOptions())
                )
            }
        return stompServer
    }

}
