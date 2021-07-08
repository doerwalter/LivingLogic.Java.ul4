/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "AttrAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.attr";
		}

		@Override
		public String getDoc()
		{
			return "AST node for an expression that gets or sets an attribute of an object.\n(e.g. ``x.y``).";
		}

		@Override
		public AttrAST create(String id)
		{
			return new AttrAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof AttrAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected AST obj;
	protected String attrname;

	public AttrAST(Template template, Slice pos, AST obj, String attrname)
	{
		super(template, pos);
		this.obj = obj;
		this.attrname = attrname;
	}

	public String getType()
	{
		return "attr";
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj.decoratedEvaluate(context), attrname);
	}

	public void evaluateSet(EvaluationContext context, Object value)
	{
		callSet(context, obj.decoratedEvaluate(context), attrname, value);
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

	public static Object call(EvaluationContext context, Object obj, String attrname)
	{
		try
		{
			return UL4Type.getType(obj).getAttr(context, obj, attrname);
		}
		catch (AttributeException exc)
		{
			if (exc.getObject() == obj)
				return new UndefinedAttribute(obj, attrname);
			else
				// The {@code AttributeException} originated from another object
				throw exc;
		}
	}

	public static void callSet(EvaluationContext context, Object obj, String attrname, Object value)
	{
		UL4Type.getType(obj).setAttr(context, obj, attrname, value);
	}

	public static void callAdd(EvaluationContext context, Object obj, String attrname, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		// We do not catch the {@code AttributeException} here, because we want
		// to throw an {@code AttributeException} for non-existant attributes
		// and {@code ReadonlyException} for read-only ones.
		Object oldValue = type.getAttr(context, obj, attrname);
		Object newValue = IAdd.call(context, oldValue, value);
		type.setAttr(context, obj, attrname, newValue);
	}

	public static void callSub(EvaluationContext context, Object obj, String attrname, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrname);
		Object newValue = SubAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrname, newValue);
	}

	public static void callMul(EvaluationContext context, Object obj, String attrname, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrname);
		Object newValue = IMul.call(context, oldValue, value);
		type.setAttr(context, obj, attrname, newValue);
	}

	public static void callFloorDiv(EvaluationContext context, Object obj, String attrname, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrname);
		Object newValue = FloorDivAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrname, newValue);
	}

	public static void callTrueDiv(EvaluationContext context, Object obj, String attrname, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrname);
		Object newValue = TrueDivAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrname, newValue);
	}

	public static void callMod(EvaluationContext context, Object obj, String attrname, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrname);
		Object newValue = ModAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrname, newValue);
	}

	public static void callShiftLeft(EvaluationContext context, Object obj, String attrname, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrname);
		Object newValue = ShiftLeftAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrname, newValue);
	}

	public static void callShiftRight(EvaluationContext context, Object obj, String attrname, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrname);
		Object newValue = ShiftRightAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrname, newValue);
	}

	public static void callBitAnd(EvaluationContext context, Object obj, String attrname, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrname);
		Object newValue = BitAndAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrname, newValue);
	}

	public static void callBitXOr(EvaluationContext context, Object obj, String attrname, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrname);
		Object newValue = BitXOrAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrname, newValue);
	}

	public static void callBitOr(EvaluationContext context, Object obj, String attrname, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrname);
		Object newValue = BitOrAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrname, newValue);
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
		encoder.dump(attrname);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (AST)decoder.load();
		attrname = (String)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "obj", "attrname");

	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		switch (key)
		{
			case "obj":
				return obj;
			case "attrname":
				return attrname;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}
