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

public class Eq extends Binary
{
	public Eq(SourcePart origin, Node obj1, Node obj2)
	{
		super(origin, obj1, obj2);
	}

	protected SQLSnippet sqlOracle()
	{
		SQLSnippet snippet1 = obj1.sqlOracle();
		SQLSnippet snippet2 = obj2.sqlOracle();

		switch (snippet1.type)
		{
			case NULL:
				switch (snippet2.type)
				{
					case NULL:
						return new SQLSnippet(Type.BOOL, "1");
					default:
						return new SQLSnippet(Type.BOOL, "case when ", snippet2, " is null then 1 else 0 end");
				}
			case BOOL:
				switch (snippet2.type)
				{
					case NULL:
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null then 1 else 0 end");
					case BOOL:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_bool_bool(", snippet1, ", ", snippet2, ")");
					case INT:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_bool_int(", snippet1, ", ", snippet2, ")");
					case NUMBER:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_bool_number(", snippet1, ", ", snippet2, ")");
					default:
						// mixed types are only equal, if they are both null
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null and ", snippet2, " is null then 1 else 0 end");
				}
			case INT:
				switch (snippet2.type)
				{
					case NULL:
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null then 1 else 0 end");
					case BOOL:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_int_bool(", snippet1, ", ", snippet2, ")");
					case INT:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_int_int(", snippet1, ", ", snippet2, ")");
					case NUMBER:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_int_number(", snippet1, ", ", snippet2, ")");
					default:
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null and ", snippet2, " is null then 1 else 0 end");
				}
			case NUMBER:
				switch (snippet2.type)
				{
					case NULL:
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null then 1 else 0 end");
					case BOOL:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_number_bool(", snippet1, ", ", snippet2, ")");
					case INT:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_number_int(", snippet1, ", ", snippet2, ")");
					case NUMBER:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_number_number(", snippet1, ", ", snippet2, ")");
					default:
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null and ", snippet2, " is null then 1 else 0 end");
				}
			case DATE:
				switch (snippet2.type)
				{
					case NULL:
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null then 1 else 0 end");
					case DATE:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_date_date(", snippet1, ", ", snippet2, ")");
					case DATETIME:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_date_datetime(", snippet1, ", ", snippet2, ")");
					case TIMESTAMP:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_date_timestamp(", snippet1, ", ", snippet2, ")");
					default:
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null and ", snippet2, " is null then 1 else 0 end");
				}
			case DATETIME:
				switch (snippet2.type)
				{
					case NULL:
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null then 1 else 0 end");
					case DATE:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_datetime_date(", snippet1, ", ", snippet2, ")");
					case DATETIME:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_datetime_datetime(", snippet1, ", ", snippet2, ")");
					case TIMESTAMP:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_datetime_timestamp(", snippet1, ", ", snippet2, ")");
					default:
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null and ", snippet2, " is null then 1 else 0 end");
				}
			case TIMESTAMP:
				switch (snippet2.type)
				{
					case NULL:
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null then 1 else 0 end");
					case DATE:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_timestamp_date(", snippet1, ", ", snippet2, ")");
					case DATETIME:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_timestamp_datetime(", snippet1, ", ", snippet2, ")");
					case TIMESTAMP:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_timestamp_timestamp(", snippet1, ", ", snippet2, ")");
					default:
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null and ", snippet2, " is null then 1 else 0 end");
				}
			case STR:
				switch (snippet2.type)
				{
					case NULL:
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null then 1 else 0 end");
					case STR:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_str_str(", snippet1, ", ", snippet2, ")");
					case CLOB:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_str_clob(", snippet1, ", ", snippet2, ")");
					default:
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null and ", snippet2, " is null then 1 else 0 end");
				}
			case CLOB:
				switch (snippet2.type)
				{
					case NULL:
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null then 1 else 0 end");
					case STR:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_clob_str(", snippet1, ", ", snippet2, ")");
					case CLOB:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.eq_clob_clob(", snippet1, ", ", snippet2, ")");
					default:
						return new SQLSnippet(Type.BOOL, "case when ", snippet1, " is null and ", snippet2, " is null then 1 else 0 end");
				}
		}
		return null;
	}

	public static class Function extends Binary.Function
	{
		public String nameUL4()
		{
			return "vsql.Eq";
		}

		public Object evaluate(BoundArguments args)
		{
			return new Eq((SourcePart)args.get(2), (Node)args.get(0), (Node)args.get(1));
		}
	}
}
