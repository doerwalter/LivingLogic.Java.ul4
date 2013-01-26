/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
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

	public String toString(InterpretedCode code, int indent)
	{
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("render(");
		buffer.append(obj.toString(code, indent));
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
		context.write(FunctionStr.call(object)); // This normally outputs nothing, because the content of the render tag normally is a call to the {@code render} method
		return null;
	}
}
