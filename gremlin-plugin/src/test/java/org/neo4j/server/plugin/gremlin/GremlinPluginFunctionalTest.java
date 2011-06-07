/**
 * Copyright (c) 2002-2011 "Neo Technology,"
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
package org.neo4j.server.plugin.gremlin;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.kernel.ImpermanentGraphDatabase;
import org.neo4j.kernel.impl.annotations.Documented;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.rest.DocsGenerator;
import org.neo4j.test.GraphDescription;
import org.neo4j.test.GraphDescription.Graph;
import org.neo4j.test.GraphHolder;
import org.neo4j.test.TestData;
import org.neo4j.test.TestData.Title;

public class GremlinPluginFunctionalTest implements GraphHolder
{
    private static final String ENDPOINT = "http://localhost:7474/db/data/ext/GremlinPlugin/graphdb/execute_script";
    private static ImpermanentGraphDatabase graphdb;
    public @Rule
    TestData<Map<String, Node>> data = TestData.producedThrough( GraphDescription.createGraphFor(
            this, true ) );
    
    public @Rule
    TestData<DocsGenerator> gen = TestData.producedThrough( DocsGenerator.PRODUCER );
    private static WrappingNeoServerBootstrapper server;

    /**
     * Send a Gremlin Script, URL-encoded with UTF-8 encoding, e.g.
     * the equivalent of the Gremlin Script `i = g.v(1);i.outE.inV`
     */
    @Test
    @Title("Send a Gremlin Script - URL encoded")
    @Documented
    @Graph( value = { "I know you" } )
    public void testGremlinPostURLEncoded() throws UnsupportedEncodingException
    {
        String response = gen.get()
        .expectedStatus( Status.OK.getStatusCode() )
        .payload( "script=" + URLEncoder.encode( "i = g.v("+data.get().get( "I" ).getId() +");i.outE.inV", "UTF-8") )
        .payloadType( MediaType.APPLICATION_FORM_URLENCODED_TYPE )
        .post( ENDPOINT )
        .entity();
        assertTrue(response.contains( "you" ));
    }

    /**
     * Load a sample graph graph from a GraphML file URL.
     *
     * Import a graph form a http://graphml.graphdrawing.org/[GraphML] file
     * can be achieved through the Gremlin GraphMLReader.
     * The following script imports 2 nodes into Neo4j
     * and returns all vertices in the graph (the 2 new ones and the
     * reference node 0)
     */
    @Test
    @Documented
    public void testGremlinImportGraph() throws UnsupportedEncodingException
    {
        String response = gen.get()
        .expectedStatus( Status.OK.getStatusCode() )
        .payload( "{\"script\":\"GraphMLReader.inputGraph(g, new URL('https://raw.github.com/neo4j/neo4j-gremlin-plugin/master/src/data/graphml1.xml').openStream());g.V\"}" )
        .payloadType( MediaType.APPLICATION_JSON_TYPE )
        .post( ENDPOINT )
        .entity();
        assertTrue(response.contains( "you" ));
    }
    /**
     * To send a Script JSON encoded, set the payload Content-Type Header
     */
    @Test
    @Title("Send a Gremlin Script - JSON encoded")
    @Documented
    @Graph( value = { "I know you" } )
    public void testGremlinPostJSON()
    {
        String response = gen.get()
        .expectedStatus( Status.OK.getStatusCode() )
        .payload( "{\"script\":\"i = g.v("+data.get().get( "I" ).getId() +");i.outE.inV\"}" )
        .payloadType( MediaType.APPLICATION_JSON_TYPE )
        .post( ENDPOINT )
        .entity();
        assertTrue(response.contains( "you" ));
    }
    @BeforeClass
    public static void startDatabase()
    {
        graphdb = new ImpermanentGraphDatabase("target/db"+System.currentTimeMillis());
        
    }

    @AfterClass
    public static void stopDatabase()
    {
    }

    @Override
    public GraphDatabaseService graphdb()
    {
        return graphdb;
    }
    
    @Before
    public void startServer() {
        server = new WrappingNeoServerBootstrapper(
                graphdb );
        server.start();
    }
    
    @After
    public void shutdownServer() {
        server.stop();
    }
}
