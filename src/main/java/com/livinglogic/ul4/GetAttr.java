/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class GetAttr extends AST
{
	protected AST obj;
	protected String attrname;

	public GetAttr(AST obj, String attrname)
	{
		this.obj = obj;
		this.attrname = attrname;
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = obj.compile(template, registers, location);
		template.opcode(Opcode.OC_GETATTR, r, r, attrname, location);
		return r;
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.getItem(obj.evaluate(context), attrname);
	}

	public String toString()
	{
		return "getattr(" + obj + ", " + Utils.repr(attrname) + ")";
	}
}
