/**
 * Copyright (c) 2002-2012 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cluster.protocol.omega.payload;

import java.io.Serializable;

public class CollectPayload implements Serializable
{
    private final int readNum;

    public CollectPayload( int readNum )
    {
        this.readNum = readNum;
    }

    public int getReadNum()
    {
        return readNum;
    }

    @Override
    public boolean equals( Object obj )
    {
        if (obj == null)
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof CollectPayload))
        {
            return false;
        }
        CollectPayload other = (CollectPayload) obj;
        return readNum == other.readNum;
    }

    @Override
    public String toString()
    {
        return "CollectPayload[readNum= "+readNum+"]";
    }
}
