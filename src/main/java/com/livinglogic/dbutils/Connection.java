/*
** Copyright 2013-2021 by LivingLogic AG, Bayreuth/Germany
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

import com.livinglogic.ul4.EvaluationContext;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.ArgumentException;
import com.livinglogic.ul4.Utils;
import com.livinglogic.ul4.UL4GetAttr;
import com.livinglogic.ul4.UL4Dir;
import com.livinglogic.ul4.BoundMethodWithContext;
import com.livinglogic.ul4.BoundMethod;
import com.livinglogic.ul4.AttributeException;
import com.livinglogic.utils.CloseableRegistry;
import com.livinglogic.dbutils.IntVar;
import com.livinglogic.ul4.BoundArguments;

import static com.livinglogic.utils.SetUtils.makeSet;

public class Connection implements AutoCloseable, UL4GetAttr, UL4Dir
{
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

	private static class BoundMethodQueryArgs extends BoundMethodWithContext<Connection>
	{
		public BoundMethodQueryArgs(Connection object)
		{
			super(object);
		}

		@Override
		public String nameUL4()
		{
			return "queryargs";
		}

		private static final Signature signature = new Signature("query", Signature.required, "args", Signature.remainingParameters);

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		@Override
		public Object evaluate(EvaluationContext context, BoundArguments args)
		{
			if (!(args.get(0) instanceof String))
				throw new UnsupportedOperationException("query must be string, not " + Utils.objectType(args.get(0)) + "!");
			return object.queryargs(context, (String)args.get(0), (List)args.get(1));
		}
	}

	private static class BoundMethodQuery extends BoundMethodWithContext<Connection>
	{
		public BoundMethodQuery(Connection object)
		{
			super(object);
		}

		@Override
		public String nameUL4()
		{
			return "query";
		}

		private static final Signature signature = new Signature("args", Signature.remainingParameters);

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		@Override
		public Object evaluate(EvaluationContext context, BoundArguments args)
		{
			return object.query(context, (List)args.get(0));
		}
	}

	private static class BoundMethodQueryOne extends BoundMethod<Connection>
	{
		public BoundMethodQueryOne(Connection object)
		{
			super(object);
		}

		@Override
		public String nameUL4()
		{
			return "queryone";
		}

		private static final Signature signature = new Signature("args", Signature.remainingParameters);

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		@Override
		public Object evaluate(BoundArguments args)
		{
			return object.queryone((List)args.get(0));
		}
	}

	private static class BoundMethodExecute extends BoundMethodWithContext<Connection>
	{
		public BoundMethodExecute(Connection object)
		{
			super(object);
		}

		@Override
		public String nameUL4()
		{
			return "execute";
		}

		private static final Signature signature = new Signature("args", Signature.remainingParameters);

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		@Override
		public Object evaluate(EvaluationContext context, BoundArguments args)
		{
			object.execute(context, (List)args.get(0));
			return null;
		}
	}

	protected static Set<String> attributes = makeSet("queryargs", "query", "queryone", "execute", "int", "number", "str", "clob", "date");

	public Set<String> dirUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "queryargs":
				return new BoundMethodQueryArgs(this);
			case "query":
				return new BoundMethodQuery(this);
			case "queryone":
				return new BoundMethodQueryOne(this);
			case "execute":
				return new BoundMethodExecute(this);
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
				throw new AttributeException(this, key);
		}
	}
}
