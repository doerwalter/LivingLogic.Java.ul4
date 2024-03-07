/*
** Copyright 2013-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.dbutils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.livinglogic.ul4.Utils;

public class ResultSetMapIterator implements Iterator<Map<String, Object>>
{
	private ResultSet resultSet;
	private ResultSetMetaData metaData;
	private int numberOfColumns;
	Map<String, Object> nextRecord;

	public ResultSetMapIterator(ResultSet resultSet)
	{
		this.resultSet = resultSet;
		try
		{
			metaData = resultSet.getMetaData();
			numberOfColumns = metaData.getColumnCount();
		}
		catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		nextRecord = null;
		fetch();
	}

	public boolean hasNext()
	{
		return nextRecord != null;
	}

	public Map<String, Object> next()
	{
		Map<String, Object> result = nextRecord;
		fetch();
		return result;
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	private void fetch()
	{
		try
		{
			if (resultSet.next())
			{
				nextRecord = Connection.makeRecord(resultSet, metaData);
			}
			else
			{
				nextRecord = null;
				resultSet.close();
			}
		}
		catch (RuntimeException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
