/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
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

public class RenderBlockAST extends RenderAST implements BlockLike
{
	protected InterpretedTemplate content;
	protected Tag endtag = null;

	public RenderBlockAST(Tag tag, Slice pos, AST obj)
	{
		super(tag, pos, obj);
		this.content = null;
	}

	/**
	 * This is used to "convert" a {@link CallAST} that comes out of the parser into a {@code RenderBlockAST}
	 */
	public RenderBlockAST(Tag tag, CallAST call, InterpretedTemplate.Whitespace whitespace, String startdelim, String enddelim)
	{
		super(call);
		this.content = new InterpretedTemplate(tag, "content", whitespace, startdelim, enddelim, null);
	}

	@Override
	public String getType()
	{
		return "renderblock";
	}

	public void toString(Formatter formatter)
	{
		formatter.write(getType());
		formatter.write(" ");
		super.toString(formatter);
		if (indent != null)
		{
			formatter.write(" with indent ");
			formatter.write(FunctionRepr.call(indent.getCodeText()));
		}
		formatter.indent();
		BlockAST.blockToString(formatter, content.getContent());
		formatter.dedent();
	}

	@Override
	public IndentAST popTrailingIndent()
	{
		if (content != null)
			return content.popTrailingIndent();
		else
			return null;
	}
	@Override
	public void append(AST item)
	{
		content.append(item);
	}

	@Override
	public void finish(Tag endtag)
	{
		String type = endtag.getCodeText().trim();
		if (type != null && type.length() != 0 && !type.equals("renderblock"))
			throw new BlockException("renderblock ended by end" + type);
		content.endtag = endtag;
		this.endtag = endtag;
	}

	@Override
	public boolean handleLoopControl(String name)
	{
		throw new BlockException(name + " outside of for/while loop");
	}

	@Override
	public void call(EvaluationContext context, UL4RenderWithContext obj, List<Object> args, Map<String, Object> kwargs)
	{
		if (kwargs.containsKey("content"))
			throw new DuplicateArgumentException(content, "content");
		kwargs.put("content", new TemplateClosure(content, context));
		super.call(context, obj, args, kwargs);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(content);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		content = (InterpretedTemplate)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(RenderAST.attributes, "content", "endtag");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "content":
				return content;
			case "endtag":
				return endtag;
			default:
				return super.getAttrUL4(key);
		}
	}
}
