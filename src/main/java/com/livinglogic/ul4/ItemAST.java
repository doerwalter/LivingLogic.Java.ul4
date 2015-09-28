/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
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
	public ItemAST(Tag tag, int startPos, int endPos, CodeAST obj1, CodeAST obj2)
	{
		super(tag, startPos, endPos, obj1, obj2);
	}

	public String getType()
	{
		return "item";
	}

	public static CodeAST make(Tag tag, int startPos, int endPos, CodeAST obj1, CodeAST obj2)
	{
		if (obj1 instanceof ConstAST && obj2 instanceof ConstAST)
		{
			Object result = call(((ConstAST)obj1).value, ((ConstAST)obj2).value);
			if (!(result instanceof Undefined))
				return new ConstAST(tag, startPos, endPos, result);
		}
		return new ItemAST(tag, startPos, endPos, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public void evaluateSet(EvaluationContext context, Object value)
	{
		callSet(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
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

	public static Object call(String obj, int index)
	{
		if (0 > index)
			index += obj.length();
		if (index < 0 || index >= obj.length())
			return new UndefinedIndex(index);
		return obj.substring(index, index+1);
	}

	public static Object call(String obj, Slice slice)
	{
		int size = obj.length();
		int startIndex = slice.getStartIndex(size);
		int stopIndex = slice.getStopIndex(size);
		if (stopIndex < startIndex)
			stopIndex = startIndex;
		return obj.substring(startIndex, stopIndex);
	}

	public static Object call(List obj, int index)
	{
		if (0 > index)
			index += obj.size();
		if (index < 0 || index >= obj.size())
			return new UndefinedIndex(index);
		return obj.get(index);
	}

	public static Object call(List obj, Slice slice)
	{
		int size = obj.size();
		int startIndex = slice.getStartIndex(size);
		int endIndex = slice.getStopIndex(size);
		if (endIndex < startIndex)
			endIndex = startIndex;
		return new ArrayList(obj.subList(startIndex, endIndex));
	}


	public static Object call(UL4GetItem obj, Object key)
	{
		return obj.getItemUL4(key);
	}

	public static Object call(UL4GetItemString obj, String key)
	{
		return obj.getItemStringUL4(key);
	}

	public static int call(Color obj, int index)
	{
		return obj.getItemIntegerUL4(index);
	}

	public static Object call(Map obj, Object index)
	{
		Object result = obj.get(index);

		if ((result == null) && !obj.containsKey(index))
			return new UndefinedKey(index);
		return result;
	}

	public static Object call(Object obj, String key)
	{
		if (obj instanceof UL4GetItem)
			return call((UL4GetItem)obj, (Object)key);
		else if (obj instanceof UL4GetItemString)
			return call((UL4GetItemString)obj, key);
		else if (obj instanceof Map)
			return call((Map)obj, (Object)key);
		throw new ArgumentTypeMismatchException("{}[{}]", obj, key);
	}

	public static Object call(Object obj, Object index)
	{
		if (obj instanceof UL4GetItem)
			return call((UL4GetItem)obj, index);
		else if (obj instanceof UL4GetItemString && index instanceof String)
			return call((UL4GetItemString)obj, (String)index);
		else if (obj instanceof Map)
			return call((Map)obj, index);
		else if (index instanceof Slice)
		{
			if (obj instanceof String)
				return call((String)obj, (Slice)index);
			else if (obj instanceof List)
				return call((List)obj, (Slice)index);
		}
		else if (index instanceof Boolean || index instanceof Number)
		{
			if (obj instanceof String)
				return call((String)obj, Utils.toInt(index));
			else if (obj instanceof List)
				return call((List)obj, Utils.toInt(index));
			else if (obj instanceof Color)
				return call((Color)obj, Utils.toInt(index));
		}
		throw new ArgumentTypeMismatchException("{}[{}]", obj, index);
	}

	public static Object call(EvaluationContext context, UL4GetItemWithContext obj, Object key)
	{
		return obj.getItemWithContextUL4(context, key);
	}

	public static Object call(EvaluationContext context, UL4GetItemStringWithContext obj, String key)
	{
		return obj.getItemStringWithContextUL4(context, key);
	}

	public static Object call(EvaluationContext context, Object obj, Object index)
	{
		if (obj instanceof UL4GetItemWithContext)
			return call(context, (UL4GetItemWithContext)obj, index);
		else if (obj instanceof UL4GetItemStringWithContext)
			return call(context, (UL4GetItemStringWithContext)obj, index);
		else
			return call(obj, index);
	}

	public static void callSet(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, value);
	}

	public static void callSet(List obj, Slice slice, Object value)
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

	public static void callSet(UL4SetItem obj, Object key, Object value)
	{
		obj.setItemUL4(key, value);
	}

	public static void callSet(UL4SetItemString obj, String key, Object value)
	{
		obj.setItemStringUL4(key, value);
	}

	public static void callSet(Map obj, Object index, Object value)
	{
		obj.put(index, value);
	}

	public static void callSet(Object obj, Object index, Object value)
	{
		if (obj instanceof UL4SetItem)
			callSet((UL4SetItem)obj, index, value);
		if (obj instanceof UL4SetItemString && index instanceof String)
			callSet((UL4SetItemString)obj, (String)index, value);
		else if (obj instanceof Map)
			callSet((Map)obj, index, value);
		else if (obj instanceof List)
			if (index instanceof Slice)
				callSet((List)obj, (Slice)index, value);
			else if (index instanceof Boolean || index instanceof Number)
				callSet((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{}[{}] = {}", obj, index, value);
	}

	private static Object getValue(EvaluationContext context, Object obj, Object key, String excmessage, Object value)
	{
		if (key instanceof String)
		{
			if (obj instanceof UL4GetItemString)
				return ((UL4GetItemString)obj).getItemStringUL4((String)key);
			else if (obj instanceof UL4GetItemStringWithContext)
				return ((UL4GetItemStringWithContext)obj).getItemStringWithContextUL4(context, (String)key);
			else if (obj instanceof UL4GetItem)
				return ((UL4GetItem)obj).getItemUL4((String)key);
			else if (obj instanceof UL4GetItemWithContext)
				return ((UL4GetItemWithContext)obj).getItemWithContextUL4(context, (String)key);
		}
		else
		{
			if (obj instanceof UL4GetItem)
				return ((UL4GetItem)obj).getItemUL4(key);
			else if (obj instanceof UL4GetItemWithContext)
				return ((UL4GetItemWithContext)obj).getItemWithContextUL4(context, key);
		}
		throw new ArgumentTypeMismatchException(excmessage, obj, key, value);
	}

	public static void callAdd(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, AddAST.call(obj.get(index), value));
	}

	public static void callAdd(Map obj, Object index, Object value)
	{
		obj.put(index, AddAST.call(call(obj, index), value));
	}

	public static void callAdd(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof Map)
			callAdd((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callAdd((List)obj, Utils.toInt(index), value);
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] += {}", value);
			((UL4SetItem)obj).setItemUL4(index, IAdd.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemString && index instanceof String)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] += {}", value);
			((UL4SetItemString)obj).setItemStringUL4((String)index, IAdd.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}[{}] += {}", obj, index, value);
	}

	public static void callSub(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, SubAST.call(obj.get(index), value));
	}

	public static void callSub(Map obj, Object index, Object value)
	{
		obj.put(index, SubAST.call(call(obj, index), value));
	}

	public static void callSub(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof Map)
			callSub((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callSub((List)obj, Utils.toInt(index), value);
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] -= {}", value);
			((UL4SetItem)obj).setItemUL4(index, SubAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemString && index instanceof String)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] -= {}", value);
			((UL4SetItemString)obj).setItemStringUL4((String)index, SubAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}[{}] -= {}", obj, index, value);
	}

	public static void callMul(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, IMul.call(obj.get(index), value));
	}

	public static void callMul(Map obj, Object index, Object value)
	{
		obj.put(index, IMul.call(call(obj, index), value));
	}

	public static void callMul(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof Map)
			callMul((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callMul((List)obj, Utils.toInt(index), value);
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] *= {}", value);
			((UL4SetItem)obj).setItemUL4(index, IMul.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemString && index instanceof String)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] *= {}", value);
			((UL4SetItemString)obj).setItemStringUL4((String)index, IMul.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}[{}] *= {}", obj, index, value);
	}

	public static void callFloorDiv(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, FloorDivAST.call(obj.get(index), value));
	}

	public static void callFloorDiv(Map obj, Object index, Object value)
	{
		obj.put(index, FloorDivAST.call(call(obj, index), value));
	}


	public static void callFloorDiv(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof Map)
			callFloorDiv((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callFloorDiv((List)obj, Utils.toInt(index), value);
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] //= {}", value);
			((UL4SetItem)obj).setItemUL4(index, FloorDivAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemString && index instanceof String)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] //= {}", value);
			((UL4SetItemString)obj).setItemStringUL4((String)index, FloorDivAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}[{}] //= {}", obj, index, value);
	}

	public static void callTrueDiv(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, TrueDivAST.call(obj.get(index), value));
	}

	public static void callTrueDiv(Map obj, Object index, Object value)
	{
		obj.put(index, TrueDivAST.call(call(obj, index), value));
	}

	public static void callTrueDiv(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof Map)
			callTrueDiv((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callTrueDiv((List)obj, Utils.toInt(index), value);
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] /= {}", value);
			((UL4SetItem)obj).setItemUL4(index, TrueDivAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemString && index instanceof String)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] /= {}", value);
			((UL4SetItemString)obj).setItemStringUL4((String)index, TrueDivAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}[{}] /= {}", obj, index, value);
	}

	public static void callMod(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, ModAST.call(obj.get(index), value));
	}

	public static void callMod(Map obj, Object index, Object value)
	{
		obj.put(index, ModAST.call(call(obj, index), value));
	}

	public static void callMod(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof Map)
			callMod((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callMod((List)obj, Utils.toInt(index), value);
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] %= {}", value);
			((UL4SetItem)obj).setItemUL4(index, ModAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemString && index instanceof String)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] %= {}", value);
			((UL4SetItemString)obj).setItemStringUL4((String)index, ModAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}[{}] %= {}", obj, index, value);
	}

	public static void callShiftLeft(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, ShiftLeftAST.call(obj.get(index), value));
	}

	public static void callShiftLeft(Map obj, Object index, Object value)
	{
		obj.put(index, ShiftLeftAST.call(call(obj, index), value));
	}

	public static void callShiftLeft(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof Map)
			callShiftLeft((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callShiftLeft((List)obj, Utils.toInt(index), value);
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] <<= {}", value);
			((UL4SetItem)obj).setItemUL4(index, ShiftLeftAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemString && index instanceof String)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] <<= {}", value);
			((UL4SetItemString)obj).setItemStringUL4((String)index, ShiftLeftAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}[{}] <<= {}", obj, index, value);
	}

	public static void callShiftRight(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, ShiftRightAST.call(obj.get(index), value));
	}

	public static void callShiftRight(Map obj, Object index, Object value)
	{
		obj.put(index, ShiftRightAST.call(call(obj, index), value));
	}

	public static void callShiftRight(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof Map)
			callShiftRight((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callShiftRight((List)obj, Utils.toInt(index), value);
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] >>= {}", value);
			((UL4SetItem)obj).setItemUL4(index, ShiftRightAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemString && index instanceof String)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] >>= {}", value);
			((UL4SetItemString)obj).setItemStringUL4((String)index, ShiftRightAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}[{}] >>= {}", obj, index, value);
	}

	public static void callBitAnd(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, BitAndAST.call(obj.get(index), value));
	}

	public static void callBitAnd(Map obj, Object index, Object value)
	{
		obj.put(index, BitAndAST.call(call(obj, index), value));
	}

	public static void callBitAnd(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof Map)
			callBitAnd((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callBitAnd((List)obj, Utils.toInt(index), value);
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] &= {}", value);
			((UL4SetItem)obj).setItemUL4(index, BitAndAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemString && index instanceof String)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] &= {}", value);
			((UL4SetItemString)obj).setItemStringUL4((String)index, BitAndAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}[{}] &= {}", obj, index, value);
	}

	public static void callBitXOr(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, BitXOrAST.call(obj.get(index), value));
	}

	public static void callBitXOr(Map obj, Object index, Object value)
	{
		obj.put(index, BitXOrAST.call(call(obj, index), value));
	}

	public static void callBitXOr(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof Map)
			callBitXOr((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callBitXOr((List)obj, Utils.toInt(index), value);
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] ^= {}", value);
			((UL4SetItem)obj).setItemUL4(index, BitXOrAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemString && index instanceof String)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] ^= {}", value);
			((UL4SetItemString)obj).setItemStringUL4((String)index, BitXOrAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}[{}] ^= {}", obj, index, value);
	}

	public static void callBitOr(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, BitOrAST.call(obj.get(index), value));
	}

	public static void callBitOr(Map obj, Object index, Object value)
	{
		obj.put(index, BitOrAST.call(call(obj, index), value));
	}

	public static void callBitOr(EvaluationContext context, Object obj, Object index, Object value)
	{
		if (obj instanceof Map)
			callBitOr((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callBitOr((List)obj, Utils.toInt(index), value);
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] |= {}", value);
			((UL4SetItem)obj).setItemUL4(index, BitOrAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemString && index instanceof String)
		{
			Object orgvalue = getValue(context, obj, index, "{}[{}] |= {}", value);
			((UL4SetItemString)obj).setItemStringUL4((String)index, BitOrAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}[{}] |= {}", obj, index, value);
	}
}
