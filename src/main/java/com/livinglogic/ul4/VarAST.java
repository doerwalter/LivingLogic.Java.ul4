/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class VarAST extends CodeAST implements LValue
{
	protected static class Type extends SeqItemASTBase.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VarAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.var";
		}

		@Override
		public String getDoc()
		{
			return "AST node for getting a variable.";
		}

		@Override
		public VarAST create(String id)
		{
			return new VarAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VarAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected String name;

	public VarAST(Template template, Slice pos, String name)
	{
		super(template, pos);
		this.name = name;
	}

	public String getType()
	{
		return "var";
	}

	public Object evaluate(EvaluationContext context)
	{
		return context.get(name);
	}

	public void evaluateSet(EvaluationContext context, Object value)
	{
		context.set(name, value);
	}

	public void evaluateAdd(EvaluationContext context, Object value)
	{
		context.set(name, AddAST.call(context, context.get(name), value));
	}

	public void evaluateSub(EvaluationContext context, Object value)
	{
		context.set(name, SubAST.call(context, context.get(name), value));
	}

	public void evaluateMul(EvaluationContext context, Object value)
	{
		context.set(name, MulAST.call(context, context.get(name), value));
	}

	public void evaluateFloorDiv(EvaluationContext context, Object value)
	{
		context.set(name, FloorDivAST.call(context, context.get(name), value));
	}

	public void evaluateTrueDiv(EvaluationContext context, Object value)
	{
		context.set(name, TrueDivAST.call(context, context.get(name), value));
	}

	public void evaluateMod(EvaluationContext context, Object value)
	{
		context.set(name, ModAST.call(context.get(name), value));
	}

	public void evaluateShiftLeft(EvaluationContext context, Object value)
	{
		context.set(name, ShiftLeftAST.call(context, context.get(name), value));
	}

	public void evaluateShiftRight(EvaluationContext context, Object value)
	{
		context.set(name, ShiftRightAST.call(context, context.get(name), value));
	}

	public void evaluateBitAnd(EvaluationContext context, Object value)
	{
		context.set(name, BitAndAST.call(context, context.get(name), value));
	}

	public void evaluateBitXOr(EvaluationContext context, Object value)
	{
		context.set(name, BitXOrAST.call(context, context.get(name), value));
	}

	public void evaluateBitOr(EvaluationContext context, Object value)
	{
		context.set(name, BitOrAST.call(context, context.get(name), value));
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(name);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		name = (String)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "name");

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
			case "name":
				return name;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}
