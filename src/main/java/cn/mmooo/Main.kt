package cn.mmooo

import cn.mmooo.verticle.*
import io.vertx.core.*


fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(
    { StompBridgeVerticle() },
    DeploymentOptions().apply {
      instances = 8
    })

}
