import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import java.time.LocalDateTime

// 定时往集群丢消息 测试消息下发用 配合压力测试脚本

suspend fun main() {
    val vertx = Vertx.clusteredVertx(VertxOptions()).await()


    vertx.setPeriodic(5_000) {
        vertx.eventBus().publish("123", JsonObject().put("time", LocalDateTime.now().toString()))
    }
}
