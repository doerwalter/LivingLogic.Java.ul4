/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class UndefinedVariable extends Undefined
{
	private static class Type extends Undefined.Type
	{
		@Override
		public String getNameUL4()
		{
			return "undefinedvariable";
		}

		@Override
		public String getDoc()
		{
			return "The result of accessing an undefined variable.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof UndefinedVariable;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	private String varname;

	public UndefinedVariable(String varname)
	{
		this.varname = varname;
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" ");
		formatter.visit(varname);
		formatter.append(">");
	}

	public String toString()
	{
		return "UndefinedVariable(" + FunctionRepr.call(varname) + ")";
	}
}
