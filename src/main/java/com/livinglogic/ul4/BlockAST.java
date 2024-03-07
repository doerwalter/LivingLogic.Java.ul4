/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

abstract class BlockAST extends CodeAST implements BlockLike
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "BlockAST";
		}

		@Override
		public String getDoc()
		{
			return "Base class for all AST nodes that are blocks.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof BlockAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected List<AST> content = new LinkedList<AST>();

	public BlockAST(Template template, int startPosStart, int startPosStop, int stopPosStart, int stopPosStop)
	{
		super(template, startPosStart, startPosStop, stopPosStart, stopPosStop);
	}

	public abstract String getBlockTag();

	@Override
	public IndentAST popTrailingIndent()
	{
		if (content.size() > 0)
		{
			AST lastItem = content.get(content.size()-1);
			if (lastItem instanceof IndentAST)
			{
				content.remove(content.size()-1);
				return (IndentAST)lastItem;
			}
		}
		return null;
	}

	@Override
	public void append(AST item)
	{
		content.add(item);
	}

	public void finish(Tag endtag)
	{
		setStopPos(endtag.getStartPosStart(), endtag.getStartPosStop());
	}

	public List<AST> getContent()
	{
		return content;
	}

	/**
	Return whether this block can handle a {@code break} oder {@code continue} tag ({@code true})
	or whether the decision should be delegated to the parent block ({@code false}).
	Returns {@code true} for {@code for} and {@code while} blocks and
	{@code false} for {@code if}/{@code elif}/{@code else}.
	For {@code Template} an exception is thrown.
	**/
	abstract public boolean handleLoopControl(String name);

	@Override
	public Object evaluate(EvaluationContext context)
	{
		for (AST item : content)
			item.decoratedEvaluate(context);
		return null;
	}

	public static void blockToString(Formatter formatter, List<AST> content)
	{
		if (content.size() != 0)
		{
			for (AST item : content)
			{
				item.toString(formatter);
				formatter.lf();
			}
		}
		else
		{
			formatter.write("pass");
			formatter.lf();
		}
	}

	public void toString(Formatter formatter)
	{
		blockToString(formatter, content);
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(stopPosStart);
		encoder.dump(stopPosStop);
		encoder.dump(content);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		setStopPos((int)decoder.load(), (int)decoder.load());
		content = (List<AST>)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "stoppos", "stopline", "stopcol", "stopsource", "stopsourceprefix", "stopsourcesuffix", "content");

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
			case "stoppos":
				return getStopPos();
			case "stopline":
				return stopPosStart < 0 ? null : getStopLine();
			case "stopcol":
				return stopPosStart < 0 ? null : getStopCol();
			case "stopsource":
				return getStopSource();
			case "stopsourceprefix":
				return getStopSourcePrefix();
			case "stopsourcesuffix":
				return getStopSourceSuffix();
			case "content":
				return content;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}
