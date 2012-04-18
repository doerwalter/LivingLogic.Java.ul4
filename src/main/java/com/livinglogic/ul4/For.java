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

	public void finish(String name)
	{
		if (name != null && name.length() != 0 && !name.equals("for"))
			throw new BlockException("for ended by end" + name);
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
