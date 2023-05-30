package cn.mmooo

import cn.mmooo.verticle.*
import io.vertx.core.*
import mu.*

private val logger = KotlinLogging.logger { }

fun main() {
  val vertx = Vertx.vertx(VertxOptions())
  vertx.deployVerticle({ StompBridgeVerticle() }, DeploymentOptions().apply {
      instances = 8
    })

  logger.info { "application started" }

}
