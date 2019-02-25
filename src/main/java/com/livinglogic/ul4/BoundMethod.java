/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

/**
 * <p>A {@code BoundMethod} object is a callable that can be returned from
 * {@link UL4GetAttr#getAttrUL4} or
 * {@link UL4GetAttrWithContext#getAttrWithContextUL4} to implement
 * an object method that can be called from UL4.</p>
 *
 * <p>{@code BoundMethod} is abstract. Subclasses must implemented the method
 * {@link Function#evaluate}. Also when the method requires arguments
 * {@link Function#getSignature} must be overwritten.</p>
 */
public abstract class BoundMethod<T> extends Function
{
	protected T object = null;

	/**
	 * Return a new {@code BoundMethod} object bound to {@code object}.
	 *
	 * @param object The object this new {@code BoundMethod} is bound to.
	 */
	public BoundMethod(T object)
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
