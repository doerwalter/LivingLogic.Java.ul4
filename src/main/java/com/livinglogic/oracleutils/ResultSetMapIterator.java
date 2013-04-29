/*
** Copyright 2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.oracleutils;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import oracle.sql.DATE;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;


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
				Map<String, Object> record = new HashMap<String, Object>();

				for (int i = 1; i <= numberOfColumns; ++i)
				{
					String key = metaData.getColumnLabel(i);
					Object value = resultSet.getObject(i);

					if (value instanceof DATE)
						value = resultSet.getDate(i);
					else if (value instanceof TIMESTAMP || value instanceof TIMESTAMPLTZ || value instanceof TIMESTAMPTZ)
						value = resultSet.getTimestamp(i);
					else if (value instanceof Clob)
					{
						Clob clob = (Clob)value;
						value = clob.getSubString(1L, (int)clob.length());
					}
					else if (value instanceof BigDecimal)
					{
						if (metaData.getScale(i) <= 0)
							value = ((BigDecimal)value).toBigInteger();
					}
					record.put(key, value);
				}
				nextRecord = record;
			}
			else
				nextRecord = null;
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
