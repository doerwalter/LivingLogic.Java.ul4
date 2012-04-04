/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public abstract class AST
{
	abstract public int compile(InterpretedTemplate template, Registers registers, Location location);
}
