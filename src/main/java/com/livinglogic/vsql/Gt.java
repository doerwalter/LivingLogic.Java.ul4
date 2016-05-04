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

public class Gt extends Binary
{
	public Gt(SourcePart origin, Node obj1, Node obj2)
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
				complain(snippet1, snippet2);
			case BOOL:
				switch (snippet2.type)
				{
					case BOOL:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_bool_bool(", snippet1, ", ", snippet2, ")");
					case INT:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_bool_int(", snippet1, ", ", snippet2, ")");
					case NUMBER:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_bool_number(", snippet1, ", ", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
				break;
			case INT:
				switch (snippet2.type)
				{
					case BOOL:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_int_bool(", snippet1, ", ", snippet2, ")");
					case INT:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_int_int(", snippet1, ", ", snippet2, ")");
					case NUMBER:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_int_number(", snippet1, ", ", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
				break;
			case NUMBER:
				switch (snippet2.type)
				{
					case BOOL:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_number_bool(", snippet1, ", ", snippet2, ")");
					case INT:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_number_int(", snippet1, ", ", snippet2, ")");
					case NUMBER:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_number_number(", snippet1, ", ", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
				break;
			case DATE:
				switch (snippet2.type)
				{
					case DATE:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_date_date(", snippet1, ", ", snippet2, ")");
					case DATETIME:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_date_datetime(", snippet1, ", ", snippet2, ")");
					case TIMESTAMP:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_date_timestamp(", snippet1, ", ", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
				break;
			case DATETIME:
				switch (snippet2.type)
				{
					case DATE:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_datetime_date(", snippet1, ", ", snippet2, ")");
					case DATETIME:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_datetime_datetime(", snippet1, ", ", snippet2, ")");
					case TIMESTAMP:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_datetime_timestamp(", snippet1, ", ", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
				break;
			case TIMESTAMP:
				switch (snippet2.type)
				{
					case DATE:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_timestamp_date(", snippet1, ", ", snippet2, ")");
					case DATETIME:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_timestamp_datetime(", snippet1, ", ", snippet2, ")");
					case TIMESTAMP:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_timestamp_timestamp(", snippet1, ", ", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
				break;
			case STR:
				switch (snippet2.type)
				{
					case STR:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_str_str(", snippet1, ", ", snippet2, ")");
					case CLOB:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_str_clob(", snippet1, ", ", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
				break;
			case CLOB:
				switch (snippet2.type)
				{
					case STR:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_clob_str(", snippet1, ", ", snippet2, ")");
					case CLOB:
						return new SQLSnippet(Type.BOOL, "ul4_pkg.gt_clob_clob(", snippet1, ", ", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
				break;
		}
		return null;
	}

	private void complain(SQLSnippet snippet1, SQLSnippet snippet2)
	{
		throw error("vsql.Gt({}, {}) not supported!", snippet1.type, snippet2.type);
	}

	public static class Function extends Binary.Function
	{
		public String nameUL4()
		{
			return "vsql.Gt";
		}

		public Object evaluate(BoundArguments args)
		{
			return new Gt((SourcePart)args.get(2), (Node)args.get(0), (Node)args.get(1));
		}
	}
}
