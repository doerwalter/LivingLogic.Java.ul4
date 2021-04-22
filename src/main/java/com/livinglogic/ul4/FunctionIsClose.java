/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


public class FunctionIsClose extends Function
{
	@Override
	public String getNameUL4()
	{
		return "isclose";
	}

	private static final Signature signature = new Signature().addBoth("a").addBoth("b").addKeywordOnly("rel_tol", 1e-9).addKeywordOnly("abs_tol", 0.0);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments arguments)
	{
		Object arg1 = arguments.get(0);
		Object arg2 = arguments.get(1);
		Object arg3 = arguments.get(2);
		Object arg4 = arguments.get(3);

		return call(arg1, arg2, arg3, arg4);
	}

	public static boolean call(double a, double b, double rel_tol, double abs_tol)
	{
		if (a == b)
			return true;

		if (Double.isInfinite(a) || Double.isInfinite(b))
			return false;

		double diff = Math.abs(b - a);

		if (diff <= Math.abs(rel_tol * b))
			return true;
		if (diff <= Math.abs(rel_tol * a))
			return true;
		if (diff <= abs_tol)
			return true;
		return false;
	}

	public static boolean call(Object a, Object b, Object rel_tol, Object abs_tol)
	{
		if (!(a instanceof Number) || !(b instanceof Number) || !(rel_tol instanceof Number) || !(abs_tol instanceof Number))
			throw new ArgumentTypeMismatchException("math.isclose({!t}, {!t}, {!t}, {!t}) not supported", a, b, rel_tol, abs_tol);

		double dbl_a = ((Number)a).doubleValue();
		double dbl_b = ((Number)b).doubleValue();
		double dbl_rel_tol = ((Number)rel_tol).doubleValue();
		double dbl_abs_tol = ((Number)abs_tol).doubleValue();

		if (dbl_rel_tol < 0.0)
			throw new RuntimeException("math.isclose() argument rel_tol must be non-negative");
		if (dbl_abs_tol < 0.0)
			throw new RuntimeException("math.isclose() argument abs_tol must be non-negative");

		return call(dbl_a, dbl_b, dbl_rel_tol, dbl_abs_tol);
	}

	public static final Function function = new FunctionIsClose();
}
