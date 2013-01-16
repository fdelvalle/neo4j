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

import java.util.Set;

import org.neo4j.kernel.api.LabelNotFoundException;
import org.neo4j.kernel.api.StatementContext;
import org.neo4j.kernel.impl.core.KeyNotFoundException;
import org.neo4j.kernel.impl.core.NodeManager;
import org.neo4j.kernel.impl.core.PropertyIndex;
import org.neo4j.kernel.impl.core.PropertyIndexManager;
import org.neo4j.kernel.impl.core.TransactionState;
import org.neo4j.kernel.impl.persistence.PersistenceManager;

public class StatementContextImpl implements StatementContext
{
    private static final String LABEL_PREFIX = "___label___";
    
    private final TransactionState state;
    private final PropertyIndexManager propertyIndexManager;
    private final PersistenceManager persistenceManager;
    private final NodeManager nodeManager;

    public StatementContextImpl( TransactionState state, PropertyIndexManager propertyIndexManager,
            PersistenceManager persistenceManager, NodeManager nodeManager )
    {
        this.state = state;
        this.propertyIndexManager = propertyIndexManager;
        this.persistenceManager = persistenceManager;
        this.nodeManager = nodeManager;
    }
    
    @Override
    public long getOrCreateLabelId( String label )
    {
        return propertyIndexManager.getOrCreateId( internalLabelName( label ) );
    }

    @Override
    public long getLabelId( String label ) throws LabelNotFoundException
    {
        try
        {
            return propertyIndexManager.getIdByKeyName( label );
        }
        catch ( KeyNotFoundException e )
        {
            throw new LabelNotFoundException( label, e );
        }
    }

    @Override
    public void addLabelToNode( long labelId, long nodeId )
    {
        state.acquireWriteLock( nodeManager.newNodeProxyById( nodeId ) );
        Set<Long> addedLabels = null, removedLabels = null;
        if ( state.hasChanges() )
        {
            addedLabels = state.getOrCreateAddedLabels( nodeId );
            removedLabels = state.getRemovedLabels( nodeId );
        }
        
        if ( !addedLabels.add( labelId ) )
            return; // Already added
        if ( removedLabels != null )
            removedLabels.remove( labelId );
        
        // TODO This is the hack where we temporarily store the labels in the property store
        PropertyIndex propertyIndex = propertyIndexManager.getKeyByIdOrNull( (int) labelId );
        persistenceManager.nodeAddProperty( nodeId, propertyIndex, new LabelAsProperty( nodeId ) );
    }

    private String internalLabelName( String label )
    {
        return LABEL_PREFIX + label;
    }
}
