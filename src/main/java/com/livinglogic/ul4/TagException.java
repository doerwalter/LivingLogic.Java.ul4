/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class TagException extends RuntimeException
{
	protected Tag tag;

	public TagException(Throwable cause, Tag tag)
	{
		super("in tag " + tag, cause);
		this.tag = tag;
	}
}
