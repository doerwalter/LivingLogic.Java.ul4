/*
** Copyright 2013-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.dbutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.livinglogic.ul4.EvaluationContext;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.ArgumentException;
import com.livinglogic.ul4.Utils;
import com.livinglogic.ul4.UL4GetItemString;
import com.livinglogic.ul4.UL4Attributes;
import com.livinglogic.ul4.BoundMethodWithContext;
import com.livinglogic.ul4.BoundMethod;
import com.livinglogic.ul4.UndefinedKey;
import com.livinglogic.utils.Closeable;
import com.livinglogic.utils.CloseableRegistry;
import com.livinglogic.dbutils.IntVar;
import com.livinglogic.ul4.BoundArguments;

import static com.livinglogic.utils.SetUtils.makeSet;

public class Connection implements UL4GetItemString, UL4Attributes
{
	private java.sql.Connection connection;

	public Connection(java.sql.Connection connection)
	{
		this.connection = connection;
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
				closeableRegistry.registerCloseable(new Closeable() { public void close() {try { statement.close(); } catch (SQLException ex) {} } } );

			resultSet = statement.executeQuery();

			if (closeableRegistry != null)
				closeableRegistry.registerCloseable(new Closeable() { public void close() {try { resultSet.close(); } catch (SQLException ex) {} } } );

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

	public Iterator<Map<String, Object>> query(CloseableRegistry closeableRegistry, List args)
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

		final CallableStatement statement;
		final ResultSet resultSet;
		try
		{
			statement = connection.prepareCall(query.toString());

			if (closeableRegistry != null)
				closeableRegistry.registerCloseable(new Closeable() { public void close() {try { statement.close(); } catch (SQLException ex) {} } } );

			registerParameters(statement, parameters);

			resultSet = statement.executeQuery();

			if (closeableRegistry != null)
				closeableRegistry.registerCloseable(new Closeable() { public void close() {try { resultSet.close(); } catch (SQLException ex) {} } } );

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

		final CallableStatement statement;
		try
		{
			statement = connection.prepareCall(query.toString());
			try
			{
				registerParameters(statement, parameters);

				statement.execute();

				fetchParameters(statement, parameters);
			}
			finally
			{
				statement.close();
			}
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

	private static class BoundMethodQueryArgs extends BoundMethodWithContext<Connection>
	{
		public BoundMethodQueryArgs(Connection object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "connection.queryargs";
		}

		private static final Signature signature = new Signature("query", Signature.required, "args", Signature.remainingParameters);

		public Signature getSignature()
		{
			return signature;
		}

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

		public String nameUL4()
		{
			return "connection.query";
		}

		private static final Signature signature = new Signature("args", Signature.remainingParameters);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(EvaluationContext context, BoundArguments args)
		{
			return object.query(context, (List)args.get(0));
		}
	}

	private static class BoundMethodExecute extends BoundMethodWithContext<Connection>
	{
		public BoundMethodExecute(Connection object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "connection.execute";
		}

		private static final Signature signature = new Signature("args", Signature.remainingParameters);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(EvaluationContext context, BoundArguments args)
		{
			object.execute(context, (List)args.get(0));
			return null;
		}
	}

	protected static Set<String> attributes = makeSet("queryargs", "query", "execute", "int", "number", "str", "clob", "date");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("queryargs".equals(key))
			return new BoundMethodQueryArgs(this);
		else if ("query".equals(key))
			return new BoundMethodQuery(this);
		else if ("execute".equals(key))
			return new BoundMethodExecute(this);
		else if ("int".equals(key))
			return IntVar.function;
		else if ("number".equals(key))
			return NumberVar.function;
		else if ("str".equals(key))
			return StrVar.function;
		else if ("clob".equals(key))
			return CLOBVar.function;
		else if ("date".equals(key))
			return DateVar.function;
		else
			return new UndefinedKey(key);
	}
}
