/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

import com.livinglogic.vsql.VSQLIsAST;
import com.livinglogic.vsql.VSQLField;
import com.livinglogic.utils.VSQLUtils;


public class IsAST extends BinaryAST
{
	protected static class Type extends BinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "IsAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.is";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a binary ``is`` comparison expression (e.g. ``x is y``).";
		}

		@Override
		public IsAST create(String id)
		{
			return new IsAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof IsAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public IsAST(Template template, int posStart, int posStop, CodeAST obj1, CodeAST obj2)
	{
		super(template, posStart, posStop, obj1, obj2);
	}

	public String getType()
	{
		return "is";
	}

	@Override
	public VSQLIsAST asVSQL(Map<String, VSQLField> vars)
	{
		return new VSQLIsAST(
			VSQLUtils.getSourcePrefix(this, obj1),
			obj1.asVSQL(vars),
			VSQLUtils.getSourceInfix(obj1, obj2),
			obj2.asVSQL(vars),
			VSQLUtils.getSourceSuffix(obj2, this)
		);
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static boolean call(Object obj1, Object obj2)
	{
		return obj1 == obj2;
	}
}
