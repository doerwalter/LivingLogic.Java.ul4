/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class WhileBlockAST extends BlockAST
{
	protected AST condition;

	public WhileBlockAST(Tag tag, int start, int end, AST condition)
	{
		super(tag, start, end);
		this.condition = condition;
	}

	public String getType()
	{
		return "whileblock";
	}

	public void finish(Tag endtag)
	{
		super.finish(endtag);
		String type = endtag.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("while"))
			throw new BlockException("while ended by end" + type);
	}

	public void toString(Formatter formatter)
	{
		formatter.write(getType());
		formatter.write(" ");
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

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(condition);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		condition = (AST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(BlockAST.attributes, "condition");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "condition":
				return condition;
			default:
				return super.getItemStringUL4(key);
		}
	}

	public Object evaluate(EvaluationContext context)
	{
		for (;;)
		{
			Object condition = this.condition.decoratedEvaluate(context);
			if (!FunctionBool.call(condition))
				break;

			try
			{
				for (AST item : content)
					item.decoratedEvaluate(context);
			}
			catch (BreakException ex)
			{
				break; // breaking this for loop breaks the evaluated while loop
			}
			catch (ContinueException ex)
			{
				// doing nothing here does exactly what we need ;)
			}
		}
		return null;
	}
}
