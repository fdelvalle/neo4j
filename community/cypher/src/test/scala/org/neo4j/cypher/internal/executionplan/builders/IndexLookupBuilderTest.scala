package org.neo4j.cypher.internal.executionplan.builders

import org.junit.Test
import org.neo4j.cypher.internal.executionplan.{PartiallySolvedQuery, PlanBuilder}
import org.neo4j.cypher.internal.commands.{Equals, IndexHint}
import org.neo4j.cypher.internal.commands.expressions.{Literal, Property, Identifier}
import org.neo4j.cypher.IndexHintException

class IndexLookupBuilderTest extends BuilderTest {

  def builder = new IndexLookupBuilder()

  @Test def should_not_accept_empty_query() {
    assertRejects(PartiallySolvedQuery())
  }

  @Test def should_accept_a_query_with_index_hints() {
    //GIVEN
    val identifier = "id"
    val label = "label"
    val property = "prop"
    val valueExpression = Literal(42)
    val predicate = Equals(Property(Identifier(identifier), property), valueExpression)


    test(identifier, label, property, predicate, valueExpression)
  }

  @Test def should_accept_a_query_with_index_hints2() {
    //GIVEN
    val identifier = "id"
    val label = "label"
    val property = "prop"
    val valueExpression = Literal(42)
    val predicate = Equals(valueExpression, Property(Identifier(identifier), property))


    test(identifier, label, property, predicate, valueExpression)
  }

  @Test def should_throw_if_no_matching_index_is_found() {
    //GIVEN
    val identifier = "id"
    val label = "label"
    val property = "prop"

    val q = PartiallySolvedQuery().copy(
      start = Seq(Unsolved(IndexHint(identifier, label, property, None)))
    )

    //WHEN
    intercept[IndexHintException](assertAccepts(q))
  }

  private def test(identifier: String, label: String, property: String, predicate: Equals, valueExpression: Literal) {
    val q = PartiallySolvedQuery().copy(
      start = Seq(Unsolved(IndexHint(identifier, label, property, None))),
      where = Seq(Unsolved(predicate))
    )

    //WHEN
    val plan = assertAccepts(q)

    //THEN
    assert(plan.query.start === Seq(Unsolved(IndexHint(identifier, label, property, Some(valueExpression)))))
    assert(plan.query.where === Seq(Solved(predicate)))
  }
}
