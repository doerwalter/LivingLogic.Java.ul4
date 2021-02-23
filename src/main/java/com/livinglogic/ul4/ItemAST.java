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
		public String getModuleName()
		{
			return "ul4";
		}

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
			return "An item access expression (i.e. `x[y]`).";
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

	public static UL4Type type = new Type();

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

	public static CodeAST make(Template template, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		if (obj1 instanceof ConstAST && obj2 instanceof ConstAST)
		{
			try
			{
				Object result = call(((ConstAST)obj1).value, ((ConstAST)obj2).value);
				if (!(result instanceof Undefined))
					return new ConstAST(template, pos, result);
			}
			catch (Exception ex)
			{
				// fall through to create a real {@code ItemAST} object
			}
		}
		return new ItemAST(template, pos, obj1, obj2);
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
		try
		{
			return obj.getItemUL4(key);
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
		else if (obj instanceof Map)
			return call((Map)obj, (Object)key);
		throw new ArgumentTypeMismatchException("{!t}[{!t}] not supported", obj, key);
	}

	public static Object call(Object obj, Object index)
	{
		if (obj instanceof UL4GetItem)
			return call((UL4GetItem)obj, index);
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
		}
		throw new ArgumentTypeMismatchException("{!t}[{!t}] not supported", obj, index);
	}

	public static Object call(EvaluationContext context, UL4GetItemWithContext obj, Object key)
	{
		try
		{
			return obj.getItemWithContextUL4(context, key);
		}
		catch (AttributeException exc)
		{
			return new UndefinedKey(key);
		}
	}

	public static Object call(EvaluationContext context, Object obj, Object index)
	{
		if (obj instanceof UL4GetItemWithContext)
			return call(context, (UL4GetItemWithContext)obj, index);
		else if (obj instanceof UL4GetItem)
			return call((UL4GetItem)obj, index);
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

	public static void callSet(Map obj, Object index, Object value)
	{
		obj.put(index, value);
	}

	public static void callSet(Object obj, Object index, Object value)
	{
		if (obj instanceof UL4SetItem)
			callSet((UL4SetItem)obj, index, value);
		else if (obj instanceof Map)
			callSet((Map)obj, index, value);
		else if (obj instanceof List)
			if (index instanceof Slice)
				callSet((List)obj, (Slice)index, value);
			else if (index instanceof Boolean || index instanceof Number)
				callSet((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] = {!t} not supported", obj, index, value);
	}

	private static Object getValue(EvaluationContext context, Object obj, Object key, String excmessage, Object value)
	{
		if (obj instanceof UL4GetItemWithContext)
			return ((UL4GetItemWithContext)obj).getItemWithContextUL4(context, key);
		else if (obj instanceof UL4GetItem)
			return ((UL4GetItem)obj).getItemUL4(key);
		else
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
		if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] += {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, index, IAdd.call(orgvalue, value));
		}
		if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] += {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(index, IAdd.call(orgvalue, value));
		}
		if (obj instanceof Map)
			callAdd((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callAdd((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] += {!t} not supported", obj, index, value);
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
		if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] -= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, index, SubAST.call(orgvalue, value));
		}
		if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] -= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(index, SubAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callSub((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callSub((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] -= {!t} not supported", obj, index, value);
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
		if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] *= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, index, IMul.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] *= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(index, IMul.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callMul((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callMul((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] *= {!t} not supported", obj, index, value);
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
		if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] //= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, index, FloorDivAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] //= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(index, FloorDivAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callFloorDiv((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callFloorDiv((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] //= {!t}", obj, index, value);
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
		if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] /= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, index, TrueDivAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] /= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(index, TrueDivAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callTrueDiv((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callTrueDiv((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] /= {!t} not supported", obj, index, value);
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
		if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] %= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, index, ModAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] %= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(index, ModAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callMod((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callMod((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] %= {!t} not supported", obj, index, value);
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
		if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] <<= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, index, ShiftLeftAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] <<= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(index, ShiftLeftAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callShiftLeft((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callShiftLeft((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] <<= {!t} not supported", obj, index, value);
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
		if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] >>= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, index, ShiftRightAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] >>= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(index, ShiftRightAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callShiftRight((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callShiftRight((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] >>= {!t} not supported", obj, index, value);
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
		if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] &= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, index, BitAndAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] &= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(index, BitAndAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callBitAnd((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callBitAnd((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] &= {!t} not supported", obj, index, value);
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
		if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] ^= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, index, BitXOrAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] ^= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(index, BitXOrAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callBitXOr((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callBitXOr((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] ^= {!t} not supported", obj, index, value);
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
		if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] |= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, index, BitOrAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, index, "{!t}[{!t}] |= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(index, BitOrAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callBitOr((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callBitOr((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{!t}[{!t}] |= {!t} not supported", obj, index, value);
	}
}
