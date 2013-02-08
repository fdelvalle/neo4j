package org.neo4j.cypher.internal.pipes

import org.junit.Test
import org.scalatest.Assertions


class UnionIteratorTest extends Assertions {
  val state = QueryState()

  @Test def empty_plus_empty_is_empty() {
    //GIVEN
    val union = createUnion(Iterator.empty, Iterator.empty)

    //THEN
    assert(union.isEmpty, "Union of empty inputs should be empty")
  }

  @Test
  def single_element() {
    // GIVEN
    val singleMap = Map("x" -> 1)
    val union = createUnion(Iterator(singleMap), Iterator.empty)

    //THEN
    assert(union.toList === List(singleMap))
  }

  @Test
  def two_elements() {
    //GIVEN
    val aMap = Map("x" -> 1)
    val bMap = Map("x" -> 2)
    val union = createUnion(Iterator(aMap), Iterator(bMap))

    //THEN
    assert(union.toList === List(aMap, bMap))
  }

  private def createUnion(aIt: Iterator[Map[String, Any]], bIt: Iterator[Map[String, Any]]): UnionIterator = {
    val a = new FakePipe(aIt)
    val b = new FakePipe(bIt)

    new UnionIterator(Seq(a, b), state)
  }
}