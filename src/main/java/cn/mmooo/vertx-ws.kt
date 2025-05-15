package cn.mmooo

import io.vertx.core.*
import io.vertx.core.http.HttpServerOptions
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import java.lang.System.Logger.Level.INFO
import java.time.LocalDateTime
import java.util.function.Supplier


fun main() {

    val vertx = Vertx.builder().build()


    // Run the verticle a on virtual thread
    val server = Server()
    vertx.deployVerticle(Supplier { server }, DeploymentOptions().setThreadingModel(ThreadingModel.VIRTUAL_THREAD))

    vertx.setPeriodic(3000) {
        if (server.textHandlerID.isNotBlank()) {
            vertx.eventBus().request<String>(server.textHandlerID, "hello ${LocalDateTime.now()}")
        }
    }

}

val userIdToSocket = DualHashBidiMap<String, String>()

class Server : VerticleBase() {


    val log = System.getLogger("ws")

    var textHandlerID: String = ""

    override fun start(): Future<*> {


        val httpServer = vertx.createHttpServer(
            HttpServerOptions().setRegisterWebSocketWriteHandlers(true)
        )

        httpServer.webSocketHandshakeHandler {
            it.accept()
        }.webSocketHandler { wsSocket ->
            wsSocket.textMessageHandler { textMsg ->
                println(textMsg)
                Future.await(wsSocket.writeTextMessage("收到: $textMsg"))

                if (loginReq(textMsg)) {
                    val userId = extractUserId(textMsg);
                    userIdToSocket[userId] = wsSocket.textHandlerID()
                }

            }
            wsSocket.exceptionHandler {
                log.log(INFO, "error", it)
            }

            textHandlerID = wsSocket.textHandlerID()
            println("textHandlerID ->" + wsSocket.textHandlerID())
//            vertx.eventBus().consumer<String>(textHandlerID) {
//                println(it.body())
//                println(it.address())
//                println(it.replyAddress())
//                println(it.headers())
//            }


        }

        return httpServer.listen(9998)
    }

    private fun extractUserId(textMsg: String?): String? {
        TODO("Not yet implemented")
    }

    private fun loginReq(textMsg: String): Boolean {
        TODO("Not yet implemented")
    }
}

