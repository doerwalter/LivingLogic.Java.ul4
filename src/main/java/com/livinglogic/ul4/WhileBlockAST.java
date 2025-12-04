/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
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
	protected static class Type extends BlockAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "WhileBlockAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.whileblock";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a `<?while?>` loop.";
		}

		@Override
		public WhileBlockAST create(String id)
		{
			return new WhileBlockAST(null, -1, -1, -1, -1, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof WhileBlockAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	@Override
	public String getBlockTag()
	{
		return "<?while?>";
	}

	protected AST condition;

	public WhileBlockAST(Template template, int startPosStart, int startPosStop, int stopPosStart, int stopPosStop, AST condition)
	{
		super(template, startPosStart, startPosStop, stopPosStart, stopPosStop);
		this.condition = condition;
	}

	@Override
	public String getType()
	{
		return "whileblock";
	}

	@Override
	public void finish(Tag endtag)
	{
		String type = endtag.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("while"))
			throw new BlockException("<?while?> ended by <?end " + type + "?>");
		super.finish(endtag);
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

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(condition);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		condition = (AST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(BlockAST.attributes, "condition");

	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		switch (key)
		{
			case "condition":
				return condition;
			default:
				return super.getAttrUL4(context, key);
		}
	}

	public Object evaluate(EvaluationContext context)
	{
		for (;;)
		{
			Object condition = this.condition.decoratedEvaluate(context);
			if (!Bool.call(context, condition))
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
