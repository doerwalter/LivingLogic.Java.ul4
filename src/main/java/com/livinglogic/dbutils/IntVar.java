/*
** Copyright 2014-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.dbutils;

import java.util.List;
import java.math.BigInteger;

import java.sql.CallableStatement;
import java.sql.Types;
import java.sql.SQLException;

import com.livinglogic.ul4.ArgumentTypeMismatchException;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.Function;
import com.livinglogic.ul4.Utils;
import com.livinglogic.ul4.BoundArguments;

public class IntVar extends Var
{
	public IntVar()
	{
		super();
	}

	public IntVar(Object value)
	{
		if (value == noValue)
			this.value = value;
		else
			setValue(value);
	}

	public void setValue(Object value)
	{
		if (value == null || value instanceof Boolean || value instanceof Number)
			this.value = value;
		else
			throw new ArgumentTypeMismatchException("int.value = {!t} not supported", value);
	}

	public void register(CallableStatement statement, int position) throws SQLException
	{
		if (value != noValue)
			statement.setObject(position, value);
		statement.registerOutParameter(position, Types.INTEGER);
	}

	public void fetch(CallableStatement statement, int position) throws SQLException
	{
		Object newValue = statement.getObject(position);
		value = (newValue instanceof BigInteger) ? Utils.narrowBigInteger((BigInteger)newValue) : newValue;
	}

	private static class FunctionInt extends Function
	{
		public String nameUL4()
		{
			return "connection.int";
		}

		private static final Signature signature = new Signature("value", noValue);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments args)
		{
			return new IntVar(args.get(0));
		}
	}

	public static Function function = new FunctionInt();
}
