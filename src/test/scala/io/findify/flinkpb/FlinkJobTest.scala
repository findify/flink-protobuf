package io.findify.flinkpb

import io.findify.flinkprotobuf.scala.{Bar, Foo, Sealed, SealedMessage}
import org.apache.flink.api.common.{ExecutionConfig, RuntimeExecutionMode}
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.collection.JavaConverters._

class FlinkJobTest extends AnyFlatSpec with Matchers with BeforeAndAfterAll {
  lazy val cluster = new MiniClusterWithClientResource(
    new MiniClusterResourceConfiguration.Builder().setNumberSlotsPerTaskManager(1).setNumberTaskManagers(1).build()
  )

  lazy val env = {
    cluster.getTestEnvironment.setAsContext()
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    env.setRuntimeMode(RuntimeExecutionMode.BATCH)
    env.enableCheckpointing(1000)
    env.setRestartStrategy(RestartStrategies.noRestart())
    env.getConfig.disableGenericTypes()
    env
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    cluster.before()
  }

  override def afterAll(): Unit = {
    cluster.after()
    super.afterAll()
  }

  it should "use protobuf serialization for simple messages" in {
    implicit val ti = FlinkProtobuf.generateScala(Foo)
    val result      = env.fromCollection(List(Foo(1), Foo(2), Foo(3))).executeAndCollect(10)
    result.map(_.value) shouldBe List(1, 2, 3)
  }

  it should "use protobuf serialization for simple messages on java api" in {
    val jenv   = env.getJavaEnv
    val result = JobTest.test(env.getJavaEnv).asScala.toList
    result.map(_.getValue) shouldBe List(1)
  }

  it should "use protobuf serialization for oneof messages" in {
    implicit val ti = FlinkProtobuf.generateScala(SealedMessage)
    val result = env
      .fromCollection(List[SealedMessage](Foo(1).asMessage, Foo(2).asMessage, Foo(3).asMessage, Bar("a").asMessage))
      .rebalance
      .executeAndCollect(10)
    result.flatMap(_.toSealed) shouldBe List(Foo(1), Foo(2), Foo(3), Bar("a"))
  }
}
