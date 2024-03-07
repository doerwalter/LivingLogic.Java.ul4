/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class RenderOrPrintXAST extends RenderAST
{
	protected static class Type extends RenderAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "RenderOrPrintXAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.render_or_printx";
		}

		@Override
		public String getDoc()
		{
			return "AST node for rendering a template or printing an object\n(e.g. ``<?render_or_printx t(x)?>``.";
		}

		@Override
		public RenderOrPrintXAST create(String id)
		{
			return new RenderOrPrintXAST(null, -1, -1, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof RenderOrPrintXAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public RenderOrPrintXAST(Template template, int posStart, int posStop, AST obj)
	{
		super(template, posStart, posStop, obj);
	}

	public RenderOrPrintXAST(CallAST call)
	{
		super(call);
	}

	public String getType()
	{
		return "render_or_printx";
	}

	@Override
	public void call(EvaluationContext context, Object obj, List<Object> args, Map<String, Object> kwargs)
	{
		if (obj instanceof UL4Render)
		{
			call(context, (UL4Render)obj, args, kwargs);
		}
		else
		{
			if (indent != null)
				context.write(indent.getText());
			context.write(FunctionXMLEscape.call(context, obj));
		}
	}
}
