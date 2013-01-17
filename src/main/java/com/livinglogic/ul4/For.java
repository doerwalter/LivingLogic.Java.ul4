/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class For extends Block
{
	/**
	 * This is either a string or a list of strings/lists
	 */
	protected Object varname;
	protected AST container;

	public For(Location location, Object varname, AST container)
	{
		super(location);
		this.varname = varname;
		this.container = container;
	}

	public String getType()
	{
		return "for";
	}

	public void finish(InterpretedTemplate template, Location endlocation)
	{
		super.finish(template, endlocation);
		String type = endlocation.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("for"))
			throw new BlockException("for ended by end" + type);
	}

	public String toString(InterpretedTemplate template, int indent)
	{
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("for ");
		Utils.formatVarname(buffer, varname);
		buffer.append(" in ");
		buffer.append(container.toString(template, indent));
		buffer.append("\n");
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("{\n");
		++indent;
		for (AST item : content)
			buffer.append(item.toString(template, indent));
		--indent;
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("}\n");
		return buffer.toString();
	}

	public boolean handleLoopControl(String name)
	{
		return true;
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Object container = this.container.decoratedEvaluate(context);

		Iterator iter = Utils.iterator(container);

		while (iter.hasNext())
		{
			Utils.unpackVariable(context.getVariables(), varname, iter.next());

			try
			{
				for (AST item : content)
					item.decoratedEvaluate(context);
			}
			catch (BreakException ex)
			{
				break; // breaking this while loop breaks the evaluated for loop
			}
			catch (ContinueException ex)
			{
				// doing nothing here does exactly what we need ;)
			}
		}
		return null;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(varname);
		encoder.dump(container);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		varname = decoder.load();
		container = (AST)decoder.load();
	}
}
