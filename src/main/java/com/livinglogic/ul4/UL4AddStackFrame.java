/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Marker interface for adding objects to the UL4 stack trace.

When UL4 calls or renders an object and this raises an exception an UL4 stack frame will only
be added when the called/rendered object is an instance of UL4AddStackFrame.
**/
public interface UL4AddStackFrame
{
}
