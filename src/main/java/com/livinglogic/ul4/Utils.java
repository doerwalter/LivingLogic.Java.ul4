/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;
import java.util.Set;
import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.MathContext;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URLDecoder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;


class StringIterator implements Iterator<String>
{
	String string;

	int stringSize;

	int index;

	public StringIterator(String string)
	{
		this.string = string;
		stringSize = string.length();
		index = 0;
	}

	public boolean hasNext()
	{
		return index < stringSize;
	}

	public String next()
	{
		if (index >= stringSize)
		{
			throw new NoSuchElementException("No more characters available!");
		}
		return String.valueOf(string.charAt(index++));
	}

	public void remove()
	{
		throw new UnsupportedOperationException("Strings don't support character removal!");
	}
}

public class Utils
{
	public static String objectType(Object obj)
	{
		return (obj != null) ? obj.getClass().toString().substring(6) : "null";
	}

	public static BigInteger toBigInteger(int arg)
	{
		return new BigInteger(Integer.toString(arg));
	}

	public static BigInteger toBigInteger(long arg)
	{
		return new BigInteger(Long.toString(arg));
	}

	public static int toInt(Object arg)
	{
		if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1 : 0;
		else if (arg instanceof Number)
			return ((Number)arg).intValue();
		throw new UnsupportedOperationException("can't convert " + objectType(arg) + " to int!");
	}

	public static long toLong(Object arg)
	{
		if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1L : 0L;
		else if (arg instanceof Number)
			return ((Number)arg).longValue();
		throw new UnsupportedOperationException("can't convert " + objectType(arg) + " to long!");
	}

	public static float toFloat(Object arg)
	{
		if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1.0f : 0.0f;
		else if (arg instanceof Number)
			return ((Number)arg).floatValue();
		throw new UnsupportedOperationException("can't convert " + objectType(arg) + " to float!");
	}

	public static double toDouble(Object arg)
	{
		if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1.0d : 0.0d;
		else if (arg instanceof Number)
			return ((Number)arg).doubleValue();
		throw new UnsupportedOperationException("can't convert " + objectType(arg) + " to double!");
	}

	public static int cmp(int arg1, int arg2)
	{
		return ((arg1 > arg2) ? 1 : 0) - ((arg1 < arg2) ? 1 : 0);
	}

	public static int cmp(long arg1, long arg2)
	{
		return ((arg1 > arg2) ? 1 : 0) - ((arg1 < arg2) ? 1 : 0);
	}

	public static int cmp(float arg1, float arg2)
	{
		return ((arg1 > arg2) ? 1 : 0) - ((arg1 < arg2) ? 1 : 0);
	}

	public static int cmp(double arg1, double arg2)
	{
		return ((arg1 > arg2) ? 1 : 0) - ((arg1 < arg2) ? 1 : 0);
	}

	public static int cmp(Comparable arg1, Comparable arg2)
	{
		return arg1.compareTo(arg2);
	}

