/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class RenderXOrPrintAST extends RenderAST
{
	protected static class Type extends RenderAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "RenderXOrPrintAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.renderx_or_print";
		}

		@Override
		public String getDoc()
		{
			return "AST node for rendering a template or printing an object\n(e.g. ``<?renderx_or_print t(x)?>``.";
		}

		@Override
		public RenderXOrPrintAST create(String id)
		{
			return new RenderXOrPrintAST(null, -1, -1, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof RenderXOrPrintAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public RenderXOrPrintAST(Template template, int posStart, int posStop, AST obj)
	{
		super(template, posStart, posStop, obj);
	}

	public RenderXOrPrintAST(CallAST call)
	{
		super(call);
	}

	public String getType()
	{
		return "renderx_or_print";
	}

	@Override
	public void call(EvaluationContext context, Object obj, List<Object> args, Map<String, Object> kwargs)
	{
		if (obj instanceof UL4Render)
		{
			context.pushEscape(XMLStringEscape.function);
			call(context, (UL4Render)obj, args, kwargs);
			context.popEscape();
		}
		else
		{
			if (indent != null)
				context.write(indent.getText());
			context.write(Str.call(context, obj));
		}
	}
}
