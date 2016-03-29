/*
** Copyright 2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Set;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;

import com.livinglogic.ul4.InterpretedTemplate;
import com.livinglogic.ul4.SourcePart;
import com.livinglogic.ul4.BoundMethod;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.UL4Attributes;
import com.livinglogic.ul4.UL4GetItemString;
import com.livinglogic.ul4.UL4Repr;
import com.livinglogic.ul4.BoundArguments;
import com.livinglogic.ul4.SourceException;
import com.livinglogic.ul4.UndefinedKey;
import com.livinglogic.ul4.Utils;
import com.livinglogic.ul4.FunctionStr;

import static com.livinglogic.utils.SetUtils.makeSet;

public class Field
{
	protected String identifier;
	protected Type type;
	protected String sql;

	public Field(String identifier, Type type, String sql)
	{
		this.identifier = identifier;
		this.type = type;
		this.sql = sql;
	}

	public Type type()
	{
		return type;
	}

	protected void sqlOracle(StringBuffer buffer)
	{
		buffer.append(sql);
	}

	protected static Set<String> attributes = makeSet("identifier", "type", "sql");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "identifier":
				return identifier;
			case "type":
				return type.toString();
			case "sql":
				return sql;
			default:
				return new UndefinedKey(key);
		}
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" identifier=");
		formatter.visit(identifier);
		formatter.append(" type=");
		formatter.visit(type.toString());
		formatter.append(" sql=");
		formatter.visit(sql);
		formatter.append(">");
	}
}
