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

public class Today extends Node
{
	public Today(SourcePart origin)
	{
		super(origin);
	}

	protected SQLSnippet sqlOracle()
	{
		return new SQLSnippet(Type.DATE, "trunc(sysdate)");
	}

	public static class Function extends Unary.Function
	{
		public String nameUL4()
		{
			return "vsql.today";
		}

		public Object evaluate(BoundArguments args)
		{
			return new Today((SourcePart)args.get(0));
		}
	}
}
