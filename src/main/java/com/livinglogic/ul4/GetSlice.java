/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class GetSlice extends AST
{
	protected AST obj;
	protected AST index1;
	protected AST index2;

	public GetSlice(Location location, AST obj, AST index1, AST index2)
	{
		super(location);
		this.obj = obj;
		this.index1 = index1;
		this.index2 = index2;
	}

	public String toString(int indent)
	{
		return "getslice(" + obj + ", " + index1 + ", " + index2 + ")";
	}

	public String getType()
	{
		return "getslice";
	}

	public static AST make(Location location, AST obj, AST index1, AST index2)
	{
		if (obj instanceof Const)
		{
			if (index1 == null)
			{
				if (index2 == null)
					return new Const(location, call(((Const)obj).value, null, null));
				else if (index2 instanceof Const)
					return new Const(location, call(((Const)obj).value, null, ((Const)index2).value));
			}
			else if (index1 instanceof Const)
			{
				if (index2 == null)
					return new Const(location, call(((Const)obj).value, ((Const)index1).value, null));
				else if (index2 instanceof Const)
					return new Const(location, call(((Const)obj).value, ((Const)index1).value, ((Const)index2).value));
			}
		}
		return new GetSlice(location, obj, index1, index2);
	}

	public Object evaluate(EvaluationContext context) throws IOException
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

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("obj", new ValueMaker(){public Object getValue(Object object){return ((GetSlice)object).obj;}});
			v.put("index1", new ValueMaker(){public Object getValue(Object object){return ((GetSlice)object).index1;}});
			v.put("index2", new ValueMaker(){public Object getValue(Object object){return ((GetSlice)object).index2;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
