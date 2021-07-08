/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

public class ItemAST extends BinaryAST implements LValue
{
	protected static class Type extends BinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ItemAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.item";
		}

		@Override
		public String getDoc()
		{
			return "AST node for subscripting expression (e.g. ``x[y]``).";
		}

		@Override
		public ItemAST create(String id)
		{
			return new ItemAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ItemAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ItemAST(Template template, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		super(template, pos, obj1, obj2);
	}

	public String getType()
	{
		return "item";
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public void evaluateSet(EvaluationContext context, Object value)
	{
		callSet(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateAdd(EvaluationContext context, Object value)
	{
		callAdd(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateSub(EvaluationContext context, Object value)
	{
		callSub(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateMul(EvaluationContext context, Object value)
	{
		callMul(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateFloorDiv(EvaluationContext context, Object value)
	{
		callFloorDiv(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateTrueDiv(EvaluationContext context, Object value)
	{
		callTrueDiv(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateMod(EvaluationContext context, Object value)
	{
		callMod(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateShiftLeft(EvaluationContext context, Object value)
	{
		callShiftLeft(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateShiftRight(EvaluationContext context, Object value)
	{
		callShiftRight(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateBitAnd(EvaluationContext context, Object value)
	{
		callBitAnd(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateBitXOr(EvaluationContext context, Object value)
	{
		callBitXOr(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateBitOr(EvaluationContext context, Object value)
	{
		callBitOr(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public static Object call(EvaluationContext context, String obj, int index)
	{
		if (0 > index)
			index += obj.length();
		return obj.substring(index, index+1);
	}

	public static Object call(EvaluationContext context, String obj, Slice slice)
	{
		int size = obj.length();
		int startIndex = slice.getStartIndex(size);
		int stopIndex = slice.getStopIndex(size);
		if (stopIndex < startIndex)
			stopIndex = startIndex;
		return obj.substring(startIndex, stopIndex);
	}

	public static Object call(EvaluationContext context, List obj, int index)
	{
		if (0 > index)
			index += obj.size();
		return obj.get(index);
	}

	public static Object call(EvaluationContext context, List obj, Slice slice)
	{
		int size = obj.size();
		int startIndex = slice.getStartIndex(size);
		int endIndex = slice.getStopIndex(size);
		if (endIndex < startIndex)
			endIndex = startIndex;
		return new ArrayList(obj.subList(startIndex, endIndex));
	}

	public static Object call(EvaluationContext context, UL4GetItem obj, Object key)
	{
		try
		{
			return obj.getItemUL4(context, key);
		}
		catch (AttributeException exc)
		{
			if (exc.getObject() == obj)
				return new UndefinedKey(key);
			else
				// The {@code AttributeException} originated from another object
				throw exc;
		}
	}

	public static Object call(EvaluationContext context, Map obj, Object index)
	{
		Object result = obj.get(index);

		if ((result == null) && !obj.containsKey(index))
			return new UndefinedKey(index);
		return result;
	}

	public static Object call(EvaluationContext context, Object obj, String key)
	{
		if (obj instanceof UL4GetItem)
			return call(context, (UL4GetItem)obj, (Object)key);
		else if (obj instanceof Map)
			return call(context, (Map)obj, (Object)key);
		throw new ArgumentTypeMismatchException("{!t}[{!t}] not supported", obj, key);
	}

	public static Object call(EvaluationContext context, Object obj, Object index)
	{
		if (obj instanceof UL4GetItem)
			return call(context, (UL4GetItem)obj, index);
		else if (obj instanceof Map)
			return call(context, (Map)obj, index);
		else if (index instanceof Slice)
		{
			if (obj instanceof String)
				return call(context, (String)obj, (Slice)index);
			else if (obj instanceof List)
				return call(context, (List)obj, (Slice)index);
		}
		else if (index instanceof Boolean || index instanceof Number)
		{
			if (obj instanceof String)
				return call(context, (String)obj, Utils.toInt(index));
			else if (obj instanceof List)
				return call(context, (List)obj, Utils.toInt(index));
		}
		throw new ArgumentTypeMismatchException("{!t}[{!t}] not supported", obj, index);
	}

	public static void callSet(EvaluationContext context, List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, value);
	}

	public static void callSet(EvaluationContext context, List obj, Slice slice, Object value)
	{
		int size = obj.size();
		int startIndex = slice.getStartIndex(size);
		int stopIndex = slice.getStopIndex(size);
		Iterator iterator = Utils.iterator(value);
		if (stopIndex < startIndex)
			stopIndex = startIndex;
		while (startIndex < stopIndex--)
			obj.remove(startIndex);
		while (iterator.hasNext())
			obj.add(startIndex++, iterator.next());
	}

	public static void callSet(EvaluationContext context, UL4SetItem obj, Object key, Object value)
	{
		obj.setItemUL4(context, key, value);
	}

	public static void callSet(EvaluationContext context, Map obj, Object index, Object value)
	{
		obj.put(index, value);
	}

	public static void callSet(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof UL4SetItem)
			callSet(context, (UL4SetItem)obj, index, value);
		else if (obj instanceof Map)
			callSet(context, (Map)obj, index, value);
		else if (obj instanceof List)
			if (index instanceof Slice)
				callSet(context, (List)obj, (Slice)index, value);
			else if (index instanceof Boolean || index instanceof Number)
				callSet(context, (List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] = {!t} not supported", obj, index, value);
	}

	private static Object getValue(EvaluationContext context, Object obj, Object key, String excmessage, Object value)
	{
		if (obj instanceof UL4GetItem)
			return ((UL4GetItem)obj).getItemUL4(context, key);
		else
			throw new ArgumentTypeMismatchException(excmessage, obj, key, value);
	}

	public static void callAdd(EvaluationContext context, List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, AddAST.call(context, obj.get(index), value));
	}

	public static void callAdd(EvaluationContext context, Map obj, Object index, Object value)
	{
		obj.put(index, AddAST.call(context, call(context, obj, index), value));
	}

	public static void callAdd(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] += {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(context, index, IAdd.call(context, orgvalue, value));
		}
		if (obj instanceof Map)
			callAdd(context, (Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callAdd(context, (List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] += {!t} not supported", obj, index, value);
	}

	public static void callSub(EvaluationContext context, List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, SubAST.call(context, obj.get(index), value));
	}

	public static void callSub(EvaluationContext context, Map obj, Object index, Object value)
	{
		obj.put(index, SubAST.call(context, call(context, obj, index), value));
	}

	public static void callSub(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] -= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(context, index, SubAST.call(context, orgvalue, value));
		}
		else if (obj instanceof Map)
			callSub(context, (Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callSub(context, (List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] -= {!t} not supported", obj, index, value);
	}

	public static void callMul(EvaluationContext context, List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, IMul.call(context, obj.get(index), value));
	}

	public static void callMul(EvaluationContext context, Map obj, Object index, Object value)
	{
		obj.put(index, IMul.call(context, call(context, obj, index), value));
	}

	public static void callMul(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] *= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(context, index, IMul.call(context, orgvalue, value));
		}
		else if (obj instanceof Map)
			callMul(context, (Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callMul(context, (List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] *= {!t} not supported", obj, index, value);
	}

	public static void callFloorDiv(EvaluationContext context, List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, FloorDivAST.call(context, obj.get(index), value));
	}

	public static void callFloorDiv(EvaluationContext context, Map obj, Object index, Object value)
	{
		obj.put(index, FloorDivAST.call(context, call(context, obj, index), value));
	}

	public static void callFloorDiv(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] //= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(context, index, FloorDivAST.call(context, orgvalue, value));
		}
		else if (obj instanceof Map)
			callFloorDiv(context, (Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callFloorDiv(context, (List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] //= {!t}", obj, index, value);
	}

	public static void callTrueDiv(EvaluationContext context, List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, TrueDivAST.call(context, obj.get(index), value));
	}

	public static void callTrueDiv(EvaluationContext context, Map obj, Object index, Object value)
	{
		obj.put(index, TrueDivAST.call(context, call(context, obj, index), value));
	}

	public static void callTrueDiv(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] /= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(context, index, TrueDivAST.call(context, orgvalue, value));
		}
		else if (obj instanceof Map)
			callTrueDiv(context, (Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callTrueDiv(context, (List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] /= {!t} not supported", obj, index, value);
	}

	public static void callMod(EvaluationContext context, List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, ModAST.call(context, obj.get(index), value));
	}

	public static void callMod(EvaluationContext context, Map obj, Object index, Object value)
	{
		obj.put(index, ModAST.call(context, call(context, obj, index), value));
	}

	public static void callMod(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] %= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(context, index, ModAST.call(context, orgvalue, value));
		}
		else if (obj instanceof Map)
			callMod(context, (Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callMod(context, (List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] %= {!t} not supported", obj, index, value);
	}

	public static void callShiftLeft(EvaluationContext context, List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, ShiftLeftAST.call(context, obj.get(index), value));
	}

	public static void callShiftLeft(EvaluationContext context, Map obj, Object index, Object value)
	{
		obj.put(index, ShiftLeftAST.call(context, call(context, obj, index), value));
	}

	public static void callShiftLeft(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] <<= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(context, index, ShiftLeftAST.call(context, orgvalue, value));
		}
		else if (obj instanceof Map)
			callShiftLeft(context, (Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callShiftLeft(context, (List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] <<= {!t} not supported", obj, index, value);
	}

	public static void callShiftRight(EvaluationContext context, List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, ShiftRightAST.call(context, obj.get(index), value));
	}

	public static void callShiftRight(EvaluationContext context, Map obj, Object index, Object value)
	{
		obj.put(index, ShiftRightAST.call(context, call(context, obj, index), value));
	}

	public static void callShiftRight(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] >>= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(context, index, ShiftRightAST.call(context, orgvalue, value));
		}
		else if (obj instanceof Map)
			callShiftRight(context, (Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callShiftRight(context, (List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] >>= {!t} not supported", obj, index, value);
	}

	public static void callBitAnd(EvaluationContext context, List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, BitAndAST.call(context, obj.get(index), value));
	}

	public static void callBitAnd(EvaluationContext context, Map obj, Object index, Object value)
	{
		obj.put(index, BitAndAST.call(context, call(context, obj, index), value));
	}

	public static void callBitAnd(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] &= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(context, index, BitAndAST.call(context, orgvalue, value));
		}
		else if (obj instanceof Map)
			callBitAnd(context, (Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callBitAnd(context, (List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] &= {!t} not supported", obj, index, value);
	}

	public static void callBitXOr(EvaluationContext context, List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, BitXOrAST.call(context, obj.get(index), value));
	}

	public static void callBitXOr(EvaluationContext context, Map obj, Object index, Object value)
	{
		obj.put(index, BitXOrAST.call(context, call(context, obj, index), value));
	}

	public static void callBitXOr(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] ^= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(context, index, BitXOrAST.call(context, orgvalue, value));
		}
		else if (obj instanceof Map)
			callBitXOr(context, (Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callBitXOr(context, (List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] ^= {!t} not supported", obj, index, value);
	}

	public static void callBitOr(EvaluationContext context, List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, BitOrAST.call(context, obj.get(index), value));
	}

	public static void callBitOr(EvaluationContext context, Map obj, Object index, Object value)
	{
		obj.put(index, BitOrAST.call(context, call(context, obj, index), value));
	}

	public static void callBitOr(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] |= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(context, index, BitOrAST.call(context, orgvalue, value));
		}
		else if (obj instanceof Map)
			callBitOr(context, (Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callBitOr(context, (List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] |= {!t} not supported", obj, index, value);
	}
}
