/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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
	protected static class Type extends RenderAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "RenderBlockAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.renderblock";
		}

		@Override
		public String getDoc()
		{
			return "AST node for rendering a template via a ``<?renderblock?>`` block and\npassing the content of the block as one additional keyword argument named\n``content``.";
		}

		@Override
		public RenderBlockAST create(String id)
		{
			return new RenderBlockAST(null, (Slice)null, (AST)null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof RenderBlockAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected Template content;

	public RenderBlockAST(Template template, Slice pos, AST obj)
	{
		super(template, pos, obj);
		content = null;
	}

	/**
	This is used to "convert" a {@link CallAST} that comes out of the parser into a {@code RenderBlockAST}
	**/
	public RenderBlockAST(Template template, CallAST call, Template.Whitespace whitespace)
	{
		super(call);
		content = new Template(template, "content", whitespace, null);
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
			formatter.write(FunctionRepr.call(indent.getText()));
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
		String type = endtag.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("renderblock"))
			throw new BlockException("renderblock ended by end" + type);
		setStopPos(endtag.getStartPos());
		int start = content.getStartPos().getStart();
		content.setStartPos(start, start);
		int stop = endtag.getStartPos().getStart();
		content.setStopPos(stop, stop);
	}

	@Override
	public boolean handleLoopControl(String name)
	{
		throw new BlockException(name + " outside of for/while loop");
	}

	@Override
	public void call(EvaluationContext context, UL4Render obj, List<Object> args, Map<String, Object> kwargs)
	{
		if (kwargs.containsKey("content"))
			throw new DuplicateArgumentException(content, "content");
		kwargs.put("content", new TemplateClosure(content, context));
		super.call(context, obj, args, kwargs);
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(stopPos);
		encoder.dump(content);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		setStopPos((Slice)decoder.load());
		content = (Template)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(RenderAST.attributes, "stoppos", "stopline", "stopcol", "stopsource", "stopsourceprefix", "stopsourcesuffix", "content");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "stoppos":
				return stopPos;
			case "stopline":
				return getStopLine();
			case "stopcol":
				return getStopCol();
			case "stopsource":
				return getStopSource();
			case "stopsourceprefix":
				return getStopSourcePrefix();
			case "stopsourcesuffix":
				return getStopSourceSuffix();
			case "content":
				return content;
			default:
				return super.getAttrUL4(key);
		}
	}
}
