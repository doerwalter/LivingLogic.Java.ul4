/*
** Copyright 2013-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.dbutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.math.BigInteger;
import java.math.BigDecimal;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Clob;
import static java.sql.Types.DATE;
import static java.sql.Types.TIMESTAMP;

import org.apache.commons.collections.map.CaseInsensitiveMap;

import com.livinglogic.ul4.UL4Instance;
import com.livinglogic.ul4.AbstractInstanceType;
import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.EvaluationContext;
import com.livinglogic.ul4.MethodDescriptor;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.ArgumentException;
import com.livinglogic.ul4.Utils;
import com.livinglogic.ul4.UL4GetAttr;
import com.livinglogic.ul4.UL4Dir;
import com.livinglogic.ul4.BoundMethod;
import com.livinglogic.ul4.AttributeException;
import com.livinglogic.utils.CloseableRegistry;
import com.livinglogic.ul4.BoundArguments;

import static com.livinglogic.utils.SetUtils.makeSet;

public class Connection implements UL4Instance, AutoCloseable, UL4GetAttr, UL4Dir
{
	protected static class Type extends AbstractInstanceType
	{
		@Override
		public String getNameUL4()
		{
			return "Connection";
		}

		@Override
		public String getDoc()
		{
			return "A database connection";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof Connection;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	private java.sql.Connection connection;

	public Connection(java.sql.Connection connection)
	{
		this.connection = connection;
	}

	@Override
	public void close() throws SQLException
	{
		connection.close();
	}

	public void commit() throws SQLException
	{
		connection.commit();
	}

	public void rollback() throws SQLException
	{
		connection.rollback();
	}

	public Iterator<Map<String, Object>> queryargs(CloseableRegistry closeableRegistry, String query, List args)
	{
		final CallableStatement statement;
		final ResultSet resultSet;
		try
		{
			statement = connection.prepareCall(query);

			registerParameters(statement, args);

			if (closeableRegistry != null)
				closeableRegistry.registerCloseable(statement);

			resultSet = statement.executeQuery();

			if (closeableRegistry != null)
				closeableRegistry.registerCloseable(resultSet);

			fetchParameters(statement, args);
		}
		catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		return new ResultSetMapIterator(resultSet);
	}

	public Iterator<Map<String, Object>> queryargs(String query, List args)
	{
		return queryargs(null, query, args);
	}

	private void buildQueryAndParameters(List args, StringBuilder query, List<Object> parameters)
	{
		int i = 0;
		for (Object arg : args)
		{
			if (i % 2 == 0)
			{
				if (!(arg instanceof String))
					throw new ArgumentException("query part must be string, not " + Utils.objectType(arg));
				query.append((String)arg);
			}
			else
			{
				query.append("?");
				parameters.add(arg);
			}
			++i;
		}
	}

	public Iterator<Map<String, Object>> query(CloseableRegistry closeableRegistry, List args)
	{
		StringBuilder query = new StringBuilder();
		ArrayList<Object> parameters = new ArrayList<Object>(args.size()/2);

		buildQueryAndParameters(args, query, parameters);

		final CallableStatement statement;
		final ResultSet resultSet;
		try
		{
			statement = connection.prepareCall(query.toString());

			if (closeableRegistry != null)
				closeableRegistry.registerCloseable(statement);

			registerParameters(statement, parameters);

			resultSet = statement.executeQuery();

			if (closeableRegistry != null)
				closeableRegistry.registerCloseable(resultSet);

			fetchParameters(statement, parameters);

			return new ResultSetMapIterator(resultSet);
		}
		catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public Iterator<Map<String, Object>> query(List args)
	{
		return query((CloseableRegistry)null, args);
	}

	public Map<String, Object> queryone(List args)
	{
		StringBuilder query = new StringBuilder();
		ArrayList<Object> parameters = new ArrayList<Object>(args.size()/2);

		buildQueryAndParameters(args, query, parameters);

		try (CallableStatement statement = connection.prepareCall(query.toString()))
		{
			registerParameters(statement, parameters);

			try (ResultSet resultSet = statement.executeQuery())
			{
				fetchParameters(statement, parameters);

				if (resultSet.next())
					return makeRecord(resultSet, resultSet.getMetaData());
				else
					return null;
			}
		}
		catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public void execute(CloseableRegistry closeableRegistry, List args)
	{
		StringBuilder query = new StringBuilder();
		ArrayList<Object> parameters = new ArrayList<Object>(args.size()/2);
		int i = 0;
		for (Object arg : args)
		{
			if (i % 2 == 0)
			{
				if (!(arg instanceof String))
					throw new ArgumentException("query part must be string, not " + Utils.objectType(arg));
				query.append((String)arg);
			}
			else
			{
				query.append("?");
				parameters.add(arg);
			}
			++i;
		}

		try (CallableStatement statement = connection.prepareCall(query.toString()))
		{
			registerParameters(statement, parameters);

			statement.execute();

			fetchParameters(statement, parameters);
		}
		catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private static void registerParameters(CallableStatement statement, List parameters) throws SQLException
	{
		if (parameters != null)
		{
			int pos = 1;
			for (Object parameter : parameters)
			{
				if (parameter instanceof Var)
					((Var)parameter).register(statement, pos);
				else
					statement.setObject(pos, parameter);
				++pos;
			}
		}
	}

	private static void fetchParameters(CallableStatement statement, List parameters) throws SQLException
	{
		if (parameters != null)
		{
			int pos = 1;
			for (Object parameter : parameters)
			{
				if (parameter instanceof Var)
					((Var)parameter).fetch(statement, pos);
				++pos;
			}
		}
	}

	public static Map<String, Object> makeRecord(ResultSet resultSet, ResultSetMetaData metaData) throws SQLException
	{
		Map<String, Object> record = new CaseInsensitiveMap();

		int numberOfColumns = metaData.getColumnCount();
		for (int i = 1; i <= numberOfColumns; ++i)
		{
			String key = metaData.getColumnLabel(i);
			int type = metaData.getColumnType(i);
			Object value;
			if (type == DATE)
				value = resultSet.getDate(i);
			else if (type == TIMESTAMP
				      // Don't use the constants from oracle.jdbc.OracleTypes, so that we don't require ojdbc.jar
				      || type == -102 // oracle.jdbc.OracleTypes.TIMESTAMPLTZ
				      || type == -100 // oracle.jdbc.OracleTypes.TIMESTAMPNS
				      || type == -101) // oracle.jdbc.OracleTypes.TIMESTAMPTZ
				value = resultSet.getTimestamp(i);
			else
			{
				value = resultSet.getObject(i);
				if (value instanceof Clob)
				{
					String stringValue = ((Clob)value).getSubString(1L, (int)((Clob)value).length());
					try
					{
						((Clob)value).free();
					}
					catch (SQLException ex)
					{
					}
					value = stringValue;
				}
				else if (value instanceof BigDecimal)
					value = Utils.narrowBigDecimal((BigDecimal)value);
				else if (value instanceof BigInteger)
					value = Utils.narrowBigInteger((BigInteger)value);
			}
			record.put(key, value);
		}
		return record;
	}

	private static final Signature signatureQueryArgs = new Signature().addBoth("query").addVarPositional("args");
	private static final Signature signatureArgs = new Signature().addVarPositional("args");

	private static final MethodDescriptor<Connection> methodQueryArgs = new MethodDescriptor<Connection>(type, "queryargs", signatureQueryArgs);
	private static final MethodDescriptor<Connection> methodQuery = new MethodDescriptor<Connection>(type, "query", signatureArgs);
	private static final MethodDescriptor<Connection> methodQueryOne = new MethodDescriptor<Connection>(type, "queryone", signatureArgs);
	private static final MethodDescriptor<Connection> methodExecute = new MethodDescriptor<Connection>(type, "execute", signatureArgs);

	protected static Set<String> attributes = makeSet("queryargs", "query", "queryone", "execute", "int", "number", "str", "clob", "date");

	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		switch (key)
		{
			case "queryargs":
				return methodQueryArgs.bindMethod(this);
			case "query":
				return methodQuery.bindMethod(this);
			case "queryone":
				return methodQueryOne.bindMethod(this);
			case "execute":
				return methodExecute.bindMethod(this);
			case "int":
				return IntVar.function;
			case "number":
				return NumberVar.function;
			case "str":
				return StrVar.function;
			case "clob":
				return CLOBVar.function;
			case "date":
				return DateVar.function;
			default:
				return UL4Instance.super.getAttrUL4(context, key);
		}
	}

	@Override
	public Object callAttrUL4(EvaluationContext context, String key, List<Object> args, Map<String, Object> kwargs)
	{
		switch (key)
		{
			case "queryargs":
				BoundArguments boundQueryArgsArgs = methodQueryArgs.bindArguments(args, kwargs);
				return queryargs(context, boundQueryArgsArgs.getString(0), boundQueryArgsArgs.getList(1));
			case "query":
				BoundArguments boundQueryArgs = methodQuery.bindArguments(args, kwargs);
				return query(context, boundQueryArgs.getList(0));
			case "queryone":
				BoundArguments boundQueryOneArgs = methodQueryOne.bindArguments(args, kwargs);
				return queryone(boundQueryOneArgs.getList(0));
			case "execute":
				BoundArguments boundExecuteArgs = methodExecute.bindArguments(args, kwargs);
				execute(context, boundExecuteArgs.getList(0));
				return null;
			default:
				return UL4Instance.super.callAttrUL4(context, key, args, kwargs);
		}
	}
}
