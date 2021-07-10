/*
** Copyright 2014-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.dbutils;

import java.util.Set;

import java.sql.CallableStatement;
import java.sql.SQLException;

import com.livinglogic.ul4.EvaluationContext;
import com.livinglogic.ul4.UL4GetAttr;
import com.livinglogic.ul4.UL4SetAttr;
import com.livinglogic.ul4.UL4Dir;
import com.livinglogic.ul4.AttributeException;
import com.livinglogic.ul4.ReadonlyException;

import static com.livinglogic.utils.SetUtils.makeSet;

public abstract class Var implements UL4GetAttr, UL4SetAttr, UL4Dir
{
	public static final Object noValue = new Object();

	protected Object value;

	public Var()
	{
		this.value = noValue;
	}

	protected static Set<String> attributes = makeSet("value");

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
			case "value":
				return value == noValue ? null : value;
			default:
				return UL4GetAttr.super.getAttrUL4(context, key);
		}
	}

	public void setAttrUL4(EvaluationContext context, String key, Object value)
	{
		switch (key)
		{
			case "value":
				if (value == noValue)
					value = noValue;
				else
					setValue(value);
				break;
			default:
				throw new ReadonlyException(this, key);
		}
	}

	public abstract void register(CallableStatement statement, int position) throws SQLException;
	public abstract void fetch(CallableStatement statement, int position) throws SQLException;
	public abstract void setValue(Object value);
}
