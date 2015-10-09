/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import org.apache.commons.lang.StringUtils;

public class ASTException extends RuntimeException
{
	private static String makeMessage(InterpretedTemplate template, AST node)
	{
		StringBuilder buffer = new StringBuilder();
		String name = template.nameUL4();
		if (name == null)
			buffer.append("in unnamed template");
		else
		{
			buffer.append("in template ");
			buffer.append(FunctionRepr.call(name));
		}
		buffer.append(": offset ");
		buffer.append(node.getStartPos());
		buffer.append(":");
		buffer.append(node.getEndPos());
		buffer.append("\n");
		buffer.append(node.getSnippet().toString());
		return buffer.toString();
	}

	public ASTException(Throwable cause, InterpretedTemplate template, AST node)
	{
		super(makeMessage(template, node), cause);
	}
}
