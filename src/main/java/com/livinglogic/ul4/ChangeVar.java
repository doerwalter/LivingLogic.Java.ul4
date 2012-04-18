/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

abstract class ChangeVar extends AST
{
	protected String varname;
	protected AST value;

	public ChangeVar(String varname, AST value)
	{
		this.varname = varname;
		this.value = value;
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append(getType() + "(" + Utils.repr(varname) + ", " + value + ")\n");
		return buffer.toString();
	}
}
