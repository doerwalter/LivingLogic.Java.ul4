/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class LoadFloat extends LoadConst
{
	protected double value;

	public LoadFloat(double value)
	{
		this.value = value;
	}

	public int getType()
	{
		return Opcode.OC_LOADFLOAT;
	}

	public String getTokenType()
	{
		return "float";
	}

	public Object getValue()
	{
		return new Double(value);
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_LOADFLOAT, r, String.valueOf(value), location);
		return r;
	}

	public String toString()
	{
		return Utils.repr(value);
	}

	public Object evaluate(EvaluationContext context)
	{
		return value;
	}
}
