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
		Type type = obj.type();
		return (type == Type.CLOB) ? type : Type.STR;
	}

	protected void sqlOracle(StringBuffer buffer)
	{
		Type type = obj.type();

		if (type == Type.BOOL)
		{
			buffer.append("(case ");
			obj.sqlOracle(buffer);
			buffer.append(" when 1 then 'True' when 0 then 'False' else null end)");
		}
		else if (type == Type.INT)
		{
			buffer.append("to_char(");
			obj.sqlOracle(buffer);
			buffer.append(")");
		}
		else if (type == Type.NUMBER)
		{
			buffer.append("to_char(");
			obj.sqlOracle(buffer);
			buffer.append(")");
		}
		else if (type == Type.DATE)
		{
			buffer.append("to_char(");
			obj.sqlOracle(buffer);
			buffer.append(", 'YYYY-MM-DD')");
		}
		else if (type == Type.DATETIME)
		{
			buffer.append("to_char(");
			obj.sqlOracle(buffer);
			buffer.append(", 'YYYY-MM-DD HH24:MI:SS')");
		}
		else if (type == Type.TIMESTAMP)
		{
			buffer.append("to_char(");
			obj.sqlOracle(buffer);
			buffer.append(", 'YYYY-MM-DD HH24:MI:SS.FF6')");
		}
		else if (type == Type.STR || type == Type.CLOB)
		{
			obj.sqlOracle(buffer);
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
