/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class ItemAST extends BinaryAST implements LValue
{
	public ItemAST(Location location, int start, int end, AST obj1, AST obj2)
	{
		super(location, start, end, obj1, obj2);
	}

	public String getType()
	{
		return "item";
	}

	public static AST make(Location location, int start, int end, AST obj1, AST obj2)
	{
		if (obj1 instanceof ConstAST && obj2 instanceof ConstAST)
		{
			Object result = call(((ConstAST)obj1).value, ((ConstAST)obj2).value);
			if (!(result instanceof Undefined))
				return new ConstAST(location, start, end, result);
		}
		return new ItemAST(location, start, end, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public void evaluateSet(EvaluationContext context, Object value)
	{
		callSet(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateAdd(EvaluationContext context, Object value)
	{
		callAdd(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateSub(EvaluationContext context, Object value)
	{
		callSub(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateMul(EvaluationContext context, Object value)
	{
		callMul(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateFloorDiv(EvaluationContext context, Object value)
	{
		callFloorDiv(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateTrueDiv(EvaluationContext context, Object value)
	{
		callTrueDiv(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public void evaluateMod(EvaluationContext context, Object value)
	{
		callMod(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context), value);
	}

	public static Object call(String obj, int index)
	{
		if (0 > index)
			index += obj.length();
		if (index < 0 || index >= obj.length())
			return new UndefinedIndex(index);
		return obj.substring(index, index+1);
	}

	public static Object call(List obj, int index)
	{
		if (0 > index)
			index += obj.size();
		if (index < 0 || index >= obj.size())
			return new UndefinedIndex(index);
		return obj.get(index);
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
		else if (obj instanceof Map)
			return call((Map)obj, index);
		else if (index instanceof String)
			return call(obj, (String)index);
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

	public static void callSet(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, value);
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
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callSet((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{}[{}] = {}", obj, index, value);
	}

	public static void callAdd(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, AddAST.call(obj.get(index), value));
	}

	public static void callAdd(UL4GetSetItem obj, Object key, Object value)
	{
		obj.setItemUL4(key, AddAST.call(obj.getItemUL4(key), value));
	}

	public static void callAdd(UL4GetSetItemString obj, String key, Object value)
	{
		obj.setItemStringUL4(key, AddAST.call(obj.getItemStringUL4(key), value));
	}

	public static void callAdd(Map obj, Object index, Object value)
	{
		obj.put(index, AddAST.call(call(obj, index), value));
	}

	public static void callAdd(Object obj, Object index, Object value)
	{
		if (obj instanceof UL4GetSetItem)
			callAdd((UL4GetSetItem)obj, index, value);
		if (obj instanceof UL4GetSetItemString && index instanceof String)
			callAdd((UL4GetSetItemString)obj, (String)index, value);
		else if (obj instanceof Map)
			callAdd((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callAdd((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{}[{}] += {}", obj, index, value);
	}

	public static void callSub(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, SubAST.call(obj.get(index), value));
	}

	public static void callSub(UL4GetSetItem obj, Object key, Object value)
	{
		obj.setItemUL4(key, SubAST.call(obj.getItemUL4(key), value));
	}

	public static void callSub(UL4GetSetItemString obj, String key, Object value)
	{
		obj.setItemStringUL4(key, SubAST.call(obj.getItemStringUL4(key), value));
	}

	public static void callSub(Map obj, Object index, Object value)
	{
		obj.put(index, SubAST.call(call(obj, index), value));
	}

	public static void callSub(Object obj, Object index, Object value)
	{
		if (obj instanceof UL4GetSetItem)
			callSub((UL4GetSetItem)obj, index, value);
		if (obj instanceof UL4GetSetItemString && index instanceof String)
			callSub((UL4GetSetItemString)obj, (String)index, value);
		else if (obj instanceof Map)
			callSub((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callSub((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{}[{}] -= {}", obj, index, value);
	}

	public static void callMul(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, IMul.call(obj.get(index), value));
	}

	public static void callMul(UL4GetSetItem obj, Object key, Object value)
	{
		obj.setItemUL4(key, IMul.call(obj.getItemUL4(key), value));
	}

	public static void callMul(UL4GetSetItemString obj, String key, Object value)
	{
		obj.setItemStringUL4(key, IMul.call(obj.getItemStringUL4(key), value));
	}

	public static void callMul(Map obj, Object index, Object value)
	{
		obj.put(index, IMul.call(call(obj, index), value));
	}

	public static void callMul(Object obj, Object index, Object value)
	{
		if (obj instanceof UL4GetSetItem)
			callMul((UL4GetSetItem)obj, index, value);
		if (obj instanceof UL4GetSetItemString && index instanceof String)
			callMul((UL4GetSetItemString)obj, (String)index, value);
		else if (obj instanceof Map)
			callMul((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callMul((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{}[{}] *= {}", obj, index, value);
	}

	public static void callFloorDiv(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, FloorDivAST.call(obj.get(index), value));
	}

	public static void callFloorDiv(UL4GetSetItem obj, Object key, Object value)
	{
		obj.setItemUL4(key, FloorDivAST.call(obj.getItemUL4(key), value));
	}

	public static void callFloorDiv(UL4GetSetItemString obj, String key, Object value)
	{
		obj.setItemStringUL4(key, FloorDivAST.call(obj.getItemStringUL4(key), value));
	}

	public static void callFloorDiv(Map obj, Object index, Object value)
	{
		obj.put(index, FloorDivAST.call(call(obj, index), value));
	}

	public static void callFloorDiv(Object obj, Object index, Object value)
	{
		if (obj instanceof UL4GetSetItem)
			callFloorDiv((UL4GetSetItem)obj, index, value);
		if (obj instanceof UL4GetSetItemString && index instanceof String)
			callFloorDiv((UL4GetSetItemString)obj, (String)index, value);
		else if (obj instanceof Map)
			callFloorDiv((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callFloorDiv((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{}[{}] //= {}", obj, index, value);
	}

	public static void callTrueDiv(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, TrueDivAST.call(obj.get(index), value));
	}

	public static void callTrueDiv(UL4GetSetItem obj, Object key, Object value)
	{
		obj.setItemUL4(key, TrueDivAST.call(obj.getItemUL4(key), value));
	}

	public static void callTrueDiv(UL4GetSetItemString obj, String key, Object value)
	{
		obj.setItemStringUL4(key, TrueDivAST.call(obj.getItemStringUL4(key), value));
	}

	public static void callTrueDiv(Map obj, Object index, Object value)
	{
		obj.put(index, TrueDivAST.call(call(obj, index), value));
	}

	public static void callTrueDiv(Object obj, Object index, Object value)
	{
		if (obj instanceof UL4GetSetItem)
			callTrueDiv((UL4GetSetItem)obj, index, value);
		if (obj instanceof UL4GetSetItemString && index instanceof String)
			callTrueDiv((UL4GetSetItemString)obj, (String)index, value);
		else if (obj instanceof Map)
			callTrueDiv((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callTrueDiv((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{}[{}] /= {}", obj, index, value);
	}

	public static void callMod(List obj, int index, Object value)
	{
		if (0 > index)
			index += obj.size();
		obj.set(index, ModAST.call(obj.get(index), value));
	}

	public static void callMod(UL4GetSetItem obj, Object key, Object value)
	{
		obj.setItemUL4(key, ModAST.call(obj.getItemUL4(key), value));
	}

	public static void callMod(UL4GetSetItemString obj, String key, Object value)
	{
		obj.setItemStringUL4(key, ModAST.call(obj.getItemStringUL4(key), value));
	}

	public static void callMod(Map obj, Object index, Object value)
	{
		obj.put(index, ModAST.call(call(obj, index), value));
	}

	public static void callMod(Object obj, Object index, Object value)
	{
		if (obj instanceof UL4GetSetItem)
			callMod((UL4GetSetItem)obj, index, value);
		if (obj instanceof UL4GetSetItemString && index instanceof String)
			callMod((UL4GetSetItemString)obj, (String)index, value);
		else if (obj instanceof Map)
			callMod((Map)obj, index, value);
		else if (obj instanceof List && (index instanceof Boolean || index instanceof Number))
			callMod((List)obj, Utils.toInt(index), value);
		else
			throw new ArgumentTypeMismatchException("{}[{}] %= {}", obj, index, value);
	}
}
