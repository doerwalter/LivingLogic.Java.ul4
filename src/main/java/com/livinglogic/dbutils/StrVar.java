/*
** Copyright 2014-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.dbutils;

import java.sql.CallableStatement;
import java.sql.Types;
import java.sql.SQLException;

import com.livinglogic.ul4.EvaluationContext;
import com.livinglogic.ul4.ArgumentTypeMismatchException;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.Function;
import com.livinglogic.ul4.BoundArguments;

public class StrVar extends Var
{
	public StrVar()
	{
		super();
	}

	public StrVar(Object value)
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
			throw new ArgumentTypeMismatchException("str.value = {!t} not supported", value);
	}

	public void register(CallableStatement statement, int position) throws SQLException
	{
		if (value != noValue)
			statement.setObject(position, value);
		statement.registerOutParameter(position, Types.VARCHAR);
	}

	public void fetch(CallableStatement statement, int position) throws SQLException
	{
		value = statement.getObject(position);
	}

	private static class FunctionStr extends Function
	{
		@Override
		public String getNameUL4()
		{
			return "str";
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
			return new StrVar(args.get(0));
		}
	}

	public static final Function function = new FunctionStr();
}
