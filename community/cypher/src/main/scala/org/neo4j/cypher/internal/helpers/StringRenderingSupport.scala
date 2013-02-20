package org.neo4j.cypher.internal.helpers

/**
 * Subclasses prefer to use StringBuilders to render themselves
 */
trait StringRenderingSupport {
  override def toString = {
    val builder = new StringBuilder
    render(builder)
    builder.toString()
  }

  def render(builder: StringBuilder) {
    builder ++= super.toString
  }
}

