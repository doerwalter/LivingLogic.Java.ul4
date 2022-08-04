/*
** Copyright 2013-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Thrown by {@link RenderAST} if the object is not renderable.
**/
public class NotRenderableException extends UnsupportedOperationException
{
	public NotRenderableException(Object obj)
	{
		super(Utils.objectType(obj) + " is not renderable!");
	}
}
