/*
** Copyright 2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Set;
import java.util.Date;
import java.util.Calendar;
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

public class IfElse extends Node
{
	protected Node objIf;
	protected Node objCond;
	protected Node objElse;

	public IfElse(InterpretedTemplate template, SourcePart origin, Node objIf, Node objCond, Node objElse)
	{
		super(template, origin);
		this.objIf = objIf;
		this.objCond = objCond;
		this.objElse = objElse;
	}

	public Type type()
	{
		Type typeIf = objIf.type();
		Type typeCond = objCond.type();
		Type typeElse = objElse.type();

		if (typeIf == Type.BOOL)
		{
			if (typeElse == Type.BOOL)
				return Type.BOOL;
			else if (typeElse == Type.INT)
				return Type.INT;
			else if (typeElse == Type.NUMBER)
				return Type.NUMBER;
		}
		else if (typeIf == Type.INT)
		{
			if (typeElse == Type.BOOL || typeElse == Type.INT)
				return Type.INT;
			else if (typeElse == Type.NUMBER)
				return Type.NUMBER;
		}
		else if (typeIf == Type.NUMBER)
		{
			if (typeElse == Type.BOOL || typeElse == Type.INT || typeElse == Type.NUMBER)
				return Type.NUMBER;
		}
		else if (typeIf == Type.STR)
		{
			if (typeElse == Type.STR)
				return Type.STR;
			else if (typeElse == Type.CLOB)
				return Type.CLOB;
		}
		else if (typeIf == Type.CLOB)
		{
			if (typeElse == Type.STR || typeElse == Type.CLOB)
				return Type.CLOB;
		}

		throw error("vsql.ifelse(" + typeIf + ", ?, " + typeElse + ") not supported!");
	}

	protected void sqlOracle(StringBuffer buffer)
	{
		Type typeIf = objIf.type();
		Type typeCond = objCond.type();
		Type typeElse = objElse.type();
		Type type = type();

		buffer.append("case when ul4_pkg.bool_");
		buffer.append(typeCond.toString());
		buffer.append("(");
		objCond.sqlOracle(buffer);
		buffer.append(") then ");
		objIf.sqlOracle(buffer);
		buffer.append(" else ");
		objElse.sqlOracle(buffer);
		buffer.append(" end");
	}

	protected static Set<String> attributes = makeExtendedSet(Node.attributes, "objif", "objcond", "objelse");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "objif":
				return objIf;
			case "objcond":
				return objCond;
			case "objelse":
				return objElse;
			default:
				return super.getItemStringUL4(key);
		}
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" objif=");
		formatter.visit(objIf);
		formatter.append(" objcond=");
		formatter.visit(objCond);
		formatter.append(" objelse=");
		formatter.visit(objElse);
		formatter.append(">");
	}

	public static class Function extends com.livinglogic.ul4.Function
	{
		public String nameUL4()
		{
			return "vsql.ifelse";
		}

		private static final Signature signature = new Signature("objif", Signature.required, "objcond", Signature.required, "objelse", Signature.required, "origin", null, "template", null);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments args)
		{
			return new IfElse((InterpretedTemplate)args.get(4), (SourcePart)args.get(3), (Node)args.get(0), (Node)args.get(1), (Node)args.get(2));
		}
	}
}
