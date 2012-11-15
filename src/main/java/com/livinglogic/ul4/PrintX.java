/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

/**
 * {@code PrintX} is an unary AST node that writes a string version of its
 * operand to the output stream and replaces the characters {@code <}, {@code >},
 * {@code &}, {@code '} and {@code "} with the appropriate XML character
 * entities.
 */
public class PrintX extends LocationAST
{
	/**
	 * The object to be printed
	 */
	protected AST obj;

	public PrintX(Location location, AST obj)
	{
		super(location);
		this.obj = obj;
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("printx(");
		buffer.append(obj.toString(indent));
		buffer.append(")\n");
		return buffer.toString();
	}

	public String getType()
	{
		return "printx";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.write(FunctionXMLEscape.call(obj.decoratedEvaluate(context)));
		return null;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (AST)decoder.load();
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("obj", new ValueMaker(){public Object getValue(Object object){return ((PrintX)object).obj;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
