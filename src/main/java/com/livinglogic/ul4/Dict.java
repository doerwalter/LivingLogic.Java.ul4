/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public class Dict extends AST
{
	protected LinkedList<DictItem> items = new LinkedList<DictItem>();

	public Dict()
	{
	}

	public void append(AST key, AST value)
	{
		items.add(new DictItemKeyValue(key, value));
	}

	public void append(AST dict)
	{
		items.add(new DictItemDict(dict));
	}

	public void append(DictItem item)
	{
		items.add(item);
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");

		boolean first = true;
		for (DictItem item : items)
		{
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append(item.toString());
		}
		buffer.append("}");
		return buffer.toString();
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_BUILDDICT, r, location);
		for (DictItem item : items)
		{
			if (item instanceof DictItemDict)
			{
				int rv = ((DictItemDict)item).dict.compile(template, registers, location);
				template.opcode(Opcode.OC_UPDATEDICT, r, rv, location);
				registers.free(rv);
			}
			else
			{
				int rk = ((DictItemKeyValue)item).key.compile(template, registers, location);
				int rv = ((DictItemKeyValue)item).value.compile(template, registers, location);
				template.opcode(Opcode.OC_ADDDICT, r, rk, rv, location);
				registers.free(rv);
				registers.free(rk);
			}
		}
		return r;
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Map result = new HashMap(items.size());

		for (DictItem item : items)
			item.addTo(context, result);
		return result;
	}
}
