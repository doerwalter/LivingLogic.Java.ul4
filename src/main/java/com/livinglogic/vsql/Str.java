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

	public Type type()
	{
		switch (obj.type())
		{
			case CLOB:
				return Type.CLOB;
			default:
				return Type.STR;
		}
	}

	protected void sqlOracle(StringBuilder buffer)
	{
		switch (obj.type())
		{
			case NULL:
				outOracle(buffer, "null");
				break;
			case BOOL:
				outOracle(buffer, "case ", obj, " when null then null when 0 then 'False' else 'True' end");
				break;
			case INT:
				outOracle(buffer, "to_char(", obj, ")");
				break;
			case NUMBER:
				outOracle(buffer, "to_char(", obj, ")");
				break;
			case DATE:
				outOracle(buffer, "ul4_pkg.str_date(", obj, ")");
				break;
			case DATETIME:
				outOracle(buffer, "ul4_pkg.str_datetime(", obj, ")");
				break;
			case TIMESTAMP:
				outOracle(buffer, "ul4_pkg.str_timestamp(", obj, ")");
				break;
			case STR:
				outOracle(buffer, obj);
				break;
			case CLOB:
				outOracle(buffer, obj);
				break;
		}
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
