/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Random;

public class FunctionRandom extends Function
{
	public String nameUL4()
	{
		return "random";
	}

	private static Signature signature = new Signature("random");

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(Object[] args)
	{
		return call();
	}

	private static Random rng = new Random();

	public static double call()
	{
		return rng.nextDouble();
	}
}
