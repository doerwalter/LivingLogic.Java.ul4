/*
** Copyright 2012-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;

/**
 * A {@code DecoderException} is raised when on UL4ON stream is broken.
 */
public class DecoderException extends RuntimeException
{
	public DecoderException(int position, String message)
	{
		super(message + " (at stream position " + position + ")");
	}
}
