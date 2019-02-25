/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
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
 * The base class of all nodes in the abstract syntax tree, i.e. everything
 * inside a template tag.
 */
public abstract class CodeAST extends AST
{
	/**
	 * Create a new {@code CodeAST} object.
	 * @param template The {@code InterpretedTemplate} object this node belongs to.
	 * @param pos The slice in the template source, where the source for this object is located.
	 */
	public CodeAST(InterpretedTemplate template, Slice pos)
	{
		super(template, pos);
	}
}
