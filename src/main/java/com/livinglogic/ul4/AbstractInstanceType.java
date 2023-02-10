/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;


public abstract class AbstractInstanceType extends AbstractType
{
	@Override
	public boolean boolInstance(EvaluationContext context, Object instance)
	{
		return ((UL4Instance)instance).boolUL4(context);
	}

	@Override
	public Number intInstance(EvaluationContext context, Object instance)
	{
		return ((UL4Instance)instance).intUL4(context);
	}

	@Override
	public Number floatInstance(EvaluationContext context, Object instance)
	{
		return ((UL4Instance)instance).floatUL4(context);
	}

	public String strInstance(EvaluationContext context, Object object)
	{
		return ((UL4Instance)object).strUL4(context);
	}

	@Override
	public Set<String> dirInstance(EvaluationContext context, Object instance)
	{
		return ((UL4Instance)instance).dirUL4(context);
	}

	@Override
	public Object getAttr(EvaluationContext context, Object object, String key)
	{
		return ((UL4Instance)object).getAttrUL4(context, key);
	}

	@Override
	public void setAttr(EvaluationContext context, Object object, String key, Object value)
	{
		((UL4Instance)object).setAttrUL4(context, key, value);
	}
}
