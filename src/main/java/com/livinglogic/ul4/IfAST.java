/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.Set;

import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class IfAST extends AST
{
	private AST objIf;
	private AST objCond;
	private AST objElse;

	public IfAST(Location location, int start, int end, AST objIf, AST objCond, AST objElse)
	{
		super(location, start, end);
		this.objIf = objIf;
		this.objCond = objCond;
		this.objElse = objElse;
	}

	public String getType()
	{
		return "if";
	}

	public static AST make(Location location, int start, int end, AST objIf, AST objCond, AST objElse)
	{
		if (objCond instanceof ConstAST)
		{
			Object cond = ((ConstAST)objCond).value;

			if (!(cond instanceof Undefined))
				return FunctionBool.call(cond) ? objIf : objElse;
		}
		return new IfAST(location, start, end, objIf, objCond, objElse);
	}

	public Object evaluate(EvaluationContext context)
	{
		Object objCondEv = objCond.decoratedEvaluate(context);
		if (FunctionBool.call(objCondEv))
			return objIf.decoratedEvaluate(context);
		else
			return objElse.decoratedEvaluate(context);
	}

	// this static version is only used for constant folding, not in evaluate(), because that would require that we evaluate both branches
	public static Object call(Object argIf, Object argCond, Object argElse)
	{
		return FunctionBool.call(argCond) ? argIf : argElse;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(objIf);
		encoder.dump(objCond);
		encoder.dump(objElse);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		objIf = (AST)decoder.load();
		objCond = (AST)decoder.load();
		objElse = (AST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(AST.attributes, "objif", "objcond", "objelse");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("objif".equals(key))
			return objIf;
		else if ("objcond".equals(key))
			return objCond;
		else if ("objelse".equals(key))
			return objElse;
		else
			return super.getItemStringUL4(key);
	}
}
