package cn.mmooo

import cn.mmooo.verticle.StompBridgeVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions



fun main() {
//System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
  val logger = System.getLogger("main")

  System.setProperty("hazelcast.logging.type", "jul")

  val vertx = Vertx.vertx(VertxOptions())

  vertx.runOnContext {
    vertx.deployVerticle({ StompBridgeVerticle() }, DeploymentOptions().also {
      it.threadingModel = io.vertx.core.ThreadingModel.EVENT_LOOP
      it.workerPoolSize = 10
      it.instances = 8
    })
    logger.log(System.Logger.Level.INFO, "application started")
  }
}
