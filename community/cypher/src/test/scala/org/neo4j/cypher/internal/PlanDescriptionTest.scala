package org.neo4j.cypher.internal

import data.{SeqVal, PrimVal, MapVal, StrVal}
import org.scalatest.Assertions
import org.junit.Test
import org.neo4j.cypher.{ProfilerStatisticsNotReadyException, PlanDescription}

class PlanDescriptionTest extends Assertions {

  @Test
  def should_render_simple_descriptions() {
    // GIVEN
    val plan = PlanDescription(null, "plain")

    // WHEN
    val result = renderPlan(plan)

    // THEN
    assert("plain()" === result)
  }

  @Test
  def should_render_simple_descriptions_with_args() {
    // GIVEN
    val plan = PlanDescription(null, "plain", "a" -> StrVal("ha"), "b" -> StrVal("ho"), "c" -> PrimVal(12))

    // WHEN
    val result = renderPlan(plan)

    // THEN
    assert("""plain(a="ha", b="ho", c=12)""" === result)
  }

  @Test
  def should_render_simple_descriptions_with_map_args() {
    // GIVEN
    val plan = PlanDescription(null, "plain", "m" -> MapVal(Map("a" -> StrVal("ha"), "b" -> StrVal("ho"))))

    // WHEN
    val result = renderPlan(plan)

    // THEN
    assert("""plain(m={"a": "ha", "b": "ho"})""" === result)
  }

  @Test
  def should_render_simple_descriptions_with_seq_args() {
    // GIVEN
    val plan = PlanDescription(null, "plain", "s" -> SeqVal(Seq(StrVal("ha"), PrimVal(21))))

    // WHEN
    val result = renderPlan(plan)

    // THEN
    assert("""plain(s=["ha", 21])""" === result)
  }

  private def renderPlan(plan: PlanDescription) = {
    val builder = new StringBuilder
    plan.render(builder, "=>", "+>")
    builder.toString()
  }

  @Test
  def should_render_nested_descriptions() {
    // GIVEN
    val root = PlanDescription(null, "plain")
    val plan = root.withChildren(
      PlanDescription(null, "linus"),
      PlanDescription(null, "pauline").withChildren(PlanDescription(null, "fritz")))

    // WHEN
    val result = renderPlan(plan)

    // THEN
    assert("plain()=>linus()=>pauline()=>+>fritz()" === result)
  }

  @Test
  def should_generate_java_descriptions_with_args() {
    // GIVEN
    val plan = PlanDescription(null, "plain", "m" -> MapVal(Map("a" -> StrVal("ha"), "b" -> StrVal("ho"))))

    // WHEN
    val result = plan.asJava.getArguments.get("m").asInstanceOf[java.util.Map[String, Any]]

    // THEN
    assert("ha" === result.get("a"))
    assert("ho" === result.get("b"))
  }

  @Test
  def should_generate_nested_java_description() {
    // GIVEN
    val root = PlanDescription(null, "plain")
    val plan = root.withChildren(
      PlanDescription(null, "linus"),
      PlanDescription(null, "pauline").withChildren(PlanDescription(null, "fritz")))

    // WHEN
    val result = plan.asJava

    // THEN
    assert("plain" === result.getName)
    assert("linus" == result.getChild("linus").getName)
    assert("linus" === result.cd("linus").getName)
    assert("pauline" === result.cd("pauline").getName)
    assert("fritz" === result.cd("pauline", "fritz").getName)
    assert(true === result.isRoot)
    assert(result === result.getRoot)
    assert(false === result.cd("linus").isRoot)
    assert(result === result.cd("linus").getRoot)
    assert(false === result.cd("pauline").isRoot)
    assert(result === result.cd("pauline").getRoot)
    assert("fritz" == result.getChild("pauline").getChild("fritz").getName)
    assert(false === result.cd("pauline", "fritz").isRoot)
    assert(result === result.cd("pauline", "fritz").getRoot)
  }

  @Test
  def should_handle_missing_java_profiler_statistics() {
    // GIVEN
    val plan = PlanDescription(null, "plain", "m" -> MapVal(Map("a" -> StrVal("ha"), "b" -> StrVal("ho"))))

    // WHEN
    val result = plan.asJava

    // THEN
    assert(false === result.hasProfilerStatistics)
    intercept[ProfilerStatisticsNotReadyException]{result.getProfilerStatistics}
  }
}