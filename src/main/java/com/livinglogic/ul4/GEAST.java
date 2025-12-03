/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

import com.livinglogic.vsql.VSQLGEAST;
import com.livinglogic.vsql.VSQLField;
import com.livinglogic.utils.VSQLUtils;


public class GEAST extends BinaryAST
{
	protected static class Type extends BinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "GEAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.ge";
		}

		@Override
		public String getDoc()
		{
			return "AST node for the binary \"greater than or equal\" comparison (e.g. ``x >= y``).";
		}

		@Override
		public GEAST create(String id)
		{
			return new GEAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof GEAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public GEAST(Template template, int posStart, int posStop, CodeAST obj1, CodeAST obj2)
	{
		super(template, posStart, posStop, obj1, obj2);
	}

	public String getType()
	{
		return "ge";
	}

	@Override
	public VSQLGEAST asVSQL(Map<String, VSQLField> vars)
	{
		return new VSQLGEAST(
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
		return call(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static boolean call(EvaluationContext context, Object obj1, Object obj2)
	{
		return Utils.cmp(obj1, obj2, ">=") >= 0;
	}
}
