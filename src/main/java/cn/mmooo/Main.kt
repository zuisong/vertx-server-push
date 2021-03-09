package cn.mmooo

import cn.mmooo.verticle.*
import io.vertx.core.*
import io.vertx.kotlin.coroutines.*


suspend fun main() {
  val vertx = Vertx.clusteredVertx(VertxOptions()).await()
  vertx.deployVerticle(
    StompBridgeVerticle::class.java.name,
    DeploymentOptions().apply {
      instances = 8
    })

}
