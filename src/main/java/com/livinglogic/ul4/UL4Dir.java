/*
** Copyright 2013-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

public interface UL4Dir
{
	/**
	 * Return the set of attribute names of this object that are available to UL4.
	 *
	 * @return a {@link java.util.Set} of attribute names.
	 */
	Set<String> dirUL4();
}
