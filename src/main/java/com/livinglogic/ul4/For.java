/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;

public class For extends Block
{
	protected String itername;
	protected AST container;

	public For(String itername, AST container)
	{
		super();
		this.itername = itername;
		this.container = container;
	}

	public String name()
	{
		return "for";
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("for ");
		buffer.append(itername);
		buffer.append(" in ");
		buffer.append(container);
		buffer.append("\n");
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("{\n");
		++indent;
		for (AST item : content)
			buffer.append(item.toString(indent));
		--indent;
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("}\n");
		return buffer.toString();
	}

	public void finish(String name)
	{
		if (name != null && name.length() != 0 && !name.equals("for"))
			throw new BlockException("for ended by end" + name);
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Object container = this.container.evaluate(context);

		Iterator iter = Utils.iterator(container);

		while (iter.hasNext())
		{
			Object item = iter.next();
			context.put(itername, item);

			try
			{
				super.evaluate(context);
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
}
