/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

abstract class Block extends Tag
{
	protected List<Tag> content = new LinkedList<Tag>();
	protected Location endlocation = null;

	public Block(Location location)
	{
		super(location);
	}

	public void append(Tag item)
	{
		content.add(item);
	}

	public void finish(InterpretedTemplate template, Location endlocation)
	{
		this.endlocation = endlocation;
	}

	abstract public boolean handleLoopControl(String name);

	public Object decoratedEvaluate(EvaluationContext context) throws IOException
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
		catch (TagException ex)
		{
			if (ex.location != location && location != null)
				throw new TagException(ex, location);
			else
				throw ex;
		}
		catch (Exception ex)
		{
			throw new TagException(ex, location);
		}
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		for (Tag item : content)
			item.decoratedEvaluate(context);
		return null;
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
		content = (List<Tag>)decoder.load();
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("endlocation", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)object).endlocation;}});
			v.put("content", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)object).content;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
