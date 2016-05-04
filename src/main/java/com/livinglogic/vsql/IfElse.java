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

	public IfElse(SourcePart origin, Node objIf, Node objCond, Node objElse)
	{
		super(origin);
		this.objIf = objIf;
		this.objCond = objCond;
		this.objElse = objElse;
	}

	protected SQLSnippet sqlOracle()
	{
		SQLSnippet snippetIf = objIf.sqlOracle();
		SQLSnippet snippetCond = objCond.sqlOracle();
		SQLSnippet snippetElse = objElse.sqlOracle();

		switch (snippetIf.type)
		{
			case NULL:
				switch (snippetElse.type)
				{
					case NULL:
						return new SQLSnippet(Type.NULL, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case BOOL:
						return new SQLSnippet(Type.BOOL, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case INT:
						return new SQLSnippet(Type.INT, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case NUMBER:
						return new SQLSnippet(Type.NUMBER, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case DATE:
						return new SQLSnippet(Type.DATE, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case DATETIME:
						return new SQLSnippet(Type.DATETIME, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case TIMESTAMP:
						return new SQLSnippet(Type.TIMESTAMP, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case STR:
						return new SQLSnippet(Type.STR, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case CLOB:
						return new SQLSnippet(Type.CLOB, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
				}
			case BOOL:
				switch (snippetElse.type)
				{
					case NULL:
						return new SQLSnippet(Type.BOOL, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case BOOL:
						return new SQLSnippet(Type.BOOL, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case INT:
						return new SQLSnippet(Type.INT, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case NUMBER:
						return new SQLSnippet(Type.NUMBER, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					default:
						complain(snippetIf, snippetElse);
				}
			case INT:
				switch (snippetElse.type)
				{
					case NULL:
						return new SQLSnippet(Type.INT, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case BOOL:
						return new SQLSnippet(Type.INT, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case INT:
						return new SQLSnippet(Type.INT, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case NUMBER:
						return new SQLSnippet(Type.NUMBER, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					default:
						complain(snippetIf, snippetElse);
				}
			case NUMBER:
				switch (snippetElse.type)
				{
					case NULL:
						return new SQLSnippet(Type.NUMBER, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case BOOL:
						return new SQLSnippet(Type.NUMBER, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case INT:
						return new SQLSnippet(Type.NUMBER, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case NUMBER:
						return new SQLSnippet(Type.NUMBER, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					default:
						complain(snippetIf, snippetElse);
				}
			case DATE:
				switch (snippetElse.type)
				{
					case NULL:
						return new SQLSnippet(Type.DATE, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case DATE:
						return new SQLSnippet(Type.DATE, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case DATETIME:
						return new SQLSnippet(Type.DATETIME, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case TIMESTAMP:
						return new SQLSnippet(Type.TIMESTAMP, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					default:
						complain(snippetIf, snippetElse);
				}
			case DATETIME:
				switch (snippetElse.type)
				{
					case NULL:
						return new SQLSnippet(Type.DATETIME, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case DATE:
						return new SQLSnippet(Type.DATETIME, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case DATETIME:
						return new SQLSnippet(Type.DATETIME, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case TIMESTAMP:
						return new SQLSnippet(Type.TIMESTAMP, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					default:
						complain(snippetIf, snippetElse);
				}
			case TIMESTAMP:
				switch (snippetElse.type)
				{
					case NULL:
						return new SQLSnippet(Type.TIMESTAMP, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case DATE:
						return new SQLSnippet(Type.TIMESTAMP, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case DATETIME:
						return new SQLSnippet(Type.TIMESTAMP, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case TIMESTAMP:
						return new SQLSnippet(Type.TIMESTAMP, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					default:
						complain(snippetIf, snippetElse);
				}
			case STR:
				switch (snippetElse.type)
				{
					case STR:
						return new SQLSnippet(Type.STR, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case CLOB:
						return new SQLSnippet(Type.CLOB, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					default:
						complain(snippetIf, snippetElse);
				}
			case CLOB:
				switch (snippetElse.type)
				{
					case STR:
						return new SQLSnippet(Type.CLOB, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					case CLOB:
						return new SQLSnippet(Type.CLOB, "case when ul4_pkg.bool_", snippetCond.type.toString(), "(", snippetCond, ") then ", snippetIf, " else ", snippetElse, " end");
					default:
						complain(snippetIf, snippetElse);
				}
		}
		return null;
	}

	private void complain(SQLSnippet snippetIf, SQLSnippet snippetElse)
	{
		throw error("vsql.If({}, ?, {}) not supported!", snippetIf.type, snippetElse.type);
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

		private static final Signature signature = new Signature("objif", Signature.required, "objcond", Signature.required, "objelse", Signature.required, "origin", null);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments args)
		{
			return new IfElse((SourcePart)args.get(3), (Node)args.get(0), (Node)args.get(1), (Node)args.get(2));
		}
	}
}
