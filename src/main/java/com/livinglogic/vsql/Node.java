/*
** Copyright 2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Set;

import com.livinglogic.ul4.Utils;
import com.livinglogic.ul4.InterpretedTemplate;
import com.livinglogic.ul4.SourcePart;
import com.livinglogic.ul4.BoundMethod;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.UL4Attributes;
import com.livinglogic.ul4.UL4GetItemString;
import com.livinglogic.ul4.UL4Repr;
import com.livinglogic.ul4.BoundArguments;
import com.livinglogic.ul4.ArgumentTypeMismatchException;
import com.livinglogic.ul4.UndefinedKey;
import com.livinglogic.ul4.SourceException;

import static com.livinglogic.utils.SetUtils.makeSet;

/**
 * Base class of all "virtual SQL" syntax tree nodes.
 */
public abstract class Node implements UL4Attributes, UL4GetItemString, UL4Repr
{
	protected InterpretedTemplate template;
	protected SourcePart origin;

	public Node(InterpretedTemplate template, SourcePart origin)
	{
		this.template = template;
		this.origin = origin;
	}

	public static RuntimeException error(InterpretedTemplate template, SourcePart origin, String message, Object... args)
	{
		RuntimeException ex = new RuntimeException(Utils.formatMessage(message, args));
		if (origin != null)
			ex = new SourceException(ex, template, origin);
		return ex;
	}

	protected RuntimeException error(String message, Object... args)
	{
		return error(template, origin, message, args);
	}

	public abstract Type type();

	String sql(String mode)
	{
		StringBuffer buffer = new StringBuffer();
		if ("oracle".equals(mode))
		{
			sqlOracle(buffer);
		}
		else
			throw new RuntimeException("unknown mode " + mode);
		return buffer.toString();
	}

	protected abstract void sqlOracle(StringBuffer buffer);

	private static class BoundMethodSQL extends BoundMethod<Node>
	{
		public BoundMethodSQL(Node object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "node.sql";
		}

		private static final Signature signature = new Signature("mode", Signature.required);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments args)
		{
			if (!(args.get(0) instanceof String))
				throw new ArgumentTypeMismatchException("sql() argument must be string not {!t}", args.get(0));
			return object.sql((String)args.get(0));
		}
	}

	protected static Set<String> attributes = makeSet("type", "sql");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "template":
				return template;
			case "origin":
				return origin;
			case "type":
				return type().toString();
			case "sql":
				return new BoundMethodSQL(this);
			default:
				return new UndefinedKey(key);
		}
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(">");
	}
}
