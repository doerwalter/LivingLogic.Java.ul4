/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.util.ArrayList;

public class List extends AST
{
	protected LinkedList<AST> items = new LinkedList<AST>();

	public List()
	{
	}

	public void append(AST item)
	{
		items.add(item);
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_BUILDLIST, r, location);

		for (AST item : items)
		{
			int ri = item.compile(template, registers, location);
			template.opcode(Opcode.OC_ADDLIST, r, ri, location);
			registers.free(ri);
		}
		return r;
	}

	public Object evaluate(EvaluationContext context)
	{
		ArrayList result = new ArrayList(items.size());

		for (AST item : items)
			result.add(item.evaluate(context));
		return result;
	}
}
