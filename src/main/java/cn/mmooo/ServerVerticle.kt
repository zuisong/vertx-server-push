package cn.mmooo

import io.netty.handler.codec.http.*
import io.vertx.core.*
import io.vertx.core.http.*
import io.vertx.core.impl.*
import io.vertx.core.json.*
import io.vertx.ext.web.*
import io.vertx.ext.web.handler.*
import io.vertx.kotlin.coroutines.*
import org.slf4j.*
import java.lang.invoke.*
import java.util.*


val logger: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

class ServerVerticle : CoroutineVerticle() {

    override suspend fun start() {

        val router = Router.router(vertx)
        router.route().handler(CorsHandler.create("*"))
        router.route().handler(BodyHandler.create())
        router.route().handler(ResponseTimeHandler.create())
        router.route().handler(LoggerHandler.create())

        router.post("/say").handler {
            val msg = it.request().formAttributes()["message"]
            val user = it.request().formAttributes()["user"]
            val data = JsonObject.mapFrom(mapOf("text" to msg, "from" to user))

            it.response().end(data.toBuffer())
        }

        router.get("/time").handler(TimeEventStream(vertx))

        val port = 8080
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(port)
            .onSuccess {
                logger.info("http listen on {}", port)
            }
    }
}


/*
http://www.ruanyifeng.com/blog/2017/05/server-sent_events.html
 */
// SSE 主动推送 Vertx
// 跑起项目后就能看到效果  curl 127.0.0.1:8080/time
class TimeEventStream(val vertx: Vertx) : Handler<RoutingContext> {

    init {
        Objects.requireNonNull(vertx)
    }

    override fun handle(rc: RoutingContext) {
        val response = rc.response()
        response.setChunked(true)
            .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_EVENT_STREAM)
            .putHeader(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE)
            .putHeader(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)

        val hotelCode = rc.request().getHeader("hotelCode")
        val username = rc.request().getHeader("username")

        logger.info("hotelCode:{} | username:{} | connected", hotelCode, username)

        val hotelConsumer = vertx.eventBus().consumer<JsonObject>(hotelCode) {
            logger.debug("hotelMsg, from: {}, msgContent: {}", it.address(), it.body())
            response.write(it.body().toBuffer())
            response.write("\n\n")
        }

        val hotelUserConsumer = vertx.eventBus().consumer<JsonObject>("$hotelCode:$username") {
            logger.debug("hotelUserMsg, from: {}, msgContent: {}", it.address(), it.body())
            response.write(it.body().toBuffer())
            response.write("\n\n")
        }


        rc.request()
            .connection()
            .closeHandler {
                logger.info("hotelCode:{} | username:{} | closed connection", hotelCode, username)
                hotelConsumer.unregister()
                hotelUserConsumer.unregister()
                endQuietly(response)
            }
            .exceptionHandler { t ->
                logger.error("hotelCode:{} | username:{} | closed connection with exception", hotelCode, username, t)
                hotelConsumer.unregister()
                hotelUserConsumer.unregister()
                rc.fail(t)
            }
    }

    private fun endQuietly(response: HttpServerResponse) {
        if (response.ended()) {
            return
        }
        try {
            response.end()
        } catch (e: IllegalStateException) {
            // Ignore it.
        }
    }
}
