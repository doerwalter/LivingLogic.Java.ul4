/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class GetSlice extends AST
{
	protected AST obj;
	protected AST index1;
	protected AST index2;

	public GetSlice(AST obj, AST index1, AST index2)
	{
		this.obj = obj;
		this.index1 = index1;
		this.index2 = index2;
	}

	public String toString()
	{
		return "getslice(" + obj + ", " + index1 + ", " + index2 + ")";
	}

	public String name()
	{
		return "getslice";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return Utils.getSlice(obj.evaluate(context), index1.evaluate(context), index2.evaluate(context));
	}
}
