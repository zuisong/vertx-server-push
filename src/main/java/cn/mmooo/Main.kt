package cn.mmooo

import cn.mmooo.verticle.StompBridgeVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import org.slf4j.LoggerFactory


private val logger = LoggerFactory.getLogger("MainKt")

fun main() {
  System.setProperty("hazelcast.logging.type", "slf4j")

  val vertx = Vertx.vertx(VertxOptions())

  vertx.runOnContext {
    vertx.deployVerticle({ StompBridgeVerticle() }, DeploymentOptions().also {
      it.threadingModel = io.vertx.core.ThreadingModel.VIRTUAL_THREAD
      it.workerPoolSize = 10
      it.instances = 8
    })
    logger.atInfo().log { "application started" }
  }
}
