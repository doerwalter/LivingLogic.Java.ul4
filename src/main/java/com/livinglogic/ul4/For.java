/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;

import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;

public abstract class For extends Block
{
	protected AST container;

	public For(Location location, AST container)
	{
		super(location);
		this.container = container;
	}

	public void setContainer(AST container)
	{
		this.container = container;
	}

	public void finish(InterpretedTemplate template, Location endlocation)
	{
		super.finish(template, endlocation);
		String type = endlocation.getCode().trim();
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
		Object container = this.container.decoratedEvaluate(context);

		Iterator iter = Utils.iterator(container);

		while (iter.hasNext())
		{
			unpackLoopVariable(context, iter.next());

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
		encoder.dump(container);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		container = (AST)decoder.load();
	}
}
