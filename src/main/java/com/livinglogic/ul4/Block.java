/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeSet;
import static com.livinglogic.utils.SetUtils.union;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

abstract class Block extends AST
{
	protected List<AST> content = new LinkedList<AST>();
	protected Location endlocation = null;

	public Block(Location location, int start, int end)
	{
		super(location, start, end);
	}

	public void append(AST item)
	{
		content.add(item);
	}

	public void finish(Location endlocation)
	{
		this.endlocation = endlocation;
	}

	public Location getEndLocation()
	{
		return endlocation;
	}

	public List<AST> getContent()
	{
		return content;
	}

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
		catch (Exception ex)
		{
			throw new ASTException(ex, this);
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
		encoder.dump(endlocation);
		encoder.dump(content);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		endlocation = (Location)decoder.load();
		content = (List<AST>)decoder.load();
	}

	protected static Set<String> attributes = union(AST.attributes, makeSet("endlocation", "content"));

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("endlocation".equals(key))
			return endlocation;
		else if ("content".equals(key))
			return content;
		else
			return super.getItemStringUL4(key);
	}
}
