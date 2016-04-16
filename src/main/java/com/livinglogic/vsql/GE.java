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

public class GE extends Binary
{
	public GE(InterpretedTemplate template, SourcePart origin, Node obj1, Node obj2)
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
				complain(type1, type2);
			case BOOL:
				switch (type2)
				{
					case BOOL:
						outOracle(buffer, "ul4_pkg.ge_bool_bool(", obj1, ", ", obj2, ")");
						break;
					case INT:
						outOracle(buffer, "ul4_pkg.ge_bool_int(", obj1, ", ", obj2, ")");
						break;
					case NUMBER:
						outOracle(buffer, "ul4_pkg.ge_bool_number(", obj1, ", ", obj2, ")");
						break;
					default:
						complain(type1, type2);
				}
				break;
			case INT:
				switch (type2)
				{
					case BOOL:
						outOracle(buffer, "ul4_pkg.ge_int_bool(", obj1, ", ", obj2, ")");
						break;
					case INT:
						outOracle(buffer, "ul4_pkg.ge_int_int(", obj1, ", ", obj2, ")");
						break;
					case NUMBER:
						outOracle(buffer, "ul4_pkg.ge_int_number(", obj1, ", ", obj2, ")");
						break;
					default:
						complain(type1, type2);
				}
				break;
			case NUMBER:
				switch (type2)
				{
					case BOOL:
						outOracle(buffer, "ul4_pkg.ge_number_bool(", obj1, ", ", obj2, ")");
						break;
					case INT:
						outOracle(buffer, "ul4_pkg.ge_number_int(", obj1, ", ", obj2, ")");
						break;
					case NUMBER:
						outOracle(buffer, "ul4_pkg.ge_number_number(", obj1, ", ", obj2, ")");
						break;
					default:
						complain(type1, type2);
				}
				break;
			case DATE:
				switch (type2)
				{
					case DATE:
						outOracle(buffer, "ul4_pkg.ge_date_date(", obj1, ", ", obj2, ")");
						break;
					case DATETIME:
						outOracle(buffer, "ul4_pkg.ge_date_datetime(", obj1, ", ", obj2, ")");
						break;
					case TIMESTAMP:
						outOracle(buffer, "ul4_pkg.ge_date_timestamp(", obj1, ", ", obj2, ")");
						break;
					default:
						complain(type1, type2);
				}
				break;
			case DATETIME:
				switch (type2)
				{
					case DATE:
						outOracle(buffer, "ul4_pkg.ge_datetime_date(", obj1, ", ", obj2, ")");
						break;
					case DATETIME:
						outOracle(buffer, "ul4_pkg.ge_datetime_datetime(", obj1, ", ", obj2, ")");
						break;
					case TIMESTAMP:
						outOracle(buffer, "ul4_pkg.ge_datetime_timestamp(", obj1, ", ", obj2, ")");
						break;
					default:
						complain(type1, type2);
				}
				break;
			case TIMESTAMP:
				switch (type2)
				{
					case DATE:
						outOracle(buffer, "ul4_pkg.ge_timestamp_date(", obj1, ", ", obj2, ")");
						break;
					case DATETIME:
						outOracle(buffer, "ul4_pkg.ge_timestamp_datetime(", obj1, ", ", obj2, ")");
						break;
					case TIMESTAMP:
						outOracle(buffer, "ul4_pkg.ge_timestamp_timestamp(", obj1, ", ", obj2, ")");
						break;
					default:
						complain(type1, type2);
				}
				break;
			case STR:
				switch (type2)
				{
					case STR:
						outOracle(buffer, "ul4_pkg.ge_str_str(", obj1, ", ", obj2, ")");
						break;
					case CLOB:
						outOracle(buffer, "ul4_pkg.ge_str_clob(", obj1, ", ", obj2, ")");
						break;
					default:
						complain(type1, type2);
				}
				break;
			case CLOB:
				switch (type2)
				{
					case STR:
						outOracle(buffer, "ul4_pkg.ge_clob_str(", obj1, ", ", obj2, ")");
						break;
					case CLOB:
						outOracle(buffer, "ul4_pkg.ge_clob_clob(", obj1, ", ", obj2, ")");
						break;
					default:
						complain(type1, type2);
				}
				break;
		}
	}

	private void complain(Type type1, Type type2)
	{
		throw error("vsql.GE(" + type1 + ", " + type2 + ") not supported!");
	}

	public static class Function extends Binary.Function
	{
		public String nameUL4()
		{
			return "vsql.GE";
		}

		public Object evaluate(BoundArguments args)
		{
			return new GE((InterpretedTemplate)args.get(3), (SourcePart)args.get(2), (Node)args.get(0), (Node)args.get(1));
		}
	}
}
