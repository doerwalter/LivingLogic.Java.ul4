/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.oracleutils;

import java.util.Map;
import java.util.Iterator;
import java.sql.PreparedStatement;

import com.livinglogic.utils.CloseableRegistry;


public class IterableStatement implements Iterable<Map<String, Object>>
{
	private CloseableRegistry closeableRegistry;
	private PreparedStatement statement;

	public IterableStatement(CloseableRegistry closeableRegistry, PreparedStatement statement)
	{
		this.closeableRegistry = closeableRegistry;
		this.statement = statement;
	}

	public Iterator<Map<String, Object>> iterator()
	{
		return new StatementIterator(closeableRegistry, statement);
	}
}
