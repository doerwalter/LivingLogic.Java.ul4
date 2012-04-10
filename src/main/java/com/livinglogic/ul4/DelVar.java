/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class DelVar extends AST
{
	protected String varname;

	public DelVar(String varname)
	{
		this.varname = varname;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		template.opcode(Opcode.OC_DELVAR, varname, location);
		return -1;
	}

	public Object evaluate(EvaluationContext context)
	{
		context.remove(varname);
		return null;
	}
}
