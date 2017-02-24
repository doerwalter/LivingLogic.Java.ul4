/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Iterator;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class ForBlockAST extends BlockAST
{
	/**
	 * This is either a string or a list of strings/lists
	 */
	protected Object varname;
	protected CodeAST container;

	public ForBlockAST(Tag tag, Slice pos, Object varname, CodeAST container)
	{
		super(tag, pos);
		this.varname = varname;
		this.container = container;
	}

	public String getType()
	{
		return "forblock";
	}

	public void finish(Tag endtag)
	{
		super.finish(endtag);
		String type = endtag.getCodeText().trim();
		if (type != null && type.length() != 0 && !type.equals("for"))
			throw new BlockException("for ended by end" + type);
	}

	public void toString(Formatter formatter)
	{
		formatter.write("for ");
		toStringFromSource(formatter);
		formatter.write(":");
		formatter.lf();
		formatter.indent();
		super.toString(formatter);
		formatter.dedent();
	}

	public boolean handleLoopControl(String name)
	{
		return true;
	}

	public Object evaluate(EvaluationContext context)
	{
		Object container = this.container.decoratedEvaluate(context);

		Iterator iter = Utils.iterator(container);

		while (iter.hasNext())
		{
			for (Utils.LValueValue lvv : Utils.unpackVariable(varname, iter.next()))
				lvv.getLValue().evaluateSet(context, lvv.getValue());

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
		container = (CodeAST)decoder.load();
	}
}
