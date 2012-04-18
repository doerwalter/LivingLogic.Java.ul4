/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class DelVar extends AST
{
	protected String varname;

	public DelVar(String varname)
	{
		this.varname = varname;
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("delvar(" + Utils.repr(varname) + ")\n");
		return buffer.toString();
	}

	public String getType()
	{
		return "delvar";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.remove(varname);
		return null;
	}
}
