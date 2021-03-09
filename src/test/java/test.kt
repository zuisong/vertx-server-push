import io.vertx.core.*
import io.vertx.core.json.*
import io.vertx.kotlin.coroutines.*
import java.time.*

// 定时往集群丢消息 测试消息下发用 配合压力测试脚本

suspend fun main() {
    val vertx = Vertx.clusteredVertx(VertxOptions()).await()


    vertx.setPeriodic(5_000) {
        vertx.eventBus().publish("123", JsonObject().put("time", LocalDateTime.now().toString()))
    }
}
