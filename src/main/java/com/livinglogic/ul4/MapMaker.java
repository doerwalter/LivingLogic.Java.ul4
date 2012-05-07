/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.HashMap;


/**
 * Utility class for creating a Map object
 *
 * @author W. Dörwald, A. Gaßner
 */
public class MapMaker
{
	private Map map = new HashMap();

	public MapMaker add(Object key, Object value)
	{
		map.put(key, value);
		return this;
	}

	public MapMaker add(Map map)
	{
		this.map.putAll(map);
		return this;
	}

	public Map getMap()
	{
		return map;
	}
}
