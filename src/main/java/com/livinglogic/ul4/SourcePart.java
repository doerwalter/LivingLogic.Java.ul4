/*
** Copyright 2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/*
 * A {@code SourcePart} object references a slice of the source code of a template.
 */
public interface SourcePart
{
	InterpretedTemplate getTemplate();

	int getStartPos();

	int getEndPos();

	CodeSnippet getSnippet();
}
