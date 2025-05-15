package cn.mmooo

import cn.mmooo.verticle.StompBridgeVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxBuilder
import io.vertx.core.VertxOptions
import java.util.function.Supplier


fun main() {
//System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    val logger = System.getLogger("main")

    logger.log(System.Logger.Level.INFO, "info")
//  System.setProperty("hazelcast.logging.type", "jul")

    val vertx = Vertx.builder().with(VertxOptions()).build()

    vertx.runOnContext {
        vertx.deployVerticle(Supplier { StompBridgeVerticle() }, DeploymentOptions().also {
            it.threadingModel = io.vertx.core.ThreadingModel.VIRTUAL_THREAD
            it.workerPoolSize = 10
            it.instances = 8
        })
        logger.log(System.Logger.Level.INFO, "application started")
    }
}
