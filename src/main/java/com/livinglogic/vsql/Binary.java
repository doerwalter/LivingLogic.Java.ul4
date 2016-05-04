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
import com.livinglogic.ul4.ArgumentTypeMismatchException;
import com.livinglogic.ul4.UndefinedKey;
import com.livinglogic.ul4.Utils;
import com.livinglogic.ul4.FunctionStr;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

public abstract class Binary extends Node
{
	protected Node obj1;
	protected Node obj2;

	public Binary(SourcePart origin, Node obj1, Node obj2)
	{
		super(origin);
		this.obj1 = obj1;
		this.obj2 = obj2;
	}

	public static abstract class Function extends com.livinglogic.ul4.Function
	{
		private static final Signature signature = new Signature("obj1", Signature.required, "obj2", Signature.required, "origin", null);

		public Signature getSignature()
		{
			return signature;
		}
	}

	protected static Set<String> attributes = makeExtendedSet(Node.attributes, "obj1", "obj2");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "obj1":
				return obj1;
			case "obj2":
				return obj2;
			default:
				return super.getItemStringUL4(key);
		}
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" obj1=");
		formatter.visit(obj1);
		formatter.append(" obj2=");
		formatter.visit(obj2);
		formatter.append(">");
	}
}
