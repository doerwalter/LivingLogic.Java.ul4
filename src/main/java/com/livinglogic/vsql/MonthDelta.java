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

public class MonthDelta extends Unary
{
	public MonthDelta(InterpretedTemplate template, SourcePart origin, Node obj)
	{
		super(template, origin, obj);
	}

	protected SQLSnippet sqlOracle()
	{
		SQLSnippet snippet = obj.sqlOracle();
		switch (snippet.type)
		{
			case BOOL:
				return new SQLSnippet(Type.MONTHDELTA, "case ", snippet, " when null then null when 0 then 0 else 1 end");
			case INT:
				return new SQLSnippet(Type.MONTHDELTA, snippet);
			default:
				complain(snippet);
		}
		return null;
	}

	private void complain(SQLSnippet snippet)
	{
		throw error("vsql.MonthDelta({}) not supported!", snippet.type);
	}

	public static class Function extends Unary.Function
	{
		public String nameUL4()
		{
			return "vsql.MonthDelta";
		}

		public Object evaluate(BoundArguments args)
		{
			return new MonthDelta((InterpretedTemplate)args.get(2), (SourcePart)args.get(1), (Node)args.get(0));
		}
	}
}
