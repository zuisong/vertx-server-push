package cn.mmooo

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.stomp.*
import java.util.*


fun main() {

    val vertx = Vertx.vertx()


    val server = StompServer.create(
        vertx,
        StompServerOptions()
            .setPort(-1) // Disable the TCP port, optional
            .setWebsocketBridge(true) // Enable the web socket support
            .setWebsocketPath("/stomp")
    )
        // Configure the web socket path, /stomp by default
        .handler(StompServerHandler.create(vertx))
        .also { server ->

            server.stompHandler()
                .bridge(
                    BridgeOptions()
                        .addInboundPermitted(PermittedOptions().setAddress("NO_PERMISSION"))
                        .addOutboundPermitted(PermittedOptions())
                )
        }

    val http: Future<HttpServer> = vertx.createHttpServer(
        HttpServerOptions()
            .setWebSocketSubProtocols(listOf("v10.stomp", "v11.stomp"))
    )
        .webSocketHandler(server.webSocketHandler())
        .listen(8080)

    vertx.setPeriodic(1000) {
        vertx.eventBus().publish(
            "/topic/chat", Buffer.buffer(
                """
            {"user":"User 12","message":"dawdadwa ${UUID.randomUUID()}" }
        """.trimIndent()
            )
        )
    }

    vertx.eventBus().consumer<Buffer>("/topic/chat") {
        println(it.body().toJson())
    }


}

