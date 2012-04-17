/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.io.IOException;

abstract class Block extends AST
{
	protected LinkedList<AST> content;

	public Block()
	{
		content = new LinkedList<AST>();
	}

	public void append(AST item)
	{
		content.add(item);
	}

	public void finish(String name)
	{
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		for (AST item : content)
			item.evaluate(context);
		return null;
	}
}
