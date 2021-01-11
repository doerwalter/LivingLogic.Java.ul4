/*
** Copyright 2014-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.dbutils;

import java.util.Set;

import java.sql.CallableStatement;
import java.sql.SQLException;

import com.livinglogic.ul4.UL4GetAttr;
import com.livinglogic.ul4.UL4SetAttr;
import com.livinglogic.ul4.UL4Dir;
import com.livinglogic.ul4.AttributeException;
import com.livinglogic.ul4.ReadonlyException;

import static com.livinglogic.utils.SetUtils.makeSet;

public abstract class Var implements UL4GetAttr, UL4SetAttr, UL4Dir
{
	public static Object noValue = new Object();
	protected Object value;

	public Var()
	{
		this.value = noValue;
	}

	protected static Set<String> attributes = makeSet("value");

	public Set<String> dirUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "value":
				return value == noValue ? null : value;
			default:
				throw new AttributeException(this, key);
		}
	}

	public void setAttrUL4(String key, Object value)
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
