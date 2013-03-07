package org.neo4j.cypher.internal.executionplan.builders

import org.neo4j.cypher.internal.executionplan.{PartiallySolvedQuery, ExecutionPlanInProgress, PlanBuilder}
import org.neo4j.cypher.internal.spi.PlanContext
import org.neo4j.cypher.internal.commands.{Predicate, StartItem, Equals, IndexHint}
import org.neo4j.cypher.internal.commands.expressions.{Expression, Identifier, Property}
import org.neo4j.cypher.IndexHintException

class IndexLookupBuilder extends PlanBuilder {
  def canWorkWith(plan: ExecutionPlanInProgress, ctx: PlanContext) =
    plan.query.start.exists {
      case Unsolved(_: IndexHint) => true
      case _                      => false
    }

  def apply(plan: ExecutionPlanInProgress, ctx: PlanContext): ExecutionPlanInProgress = {
    val startItem = extractInterestingStartItem(plan)
    val hint = startItem.token.asInstanceOf[IndexHint]
    val foundPredicates = findMatchingPredicates(plan, hint)

    if (foundPredicates.isEmpty)
      throw new IndexHintException(hint, "No useful predicate was found for your index hint. Make sure the" +
        " property expression is alone either side of the equality sign.")

    val (predicate, expression) = foundPredicates.head


    val q: PartiallySolvedQuery = plan.query

    val newHint: Unsolved[StartItem] = Unsolved(hint.copy(query = Some(expression)))
    val newQuery = q.copy(
      where = q.where.filterNot(_ == predicate) :+ predicate.solve,
      start = q.start.filterNot(_ == startItem) :+ newHint
    )

    plan.copy(query = newQuery)
  }

  private def findMatchingPredicates(plan: ExecutionPlanInProgress, hint: IndexHint): Seq[(Unsolved[Predicate], Expression)] =
    plan.query.where.flatMap {
      case x@Unsolved(Equals(Property(Identifier(id), prop), expression))
        if id == hint.identifier && prop == hint.property => Some((x, expression))

      case x@Unsolved(Equals(expression, Property(Identifier(id), prop)))
        if id == hint.identifier && prop == hint.property => Some((x, expression))

      case _ => None
    }

  private def extractInterestingStartItem(plan: ExecutionPlanInProgress): QueryToken[StartItem] = {
    plan.query.start.filter {
      case Unsolved(_: IndexHint) => true
    }.head
  }

  def priority: Int = ???
}
