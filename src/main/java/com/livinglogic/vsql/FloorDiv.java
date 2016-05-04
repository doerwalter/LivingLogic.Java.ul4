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

public class FloorDiv extends Binary
{
	public FloorDiv(SourcePart origin, Node obj1, Node obj2)
	{
		super(origin, obj1, obj2);
	}

	protected SQLSnippet sqlOracle()
	{
		SQLSnippet snippet1 = obj1.sqlOracle();
		SQLSnippet snippet2 = obj2.sqlOracle();

		switch (snippet1.type)
		{
			case BOOL:
				switch (snippet2.type)
				{
					case BOOL:
						return new SQLSnippet(Type.INT, "ul4_pkg.floordiv_bool_bool(", snippet1, ", ", snippet2, ")");
					case INT:
						return new SQLSnippet(Type.INT, "ul4_pkg.floordiv_bool_int(", snippet1, ", ", snippet2, ")");
					case NUMBER:
						return new SQLSnippet(Type.INT, "ul4_pkg.floordiv_bool_number(", snippet1, ", ", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
				break;
			case INT:
				switch (snippet2.type)
				{
					case BOOL:
						return new SQLSnippet(Type.INT, "ul4_pkg.floordiv_int_bool(", snippet1, ", ", snippet2, ")");
					case INT:
						return new SQLSnippet(Type.INT, "ul4_pkg.floordiv_int_int(", snippet1, ", ", snippet2, ")");
					case NUMBER:
						return new SQLSnippet(Type.INT, "ul4_pkg.floordiv_int_number(", snippet1, ", ", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
				break;
			case NUMBER:
				switch (snippet2.type)
				{
					case BOOL:
						return new SQLSnippet(Type.INT, "ul4_pkg.floordiv_number_bool(", snippet1, ", ", snippet2, ")");
					case INT:
						return new SQLSnippet(Type.INT, "ul4_pkg.floordiv_number_int(", snippet1, ", ", snippet2, ")");
					case NUMBER:
						return new SQLSnippet(Type.INT, "ul4_pkg.floordiv_number_number(", snippet1, ", ", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
				break;
			default:
				complain(snippet1, snippet2);
		}
		return null;
	}

	private void complain(SQLSnippet snippet1, SQLSnippet snippet2)
	{
		throw error("vsql.floordiv({}, {}) not supported!", snippet1.type, snippet2.type);
	}

	public static class Function extends Binary.Function
	{
		public String nameUL4()
		{
			return "vsql.floordiv";
		}

		public Object evaluate(BoundArguments args)
		{
			return new FloorDiv((SourcePart)args.get(2), (Node)args.get(0), (Node)args.get(1));
		}
	}
}
