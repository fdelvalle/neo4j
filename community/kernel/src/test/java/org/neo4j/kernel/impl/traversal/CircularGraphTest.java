/**
 * Copyright (c) 2002-2013 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.traversal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;

public class CircularGraphTest extends AbstractTestBase
{
    @Before
    public void createTheGraph()
    {
        createGraph( "1 TO 2", "2 TO 3", "3 TO 1" );
    }

    @Test
    public void testCircularBug()
    {
        final long timestamp = 3;
        Transaction tx = beginTx();
        getNodeWithName( "2" ).setProperty( "timestamp", 1L );
        getNodeWithName( "3" ).setProperty( "timestamp", 2L );
        tx.success();
        tx.finish();

        final RelationshipType type = DynamicRelationshipType.withName( "TO" );
        Traverser t = Traversal.traversal().depthFirst().evaluator( new Evaluator()
        {
            @Override
            public Evaluation evaluate( Path path )
            {
                Relationship relationship = path.lastRelationship();
                if ( relationship == null )
                {
                    return Evaluation.EXCLUDE_AND_CONTINUE;
                }

                if ( !relationship.isType( type ) )
                {
                    return Evaluation.EXCLUDE_AND_CONTINUE;
                }

                long currentTime = (Long) path.endNode().getProperty( "timestamp" );
                return Evaluation.ofContinues( currentTime < timestamp );
            }
        } ).traverse( node( "1" ) );
        Iterator<Node> nodes = t.nodes().iterator();
        assertEquals( "2", nodes.next().getProperty( "name" ) );
        assertEquals( "3", nodes.next().getProperty( "name" ) );
        assertFalse( nodes.hasNext() );
    }
}
