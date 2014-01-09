/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
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

public class SliceAST extends AST implements LValue
{
	protected AST obj;
	protected AST index1;
	protected AST index2;

	public SliceAST(Location location, int start, int end, AST obj, AST index1, AST index2)
	{
		super(location, start, end);
		this.obj = obj;
		this.index1 = index1;
		this.index2 = index2;
	}

	public String getType()
	{
		return "slice";
	}

	public static AST make(Location location, int start, int end, AST obj, AST index1, AST index2)
	{
		if (obj instanceof ConstAST)
		{
			if (index1 == null)
			{
				if (index2 == null)
				{
					Object result = call(((ConstAST)obj).value, null, null);
					if (!(result instanceof Undefined))
						return new ConstAST(location, start, end, result);
				}
				else if (index2 instanceof ConstAST)
				{
					Object result = call(((ConstAST)obj).value, null, ((ConstAST)index2).value);
					if (!(result instanceof Undefined))
						return new ConstAST(location, start, end, result);
				}
			}
			else if (index1 instanceof ConstAST)
			{
				if (index2 == null)
				{
					Object result = call(((ConstAST)obj).value, ((ConstAST)index1).value, null);
					if (!(result instanceof Undefined))
						return new ConstAST(location, start, end, result);
				}
				else if (index2 instanceof ConstAST)
				{
					Object result = call(((ConstAST)obj).value, ((ConstAST)index1).value, ((ConstAST)index2).value);
					if (!(result instanceof Undefined))
						return new ConstAST(location, start, end, result);
				}
			}
		}
		return new SliceAST(location, start, end, obj, index1, index2);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj.decoratedEvaluate(context), index1 != null ? index1.decoratedEvaluate(context) : null, index2 != null ? index2.decoratedEvaluate(context) : null);
	}

	public void evaluateSet(EvaluationContext context, Object value)
	{
		callSet(obj.decoratedEvaluate(context), index1 != null ? index1.decoratedEvaluate(context) : null, index2 != null ? index2.decoratedEvaluate(context) : null, value);
	}

	public void evaluateAdd(EvaluationContext context, Object value)
	{
		throw new UnsupportedOperationException("augmented slice assignment not supported");
	}

	public void evaluateSub(EvaluationContext context, Object value)
	{
		throw new UnsupportedOperationException("augmented slice assignment not supported");
	}

	public void evaluateMul(EvaluationContext context, Object value)
	{
		throw new UnsupportedOperationException("augmented slice assignment not supported");
	}

	public void evaluateFloorDiv(EvaluationContext context, Object value)
	{
		throw new UnsupportedOperationException("augmented slice assignment not supported");
	}

	public void evaluateTrueDiv(EvaluationContext context, Object value)
	{
		throw new UnsupportedOperationException("augmented slice assignment not supported");
	}

	public void evaluateMod(EvaluationContext context, Object value)
	{
		throw new UnsupportedOperationException("augmented slice assignment not supported");
	}

	public void evaluateShiftLeft(EvaluationContext context, Object value)
	{
		throw new UnsupportedOperationException("augmented slice left shift not supported");
	}

	public void evaluateShiftRight(EvaluationContext context, Object value)
	{
		throw new UnsupportedOperationException("augmented slice right shift not supported");
	}

	public void evaluateBitAnd(EvaluationContext context, Object value)
	{
		throw new UnsupportedOperationException("augmented slice bitwise and not supported");
	}

	public void evaluateBitXOr(EvaluationContext context, Object value)
	{
		throw new UnsupportedOperationException("augmented slice bitwise exclusive or not supported");
	}

	public void evaluateBitOr(EvaluationContext context, Object value)
	{
		throw new UnsupportedOperationException("augmented slice bitwise or not supported");
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
		startIndex = Utils.getSliceStartPos(size, startIndex);
		endIndex = Utils.getSliceEndPos(size, endIndex);
		if (endIndex < startIndex)
			endIndex = startIndex;
		return obj.subList(startIndex, endIndex);
	}

	public static Object call(String obj, int startIndex, int endIndex)
	{
		int size = obj.length();
		startIndex = Utils.getSliceStartPos(size, startIndex);
		endIndex = Utils.getSliceEndPos(size, endIndex);
		if (endIndex < startIndex)
			endIndex = startIndex;
		return StringUtils.substring(obj, startIndex, endIndex);
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

	public static void callSet(List obj, int startIndex, int endIndex, Iterator iterator)
	{
		int size = obj.size();
		startIndex = Utils.getSliceStartPos(size, startIndex);
		endIndex = Utils.getSliceEndPos(size, endIndex);
		if (endIndex < startIndex)
			endIndex = startIndex;
		while (startIndex < endIndex--)
			obj.remove(startIndex);
		while (iterator.hasNext())
			obj.add(startIndex++, iterator.next());
	}

	public static void callSet(Object obj, Object startIndex, Object endIndex, Object value)
	{
		if (obj instanceof List)
		{
			int start = startIndex != null ? Utils.toInt(startIndex) : 0;
			int end = endIndex != null ? Utils.toInt(endIndex) : ((List)obj).size();
			callSet((List)obj, start, end, Utils.iterator(value));
		}
		else
			throw new ArgumentTypeMismatchException("{}[{}:{}] = {}", obj, startIndex, endIndex, value);
	}

	protected static Set<String> attributes = makeExtendedSet(AST.attributes, "obj", "index1", "index2");

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
