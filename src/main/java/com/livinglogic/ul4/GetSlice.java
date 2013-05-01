/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import static com.livinglogic.utils.SetUtils.makeSet;
import static com.livinglogic.utils.SetUtils.union;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class GetSlice extends AST
{
	protected AST obj;
	protected AST index1;
	protected AST index2;

	public GetSlice(Location location, int start, int end, AST obj, AST index1, AST index2)
	{
		super(location, start, end);
		this.obj = obj;
		this.index1 = index1;
		this.index2 = index2;
	}

	public String getType()
	{
		return "getslice";
	}

	public static AST make(Location location, int start, int end, AST obj, AST index1, AST index2)
	{
		if (obj instanceof Const)
		{
			if (index1 == null)
			{
				if (index2 == null)
				{
					Object result = call(((Const)obj).value, null, null);
					if (!(result instanceof Undefined))
						return new Const(location, start, end, result);
				}
				else if (index2 instanceof Const)
				{
					Object result = call(((Const)obj).value, null, ((Const)index2).value);
					if (!(result instanceof Undefined))
						return new Const(location, start, end, result);
				}
			}
			else if (index1 instanceof Const)
			{
				if (index2 == null)
				{
					Object result = call(((Const)obj).value, ((Const)index1).value, null);
					if (!(result instanceof Undefined))
						return new Const(location, start, end, result);
				}
				else if (index2 instanceof Const)
				{
					Object result = call(((Const)obj).value, ((Const)index1).value, ((Const)index2).value);
					if (!(result instanceof Undefined))
						return new Const(location, start, end, result);
				}
			}
		}
		return new GetSlice(location, start, end, obj, index1, index2);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj.decoratedEvaluate(context), index1 != null ? index1.decoratedEvaluate(context) : null, index2 != null ? index2.decoratedEvaluate(context) : null);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
		encoder.dump(index1);
		encoder.dump(index2);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (AST)decoder.load();
		index1 = (AST)decoder.load();
		index2 = (AST)decoder.load();
	}

	public static Object call(List obj, int startIndex, int endIndex)
	{
		int size = obj.size();
		int start = Utils.getSliceStartPos(size, startIndex);
		int end = Utils.getSliceEndPos(size, endIndex);
		if (end < start)
			end = start;
		return obj.subList(start, end);
	}

	public static Object call(String obj, int startIndex, int endIndex)
	{
		int size = obj.length();
		int start = Utils.getSliceStartPos(size, startIndex);
		int end = Utils.getSliceEndPos(size, endIndex);
		if (end < start)
			end = start;
		return StringUtils.substring(obj, start, end);
	}

	public static Object call(Object obj, Object startIndex, Object endIndex)
	{
		if (obj instanceof List)
		{
			int start = startIndex != null ? Utils.toInt(startIndex) : 0;
			int end = endIndex != null ? Utils.toInt(endIndex) : ((List)obj).size();
			return call((List)obj, start, end);
		}
		else if (obj instanceof String)
		{
			int start = startIndex != null ? Utils.toInt(startIndex) : 0;
			int end = endIndex != null ? Utils.toInt(endIndex) : ((String)obj).length();
			return call((String)obj, start, end);
		}
		throw new ArgumentTypeMismatchException("{}[{}:{}]", obj, startIndex, endIndex);
	}

	protected static Set<String> attributes = union(AST.attributes, makeSet("obj", "index1", "index2"));

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("obj".equals(key))
			return obj;
		else if ("index1".equals(key))
			return index1;
		else if ("index2".equals(key))
			return index2;
		else
			return super.getItemStringUL4(key);
	}
}
