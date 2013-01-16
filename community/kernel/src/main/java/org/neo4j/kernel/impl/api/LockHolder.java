package org.neo4j.kernel.impl.api;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;
import org.neo4j.kernel.impl.core.LockElement;
import org.neo4j.kernel.impl.transaction.LockManager;

public class LockHolder
{
    private final LockManager lockManager;
    private final List<LockElement> locks = new ArrayList<LockElement>();

    public LockHolder( LockManager lockManager )
    {
        this.lockManager = lockManager;
    }

    public void acquireNodeReadLock( long nodeId )
    {
        this.lockManager.getReadLock( new NodeLock( nodeId ) );
    }

    public void acquireNodeWriteLock( long nodeId )
    {
        this.lockManager.getWriteLock( new NodeLock( nodeId ) );
    }

    void releaseLocks();

    private static class NodeLock implements Node
    {
        private final long id;

        public NodeLock( long id )
        {
            this.id = id;
        }

        @Override
        public boolean equals( Object o )
        {
            if ( !(o instanceof Node) )
            {
                return false;
            }
            return this.getId() == ((Node) o).getId();
        }

        @Override
        public int hashCode()
        {
            return (int) (( id >>> 32 ) ^ id );
        }

        @Override
        public long getId()
        {
            return id;
        }

        @Override
        public void delete()
        {
        }

        @Override
        public Iterable<Relationship> getRelationships()
        {
            return null;
        }

        @Override
        public boolean hasRelationship()
        {
            return false;
        }

        @Override
        public Iterable<Relationship> getRelationships( RelationshipType... types )
        {
            return null;
        }

        @Override
        public Iterable<Relationship> getRelationships( Direction direction, RelationshipType... types )
        {
            return null;
        }

        @Override
        public boolean hasRelationship( RelationshipType... types )
        {
            return false;
        }

        @Override
        public boolean hasRelationship( Direction direction, RelationshipType... types )
        {
            return false;
        }

        @Override
        public Iterable<Relationship> getRelationships( Direction dir )
        {
            return null;
        }

        @Override
        public boolean hasRelationship( Direction dir )
        {
            return false;
        }

        @Override
        public Iterable<Relationship> getRelationships( RelationshipType type, Direction dir )
        {
            return null;
        }

        @Override
        public boolean hasRelationship( RelationshipType type, Direction dir )
        {
            return false;
        }

        @Override
        public Relationship getSingleRelationship( RelationshipType type, Direction dir )
        {
            return null;
        }

        @Override
        public Relationship createRelationshipTo( Node otherNode, RelationshipType type )
        {
            return null;
        }

        @Override
        public Traverser traverse( Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator
                returnableEvaluator, RelationshipType relationshipType, Direction direction )
        {
            return null;
        }

        @Override
        public Traverser traverse( Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator
                returnableEvaluator, RelationshipType firstRelationshipType, Direction firstDirection,
                                   RelationshipType secondRelationshipType, Direction secondDirection )
        {
            return null;
        }

        @Override
        public Traverser traverse( Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator
                returnableEvaluator, Object... relationshipTypesAndDirections )
        {
            return null;
        }

        @Override
        public GraphDatabaseService getGraphDatabase()
        {
            return null;
        }

        @Override
        public boolean hasProperty( String key )
        {
            return false;
        }

        @Override
        public Object getProperty( String key )
        {
            return null;
        }

        @Override
        public Object getProperty( String key, Object defaultValue )
        {
            return null;
        }

        @Override
        public void setProperty( String key, Object value )
        {
        }

        @Override
        public Object removeProperty( String key )
        {
            return null;
        }

        @Override
        public Iterable<String> getPropertyKeys()
        {
            return null;
        }

        @Override
        public Iterable<Object> getPropertyValues()
        {
            return null;
        }
    }
}
