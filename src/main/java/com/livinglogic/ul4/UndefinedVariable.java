/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class UndefinedVariable extends Undefined
{
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
