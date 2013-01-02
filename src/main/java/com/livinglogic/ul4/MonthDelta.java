/*
** Copyright 2012-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MonthDelta implements Comparable, UL4Bool, UL4Repr, UL4Type, UL4Abs
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

	public boolean boolUL4()
	{
		return months != 0;
	}

	public String reprUL4()
	{
		StringBuilder buffer = new StringBuilder();

		buffer.append("monthdelta(");
		if (months != 0)
			buffer.append(months);
		buffer.append(")");
		return buffer.toString();
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
}
