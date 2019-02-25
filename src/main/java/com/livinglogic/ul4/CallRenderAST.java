/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Set;
import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import static com.livinglogic.ul4.Utils.findInnermostException;

/**
 * Common base class of {@link CallAST} and {@link RenderAST}
 */
public abstract class CallRenderAST extends CodeAST
{
	protected AST obj;
	protected List<ArgumentASTBase> arguments = new LinkedList<ArgumentASTBase>();

	public CallRenderAST(InterpretedTemplate template, Slice pos, AST obj)
	{
		super(template, pos);
		this.obj = obj;
	}

	protected void decorateException(Throwable ex, Object obj)
	{
		ex = findInnermostException(ex);
		if (FunctionIsTemplate.call(obj) || (!(ex instanceof LocationException)))
			ex.addSuppressed(new LocationException(this));
	}

	public void addArgument(ArgumentASTBase argument)
	{
		arguments.add(argument);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
		encoder.dump(arguments);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (AST)decoder.load();
		arguments = (List<ArgumentASTBase>)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "obj", "args");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "obj":
				return obj;
			case "args":
				return arguments;
			default:
				return super.getAttrUL4(key);
		}
	}
}
