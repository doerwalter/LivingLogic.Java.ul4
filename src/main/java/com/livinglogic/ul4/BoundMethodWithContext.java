/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

/**
 * <p>A {@code BoundMethodWithContext} object is a callable that can be returned
 * from {@link UL4GetItemStringWithContext#getItemStringWithContextUL4} to
 * implement an object method that can be called from UL4. It is similar to
 * {@link BoundMethod} except that {@link #evaluate} gets passed the context in
 * addition to the arguments.</p>
 *
 * <p>{@code BoundMethod} is abstract. Subclasses must implemented the method
 * {@link Function#evaluate}. Also when the method requires arguments
 * {@link Function#getSignature} must be overwritten.</p>
 */

public abstract class BoundMethodWithContext<T> extends FunctionWithContext
{
	protected T object = null;

	public BoundMethodWithContext(T object)
	{
		this.object = object;
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<method ");
		formatter.append(nameUL4());
		formatter.append(" of ");
		formatter.append(Utils.objectType(object));
		formatter.append(" object>");
	}
}
