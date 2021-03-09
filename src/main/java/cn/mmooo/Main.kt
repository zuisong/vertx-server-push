package cn.mmooo
import cn.mmooo.verticle.StompBridgeVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.kotlin.coroutines.await


suspend fun main() {
    val vertx = Vertx.clusteredVertx(VertxOptions()).await()
    vertx.deployVerticle(
        StompBridgeVerticle::class.java.name,
        DeploymentOptions().apply {
            instances = 8
        })

}
