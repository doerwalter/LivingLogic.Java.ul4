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


public class ModuleUL4ON extends Module
{
	public ModuleUL4ON()
	{
		super("ul4on", "Object serialization");
		addObject(new FunctionLoadS());
		addObject(new FunctionDumpS());
		addObject(Decoder.type);
		addObject(Encoder.type);
	}

	private static abstract class ModuleFunction extends Function
	{
		@Override
		public String getModuleName()
		{
			return "ul4on";
		}
	}

	private static class FunctionLoadS extends ModuleFunction
	{

		@Override
		public String getNameUL4()
		{
			return "loads";
		}

		private static final Signature signature = new Signature().addPositionalOnly("dump");

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		@Override
		public Object evaluate(BoundArguments arguments)
		{
			Object arg = arguments.get(0);

			if (!(arg instanceof String))
				throw new ArgumentTypeMismatchException("loads({!t}) not supported", arg);
			return Utils.loads((String)arg, null);
		}
	}

	private static class FunctionDumpS extends ModuleFunction
	{
		@Override
		public String getNameUL4()
		{
			return "dumps";
		}

		private static final Signature signature = new Signature().addPositionalOnly("obj").addBoth("indent", null);

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		@Override
		public Object evaluate(BoundArguments arguments)
		{
			Object obj = arguments.get(0);
			Object indent = arguments.get(1);

			if (indent != null && !(indent instanceof String))
				throw new ArgumentTypeMismatchException("dumps({!t}, {!t}) not supported", obj, indent);

			return Utils.dumps(obj, (String)indent);
		}
	}

	public static final Module module = new ModuleUL4ON();
}
