/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class SliceAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "SliceAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.slice";
		}

		@Override
		public String getDoc()
		{
			return "AST node for creating a slice object (used in ``obj[index1:index2]``).";
		}

		@Override
		public SliceAST create(String id)
		{
			return new SliceAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof SliceAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected AST index1;
	protected AST index2;

	public SliceAST(Template template, int posStart, int posStop, AST index1, AST index2)
	{
		super(template, posStart, posStop);
		this.index1 = index1;
		this.index2 = index2;
	}

	public String getType()
	{
		return "slice";
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(index1 != null ? index1.decoratedEvaluate(context) : null, index2 != null ? index2.decoratedEvaluate(context) : null);
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(index1);
		encoder.dump(index2);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		index1 = (AST)decoder.load();
		index2 = (AST)decoder.load();
	}

	public static Object call(Object startIndex, Object endIndex)
	{
		if (startIndex == null)
		{
			if (endIndex == null)
				return new Slice(false, false, 0, 0);
			else
				return new Slice(false, true, 0, Utils.toInt(endIndex));
		}
		else
		{
			if (endIndex == null)
				return new Slice(true, false, Utils.toInt(startIndex), 0);
			else
				return new Slice(true, true, Utils.toInt(startIndex), Utils.toInt(endIndex));
		}
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "start", "stop");

	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		switch (key)
		{
			case "index1":
				return index1;
			case "index2":
				return index2;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}
