/*
** Copyright 2012-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * <p>An object with attributes that should be accessible to UL4.</p>
 *
 * <p>These attributes can either be normal "data attributes" or they can be
 * methods. To implement a method define a subclass of {@link BoundMethod} and
 * return an instance of it in {@link #getAttrUL4}.
 */
public interface UL4GetAttr
{
	/**
	 * Return an attribute of this object to UL4.
	 *
	 * If the object doesn't have an attribute of that name, an
	 * {@link AttributeException} must be thrown.
	 *
	 * @param key The name of the requested attribute.
	 * @return the value of the requested attribute.
	 * @throws AttributeException if the requested attribute doesn't exist.
	 */
	Object getAttrUL4(String key);
}
