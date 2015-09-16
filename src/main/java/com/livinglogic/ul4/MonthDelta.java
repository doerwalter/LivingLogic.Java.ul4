/*
** Copyright 2012-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.Map;

import static com.livinglogic.utils.SetUtils.makeSet;

public class MonthDelta implements Comparable, UL4Bool, UL4Repr, UL4Type, UL4Abs, UL4GetItemString, UL4Attributes
{
	private int months;

	public MonthDelta()
	{
		this.months = 0;
	}

	public MonthDelta(int months)
	{
		this.months = months;
	}

	public boolean equals(Object other)
	{
		if (other == null)
			return false;
		if (!(other instanceof MonthDelta))
			return false;
		return months == ((MonthDelta)other).months;
	}

	public int hashCode()
	{
		return months;
	}

	public int compareTo(Object other)
	{
		return Utils.cmp(months, ((MonthDelta)other).months);
	}

	public int getMonths()
	{
		return months;
	}

	public MonthDelta add(MonthDelta other)
	{
		return new MonthDelta(months+other.getMonths());
	}

	public Date addTo(Date date)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}

	public MonthDelta subtract(MonthDelta other)
	{
		return new MonthDelta(months-other.getMonths());
	}

	public Date subtractFrom(Date date)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -months);
		return calendar.getTime();
	}

	public MonthDelta negate()
	{
		return new MonthDelta(-months);
	}

	public MonthDelta mul(int factor)
	{
		return new MonthDelta(factor*months);
	}

	public MonthDelta mul(long factor)
	{
		return new MonthDelta((int)(factor*months));
	}

	public MonthDelta floordiv(int divisor)
	{
		return new MonthDelta(months/divisor);
	}

	public MonthDelta floordiv(long divisor)
	{
		return new MonthDelta((int)(months/divisor));
	}

	public double truediv(MonthDelta divisor)
	{
		return (double)months/divisor.getMonths();
	}

	public int floordiv(MonthDelta divisor)
	{
		return months/divisor.getMonths();
	}

	public boolean boolUL4()
	{
		return months != 0;
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("monthdelta(");
		if (months != 0)
			formatter.visit(months);
		formatter.append(")");
	}

	public String toString()
	{
		StringBuilder buffer = new StringBuilder();

		buffer.append(months);
		buffer.append(" month");
		if ((months != 1) && (months != -1))
			buffer.append("s");
		return buffer.toString();
	}

	public String typeUL4()
	{
		return "monthdelta";
	}

	public MonthDelta absUL4()
	{
		return months < 0 ? new MonthDelta(-months) : this;
	}

	private static class BoundMethodMonths extends BoundMethod<MonthDelta>
	{
		public BoundMethodMonths(MonthDelta object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "monthdelta.months";
		}

		public Object evaluate(BoundArguments args)
		{
			return object.months;
		}
	}

	protected static Set<String> attributes = makeSet("months");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("months".equals(key))
			return new BoundMethodMonths(this);
		else
			return new UndefinedKey(key);
	}
}
