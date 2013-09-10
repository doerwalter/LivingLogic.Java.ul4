/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class Var extends AST implements LValue
{
	protected String name;

	public Var(Location location, int start, int end, String name)
	{
		super(location, start, end);
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
		context.set(name, Add.call(context.get(name), value));
	}

	public void evaluateSub(EvaluationContext context, Object value)
	{
		context.set(name, Sub.call(context.get(name), value));
	}

	public void evaluateMul(EvaluationContext context, Object value)
	{
		context.set(name, Mul.call(context.get(name), value));
	}

	public void evaluateFloorDiv(EvaluationContext context, Object value)
	{
		context.set(name, FloorDiv.call(context.get(name), value));
	}

	public void evaluateTrueDiv(EvaluationContext context, Object value)
	{
		context.set(name, TrueDiv.call(context.get(name), value));
	}

	public void evaluateMod(EvaluationContext context, Object value)
	{
		context.set(name, Mod.call(context.get(name), value));
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(name);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		name = (String)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(AST.attributes, "name");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("name".equals(key))
			return name;
		else
			return super.getItemStringUL4(key);
	}
}
