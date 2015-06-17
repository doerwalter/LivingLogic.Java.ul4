/*
** Copyright 2013-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

public interface UL4Attributes extends UL4GetItemString
{
	/**
	 * Return the set of attribute names of this object that are available to UL4.
	 *
	 * @return a {@link java.util.Set} of attribute names.
	 */
	Set<String> getAttributeNamesUL4();
}
