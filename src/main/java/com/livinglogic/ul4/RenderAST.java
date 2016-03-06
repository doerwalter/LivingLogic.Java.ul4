/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Set;
import static java.util.Arrays.asList;
import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class RenderAST extends CallRenderAST
{
	protected IndentAST indent;

	public RenderAST(Tag tag, int start, int end, AST obj)
	{
		super(tag, start, end, obj);
		this.indent = null;
	}

	/**
	 * This is used to "convert" a {@code CallAST} that comes out of the parser into a {@code RenderAST}
	 */
	public RenderAST(CallAST call)
	{
		super(call.tag, call.startPos, call.endPos, call.obj);
		this.indent = null;
		this.arguments = call.arguments;
	}

	public String getType()
	{
		return "render";
	}

	public void toString(Formatter formatter)
	{
		formatter.write("render ");
		super.toString(formatter);
		if (indent != null)
		{
			formatter.write(" with indent ");
			formatter.write(FunctionRepr.call(indent.getText()));
		}
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		Object realObject = obj.decoratedEvaluate(context);

		List<Object> realArguments = new ArrayList<Object>();
		Map<String, Object> realKeywordArguments = new HashMap<String, Object>();

		for (Argument argument : arguments)
			argument.addToCallArguments(context, realObject, realArguments, realKeywordArguments);

		call(context, realObject, realArguments, realKeywordArguments);

		return null;
	}

	@Override
	public Object decoratedEvaluate(EvaluationContext context)
	{
		// Overwrite with a version that rewraps SourceException too, because we want to see the render call in the exception chain.
		try
		{
			context.tick();
			return evaluate(context);
		}
		catch (BreakException|ContinueException|ReturnException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new SourceException(ex, context.getTemplate(), this);
		}
	}

	public void call(EvaluationContext context, UL4RenderWithContext obj, List<Object> args, Map<String, Object> kwargs)
	{
		if (indent != null)
			context.pushIndent(indent.getText());
		obj.renderUL4(context, args, kwargs);
		if (indent != null)
			context.popIndent();
	}

	public void call(EvaluationContext context, Object obj, List<Object> args, Map<String, Object> kwargs)
	{
		if (obj instanceof UL4RenderWithContext)
			call(context, (UL4RenderWithContext)obj, args, kwargs);
		else
			throw new NotRenderableException(obj);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(indent);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		indent = (IndentAST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CallRenderAST.attributes, "indent");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "indent":
				return indent;
			default:
				return super.getItemStringUL4(key);
		}
	}
}
