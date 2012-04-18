/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;

public abstract class For extends Block
{
	protected AST container;

	public For(AST container)
	{
		super();
		this.container = container;
	}

	public String getType()
	{
		return "for";
	}

	public void setContainer(AST container)
	{
		this.container = container;
	}

	public void finish(InterpretedTemplate template, Location startLocation, Location endLocation)
	{
		String type = endLocation.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("for"))
			throw new BlockException("for ended by end" + type);
	}

	public boolean handleLoopControl(String name)
	{
		return true;
	}

	abstract protected void unpackLoopVariable(EvaluationContext context, Object item);

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Object container = this.container.evaluate(context);

		Iterator iter = Utils.iterator(container);

		while (iter.hasNext())
		{
			unpackLoopVariable(context, iter.next());

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
