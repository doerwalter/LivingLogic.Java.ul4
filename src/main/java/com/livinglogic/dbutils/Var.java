/*
** Copyright 2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.dbutils;

import java.util.Set;

import java.sql.CallableStatement;
import java.sql.SQLException;

import com.livinglogic.ul4.UL4GetSetAttributes;
import com.livinglogic.ul4.UndefinedKey;
import com.livinglogic.ul4.ReadonlyException;

import static com.livinglogic.utils.SetUtils.makeSet;

public abstract class Var implements UL4GetSetAttributes
{
	public static Object noValue = new Object();
	protected Object value;

	public Var()
	{
		this.value = noValue;
	}

	protected static Set<String> attributes = makeSet("value");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("value".equals(key))
			return value == noValue ? null : value;
		else
			return new UndefinedKey(key);
	}

	public void setItemStringUL4(String key, Object value)
	{
		if ("value".equals(key))
		{
			if (value == noValue)
				value = noValue;
			else
				setValue(value);
		}
		else
			throw new ReadonlyException(key);
	}

	public abstract void register(CallableStatement statement, int position) throws SQLException;
	public abstract void fetch(CallableStatement statement, int position) throws SQLException;
	public abstract void setValue(Object value);
}
