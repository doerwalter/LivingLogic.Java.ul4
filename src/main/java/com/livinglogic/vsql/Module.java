/*
** Copyright 2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import com.livinglogic.ul4.UL4GetItemString;
import com.livinglogic.ul4.UL4Repr;
import com.livinglogic.ul4.Function;
import com.livinglogic.ul4.UndefinedKey;

public class Module implements UL4GetItemString, UL4Repr
{
	private Map<String, Function> functions;

	public Module()
	{
		functions = new HashMap<String, Function>();
		functions.put("const", new Const.Function());
		functions.put("lower", new Lower.Function());
		functions.put("upper", new Upper.Function());
		functions.put("add", new Add.Function());
		functions.put("mul", new Mul.Function());
		functions.put("field", new FieldRef.Function());
	}

	public Set<String> getAttributeNamesUL4()
	{
		return functions.keySet();
	}

	public Object getItemStringUL4(String key)
	{
		Function function = functions.get(key);

		if (function != null)
			return function;
		else
			return new UndefinedKey(key);
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<module vsql>");
	}
}
