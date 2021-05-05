/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.UL4ONSerializable;

/**
The base class of all nodes in the abstract syntax tree, i.e. everything
inside a template tag.
**/
public abstract class CodeAST extends AST
{
	protected static class Type extends AST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "CodeAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.codeast";
		}

		@Override
		public String getDoc()
		{
			return "The base class of all AST nodes that are not literal text.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof CodeAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected CodeAST(Template template, Slice startPos, Slice stopPos)
	{
		super(template, startPos, stopPos);
	}

	/**
	Create a new {@code CodeAST} object.
	@param template The {@code Template} object this node belongs to.
	@param startPos The slice in the template source, where the source for this object is located.
	**/
	public CodeAST(Template template, Slice startPos)
	{
		super(template, startPos);
	}
}
