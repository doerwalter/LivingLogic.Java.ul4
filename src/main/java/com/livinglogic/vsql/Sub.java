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

public class Sub extends Binary
{
	public Sub(InterpretedTemplate template, SourcePart origin, Node obj1, Node obj2)
	{
		super(template, origin, obj1, obj2);
	}

	public Type type()
	{
		Type type1 = obj1.type();
		Type type2 = obj2.type();

		return Type.widenNumber(type1, type2, this, "vsql.sub({}, {}) not supported!", type1, type2);
	}

	protected void sqlOracle(StringBuilder buffer)
	{
		Type type1 = obj1.type();
		Type type2 = obj2.type();

		if ((type1 == Type.BOOL || type1 == Type.INT || type1 == Type.NUMBER) && (type2 == Type.BOOL || type2 == Type.INT || type2 == Type.NUMBER))
		{
			buffer.append("(");
			obj1.sqlOracle(buffer);
			buffer.append("-");
			obj2.sqlOracle(buffer);
			buffer.append(")");
		}
		else
			throw error("vsql.sub(" + type1 + ", " + type2 + ") not supported!");
	}

	public static class Function extends Binary.Function
	{
		public String nameUL4()
		{
			return "vsql.sub";
		}

		public Object evaluate(BoundArguments args)
		{
			return new Sub((InterpretedTemplate)args.get(3), (SourcePart)args.get(2), (Node)args.get(0), (Node)args.get(1));
		}
	}
}
