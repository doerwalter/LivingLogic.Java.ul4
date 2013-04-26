/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public abstract class Function implements UL4Call, UL4Name, UL4Type
{
	public abstract String nameUL4();

	public String typeUL4()
	{
		return "function";
	}

	private Signature signature = null;

	protected void makeSignature(Signature signature)
	{
	}

	private Signature getSignature()
	{
		if (signature == null)
		{
			signature = new Signature(nameUL4());
			makeSignature(signature);
		}
		return signature;
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		return evaluate(getSignature().makeArgumentArray(args, kwargs));
	}

	public abstract Object evaluate(Object[] args);
}
