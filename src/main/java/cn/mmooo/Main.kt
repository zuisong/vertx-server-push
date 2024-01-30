package cn.mmooo

import cn.mmooo.verticle.StompBridgeVerticle
import io.vertx.core.*
import org.slf4j.LoggerFactory


private val logger = LoggerFactory.getLogger("MainKt")

fun main() {
    System.setProperty("hazelcast.logging.type", "slf4j")

    val vertx = Vertx.vertx(VertxOptions())

    vertx.runOnContext {
        vertx.deployVerticle({ StompBridgeVerticle() }, DeploymentOptions().also {
            it.threadingModel = ThreadingModel.VIRTUAL_THREAD
            it.workerPoolSize = 10
            it.instances = 8
        })
    }
    logger.atInfo().log { "application started" }
}
