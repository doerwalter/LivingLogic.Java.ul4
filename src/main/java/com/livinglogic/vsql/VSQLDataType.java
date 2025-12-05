/*
** Copyright 2019-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Set;
import java.util.Collections;
import java.sql.Timestamp;
import java.sql.Clob;
import java.sql.SQLException;
import java.math.BigDecimal;

import com.livinglogic.ul4.Utils;
import com.livinglogic.ul4.EnumValueException;

/**
An enum that specifies the various data types available in vSQL.
**/
public enum VSQLDataType
{
	NULL
	{
		@Override
		public String toString()
		{
			return "null";
		}
	},
	BOOL
	{
		@Override
		public String toString()
		{
			return "bool";
		}

		public Object conform(Object value)
		{
			if (value instanceof BigDecimal bigDecimalValue)
			{
				value = bigDecimalValue.signum() != 0;
			}
			return value;
		}

		@Override
		public Set<VSQLAggregate> getAllowedAggregates()
		{
			return Set.of(VSQLAggregate.GROUP, VSQLAggregate.MIN, VSQLAggregate.MAX, VSQLAggregate.SUM);
		}
	},
	INT
	{
		@Override
		public String toString()
		{
			return "int";
		}

		public Object conform(Object value)
		{
			if (value instanceof BigDecimal bigDecimalValue)
			{
				value = Utils.narrowBigInteger(bigDecimalValue.toBigInteger());
			}
			return value;
		}

		@Override
		public Set<VSQLAggregate> getAllowedAggregates()
		{
			return Set.of(VSQLAggregate.GROUP, VSQLAggregate.MIN, VSQLAggregate.MAX, VSQLAggregate.SUM);
		}
	},
	NUMBER
	{
		@Override
		public String toString()
		{
			return "number";
		}

		public Object conform(Object value)
		{
			if (value instanceof BigDecimal bigDecimalValue)
			{
				value = Utils.narrowBigDecimal(bigDecimalValue);
			}
			return value;
		}

		@Override
		public Set<VSQLAggregate> getAllowedAggregates()
		{
			return Set.of(VSQLAggregate.GROUP, VSQLAggregate.MIN, VSQLAggregate.MAX, VSQLAggregate.SUM);
		}
	},
	STR
	{
		@Override
		public String toString()
		{
			return "str";
		}

		public Object conform(Object value)
		{
			return clobToString(value);
		}

		@Override
		public Set<VSQLAggregate> getAllowedAggregates()
		{
			return Set.of(VSQLAggregate.GROUP, VSQLAggregate.MIN, VSQLAggregate.MAX);
		}
	},
	CLOB
	{
		@Override
		public String toString()
		{
			return "clob";
		}

		public Object conform(Object value)
		{
			return clobToString(value);
		}
	},
	COLOR
	{
		@Override
		public String toString()
		{
			return "color";
		}

		@Override
		public Set<VSQLAggregate> getAllowedAggregates()
		{
			return Set.of(VSQLAggregate.GROUP);
		}
	},
	GEO
	{
		@Override
		public String toString()
		{
			return "geo";
		}
	},
	DATE
	{
		@Override
		public String toString()
		{
			return "date";
		}

		public Object conform(Object value)
		{
			if (value instanceof Timestamp timestampValue)
			{
				value = timestampValue.toLocalDateTime().toLocalDate();
			}
			return value;
		}

		@Override
		public Set<VSQLAggregate> getAllowedAggregates()
		{
			return Set.of(VSQLAggregate.GROUP, VSQLAggregate.MIN, VSQLAggregate.MAX);
		}
	},
	DATETIME
	{
		@Override
		public String toString()
		{
			return "datetime";
		}

		public Object conform(Object value)
		{
			if (value instanceof Timestamp timestampValue)
			{
				value = timestampValue.toLocalDateTime();
			}
			return value;
		}

		@Override
		public Set<VSQLAggregate> getAllowedAggregates()
		{
			return Set.of(VSQLAggregate.GROUP, VSQLAggregate.MIN, VSQLAggregate.MAX);
		}
	},
	DATEDELTA
	{
		@Override
		public String toString()
		{
			return "datedelta";
		}

		@Override
		public Set<VSQLAggregate> getAllowedAggregates()
		{
			return Set.of(VSQLAggregate.GROUP, VSQLAggregate.MIN, VSQLAggregate.MAX, VSQLAggregate.SUM);
		}
	},
	DATETIMEDELTA
	{
		@Override
		public String toString()
		{
			return "datetimedelta";
		}

		@Override
		public Set<VSQLAggregate> getAllowedAggregates()
		{
			return Set.of(VSQLAggregate.GROUP, VSQLAggregate.MIN, VSQLAggregate.MAX, VSQLAggregate.SUM);
		}
	},
	MONTHDELTA
	{
		@Override
		public String toString()
		{
			return "monthdelta";
		}

		@Override
		public Set<VSQLAggregate> getAllowedAggregates()
		{
			return Set.of(VSQLAggregate.GROUP, VSQLAggregate.MIN, VSQLAggregate.MAX, VSQLAggregate.SUM);
		}
	},
	NULLLIST
	{
		@Override
		public String toString()
		{
			return "nulllist";
		}
	},
	INTLIST
	{
		@Override
		public String toString()
		{
			return "intlist";
		}
	},
	NUMBERLIST
	{
		@Override
		public String toString()
		{
			return "numberlist";
		}
	},
	STRLIST
	{
		@Override
		public String toString()
		{
			return "strlist";
		}
	},
	CLOBLIST
	{
		@Override
		public String toString()
		{
			return "cloblist";
		}
	},
	DATELIST
	{
		@Override
		public String toString()
		{
			return "datelist";
		}
	},
	DATETIMELIST
	{
		@Override
		public String toString()
		{
			return "datetimelist";
		}
	},
	INTSET
	{
		@Override
		public String toString()
		{
			return "intset";
		}
	},
	NULLSET
	{
		@Override
		public String toString()
		{
			return "nullset";
		}
	},
	NUMBERSET
	{
		@Override
		public String toString()
		{
			return "numberset";
		}
	},
	STRSET
	{
		@Override
		public String toString()
		{
			return "strset";
		}
	},
	DATESET
	{
		@Override
		public String toString()
		{
			return "dateset";
		}
	},
	DATETIMESET
	{
		@Override
		public String toString()
		{
			return "datetimeset";
		}
	};

