/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class AttrAST extends CodeAST implements LValue
{
	protected AST obj;
	protected String attrname;

	public AttrAST(InterpretedTemplate template, Slice pos, AST obj, String attrname)
	{
		super(template, pos);
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

	public static Object call(UL4GetAttr obj, String attrname)
	{
		try
		{
			return obj.getAttrUL4(attrname);
		}
		catch (AttributeException exc)
		{
			if (exc.getObject() == obj)
				return new UndefinedKey(attrname);
			else
				// The {@code AttributeException} originated from another object
				throw exc;
		}
	}

	public static Object call(UL4GetItem obj, String attrname)
	{
		try
		{
			return obj.getItemUL4(attrname);
		}
		catch (AttributeException exc)
		{
			if (exc.getObject() == obj)
				return new UndefinedKey(attrname);
			else
				// The {@code AttributeException} originated from another object
				throw exc;
		}
	}

	public static Object call(Map obj, String attrname)
	{
		return DictProto.getAttr(obj, attrname);
	}

	public static Object call(Set obj, String attrname)
	{
		return SetProto.getAttr(obj, attrname);
	}

	public static Object call(List obj, String attrname)
	{
		return ListProto.getAttr(obj, attrname);
	}

	public static Object call(String obj, String attrname)
	{
		return StrProto.getAttr(obj, attrname);
	}

	public static Object call(Date obj, String attrname)
	{
		return DateProto.getAttr(obj, attrname);
	}

	public static Object call(Throwable obj, String attrname)
	{
		return new ExceptionProto(obj.getClass()).getAttr(obj, attrname);
	}

	public static Object call(Object obj, String attrname)
	{
		if (obj instanceof UL4GetAttr)
			return call((UL4GetAttr)obj, attrname);
		else if (obj instanceof UL4GetItem)
			return call((UL4GetItem)obj, attrname);
		else
		{
			try
			{
				return Proto.get(obj).getAttr(obj, attrname);
			}
			catch (AttributeException exc)
			{
				if (exc.getObject() == obj)
					return new UndefinedKey(attrname);
				else
					// The {@code AttributeException} originated from another object
					throw exc;
			}
		}
	}

	public static Object call(EvaluationContext context, UL4GetAttrWithContext obj, String attrname)
	{
		return obj.getAttrWithContextUL4(context, attrname);
	}

	public static Object call(EvaluationContext context, Object obj, String attrname)
	{
		if (obj instanceof UL4GetAttrWithContext)
			return call(context, (UL4GetAttrWithContext)obj, attrname);
		else if (obj instanceof UL4GetAttr)
			return call((UL4GetAttr)obj, attrname);
		if (obj instanceof UL4GetItemWithContext)
			return call(context, (UL4GetItemWithContext)obj, attrname);
		else if (obj instanceof UL4GetItem)
			return call((UL4GetItem)obj, attrname);
		else
		{
			try
			{
				return Proto.get(obj).getAttr(context, obj, attrname);
			}
			catch (AttributeException exc)
			{
				if (exc.getObject() == obj)
					return new UndefinedKey(attrname);
				else
					// The {@code AttributeException} originated from another object
					throw exc;
			}
		}
	}

	public static void callSet(UL4SetAttr obj, String attrname, Object value)
	{
		obj.setAttrUL4(attrname, value);
	}

	public static void callSet(Map obj, String attrname, Object value)
	{
		obj.put(attrname, value);
	}

	public static void callSet(Object obj, String attrname, Object value)
	{
		Proto.get(obj).setAttr(obj, attrname, value);
	}

	private static Object getValue(EvaluationContext context, Object obj, String attrname, String excmessage, Object value)
	{
		// We do not catch the {@code AttributeException} here, because {@code getValue} is
		// only called for augmented assignment so eventually we will try to set the attribute,
		// so we want to throw an {@code AttributeException} for non-existant attributes
		// and {@code ReadonlyException} for read-only ones.
		if (obj instanceof UL4GetAttrWithContext)
			return ((UL4GetAttrWithContext)obj).getAttrWithContextUL4(context, attrname);
		else if (obj instanceof UL4GetAttr)
			return ((UL4GetAttr)obj).getAttrUL4(attrname);
		else if (obj instanceof UL4GetItemWithContext)
			return ((UL4GetItemWithContext)obj).getItemWithContextUL4(context, attrname);
		else if (obj instanceof UL4GetItem)
			return ((UL4GetItem)obj).getItemUL4(attrname);
		throw new ArgumentTypeMismatchException(excmessage, obj, attrname, value);
	}

	public static void callAdd(Map obj, String attrname, Object value)
	{
		obj.put(attrname, IAdd.call(call(obj, attrname), value));
	}

	public static void callAdd(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4SetAttrWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} += {!t} not supported", value);
			((UL4SetAttrWithContext)obj).setAttrWithContextUL4(context, attrname, IAdd.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetAttr)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} += {!t} not supported", value);
			((UL4SetAttr)obj).setAttrUL4(attrname, IAdd.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} += {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, attrname, IAdd.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} += {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(attrname, IAdd.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callAdd((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{!t}.{} += {!t} not supported", obj, attrname, value);
	}

	public static void callSub(Map obj, String attrname, Object value)
	{
		obj.put(attrname, SubAST.call(call(obj, attrname), value));
	}

	public static void callSub(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4SetAttrWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} -= {!t} not supported", value);
			((UL4SetAttrWithContext)obj).setAttrWithContextUL4(context, attrname, SubAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetAttr)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} -= {!t} not supported", value);
			((UL4SetAttr)obj).setAttrUL4(attrname, SubAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} -= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, attrname, SubAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} -= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(attrname, SubAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callSub((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{!t}.{} -= {!t} not supported", obj, attrname, value);
	}

	public static void callMul(Map obj, String attrname, Object value)
	{
		obj.put(attrname, IMul.call(call(obj, attrname), value));
	}

	public static void callMul(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4SetAttrWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} *= {!t} not supported", value);
			((UL4SetAttrWithContext)obj).setAttrWithContextUL4(context, attrname, IMul.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetAttr)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} *= {!t} not supported", value);
			((UL4SetAttr)obj).setAttrUL4(attrname, IMul.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} *= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, attrname, IMul.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} *= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(attrname, IMul.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callMul((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{!t}.{} *= {!t} not supported", obj, attrname, value);
	}

	public static void callFloorDiv(Map obj, String attrname, Object value)
	{
		obj.put(attrname, FloorDivAST.call(call(obj, attrname), value));
	}

	public static void callFloorDiv(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4SetAttrWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} //= {!t} not supported", value);
			((UL4SetAttrWithContext)obj).setAttrWithContextUL4(context, attrname, FloorDivAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetAttr)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} //= {!t} not supported", value);
			((UL4SetAttr)obj).setAttrUL4(attrname, FloorDivAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} //= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, attrname, FloorDivAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} //= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(attrname, FloorDivAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callFloorDiv((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{!t}.{} //= {!t} not supported", obj, attrname, value);
	}

	public static void callTrueDiv(Map obj, String attrname, Object value)
	{
		obj.put(attrname, TrueDivAST.call(call(obj, attrname), value));
	}

	public static void callTrueDiv(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4SetAttrWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} /= {!t} not supported", value);
			((UL4SetAttrWithContext)obj).setAttrWithContextUL4(context, attrname, TrueDivAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetAttr)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} /= {!t} not supported", value);
			((UL4SetAttr)obj).setAttrUL4(attrname, TrueDivAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} /= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, attrname, TrueDivAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} /= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(attrname, TrueDivAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callTrueDiv((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{!t}.{} //= {!t} not supported", obj, attrname, value);
	}

	public static void callMod(Map obj, String attrname, Object value)
	{
		obj.put(attrname, ModAST.call(call(obj, attrname), value));
	}

	public static void callMod(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4SetAttrWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} %= {!t} not supported", value);
			((UL4SetAttrWithContext)obj).setAttrWithContextUL4(context, attrname, ModAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetAttr)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} %= {!t} not supported", value);
			((UL4SetAttr)obj).setAttrUL4(attrname, ModAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} %= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, attrname, ModAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} %= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(attrname, ModAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callMod((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{!t}.{} //= {!t} not supported", obj, attrname, value);
	}

	public static void callShiftLeft(Map obj, String attrname, Object value)
	{
		obj.put(attrname, ShiftLeftAST.call(call(obj, attrname), value));
	}

	public static void callShiftLeft(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4SetAttrWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} <<= {!t} not supported", value);
			((UL4SetAttrWithContext)obj).setAttrWithContextUL4(context, attrname, ShiftLeftAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetAttr)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} <<= {!t} not supported", value);
			((UL4SetAttr)obj).setAttrUL4(attrname, ShiftLeftAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} <<= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, attrname, ShiftLeftAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} <<= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(attrname, ShiftLeftAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callShiftLeft((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{!t}.{} <<= {!t} not supported", obj, attrname, value);
	}

	public static void callShiftRight(Map obj, String attrname, Object value)
	{
		obj.put(attrname, ShiftRightAST.call(call(obj, attrname), value));
	}

	public static void callShiftRight(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4SetAttrWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} >>= {!t} not supported", value);
			((UL4SetAttrWithContext)obj).setAttrWithContextUL4(context, attrname, ShiftRightAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetAttr)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} >>= {!t} not supported", value);
			((UL4SetAttr)obj).setAttrUL4(attrname, ShiftRightAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} >>= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, attrname, ShiftRightAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} >>= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(attrname, ShiftRightAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callShiftRight((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{!t}.{} >>= {!t} not supported", obj, attrname, value);
	}

	public static void callBitAnd(Map obj, String attrname, Object value)
	{
		obj.put(attrname, BitAndAST.call(call(obj, attrname), value));
	}

	public static void callBitAnd(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4SetAttrWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} &= {!t} not supported", value);
			((UL4SetAttrWithContext)obj).setAttrWithContextUL4(context, attrname, BitAndAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetAttr)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} &= {!t} not supported", value);
			((UL4SetAttr)obj).setAttrUL4(attrname, BitAndAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} &= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, attrname, BitAndAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} &= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(attrname, BitAndAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callBitAnd((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{!t}.{} &= {!t} not supported", obj, attrname, value);
	}

	public static void callBitXOr(Map obj, String attrname, Object value)
	{
		obj.put(attrname, BitXOrAST.call(call(obj, attrname), value));
	}

	public static void callBitXOr(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4SetAttrWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} ^= {!t} not supported", value);
			((UL4SetAttrWithContext)obj).setAttrWithContextUL4(context, attrname, BitXOrAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetAttr)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} ^= {!t} not supported", value);
			((UL4SetAttr)obj).setAttrUL4(attrname, BitXOrAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} ^= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, attrname, BitXOrAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} ^= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(attrname, BitXOrAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callBitXOr((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{!t}.{} ^= {!t} not supported", obj, attrname, value);
	}

	public static void callBitOr(Map obj, String attrname, Object value)
	{
		obj.put(attrname, BitOrAST.call(call(obj, attrname), value));
	}

	public static void callBitOr(EvaluationContext context, Object obj, String attrname, Object value)
	{
		if (obj instanceof UL4SetAttrWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} |= {!t} not supported", value);
			((UL4SetAttrWithContext)obj).setAttrWithContextUL4(context, attrname, BitOrAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetAttr)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} |= {!t} not supported", value);
			((UL4SetAttr)obj).setAttrUL4(attrname, BitOrAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItemWithContext)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} |= {!t} not supported", value);
			((UL4SetItemWithContext)obj).setItemWithContextUL4(context, attrname, BitOrAST.call(orgvalue, value));
		}
		else if (obj instanceof UL4SetItem)
		{
			Object orgvalue = getValue(context, obj, attrname, "{!t}.{} |= {!t} not supported", value);
			((UL4SetItem)obj).setItemUL4(attrname, BitOrAST.call(orgvalue, value));
		}
		else if (obj instanceof Map)
			callBitOr((Map)obj, attrname, value);
		else
			throw new ArgumentTypeMismatchException("{!t}.{} |= {!t} not supported", obj, attrname, value);
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

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "obj":
				return obj;
			case "attrname":
				return attrname;
			default:
				return super.getAttrUL4(key);
		}
	}
}
