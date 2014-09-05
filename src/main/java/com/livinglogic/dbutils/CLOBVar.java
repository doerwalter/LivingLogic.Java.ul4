/*
** Copyright 2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.dbutils;

import java.sql.CallableStatement;
import java.sql.Types;
import java.sql.Clob;
import java.sql.SQLException;

import com.livinglogic.ul4.ArgumentTypeMismatchException;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.Function;
import com.livinglogic.ul4.Utils;

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
			throw new ArgumentTypeMismatchException("clob.value = {}", value);
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
			newValue = ((Clob)newValue).getSubString(1L, (int)((Clob)newValue).length());
		value = newValue;
	}

	private static class FunctionCLOB extends Function
	{
		public String nameUL4()
		{
			return "clob";
		}

		protected Signature makeSignature()
		{
			return new Signature(
				nameUL4(),
				"value", noValue
			);
		}

		public Object evaluate(Object[] args)
		{
			return new CLOBVar(args[0]);
		}
	}

	public static Function function = new FunctionCLOB();
}
