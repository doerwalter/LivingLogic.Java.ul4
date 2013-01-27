/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class CodeException extends RuntimeException
{
	protected InterpretedCode code;

	public CodeException(Throwable cause, InterpretedCode code)
	{
		super(code.nameUL4() != null ? "in " + code.getType() + " named " + code.nameUL4() : "in unnamed " + code.getType(), cause);
		this.code = code;
	}
}
