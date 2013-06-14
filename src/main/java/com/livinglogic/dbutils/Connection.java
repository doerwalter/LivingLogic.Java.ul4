/*
** Copyright 2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.dbutils;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.livinglogic.ul4.EvaluationContext;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.UL4MethodCallWithContext;
import com.livinglogic.ul4.UnknownMethodException;
import com.livinglogic.ul4.Utils;
import com.livinglogic.utils.Closeable;
import com.livinglogic.utils.CloseableRegistry;

public class Connection implements UL4MethodCallWithContext
{
	private java.sql.Connection connection;

	public Connection(java.sql.Connection connection)
	{
		this.connection = connection;
	}

	public Iterable<Map<String, Object>> query(CloseableRegistry closeableRegistry, String query, List args, Map<String, Object> kwargs)
	{
		final CallableStatement stmt;
		try
		{
			stmt = connection.prepareCall(query);
			int pos = 1;
			if (args != null)
			{
				for (Object arg : args)
					stmt.setObject(pos++, arg);
			}
			if (kwargs != null)
			{
				for (String key : kwargs.keySet())
					stmt.setObject(key, kwargs.get(key));
			}
			if (closeableRegistry != null)
				closeableRegistry.registerCloseable(new Closeable() { public void close() {try { stmt.close(); } catch (SQLException ex) {} } } );
		}
		catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return new IterableStatement(closeableRegistry, stmt);
	}

	public Iterable<Map<String, Object>> query(String query, List args, Map<String, Object> kwargs)
	{
		return query(null, query, args, kwargs);
	}

	private Signature querySignature = new Signature(
		"query",
		"query", Signature.required,
		"args", Signature.remainingArguments,
		"kwargs", Signature.remainingKeywordArguments
	);

	public Object callMethodUL4(EvaluationContext context, String methodName, Object[] args, Map<String, Object> kwargs)
	{
		if ("query".equals(methodName))
		{
			args = querySignature.makeArgumentArray(args, kwargs);
			if (!(args[0] instanceof String))
				throw new UnsupportedOperationException("query must be string, not " + Utils.objectType(args[0]) + "!");
			return query(context, (String)args[0], (List)args[1], (Map<String, Object>)args[2]);
		}
		else
			throw new UnknownMethodException(methodName);
	}
}
