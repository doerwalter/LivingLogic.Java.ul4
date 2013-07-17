/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class GetAttr extends AST
{
	protected AST obj;
	protected String attrname;

	public GetAttr(Location location, int start, int end, AST obj, String attrname)
	{
		super(location, start, end);
		this.obj = obj;
		this.attrname = attrname;
	}

	public String getType()
	{
		return "getattr";
	}

	public CallMeth makeCallMeth()
	{
		return new CallMeth(location, start, end, obj, attrname);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj.decoratedEvaluate(context), attrname);
	}

	public static Object call(Object obj, String attrname)
	{
		return GetItem.call(obj, attrname);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
		encoder.dump(attrname);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (AST)decoder.load();
		attrname = (String)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(AST.attributes, "obj", "attrname");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("obj".equals(key))
			return obj;
		else if ("attrname".equals(key))
			return attrname;
		else
			return super.getItemStringUL4(key);
	}
}
