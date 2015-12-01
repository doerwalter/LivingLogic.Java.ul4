/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
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

abstract class BlockAST extends CodeAST
{
	protected List<AST> content = new LinkedList<AST>();
	protected Tag endtag = null;

	public BlockAST(Tag tag, int start, int end)
	{
		super(tag, start, end);
	}

	public void append(AST item)
	{
		content.add(item);
	}

	public void finish(Tag endtag)
	{
		this.endtag = endtag;
	}

	public Tag getEndTag()
	{
		return endtag;
	}

	public List<AST> getContent()
	{
		return content;
	}

	/**
	 * Return whether this block can handle a {@code break} oder {@code continue} tag ({@code true})
	 * or whether the decision should be delegated to the parent block ({@code false}).
	 * Returns {@code true} for {@code for} and {@code while} blocks and
	 * {@code false} for {@code if}/{@code elif}/{@code else}.
	 * For {@code InterpretedTemplate} an exception is thrown.
	 */
	abstract public boolean handleLoopControl(String name);

	public Object decoratedEvaluate(EvaluationContext context)
	{
		try
		{
			return evaluate(context);
		}
		catch (BreakException ex)
		{
			throw ex;
		}
		catch (ContinueException ex)
		{
			throw ex;
		}
		catch (ReturnException ex)
		{
			throw ex;
		}
		catch (SourceException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new SourceException(ex, context.getTemplate(), this);
		}
	}

	public Object evaluate(EvaluationContext context)
	{
		for (AST item : content)
			item.decoratedEvaluate(context);
		return null;
	}

	public void toString(Formatter formatter)
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

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(endtag);
		encoder.dump(content);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		endtag = (Tag)decoder.load();
		content = (List<AST>)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "endtag", "content");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("endtag".equals(key))
			return endtag;
		else if ("content".equals(key))
			return content;
		else
			return super.getItemStringUL4(key);
	}
}
