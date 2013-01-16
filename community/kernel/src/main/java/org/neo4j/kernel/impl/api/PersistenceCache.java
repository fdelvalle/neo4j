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
package org.neo4j.kernel.impl.api;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.kernel.impl.cache.EntityWithSize;
import org.neo4j.kernel.impl.cache.LockStripedCache;
import org.neo4j.kernel.impl.cache.SoftLruCache;

public class PersistenceCache
{
    private final LockStripedCache<NodeEntity> nodeCache;

    public PersistenceCache( LockStripedCache.Loader<NodeEntity> nodeLoader )
    {
        this.nodeCache = new LockStripedCache<NodeEntity>( new SoftLruCache<NodeEntity>( "Kernel API label cache" ),
                32, nodeLoader );
    }

    public void addLabel( long nodeId, long labelId )
    {
        // TODO
        nodeCache.get( nodeId );
    }

    public Set<Long> getLabels( long nodeId )
    {
        NodeEntity node = nodeCache.get( nodeId );
        return node != null ? node.getLabels() : null;
    }

    private static class NodeEntity implements EntityWithSize
    {
        private final long id;
        private final Set<Long> labels = new HashSet<Long>();

        public NodeEntity( long id )
        {
            this.id = id;
        }

        @Override
        public long getId()
        {
            return id;
        }

        @Override
        public void setRegisteredSize( int size )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getRegisteredSize()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size()
        {
            throw new UnsupportedOperationException();
        }

        public Set<Long> getLabels()
        {
            return labels;
        }
    }
}
