/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


/**
AST node for an {@code <?else?>} block.
**/
public class ElseBlockAST extends ConditionalBlock
{
	protected static class Type extends ConditionalBlock.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ElseBlockAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.elseblock";
		}

		@Override
		public String getDoc()
		{
			return "AST node for an `<?else?>` block.";
		}

		@Override
		public ElseBlockAST create(String id)
		{
			return new ElseBlockAST(null, -1, -1, -1, -1);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ElseBlockAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ElseBlockAST(Template template, int startPosStart, int startPosStop, int stopPosStart, int stopPosStop)
	{
		super(template, startPosStart, startPosStop, stopPosStart, stopPosStop);
	}

	public String getType()
	{
		return "elseblock";
	}

	@Override
	public String getBlockTag()
	{
		return "<?else?>";
	}

	public boolean hasToBeExecuted(EvaluationContext context)
	{
		return true;
	}

	public void toString(Formatter formatter)
	{
		formatter.write("else:");
		formatter.lf();
		formatter.indent();
		super.toString(formatter);
		formatter.dedent();
	}
}
