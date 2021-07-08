/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "IfAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.if";
		}

		@Override
		public String getDoc()
		{
			return "AST node for the ternary inline ``if/else`` operator (e.g. ``x if y else z``).";
		}

		@Override
		public IfAST create(String id)
		{
			return new IfAST(null, null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof IfAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	private CodeAST objIf;
	private CodeAST objCond;
	private CodeAST objElse;

	public IfAST(Template template, Slice pos, CodeAST objIf, CodeAST objCond, CodeAST objElse)
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

	@Override
	public Object evaluate(EvaluationContext context)
	{
		Object objCondEv = objCond.decoratedEvaluate(context);
		if (Bool.call(context, objCondEv))
			return objIf.decoratedEvaluate(context);
		else
			return objElse.decoratedEvaluate(context);
	}

	// this static version can not be used in evaluate(), because that would require that we evaluate both branches
	public static Object call(EvaluationContext context, Object argIf, Object argCond, Object argElse)
	{
		return Bool.call(context, argCond) ? argIf : argElse;
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(objIf);
		encoder.dump(objCond);
		encoder.dump(objElse);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		objIf = (CodeAST)decoder.load();
		objCond = (CodeAST)decoder.load();
		objElse = (CodeAST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "objif", "objcond", "objelse");

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
			case "objif":
				return objIf;
			case "objcond":
				return objCond;
			case "objelse":
				return objElse;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}
