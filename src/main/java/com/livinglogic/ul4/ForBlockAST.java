/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Iterator;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;


/**
AST node for a {@code <?for?>} loop.
**/
public class ForBlockAST extends BlockAST
{
	protected static class Type extends BlockAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ForBlockAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.forblock";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a ``<?for?>`` loop.";
		}

		@Override
		public ForBlockAST create(String id)
		{
			return new ForBlockAST(null, -1, -1, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ForBlockAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	/**
	This is either a string or a list of strings/lists
	**/
	protected Object varname;
	protected CodeAST container;

	public ForBlockAST(Template template, int startPosStart, int startPosStop, int stopPosStart, int stopPosStop, Object varname, CodeAST container)
	{
		super(template, startPosStart, startPosStop, stopPosStart, stopPosStop);
		this.varname = varname;
		this.container = container;
	}

	@Override
	public String getType()
	{
		return "forblock";
	}

	@Override
	public String getBlockTag()
	{
		return "<?for?>";
	}

	@Override
	public void finish(Tag endtag)
	{
		String type = endtag.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("for"))
			throw new BlockException("<?for?> ended by <?end " + type + "?>");
		super.finish(endtag);
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

	@Override
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
				break; // breaking the evaluated for loop
			}
			catch (ContinueException ex)
			{
				// doing nothing here does exactly what we need ;)
			}
		}
		return null;
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(varname);
		encoder.dump(container);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		varname = decoder.load();
		container = (CodeAST)decoder.load();
	}
}
