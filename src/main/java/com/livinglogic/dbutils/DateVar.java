/*
** Copyright 2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.dbutils;

import java.util.List;
import java.util.Date;

import java.sql.CallableStatement;
import java.sql.Types;
import java.sql.Timestamp;
import java.sql.SQLException;

import com.livinglogic.ul4.ArgumentTypeMismatchException;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.Function;
import com.livinglogic.ul4.Utils;

public class DateVar extends Var
{
	public DateVar()
	{
		super();
	}

	public DateVar(Object value)
	{
		if (value == noValue)
			this.value = value;
		else
			setValue(value);
	}

	public void setValue(Object value)
	{
		if (value == null || value instanceof Date || value instanceof Timestamp)
			this.value = value;
		else
			throw new ArgumentTypeMismatchException("date.value = {}", value);
	}

	public void register(CallableStatement statement, int position) throws SQLException
	{
		if (value != noValue)
		{
			Object timestamp;
			if (value instanceof Date)
				timestamp = new Timestamp(((Date)value).getTime());
			else
				timestamp = value;
			statement.setObject(position, timestamp);
		}
		statement.registerOutParameter(position, Types.TIMESTAMP);
	}

	public void fetch(CallableStatement statement, int position) throws SQLException
	{
		value = statement.getObject(position);
	}

	private static class FunctionDate extends Function
	{
		public String nameUL4()
		{
			return "connection.date";
		}

		private static final Signature signature = new Signature("value", noValue);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(List<Object> args)
		{
			return new DateVar(args.get(0));
		}
	}

	public static Function function = new FunctionDate();
}
