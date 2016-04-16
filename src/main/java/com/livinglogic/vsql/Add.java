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

public class Add extends Binary
{
	public Add(InterpretedTemplate template, SourcePart origin, Node obj1, Node obj2)
	{
		super(template, origin, obj1, obj2);
	}

	public Type type()
	{
		Type type1 = obj1.type();
		Type type2 = obj2.type();

		switch (type1)
		{
			case NULL:
				complain(type1, type2);
			case BOOL:
			case INT:
				switch (type2)
				{
					case BOOL:
					case INT:
						return Type.INT;
					case NUMBER:
						return Type.NUMBER;
					default:
						complain(type1, type2);
				}
			case NUMBER:
				switch (type2)
				{
					case BOOL:
					case INT:
					case NUMBER:
						return Type.NUMBER;
					default:
						complain(type1, type2);
				}
			case DATE:
			case DATETIME:
			case TIMESTAMP:
				complain(type1, type2);
			case STR:
				switch (type2)
				{
					case STR:
						return Type.STR;
					case CLOB:
						return Type.CLOB;
					default:
						complain(type1, type2);
				}
				break;
			case CLOB:
				switch (type2)
				{
					case STR:
					case CLOB:
						return Type.CLOB;
					default:
						complain(type1, type2);
				}
				break;
		}
		complain(type1, type2);
		return null;
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
			case INT:
			case NUMBER:
				switch (type2)
				{
					case BOOL:
					case INT:
					case NUMBER:
						outOracle(buffer, "(", obj1, "+", obj2, ")");
						break;
					default:
						complain(type1, type2);
				}
				break;
			case DATE:
			case DATETIME:
			case TIMESTAMP:
				complain(type1, type2);
			case STR:
				switch (type2)
				{
					case STR:
						outOracle(buffer, "(", obj1, "||", obj2, ")");
						break;
					case CLOB:
						outOracle(buffer, "(", obj1, "||", obj2, ")");
						break;
					default:
						complain(type1, type2);
				}
				break;
			case CLOB:
				switch (type2)
				{
					case STR:
						outOracle(buffer, "(", obj1, "||", obj2, ")");
						break;
					case CLOB:
						outOracle(buffer, "(", obj1, "||", obj2, ")");
						break;
					default:
						complain(type1, type2);
				}
				break;
		}
	}

	private void complain(Type type1, Type type2)
	{
		throw error("vsql.add(" + type1 + ", " + type2 + ") not supported!");
	}

	public static class Function extends Binary.Function
	{
		public String nameUL4()
		{
			return "vsql.add";
		}

		public Object evaluate(BoundArguments args)
		{
			return new Add((InterpretedTemplate)args.get(3), (SourcePart)args.get(2), (Node)args.get(0), (Node)args.get(1));
		}
	}
}
