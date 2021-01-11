/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

import com.livinglogic.ul4on.Utils;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;

import static com.livinglogic.utils.SetUtils.makeSet;


public class ModuleUL4ON implements UL4Repr, UL4GetAttr, UL4Dir, UL4Type, UL4Name
{
	@Override
	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter
			.append("<module ")
			.append(getClass().getName())
			.append(">")
		;
	}

	protected static Set<String> attributes = makeSet("loads", "dumps", "Encoder", "Decoder");

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
			case "loads":
				return functionLoadS;
			case "dumps":
				return functionDumpS;
			case "Encoder":
				return functionEncoder;
			case "Decoder":
				return functionDecoder;
			default:
				throw new AttributeException(this, key);
		}
	}

	@Override
	public String typeUL4()
	{
		return "module";
	}

	public String nameUL4()
	{
		return "ul4on";
	}

	private static class FunctionLoadS extends Function
	{
		public String nameUL4()
		{
			return "loads";
		}

		private static final Signature signature = new Signature("dump", Signature.required);

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments arguments)
		{
			Object arg = arguments.get(0);

			if (!(arg instanceof String))
				throw new ArgumentTypeMismatchException("loads({!t}) not supported", arg);
			return Utils.loads((String)arg, null);
		}
	}

	private static FunctionLoadS functionLoadS = new FunctionLoadS();

	private static class FunctionDumpS extends Function
	{
		public String nameUL4()
		{
			return "dumps";
		}

		private static final Signature signature = new Signature("obj", Signature.required, "indent", null);

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments arguments)
		{
			Object obj = arguments.get(0);
			Object indent = arguments.get(1);

			if (indent != null && !(indent instanceof String))
				throw new ArgumentTypeMismatchException("dumps({!t}, {!t}) not supported", obj, indent);

			return Utils.dumps(obj, (String)indent);
		}
	}

	private static FunctionDumpS functionDumpS = new FunctionDumpS();

	private static class FunctionEncoder extends Function
	{
		public String nameUL4()
		{
			return "Encoder";
		}

		private static final Signature signature = new Signature("indent", null);

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments arguments)
		{
			Object indent = arguments.get(0);

			if (indent != null && !(indent instanceof String))
				throw new ArgumentTypeMismatchException("Encoder({!t}) not supported", indent);

			return new Encoder((String)indent);
		}
	}

	private static FunctionEncoder functionEncoder = new FunctionEncoder();

	private static class FunctionDecoder extends Function
	{
		public String nameUL4()
		{
			return "Decoder";
		}

		public Object evaluate(BoundArguments arguments)
		{
			return new Decoder();
		}
	}

	private static FunctionDecoder functionDecoder = new FunctionDecoder();
}
