/*
** Copyright 2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Set;

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

public class Str extends Unary
{
	public Str(InterpretedTemplate template, SourcePart origin, Node obj)
	{
		super(template, origin, obj);
	}

	protected SQLSnippet sqlOracle()
	{
		SQLSnippet snippet = obj.sqlOracle();
		switch (snippet.type)
		{
			case NULL:
				return new SQLSnippet(Type.STR, "null");
			case BOOL:
				return new SQLSnippet(Type.STR, "case ", snippet, " when null then null when 0 then 'False' else 'True' end");
			case INT:
				return new SQLSnippet(Type.STR, "to_char(", snippet, ")");
			case NUMBER:
				return new SQLSnippet(Type.STR, "to_char(", snippet, ")");
			case DATE:
				return new SQLSnippet(Type.STR, "ul4_pkg.str_date(", snippet, ")");
			case DATETIME:
				return new SQLSnippet(Type.STR, "ul4_pkg.str_datetime(", snippet, ")");
			case TIMESTAMP:
				return new SQLSnippet(Type.STR, "ul4_pkg.str_timestamp(", snippet, ")");
			case STR:
				return new SQLSnippet(Type.STR, snippet);
			case CLOB:
				return new SQLSnippet(Type.CLOB, snippet);
		}
		return null;
	}

	public static class Function extends Unary.Function
	{
		public String nameUL4()
		{
			return "vsql.str";
		}

		public Object evaluate(BoundArguments args)
		{
			return new Str((InterpretedTemplate)args.get(2), (SourcePart)args.get(1), (Node)args.get(0));
		}
	}
}
