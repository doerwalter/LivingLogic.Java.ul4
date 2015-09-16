/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
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

public class AttrAST extends CodeAST implements LValue
{
	protected AST obj;
	protected String attrname;

	public AttrAST(Tag tag, int start, int end, AST obj, String attrname)
	{
		super(tag, start, end);
		this.obj = obj;
		this.attrname = attrname;
	}

	public String getType()
	{
		return "attr";
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj.decoratedEvaluate(context), attrname);
	}

	public void evaluateSet(EvaluationContext context, Object value)
	{
		callSet(obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateAdd(EvaluationContext context, Object value)
	{
		callAdd(context, obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateSub(EvaluationContext context, Object value)
	{
		callSub(context, obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateMul(EvaluationContext context, Object value)
	{
		callMul(context, obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateFloorDiv(EvaluationContext context, Object value)
	{
		callFloorDiv(context, obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateTrueDiv(EvaluationContext context, Object value)
	{
		callTrueDiv(context, obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateMod(EvaluationContext context, Object value)
	{
		callMod(context, obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateShiftLeft(EvaluationContext context, Object value)
	{
		callShiftLeft(context, obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateShiftRight(EvaluationContext context, Object value)
	{
		callShiftRight(context, obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateBitAnd(EvaluationContext context, Object value)
	{
		callBitAnd(context, obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateBitXOr(EvaluationContext context, Object value)
	{
		callBitXOr(context, obj.decoratedEvaluate(context), attrname, value);
	}

	public void evaluateBitOr(EvaluationContext context, Object value)
	{
		callBitOr(context, obj.decoratedEvaluate(context), attrname, value);
	}

	public static Object call(UL4GetItem obj, String attrname)
	{
		return obj.getItemUL4(attrname);
	}

	public static Object call(UL4Attributes obj, String attrname)
	{
		if ("items".equals(attrname) && obj instanceof UL4GetItemString)
			return new BoundUL4GetAttributesMethodItems(obj);
		else if ("values".equals(attrname) && obj instanceof UL4GetItemString)
			return new BoundUL4GetAttributesMethodValues(obj);
		throw new ArgumentTypeMismatchException("{}.{}", obj, attrname);
	}

	public static Object call(UL4GetItemString obj, String attrname)
	{
		if ("get".equals(attrname))
			return new BoundUL4GetItemStringMethodGet(obj);
		else if ("items".equals(attrname) && obj instanceof UL4Attributes)
			return new BoundUL4GetAttributesMethodItems((UL4Attributes)obj);
		else if ("values".equals(attrname) && obj instanceof UL4Attributes)
			return new BoundUL4GetAttributesMethodValues((UL4Attributes)obj);
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
		if (obj instanceof UL4GetItemString)
			return call((UL4GetItemString)obj, attrname);
		else if (obj instanceof UL4GetItem)
			return call((UL4GetItem)obj, attrname);
		else if (obj instanceof UL4Attributes)
			return call((UL4Attributes)obj, attrname);
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

	public static Object call(EvaluationContext context, UL4GetItemWithContext obj, String attrname)
	{
		return obj.getItemWithContextUL4(context, attrname);
	}

	public static Object call(EvaluationContext context, UL4GetItemStringWithContext obj, String attrname)
	{
		return obj.getItemStringWithContextUL4(context, attrname);
	}

	public static Object call(EvaluationContext context, Object obj, String attrname)
	{
		if (obj instanceof UL4GetItemStringWithContext)
			return call(context, (UL4GetItemStringWithContext)obj, attrname);
		else if (obj instanceof UL4GetItemWithContext)
			return call(context, (UL4GetItemWithContext)obj, attrname);
		else
			return call(obj, attrname);
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

	private static Object getValue(EvaluationContext context, Object obj, String attrname, String excmessage, Object value)
	{
		if (obj instanceof UL4GetItemString)
			return ((UL4GetItemString)obj).getItemStringUL4(attrname);
		else if (obj instanceof UL4GetItemStringWithContext)
			return ((UL4GetItemStringWithContext)obj).getItemStringWithContextUL4(context, attrname);
		else if (obj instanceof UL4GetItem)
			return ((UL4GetItem)obj).getItemUL4(attrname);
		else if (obj instanceof UL4GetItemWithContext)
			return ((UL4GetItemWithContext)obj).getItemWithContextUL4(context, attrname);
		throw new ArgumentTypeMismatchException(excmessage, obj, attrname, value);
	}

	public static void callAdd(Map obj, String attrname, Object value)
	{
		obj.put(attrname, IAdd.call(call(obj, attrname), value));
	}

	public static void callAdd(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof Map)
			callAdd((Map)obj, attrname, value);
		else if (obj instanceof UL4SetItemString)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} += {}", value);
			((UL4SetItemString)obj).setItemStringUL4(attrname, IAdd.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} += {}", value);
			((UL4SetItem)obj).setItemUL4(attrname, IAdd.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}.{} += {}", obj, attrname, value);
	}

	public static void callSub(Map obj, String attrname, Object value)
	{
		obj.put(attrname, SubAST.call(call(obj, attrname), value));
	}

	public static void callSub(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof Map)
			callSub((Map)obj, attrname, value);
		else if (obj instanceof UL4SetItemString)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} -= {}", value);
			((UL4SetItemString)obj).setItemStringUL4(attrname, SubAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} -= {}", value);
			((UL4SetItem)obj).setItemUL4(attrname, SubAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}.{} -= {}", obj, attrname, value);
	}

	public static void callMul(Map obj, String attrname, Object value)
	{
		obj.put(attrname, IMul.call(call(obj, attrname), value));
	}

	public static void callMul(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof Map)
			callMul((Map)obj, attrname, value);
		else if (obj instanceof UL4SetItemString)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} *= {}", value);
			((UL4SetItemString)obj).setItemStringUL4(attrname, IMul.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} *= {}", value);
			((UL4SetItem)obj).setItemUL4(attrname, IMul.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}.{} *= {}", obj, attrname, value);
	}

	public static void callFloorDiv(Map obj, String attrname, Object value)
	{
		obj.put(attrname, FloorDivAST.call(call(obj, attrname), value));
	}

	public static void callFloorDiv(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof Map)
			callFloorDiv((Map)obj, attrname, value);
		else if (obj instanceof UL4SetItemString)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} //= {}", value);
			((UL4SetItemString)obj).setItemStringUL4(attrname, FloorDivAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} //= {}", value);
			((UL4SetItem)obj).setItemUL4(attrname, FloorDivAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}.{} //= {}", obj, attrname, value);
	}

	public static void callTrueDiv(Map obj, String attrname, Object value)
	{
		obj.put(attrname, TrueDivAST.call(call(obj, attrname), value));
	}

	public static void callTrueDiv(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof Map)
			callTrueDiv((Map)obj, attrname, value);
		else if (obj instanceof UL4SetItemString)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} /= {}", value);
			((UL4SetItemString)obj).setItemStringUL4(attrname, TrueDivAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} /= {}", value);
			((UL4SetItem)obj).setItemUL4(attrname, TrueDivAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}.{} //= {}", obj, attrname, value);
	}

	public static void callMod(Map obj, String attrname, Object value)
	{
		obj.put(attrname, ModAST.call(call(obj, attrname), value));
	}

	public static void callMod(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof Map)
			callMod((Map)obj, attrname, value);
		else if (obj instanceof UL4SetItemString)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} %= {}", value);
			((UL4SetItemString)obj).setItemStringUL4(attrname, ModAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} %= {}", value);
			((UL4SetItem)obj).setItemUL4(attrname, ModAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}.{} //= {}", obj, attrname, value);
	}

	public static void callShiftLeft(Map obj, String attrname, Object value)
	{
		obj.put(attrname, ShiftLeftAST.call(call(obj, attrname), value));
	}

	public static void callShiftLeft(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof Map)
			callShiftLeft((Map)obj, attrname, value);
		else if (obj instanceof UL4SetItemString)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} <<= {}", value);
			((UL4SetItemString)obj).setItemStringUL4(attrname, ShiftLeftAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} <<= {}", value);
			((UL4SetItem)obj).setItemUL4(attrname, ShiftLeftAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}.{} <<= {}", obj, attrname, value);
	}

	public static void callShiftRight(Map obj, String attrname, Object value)
	{
		obj.put(attrname, ShiftRightAST.call(call(obj, attrname), value));
	}

	public static void callShiftRight(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof Map)
			callShiftRight((Map)obj, attrname, value);
		else if (obj instanceof UL4SetItemString)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} >>= {}", value);
			((UL4SetItemString)obj).setItemStringUL4(attrname, ShiftRightAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} >>= {}", value);
			((UL4SetItem)obj).setItemUL4(attrname, ShiftRightAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}.{} >>= {}", obj, attrname, value);
	}

	public static void callBitAnd(Map obj, String attrname, Object value)
	{
		obj.put(attrname, BitAndAST.call(call(obj, attrname), value));
	}

	public static void callBitAnd(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof Map)
			callBitAnd((Map)obj, attrname, value);
		else if (obj instanceof UL4SetItemString)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} &= {}", value);
			((UL4SetItemString)obj).setItemStringUL4(attrname, BitAndAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} &= {}", value);
			((UL4SetItem)obj).setItemUL4(attrname, BitAndAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}.{} &= {}", obj, attrname, value);
	}

	public static void callBitXOr(Map obj, String attrname, Object value)
	{
		obj.put(attrname, BitXOrAST.call(call(obj, attrname), value));
	}

	public static void callBitXOr(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof Map)
			callBitXOr((Map)obj, attrname, value);
		else if (obj instanceof UL4SetItemString)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} ^= {}", value);
			((UL4SetItemString)obj).setItemStringUL4(attrname, BitXOrAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} ^= {}", value);
			((UL4SetItem)obj).setItemUL4(attrname, BitXOrAST.call(orgvalue, value));
		}
		else
			throw new ArgumentTypeMismatchException("{}.{} ^= {}", obj, attrname, value);
	}

	public static void callBitOr(Map obj, String attrname, Object value)
	{
		obj.put(attrname, BitOrAST.call(call(obj, attrname), value));
	}

	public static void callBitOr(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof Map)
			callBitOr((Map)obj, attrname, value);
		else if (obj instanceof UL4SetItemString)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} |= {}", value);
			((UL4SetItemString)obj).setItemStringUL4(attrname, BitOrAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{}.{} |= {}", value);
			((UL4SetItem)obj).setItemUL4(attrname, BitOrAST.call(orgvalue, value));
		}
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

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "obj", "attrname");

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
