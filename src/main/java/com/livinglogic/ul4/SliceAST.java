/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class SliceAST extends CodeAST
{
	protected AST index1;
	protected AST index2;

	public SliceAST(Tag tag, int start, int end, AST index1, AST index2)
	{
		super(tag, start, end);
		this.index1 = index1;
		this.index2 = index2;
	}

	public String getType()
	{
		return "slice";
	}

	public static AST make(Tag tag, int start, int end, AST obj, AST index1, AST index2)
	{
		if (obj instanceof ConstAST)
		{
			if (index1 == null)
			{
				if (index2 == null)
				{
					Object result = call(null, null);
					if (!(result instanceof Undefined))
						return new ConstAST(tag, start, end, result);
				}
				else if (index2 instanceof ConstAST)
				{
					Object result = call(null, ((ConstAST)index2).value);
					if (!(result instanceof Undefined))
						return new ConstAST(tag, start, end, result);
				}
			}
			else if (index1 instanceof ConstAST)
			{
				if (index2 == null)
				{
					Object result = call(((ConstAST)index1).value, null);
					if (!(result instanceof Undefined))
						return new ConstAST(tag, start, end, result);
				}
				else if (index2 instanceof ConstAST)
				{
					Object result = call(((ConstAST)index1).value, ((ConstAST)index2).value);
					if (!(result instanceof Undefined))
						return new ConstAST(tag, start, end, result);
				}
			}
		}
		return new SliceAST(tag, start, end, index1, index2);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(index1 != null ? index1.decoratedEvaluate(context) : null, index2 != null ? index2.decoratedEvaluate(context) : null);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(index1);
		encoder.dump(index2);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		index1 = (AST)decoder.load();
		index2 = (AST)decoder.load();
	}

	public static Object call(Object startIndex, Object endIndex)
	{
		if (startIndex == null)
		{
			if (endIndex == null)
				return new Slice(false, false, 0, 0);
			else
				return new Slice(false, true, 0, Utils.toInt(endIndex));
		}
		else
		{
			if (endIndex == null)
				return new Slice(true, false, Utils.toInt(startIndex), 0);
			else
				return new Slice(true, true, Utils.toInt(startIndex), Utils.toInt(endIndex));
		}
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "start", "stop");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("index1".equals(key))
			return index1;
		else if ("index2".equals(key))
			return index2;
		else
			return super.getItemStringUL4(key);
	}
}
