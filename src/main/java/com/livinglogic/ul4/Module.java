/*
** Copyright 2021-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.LinkedHashMap;


public class Module implements UL4Instance, UL4Repr, UL4Dir, UL4Name
{
	protected static class Type extends AbstractInstanceType
	{
		@Override
		public String getNameUL4()
		{
			return "module";
		}

		@Override
		public String getDoc()
		{
			return "An object containing other objects (functions, types, constants, etc.)";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof Module;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected String name;
	protected String doc;
	protected Map<String, Object> content = new LinkedHashMap<String, Object>();

	public Module(String name, String doc)
	{
		this.name = name;
		this.doc = doc;
	}

	public Module addObject(UL4Name object)
	{
		return addObject(object.getNameUL4(), object);
	}

	public Module addObject(String name, Object object)
	{
		content.put(name, object);
		return this;
	}

	@Override
	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter
			.append("<module ")
			.append(name)
			.append(">")
		;
	}

	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		Set<String> attributes = new LinkedHashSet<String>();
		attributes.add("__name__");
		attributes.add("__doc__");
		attributes.addAll(content.keySet());

		return attributes;
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		switch (key)
		{
			case "__name__":
				return name;
			case "__doc__":
				return doc;
			default:
			{
				Object object = content.get(key);
				if (key == null && !content.containsKey(key))
					return UL4Instance.super.getAttrUL4(context, key);
				return object;
			}
		}
	}

	@Override
	public String getNameUL4()
	{
		return name;
	}
}
