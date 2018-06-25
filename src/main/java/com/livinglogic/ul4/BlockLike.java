/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * A {@code BlockLike} object behaves like a block,
 * i.e. like {@see BlockAST}, but we have two special cases
 * where the block itself has a nested structure:
 *
 * {@see ConditionalBlocks} itself contains the {@code if}, {@code else} and
 * {@code elif} blocks, which are themselves block. Appending to the conditional
 * block appends to the last {@code if}/{@code else}/{@code elif} block.
 *
 * {@see RenderBlockAST} itself contains the content template. Appending to the
 * {@class RenderBlockAST} object appends to the template itself.
 */
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
