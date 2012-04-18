/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class TemplateBlock extends Block
{
	public String getType()
	{
		return "template";
	}

	public void finish(String name)
	{
		if (name != null && name.length() != 0 && !name.equals("def"))
			throw new BlockException("def ended by end" + name);
	}

	public boolean handleLoopControl(String name)
	{
		throw new BlockException(name + " outside of for loop");
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("{\n");
		++indent;
		for (AST item : content)
			buffer.append(item.toString(indent));
		--indent;
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("}\n");
		return buffer.toString();
	}
}