	public String toString()
	{
		return null;
	}

	public Set<VSQLAggregate> getAllowedAggregates()
	{
		return Collections.emptySet();
	}

	/**
	Convert to the canonical type excepted for that datatype.

	E.g. for `DATE` convert `java.sql.Timestamp` and `java.util.Date` to `java.date.LocalDate`.
	**/
	public Object conform(Object value)
	{
		return value;
	}

	public static VSQLDataType fromString(String value)
	{
		if (value == null)
			return null;
		switch (value)
		{
			case "null":
				return NULL;
			case "bool":
				return BOOL;
			case "int":
				return INT;
			case "number":
				return NUMBER;
			case "str":
				return STR;
			case "clob":
				return CLOB;
			case "color":
				return COLOR;
			case "geo":
				return GEO;
			case "date":
				return DATE;
			case "datetime":
				return DATETIME;
			case "datedelta":
				return DATEDELTA;
			case "datetimedelta":
				return DATETIMEDELTA;
			case "monthdelta":
				return MONTHDELTA;
			case "intlist":
				return INTLIST;
			case "nulllist":
				return NULLLIST;
			case "numberlist":
				return NUMBERLIST;
			case "strlist":
				return STRLIST;
			case "cloblist":
				return CLOBLIST;
			case "datelist":
				return DATELIST;
			case "datetimelist":
				return DATETIMELIST;
			case "nullset":
				return NULLSET;
			case "intset":
				return INTSET;
			case "numberset":
				return NUMBERSET;
			case "strset":
				return STRSET;
			case "dateset":
				return DATESET;
			case "datetimeset":
				return DATETIMESET;
		}
		throw new EnumValueException("com.livinglogic.livingapps.vsql.VSQLDataType", value);
	}

	private static Object clobToString(Object value)
	{
		if (value instanceof Clob clobValue)
		{
			try
			{
				value = clobValue.getSubString(1, (int)clobValue.length());
				clobValue.free();
			}
			catch (SQLException ex)
			{
				throw new RuntimeException(ex);
			}
		}
		return value;
	}
};
