/*
** Copyright 2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/*
 * A {@code SourcePart} object references a slice of a source code string.
 */
public interface SourcePart
{
	String getSource();

	int getStartPos();

	int getEndPos();

	CodeSnippet getSnippet();
}
