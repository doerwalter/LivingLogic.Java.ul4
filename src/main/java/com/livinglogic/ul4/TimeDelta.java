/*
** Copyright 2012-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.livinglogic.utils.SetUtils.makeSet;

public class TimeDelta implements UL4Instance, Comparable, UL4Bool, UL4Repr, UL4Abs, UL4GetAttr, UL4Dir
{
	protected static class Type extends AbstractInstanceType
	{
		@Override
		public String getNameUL4()
		{
			return "TimeDelta";
		}

		@Override
		public String getDoc()
		{
			return "A time span (days/seconds/microseconds).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof TimeDelta;
		}

		private static final Signature signature = new Signature("days", 0, "seconds", 0, "microseconds", 0);

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		@Override
		public Object create(BoundArguments args)
		{
			Object days = args.get(0);
			Object seconds = args.get(1);
			Object microseconds = args.get(2);

			if (days instanceof Float || days instanceof Double || seconds instanceof Float || seconds instanceof Double || microseconds instanceof Float || microseconds instanceof Double)
				return new TimeDelta(Utils.toDouble(days), Utils.toDouble(seconds), Utils.toDouble(microseconds));
			else
				return new TimeDelta(Utils.toInt(days), Utils.toLong(seconds), Utils.toLong(microseconds));
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

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

	public int compareTo(Object other)
	{
		int temp;

		temp = Utils.cmp(days, ((TimeDelta)other).days);
		if (temp == 0)
			temp = Utils.cmp(seconds, ((TimeDelta)other).seconds);
		if (temp == 0)
			temp = Utils.cmp(microseconds, ((TimeDelta)other).microseconds);
		return temp;
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

	public LocalDate addTo(LocalDate date)
	{
		return date.plusDays(days);
	}

	public LocalDateTime addTo(LocalDateTime date)
	{
		return date.plusDays(days).plusSeconds(seconds).plusNanos(microseconds * 1000);
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

	public LocalDate subtractFrom(LocalDate date)
	{
		return date.minusDays(days);
	}

	public LocalDateTime subtractFrom(LocalDateTime date)
	{
		return date.minusDays(days).minusSeconds(seconds).minusNanos(microseconds * 1000);
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
		if (divisor == 0)
			throw new ArithmeticException("division by zero");
		return new TimeDelta(
			days/((double)divisor),
			seconds/((double)divisor),
			microseconds/((double)divisor)
		);
	}

	public TimeDelta truediv(long divisor)
	{
		if (divisor == 0)
			throw new ArithmeticException("division by zero");
		return new TimeDelta(
			days/((double)divisor),
			seconds/((double)divisor),
			microseconds/((double)divisor)
		);
	}

	public TimeDelta truediv(float divisor)
	{
		if (divisor == 0.0)
			throw new ArithmeticException("division by zero");
		return new TimeDelta(
			days/divisor,
			seconds/divisor,
			microseconds/divisor
		);
	}

	public TimeDelta truediv(double divisor)
	{
		if (divisor == 0.0)
			throw new ArithmeticException("division by zero");
		return new TimeDelta(
			days/divisor,
			seconds/divisor,
			microseconds/divisor
		);
	}

	public double truediv(TimeDelta divisor)
	{
		if (!divisor.boolUL4())
			throw new ArithmeticException("division by zero");
		double myValue = days;
		double divisorValue = divisor.getDays();
		boolean hasSeconds = seconds != 0 || divisor.getSeconds() != 0;
		boolean hasMicroseconds = microseconds != 0 || divisor.getMicroseconds() != 0;
		if (hasSeconds || hasMicroseconds)
		{
			myValue = myValue*86400+seconds;
			divisorValue = divisorValue*86400 + divisor.getSeconds();
			if (hasMicroseconds)
			{
				myValue = myValue * 1000000 + microseconds;
				divisorValue = divisorValue * 1000000 + divisor.getMicroseconds();
			}
		}
		return myValue/divisorValue;
	}

	public TimeDelta truediv(BigInteger divisor)
	{
		if (divisor.compareTo(BigInteger.ZERO) == 0)
			throw new ArithmeticException("division by zero");
		BigDecimal decimalDivisor = new BigDecimal(divisor);
		BigDecimal resultDays = Utils.toBigDecimal(days).divide(decimalDivisor);
		BigDecimal resultSeconds = Utils.toBigDecimal(seconds).divide(decimalDivisor);
		BigDecimal resultMicroseconds = Utils.toBigDecimal(microseconds).divide(decimalDivisor);
		return new TimeDelta(
			resultDays.doubleValue(),
			resultSeconds.doubleValue(),
			resultMicroseconds.doubleValue()
		);
	}

	public TimeDelta truediv(BigDecimal divisor)
	{
		if (divisor.compareTo(BigDecimal.ZERO) == 0)
			throw new ArithmeticException("division by zero");
		BigDecimal resultDays = Utils.toBigDecimal(days).divide(divisor);
		BigDecimal resultSeconds = Utils.toBigDecimal(seconds).divide(divisor);
		BigDecimal resultMicroseconds = Utils.toBigDecimal(microseconds).divide(divisor);
		return new TimeDelta(
			resultDays.doubleValue(),
			resultSeconds.doubleValue(),
			resultMicroseconds.doubleValue()
		);
	}

	public TimeDelta floordiv(int divisor)
	{
		if (divisor == 0)
			throw new ArithmeticException("division by zero");
		return new TimeDelta(0, 0, totalMicroseconds()/divisor);
	}

	public TimeDelta floordiv(long divisor)
	{
		if (divisor == 0)
			throw new ArithmeticException("division by zero");
		return new TimeDelta(0, 0, totalMicroseconds()/divisor);
	}

	public TimeDelta floordiv(BigInteger divisor)
	{
		if (divisor.compareTo(BigInteger.ZERO) == 0)
			throw new ArithmeticException("division by zero");
		BigInteger resultMicroSeconds = Utils.toBigInteger(totalMicroseconds()).divide(divisor);
		return new TimeDelta(0, 0, resultMicroSeconds.longValueExact());
	}

	public TimeDelta floordiv(BigDecimal divisor)
	{
		if (divisor.compareTo(BigDecimal.ZERO) == 0)
			throw new ArithmeticException("division by zero");
		BigInteger resultMicroSeconds = Utils.toBigDecimal(totalMicroseconds()).divideToIntegralValue(divisor).toBigInteger();
		return new TimeDelta(0, 0, resultMicroSeconds.longValueExact());
	}

	public long floordiv(TimeDelta divisor)
	{
		return totalMicroseconds()/divisor.totalMicroseconds();
	}

	public boolean boolUL4()
	{
		return days != 0 || seconds != 0 || microseconds != 0;
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("timedelta(");
		boolean first = true;
		if (days != 0)
		{
			formatter.append("days=");
			formatter.visit(days);
			first = false;
		}
		if (seconds != 0)
		{
			if (!first)
				formatter.append(", ");
			formatter.append("seconds=");
			formatter.visit(seconds);
			first = false;
		}
		if (microseconds != 0)
		{
			if (!first)
				formatter.append(", ");
			formatter.append("microseconds=");
			formatter.visit(microseconds);
		}
		formatter.append(")");
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

	public String getTypeNameUL4()
	{
		return "timedelta";
	}

	public TimeDelta absUL4()
	{
		return days < 0 ? new TimeDelta(-days, -seconds, -microseconds) : this;
	}

	public long totalSeconds()
	{
		return (24*60*60) * days + seconds;
	}

	public long totalMicroseconds()
	{
		return totalSeconds() * 1000000 + microseconds;
	}

	private static class BoundMethodDays extends BoundMethod<TimeDelta>
	{
		public BoundMethodDays(TimeDelta object)
		{
			super(object);
		}

		@Override
		public String getNameUL4()
		{
			return "days";
		}

		@Override
		public Object evaluate(BoundArguments args)
		{
			return object.days;
		}
	}

	private static class BoundMethodSeconds extends BoundMethod<TimeDelta>
	{
		public BoundMethodSeconds(TimeDelta object)
		{
			super(object);
		}

		@Override
		public String getNameUL4()
		{
			return "seconds";
		}

		@Override
		public Object evaluate(BoundArguments args)
		{
			return object.seconds;
		}
	}

	private static class BoundMethodMicroseconds extends BoundMethod<TimeDelta>
	{
		public BoundMethodMicroseconds(TimeDelta object)
		{
			super(object);
		}

		@Override
		public String getNameUL4()
		{
			return "microseconds";
		}

		@Override
		public Object evaluate(BoundArguments args)
		{
			return object.microseconds;
		}
	}

	protected static Set<String> attributes = makeSet("days", "seconds", "microseonds");

	public Set<String> dirUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "days":
				return new BoundMethodDays(this);
			case "seconds":
				return new BoundMethodSeconds(this);
			case "microseconds":
				return new BoundMethodMicroseconds(this);
			default:
				throw new AttributeException(this, key);
		}
	}
}
