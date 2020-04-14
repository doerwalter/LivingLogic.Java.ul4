/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.Map;
import java.util.LinkedHashMap;
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

	public RenderAST(InterpretedTemplate template, Slice pos, AST obj)
	{
		super(template, pos, obj);
		this.indent = null;
	}

	/**
	 * This is used to "convert" a {@link CallAST} that comes out of the parser into a {@code RenderAST}
	 */
	public RenderAST(CallAST call)
	{
		super(call.template, call.startPos, call.obj);
		this.indent = null;
		this.arguments = call.arguments;
	}

	public String getType()
	{
		return "render";
	}

	public void toString(Formatter formatter)
	{
		formatter.write(getType());
		formatter.write(" ");
		super.toString(formatter);
		if (indent != null)
		{
			formatter.write(" with indent ");
			formatter.write(FunctionRepr.call(indent.getText()));
		}
	}

	/**
	 * If we have an indentation before the render tag, we remove it from the
	 * containing block and set it as the {@code indent} attribute of the
	 * {@code RenderAST} object, because this indentation must be added to every
	 * line that the rendered template outputs.
	 *
	 * @param block the block which contains this object
	 */
	public void stealIndent(BlockLike block)
	{
		indent = block.popTrailingIndent();
	}

	@Override
	public Object decoratedEvaluate(EvaluationContext context)
	{
		// Overwrite with a version that attaches a new stackframe when the render object is a template, because we want to see the call in the exception chain.
		Object realObject = null;
		try
		{
			context.tick();
			realObject = obj.decoratedEvaluate(context);

			List<Object> realArguments = new ArrayList<Object>();

			Map<String, Object> realKeywordArguments = new LinkedHashMap<String, Object>();

			for (ArgumentASTBase argument : arguments)
				argument.decoratedEvaluateCall(context, realArguments, realKeywordArguments);

			call(context, realObject, realArguments, realKeywordArguments);
			return null;
		}
		catch (BreakException|ContinueException|ReturnException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			decorateException(ex, realObject);
			throw ex;
		}
	}

	public Object evaluate(EvaluationContext context)
	{
		// Do nothing here as the implementation is in {@code decoratedEvaluate}
		return null;
	}

	public void call(EvaluationContext context, UL4RenderWithContext obj, List<Object> args, Map<String, Object> kwargs)
	{
		if (obj == null)
			throw new NotRenderableException(obj);
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

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "indent":
				return indent;
			default:
				return super.getAttrUL4(key);
		}
	}
}
