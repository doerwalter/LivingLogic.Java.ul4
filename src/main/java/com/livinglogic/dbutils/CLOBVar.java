/*
** Copyright 2014-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.dbutils;

import java.sql.CallableStatement;
import java.sql.Types;
import java.sql.Clob;
import java.sql.SQLException;

import com.livinglogic.ul4.EvaluationContext;
import com.livinglogic.ul4.ArgumentTypeMismatchException;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.Function;
import com.livinglogic.ul4.BoundArguments;

public class CLOBVar extends Var
{
	public CLOBVar()
	{
		super();
	}

	public CLOBVar(Object value)
	{
		if (value == noValue)
			this.value = value;
		else
			setValue(value);
	}

	public void setValue(Object value)
	{
		if (value == null || value instanceof String)
			this.value = value;
		else
			throw new ArgumentTypeMismatchException("clob.value = {!t} not supported", value);
	}

	public void register(CallableStatement statement, int position) throws SQLException
	{
		if (value != noValue)
			statement.setObject(position, value);
		statement.registerOutParameter(position, Types.CLOB);
	}

	public void fetch(CallableStatement statement, int position) throws SQLException
	{
		Object newValue = statement.getObject(position);
		if (newValue instanceof Clob)
		{
			value = ((Clob)newValue).getSubString(1L, (int)((Clob)newValue).length());
			try
			{
				((Clob)newValue).free();
			}
			catch (SQLException ex)
			{
			}
		}
		else
			value = newValue;
	}

	private static class FunctionCLOB extends Function
	{
		@Override
		public String getNameUL4()
		{
			return "clob";
		}

		private static final Signature signature = new Signature().addBoth("value", noValue);

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		@Override
		public Object evaluate(EvaluationContext context, BoundArguments args)
		{
			return new CLOBVar(args.get(0));
		}
	}

	public static final Function function = new FunctionCLOB();
}
