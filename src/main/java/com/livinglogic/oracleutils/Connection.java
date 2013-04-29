/*
** Copyright 2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.oracleutils;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.Map;
import java.util.HashMap;

import com.livinglogic.ul4.UL4MethodCallWithContext;
import com.livinglogic.ul4.EvaluationContext;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.UnknownMethodException;
import com.livinglogic.ul4.Utils;
import com.livinglogic.utils.CloseableRegistry;

public class Connection implements UL4MethodCallWithContext
{
	private java.sql.Connection connection;

	public Connection(java.sql.Connection connection)
	{
		this.connection = connection;
	}

	public Iterable<Map<String, Object>> query(CloseableRegistry closeableRegistry, String query, Map<String, Object> parameters)
	{
		CallableStatement stmt;
		try
		{
			stmt = connection.prepareCall(query);
			for (String key : parameters.keySet())
			{
				stmt.setObject(key, parameters.get(key));
			}
			if (closeableRegistry != null)
				closeableRegistry.registerCloseable(stmt);
		}
		catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return new IterableStatement(closeableRegistry, stmt);
	}

	public Iterable<Map<String, Object>> query(String query, Map<String, Object> parameters)
	{
		return query(null, query, parameters);
	}

	private Signature querySignature = new Signature("query", null, "parameters", "query", Signature.required);

	public Object callMethodUL4(EvaluationContext context, String methodName, Object[] args, Map<String, Object> kwargs)
	{
		if ("query".equals(methodName))
		{
			args = querySignature.makeArgumentArray(args, kwargs);
			if (!(args[0] instanceof String))
				throw new UnsupportedOperationException("query must be string, not " + Utils.objectType(args[0]) + "!");
			return query(context, (String)args[0], (Map<String, Object>)args[1]);
		}
		else
			throw new UnknownMethodException(methodName);
	}
}
