/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class AttrAST extends AST implements LValue
{
	protected AST obj;
	protected String attrname;

	public AttrAST(Location location, int start, int end, AST obj, String attrname)
	{
		super(location, start, end);
		this.obj = obj;
		this.attrname = attrname;
	}

	public String getType()
	{
		return "attr";
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj.decoratedEvaluate(context), attrname);
	}

	public void evaluateSet(EvaluationContext context, Object value)
	{
		callSet(obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateAdd(EvaluationContext context, Object value)
	{
		callAdd(obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateSub(EvaluationContext context, Object value)
	{
		callSub(obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateMul(EvaluationContext context, Object value)
	{
		callMul(obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateFloorDiv(EvaluationContext context, Object value)
	{
		callFloorDiv(obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateTrueDiv(EvaluationContext context, Object value)
	{
		callTrueDiv(obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateMod(EvaluationContext context, Object value)
	{
		callMod(obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateShiftLeft(EvaluationContext context, Object value)
	{
		callShiftLeft(obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateShiftRight(EvaluationContext context, Object value)
	{
		callShiftRight(obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateBitAnd(EvaluationContext context, Object value)
	{
		callBitAnd(obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateBitXOr(EvaluationContext context, Object value)
	{
		callBitXOr(obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateBitOr(EvaluationContext context, Object value)
	{
		callBitOr(obj.decoratedEvaluate(context), attrname, value);
	}

	public static Object call(UL4GetItem obj, String attrname)
	{
		return obj.getItemUL4(attrname);
	}

	public static Object call(UL4GetAttributes obj, String attrname)
	{
		if ("items".equals(attrname))
			return new BoundUL4GetAttributesMethodItems(obj);
		else if ("values".equals(attrname))
			return new BoundUL4GetAttributesMethodValues(obj);
		else if ("get".equals(attrname))
			return new BoundUL4GetItemStringMethodGet(obj);

		return obj.getItemStringUL4(attrname);
	}

	public static Object call(UL4GetItemString obj, String attrname)
	{
		if ("get".equals(attrname))
			return new BoundUL4GetItemStringMethodGet(obj);
		return obj.getItemStringUL4(attrname);
	}

	public static Object call(Map obj, String attrname)
	{
		if ("items".equals(attrname))
			return new BoundDictMethodItems(obj);
		else if ("values".equals(attrname))
			return new BoundDictMethodValues(obj);
		else if ("get".equals(attrname))
			return new BoundDictMethodGet(obj);
		else if ("update".equals(attrname))
			return new BoundDictMethodUpdate(obj);

		Object result = obj.get(attrname);

		if ((result == null) && !obj.containsKey(attrname))
			return new UndefinedKey(attrname);
		return result;
	}

	public static Object call(List obj, String attrname)
	{
		if ("append".equals(attrname))
			return new BoundListMethodAppend(obj);
		else if ("insert".equals(attrname))
			return new BoundListMethodInsert(obj);
		else if ("pop".equals(attrname))
			return new BoundListMethodPop(obj);
		else if ("find".equals(attrname))
			return new BoundListMethodFind(obj);
		else if ("rfind".equals(attrname))
			return new BoundListMethodRFind(obj);
		else
			return new UndefinedKey(attrname);
	}

	public static Object call(String obj, String attrname)
	{
		if ("split".equals(attrname))
			return new BoundStringMethodSplit(obj);
		else if ("rsplit".equals(attrname))
			return new BoundStringMethodRSplit(obj);
		else if ("strip".equals(attrname))
			return new BoundStringMethodStrip(obj);
		else if ("lstrip".equals(attrname))
			return new BoundStringMethodLStrip(obj);
		else if ("rstrip".equals(attrname))
			return new BoundStringMethodRStrip(obj);
		else if ("upper".equals(attrname))
			return new BoundStringMethodUpper(obj);
		else if ("lower".equals(attrname))
			return new BoundStringMethodLower(obj);
		else if ("capitalize".equals(attrname))
			return new BoundStringMethodCapitalize(obj);
		else if ("startswith".equals(attrname))
			return new BoundStringMethodStartsWith(obj);
		else if ("endswith".equals(attrname))
			return new BoundStringMethodEndsWith(obj);
		else if ("replace".equals(attrname))
			return new BoundStringMethodReplace(obj);
		else if ("find".equals(attrname))
			return new BoundStringMethodFind(obj);
		else if ("rfind".equals(attrname))
			return new BoundStringMethodRFind(obj);
		else if ("join".equals(attrname))
			return new BoundStringMethodJoin(obj);
		else
			return new UndefinedKey(attrname);
	}

	public static Object call(Date obj, String attrname)
	{
		if ("year".equals(attrname))
			return new BoundDateMethodYear(obj);
		else if ("month".equals(attrname))
			return new BoundDateMethodMonth(obj);
		else if ("day".equals(attrname))
			return new BoundDateMethodDay(obj);
		else if ("hour".equals(attrname))
			return new BoundDateMethodHour(obj);
		else if ("minute".equals(attrname))
			return new BoundDateMethodMinute(obj);
		else if ("second".equals(attrname))
			return new BoundDateMethodSecond(obj);
		else if ("microsecond".equals(attrname))
			return new BoundDateMethodMicrosecond(obj);
		else if ("weekday".equals(attrname))
			return new BoundDateMethodWeekday(obj);
		else if ("yearday".equals(attrname))
			return new BoundDateMethodYearday(obj);
		else if ("week".equals(attrname))
			return new BoundDateMethodWeek(obj);
		else if ("isoformat".equals(attrname))
			return new BoundDateMethodISOFormat(obj);
		else if ("mimeformat".equals(attrname))
			return new BoundDateMethodMIMEFormat(obj);
		else
			return new UndefinedKey(attrname);
	}

	public static Object call(Object obj, String attrname)
	{
		if (obj instanceof UL4GetAttributes) // test this before UL4GetItemString
			return call((UL4GetAttributes)obj, attrname);
		else if (obj instanceof UL4GetItemString)
			return call((UL4GetItemString)obj, attrname);
		else if (obj instanceof UL4GetItem)
			return call((UL4GetItem)obj, attrname);
		else if (obj instanceof Map)
			return call((Map)obj, attrname);
		else if (obj instanceof List)
			return call((List)obj, attrname);
		else if (obj instanceof String)
			return call((String)obj, attrname);
		else if (obj instanceof Date)
			return call((Date)obj, attrname);
		throw new ArgumentTypeMismatchException("{}.{}", obj, attrname);
	}

	public static void callSet(UL4SetItem obj, String attrname, Object value)
	{
		obj.setItemUL4(attrname, value);
	}

	public static void callSet(UL4SetItemString obj, String attrname, Object value)
	{
		obj.setItemStringUL4(attrname, value);
	}

	public static void callSet(Map obj, String attrname, Object value)
	{
		obj.put(attrname, value);
	}

	public static void callSet(Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4SetItemString)
			callSet((UL4SetItemString)obj, attrname, value);
		else if (obj instanceof UL4SetItem)
			callSet((UL4SetItem)obj, attrname, value);
		else if (obj instanceof Map)
			callSet((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{}.{} = {}", obj, attrname, value);
	}

	public static void callAdd(UL4GetSetItem obj, String attrname, Object value)
	{
		obj.setItemUL4(attrname, IAdd.call(obj.getItemUL4(attrname), value));
	}

	public static void callAdd(UL4GetSetItemString obj, String attrname, Object value)
	{
		obj.setItemStringUL4(attrname, IAdd.call(obj.getItemStringUL4(attrname), value));
	}

	public static void callAdd(Map obj, String attrname, Object value)
	{
		obj.put(attrname, IAdd.call(call(obj, attrname), value));
	}

	public static void callAdd(Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4GetSetItemString)
			callAdd((UL4GetSetItemString)obj, attrname, value);
		else if (obj instanceof UL4SetItem)
			callAdd((UL4SetItem)obj, attrname, value);
		else if (obj instanceof Map)
			callAdd((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{}.{} += {}", obj, attrname, value);
	}

	public static void callSub(UL4GetSetItem obj, String attrname, Object value)
	{
		obj.setItemUL4(attrname, SubAST.call(obj.getItemUL4(attrname), value));
	}

	public static void callSub(UL4GetSetItemString obj, String attrname, Object value)
	{
		obj.setItemStringUL4(attrname, SubAST.call(obj.getItemStringUL4(attrname), value));
	}

	public static void callSub(Map obj, String attrname, Object value)
	{
		obj.put(attrname, SubAST.call(call(obj, attrname), value));
	}

	public static void callSub(Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4GetSetItemString)
			callSub((UL4GetSetItemString)obj, attrname, value);
		else if (obj instanceof UL4SetItem)
			callSub((UL4SetItem)obj, attrname, value);
		else if (obj instanceof Map)
			callSub((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{}.{} -= {}", obj, attrname, value);
	}

	public static void callMul(UL4GetSetItem obj, String attrname, Object value)
	{
		obj.setItemUL4(attrname, IMul.call(obj.getItemUL4(attrname), value));
	}

	public static void callMul(UL4GetSetItemString obj, String attrname, Object value)
	{
		obj.setItemStringUL4(attrname, IMul.call(obj.getItemStringUL4(attrname), value));
	}

	public static void callMul(Map obj, String attrname, Object value)
	{
		obj.put(attrname, IMul.call(call(obj, attrname), value));
	}

	public static void callMul(Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4GetSetItemString)
			callMul((UL4GetSetItemString)obj, attrname, value);
		else if (obj instanceof UL4SetItem)
			callMul((UL4SetItem)obj, attrname, value);
		else if (obj instanceof Map)
			callMul((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{}.{} *= {}", obj, attrname, value);
	}

	public static void callFloorDiv(UL4GetSetItem obj, String attrname, Object value)
	{
		obj.setItemUL4(attrname, FloorDivAST.call(obj.getItemUL4(attrname), value));
	}

	public static void callFloorDiv(UL4GetSetItemString obj, String attrname, Object value)
	{
		obj.setItemStringUL4(attrname, FloorDivAST.call(obj.getItemStringUL4(attrname), value));
	}

	public static void callFloorDiv(Map obj, String attrname, Object value)
	{
		obj.put(attrname, FloorDivAST.call(call(obj, attrname), value));
	}

	public static void callFloorDiv(Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4GetSetItemString)
			callFloorDiv((UL4GetSetItemString)obj, attrname, value);
		else if (obj instanceof UL4SetItem)
			callFloorDiv((UL4SetItem)obj, attrname, value);
		else if (obj instanceof Map)
			callFloorDiv((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{}.{} //= {}", obj, attrname, value);
	}

	public static void callTrueDiv(UL4GetSetItem obj, String attrname, Object value)
	{
		obj.setItemUL4(attrname, TrueDivAST.call(obj.getItemUL4(attrname), value));
	}

	public static void callTrueDiv(UL4GetSetItemString obj, String attrname, Object value)
	{
		obj.setItemStringUL4(attrname, TrueDivAST.call(obj.getItemStringUL4(attrname), value));
	}

	public static void callTrueDiv(Map obj, String attrname, Object value)
	{
		obj.put(attrname, TrueDivAST.call(call(obj, attrname), value));
	}

	public static void callTrueDiv(Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4GetSetItemString)
			callTrueDiv((UL4GetSetItemString)obj, attrname, value);
		else if (obj instanceof UL4SetItem)
			callTrueDiv((UL4SetItem)obj, attrname, value);
		else if (obj instanceof Map)
			callTrueDiv((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{}.{} /= {}", obj, attrname, value);
	}

	public static void callMod(UL4GetSetItem obj, String attrname, Object value)
	{
		obj.setItemUL4(attrname, ModAST.call(obj.getItemUL4(attrname), value));
	}

	public static void callMod(UL4GetSetItemString obj, String attrname, Object value)
	{
		obj.setItemStringUL4(attrname, ModAST.call(obj.getItemStringUL4(attrname), value));
	}

	public static void callMod(Map obj, String attrname, Object value)
	{
		obj.put(attrname, ModAST.call(call(obj, attrname), value));
	}

	public static void callMod(Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4GetSetItemString)
			callMod((UL4GetSetItemString)obj, attrname, value);
		else if (obj instanceof UL4SetItem)
			callMod((UL4SetItem)obj, attrname, value);
		else if (obj instanceof Map)
			callMod((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{}.{} %= {}", obj, attrname, value);
	}

	public static void callShiftLeft(UL4GetSetItem obj, String attrname, Object value)
	{
		obj.setItemUL4(attrname, ShiftLeftAST.call(obj.getItemUL4(attrname), value));
	}

	public static void callShiftLeft(UL4GetSetItemString obj, String attrname, Object value)
	{
		obj.setItemStringUL4(attrname, ShiftLeftAST.call(obj.getItemStringUL4(attrname), value));
	}

	public static void callShiftLeft(Map obj, String attrname, Object value)
	{
		obj.put(attrname, ShiftLeftAST.call(call(obj, attrname), value));
	}

	public static void callShiftLeft(Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4GetSetItemString)
			callShiftLeft((UL4GetSetItemString)obj, attrname, value);
		else if (obj instanceof UL4SetItem)
			callShiftLeft((UL4SetItem)obj, attrname, value);
		else if (obj instanceof Map)
			callShiftLeft((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{}.{} <<= {}", obj, attrname, value);
	}

	public static void callShiftRight(UL4GetSetItem obj, String attrname, Object value)
	{
		obj.setItemUL4(attrname, ShiftRightAST.call(obj.getItemUL4(attrname), value));
	}

	public static void callShiftRight(UL4GetSetItemString obj, String attrname, Object value)
	{
		obj.setItemStringUL4(attrname, ShiftRightAST.call(obj.getItemStringUL4(attrname), value));
	}

	public static void callShiftRight(Map obj, String attrname, Object value)
	{
		obj.put(attrname, ShiftRightAST.call(call(obj, attrname), value));
	}

	public static void callShiftRight(Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4GetSetItemString)
			callShiftRight((UL4GetSetItemString)obj, attrname, value);
		else if (obj instanceof UL4SetItem)
			callShiftRight((UL4SetItem)obj, attrname, value);
		else if (obj instanceof Map)
			callShiftRight((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{}.{} >>= {}", obj, attrname, value);
	}

	public static void callBitAnd(UL4GetSetItem obj, String attrname, Object value)
	{
		obj.setItemUL4(attrname, BitAndAST.call(obj.getItemUL4(attrname), value));
	}

	public static void callBitAnd(UL4GetSetItemString obj, String attrname, Object value)
	{
		obj.setItemStringUL4(attrname, BitAndAST.call(obj.getItemStringUL4(attrname), value));
	}

	public static void callBitAnd(Map obj, String attrname, Object value)
	{
		obj.put(attrname, BitAndAST.call(call(obj, attrname), value));
	}

	public static void callBitAnd(Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4GetSetItemString)
			callBitAnd((UL4GetSetItemString)obj, attrname, value);
		else if (obj instanceof UL4SetItem)
			callBitAnd((UL4SetItem)obj, attrname, value);
		else if (obj instanceof Map)
			callBitAnd((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{}.{} &= {}", obj, attrname, value);
	}

	public static void callBitXOr(UL4GetSetItem obj, String attrname, Object value)
	{
		obj.setItemUL4(attrname, BitXOrAST.call(obj.getItemUL4(attrname), value));
	}

	public static void callBitXOr(UL4GetSetItemString obj, String attrname, Object value)
	{
		obj.setItemStringUL4(attrname, BitXOrAST.call(obj.getItemStringUL4(attrname), value));
	}

	public static void callBitXOr(Map obj, String attrname, Object value)
	{
		obj.put(attrname, BitXOrAST.call(call(obj, attrname), value));
	}

	public static void callBitXOr(Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4GetSetItemString)
			callBitXOr((UL4GetSetItemString)obj, attrname, value);
		else if (obj instanceof UL4SetItem)
			callBitXOr((UL4SetItem)obj, attrname, value);
		else if (obj instanceof Map)
			callBitXOr((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{}.{} ^= {}", obj, attrname, value);
	}

	public static void callBitOr(UL4GetSetItem obj, String attrname, Object value)
	{
		obj.setItemUL4(attrname, BitOrAST.call(obj.getItemUL4(attrname), value));
	}

	public static void callBitOr(UL4GetSetItemString obj, String attrname, Object value)
	{
		obj.setItemStringUL4(attrname, BitOrAST.call(obj.getItemStringUL4(attrname), value));
	}

	public static void callBitOr(Map obj, String attrname, Object value)
	{
		obj.put(attrname, BitOrAST.call(call(obj, attrname), value));
	}

	public static void callBitOr(Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4GetSetItemString)
			callBitOr((UL4GetSetItemString)obj, attrname, value);
		else if (obj instanceof UL4SetItem)
			callBitOr((UL4SetItem)obj, attrname, value);
		else if (obj instanceof Map)
			callBitOr((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{}.{} |= {}", obj, attrname, value);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
		encoder.dump(attrname);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (AST)decoder.load();
		attrname = (String)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(AST.attributes, "obj", "attrname");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("obj".equals(key))
			return obj;
		else if ("attrname".equals(key))
			return attrname;
		else
			return super.getItemStringUL4(key);
	}
}
