/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;

import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;

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

	private static int getSliceStartPos(int sequenceSize, int virtualPos)
	{
		int retVal = virtualPos;
		if (0 > retVal)
		{
			retVal += sequenceSize;
		}
		if (0 > retVal)
		{
			retVal = 0;
		}
		else if (sequenceSize < retVal)
		{
			retVal = sequenceSize;
		}
		return retVal;
	}

	private static int getSliceEndPos(int sequenceSize, int virtualPos)
	{
		int retVal = virtualPos;
		if (0 > retVal)
		{
			retVal += sequenceSize;
		}
		if (0 > retVal)
		{
			retVal = 0;
		}
		else if (sequenceSize < retVal)
		{
			retVal = sequenceSize;
		}
		return retVal;
	}

	public static Object call(List obj, int startIndex, int endIndex)
	{
		int size = obj.size();
		int start = getSliceStartPos(size, startIndex);
		int end = getSliceEndPos(size, endIndex);
		if (end < start)
			end = start;
		return obj.subList(start, end);
	}

	public static Object call(String obj, int startIndex, int endIndex)
	{
		int size = obj.length();
		int start = getSliceStartPos(size, startIndex);
		int end = getSliceEndPos(size, endIndex);
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
