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

import org.neo4j.kernel.api.StatementContext;
import org.neo4j.kernel.api.TransactionContext;
import org.neo4j.kernel.impl.core.NodeManager;
import org.neo4j.kernel.impl.core.PropertyIndexManager;
import org.neo4j.kernel.impl.core.TransactionState;
import org.neo4j.kernel.impl.persistence.PersistenceManager;

public class TransactionContextImpl implements TransactionContext
{
    private final TransactionState state;
    private final PropertyIndexManager propertyIndexManager;
    private final PersistenceManager persistenceManager;
    private final NodeManager nodeManager;

    public TransactionContextImpl( TransactionState state, PropertyIndexManager propertyIndexManager,
                                   PersistenceManager persistenceManager, NodeManager nodeManager )
    {
        this.state = state;
        this.propertyIndexManager = propertyIndexManager;
        this.persistenceManager = persistenceManager;
        this.nodeManager = nodeManager;
    }

    @Override
    public StatementContext newStatementContext()
    {
        TemporaryLabelAsPropertyContext statementContext =
                new TemporaryLabelAsPropertyContext( propertyIndexManager, persistenceManager );

        return new TransactionStateAwareStatementContext( statementContext, state );
    }

    @Override
    public void commit()
    {
        // tx.success
        // tx.finish
        // commit cache
    }
}