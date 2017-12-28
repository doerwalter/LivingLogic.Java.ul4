/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public interface BlockLike extends SourcePart
{
	String getType();

	void append(AST item);

	void finish(Tag endtag);

	IndentAST popTrailingIndent();

	/**
	 * Return whether this block can handle a {@code break} oder {@code continue} tag ({@code true})
	 * or whether the decision should be delegated to the parent block ({@code false}).
	 * Returns {@code true} for {@code for} and {@code while} blocks and
	 * {@code false} for {@code if}/{@code elif}/{@code else}.
	 * For {@code InterpretedTemplate} and {code RenderBlockAST} an exception is thrown.
	 */
	boolean handleLoopControl(String name);
}