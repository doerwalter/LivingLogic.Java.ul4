/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeSet;

import java.util.Set;


public abstract class AbstractType implements UL4Type, UL4GetAttr, UL4Dir
{
	public String getModuleName()
	{
		return null;
	}

	@Override
	public String getFullNameUL4()
	{
		String moduleName = getModuleName();
		if (moduleName != null)
			return moduleName + "." + getNameUL4();
		else
			return getNameUL4();
	}

	public String getDoc()
	{
		return null;
	}

 	private static final Signature signature = new Signature(); // default signature: no arguments

	/**
	<p>Return a signature for this type for creating instances.</p>

	<p>The default returns a signature without any arguments.</p>
	**/
	@Override
	public Signature getSignature()
	{
		return signature;
	}

	protected static Set<String> attributes = makeSet("module", "name", "doc");

	@Override
	public Set<String> dirUL4()
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "module":
				return getModuleName();
			case "name":
				return getNameUL4();
			case "doc":
				return getDoc();
			default:
				throw new AttributeException(this, key);
		}
	}
}
