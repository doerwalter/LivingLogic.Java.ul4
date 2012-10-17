/*
** Copyright 2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeDelta
{
	private int days;
	private int seconds;
	private int microseconds;

	public TimeDelta()
	{
		this(0, 0, 0);
	}

	public TimeDelta(int days)
	{
		this(days, 0, 0);
	}

	public TimeDelta(int days, long seconds)
	{
		this(days, seconds, 0);
	}

	public TimeDelta(int days, long seconds, long microseconds)
	{
		long microseconds_div = microseconds / 1000000;
		microseconds = microseconds % 1000000;

		if (microseconds < 0)
		{
			microseconds += 1000000;
			--microseconds_div;
		}
		seconds += microseconds_div;
		this.microseconds = (int)microseconds;

		long seconds_div = seconds / (24*60*60);
		seconds = seconds % (24*60*60);

		if (seconds < 0)
		{
			seconds += (24*60*60);
			--seconds_div;
		}
		days += seconds_div;
		this.seconds = (int)seconds;

		this.days = days;
	}

	public TimeDelta(double days, double seconds, double microseconds)
	{
		this((int)days, (long)((days % 1.0)*24*60*60+seconds), (long)((days%(1./(24*60*60)))*24*60*60*1000000L+(seconds%1.0)*1000000+microseconds));
	}

	public boolean equals(Object other)
	{
		if (other == null)
			return false;
		if (!(other instanceof TimeDelta))
			return false;
		return days == ((TimeDelta)other).days && seconds == ((TimeDelta)other).seconds && microseconds == ((TimeDelta)other).microseconds;
	}

	public int hashCode()
	{
		return days ^ seconds ^ microseconds;
	}

	public int getDays()
	{
		return days;
	}

	public int getSeconds()
	{
		return seconds;
	}

	public int getMicroseconds()
	{
		return microseconds;
	}

	public TimeDelta add(TimeDelta other)
	{
		return new TimeDelta(
			days+other.getDays(),
			seconds+other.getSeconds(),
			microseconds+other.getMicroseconds()
		);
	}

	public Date addTo(Date date)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		calendar.add(Calendar.SECOND, seconds);
		calendar.add(Calendar.MILLISECOND, microseconds/1000);
		return calendar.getTime();
	}

	public TimeDelta subtract(TimeDelta other)
	{
		return new TimeDelta(
			days-other.getDays(),
			seconds-other.getSeconds(),
			microseconds-other.getMicroseconds()
		);
	}

	public Date subtractFrom(Date date)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -days);
		calendar.add(Calendar.SECOND, -seconds);
		calendar.add(Calendar.MILLISECOND, -microseconds/1000);
		return calendar.getTime();
	}

	public TimeDelta negate()
	{
		return new TimeDelta(-days, -seconds, -microseconds);
	}

	public TimeDelta mul(int factor)
	{
		return new TimeDelta(
			factor*days,
			factor*seconds,
			factor*microseconds
		);
	}

	public TimeDelta mul(long factor)
	{
		return new TimeDelta(
			(int)(factor*days),
			(int)(factor*seconds),
			(int)(factor*microseconds)
		);
	}

	public TimeDelta mul(double factor)
	{
		return new TimeDelta(
			factor*days,
			factor*seconds,
			factor*microseconds
		);
	}

	public TimeDelta truediv(int divisor)
	{
		return new TimeDelta(
			days/((double)divisor),
			seconds/((double)divisor),
			microseconds/((double)divisor)
		);
	}

	public TimeDelta truediv(long divisor)
	{
		return new TimeDelta(
			days/((double)divisor),
			seconds/((double)divisor),
			microseconds/((double)divisor)
		);
	}

	public TimeDelta truediv(float divisor)
	{
		return new TimeDelta(
			days/divisor,
			seconds/divisor,
			microseconds/divisor
		);
	}

	public TimeDelta truediv(double divisor)
	{
		return new TimeDelta(
			days/divisor,
			seconds/divisor,
			microseconds/divisor
		);
	}

	public TimeDelta floordiv(int divisor)
	{
		return new TimeDelta(
			days/((double)divisor),
			seconds/((double)divisor),
			microseconds/((double)divisor)
		);
	}

	public TimeDelta floordiv(long divisor)
	{
		return new TimeDelta(
			days/((double)divisor),
			seconds/((double)divisor),
			microseconds/((double)divisor)
		);
	}

	public String repr()
	{
		StringBuilder buffer = new StringBuilder();

		buffer.append("timedelta(");
		if (days != 0 || seconds != 0 || microseconds != 0)
		{
			buffer.append(days);
			if (seconds != 0 || microseconds != 0)
			{
				buffer.append(", ");
				buffer.append(seconds);
				if (microseconds != 0)
				{
					buffer.append(", ");
					buffer.append(microseconds);
				}
			}
		}
		buffer.append(")");
		return buffer.toString();
	}

	private static DecimalFormat twodigits = new DecimalFormat("00");
	private static DecimalFormat sixdigits = new DecimalFormat("000000");

	public String toString()
	{
		StringBuilder buffer = new StringBuilder();

		if (days != 0)
		{
			buffer.append(days);
			buffer.append(" day");
			if ((days != 1) && (days != -1))
				buffer.append("s");
			buffer.append(", ");
		}
		int ss = seconds%60;
		int mm = seconds/60;
		int hh = mm/60;
		    mm = mm%60;

		buffer.append(hh);
		buffer.append(":");
		buffer.append(twodigits.format(mm));
		buffer.append(":");
		buffer.append(twodigits.format(ss));

		if (microseconds != 0)
		{
			buffer.append(".");
			buffer.append(sixdigits.format(microseconds));
		}
		return buffer.toString();
	}
}
