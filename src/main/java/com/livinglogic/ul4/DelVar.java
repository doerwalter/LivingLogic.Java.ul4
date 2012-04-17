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

	public String toString()
	{
		return "delvar(" + Utils.repr(varname) + ")";
	}

	public String name()
	{
		return "delvar";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.remove(varname);
		return null;
	}
}
