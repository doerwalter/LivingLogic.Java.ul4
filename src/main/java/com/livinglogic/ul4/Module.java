/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.LinkedHashMap;

import static com.livinglogic.utils.SetUtils.makeSet;


public class Module implements UL4Instance, UL4Repr, UL4GetAttr, UL4Dir, UL4Name
{
	protected static class Type extends AbstractInstanceType
	{
		public Type()
		{
			super(null, "module", null, "A package containing other object (function, types, etc.)");
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof Module;
		}
	}

	public static UL4Type type = new Type();

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
	public Set<String> dirUL4()
	{
		Set<String> attributes = new LinkedHashSet<String>();
		attributes.add("name");
		attributes.add("doc");
		attributes.addAll(content.keySet());

		return attributes;
	}

	@Override
	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "name":
				return name;
			case "doc":
				return doc;
			default:
			{
				Object object = content.get(key);
				if (key == null && !content.containsKey(key))
					throw new AttributeException(this, key);
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
