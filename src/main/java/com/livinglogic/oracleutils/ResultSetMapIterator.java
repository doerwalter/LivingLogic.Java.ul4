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
import static java.sql.Types.*;


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
					int type = metaData.getColumnType(i);
					Object value;
					if (type == DATE)
						value = resultSet.getDate(i);
					else if (type == TIMESTAMP
					         // Don't use the constants from oracle.jdbc.OracleTypes, so that we don't require ojdbc.jar
					         || type == -102 // oracle.jdbc.OracleTypes.TIMESTAMPLTZ
					         || type == -100 // oracle.jdbc.OracleTypes.TIMESTAMPNS
					         || type == -101) // oracle.jdbc.OracleTypes.TIMESTAMPTZ
						value = resultSet.getTimestamp(i);
					else
					{
						value = resultSet.getObject(i);
						if (value instanceof Clob)
							value = ((Clob)value).getSubString(1L, (int)((Clob)value).length());
						else if (value instanceof BigDecimal)
						{
							if (metaData.getScale(i) <= 0)
								value = ((BigDecimal)value).toBigInteger();
						}
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
