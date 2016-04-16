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

public class NE extends Binary
{
	public NE(InterpretedTemplate template, SourcePart origin, Node obj1, Node obj2)
	{
		super(template, origin, obj1, obj2);
	}

	public Type type()
	{
		return Type.BOOL;
	}

	protected void sqlOracle(StringBuilder buffer)
	{
		Type type1 = obj1.type();
		Type type2 = obj2.type();

		switch (type1)
		{
			case NULL:
				switch (type2)
				{
					case NULL:
						outOracle(buffer, "1");
					default:
						outOracle(buffer, "case when ", obj2, " is null then 0 else 1 end");
						break;
				}
				break;
			case BOOL:
				switch (type2)
				{
					case NULL:
						outOracle(buffer, "case when ", obj1, " is null then 0 else 1 end");
						break;
					case BOOL:
						outOracle(buffer, "ul4_pkg.ne_bool_bool(", obj1, ", ", obj2, ")");
						break;
					case INT:
						outOracle(buffer, "ul4_pkg.ne_bool_int(", obj1, ", ", obj2, ")");
						break;
					case NUMBER:
						outOracle(buffer, "ul4_pkg.ne_bool_number(", obj1, ", ", obj2, ")");
						break;
					default:
						// mixed types are only equal, if they are both null
						outOracle(buffer, "case when ", obj1, " is null and ", obj2, " is null then 0 else 1 end");
						break;
				}
				break;
			case INT:
				switch (type2)
				{
					case NULL:
						outOracle(buffer, "case when ", obj1, " is null then 0 else 1 end");
						break;
					case BOOL:
						outOracle(buffer, "ul4_pkg.ne_int_bool(", obj1, ", ", obj2, ")");
						break;
					case INT:
						outOracle(buffer, "ul4_pkg.ne_int_int(", obj1, ", ", obj2, ")");
						break;
					case NUMBER:
						outOracle(buffer, "ul4_pkg.ne_int_number(", obj1, ", ", obj2, ")");
						break;
					default:
						outOracle(buffer, "case when ", obj1, " is null and ", obj2, " is null then 0 else 1 end");
						break;
				}
				break;
			case NUMBER:
				switch (type2)
				{
					case NULL:
						outOracle(buffer, "case when ", obj1, " is null then 0 else 1 end");
						break;
					case BOOL:
						outOracle(buffer, "ul4_pkg.ne_number_bool(", obj1, ", ", obj2, ")");
						break;
					case INT:
						outOracle(buffer, "ul4_pkg.ne_number_int(", obj1, ", ", obj2, ")");
						break;
					case NUMBER:
						outOracle(buffer, "ul4_pkg.ne_number_number(", obj1, ", ", obj2, ")");
						break;
					default:
						outOracle(buffer, "case when ", obj1, " is null and ", obj2, " is null then 0 else 1 end");
						break;
				}
				break;
			case DATE:
				switch (type2)
				{
					case NULL:
						outOracle(buffer, "case when ", obj1, " is null then 0 else 1 end");
						break;
					case DATE:
						outOracle(buffer, "ul4_pkg.ne_date_date(", obj1, ", ", obj2, ")");
						break;
					case DATETIME:
						outOracle(buffer, "ul4_pkg.ne_date_datetime(", obj1, ", ", obj2, ")");
						break;
					case TIMESTAMP:
						outOracle(buffer, "ul4_pkg.ne_date_timestamp(", obj1, ", ", obj2, ")");
						break;
					default:
						outOracle(buffer, "case when ", obj1, " is null and ", obj2, " is null then 0 else 1 end");
						break;
				}
				break;
			case DATETIME:
				switch (type2)
				{
					case NULL:
						outOracle(buffer, "case when ", obj1, " is null then 0 else 1 end");
						break;
					case DATE:
						outOracle(buffer, "ul4_pkg.ne_datetime_date(", obj1, ", ", obj2, ")");
						break;
					case DATETIME:
						outOracle(buffer, "ul4_pkg.ne_datetime_datetime(", obj1, ", ", obj2, ")");
						break;
					case TIMESTAMP:
						outOracle(buffer, "ul4_pkg.ne_datetime_timestamp(", obj1, ", ", obj2, ")");
						break;
					default:
						outOracle(buffer, "case when ", obj1, " is null and ", obj2, " is null then 0 else 1 end");
						break;
				}
				break;
			case TIMESTAMP:
				switch (type2)
				{
					case NULL:
						outOracle(buffer, "case when ", obj1, " is null then 0 else 1 end");
						break;
					case DATE:
						outOracle(buffer, "ul4_pkg.ne_timestamp_date(", obj1, ", ", obj2, ")");
						break;
					case DATETIME:
						outOracle(buffer, "ul4_pkg.ne_timestamp_datetime(", obj1, ", ", obj2, ")");
						break;
					case TIMESTAMP:
						outOracle(buffer, "ul4_pkg.ne_timestamp_timestamp(", obj1, ", ", obj2, ")");
						break;
					default:
						outOracle(buffer, "case when ", obj1, " is null and ", obj2, " is null then 0 else 1 end");
						break;
				}
				break;
			case STR:
				switch (type2)
				{
					case NULL:
						outOracle(buffer, "case when ", obj1, " is null then 0 else 1 end");
						break;
					case STR:
						outOracle(buffer, "ul4_pkg.ne_str_str(", obj1, ", ", obj2, ")");
						break;
					case CLOB:
						outOracle(buffer, "ul4_pkg.ne_str_clob(", obj1, ", ", obj2, ")");
						break;
					default:
						outOracle(buffer, "case when ", obj1, " is null and ", obj2, " is null then 0 else 1 end");
				}
				break;
			case CLOB:
				switch (type2)
				{
					case NULL:
						outOracle(buffer, "case when ", obj1, " is null then 0 else 1 end");
						break;
					case STR:
						outOracle(buffer, "ul4_pkg.ne_clob_str(", obj1, ", ", obj2, ")");
						break;
					case CLOB:
						outOracle(buffer, "ul4_pkg.ne_clob_clob(", obj1, ", ", obj2, ")");
						break;
					default:
						outOracle(buffer, "case when ", obj1, " is null and ", obj2, " is null then 0 else 1 end");
				}
				break;
		}
	}

	public static class Function extends Binary.Function
	{
		public String nameUL4()
		{
			return "vsql.NE";
		}

		public Object evaluate(BoundArguments args)
		{
			return new NE((InterpretedTemplate)args.get(3), (SourcePart)args.get(2), (Node)args.get(0), (Node)args.get(1));
		}
	}
}
