package org.neo4j.kernel.impl.api;

import org.neo4j.kernel.api.StatementContext;
import org.neo4j.kernel.api.TransactionContext;
import org.neo4j.kernel.impl.transaction.LockManager;

public class LockingTransactionContext implements TransactionContext
{
    private final TransactionContext actual;
    private final LockHolder lockHolder = new LockHolder();

    public LockingTransactionContext( TransactionContext actual, LockManager lockManager )
    {
        this.actual = actual;
    }

    @Override
    public StatementContext newStatementContext()
    {
        return new LockingStatementContext( );
    }

    @Override
    public void commit()
    {
    }
}
