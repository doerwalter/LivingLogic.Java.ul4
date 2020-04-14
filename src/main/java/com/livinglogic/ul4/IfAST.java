/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.Set;

import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class IfAST extends CodeAST
{
	private CodeAST objIf;
	private CodeAST objCond;
	private CodeAST objElse;

	public IfAST(InterpretedTemplate template, Slice pos, CodeAST objIf, CodeAST objCond, CodeAST objElse)
	{
		super(template, pos);
		this.objIf = objIf;
		this.objCond = objCond;
		this.objElse = objElse;
	}

	public String getType()
	{
		return "if";
	}

	public static CodeAST make(InterpretedTemplate template, Slice pos, CodeAST objIf, CodeAST objCond, CodeAST objElse)
	{
		if (objCond instanceof ConstAST)
		{
			Object cond = ((ConstAST)objCond).value;

			if (!(cond instanceof Undefined))
			{
				try
				{
					return FunctionBool.call(cond) ? objIf : objElse;
				}
				catch (Exception ex)
				{
					// fall through to create a real {@code IfAST} object
				}
			}
		}
		return new IfAST(template, pos, objIf, objCond, objElse);
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
		objIf = (CodeAST)decoder.load();
		objCond = (CodeAST)decoder.load();
		objElse = (CodeAST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "objif", "objcond", "objelse");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "objif":
				return objIf;
			case "objcond":
				return objCond;
			case "objelse":
				return objElse;
			default:
				return super.getAttrUL4(key);
		}
	}
}
