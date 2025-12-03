/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.io.IOException;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

import com.livinglogic.vsql.VSQLAST;
import com.livinglogic.vsql.VSQLFieldRefAST;
import com.livinglogic.vsql.VSQLAttrAST;
import com.livinglogic.vsql.VSQLField;
import com.livinglogic.vsql.VSQLGroup;
import com.livinglogic.utils.VSQLUtils;


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
			return new AttrAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof AttrAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected AST obj;
	protected String attrName;

	public AttrAST(Template template, int posStart, int posStop, AST obj, String attrName)
	{
		super(template, posStart, posStop);
		this.obj = obj;
		this.attrName = attrName;
	}

	public String getType()
	{
		return "attr";
	}

	public AST getObj()
	{
		return obj;
	}

	public String getAttrName()
	{
		return attrName;
	}

	@Override
	public VSQLAST asVSQL(Map<String, VSQLField> vars)
	{
		String trailingSource = VSQLUtils.getSourceSuffix(obj, this);
		int dotPos = trailingSource.indexOf(".");

		for (++dotPos; dotPos < trailingSource.length() && trailingSource.charAt(dotPos) == ' '; ++dotPos)
			;

		String attrNamePrefix = trailingSource.substring(0, dotPos);
		String attrNameSuffix = trailingSource.substring(dotPos + attrName.length());

		VSQLAST vsqlObj = obj.asVSQL(vars);

		if (vsqlObj instanceof VSQLFieldRefAST vqlFieldRef)
		{
			VSQLField field = vqlFieldRef.getField();
			if (field != null)
			{
				VSQLGroup group = field.getRefGroup();
				if (group != null)
				{
					VSQLField refField = group.getField(attrName);
					if (refField != null)
					{
						return new VSQLFieldRefAST(
							VSQLUtils.getSourcePrefix(this, obj),
							vqlFieldRef,
							attrNamePrefix,
							attrName,
							attrNameSuffix,
							refField
						);
					}
				}
			}
			// Fall through to a normal attribute access
		}
		return new VSQLAttrAST(
			VSQLUtils.getSourcePrefix(this, obj),
			vsqlObj,
			attrNamePrefix,
			attrName,
			attrNameSuffix
		);
	}


	@Override
	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj.decoratedEvaluate(context), attrName);
	}

	public void evaluateSet(EvaluationContext context, Object value)
	{
		callSet(context, obj.decoratedEvaluate(context), attrName, value);
	}

	public void evaluateAdd(EvaluationContext context, Object value)
	{
		callAdd(context, obj.decoratedEvaluate(context), attrName, value);
	}

	public void evaluateSub(EvaluationContext context, Object value)
	{
		callSub(context, obj.decoratedEvaluate(context), attrName, value);
	}

	public void evaluateMul(EvaluationContext context, Object value)
	{
		callMul(context, obj.decoratedEvaluate(context), attrName, value);
	}

	public void evaluateFloorDiv(EvaluationContext context, Object value)
	{
		callFloorDiv(context, obj.decoratedEvaluate(context), attrName, value);
	}

	public void evaluateTrueDiv(EvaluationContext context, Object value)
	{
		callTrueDiv(context, obj.decoratedEvaluate(context), attrName, value);
	}

	public void evaluateMod(EvaluationContext context, Object value)
	{
		callMod(context, obj.decoratedEvaluate(context), attrName, value);
	}

	public void evaluateShiftLeft(EvaluationContext context, Object value)
	{
		callShiftLeft(context, obj.decoratedEvaluate(context), attrName, value);
	}

	public void evaluateShiftRight(EvaluationContext context, Object value)
	{
		callShiftRight(context, obj.decoratedEvaluate(context), attrName, value);
	}

	public void evaluateBitAnd(EvaluationContext context, Object value)
	{
		callBitAnd(context, obj.decoratedEvaluate(context), attrName, value);
	}

	public void evaluateBitXOr(EvaluationContext context, Object value)
	{
		callBitXOr(context, obj.decoratedEvaluate(context), attrName, value);
	}

	public void evaluateBitOr(EvaluationContext context, Object value)
	{
		callBitOr(context, obj.decoratedEvaluate(context), attrName, value);
	}

	public static Object call(EvaluationContext context, Object obj, String attrName)
	{
		try
		{
			return UL4Type.getType(obj).getAttr(context, obj, attrName);
		}
		catch (AttributeException exc)
		{
			if (exc.getObject() == obj)
				return new UndefinedAttribute(obj, attrName);
			else
				// The {@code AttributeException} originated from another object
				throw exc;
		}
	}

	public static void callSet(EvaluationContext context, Object obj, String attrName, Object value)
	{
		UL4Type.getType(obj).setAttr(context, obj, attrName, value);
	}

	public static void callAdd(EvaluationContext context, Object obj, String attrName, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		// We do not catch the {@code AttributeException} here, because we want
		// to throw an {@code AttributeException} for non-existant attributes
		// and {@code ReadonlyException} for read-only ones.
		Object oldValue = type.getAttr(context, obj, attrName);
		Object newValue = IAdd.call(context, oldValue, value);
		type.setAttr(context, obj, attrName, newValue);
	}

	public static void callSub(EvaluationContext context, Object obj, String attrName, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrName);
		Object newValue = SubAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrName, newValue);
	}

	public static void callMul(EvaluationContext context, Object obj, String attrName, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrName);
		Object newValue = IMul.call(context, oldValue, value);
		type.setAttr(context, obj, attrName, newValue);
	}

	public static void callFloorDiv(EvaluationContext context, Object obj, String attrName, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrName);
		Object newValue = FloorDivAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrName, newValue);
	}

	public static void callTrueDiv(EvaluationContext context, Object obj, String attrName, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrName);
		Object newValue = TrueDivAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrName, newValue);
	}

	public static void callMod(EvaluationContext context, Object obj, String attrName, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrName);
		Object newValue = ModAST.call(oldValue, value);
		type.setAttr(context, obj, attrName, newValue);
	}

	public static void callShiftLeft(EvaluationContext context, Object obj, String attrName, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrName);
		Object newValue = ShiftLeftAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrName, newValue);
	}

	public static void callShiftRight(EvaluationContext context, Object obj, String attrName, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrName);
		Object newValue = ShiftRightAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrName, newValue);
	}

	public static void callBitAnd(EvaluationContext context, Object obj, String attrName, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrName);
		Object newValue = BitAndAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrName, newValue);
	}

	public static void callBitXOr(EvaluationContext context, Object obj, String attrName, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrName);
		Object newValue = BitXOrAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrName, newValue);
	}

	public static void callBitOr(EvaluationContext context, Object obj, String attrName, Object value)
	{
		UL4Type type = UL4Type.getType(obj);

		Object oldValue = type.getAttr(context, obj, attrName);
		Object newValue = BitOrAST.call(context, oldValue, value);
		type.setAttr(context, obj, attrName, newValue);
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
		encoder.dump(attrName);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (AST)decoder.load();
		attrName = ((String)decoder.load()).intern();
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
				return attrName;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}
