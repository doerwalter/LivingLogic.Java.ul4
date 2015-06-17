/*
** Copyright 2012-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public interface UL4GetItemString
{
	/**
	 * Return an attribute of this object to UL4.
	 *
	 * If the object doesn't have an attribute of that name, an
	 * {@link UndefinedKey} object must be returned.
	 *
	 * @param key The name of the requested attribute.
	 * @return the requested attribute or an {@link UndefinedKey} object.
	 */
	Object getItemStringUL4(String key);
}
