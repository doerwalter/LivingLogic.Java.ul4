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

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

public class FieldRef extends Node
{
	protected Field field;

	public FieldRef(SourcePart origin, Field field)
	{
		super(origin);
		this.field = field;
	}

	protected SQLSnippet sqlOracle()
	{
		return new SQLSnippet(field.type, field.sql);
	}

	public static class Function extends com.livinglogic.ul4.Function
	{
		public String nameUL4()
		{
			return "vsql.field";
		}

		private static final Signature signature = new Signature("field", Signature.required, "origin", null);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments args)
		{
			return new FieldRef((SourcePart)args.get(1), (Field)args.get(0));
		}
	}

	protected static Set<String> attributes = makeExtendedSet(Node.attributes, "field");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "field":
				return field;
			default:
				return super.getItemStringUL4(key);
		}
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" field=");
		formatter.visit(field);
		formatter.append(">");
	}
}
