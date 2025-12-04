/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;

public class RenderXOrPrintXAST extends RenderAST
{
	protected static class Type extends RenderAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "RenderXOrPrintXAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.renderx_or_printx";
		}

		@Override
		public String getDoc()
		{
			return "AST node for rendering a template or printing an object\n(e.g. `<?renderx_or_printx t(x)?>`.";
		}

		@Override
		public RenderXOrPrintXAST create(String id)
		{
			return new RenderXOrPrintXAST(null, -1, -1, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof RenderXOrPrintXAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public RenderXOrPrintXAST(Template template, int posStart, int posStop, AST obj)
	{
		super(template, posStart, posStop, obj);
	}

	public RenderXOrPrintXAST(CallAST call)
	{
		super(call);
	}

	public String getType()
	{
		return "renderx_or_printx";
	}

	@Override
	public void call(EvaluationContext context, Object obj, List<Object> args, Map<String, Object> kwargs)
	{
		if (obj instanceof UL4Render)
		{
			Writer newWriter = new XMLEscapeWriter(context.getWriter());
			Writer oldWriter = context.setWriter(newWriter);
			try
			{
				call(context, (UL4Render)obj, args, kwargs);
			}
			finally
			{
				context.setWriter(oldWriter);
			}
		}
		else
		{
			if (indent != null)
				context.write(indent.getText());
			context.write(FunctionXMLEscape.call(context, obj));
		}
	}
}
