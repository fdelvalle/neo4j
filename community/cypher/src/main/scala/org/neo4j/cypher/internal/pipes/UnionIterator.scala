package org.neo4j.cypher.internal.pipes

import org.neo4j.cypher.internal.ExecutionContext


class UnionIterator(in: Seq[Pipe], state: QueryState) extends Iterator[ExecutionContext] {

  /*
  This field can have one of three states:
    null    -> the next value has not yet been fetched from the underlying Pipes
    None    -> this iterator has been emptied
    Some(x) -> the next value has been fetched, but not yet seen by next()
  */
  var currentValue: Option[ExecutionContext] = null

  /*
  Before the first pipe has been applied, currentIterator will have null in it. After that it will have an
  iterator in it always.
   */
  var currentIterator: Iterator[ExecutionContext] = null
  var pipesLeft: List[Pipe] = in.toList

  def hasNext: Boolean = {
    stepIfNeccessary()
    currentValue.nonEmpty
  }

  def next(): ExecutionContext = {
    stepIfNeccessary()

    val result = currentValue.getOrElse(Iterator.empty.next())
    currentValue = null
    result
  }

  private def stepIfNeccessary() {
    def loadNextIterator() {
      val p = pipesLeft.head
      pipesLeft = pipesLeft.tail

      currentIterator = p.createResults(state)
    }

    def step() {
      if (currentIterator == null)
        loadNextIterator()

      while (currentIterator.isEmpty && pipesLeft.nonEmpty) {
        loadNextIterator()
      }

      if (currentIterator.hasNext) {
        currentValue = Some(currentIterator.next())
      } else {
        currentValue = None
      }
    }

    if (currentValue == null) {
      step()
    }
  }
}
