/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class Render extends UnaryTag
{
	public Render(Location location, AST obj)
	{
		super(location, obj);
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("render(");
		buffer.append(obj.toString(indent));
		buffer.append(")\n");
		return buffer.toString();
	}

	public String getType()
	{
		return "render";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Object object = obj.decoratedEvaluate(context);
		if (object != null)
			context.write(FunctionStr.call(object));
		return null;
	}
}