	public static int cmp(Object arg1, Object arg2, String op)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return cmp(toInt(arg1), toInt(arg2));
			else if (arg2 instanceof Long)
				return cmp(toLong(arg1), toLong(arg2));
			else if (arg2 instanceof Float)
				return cmp(toFloat(arg1), toFloat(arg2));
			else if (arg2 instanceof Double)
				return cmp(toDouble(arg1), toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return cmp(toBigInteger(toInt(arg1)), (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return cmp(new BigDecimal(toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return cmp(toLong(arg1), toLong(arg2));
			else if (arg2 instanceof Float)
				return cmp(toFloat(arg1), toFloat(arg2));
			else if (arg2 instanceof Double)
				return cmp(toDouble(arg1), toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return cmp(toBigInteger(toLong(arg1)), (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return cmp(new BigDecimal(toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return cmp(toFloat(arg1), toFloat(arg2));
			else if (arg2 instanceof Double)
				return cmp(toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return cmp(new BigDecimal(toDouble(arg1)), new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return cmp(new BigDecimal(toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Double)
		{
			double value1 = (((Double)arg1).doubleValue());
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return cmp(value1, toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return cmp(new BigDecimal(value1), new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return cmp(new BigDecimal(value1), (BigDecimal)arg2);
		}
		else if (arg1 instanceof BigInteger)
		{
			BigInteger value1 = (BigInteger)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return cmp(value1, toBigInteger(toInt(arg2)));
			else if (arg2 instanceof Long)
				return cmp(value1, toBigInteger(toLong(arg2)));
			else if (arg2 instanceof Float)
				return cmp(new BigDecimal(value1), new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return cmp(new BigDecimal(value1), new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return cmp(value1, (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return cmp(new BigDecimal(value1), (BigDecimal)arg2);
		}
		else if (arg1 instanceof BigDecimal)
		{
			BigDecimal value1 = (BigDecimal)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return cmp(value1, new BigDecimal(Integer.toString(toInt(arg2))));
			else if (arg2 instanceof Long)
				return cmp(value1, new BigDecimal(Long.toString(toLong(arg2))));
			else if (arg2 instanceof Float)
				return cmp(value1, new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return cmp(value1, new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return cmp(value1, new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return cmp(value1, (BigDecimal)arg2);
		}
		else if (arg1 instanceof String && arg2 instanceof String)
			return cmp((String)arg1, (String)arg2);
		throw new ArgumentTypeMismatchException("{} " + op + " {}", arg1, arg2);
	}

	public static Iterator iterator(Object obj)
	{
		if (obj instanceof String)
			return new StringIterator((String)obj);
		else if (obj instanceof Iterable)
			return ((Iterable)obj).iterator();
		else if (obj instanceof Map)
			return ((Map)obj).keySet().iterator();
		else if (obj instanceof Iterator)
			return (Iterator)obj;
		throw new ArgumentTypeMismatchException("iter({})", obj);
	}

	public static Date makeDate(int year, int month, int day, int hour, int minute, int second, int microsecond)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.set(year, month-1, day, hour, minute, second);
		calendar.set(Calendar.MILLISECOND, microsecond/1000);
		return calendar.getTime();
	}

	public static Date makeDate(int year, int month, int day, int hour, int minute, int second)
	{
		return makeDate(year, month, day, hour, minute, second, 0);
	}

	public static Date makeDate(int year, int month, int day)
	{
		return makeDate(year, month, day, 0, 0, 0, 0);
	}

	public static SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat isoDateTime1Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
	public static SimpleDateFormat isoDateTime2Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static SimpleDateFormat isoTimestampFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	public static SimpleDateFormat isoTimestampMicroFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'000'");

	public static Date isoparse(String format)
	{
		try
		{
			int length = format.length();
			if (length == 10)
				return isoDateFormatter.parse(format);
			else if (length == 11)
				return isoDateFormatter.parse(format.substring(0, 10)); // ignore the trailing 'T'
			else if (length == 16)
				return isoDateTime1Formatter.parse(format);
			else if (length == 19)
				return isoDateTime2Formatter.parse(format);
			else
			{
				if (length > 23)
					format = format.substring(0, 23); // ignore last three digits
				return isoTimestampFormatter.parse(format);
			}
		}
		catch (java.text.ParseException ex) // can not happen when reading from the binary format
		{
			throw new RuntimeException("can't parse " + FunctionRepr.call(format));
		}
	}

	public static int getSliceStartPos(int sequenceSize, int virtualPos)
	{
		int retVal = virtualPos;
		if (0 > retVal)
		{
			retVal += sequenceSize;
		}
		if (0 > retVal)
		{
			retVal = 0;
		}
		else if (sequenceSize < retVal)
		{
			retVal = sequenceSize;
		}
		return retVal;
	}

	public static int getSliceEndPos(int sequenceSize, int virtualPos)
	{
		int retVal = virtualPos;
		if (0 > retVal)
		{
			retVal += sequenceSize;
		}
		if (0 > retVal)
		{
			retVal = 0;
		}
		else if (sequenceSize < retVal)
		{
			retVal = sequenceSize;
		}
		return retVal;
	}

	public static String unescapeUL4String(String string)
	{
		if (string == null)
			return null;
		StringBuffer output = new StringBuffer(string.length());
		for (int i = 0; i < string.length(); ++i)
		{
			char c = string.charAt(i);
			if (c != '\\' || i == string.length()-1)
				output.append(c);
			else
			{
				char c2 = string.charAt(++i);
				switch (c2)
				{
					case '\\':
						output.append('\\');
						break;
					case 'n':
						output.append('\n');
						break;
					case 'r':
						output.append('\r');
						break;
					case 't':
						output.append('\t');
						break;
					case 'f':
						output.append('\f');
						break;
					case 'b':
						output.append('\b');
						break;
					case 'a':
						output.append('\u0007');
						break;
					case 'e':
						output.append('\u001b');
						break;
					case '"':
						output.append('"');
						break;
					case '\'':
						output.append('\'');
						break;
					case 'x':
						output.append((char)Integer.parseInt(string.substring(i+1, i+3), 16));
						i += 3;
						break;
					case 'u':
						output.append((char)Integer.parseInt(string.substring(i+1, i+5), 16));
						i += 5;
						break;
					case 'U':
						throw new RuntimeException("\\U escapes are not supported");
					default:
						output.append(c);
						output.append(c2);
				}
			}
		}
		return output.toString();
	}

	public static Object parseUL4Int(String string)
	{
		boolean neg = false;
		int base = 10;
		Object value;
		if (string.charAt(0) == '-')
		{
			neg = true;
			string = string.substring(1);
		}
		if (string.startsWith("0x") || string.startsWith("0X"))
		{
			string = string.substring(2);
			base = 16;
		}
		else if (string.startsWith("0o") || string.startsWith("0O"))
		{
			string = string.substring(2);
			base = 8;
		}
		else if (string.startsWith("0b") || string.startsWith("0B"))
		{
			string = string.substring(2);
			base = 2;
		}
		if (neg)
			string = "-" + string;
		try
		{
			return Integer.parseInt(string, base);
		}
		catch (NumberFormatException ex1)
		{
			try
			{
				return Long.parseLong(string, base);
			}
			catch (NumberFormatException ex2)
			{
				return new BigInteger(string, base);
			}
		}
	}

	/**
	 * Compile Java source code of a Java class and return the class object
	 * @param source The source of the body of the class
	 * @param extendsSpec Name of base class (or null)
	 * @param implementsSpec Comma separated list of implemented interfaces (or null)
	 * @return The Class object that contains the compiled Java code
	 */
	public static Class compileToJava(String source, String extendsSpec, String implementsSpec) throws java.io.IOException
	{
		String dirname = System.getProperty("user.dir");
		File file = File.createTempFile("jav", ".java", new File(dirname));
		file.deleteOnExit(); // Set the file to delete on exit
		// Get the file name and extract a class name from it
		String filename = file.getName();
		String classname = filename.substring(0, filename.length()-5);

		PrintWriter out = new PrintWriter(new FileOutputStream(file));

		out.println("/* Created on " + new Date() + " */");
		out.println();
		out.print("public class " + classname);
		if (extendsSpec != null)
		{
			out.print(" extends ");
			out.print(extendsSpec);
		}
		if (implementsSpec != null)
		{
			out.print(" implements ");
			out.print(implementsSpec);
		}
		out.println();
		out.println("{");
		out.println(source);
		out.println("}");
		// Flush and close the stream
		out.flush();
		out.close();

		// This requires $JAVA_HOME/lib/tools.jar in the CLASSPATH
		com.sun.tools.javac.Main javac = new com.sun.tools.javac.Main();

		String[] args = new String[] {
			"-d", System.getProperty("user.dir"),
			filename
		};

		OutputStream nulloutput = new OutputStream() {
			public void write(int i) throws IOException {
				//do nothing
			}
		};

		// TODO add ul4.jar to java.class.path in a more generic way
		System.setProperty("java.class.path", "/Users/walter/.m2/repository/com/livinglogic/ul4/0.48/ul4-0.48.jar:/home/andreas/LivingLogic/cms/install/xist4c/WEB-INF/lib/ul4.jar:" + System.getProperty("user.dir") + ":" + System.getProperty("java.class.path") + ":/Users/walter/apache-tomcat-6.0.18/lib/naming-resources.jar:/Users/walter/apache-tomcat-6.0.18/lib/servlet-api.jar:/Users/walter/apache-tomcat-6.0.18/lib/jsp-api.jar:/Users/walter/.m2/repository/commons-lang/commons-lang/2.6/commons-lang-2.6.jar:/Users/walter/checkouts/LivingLogic.Java.ul4/ul4jython.jar:.");
		int status = javac.compile(args, new PrintWriter(System.err));
		System.err.flush();
		switch (status)
		{
			case 0:  // OK
				// Make the class file temporary as well
				new File(file.getParent(), classname + ".class").deleteOnExit();

				// Create an instance and return it
				try
				{
					return Class.forName(classname);
				}
				catch (ClassNotFoundException ex)
				{
					// Can't happen
					throw new RuntimeException(ex);
				}
			case 1:
				throw new RuntimeException("Compile status: ERROR");
			case 2:
				throw new RuntimeException("Compile status: CMDERR");
			case 3:
				throw new RuntimeException("Compile status: SYSERR");
			case 4:
				throw new RuntimeException("Compile status: ABNORMAL");
			default:
				throw new RuntimeException("Compile status: Unknown exit status");
		}
	}
}
