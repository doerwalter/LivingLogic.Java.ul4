/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.dbutils;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Map;


public class IterableResultSet implements Iterable<Map<String, Object>>
{
	private ResultSet resultSet;

	public IterableResultSet(ResultSet resultSet)
	{
		this.resultSet = resultSet;
	}

	public Iterator<Map<String, Object>> iterator()
	{
		return new ResultSetMapIterator(resultSet);
	}
}
