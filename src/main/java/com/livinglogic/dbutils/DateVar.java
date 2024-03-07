/*
** Copyright 2014-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.dbutils;

import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.sql.CallableStatement;
import java.sql.Types;
import java.sql.Timestamp;
import java.sql.SQLException;

import com.livinglogic.ul4.EvaluationContext;
import com.livinglogic.ul4.ArgumentTypeMismatchException;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.Function;
import com.livinglogic.ul4.BoundArguments;

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
		if (value == null || value instanceof Date || value instanceof LocalDate || value instanceof LocalDateTime || value instanceof Timestamp)
			this.value = value;
		else
			throw new ArgumentTypeMismatchException("date.value = {!t} not supported", value);
	}

	public void register(CallableStatement statement, int position) throws SQLException
	{
		if (value != noValue)
		{
			Object timestamp;
			if (value instanceof Date)
				timestamp = new Timestamp(((Date)value).getTime());
			else if (value instanceof LocalDateTime)
				timestamp = Timestamp.valueOf((LocalDateTime)value);
			else if (value instanceof LocalDate)
				timestamp = Timestamp.valueOf(((LocalDate)value).atStartOfDay());
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
		@Override
		public String getNameUL4()
		{
			return "date";
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
			return new DateVar(args.get(0));
		}
	}

	public static final Function function = new FunctionDate();
}
