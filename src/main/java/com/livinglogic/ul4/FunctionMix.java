/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionMix extends Function
{
	@Override
	public String getModuleName()
	{
		return "color";
	}

	@Override
	public String getNameUL4()
	{
		return "mix";
	}

	private static final Signature signature = new Signature().addVarPositional("values");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call((List)args.get(0));
	}

	public static Object call(List values)
	{
		double r= 0.0;
		double g= 0.0;
		double b= 0.0;
		double a= 0.0;
		double weight = 1.0;
		double sumWeight = 0.0;

		for (Object value : values)
		{
			if (value instanceof Color)
			{
				Color c = (Color)value;
				r += weight * c.getR();
				g += weight * c.getG();
				b += weight * c.getB();
				a += weight * c.getA();
				sumWeight += weight;
			}
			else if (value instanceof Number)
				weight = ((Number)value).doubleValue();
			else
				throw new ArgumentTypeMismatchException("color.mix() arguments msut be numbers or colors, not {!t}", value);
		}
		if (sumWeight == 0.0)
				throw new ArithmeticException("at least one of the arguments must be a color and at least one of the weights must be >0");
		return new Color(
			(int)Math.round(r/sumWeight),
			(int)Math.round(g/sumWeight),
			(int)Math.round(b/sumWeight),
			(int)Math.round(a/sumWeight)
		);
	}

	public static FunctionMix function = new FunctionMix();
}
