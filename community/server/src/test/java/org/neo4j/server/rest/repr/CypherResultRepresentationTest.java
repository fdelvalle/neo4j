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
package org.neo4j.server.rest.repr;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.neo4j.server.rest.domain.JsonHelper.jsonToMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;
import org.junit.Test;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.cypher.javacompat.PlanDescription;
import org.neo4j.server.rest.domain.JsonParseException;
import org.neo4j.server.rest.repr.formats.JsonFormat;

public class CypherResultRepresentationTest
{

    @Test
    public void shouldSerializeProfilingResult() throws Exception
    {
        // Given
        ExecutionResult result = mock(ExecutionResult.class);
        when(result.iterator()).thenReturn(IteratorUtils.emptyIterator());
        when(result.columns()).thenReturn(new ArrayList<String>());
        
        PlanDescription plan = mock(PlanDescription.class);
        when(result.executionPlanDescription()).thenReturn(plan);

        // When
        Map<String, Object> serialized = serialize( new CypherResultRepresentation( result, true ) );

        // Then
        Map<String, Object> serializedPlan = (Map<String, Object>) serialized.get( "plan" );
    }

    private Map<String, Object> serialize( CypherResultRepresentation repr ) throws URISyntaxException, JsonParseException
    {
        OutputFormat format = new OutputFormat( new JsonFormat(), new URI( "http://localhost/" ), null );
        return jsonToMap( format.format( repr ) );
    }

}
