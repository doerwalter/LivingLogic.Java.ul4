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

	protected void sqlOracle(StringBuffer buffer)
	{
		Type type1 = obj1.type();
		Type type2 = obj2.type();

		if ((type1 == Type.BOOL || type1 == Type.INT) && (type2 == Type.BOOL || type2 == Type.INT))
		{
			buffer.append("case when ");
			obj1.sqlOracle(buffer);
			buffer.append(" = ");
			obj2.sqlOracle(buffer);
			buffer.append(" then 0 else 1 end");
		}
		else if ((type1 == Type.BOOL || type1 == Type.INT || type1 == Type.NUMBER) && (type2 == Type.BOOL || type2 == Type.INT || type2 == Type.NUMBER))
		{
			buffer.append("case when ");
			obj1.sqlOracle(buffer);
			buffer.append(" = ");
			obj2.sqlOracle(buffer);
			buffer.append(" then 0 else 1 end");
		}
		else if (type1 == Type.STR)
		{
			if (type2 == Type.STR)
			{
				buffer.append("case when ");
				obj1.sqlOracle(buffer);
				buffer.append(" = ");
				obj2.sqlOracle(buffer);
				buffer.append(" then 0 else 1 end");
			}
			else if (type2 == Type.CLOB)
			{
				buffer.append("ul4_pkg.ne_str_clob");
				obj1.sqlOracle(buffer);
				buffer.append(", ");
				obj2.sqlOracle(buffer);
				buffer.append(")");
			}
			else
			{
				// mixed types are only equal, if they are both null
				buffer.append("case when ");
				obj1.sqlOracle(buffer);
				buffer.append(" is null and ");
				obj2.sqlOracle(buffer);
				buffer.append(" is null then 0 else 1 end");
			}
		}
		else if (type1 == Type.CLOB)
		{
			if (type2 == Type.STR)
			{
				buffer.append("ul4_pkg.ne_clob_str");
				obj1.sqlOracle(buffer);
				buffer.append(", ");
				obj2.sqlOracle(buffer);
				buffer.append(")");
			}
			else if (type2 == Type.CLOB)
			{
				buffer.append("ul4_pkg.ne_clob_clob");
				obj1.sqlOracle(buffer);
				buffer.append(", ");
				obj2.sqlOracle(buffer);
				buffer.append(")");
			}
			else
			{
				// mixed types are only equal, if they are both null
				buffer.append("case when ");
				obj1.sqlOracle(buffer);
				buffer.append(" is null and ");
				obj2.sqlOracle(buffer);
				buffer.append(" is null then 0 else 1 end");
			}
		}
		else
		{
			// mixed types are only equal, if they are both null
			buffer.append("case when ");
			obj1.sqlOracle(buffer);
			buffer.append(" is null and ");
			obj2.sqlOracle(buffer);
			buffer.append(" is null then 0 else 1 end");
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
