/*
** Copyright 2012-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.Map;

import static com.livinglogic.utils.SetUtils.makeSet;


public class MonthDelta implements Comparable, UL4Instance, UL4Bool, UL4Repr, UL4Abs, UL4Dir
{
	protected static class Type extends AbstractInstanceType
	{
		@Override
		public String getNameUL4()
		{
			return "monthdelta";
		}

		@Override
		public String getDoc()
		{
			return "A time span of a number of months.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof MonthDelta;
		}

		private static final Signature signature = new Signature().addPositionalOnly("months", 0);

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		@Override
		public Object create(EvaluationContext context, BoundArguments args)
		{
			return new MonthDelta(args.getInt(0));
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

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

	public LocalDate addTo(LocalDate date)
	{
		return date.plusMonths(months);
	}

	public LocalDateTime addTo(LocalDateTime date)
	{
		return date.plusMonths(months);
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

	public LocalDate subtractFrom(LocalDate date)
	{
		return date.minusMonths(months);
	}

	public LocalDateTime subtractFrom(LocalDateTime date)
	{
		return date.minusMonths(months);
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

	public int floordiv(MonthDelta divisor)
	{
		return months/divisor.getMonths();
	}

	public double truediv(MonthDelta divisor)
	{
		return (double)months/divisor.getMonths();
	}

	@Override
	public boolean boolUL4(EvaluationContext context)
	{
		return months != 0;
	}

	@Override
	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("monthdelta(");
		if (months != 0)
			formatter.visit(months);
		formatter.append(")");
	}

	@Override
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();

		buffer.append(months);
		buffer.append(" month");
		if ((months != 1) && (months != -1))
			buffer.append("s");
		return buffer.toString();
	}

	public String getTypeNameUL4()
	{
		return "monthdelta";
	}

	@Override
	public MonthDelta absUL4(EvaluationContext context)
	{
		return months < 0 ? new MonthDelta(-months) : this;
	}

	private static final MethodDescriptor<MonthDelta> methodMonths = new MethodDescriptor<MonthDelta>(type, "months", Signature.noParameters);

	protected static Set<String> attributes = makeSet("months");

	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		switch (key)
		{
			case "months":
				return methodMonths.bindMethod(this);
			default:
				return UL4Instance.super.getAttrUL4(context, key);
		}
	}

	@Override
	public Object callAttrUL4(EvaluationContext context, String key, List<Object> args, Map<String, Object> kwargs)
	{
		switch (key)
		{
			case "months":
				BoundArguments boundMonthsArgs = methodMonths.bindArguments(args, kwargs);
				return months;
			default:
				return UL4Instance.super.callAttrUL4(context, key, args, kwargs);
		}
	}
}
