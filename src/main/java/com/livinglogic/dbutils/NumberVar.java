/*
** Copyright 2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.dbutils;

import java.sql.CallableStatement;
import java.sql.Types;
import java.sql.SQLException;

import com.livinglogic.ul4.ArgumentTypeMismatchException;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.Function;
import com.livinglogic.ul4.Utils;

public class NumberVar extends Var
{
	public NumberVar()
	{
		super();
	}

	public NumberVar(Object value)
	{
		if (value == noValue)
			this.value = value;
		else
			setValue(value);
	}

	public void setValue(Object value)
	{
		if (value == null || value instanceof Number)
			this.value = value;
		else
			throw new ArgumentTypeMismatchException("number.value = {}", value);
	}

	public void register(CallableStatement statement, int position) throws SQLException
	{
		if (value != noValue)
			statement.setObject(position, value);
		statement.registerOutParameter(position, Types.DECIMAL);
	}

	public void fetch(CallableStatement statement, int position) throws SQLException
	{
		value = statement.getObject(position);
	}

	private static class FunctionNumber extends Function
	{
		public String nameUL4()
		{
			return "number";
		}

		private static Signature signature = new Signature("connection.number", "value", noValue);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(Object[] args)
		{
			return new NumberVar(args[0]);
		}
	}

	public static Function function = new FunctionNumber();
}
