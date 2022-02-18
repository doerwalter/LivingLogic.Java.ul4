package tests;

import static com.livinglogic.ul4on.Utils.dumps;
import static com.livinglogic.utils.MapUtils.makeMap;
import static com.livinglogic.utils.MapUtils.makeOrderedMap;
import static com.livinglogic.utils.SetUtils.makeSet;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;

import com.livinglogic.ul4.Utils;
import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.UL4Instance;
import com.livinglogic.ul4.AbstractInstanceType;
import com.livinglogic.ul4.ArgumentCountMismatchException;
import com.livinglogic.ul4.ArgumentTypeMismatchException;
import com.livinglogic.ul4.MissingArgumentException;
import com.livinglogic.ul4.TooManyArgumentsException;
import com.livinglogic.ul4.UnsupportedArgumentNameException;
import com.livinglogic.ul4.ReadonlyException;
import com.livinglogic.ul4.AttributeException;
import com.livinglogic.ul4.RuntimeExceededException;
import com.livinglogic.ul4.NotIterableException;
import com.livinglogic.ul4.BlockException;
import com.livinglogic.ul4.SyntaxException;
import com.livinglogic.ul4.EvaluationContext;
import com.livinglogic.ul4.Template;
import com.livinglogic.ul4.Color;
import com.livinglogic.ul4.Date_;
import com.livinglogic.ul4.DateTime;
import com.livinglogic.ul4.FunctionRepr;
import com.livinglogic.ul4.MonthDelta;
import com.livinglogic.ul4.TimeDelta;
import com.livinglogic.ul4.UndefinedKey;
import com.livinglogic.ul4.UndefinedAttribute;
import com.livinglogic.ul4.UL4Bool;
import com.livinglogic.ul4.UL4GetAttr;
import com.livinglogic.ul4.UL4SetAttr;
import com.livinglogic.ul4.UL4Dir;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.Function;
import com.livinglogic.ul4.BoundArguments;
import com.livinglogic.ul4.MulAST;
import com.livinglogic.dbutils.Connection;

@RunWith(CauseTestRunner.class)
public class UL4Test
{
	private static class Point implements UL4Instance, UL4Bool, UL4SetAttr, UL4Dir
	{
		protected static class Type extends AbstractInstanceType
		{
			@Override
			public String getNameUL4()
			{
				return "Point";
			}

			@Override
			public String getDoc()
			{
				return "A 2D point";
			}

			@Override
			public boolean instanceCheck(Object object)
			{
				return object instanceof Point;
			}
		}

		public static final Type type = new Type();

		@Override
		public UL4Type getTypeUL4()
		{
			return type;
		}

		int x;
		int y;

		Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean boolUL4(EvaluationContext context)
		{
			return x != 0 || x != 0;
		}

		@Override
		public Set<String> dirUL4(EvaluationContext context)
		{
			return makeSet("x", "y");
		}

		@Override
		public Object getAttrUL4(EvaluationContext context, String key)
		{
			switch (key)
			{
				case "x":
					return x;
				case "y":
					return y;
				default:
					return UL4Instance.super.getAttrUL4(context, key);
			}
		}

		@Override
		public void setAttrUL4(EvaluationContext context, String key, Object value)
		{
			switch (key)
			{
				case "x":
					if (value instanceof Integer)
						x = (Integer)value;
					else
						throw new ArgumentTypeMismatchException("Point.x = {!r} not supported!", value);
					break;
				case "y":
					throw new ReadonlyException(this, key);
				default:
					UL4Instance.super.setAttrUL4(context, key, value);
			}
		}
	}

	private static class DoubleIt implements UL4GetAttr
	{
		@Override
		public Object getAttrUL4(EvaluationContext context, String key)
		{
			Object value = context.get(key);
			value = MulAST.call(context, 2, value);
			return value;
		}
	}

	private static class Iterate implements Iterable
	{
		public Iterator iterator()
		{
			return asList(1, 2, 3).iterator();
		}
	}

	public static Date makeDate(int year, int month, int day)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.set(year, month-1, day, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date makeDate(int year, int month, int day, int hour, int minute, int second)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.set(year, month-1, day, hour, minute, second);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date makeDate(int year, int month, int day, int hour, int minute, int second, int microsecond)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.set(year, month-1, day, hour, minute, second);
		calendar.set(Calendar.MILLISECOND, microsecond/1000);
		return calendar.getTime();
	}

	public static Map<String, Object> V(Object... args)
	{
		return makeMap(args);
	}

	private static Template T(String source)
	{
		return T(source, null, Template.Whitespace.strip, (Signature)null);
	}


	private static Template T(String source, String name)
	{
		return T(source, name, Template.Whitespace.strip, (Signature)null);
	}

	private static Template T(String source, Template.Whitespace whitespace)
	{
		return T(source, null, whitespace, (Signature)null);
	}

	private static Template T(String source, String name, Template.Whitespace whitespace)
	{
		return T(source, name, whitespace, (Signature)null);
	}

	private static Template T(String source, String name, Template.Whitespace whitespace, String signature)
	{
		Template template = new Template(source, name, whitespace, signature);
		// System.out.println(template);
		return template;
	}

	private static Template T(String source, String name, Template.Whitespace whitespace, Signature signature)
	{
		Template template = new Template(source, name, whitespace, signature);
		// System.out.println(template);
		return template;
	}

	public static void checkOutput(String expected, Template template)
	{
		checkOutput(expected, template, -1, null, null);
	}

	public static void checkOutput(String expected, Template template, Map<String, Object> variables)
	{
		checkOutput(expected, template, -1, null, variables);
	}

	public static void checkOutput(String expected, Template template, long milliseconds)
	{
		checkOutput(expected, template, milliseconds, null, null);
	}

	public static void checkOutput(String expected, Template template, long milliseconds, Map<String, Object> variables)
	{
		checkOutput(expected, template, milliseconds, null, variables);
	}

	public static void checkOutput(String expected, Template template, Map<String, Object> globalVariables, Map<String, Object> variables)
	{
		checkOutput(expected, template, -1, globalVariables, variables);
	}

	public static void checkOutput(String expected, Template template, long milliseconds, Map<String, Object> globalVariables, Map<String, Object> variables)
	{
		// Render the template once directly
		String output1 = template.renders(milliseconds, globalVariables, variables);
		assertEquals(expected, output1);

		// Recreate the template from the dump of the compiled template
		Template template2 = Template.loads(template.dumps());

		// Check that the templates format the same
		assertEquals(template.toString(), template2.toString());

		// Check that they have the same output
		String output2 = template2.renders(milliseconds, globalVariables, variables);
		assertEquals(expected, output2);
	}

	private static void checkResult(Object expected, Template template)
	{
		checkResult(expected, template, null);
	}

	private static void checkResult(Object expected, Template template, Map<String, Object> variables)
	{
		// Execute the template once by directly compiling and calling it
		Object output1 = template.call(variables);
		assertEquals(expected, output1);

		// Recreate the template from the dump of the compiled template
		Template template2 = Template.loads(template.dumps());

		// Check that the templates format the same
		assertEquals(template.toString(), template2.toString());

		// Check that they have the same output
		Object output2 = template2.call(variables);
		assertEquals(expected, output2);
	}

	public com.livinglogic.dbutils.Connection getDatabaseConnection()
	{
		String env = System.getenv("LL_JAVA_TEST_CONNECT");
		if (env == null || env.equals(""))
			return null;

		String[] connectionInfo = StringUtils.splitByWholeSeparator(env, null);
		try
		{
			Class.forName(connectionInfo[0]);
			return new Connection(java.sql.DriverManager.getConnection(connectionInfo[1], connectionInfo[2], connectionInfo[3]));
		}
		catch (ClassNotFoundException|SQLException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Test
	public void tag_text()
	{
		checkOutput("gurk", T("gurk"));
		checkOutput("g\tu rk", T("g\t\n\t u \n  r\n\t\tk"));
	}

	@Test
	public void tag_whitespace() throws Exception
	{
		checkOutput("gurk", T("<?whitespace strip?><?if 1?>\n\tgurk\n\n<?end if?>"));
	}

	@Test
	public void whitespace_in_expression()
	{
		checkOutput("40", T("<?print\na\n+\nb\n?>"), V("a", 17, "b", 23));
	}

	@Test
	public void whitespace_before_tag()
	{
		checkOutput("42", T("<? print 42 ?>"));
	}

	@Test
	public void type_none()
	{
		checkOutput("no", T("<?if None?>yes<?else?>no<?end if?>"));
		checkOutput("", T("<?print None?>"));
	}

	@Test
	public void type_bool()
	{
		checkOutput("False", T("<?print False?>"));
		checkOutput("no", T("<?if False?>yes<?else?>no<?end if?>"));
		checkOutput("True", T("<?print True?>"));
		checkOutput("yes", T("<?if True?>yes<?else?>no<?end if?>"));
	}

	@Test
	public void type_int()
	{
		checkOutput("0", T("<?print 0?>"));
		checkOutput("42", T("<?print 42?>"));
		checkOutput("-42", T("<?print -42?>"));
		checkOutput("134217727", T("<?print 134217727?>"));
		checkOutput("134217728", T("<?print 134217728?>"));
		checkOutput("-134217728", T("<?print -134217728?>"));
		checkOutput("-134217729", T("<?print -134217729?>"));
		checkOutput("576460752303423487", T("<?print 576460752303423487?>"));
		checkOutput("576460752303423488", T("<?print 576460752303423488?>"));
		checkOutput("-576460752303423488", T("<?print -576460752303423488?>"));
		checkOutput("-576460752303423489", T("<?print -576460752303423489?>"));
		checkOutput("9999999999", T("<?print 9999999999?>"));
		checkOutput("-9999999999", T("<?print -9999999999?>"));
		checkOutput("99999999999999999999", T("<?print 99999999999999999999?>"));
		checkOutput("-99999999999999999999", T("<?print -99999999999999999999?>"));
		checkOutput("255", T("<?print 0xff?>"));
		checkOutput("255", T("<?print 0Xff?>"));
		checkOutput("-255", T("<?print -0xff?>"));
		checkOutput("-255", T("<?print -0Xff?>"));
		checkOutput("63", T("<?print 0o77?>"));
		checkOutput("63", T("<?print 0O77?>"));
		checkOutput("-63", T("<?print -0o77?>"));
		checkOutput("-63", T("<?print -0O77?>"));
		checkOutput("7", T("<?print 0b111?>"));
		checkOutput("7", T("<?print 0B111?>"));
		checkOutput("-7", T("<?print -0b111?>"));
		checkOutput("-7", T("<?print -0B111?>"));
		checkOutput("no", T("<?if 0?>yes<?else?>no<?end if?>"));
		checkOutput("yes", T("<?if 1?>yes<?else?>no<?end if?>"));
		checkOutput("yes", T("<?if -1?>yes<?else?>no<?end if?>"));
	}

	@Test
	public void type_float()
	{
		checkOutput("0.0",T( "<?print 0.?>"));
		checkOutput("42.0",T( "<?print 42.?>"));
		checkOutput("-42.0",T( "<?print -42.?>"));
		checkOutput("-42.5",T( "<?print -42.5?>"));
		checkOutput("1e42", T("<?print 1E42?>"));
		checkOutput("1e42", T("<?print 1e42?>"));
		checkOutput("-1e42", T("<?print -1E42?>"));
		checkOutput("-1e42", T("<?print -1e42?>"));
		checkOutput("no", T("<?if 0.?>yes<?else?>no<?end if?>"));
		checkOutput("yes", T("<?if 1.?>yes<?else?>no<?end if?>"));
		checkOutput("yes", T("<?if -1.?>yes<?else?>no<?end if?>"));
	}

	@Test
	public void type_string()
	{
		checkOutput("foo", T("<?print 'foo'?>"));
		checkOutput("\n", T("<?print '\\n'?>"));
		checkOutput("\r", T("<?print '\\r'?>"));
		checkOutput("\t", T("<?print '\\t'?>"));
		checkOutput("\f", T("<?print '\\f'?>"));
		checkOutput("\u0008", T("<?print '\\b'?>"));
		checkOutput("\u0007", T("<?print '\\a'?>"));
		checkOutput("\u0000", T("<?print '\\x00'?>"));
		checkOutput("\"", T("<?print \"\\\"\"?>"));
		checkOutput("'", T("<?print \"\\'\"?>"));
		checkOutput("\u20ac", T("<?print '\u20ac'?>"));
		checkOutput("\t\n\u00a0\u27f6\u00a0\t\n", T("<?print '\\t\\n\\xa0\\u27f6\\xa0\\t\\n'?>"));

		checkOutput("\u00ff", T("<?print '\\xff'?>"));
		checkOutput("\u20ac", T("<?print '\\u20ac'?>"));
		checkOutput("gu\trk", T("<?print 'gu\trk'?>"));
		checkOutput("gu\n\r\t\\rk", T("<?print 'gu\\n\\r\\t\\\\rk'?>"));
		checkOutput("gu\r\nrk", T("<?print '''gu\r\nrk'''?>"));
		checkOutput("gu\r\nrk", T("<?print \"\"\"gu\r\nrk\"\"\"?>"));
		checkOutput("gu\r\nrk", T("<?print str('''gu\r\nrk''')?>"));
		checkOutput("gu\r\nrk", T("<?print str('''gu\\r\\nrk''')?>"));
		checkOutput("no", T("<?if ''?>yes<?else?>no<?end if?>"));
		checkOutput("yes", T("<?if 'foo'?>yes<?else?>no<?end if?>"));
	}

	@Test
	public void type_date()
	{
		checkOutput("2000-02-29", T("<?print @(2000-02-29).isoformat()?>"));
		checkOutput("yes", T("<?if @(2000-02-29)?>yes<?else?>no<?end if?>"));
	}

	@Test
	public void type_datetime()
	{
		checkOutput("2000-02-29T00:00:00", T("<?print @(2000-02-29T).isoformat()?>"));
		checkOutput("2000-02-29T12:34:00", T("<?print @(2000-02-29T12:34).isoformat()?>"));
		checkOutput("2000-02-29T12:34:56", T("<?print @(2000-02-29T12:34:56).isoformat()?>"));
		checkOutput("2000-02-29T12:34:56.987654", T("<?print @(2000-02-29T12:34:56.987654).isoformat()?>"));
		checkOutput("yes", T("<?if @(2000-02-29T12:34:56.987654)?>yes<?else?>no<?end if?>"));
	}

	@Test
	public void type_color()
	{
		checkOutput("255,255,255,255", T("<?code c = #fff?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>"));
		checkOutput("255,255,255,255", T("<?code c = #ffffff?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>"));
		checkOutput("18,52,86,255", T("<?code c = #123456?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>"));
		checkOutput("17,34,51,68", T("<?code c = #1234?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>"));
		checkOutput("18,52,86,120", T("<?code c = #12345678?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>"));
		checkOutput("yes", T("<?if #fff?>yes<?else?>no<?end if?>"));
		checkOutput("rgba(0,0,0,0)", T("<?code c = #0000?><?print c?>"));
		checkOutput("rgba(136,136,136,0.533)", T("<?code c = #8888?><?print c?>"));
	}

	@Test
	public void type_list()
	{
		checkOutput("", T("<?for item in []?><?print item?>;<?end for?>"));
		checkOutput("1;", T("<?for item in [1]?><?print item?>;<?end for?>"));
		checkOutput("1;", T("<?for item in [1,]?><?print item?>;<?end for?>"));
		checkOutput("1;2;", T("<?for item in [1, 2]?><?print item?>;<?end for?>"));
		checkOutput("1;2;", T("<?for item in [1, 2,]?><?print item?>;<?end for?>"));
		checkOutput("no", T("<?if []?>yes<?else?>no<?end if?>"));
		checkOutput("yes", T("<?if [1]?>yes<?else?>no<?end if?>"));
	}

	@Test
	public void type_unpack()
	{
		checkOutput("[]", T("<?print [*[]]?>"));
		checkOutput("[0, 1, 2]", T("<?print [*range(3)]?>"));
		checkOutput("[0, 1, 2]", T("<?print [*{0, 1, 2}]?>"));
		checkOutput("[-1, 0, 1, 2, -2, 3, 4, 5]", T("<?print [-1, *range(3), -2, *range(3, 6)]?>"));
		checkOutput("[0]", T("<?print [*{0: 1}]?>"));
	}

	@Test
	public void type_listcomprehension()
	{
		checkOutput("[2, 6]", T("<?code d = [2*i for i in range(4) if i%2]?><?print d?>"));
		checkOutput("[0, 2, 4, 6]", T("<?code d = [2*i for i in range(4)]?><?print d?>"));
	}

	@Test
	public void type_set()
	{
		checkOutput("1!", T("<?for item in {1}?><?print item?>!<?end for?>"));
		checkOutput("no", T("<?if {/}?>yes<?else?>no<?end if?>"));
		checkOutput("yes", T("<?if {1}?>yes<?else?>no<?end if?>"));
	}

	@Test
	public void type_set_unpack()
	{
		checkOutput("{/}", T("<?print {*{/}}?>"));
		checkOutput("{/}", T("<?print {*[]}?>"));
		checkOutput("[0, 1, 2]", T("<?print sorted({*range(3)})?>"));
		checkOutput("[0, 1, 2]", T("<?print sorted({*{0, 1, 2}})?>"));
		checkOutput("[-2, -1, 0, 1, 2, 3, 4, 5]", T("<?print sorted({-1, *range(3), -2, *range(3, 6)})?>"));
		checkOutput("{0}", T("<?print {*{0: 1}}?>"));
	}


	@Test
	public void type_setcomprehension()
	{
		checkOutput("{2}", T("<?code d = {2*i for i in range(2) if i%2}?><?print d?>"));
		checkOutput("{2}", T("<?code d = {2*i for i in [1]}?><?print d?>"));
	}

	@Test
	public void type_dict()
	{
		checkOutput("", T("<?for (key, value) in {}.items()?><?print key?>:<?print value?>!<?end for?>"));
		checkOutput("1:2!", T("<?for (key, value) in {1: 2}.items()?><?print key?>:<?print value?>!<?end for?>"));
		checkOutput("1:2!", T("<?for (key, value) in {1: 2,}.items()?><?print key?>:<?print value?>!<?end for?>"));
		// With duplicate keys, later ones simply overwrite earlier ones
		checkOutput("1:3!", T("<?for (key, value) in {1: 2, 1: 3}.items()?><?print key?>:<?print value?>!<?end for?>"));
		checkOutput("no", T("<?if {}?>yes<?else?>no<?end if?>"));
		checkOutput("yes", T("<?if {1: 2}?>yes<?else?>no<?end if?>"));
		// Dicts are ordered
		checkOutput("1:one!2:two!3:three!", T("<?for (key, value) in {1: 'one', 2: 'two', 3: 'three'}.items()?><?print key?>:<?print value?>!<?end for?>"));
	}

	@Test
	public void type_dict_unpack()
	{
		checkOutput("{}", T("<?print {**{}}?>"));
		checkOutput("0:zero;1:one;2:two;", T("<?code a = {0: 'zero', 1: 'one'}?><?code b = {2: 'two', **a}?><?for (k, v) in sorted(b.items())?><?print k?>:<?print v?>;<?end for?>"));
		checkOutput("0:zero;1:one;2:two;3:three;", T("<?code a = {0: 'zero', 1: 'one'}?><?code b = {2: 'two'}?><?code c = {3: 'three', **a, **b.items()}?><?for (k, v) in sorted(c.items())?><?print k?>:<?print v?>;<?end for?>"));
	}

	@Test
	public void type_dictcomprehension()
	{
		checkOutput("", T("<?code d = {i:2*i for i in range(10) if i%2}?><?if 2 in d?><?print d[2]?><?end if?>"));
		checkOutput("6", T("<?code d = {i:2*i for i in range(10) if i%2}?><?if 3 in d?><?print d[3]?><?end if?>"));
		checkOutput("6", T("<?code d = {i:2*i for i in range(10)}?><?print d[3]?>"));
		// Dicts comprehensions are ordered
		checkOutput("1:one!2:two!3:three!", T("<?for (key, value) in {i:v for (i, v) in enumerate(['one', 'two', 'three'], 1)}.items()?><?print key?>:<?print value?>!<?end for?>"));
	}

	@Test
	public void generatorexpression()
	{
		checkOutput("2, 6", T("<?code ge = (str(2*i) for i in range(4) if i%2)?><?print ', '.join(ge)?>"));
		checkOutput("2, 6", T("<?print ', '.join(str(2*i) for i in range(4) if i%2)?>"));
		checkOutput("0, 2, 4, 6", T("<?print ', '.join(str(2*i) for i in range(4))?>"));
		checkOutput("0, 2, 4, 6", T("<?print ', '.join((str(2*i) for i in range(4)))?>"));
	}

	@Test
	public void storevar()
	{
		checkOutput("42", T("<?code x = 42?><?print x?>"));
		checkOutput("xyzzy", T("<?code x = 'xyzzy'?><?print x?>"));
		checkOutput("42", T("<?code (x,) = [42]?><?print x?>"));
		checkOutput("17,23", T("<?code (x,y) = [17, 23]?><?print x?>,<?print y?>"));
		checkOutput("17,23,37,42,105", T("<?code ((v, w), (x,), (y,), z) = [[17, 23], [37], [42], 105]?><?print v?>,<?print w?>,<?print x?>,<?print y?>,<?print z?>"));
	}

	@Test
	public void addvar()
	{
		Template t = T("<?code x += y?><?print x?>");
		checkOutput("40", t, V("x", 17, "y", 23));
		checkOutput("40.0", t, V("x", 17, "y", 23.0));
		checkOutput("40.0", t, V("x", 17.0, "y", 23));
		checkOutput("40.0", t, V("x", 17.0, "y", 23.0));
		checkOutput("17", t, V("x", 17, "y", false));
		checkOutput("18", t, V("x", 17, "y", true));
		checkOutput("23", t, V("x", false, "y", 23));
		checkOutput("24", t, V("x", true, "y", 23));
		checkOutput("[1, 2, 3, 4]", t, V("x", asList(1, 2), "y", asList(3, 4)));
	}

	@Test
	public void subvar()
	{
		Template t = T("<?code x -= y?><?print x?>");
		checkOutput("-6", t, V("x", 17, "y", 23));
		checkOutput("-6.0", t, V("x", 17, "y", 23.0));
		checkOutput("-6.0", t, V("x", 17.0, "y", 23));
		checkOutput("-6.0", t, V("x", 17.0, "y", 23.0));
		checkOutput("17", t, V("x", 17, "y", false));
		checkOutput("16", t, V("x", 17, "y", true));
		checkOutput("-23", t, V("x", false, "y", 23));
		checkOutput("-22", t, V("x", true, "y", 23));
	}

	@Test
	public void mulvar()
	{
		Template t = T("<?code x *= y?><?print x?>");
		checkOutput("391", t, V("x", 17, "y", 23));
		checkOutput("391.0", t, V("x", 17, "y", 23.0));
		checkOutput("391.0", t, V("x", 17.0, "y", 23));
		checkOutput("391.0", t, V("x", 17.0, "y", 23.0));
		checkOutput("0", t, V("x", 17, "y", false));
		checkOutput("17", t, V("x", 17, "y", true));
		checkOutput("0", t, V("x", false, "y", 23));
		checkOutput("23", t, V("x", true, "y", 23));
		checkOutput("xyzzyxyzzyxyzzy", t, V("x", 3, "y", "xyzzy"));
		checkOutput("", t, V("x", false, "y", "xyzzy"));
		checkOutput("xyzzy", t, V("x", true, "y", "xyzzy"));
		checkOutput("xyzzyxyzzyxyzzy", t, V("x", "xyzzy", "y", 3));
		checkOutput("", t, V("x", "xyzzy", "y", false));
		checkOutput("xyzzy", t, V("x", "xyzzy", "y", true));
	}

	@Test
	public void floordivvar()
	{
		Template t = T("<?code x //= y?><?print x?>");
		checkOutput("2", t, V("x", 5, "y", 2));
		checkOutput("-3", t, V("x", 5, "y", -2));
		checkOutput("-3", t, V("x", -5, "y", 2));
		checkOutput("2", t, V("x", -5, "y", -2));
		checkOutput("2.0", t, V("x", 5., "y", 2.));
		checkOutput("-3.0", t, V("x", 5., "y", -2.));
		checkOutput("-3.0", t, V("x", -5., "y", 2.));
		checkOutput("2.0", t, V("x", -5., "y", -2.));
		checkOutput("1", t, V("x", true, "y", 1));
		checkOutput("0", t, V("x", false, "y", 1));
	}

	@Test
	public void truedivvar()
	{
		Template t = T("<?code x /= y?><?print x?>");
		checkOutput("2.5", t, V("x", 5, "y", 2));
		checkOutput("-2.5", t, V("x", 5, "y", -2));
		checkOutput("-2.5", t, V("x", -5, "y", 2));
		checkOutput("2.5", t, V("x", -5, "y", -2));
		checkOutput("2.5", t, V("x", 5., "y", 2.));
		checkOutput("-2.5", t, V("x", 5., "y", -2.));
		checkOutput("-2.5", t, V("x", -5., "y", 2.));
		checkOutput("2.5", t, V("x", -5., "y", -2.));
		checkOutput("1.0", t, V("x", true, "y", 1));
		checkOutput("0.0", t, V("x", false, "y", 1));
	}


	@Test
	public void modvar()
	{
		Template t = T("<?code x %= y?><?print x?>");
		checkOutput("4", t, V("x", 1729, "y", 23));
		checkOutput("19", t, V("x", -1729, "y", 23));
		checkOutput("19", t, V("x", -1729, "y", 23));
		checkOutput("-4", t, V("x", -1729, "y", -23));
		checkOutput("1.5", t, V("x", 6.5, "y", 2.5));
		checkOutput("1.0", t, V("x", -6.5, "y", 2.5));
		checkOutput("-1.0", t, V("x", 6.5, "y", -2.5));
		checkOutput("-1.5", t, V("x", -6.5, "y", -2.5));
		checkOutput("1", t, V("x", true, "y", 23));
		checkOutput("0", t, V("x", false, "y", 23));
	}

	@Test
	public void leftshiftvar()
	{
		Template t = T("<?code x <<= y?><?print x?>");

		checkOutput("1", t, V("x", true, "y", false));
		checkOutput("2", t, V("x", true, "y", true));
		checkOutput("0", t, V("x", 1, "y", -1));
		checkOutput("2147483648", t, V("x", 1, "y", 31));
		checkOutput("4294967296", t, V("x", 1, "y", 32));
		checkOutput("9223372036854775808", t, V("x", 1, "y", 63));
		checkOutput("18446744073709551616", t, V("x", 1, "y", 64));
		checkOutput("340282366920938463463374607431768211456", t, V("x", 1, "y", 128));
	}

	@Test
	public void rightshiftvar()
	{
		Template t = T("<?code x >>= y?><?print x?>");

		checkOutput("1", t, V("x", true, "y", false));
		checkOutput("0", t, V("x", true, "y", true));
		checkOutput("2", t, V("x", 1, "y", -1));
		checkOutput("2147483648", t, V("x", 1, "y", -31));
		checkOutput("1", t, V("x", 2147483648L, "y", 31));
		checkOutput("0", t, V("x", 1, "y", 32));
		checkOutput("-1", t, V("x", -1, "y", 10));
		checkOutput("-1", t, V("x", -4, "y", 10));
	}

	@Test
	public void bitandvar()
	{
		Template t = T("<?code x &= y?><?print x?>");

		checkOutput("0", t, V("x", false, "y", false));
		checkOutput("0", t, V("x", false, "y", true));
		checkOutput("1", t, V("x", true, "y", true));
		checkOutput("1", t, V("x", 3, "y", true));
		checkOutput("12", t, V("x", 15, "y", 60));
		checkOutput("0", t, V("x", 255, "y", 256));
		checkOutput("0", t, V("x", 255, "y", -256));
		checkOutput("1", t, V("x", 255, "y", -255));
	}

	@Test
	public void bitxorvar()
	{
		Template t = T("<?code x ^= y?><?print x?>");

		checkOutput("0", t, V("x", false, "y", false));
		checkOutput("1", t, V("x", false, "y", true));
		checkOutput("0", t, V("x", true, "y", true));
		checkOutput("2", t, V("x", 3, "y", true));
		checkOutput("51", t, V("x", 15, "y", 60));
		checkOutput("511", t, V("x", 255, "y", 256));
		checkOutput("-1", t, V("x", 255, "y", -256));
		checkOutput("-2", t, V("x", 255, "y", -255));
	}

	@Test
	public void bitorvar()
	{
		Template t = T("<?code x |= y?><?print x?>");

		checkOutput("0", t, V("x", false, "y", false));
		checkOutput("1", t, V("x", false, "y", true));
		checkOutput("1", t, V("x", true, "y", true));
		checkOutput("3", t, V("x", 3, "y", true));
		checkOutput("63", t, V("x", 15, "y", 60));
		checkOutput("511", t, V("x", 255, "y", 256));
		checkOutput("-1", t, V("x", 255, "y", -256));
		checkOutput("-1", t, V("x", 255, "y", -255));
	}

	@Test
	public void tag_for_string()
	{
		Template t = T("<?for c in data?>(<?print c?>)<?end for?>");
		checkOutput("", t, V("data", ""));
		checkOutput("(g)(u)(r)(k)", t, V("data", "gurk"));
	}

	@Test
	public void tag_for_list()
	{
		Template t = T("<?for c in data?>(<?print c?>)<?end for?>");
		checkOutput("", t, V("data", asList()));
		checkOutput("(g)(u)(r)(k)", t, V("data", asList("g", "u", "r", "k")));
	}

	@Test
	public void tag_for_dict()
	{
		Template t = T("<?for c in sorted(data)?>(<?print c?>)<?end for?>");
		checkOutput("", t, V("data", V()));
		checkOutput("(a)(b)(c)", t, V("data", V("a", 1, "b", 2, "c", 3)));
	}

	@Test
	public void tag_for_nested_loop()
	{
		Template t = T("<?for list in data?>[<?for n in list?>(<?print n?>)<?end for?>]<?end for?>");
		checkOutput("[(1)(2)][(3)(4)]", t, V("data", asList(asList(1, 2), asList(3, 4))));
	}

	@Test
	public void tag_for_unpacking()
	{
		Object data1 = asList(
			asList("spam"),
			asList("gurk"),
			asList("hinz")
		);

		Object data2 = asList(
			asList("spam", "eggs"),
			asList("gurk", "hurz"),
			asList("hinz", "kunz")
		);

		Object data3 = asList(
			asList("spam", "eggs", 17),
			asList("gurk", "hurz", 23),
			asList("hinz", "kunz", 42)
		);

		Object data4 = asList(
			asList("spam", "eggs", 17, null),
			asList("gurk", "hurz", 23, false),
			asList("hinz", "kunz", 42, true)
		);

		checkOutput("(spam)(gurk)(hinz)", T("<?for (a,) in data?>(<?print a?>)<?end for?>"), V("data", data1));
		checkOutput("(spam,eggs)(gurk,hurz)(hinz,kunz)", T("<?for (a, b) in data?>(<?print a?>,<?print b?>)<?end for?>"), V("data", data2));
		checkOutput("(spam,eggs,17)(gurk,hurz,23)(hinz,kunz,42)", T("<?for (a, b, c) in data?>(<?print a?>,<?print b?>,<?print c?>)<?end for?>"), V("data", data3));
		checkOutput("(spam,eggs,17,)(gurk,hurz,23,False)(hinz,kunz,42,True)", T("<?for (a, b, c, d) in data?>(<?print a?>,<?print b?>,<?print c?>,<?print d?>)<?end for?>"), V("data", data4));
	}

	@Test
	public void tag_for_nested_unpacking()
	{
		Object data = asList(
			asList(asList("spam", "eggs"), asList(17), null),
			asList(asList("gurk", "hurz"), asList(23), false),
			asList(asList("hinz", "kunz"), asList(42), true)
		);

		checkOutput("(spam,eggs,17,)(gurk,hurz,23,False)(hinz,kunz,42,True)", T("<?for ((a, b), (c,), d) in data?>(<?print a?>,<?print b?>,<?print c?>,<?print d?>)<?end for?>"), V("data", data));
	}

	@Test
	public void tag_break()
	{
		checkOutput("1, 2, ", T("<?for i in [1,2,3]?><?print i?>, <?if i==2?><?break?><?end if?><?end for?>"));
	}

	@Test
	public void tag_break_nested()
	{
		checkOutput("1, 1, 2, 1, 2, 3, ", T("<?for i in [1,2,3,4]?><?for j in [1,2,3,4]?><?print j?>, <?if j>=i?><?break?><?end if?><?end for?><?if i>=3?><?break?><?end if?><?end for?>"));
	}

	@CauseTest(expectedCause=BlockException.class)
	public void tag_break_outside_loop()
	{
		checkOutput("", T("<?break?>"));
	}

	@CauseTest(expectedCause=BlockException.class)
	public void tag_break_outside_loop_in_template()
	{
		checkOutput("", T("<?def gurk?><?break?><?end def?>"));
	}

	@Test
	public void tag_continue()
	{
		checkOutput("1, 3, ", T("<?for i in [1,2,3]?><?if i==2?><?continue?><?end if?><?print i?>, <?end for?>"));
	}

	@Test
	public void tag_continue_nested()
	{
		checkOutput("1, 3, !1, 3, !", T("<?for i in [1,2,3]?><?if i==2?><?continue?><?end if?><?for j in [1,2,3]?><?if j==2?><?continue?><?end if?><?print j?>, <?end for?>!<?end for?>"));
	}

	@CauseTest(expectedCause=BlockException.class)
	public void tag_continue_outside_loop()
	{
		checkOutput("", T("<?continue?>"));
	}

	@CauseTest(expectedCause=BlockException.class)
	public void tag_continue_outside_loop_in_template()
	{
		checkOutput("", T("<?def gurk?><?continue?><?end def?>"));
	}

	public void tag_ul4()
	{
		checkOutput("42", T("<?ul4 test(x=42)?><?print x?>"));
	}

	@Test
	public void tag_while()
	{
		checkOutput("17", T("<?code x = 17?><?while False?><?code x = 23?><?end while?><?print x?>"));
		checkOutput("23", T("<?code x = 17?><?while x < 23?><?code x += 1?><?end while?><?print x?>"));
	}

	@Test
	public void tag_if()
	{
		checkOutput("42", T("<?if data?><?print data?><?end if?>"), V("data", 42));
	}

	@Test
	public void tag_else()
	{
		Template t = T("<?if data?><?print data?><?else?>no<?end if?>");
		checkOutput("42", t, V("data", 42));
		checkOutput("no", t, V("data", 0));
	}

	// FIXME: Doesn't work, because of chained exceptions, needs to be split into n tests
	// @Test(expected=BlockException)
	// public void block_errors()
	// {
	// 	checkOutput("", T("<?for x in data?>")); // "BlockError: block unclosed"
	// 	checkOutput("", T("<?for x in data?><?end if?>")); // "BlockError: endif doesn't match any if"
	// 	checkOutput("", T("<?end?>")); // "BlockError: not in any block"
	// 	checkOutput("", T("<?end for?>")); // "BlockError: not in any block"
	// 	checkOutput("", T("<?end if?>")); // "BlockError: not in any block"
	// 	checkOutput("", T("<?else?>")); // "BlockError: else doesn't match any if"
	// 	checkOutput("", T("<?if data?>")); // "BlockError: block unclosed"
	// 	checkOutput("", T("<?if data?><?else?>")); // "BlockError: block unclosed"
	// 	checkOutput("", T("<?if data?><?else?><?else?>")); // "BlockError: duplicate else"
	// 	checkOutput("", T("<?if data?><?else?><?elif data?>")); // "BlockError: else already seen in elif"
	// 	checkOutput("", T("<?if data?><?elif data?><?elif data?><?else?><?elif data?>")); // "BlockError: else already seen in elif"
	// }


	// FIXME: Doesn't work, because of chained exceptions, needs to be split into n tests
	// @Test(expected=BlockException)
	// public void empty()
	// {
	// 	checkOutput("", T("<?print?>")); // "expression required"
	// 	checkOutput("", T("<?if?>")); // "expression required"
	// 	checkOutput("", T("<?if x?><?elif?><?end if?>")); // "expression required"
	// 	checkOutput("", T("<?for?>")); // "loop expression required"
	// 	checkOutput("", T("<?code?>")); // "statement required"
	// }

	@Test
	public void operator_add()
	{
		Template t = T("<?print x + y?>");

		checkOutput("0", t, V("x", false, "y", false));
		checkOutput("1", t, V("x", false, "y", true));
		checkOutput("1", t, V("x", true, "y", false));
		checkOutput("2", t, V("x", true, "y", true));
		checkOutput("18", t, V("x", 17, "y", true));
		checkOutput("40", t, V("x", 17, "y", 23));
		checkOutput("18.0", t, V("x", 17, "y", 1.0));
		checkOutput("24", t, V("x", true, "y", 23));
		checkOutput("22.0", t, V("x", -1.0, "y", 23));
		checkOutput("foobar", t, V("x", "foo", "y", "bar"));
		checkOutput("[1, 2, 3, 4][1, 2][3, 4]", T("<?code z = x + y?><?print z?><?print x?><?print y?>"), V("x", asList(1, 2), "y", asList(3, 4)));
		for (Object d : makeDateTimeVariants(2012, 10, 17))
			checkOutput("2012-10-18 00:00", t, V("x", d, "y", new TimeDelta(1)));
		checkOutput("2012-10-18", t, V("x", LocalDate.of(2012, 10, 17), "y", new TimeDelta(1)));
		for (Object d : makeDateTimeVariants(2012, 10, 17))
			checkOutput("2013-10-17 00:00", t, V("x", d, "y", new TimeDelta(365)));
		checkOutput("2013-10-17", t, V("x", LocalDate.of(2012, 10, 17), "y", new TimeDelta(365)));
		for (Object d : makeDateTimeVariants(2012, 10, 17))
			checkOutput("2012-10-17 12:00", t, V("x", d, "y", new TimeDelta(0, 12*60*60)));
		checkOutput("2012-10-17", t, V("x", LocalDate.of(2012, 10, 17), "y", new TimeDelta(0, 12*60*60)));
		for (Object d : makeDateTimeVariants(2012, 10, 17))
			checkOutput("2012-10-17 00:00:01", t, V("x", d, "y", new TimeDelta(0, 1)));
		checkOutput("2012-10-17", t, V("x", LocalDate.of(2012, 10, 17), "y", new TimeDelta(0, 1)));
		for (Object d : makeDateTimeVariants(2012, 10, 17))
			checkOutput("2012-10-17 00:00:00.500000", t, V("x", d, "y", new TimeDelta(0, 0, 500000)));
		checkOutput("2012-10-17", t, V("x", LocalDate.of(2012, 10, 17), "y", new TimeDelta(0, 0, 500000)));
		for (Object d : makeDateTimeVariants(2012, 10, 17))
			checkOutput("2012-10-17 00:00:00.001000", t, V("x", d, "y", new TimeDelta(0, 0, 1000)));
		checkOutput("2012-10-17", t, V("x", LocalDate.of(2012, 10, 17), "y", new TimeDelta(0, 0, 1000)));
		checkOutput("2 days, 0:00:00", t, V("x", new TimeDelta(1), "y", new TimeDelta(1)));
		checkOutput("1 day, 0:00:01", t, V("x", new TimeDelta(1), "y", new TimeDelta(0, 1)));
		checkOutput("1 day, 0:00:00.000001", t, V("x", new TimeDelta(1), "y", new TimeDelta(0, 0, 1)));
		checkOutput("2 months", t, V("x", new MonthDelta(1), "y", new MonthDelta(1)));
		checkOutput("(foo)(bar)(gurk)(hurz)", T("<?for i in a+b?>(<?print i?>)<?end for?>"), V("a", asList("foo", "bar"), "b", asList("gurk", "hurz")));
		// This checks constant folding
		checkOutput("3", T("<?print 1+2?>"));
		checkOutput("2", T("<?print 1+True?>"));
		checkOutput("3.0", T("<?print 1+2.0?>"));
	}

	@Test
	public void operator_sub()
	{
		Template t = T("<?print x - y?>");

		checkOutput("0", t, V("x", false, "y", false));
		checkOutput("-1", t, V("x", false, "y", true));
		checkOutput("1", t, V("x", true, "y", false));
		checkOutput("0", t, V("x", true, "y", true));
		checkOutput("16", t, V("x", 17, "y", true));
		checkOutput("-6", t, V("x", 17, "y", 23));
		checkOutput("16.0", t, V("x", 17, "y", 1.0));
		checkOutput("-22", t, V("x", true, "y", 23));
		checkOutput("-24.0", t, V("x", -1.0, "y", 23));
		for (Object d : makeDateTimeVariants(2012, 10, 17))
			checkOutput("2012-10-16 00:00", t, V("x", d, "y", new TimeDelta(1)));
		checkOutput("2012-10-16", t, V("x", LocalDate.of(2012, 10, 17), "y", new TimeDelta(1)));
		for (Object d : makeDateTimeVariants(2012, 10, 17))
			checkOutput("2011-10-17 00:00", t, V("x", d, "y", new TimeDelta(366)));
		checkOutput("2011-10-17", t, V("x", LocalDate.of(2012, 10, 17), "y", new TimeDelta(366)));
		for (Object d : makeDateTimeVariants(2012, 10, 17))
			checkOutput("2012-10-16 12:00", t, V("x", d, "y", new TimeDelta(0, 12*60*60)));
		checkOutput("2012-10-17", t, V("x", LocalDate.of(2012, 10, 17), "y", new TimeDelta(0, 12*60*60)));
		for (Object d : makeDateTimeVariants(2012, 10, 17))
			checkOutput("2012-10-16 23:59:59", t, V("x", d, "y", new TimeDelta(0, 1)));
		checkOutput("2012-10-17", t, V("x", LocalDate.of(2012, 10, 17), "y", new TimeDelta(0, 1)));
		for (Object d : makeDateTimeVariants(2012, 10, 17))
			checkOutput("2012-10-16 23:59:59.500000", t, V("x", d, "y", new TimeDelta(0, 0, 500000)));
		checkOutput("2012-10-17", t, V("x", LocalDate.of(2012, 10, 17), "y", new TimeDelta(0, 0, 500000)));
		for (Object d : makeDateTimeVariants(2012, 10, 17))
			checkOutput("2012-10-16 23:59:59.999000", t, V("x", d, "y", new TimeDelta(0, 0, 1000)));
		checkOutput("2012-10-17", t, V("x", LocalDate.of(2012, 10, 17), "y", new TimeDelta(0, 0, 1000)));
		for (Object d1 : makeDateTimeVariants(2015, 1, 2, 1, 0, 0))
			for (Object d2 : makeDateTimeVariants(2015, 1, 1, 1, 0, 0))
				checkOutput("1 day, 0:00:00", t, V("x", d1, "y", d2));
		for (Object d1 : makeDateTimeVariants(2015, 1, 2, 2, 0, 0))
			for (Object d2 : makeDateTimeVariants(2015, 1, 1, 1, 0, 0))
				checkOutput("1 day, 1:00:00", t, V("x", d1, "y", d2));
		for (Object d1 : makeDateTimeVariants(2015, 1, 2, 2, 1, 0))
			for (Object d2 : makeDateTimeVariants(2015, 1, 1, 1, 0, 0))
				checkOutput("1 day, 1:01:00", t, V("x", d1, "y", d2));
		for (Object d1 : makeDateTimeVariants(2015, 1, 2, 2, 1, 1))
			for (Object d2 : makeDateTimeVariants(2015, 1, 1, 1, 0, 0))
				checkOutput("1 day, 1:01:01", t, V("x", d1, "y", d2));
		for (Object d1 : makeDateTimeVariants(2015, 1, 2, 2, 1, 1, 1000))
			for (Object d2 : makeDateTimeVariants(2015, 1, 1, 1, 0, 0))
				checkOutput("1 day, 1:01:01.001000", t, V("x", d1, "y", d2));
		LocalDateTime d1Micro = LocalDateTime.of(2015, 1, 2, 2, 1, 1, 1000);
		for (Object d2 : makeDateTimeVariants(2015, 1, 1, 1, 0, 0))
			checkOutput("1 day, 1:01:01.000001", t, V("x", d1Micro, "y", d2));
		for (Object d1 : makeDateTimeVariants(2012, 10, 17))
			for (Object d2 : makeDateTimeVariants(2012, 10, 15))
				checkOutput("2 days, 0:00:00", t, V("x", d1, "y", d2));
		checkOutput("2 days, 0:00:00", t, V("x", LocalDate.of(2012, 10, 17), "y", LocalDate.of(2012, 10, 15)));
		for (Object d1 : makeDateTimeVariants(1999, 1, 1))
			for (Object d2 : makeDateTimeVariants(1997, 1, 1))
				checkOutput("730 days, 0:00:00", t, V("x", d1, "y", d2));
		for (Object d1 : makeDateTimeVariants(2015, 1, 1, 13, 0, 0))
			for (Object d2 : makeDateTimeVariants(2015, 1, 1, 12, 0, 0))
				checkOutput("1:00:00", t, V("x", d1, "y", d2));
		checkOutput("730 days, 0:00:00", t, V("x", LocalDate.of(1999, 1, 1), "y", LocalDate.of(1997, 1, 1)));
		checkOutput("0:00:00", t, V("x", new TimeDelta(1), "y", new TimeDelta(1)));
		checkOutput("1 day, 0:00:00", t, V("x", new TimeDelta(2), "y", new TimeDelta(1)));
		checkOutput("23:59:59", t, V("x", new TimeDelta(1), "y", new TimeDelta(0, 1)));
		checkOutput("23:59:59.999999", t, V("x", new TimeDelta(1), "y", new TimeDelta(0, 0, 1)));
		checkOutput("-1 day, 23:59:59", t, V("x", new TimeDelta(0), "y", new TimeDelta(0, 1)));
		checkOutput("-1 day, 23:59:59.999999", t, V("x", new TimeDelta(0), "y", new TimeDelta(0, 0, 1)));
		// This checks constant folding
		checkOutput("-1", T("<?print 1-2?>"));
		checkOutput("1", T("<?print 2-True?>"));
		checkOutput("-1.0", T("<?print 1-2.0?>"));
	}

	@Test
	public void operator_neg()
	{
		Template t = T("<?print -x?>");

		checkOutput("0", t, V("x", false));
		checkOutput("-1", t, V("x", true));
		checkOutput("-17", t, V("x", 17));
		checkOutput("-17.0", t, V("x", 17.0));
		checkOutput("0:00:00", t, V("x", new TimeDelta()));
		checkOutput("-1 day, 0:00:00", t, V("x", new TimeDelta(1)));
		checkOutput("-1 day, 23:59:59", t, V("x", new TimeDelta(0, 1)));
		checkOutput("-1 day, 23:59:59.999999", t, V("x", new TimeDelta(0, 0, 1)));
		// This checks constant folding
		checkOutput("0", T("<?print -False?>"));
		checkOutput("-1", T("<?print -True?>"));
		checkOutput("-2", T("<?print -2?>"));
		checkOutput("-2.0", T("<?print -2.0?>"));
	}

	@Test
	public void operator_bitnot()
	{
		Template t = T("<?print ~x?>");

		checkOutput("-1", t, V("x", false));
		checkOutput("-2", t, V("x", true));
		checkOutput("-1", t, V("x", 0));
		checkOutput("-256", t, V("x", 255));
		checkOutput("-4294967297", t, V("x", 1L << 32));
		checkOutput("-4611686018427387905", t, V("x", 4611686018427387904L));
		checkOutput("-18446744073709551617", t, V("x", new BigInteger("18446744073709551616")));
	}

	@Test
	public void operator_mul()
	{
		Template t = T("<?print x * y?>");

		checkOutput("0", t, V("x", false, "y", false));
		checkOutput("0", t, V("x", false, "y", false));
		checkOutput("0", t, V("x", false, "y", true));
		checkOutput("0", t, V("x", true, "y", false));
		checkOutput("1", t, V("x", true, "y", true));
		checkOutput("17", t, V("x", 17, "y", true));
		checkOutput("391", t, V("x", 17, "y", 23));
		checkOutput("17.0", t, V("x", 17, "y", 1.0));
		checkOutput("23", t, V("x", true, "y", 23));
		checkOutput("-23.0", t, V("x", -1.0, "y", 23));
		checkOutput("foofoofoo", t, V("x", 3, "y", "foo"));
		checkOutput("foofoofoo", t, V("x", "foo", "y", 3));
		checkOutput("0:00:00", t, V("x", 4, "y", new TimeDelta()));
		checkOutput("4 days, 0:00:00", t, V("x", 4, "y", new TimeDelta(1)));
		checkOutput("2 days, 0:00:00", t, V("x", 4, "y", new TimeDelta(0, 12*60*60)));
		checkOutput("0:00:02", t, V("x", 4, "y", new TimeDelta(0, 0, 500000)));
		checkOutput("12:00:00", t, V("x", 0.5, "y", new TimeDelta(1)));
		checkOutput("0:00:00", t, V("x", new TimeDelta(), "y", 4));
		checkOutput("4 days, 0:00:00", t, V("x", new TimeDelta(1), "y", 4));
		checkOutput("2 days, 0:00:00", t, V("x", new TimeDelta(0, 12*60*60), "y", 4));
		checkOutput("0:00:02", t, V("x", new TimeDelta(0, 0, 500000), "y", 4));
		checkOutput("12:00:00", t, V("x", new TimeDelta(1), "y", 0.5));
		checkOutput("(foo)(bar)(foo)(bar)(foo)(bar)", T("<?for i in 3*data?>(<?print i?>)<?end for?>"), V("data", asList("foo", "bar")));
		// This checks constant folding
		checkOutput("391", T("<?print 17*23?>"));
		checkOutput("17", T("<?print 17*True?>"));
		checkOutput("391.0", T("<?print 17.0*23.0?>"));
	}

	@Test
	public void operator_truediv()
	{
		Template t = T("<?print x / y?>");

		checkOutput("0.0", t, V("x", false, "y", true));
		checkOutput("1.0", t, V("x", true, "y", true));
		checkOutput("17.0", t, V("x", 17, "y", true));
		checkOutput("17.0", t, V("x", 391, "y", 23));
		checkOutput("17.0", t, V("x", 17, "y", 1.0));
		checkOutput("0.5", t, V("x", 1, "y", 2));
		checkOutput("0:00:00", t, V("x", new TimeDelta(), "y", 4));
		checkOutput("2 days, 0:00:00", t, V("x", new TimeDelta(8), "y", 4));
		checkOutput("12:00:00", t, V("x", new TimeDelta(4), "y", 8));
		checkOutput("0:00:00.500000", t, V("x", new TimeDelta(0, 4), "y", 8));
		checkOutput("0:00:00.500000", t, V("x", new TimeDelta(0, 4), "y", new BigInteger("8")));
		checkOutput("2 days, 0:00:00", t, V("x", new TimeDelta(1), "y", 0.5));
		checkOutput("2 days, 0:00:00", t, V("x", new TimeDelta(1), "y", new BigDecimal("0.5")));
		checkOutput("19:12:00", t, V("x", new TimeDelta(2), "y", new BigDecimal("2.5")));
		checkOutput("9:36:00", t, V("x", new TimeDelta(1), "y", 2.5));
		// This checks constant foldingd
		checkOutput("0.5", T("<?print 1/2?>"));
		checkOutput("2.0", T("<?print 2.0/True?>"));
	}

	@Test
	public void operator_floordiv()
	{
		Template t = T("<?print x // y?>");

		checkOutput("0", t, V("x", false, "y", true));
		checkOutput("1", t, V("x", true, "y", true));
		checkOutput("17", t, V("x", 17, "y", true));
		checkOutput("17", t, V("x", 392, "y", 23));
		checkOutput("17.0", t, V("x", 17, "y", 1.0));
		checkOutput("0", t, V("x", 1, "y", 2));
		checkOutput("0:00:00", t, V("x", new TimeDelta(), "y", 4));
		checkOutput("2 days, 0:00:00", t, V("x", new TimeDelta(8), "y", 4));
		checkOutput("12:00:00", t, V("x", new TimeDelta(4), "y", 8));
		checkOutput("0:00:00.500000", t, V("x", new TimeDelta(0, 4), "y", 8));
		// This checks constant folding
		checkOutput("0.5", T("<?print 1/2?>"));
		checkOutput("2.0", T("<?print 2.0/True?>"));
	}

	@Test
	public void operator_mod()
	{
		Template t = T("<?print x % y?>");

		checkOutput("0", t, V("x", false, "y", true));
		checkOutput("0", t, V("x", true, "y", true));
		checkOutput("0", t, V("x", 17, "y", true));
		checkOutput("6", t, V("x", 23, "y", 17));
		checkOutput("0.5", t, V("x", 5.5, "y", 2.5));
		// This checks constant folding
		checkOutput("6", T("<?print 23 % 17?>"));
	}

	@Test
	public void operator_leftshift()
	{
		Template t = T("<?print x << y?>");

		checkOutput("1", t, V("x", true, "y", false));
		checkOutput("2", t, V("x", true, "y", true));
		checkOutput("0", t, V("x", 1, "y", -1));
		checkOutput("2147483648", t, V("x", 1, "y", 31));
		checkOutput("4294967296", t, V("x", 1, "y", 32));
		checkOutput("9223372036854775808", t, V("x", 1, "y", 63));
		checkOutput("18446744073709551616", t, V("x", 1, "y", 64));
		checkOutput("340282366920938463463374607431768211456", t, V("x", 1, "y", 128));
		// This checks constant folding
		checkOutput("16", T("<?print 1 << 4?>"));
		checkOutput("2", T("<?print True << True?>"));
	}

	@Test
	public void operator_rightshift()
	{
		Template t = T("<?print x >> y?>");

		checkOutput("1", t, V("x", true, "y", false));
		checkOutput("0", t, V("x", true, "y", true));
		checkOutput("2", t, V("x", 1, "y", -1));
		checkOutput("2147483648", t, V("x", 1, "y", -31));
		checkOutput("1", t, V("x", 2147483648L, "y", 31));
		checkOutput("0", t, V("x", 1, "y", 32));
		checkOutput("-1", t, V("x", -1, "y", 10));
		checkOutput("-1", t, V("x", -4, "y", 10));
		// This checks constant folding
		checkOutput("1", T("<?print 16 >> 4?>"));
		checkOutput("0", T("<?print True >> True?>"));
	}

	@Test
	public void operator_bitand()
	{
		Template t = T("<?print x & y?>");

		checkOutput("2", T("<?print 3 & 6?>"));
		checkOutput("1", T("<?print True & True?>"));
		checkOutput("0", t, V("x", false, "y", false));
		checkOutput("0", t, V("x", false, "y", true));
		checkOutput("1", t, V("x", true, "y", true));
		checkOutput("1", t, V("x", 3, "y", true));
		checkOutput("12", t, V("x", 15, "y", 60));
		checkOutput("0", t, V("x", 255, "y", 256));
		checkOutput("0", t, V("x", 255, "y", -256));
		checkOutput("1", t, V("x", 255, "y", -255));
	}

	@Test
	public void operator_bitxor()
	{
		Template t = T("<?print x ^ y?>");

		checkOutput("5", T("<?print 3 ^ 6?>"));
		checkOutput("0", T("<?print True ^ True?>"));
		checkOutput("0", t, V("x", false, "y", false));
		checkOutput("1", t, V("x", false, "y", true));
		checkOutput("0", t, V("x", true, "y", true));
		checkOutput("2", t, V("x", 3, "y", true));
		checkOutput("51", t, V("x", 15, "y", 60));
		checkOutput("511", t, V("x", 255, "y", 256));
		checkOutput("-1", t, V("x", 255, "y", -256));
		checkOutput("-2", t, V("x", 255, "y", -255));
	}

	@Test
	public void operator_bitor()
	{
		Template t = T("<?print x | y?>");

		checkOutput("7", T("<?print 3 | 6?>"));
		checkOutput("1", T("<?print False | True?>"));
		checkOutput("0", t, V("x", false, "y", false));
		checkOutput("1", t, V("x", false, "y", true));
		checkOutput("1", t, V("x", true, "y", true));
		checkOutput("3", t, V("x", 3, "y", true));
		checkOutput("63", t, V("x", 15, "y", 60));
		checkOutput("511", t, V("x", 255, "y", 256));
		checkOutput("-1", t, V("x", 255, "y", -256));
		checkOutput("-1", t, V("x", 255, "y", -255));
	}

	@Test
	public void operator_is()
	{
		Template t = T("<?print x is y?>");

		checkOutput("True", t, V("x", null, "y", null));

		Object obj1 = 42;
		checkOutput("True", t, V("x", obj1, "y", obj1));

		Object obj2 = asList(1, 2, 3);
		checkOutput("True", t, V("x", obj2, "y", obj2));

		Object obj3 = asList(1, 2, 3);
		Object obj4 = asList(1, 2, 3);
		checkOutput("False", t, V("x", obj3, "y", obj4));
	}

	@Test
	public void operator_isnot()
	{
		Template t = T("<?print x is not y?>");

		checkOutput("False", t, V("x", null, "y", null));

		Object obj1 = 42;
		checkOutput("False", t, V("x", obj1, "y", obj1));

		Object obj2 = asList(1, 2, 3);
		checkOutput("False", t, V("x", obj2, "y", obj2));

		Object obj3 = asList(1, 2, 3);
		Object obj4 = asList(1, 2, 3);
		checkOutput("True", t, V("x", obj3, "y", obj4));
	}

	@Test
	public void operator_eq()
	{
		Template t = T("<?print x == y?>");

		checkOutput("True", t, V("x", null, "y", null));
		checkOutput("False", t, V("x", null, "y", 42));
		checkOutput("False", t, V("x", false, "y", true));
		checkOutput("True", t, V("x", true, "y", true));
		checkOutput("True", t, V("x", 1, "y", true));
		checkOutput("False", t, V("x", 1, "y", false));
		checkOutput("False", t, V("x", 17, "y", 23));
		checkOutput("True", t, V("x", 17, "y", 17));
		checkOutput("True", t, V("x", 17, "y", 17.0));
		checkOutput("True", t, V("x", "foo", "y", "foo"));
		checkOutput("False", t, V("x", "foobar", "y", "foobaz"));
		checkOutput("True", t, V("x", new TimeDelta(0), "y", new TimeDelta(0)));
		checkOutput("False", t, V("x", new TimeDelta(0), "y", new TimeDelta(1)));
		checkOutput("False", t, V("x", new TimeDelta(0), "y", new TimeDelta(0, 1)));
		checkOutput("False", t, V("x", new TimeDelta(0), "y", new TimeDelta(0, 0, 1)));
		checkOutput("True", t, V("x", new MonthDelta(0), "y", new MonthDelta(0)));
		checkOutput("False", t, V("x", new MonthDelta(0), "y", new MonthDelta(1)));
		checkOutput("True", t, V("x", Date_.call(2015, 11, 12), "y", Date_.call(2015, 11, 12)));
		checkOutput("False", t, V("x", Date_.call(2015, 11, 12), "y", Date_.call(2015, 11, 13)));
		checkOutput("True", t, V("x", new Color(0x12, 0x34, 0x56, 0x78), "y", new Color(0x12, 0x34, 0x56, 0x78)));
		checkOutput("False", t, V("x", new Color(0x12, 0x34, 0x56, 0x78), "y", new Color(0x11, 0x34, 0x56, 0x78)));
		checkOutput("False", t, V("x", new Color(0x12, 0x34, 0x56, 0x78), "y", new Color(0x12, 0x33, 0x56, 0x78)));
		checkOutput("False", t, V("x", new Color(0x12, 0x34, 0x56, 0x78), "y", new Color(0x12, 0x34, 0x55, 0x78)));
		checkOutput("False", t, V("x", new Color(0x12, 0x34, 0x56, 0x78), "y", new Color(0x12, 0x34, 0x56, 0x77)));
		checkOutput("True", t, V("x", asList(), "y", asList()));
		checkOutput("True", t, V("x", asList(1, 2, 3), "y", asList(1, 2, 3)));
		checkOutput("True", t, V("x", asList(1, asList(2, asList(3))), "y", asList(1, asList(2, asList(3)))));
		checkOutput("False", t, V("x", asList(1, asList(2, asList(3))), "y", asList(1, asList(2, asList(4)))));
		checkOutput("False", t, V("x", asList(1, 2, 3), "y", asList(1, 2, 4)));
		checkOutput("False", t, V("x", asList(1, 2, 3), "y", asList(1, 2, 3, 4)));
		checkOutput("True", t, V("x", V(), "y", V()));
		checkOutput("True", t, V("x", V(1, 2, "foo", "bar"), "y", V(1, 2, "foo", "bar")));
		checkOutput("False", t, V("x", V(1, 2, "foo", "bar"), "y", V(1, 2, "foo", "baz")));
		checkOutput("False", t, V("x", V(1, 2, "foo", "bar", 3, 4), "y", V(1, 2, "foo", "bar", 5, 6)));
		checkOutput("True", t, V("x", makeSet(), "y", makeSet()));
		checkOutput("True", t, V("x", makeSet(1, "foo"), "y", makeSet(1, "foo")));
		checkOutput("False", t, V("x", makeSet(1, "foo"), "y", makeSet(1, "bar")));
		checkOutput("False", t, V("x", makeSet(1, 2), "y", makeSet(1, 2, 3)));

		// Check mixed number types
		checkOutput("True", t, V("x", new Integer(42), "y", new Long(42)));
		checkOutput("True", t, V("x", new Integer(42), "y", new BigInteger("42")));
		checkOutput("True", t, V("x", asList(new Integer(42)), "y", asList(new Long(42))));
		checkOutput("True", t, V("x", asList(new Integer(42)), "y", asList(new BigInteger("42"))));
		checkOutput("True", t, V("x", V("42", new Integer(42)), "y", V("42", new Long(42))));
		checkOutput("True", t, V("x", V("42", new Integer(42)), "y", V("42", new BigInteger("42"))));

		// Check mixed type comparisons
		checkOutput("False", t, V("x", null, "y", true));
		checkOutput("False", t, V("x", null, "y", 42));
		checkOutput("False", t, V("x", 42, "y", "foo"));
		checkOutput("False", t, V("x", "foo", "y", asList()));
		checkOutput("False", t, V("x", asList(), "y", V()));
		checkOutput("False", t, V("x", V(), "y", makeSet()));
		checkOutput("False", t, V("x", "foo", "y", new Date()));

		// This checks constant folding
		checkOutput("False", T("<?print 17 == 23?>"));
		checkOutput("True", T("<?print 17 == 17.?>"));
	}

	@Test
	public void operator_ne()
	{
		Template t = T("<?print x != y?>");

		checkOutput("False", t, V("x", null, "y", null));
		checkOutput("True", t, V("x", null, "y", 42));
		checkOutput("True", t, V("x", false, "y", true));
		checkOutput("False", t, V("x", true, "y", true));
		checkOutput("False", t, V("x", 1, "y", true));
		checkOutput("True", t, V("x", 1, "y", false));
		checkOutput("True", t, V("x", 17, "y", 23));
		checkOutput("False", t, V("x", 17, "y", 17));
		checkOutput("False", t, V("x", 17, "y", 17.0));
		checkOutput("False", t, V("x", "foo", "y", "foo"));
		checkOutput("True", t, V("x", "foobar", "y", "foobaz"));
		checkOutput("False", t, V("x", new TimeDelta(0), "y", new TimeDelta(0)));
		checkOutput("True", t, V("x", new TimeDelta(0), "y", new TimeDelta(1)));
		checkOutput("True", t, V("x", new TimeDelta(0), "y", new TimeDelta(0, 1)));
		checkOutput("True", t, V("x", new TimeDelta(0), "y", new TimeDelta(0, 0, 1)));
		checkOutput("False", t, V("x", new MonthDelta(0), "y", new MonthDelta(0)));
		checkOutput("True", t, V("x", new MonthDelta(0), "y", new MonthDelta(1)));
		checkOutput("False", t, V("x", Date_.call(2015, 11, 12), "y", Date_.call(2015, 11, 12)));
		checkOutput("True", t, V("x", Date_.call(2015, 11, 12), "y", Date_.call(2015, 11, 13)));
		checkOutput("False", t, V("x", new Color(0x12, 0x34, 0x56, 0x78), "y", new Color(0x12, 0x34, 0x56, 0x78)));
		checkOutput("True", t, V("x", new Color(0x12, 0x34, 0x56, 0x78), "y", new Color(0x11, 0x34, 0x56, 0x78)));
		checkOutput("True", t, V("x", new Color(0x12, 0x34, 0x56, 0x78), "y", new Color(0x12, 0x33, 0x56, 0x78)));
		checkOutput("True", t, V("x", new Color(0x12, 0x34, 0x56, 0x78), "y", new Color(0x12, 0x34, 0x55, 0x78)));
		checkOutput("True", t, V("x", new Color(0x12, 0x34, 0x56, 0x78), "y", new Color(0x12, 0x34, 0x56, 0x77)));
		checkOutput("False", t, V("x", asList(), "y", asList()));
		checkOutput("False", t, V("x", asList(1, 2, 3), "y", asList(1, 2, 3)));
		checkOutput("False", t, V("x", asList(1, asList(2, asList(3))), "y", asList(1, asList(2, asList(3)))));
		checkOutput("True", t, V("x", asList(1, asList(2, asList(3))), "y", asList(1, asList(2, asList(4)))));
		checkOutput("True", t, V("x", asList(1, 2, 3), "y", asList(1, 2, 4)));
		checkOutput("True", t, V("x", asList(1, 2, 3), "y", asList(1, 2, 3, 4)));
		checkOutput("False", t, V("x", V(), "y", V()));
		checkOutput("False", t, V("x", V(1, 2, "foo", "bar"), "y", V(1, 2, "foo", "bar")));
		checkOutput("True", t, V("x", V(1, 2, "foo", "bar"), "y", V(1, 2, "foo", "baz")));
		checkOutput("True", t, V("x", V(1, 2, "foo", "bar", 3, 4), "y", V(1, 2, "foo", "bar", 5, 6)));
		checkOutput("False", t, V("x", makeSet(), "y", makeSet()));
		checkOutput("True", t, V("x", makeSet(42), "y", makeSet(new Long(42))));
		checkOutput("False", t, V("x", makeSet(1, "foo"), "y", makeSet(1, "foo")));
		checkOutput("True", t, V("x", makeSet(1, "foo"), "y", makeSet(1, "bar")));
		checkOutput("True", t, V("x", makeSet(1, 2), "y", makeSet(1, 2, 3)));

		// Check mixed type comparisons
		checkOutput("True", t, V("x", null, "y", true));
		checkOutput("True", t, V("x", null, "y", 42));
		checkOutput("True", t, V("x", 42, "y", "foo"));
		checkOutput("True", t, V("x", "foo", "y", asList()));
		checkOutput("True", t, V("x", asList(), "y", V()));
		checkOutput("True", t, V("x", V(), "y", makeSet()));
		checkOutput("True", t, V("x", "foo", "y", new Date()));

		// This checks constant folding
		checkOutput("True", T("<?print 17 != 23?>"));
		checkOutput("False", T("<?print 17 != 17.?>"));
	}

	@Test
	public void operator_lt()
	{
		Template t = T("<?print x < y?>");

		checkOutput("True", t, V("x", false, "y", true));
		checkOutput("False", t, V("x", true, "y", true));
		checkOutput("False", t, V("x", 1, "y", true));
		checkOutput("True", t, V("x", true, "y", 2));
		checkOutput("True", t, V("x", 17, "y", 23));
		checkOutput("False", t, V("x", 23, "y", 17));
		checkOutput("False", t, V("x", 17, "y", 17.0));
		checkOutput("True", t, V("x", 17, "y", 23.0));
		checkOutput("False", t, V("x", new TimeDelta(0), "y", new TimeDelta(0)));
		checkOutput("True", t, V("x", new TimeDelta(0), "y", new TimeDelta(1)));
		checkOutput("True", t, V("x", new TimeDelta(0), "y", new TimeDelta(0, 1)));
		checkOutput("True", t, V("x", new TimeDelta(0), "y", new TimeDelta(0, 0, 1)));
		checkOutput("False", t, V("x", new MonthDelta(0), "y", new MonthDelta(0)));
		checkOutput("True", t, V("x", new MonthDelta(0), "y", new MonthDelta(1)));
		checkOutput("True", t, V("x", "bar", "y", "foo"));
		checkOutput("False", t, V("x", "foo", "y", "foo"));
		checkOutput("True", t, V("x", "foobar", "y", "foobaz"));
		checkOutput("True", t, V("x", asList(1, 2), "y", asList(1, 2, 3)));
		checkOutput("False", t, V("x", asList(1, 3), "y", asList(1, 2)));
		checkOutput("True", t, V("x", asList(1, 2, "bar"), "y", asList(1, 2, "foo")));
		checkOutput("True", t, V("x", asList(1, 2, asList(3, "bar")), "y", asList(1, 2, asList(3, "foo"))));

		// This checks constant folding
		checkOutput("True", T("<?print 17 < 23?>"));
		checkOutput("False", T("<?print 17 < 17.?>"));
	}

	@Test
	public void operator_le()
	{
		Template t = T("<?print x <= y?>");

		checkOutput("True", t, V("x", false, "y", true));
		checkOutput("True", t, V("x", true, "y", true));
		checkOutput("True", t, V("x", 1, "y", true));
		checkOutput("True", t, V("x", true, "y", 2));
		checkOutput("True", t, V("x", 17, "y", 23));
		checkOutput("False", t, V("x", 23, "y", 17));
		checkOutput("True", t, V("x", 17, "y", 17));
		checkOutput("True", t, V("x", 17, "y", 17.0));
		checkOutput("False", t, V("x", new TimeDelta(1), "y", new TimeDelta(0)));
		checkOutput("True", t, V("x", new TimeDelta(0), "y", new TimeDelta(1)));
		checkOutput("True", t, V("x", new TimeDelta(0), "y", new TimeDelta(0, 1)));
		checkOutput("True", t, V("x", new TimeDelta(0), "y", new TimeDelta(0, 0, 1)));
		checkOutput("False", t, V("x", new MonthDelta(1), "y", new MonthDelta(0)));
		checkOutput("True", t, V("x", new MonthDelta(0), "y", new MonthDelta(1)));
		checkOutput("True", t, V("x", "bar", "y", "foo"));
		checkOutput("True", t, V("x", "foo", "y", "foo"));
		checkOutput("True", t, V("x", "foobar", "y", "foobaz"));
		checkOutput("True", t, V("x", asList(1, 2), "y", asList(1, 2)));
		checkOutput("True", t, V("x", asList(1, 2), "y", asList(1, 2, 3)));
		checkOutput("False", t, V("x", asList(1, 3), "y", asList(1, 2)));
		checkOutput("True", t, V("x", asList(1, 2, "foo"), "y", asList(1, 2, "foo")));
		checkOutput("True", t, V("x", asList(1, 2, "bar"), "y", asList(1, 2, "foo")));
		checkOutput("True", t, V("x", asList(1, 2, asList(3, "bar")), "y", asList(1, 2, asList(3, "foo"))));

		// This checks constant folding
		checkOutput("True", T("<?print 17 <= 23?>"));
		checkOutput("True", T("<?print 17 <= 17.?>"));
		checkOutput("True", T("<?print 17 <= 23.?>"));
		checkOutput("False", T("<?print 18 <= 17.?>"));
	}

	@Test
	public void operator_gt()
	{
		Template t = T("<?print x > y?>");

		checkOutput("False", t, V("x", false, "y", true));
		checkOutput("False", t, V("x", true, "y", true));
		checkOutput("False", t, V("x", 1, "y", true));
		checkOutput("True", t, V("x", 2, "y", true));
		checkOutput("False", t, V("x", 17, "y", 23));
		checkOutput("True", t, V("x", 23, "y", 17));
		checkOutput("False", t, V("x", 17, "y", 17.0));
		checkOutput("True", t, V("x", 23.0, "y", 17));
		checkOutput("False", t, V("x", new TimeDelta(0), "y", new TimeDelta(0)));
		checkOutput("True", t, V("x", new TimeDelta(1), "y", new TimeDelta(0)));
		checkOutput("True", t, V("x", new TimeDelta(0, 1), "y", new TimeDelta(0)));
		checkOutput("True", t, V("x", new TimeDelta(0, 0, 1), "y", new TimeDelta(0)));
		checkOutput("False", t, V("x", new MonthDelta(0), "y", new MonthDelta(0)));
		checkOutput("True", t, V("x", new MonthDelta(1), "y", new MonthDelta(0)));
		checkOutput("True", t, V("x", "foo", "y", "bar"));
		checkOutput("False", t, V("x", "foo", "y", "foo"));
		checkOutput("True", t, V("x", "foobaz", "y", "foobar"));
		checkOutput("True", t, V("x", asList(1, 2, 3), "y", asList(1, 2)));
		checkOutput("False", t, V("x", asList(1, 2), "y", asList(1, 3)));
		checkOutput("True", t, V("x", asList(1, 2, "foo"), "y", asList(1, 2, "bar")));
		checkOutput("True", t, V("x", asList(1, 2, asList(3, "foo")), "y", asList(1, 2, asList(3, "bar"))));

		// This checks constant folding
		checkOutput("False", T("<?print 17 > 23?>"));
		checkOutput("False", T("<?print 17 > 17.?>"));
		checkOutput("False", T("<?print 17 > 23.?>"));
		checkOutput("True", T("<?print 18 > 17.?>"));
	}

	@Test
	public void operator_ge()
	{
		Template t = T("<?print x >= y?>");

		checkOutput("False", t, V("x", false, "y", true));
		checkOutput("True", t, V("x", true, "y", true));
		checkOutput("True", t, V("x", 1, "y", true));
		checkOutput("False", t, V("x", true, "y", 2));
		checkOutput("False", t, V("x", 17, "y", 23));
		checkOutput("True", t, V("x", 23, "y", 17));
		checkOutput("True", t, V("x", 17, "y", 17));
		checkOutput("True", t, V("x", 17, "y", 17.0));
		checkOutput("False", t, V("x", new TimeDelta(0), "y", new TimeDelta(1)));
		checkOutput("True", t, V("x", new TimeDelta(1), "y", new TimeDelta(0)));
		checkOutput("True", t, V("x", new TimeDelta(0, 1), "y", new TimeDelta(0)));
		checkOutput("True", t, V("x", new TimeDelta(0, 0, 1), "y", new TimeDelta(0)));
		checkOutput("False", t, V("x", new MonthDelta(0), "y", new MonthDelta(1)));
		checkOutput("True", t, V("x", new MonthDelta(1), "y", new MonthDelta(0)));
		checkOutput("True", t, V("x", "foo", "y", "bar"));
		checkOutput("True", t, V("x", "foo", "y", "foo"));
		checkOutput("True", t, V("x", "foobaz", "y", "foobar"));
		checkOutput("True", t, V("x", asList(1, 2), "y", asList(1, 2)));
		checkOutput("True", t, V("x", asList(1, 2, 3), "y", asList(1, 2)));
		checkOutput("False", t, V("x", asList(1, 2), "y", asList(1, 3)));
		checkOutput("True", t, V("x", asList(1, 2, "foo"), "y", asList(1, 2, "foo")));
		checkOutput("True", t, V("x", asList(1, 2, "foo"), "y", asList(1, 2, "bar")));
		checkOutput("True", t, V("x", asList(1, 2, asList(3, "foo")), "y", asList(1, 2, asList(3, "bar"))));

		// This checks constant folding
		checkOutput("False", T("<?print 17 >= 23?>"));
		checkOutput("True", T("<?print 17 >= 17.?>"));
		checkOutput("False", T("<?print 17 >= 23.?>"));
		checkOutput("True", T("<?print 18 >= 17.?>"));
	}

	@Test
	public void operator_contains()
	{
		Template t = T("<?print x in y?>");

		checkOutput("True", t, V("x", 2, "y", asList(1, 2, 3)));
		checkOutput("False", t, V("x", 4, "y", asList(1, 2, 3)));
		checkOutput("True", t, V("x", 2, "y", new Integer[]{1, 2, 3}));
		checkOutput("False", t, V("x", 4, "y", new Integer[]{1, 2, 3}));
		checkOutput("True", t, V("x", "ur", "y", "gurk"));
		checkOutput("False", t, V("x", "un", "y", "gurk"));
		checkOutput("True", t, V("x", "a", "y", V("a", 1, "b", 2)));
		checkOutput("False", t, V("x", "c", "y", V("a", 1, "b", 2)));
		checkOutput("True", t, V("x", 0xff, "y", new Color(0x00, 0x80, 0xff, 0x42)));
		checkOutput("False", t, V("x", 0x23, "y", new Color(0x00, 0x80, 0xff, 0x42)));
	}

	@Test
	public void operator_notcontains()
	{
		Template t = T("<?print x not in y?>");

		checkOutput("False", t, V("x", 2, "y", asList(1, 2, 3)));
		checkOutput("True", t, V("x", 4, "y", asList(1, 2, 3)));
		checkOutput("False", t, V("x", 2, "y", new Integer[]{1, 2, 3}));
		checkOutput("True", t, V("x", 4, "y", new Integer[]{1, 2, 3}));
		checkOutput("False", t, V("x", "ur", "y", "gurk"));
		checkOutput("True", t, V("x", "un", "y", "gurk"));
		checkOutput("False", t, V("x", "a", "y", V("a", 1, "b", 2)));
		checkOutput("True", t, V("x", "c", "y", V("a", 1, "b", 2)));
		checkOutput("False", t, V("x", 0xff, "y", new Color(0x00, 0x80, 0xff, 0x42)));
		checkOutput("True", t, V("x", 0x23, "y", new Color(0x00, 0x80, 0xff, 0x42)));
	}

	@Test
	public void operator_and()
	{
		Template t = T("<?print x and y?>");

		checkOutput("False", t, V("x", false, "y", false));
		checkOutput("False", t, V("x", false, "y", true));
		checkOutput("0", t, V("x", 0, "y", true));
	}

	@Test
	public void operator_or()
	{
		Template t = T("<?print x or y?>");

		checkOutput("False", t, V("x", false, "y", false));
		checkOutput("True", t, V("x", false, "y", true));
		checkOutput("42", t, V("x", 42, "y", true));
	}

	@Test
	public void operator_not()
	{
		Template t = T("<?print not x?>");

		checkOutput("True", t, V("x", false));
		checkOutput("False", t, V("x", 42));
	}

	@Test
	public void expression_if()
	{
		Template t = T("<?print x if y else z?>");

		checkOutput("23", t, V("x", 17, "y", false, "z", 23));
		checkOutput("17", t, V("x", 17, "y", true, "z", 23));
	}

	@Test
	public void operator_getitem()
	{
		checkOutput("u", T("<?print 'gurk'[1]?>"));
		checkOutput("u", T("<?print x[1]?>"), V("x", "gurk"));
		checkOutput("u", T("<?print x[1]?>"), V("x", asList("g", "u", "r", "k")));

		checkOutput("u", T("<?print 'gurk'[-3]?>"));
		checkOutput("u", T("<?print x[-3]?>"), V("x", "gurk"));
		checkOutput("u", T("<?print x[-3]?>"), V("x", asList("g", "u", "r", "k")));
	}

	@CauseTest(expectedCause=StringIndexOutOfBoundsException.class)
	public void operator_getitem_fail_pos_stringliteral()
	{
		checkOutput("", T("<?print 'gurk'[4]?>"));
	}

	@CauseTest(expectedCause=StringIndexOutOfBoundsException.class)
	public void operator_getitem_fail_pos_string()
	{
		checkOutput("", T("<?print x[4]?>"), V("x", "gurk"));
	}

	@CauseTest(expectedCause=ArrayIndexOutOfBoundsException.class)
	public void operator_getitem_fail_pos_list()
	{
		checkOutput("", T("<?print x[4]?>"), V("x", asList("g", "u", "r", "k")));
	}

	@CauseTest(expectedCause=StringIndexOutOfBoundsException.class)
	public void operator_getitem_fail_neg_stringliteral()
	{
		checkOutput("", T("<?print 'gurk'[-5]?>"));
	}

	@CauseTest(expectedCause=StringIndexOutOfBoundsException.class)
	public void operator_getitem_fail_neg_string()
	{
		checkOutput("", T("<?print x[-5]?>"), V("x", "gurk"));
	}

	@CauseTest(expectedCause=ArrayIndexOutOfBoundsException.class)
	public void operator_getitem_fail_neg_list()
	{
		checkOutput("", T("<?print x[-5]?>"), V("x", asList("g", "u", "r", "k")));
	}

	@Test
	public void operator_getslice()
	{
		checkOutput("ur", T("<?print 'gurk'[1:3]?>"));
		checkOutput("ur", T("<?print x[1:3]?>"), V("x", "gurk"));
		checkOutput("ur", T("<?print 'gurk'[-3:-1]?>"));
		checkOutput("ur", T("<?print x[-3:-1]?>"), V("x", "gurk"));
		checkOutput("", T("<?print 'gurk'[4:10]?>"));
		checkOutput("", T("<?print x[4:10]?>"), V("x", "gurk"));
		checkOutput("", T("<?print 'gurk'[-10:-5]?>"));
		checkOutput("", T("<?print x[-10:-5]?>"), V("x", "gurk"));
		checkOutput("urk", T("<?print 'gurk'[1:]?>"));
		checkOutput("urk", T("<?print x[1:]?>"), V("x", "gurk"));
		checkOutput("urk", T("<?print 'gurk'[-3:]?>"));
		checkOutput("urk", T("<?print x[-3:]?>"), V("x", "gurk"));
		checkOutput("", T("<?print 'gurk'[4:]?>"));
		checkOutput("", T("<?print x[4:]?>"), V("x", "gurk"));
		checkOutput("gurk", T("<?print 'gurk'[-10:]?>"));
		checkOutput("gurk", T("<?print x[-10:]?>"), V("x", "gurk"));
		checkOutput("gur", T("<?print 'gurk'[:3]?>"));
		checkOutput("gur", T("<?print x[:3]?>"), V("x", "gurk"));
		checkOutput("gur", T("<?print 'gurk'[:-1]?>"));
		checkOutput("gur", T("<?print x[:-1]?>"), V("x", "gurk"));
		checkOutput("gurk", T("<?print 'gurk'[:10]?>"));
		checkOutput("gurk", T("<?print x[:10]?>"), V("x", "gurk"));
		checkOutput("", T("<?print 'gurk'[:-5]?>"));
		checkOutput("", T("<?print x[:-5]?>"), V("x", "gurk"));
		checkOutput("['u', 'r']", T("<?print x[1:3]?>"), V("x", asList("g", "u", "r", "k")));
		checkOutput("['u', 'r']", T("<?print x[-3:-1]?>"), V("x", asList("g", "u", "r", "k")));
		checkOutput("[]", T("<?print x[4:10]?>"), V("x", asList("g", "u", "r", "k")));
		checkOutput("[]", T("<?print x[-10:-5]?>"), V("x", asList("g", "u", "r", "k")));
		checkOutput("['u', 'r', 'k']", T("<?print x[1:]?>"), V("x", asList("g", "u", "r", "k")));
		checkOutput("['u', 'r', 'k']", T("<?print x[-3:]?>"), V("x", asList("g", "u", "r", "k")));
		checkOutput("[]", T("<?print x[4:]?>"), V("x", asList("g", "u", "r", "k")));
		checkOutput("['g', 'u', 'r', 'k']", T("<?print x[-10:]?>"), V("x", asList("g", "u", "r", "k")));
		checkOutput("['g', 'u', 'r']", T("<?print x[:3]?>"), V("x", asList("g", "u", "r", "k")));
		checkOutput("['g', 'u', 'r']", T("<?print x[:-1]?>"), V("x", asList("g", "u", "r", "k")));
		checkOutput("['g', 'u', 'r', 'k']", T("<?print x[:10]?>"), V("x", asList("g", "u", "r", "k")));
		checkOutput("[]", T("<?print x[:-5]?>"), V("x", asList("g", "u", "r", "k")));
	}

	@Test
	public void operator_setslice()
	{
		checkOutput("[1, -2, -3, 4]", T("<?code x = [1, 2, 3, 4]?><?code x[1:3] = [-2, -3]?><?print x?>"));
		checkOutput("[1, -1, -4, -9, 4]", T("<?code x = [1, 2, 3, 4]?><?code x[1:-1] = (-i*i for i in range(1, 4))?><?print x?>"));
		checkOutput("[-1, -4, -9]", T("<?code x = [1, 2, 3, 4]?><?code x[:] = (-i*i for i in range(1, 4))?><?print x?>"));
	}

	@Test
	public void nested()
	{
		String sc = "4";
		String sv = "x";
		int n = 4;
		int depth = 10;
		for (int i = 0; i < depth; ++i)
		{
			sc = "(" + sc + ")+(" + sc + ")";
			sv = "(" + sv + ")+(" + sv + ")";
			n = n + n;
		}
		String expected = Integer.toString(n);
		checkOutput(expected, T("<?print " + sc + "?>"));
		checkOutput(expected, T("<?print " + sv + "?>"), V("x", 4));
	}

	@Test
	public void precedence()
	{
		checkOutput("10", T("<?print 2*3+4?>"));
		checkOutput("14", T("<?print 2+3*4?>"));
		checkOutput("20", T("<?print (2+3)*4?>"));
		checkOutput("10", T("<?print -2+-3*-4?>"));
		checkOutput("14", T("<?print --2+--3*--4?>"));
		checkOutput("14", T("<?print (-(-2))+(-((-3)*-(-4)))?>"));
		checkOutput("42", T("<?print 2*data.value?>"), V("data", V("value", 21)));
		checkOutput("42", T("<?print data.value[0]?>"), V("data", V("value", asList(42))));
		checkOutput("42", T("<?print data[0].value?>"), V("data", asList(makeMap("value", 42))));
		checkOutput("42", T("<?print data[0][0][0]?>"), V("data", asList(asList(asList(42)))));
		checkOutput("42", T("<?print data.value.value[0]?>"), V("data", V("value", V("value", asList(42)))));
		checkOutput("42", T("<?print data.value.value[0].value.value[0]?>"), V("data", V("value", V("value", asList(makeMap("value", V("value", asList(42))))))));
	}

	@Test
	public void associativity()
	{
		checkOutput("9", T("<?print 2+3+4?>"));
		checkOutput("-5", T("<?print 2-3-4?>"));
		checkOutput("24", T("<?print 2*3*4?>"));
		checkOutput("2.0", T("<?print 24/6/2?>"));
		checkOutput("2", T("<?print 24//6//2?>"));
	}

	@Test
	public void bracket()
	{
		String sc = "42";
		String sv = "x";
		for (int i = 0; i < 10; ++i)
		{
			sc = "(" + sc + ")";
			sv = "(" + sv + ")";
		}

		checkOutput("42", T("<?print " + sc + "?>"));
		checkOutput("42", T("<?print " + sv + "?>"), V("x", 42));
	}

	@Test
	public void function_now()
	{
		String output = T("<?print now()?>").renders();
		assertTrue(output.compareTo("2012-03-28") > 0);
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_now_1_args()
	{
		checkOutput("", T("<?print now(1)?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_now_2_args()
	{
		checkOutput("", T("<?print now(1, 2)?>"));
	}

	@Test
	public void function_utcnow()
	{
		String output = T("<?print utcnow()?>").renders();
		assertTrue(output.compareTo("2012-03-28") > 0);
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_utcnow_1_args()
	{
		checkOutput("", T("<?print utcnow(1)?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_utcnow_2_args()
	{
		checkOutput("", T("<?print utcnow(1, 2)?>"));
	}

	@Test
	public void function_date()
	{
		checkOutput("@(2012-10-06)", T("<?print repr(date(2012, 10, 6))?>"));
		// The following is only implemented for backwards compatibility
		// and will go away again.
		checkOutput("@(2012-10-06T12:34:56.987654)", T("<?print repr(date(2012, 10, 6, 12, 34, 56, 987654))?>"));
	}

	@Test
	public void function_datetime()
	{
		checkOutput("@(2012-10-06T)", T("<?print repr(datetime(2012, 10, 6))?>"));
		checkOutput("@(2012-10-06T12:00)", T("<?print repr(datetime(2012, 10, 6, 12))?>"));
		checkOutput("@(2012-10-06T12:34)", T("<?print repr(datetime(2012, 10, 6, 12, 34))?>"));
		checkOutput("@(2012-10-06T12:34:56)", T("<?print repr(datetime(2012, 10, 6, 12, 34, 56))?>"));
		checkOutput("@(2012-10-06T12:34:56.987654)", T("<?print repr(datetime(2012, 10, 6, 12, 34, 56, 987654))?>"));
		checkOutput("@(2012-10-06T12:34:56.987654)", T("<?print repr(datetime(year=2012, month=10, day=6, hour=12, minute=34, second=56, microsecond=987654))?>"));

		// date() is the best candidate for testing a mixture of the argument passing methods
		checkOutput("@(2012-10-06T12:34:56)", T("<?print repr(datetime(2012, *[10], *[6], hour=12, **{'minute': 34}, **{'second': 56}))?>"));
	}

	@Test
	public void function_timedelta()
	{
		checkOutput("0:00:00", T("<?print timedelta()?>"));
		checkOutput("1 day, 0:00:00", T("<?print timedelta(1)?>"));
		checkOutput("2 days, 0:00:00", T("<?print timedelta(2)?>"));
		checkOutput("0:00:01", T("<?print timedelta(0, 1)?>"));
		checkOutput("0:01:00", T("<?print timedelta(0, 60)?>"));
		checkOutput("1:00:00", T("<?print timedelta(0, 60*60)?>"));
		checkOutput("1 day, 1:01:01.000001", T("<?print timedelta(1, 60*60+60+1, 1)?>"));
		checkOutput("0:00:00.000001", T("<?print timedelta(0, 0, 1)?>"));
		checkOutput("0:00:01", T("<?print timedelta(0, 0, 1000000)?>"));
		checkOutput("1 day, 0:00:00", T("<?print timedelta(0, 0, 24*60*60*1000000)?>"));
		checkOutput("1 day, 0:00:00", T("<?print timedelta(0, 24*60*60)?>"));
		checkOutput("-1 day, 0:00:00", T("<?print timedelta(-1)?>"));
		checkOutput("-1 day, 23:59:59", T("<?print timedelta(0, -1)?>"));
		checkOutput("-1 day, 23:59:59.999999", T("<?print timedelta(0, 0, -1)?>"));
		checkOutput("12:00:00", T("<?print timedelta(0.5)?>"));
		checkOutput("0:00:00.500000", T("<?print timedelta(0, 0.5)?>"));
		checkOutput("0:00:00.500000", T("<?print timedelta(0.5/(24*60*60))?>"));
		checkOutput("-1 day, 12:00:00", T("<?print timedelta(-0.5)?>"));
		checkOutput("-1 day, 23:59:59.500000", T("<?print timedelta(0, -0.5)?>"));
		checkOutput("1 day, 0:00:01.000001", T("<?print timedelta(days=1, seconds=1, microseconds=1)?>"));
	}

	@Test
	public void function_monthdelta()
	{
		checkOutput("0 months", T("<?print monthdelta()?>"));
		checkOutput("1 month", T("<?print monthdelta(1)?>"));
		checkOutput("2 months", T("<?print monthdelta(2)?>"));
		checkOutput("-1 month", T("<?print monthdelta(-1)?>"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_monthdelta_bad_kwarg()
	{
		T("<?print monthdelta(months=1)?>").renders();
	}

	@Test
	public void module_color_function_Color()
	{
		checkOutput("#000", T("<?print repr(color.Color(0, 0, 0))?>"));
		checkOutput("#0000", T("<?print repr(color.Color(0, 0, 0, 0))?>"));
		checkOutput("#369c", T("<?print repr(color.Color(51, 102, 153, 204))?>"));
	}

	@Test
	public void module_color_function_css()
	{
		checkOutput("#000", T("<?print repr(color.css('black'))?>"));
		checkOutput("#fff", T("<?print repr(color.css('white'))?>"));
		checkOutput("#123", T("<?print repr(color.css('#123'))?>"));
		checkOutput("#1234", T("<?print repr(color.css('#1234'))?>"));
		checkOutput("#123456", T("<?print repr(color.css('#123456'))?>"));
		checkOutput("#12345678", T("<?print repr(color.css('#12345678'))?>"));
		checkOutput("#136", T("<?print repr(color.css('rgb(17, 20%, 40%)'))?>"));
		checkOutput("#1369", T("<?print repr(color.css('rgba(17, 20%, 40%, 0.6)'))?>"));
		checkOutput("#1369", T("<?print repr(color.css('rgba(17, 20%, 40%, 60%)'))?>"));
		checkOutput("#123", T("<?print repr(color.css('bad', #123))?>"));
	}

	@Test
	public void module_color_function_mix()
	{
		checkOutput("#aaa", T("<?print repr(color.mix(#000, #fff, #fff))?>"));
		checkOutput("#555", T("<?print repr(color.mix(#000, #000, #fff))?>"));
		checkOutput("#aaaa", T("<?print repr(color.mix(#0000, #ffff, #ffff))?>"));
		checkOutput("#aaa", T("<?print repr(color.mix(#000, 2, #fff))?>"));
		checkOutput("#12c", T("<?print repr(color.mix(#f00, 2, #0f0, 12, #00f))?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_random_1_args()
	{
		checkOutput("", T("<?print random(1)?>"));
	}

	@Test
	public void function_randrange()
	{
		checkOutput("ok", T("<?code r = randrange(4)?><?if r>=0 and r<4?>ok<?else?>fail<?end if?>"));
		checkOutput("ok", T("<?code r = randrange(17, 23)?><?if r>=17 and r<23?>ok<?else?>fail<?end if?>"));
		checkOutput("ok", T("<?code r = randrange(17, 23, 2)?><?if r>=17 and r<23 and r%2?>ok<?else?>fail<?end if?>"));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_randrange_0_args()
	{
		checkOutput("", T("<?print randrange()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_randrange_4_args()
	{
		checkOutput("", T("<?print randrange(1, 2, 3, 4)?>"));
	}

	@Test
	public void function_randchoice()
	{
		checkOutput("ok", T("<?code r = randchoice('abc')?><?if r in 'abc'?>ok<?else?>fail<?end if?>"));
		checkOutput("ok", T("<?code s = [17, 23, 42]?><?code r = randchoice(s)?><?if r in s?>ok<?else?>fail<?end if?>"));
		checkOutput("ok", T("<?code s = #12345678?><?code sl = [0x12, 0x34, 0x56, 0x78]?><?code r = randchoice(s)?><?if r in sl?>ok<?else?>fail<?end if?>"));
		checkOutput("ok", T("<?code r = randchoice(seq='abc')?><?if r in 'abc'?>ok<?else?>fail<?end if?>"));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	@Test
	public void function_randchoice_0_args()
	{
		checkOutput("", T("<?print randchoice()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_randchoice_2_args()
	{
		checkOutput("", T("<?print randchoice(1, 2)?>"));
	}

	@Test
	public void function_xmlescape()
	{
		checkOutput("&lt;&lt;&gt;&gt;&amp;&#39;&quot;gurk", T("<?print xmlescape(data)?>"), V("data", "<<>>&'\"gurk"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_xmlescape_bad_kwarg()
	{
		T("<?print xmlescape(obj=data)?>").renders(V("data", 42));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	@Test
	public void function_xmlescape_0_args()
	{
		checkOutput("", T("<?print xmlescape()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_xmlescape_2_args()
	{
		checkOutput("", T("<?print xmlescape(1, 2)?>"));
	}

	@Test
	public void function_csv()
	{
		checkOutput("", T("<?print csv(data)?>"), V("data", null));
		checkOutput("False", T("<?print csv(data)?>"), V("data", false));
		checkOutput("True", T("<?print csv(data)?>"), V("data", true));
		checkOutput("42", T("<?print csv(data)?>"), V("data", 42));
		// no check for float
		checkOutput("abc", T("<?print csv(data)?>"), V("data", "abc"));
		checkOutput("\"a,b,c\"", T("<?print csv(data)?>"), V("data", "a,b,c"));
		checkOutput("\"a\"\"b\"\"c\"", T("<?print csv(data)?>"), V("data", "a\"b\"c"));
		checkOutput("\"a\nb\nc\"", T("<?print csv(data)?>"), V("data", "a\nb\nc"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_csv_bad_kwarg()
	{
		T("<?print csv(obj=data)?>").renders(V("data", 42));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	@Test
	public void function_csv_0_args()
	{
		checkOutput("", T("<?print csv()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_csv_2_args()
	{
		checkOutput("", T("<?print csv(1, 2)?>"));
	}

	@Test
	public void function_asjson()
	{
		checkOutput("null", T("<?print asjson(data)?>"), V("data", null));
		checkOutput("false", T("<?print asjson(data)?>"), V("data", false));
		checkOutput("true", T("<?print asjson(data)?>"), V("data", true));
		checkOutput("42", T("<?print asjson(data)?>"), V("data", 42));
		checkOutput("42.5", T("<?print asjson(data)?>"), V("data", 42.5));
		checkOutput("42.5", T("<?print asjson(data)?>"), V("data", new BigDecimal("42.5")));
		checkOutput("\"abc\"", T("<?print asjson(data)?>"), V("data", "abc"));
		checkOutput("\"'\"", T("<?print asjson(data)?>"), V("data", "'"));
		checkOutput("\"\\\"\"", T("<?print asjson(data)?>"), V("data", "\""));
		checkOutput("\"\\u003c\"", T("<?print asjson(data)?>"), V("data", "<"));
		checkOutput("[1, 2, 3]", T("<?print asjson(data)?>"), V("data", asList(1, 2, 3)));
		checkOutput("[1, 2, 3]", T("<?print asjson(data)?>"), V("data", new Integer[]{1, 2, 3}));
		checkOutput("{\"one\": 1}", T("<?print asjson(data)?>"), V("data", V("one", 1)));
		checkOutput("new ul4.TimeDelta(1, 1, 1)", T("<?print asjson(data)?>"), V("data", new TimeDelta(1, 1, 1)));
		checkOutput("new ul4.MonthDelta(1)", T("<?print asjson(data)?>"), V("data", new MonthDelta(1)));
		checkOutput("new ul4.Date_(2000, 2, 29)", T("<?print asjson(data)?>"), V("data", LocalDate.of(2000, 2, 29)));
		checkOutput("new Date(2000, 1, 29, 12, 34, 56, 987)", T("<?print asjson(data)?>"), V("data", LocalDateTime.of(2000, 2, 29, 12, 34, 56, 987654321)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_asjson_bad_kwarg()
	{
		T("<?print asjson(obj=data)?>").renders(V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	@Test
	public void function_asjson_0_args()
	{
		checkOutput("", T("<?print asjson()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_asjson_2_args()
	{
		checkOutput("", T("<?print asjson(1, 2)?>"));
	}

	@Test
	public void function_fromjson()
	{
		checkOutput("None", T("<?print repr(fromjson(data))?>"), V("data", "null"));
		checkOutput("False", T("<?print repr(fromjson(data))?>"), V("data", "false"));
		checkOutput("True", T("<?print repr(fromjson(data))?>"), V("data", "true"));
		checkOutput("42", T("<?print repr(fromjson(data))?>"), V("data", "42"));
		checkOutput("'abc'", T("<?print repr(fromjson(data))?>"), V("data", "\"abc\""));
		checkOutput("[1, 2, 3]", T("<?print repr(fromjson(data))?>"), V("data", "[1,2,3]"));
		checkOutput("{'eins': 42}", T("<?print repr(fromjson(data))?>"), V("data", "{\"eins\": 42}"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_fromjson_bad_kwarg()
	{
		T("<?print repr(fromjson(string=data))?>").renders(V("data", "null"));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	@Test
	public void function_fromjson_0_args()
	{
		checkOutput("", T("<?print fromjson()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_fromjson_2_args()
	{
		checkOutput("", T("<?print fromjson(1, 2)?>"));
	}

	@Test
	public void function_asul4on()
	{
		checkOutput(dumps(null), T("<?print asul4on(data)?>"), V("data", null));
		checkOutput(dumps(false), T("<?print asul4on(data)?>"), V("data", false));
		checkOutput(dumps(true), T("<?print asul4on(data)?>"), V("data", true));
		checkOutput(dumps(42), T("<?print asul4on(data)?>"), V("data", 42));
		checkOutput(dumps(42.5), T("<?print asul4on(data)?>"), V("data", 42.5));
		checkOutput(dumps("abc"), T("<?print asul4on(data)?>"), V("data", "abc"));
		checkOutput(dumps(asList(1, 2, 3)), T("<?print asul4on(data)?>"), V("data", asList(1, 2, 3)));
		checkOutput(dumps(makeMap("one", 1)), T("<?print asul4on(data)?>"), V("data", V("one", 1)));
		// Trst pretty printing
		checkOutput("L\n\ti1\n\ti2\n\ti3\n]\n", T("<?print asul4on([1, 2, 3], '\\t')?>"));
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	@Test
	public void function_asul4on_bad_kwarg()
	{
		T("<?print asul4on(obj=42)?>").renders();
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	@Test
	public void function_asul4on_0_args()
	{
		checkOutput("", T("<?print asul4on()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_asul4on_3_args()
	{
		checkOutput("", T("<?print asul4on(1, 2, 3)?>"));
	}

	@Test
	public void function_fromul4on()
	{
		checkOutput("None", T("<?print repr(fromul4on(dump))?>"), V("dump", dumps(null)));
		checkOutput("False", T("<?print repr(fromul4on(dump))?>"), V("dump", dumps(false)));
		checkOutput("True", T("<?print repr(fromul4on(dump))?>"), V("dump", dumps(true)));
		checkOutput("42", T("<?print repr(fromul4on(dump))?>"), V("dump", dumps(42)));
		checkOutput("42.5", T("<?print repr(fromul4on(dump))?>"), V("dump", dumps(42.5)));
		checkOutput("'abc'", T("<?print repr(fromul4on(dump))?>"), V("dump", dumps("abc")));
		checkOutput("[1, 2, 3]", T("<?print repr(fromul4on(dump))?>"), V("dump", dumps(asList(1, 2, 3))));
		checkOutput("{'one': 1}", T("<?print repr(fromul4on(dump))?>"), V("dump", dumps(V("one", 1))));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_fromul4on_bad_kwarg()
	{
		T("<?print fromul4on(dump='i42')?>").renders();
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	@Test
	public void function_fromul4on_0_args()
	{
		checkOutput("", T("<?print fromul4on()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_fromul4on_2_args()
	{
		checkOutput("", T("<?print fromul4on(1, 2)?>"));
	}

	@Test
	public void function_str()
	{
		Template t = T("<?print str(data)?>");

		checkOutput("", T("<?print str()?>"));
		checkOutput("", t, V("data", null));
		checkOutput("True", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("42", t, V("data", 42));
		checkOutput("4.2", t, V("data", 4.2));
		checkOutput("foo", t, V("data", "foo"));
		checkOutput("broken", t, V("data", new RuntimeException("broken")));
		checkOutput("2011-02-09", t, V("data", Date_.call(2011, 2, 9)));
		checkOutput("2011-02-09 00:00", t, V("data", DateTime.call(2011, 2, 9)));
		checkOutput("2011-02-09 12:34", t, V("data", DateTime.call(2011, 2, 9, 12, 34)));
		checkOutput("2011-02-09 12:34:56", t, V("data", DateTime.call(2011, 2, 9, 12, 34, 56)));
		checkOutput("2011-02-09 12:34:56.987654", t, V("data", DateTime.call(2011, 2, 9, 12, 34, 56, 987654)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_str_bad_kwarg()
	{
		T("<?print str(obj=data)?>").renders(V("data", "foo"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_str_2_args()
	{
		checkOutput("", T("<?print str(1, 2)?>"));
	}

	@Test
	public void function_bool()
	{
		checkOutput("False", T("<?print bool()?>"));
		Template t = T("<?print bool(data)?>");

		checkOutput("True", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 0));
		checkOutput("True", t, V("data", 42));
		checkOutput("True", t, V("data", new Long(42)));
		checkOutput("False", t, V("data", new BigInteger("0")));
		checkOutput("True", t, V("data", new BigInteger("42")));
		checkOutput("False", t, V("data", 0.0f));
		checkOutput("True", t, V("data", 4.2f));
		checkOutput("False", t, V("data", 0.0));
		checkOutput("True", t, V("data", 4.2));
		checkOutput("False", t, V("data", new BigDecimal("0.000")));
		checkOutput("True", t, V("data", new BigDecimal("42.5")));
		checkOutput("False", t, V("data", ""));
		checkOutput("True", t, V("data", "foo"));
		checkOutput("False", t, V("data", asList()));
		checkOutput("True", t, V("data", asList("foo", "bar")));
		checkOutput("False", t, V("data", new Integer[]{}));
		checkOutput("True", t, V("data", new Integer[]{1, 2, 3}));
		checkOutput("False", t, V("data", V()));
		checkOutput("True", t, V("data", V("foo", "bar")));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("True", t, V("data", makeSet("foo", "bar")));
		checkOutput("False", t, V("data", new Point(0, 0)));
		checkOutput("True", t, V("data", new Point(17, 23)));
		checkOutput("True", t, V("data", new Object()));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_bool_bad_kwarg()
	{
		T("<?print bool(obj=data)?>").renders(V("data", true));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_bool_2_args()
	{
		checkOutput("", T("<?print bool(1, 2)?>"));
	}

	@Test
	public void function_int()
	{
		checkOutput("0", T("<?print int()?>"));
		checkOutput("1", T("<?print int(data)?>"), V("data", true));
		checkOutput("0", T("<?print int(data)?>"), V("data", false));
		checkOutput("42", T("<?print int(data)?>"), V("data", 42));
		checkOutput("4", T("<?print int(data)?>"), V("data", 4.2));
		checkOutput("42", T("<?print int(data)?>"), V("data", "42"));
		checkOutput("9999999999", T("<?print int(data)?>"), V("data", "9999999999"));
		checkOutput("999999999999999999999999", T("<?print int(data)?>"), V("data", "999999999999999999999999"));
		checkOutput("999999999999999999999999", T("<?print int(data)?>"), V("data", new BigInteger("999999999999999999999999")));
		checkOutput("66", T("<?print int(data, 16)?>"), V("data", "42"));
		checkOutput("66", T("<?print int(data, base=16)?>"), V("data", "42"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_int_bad_kwarg()
	{
		T("<?print int(obj=data, base=None)?>").renders(V("data", "42"));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_int_null()
	{
		T("<?print int(data)?>").renders(V("data", null));
	}

	@CauseTest(expectedCause=NumberFormatException.class)
	public void function_int_badstring()
	{
		checkOutput("", T("<?print int(data)?>"), V("data", "foo"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_int_3_args()
	{
		checkOutput("", T("<?print int(1, 2, 3)?>"));
	}

	@Test
	public void function_float()
	{
		Template t = T("<?print float(data)?>");

		checkOutput("0.0", T("<?print float()?>"));
		checkOutput("1.0", t, V("data", true));
		checkOutput("0.0", t, V("data", false));
		checkOutput("42.0", t, V("data", 42));
		checkOutput("42.0", t, V("data", "42"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_float_bad_kwarg()
	{
		T("<?print float(x=data)?>").renders(V("data", true));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_float_null()
	{
		checkOutput("", T("<?print float(data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=NumberFormatException.class)
	public void function_float_badstring()
	{
		checkOutput("", T("<?print float(data)?>"), V("data", "foo"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_float_2_args()
	{
		checkOutput("", T("<?print float(1, 2)?>"));
	}

	@Test
	public void function_list()
	{
		checkOutput("[]", T("<?print list()?>"));
		Template t = T("<?print list(data)?>");

		checkOutput("[1, 2]", t, V("data", asList(1, 2)));
		checkOutput("['g', 'u', 'r', 'k']", t, V("data", "gurk"));
		checkOutput("[['foo', 42]]", T("<?print repr(list(data.items()))?>"), V("data", V("foo", 42)));
		checkOutput("[0, 1, 2]", T("<?print repr(list(range(3)))?>"));
		checkOutput("[1, 2, 3]", t, V("data", new Integer[]{1, 2, 3}));
		checkOutput("[1, 2, 3]", T("<?print list(data)?>"), V("data", new Iterate()));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_list_bad_kwarg()
	{
		T("<?print list(iterable=data)?>").renders(V("data", "gurk"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_list_2_args()
	{
		checkOutput("", T("<?print list(1, 2)?>"));
	}

	@Test
	public void function_dict()
	{
		checkOutput("{}", T("<?print dict()?>"));
		checkOutput("{17: 23, 42: 73}", T("<?print dict(data)?>"), V("data", asList(asList(17, 23), asList(42, 73))));
		checkOutput("{'foo': 23, 'bar': 42}", T("<?print dict({'foo': 17}, foo=23, bar=42)?>"));
	}

	@Test
	public void function_len()
	{
		Template t = T("<?print len(data)?>");

		checkOutput("3", t, V("data", "foo"));
		checkOutput("3", t, V("data", asList(1, 2, 3)));
		checkOutput("3", t, V("data", new Integer[]{1, 2, 3}));
		checkOutput("3", t, V("data", V("a", 1, "b", 2, "c", 3)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_len_bad_kwarg()
	{
		T("<?print len(sequence=data)?>").renders(V("data", "foo"));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_len_0_args()
	{
		checkOutput("", T("<?print len()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_len_2_args()
	{
		checkOutput("", T("<?print len(1, 2)?>"));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_len_null()
	{
		checkOutput("", T("<?print len(data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_len_true()
	{
		checkOutput("", T("<?print len(data)?>"), V("data", true));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_len_false()
	{
		checkOutput("", T("<?print len(data)?>"), V("data", false));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_len_int()
	{
		checkOutput("", T("<?print len(data)?>"), V("data", 42));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_len_float()
	{
		checkOutput("", T("<?print len(data)?>"), V("data", 42.4));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_len_point()
	{
		checkOutput("", T("<?print len(data)?>"), V("data", new Point(17, 23)));
	}

	@Test
	public void function_any()
	{
		checkOutput("False", T("<?print any('')?>"));
		checkOutput("True", T("<?print any('foo')?>"));
		checkOutput("True", T("<?print any(i > 7 for i in range(10))?>"));
		checkOutput("False", T("<?print any(i > 17 for i in range(10))?>"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_any_bad_kwarg()
	{
		T("<?print any(iterable='foo')?>").renders();
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_any_0_args()
	{
		checkOutput("", T("<?print any()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_any_2_args()
	{
		checkOutput("", T("<?print any(1, 2)?>"));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_any_none()
	{
		checkOutput("", T("<?print any(None)?>"));
	}

	@Test
	public void function_all()
	{
		checkOutput("True", T("<?print all('')?>"));
		checkOutput("True", T("<?print all('foo')?>"));
		checkOutput("False", T("<?print all(i < 7 for i in range(10))?>"));
		checkOutput("True", T("<?print all(i < 17 for i in range(10))?>"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_all_bad_kwarg()
	{
		T("<?print all(iterable='foo')?>").renders();
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_all_0_args()
	{
		checkOutput("", T("<?print all()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_all_2_args()
	{
		checkOutput("", T("<?print all(1, 2)?>"));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_all_none()
	{
		checkOutput("", T("<?print all(None)?>"));
	}

	@Test
	public void function_enumerate()
	{
		Template template1 = T("<?for (i, value) in enumerate(data)?>(<?print value?>=<?print i?>)<?end for?>");
		checkOutput("(f=0)(o=1)(o=2)", template1, V("data", "foo"));
		checkOutput("(foo=0)(bar=1)", template1, V("data", asList("foo", "bar")));
		checkOutput("(foo=0)", template1, V("data", V("foo", true)));

		Template template2 = T("<?for (i, value) in enumerate(data, 42)?>(<?print value?>=<?print i?>)<?end for?>");
		checkOutput("(f=42)(o=43)(o=44)", template2, V("data", "foo"));

		Template template2kw = T("<?for (i, value) in enumerate(iterable=data, start=42)?>(<?print value?>=<?print i?>)<?end for?>");
		checkOutput("(f=42)(o=43)(o=44)", template2kw, V("data", "foo"));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_enumerate_0_args()
	{
		checkOutput("", T("<?print enumerate()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_enumerate_3_args()
	{
		checkOutput("", T("<?print enumerate(1, 2, 3)?>"));
	}

	@CauseTest(expectedCause=NotIterableException.class)
	public void function_enumerate_null()
	{
		checkOutput("", T("<?print enumerate(data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=NotIterableException.class)
	public void function_enumerate_true()
	{
		checkOutput("", T("<?print enumerate(data)?>"), V("data", true));
	}

	@CauseTest(expectedCause=NotIterableException.class)
	public void function_enumerate_false()
	{
		checkOutput("", T("<?print enumerate(data)?>"), V("data", false));
	}

	@CauseTest(expectedCause=NotIterableException.class)
	public void function_enumerate_int()
	{
		checkOutput("", T("<?print enumerate(data)?>"), V("data", 42));
	}

	@CauseTest(expectedCause=NotIterableException.class)
	public void function_enumerate_float()
	{
		checkOutput("", T("<?print enumerate(data)?>"), V("data", 42.4));
	}

	@Test
	public void function_enumfl()
	{
		Template template1 = T("<?for (i, f, l, value) in enumfl(data)?><?if f?>[<?end if?>(<?print value?>=<?print i?>)<?if l?>]<?end if?><?end for?>");
		checkOutput("", template1, V("data", ""));
		checkOutput("[(?=0)]", template1, V("data", "?"));
		checkOutput("[(f=0)(o=1)(o=2)]", template1, V("data", "foo"));
		checkOutput("[(foo=0)(bar=1)]", template1, V("data", asList("foo", "bar")));
		checkOutput("[(foo=0)]", template1, V("data", V("foo", true)));

		Template template2 = T("<?for (i, f, l, value) in enumfl(data, 42)?><?if f?>[<?end if?>(<?print value?>=<?print i?>)<?if l?>]<?end if?><?end for?>");
		checkOutput("[(f=42)(o=43)(o=44)]", template2, V("data", "foo"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_enumfl_bad_kwarg()
	{
		T("<?print enumfl(iterable=data, start=42)?>").renders(V("data", "foo"));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_enumfl_0_args()
	{
		checkOutput("", T("<?print enumfl()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_enumfl_3_args()
	{
		checkOutput("", T("<?print enumfl(1, 2, 3)?>"));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_enumfl_null()
	{
		checkOutput("", T("<?print enumfl(data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_enumfl_true()
	{
		checkOutput("", T("<?print enumfl(data)?>"), V("data", true));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_enumfl_false()
	{
		checkOutput("", T("<?print enumfl(data)?>"), V("data", false));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_enumfl_int()
	{
		checkOutput("", T("<?print enumfl(data)?>"), V("data", 42));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_enumfl_float()
	{
		checkOutput("", T("<?print enumfl(data)?>"), V("data", 42.4));
	}

	@Test
	public void function_isfirstlast()
	{
		Template t = T("<?for (f, l, value) in isfirstlast(data)?><?if f?>[<?end if?>(<?print value?>)<?if l?>]<?end if?><?end for?>");

		checkOutput("", t, V("data", ""));
		checkOutput("[(?)]", t, V("data", "?"));
		checkOutput("[(f)(o)(o)]", t, V("data", "foo"));
		checkOutput("[(foo)(bar)]", t, V("data", asList("foo", "bar")));
		checkOutput("[(foo)]", t, V("data", V("foo", true)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isfirstlast_bad_kwarg()
	{
		Template templatekw = T("<?for (f, l, value) in isfirstlast(iterable=data)?><?if f?>[<?end if?>(<?print value?>)<?if l?>]<?end if?><?end for?>");
		checkOutput("[(f)(o)(o)]", templatekw, V("data", "foo"));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isfirstlast_0_args()
	{
		checkOutput("", T("<?print isfirstlast()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isfirstlast_2_args()
	{
		checkOutput("", T("<?print isfirstlast(1, 2)?>"));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirstlast_null()
	{
		checkOutput("", T("<?print isfirstlast(data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirstlast_true()
	{
		checkOutput("", T("<?print isfirstlast(data)?>"), V("data", true));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirstlast_false()
	{
		checkOutput("", T("<?print isfirstlast(data)?>"), V("data", false));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirstlast_int()
	{
		checkOutput("", T("<?print isfirstlast(data)?>"), V("data", 42));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirstlast_float()
	{
		checkOutput("", T("<?print isfirstlast(data)?>"), V("data", 42.4));
	}

	@Test
	public void function_isfirst()
	{
		Template t = T("<?for (f, value) in isfirst(data)?><?if f?>[<?end if?>(<?print value?>)<?end for?>");

		checkOutput("", t, V("data", ""));
		checkOutput("[(?)", t, V("data", "?"));
		checkOutput("[(f)(o)(o)", t, V("data", "foo"));
		checkOutput("[(foo)(bar)", t, V("data", asList("foo", "bar")));
		checkOutput("[(foo)", t, V("data", V("foo", true)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isfirst_bad_kwarg()
	{
		Template templatekw = T("<?for (f, value) in isfirst(iterable=data)?><?if f?>[<?end if?>(<?print value?>)<?end for?>");
		checkOutput("[(f)(o)(o)", templatekw, V("data", "foo"));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isfirst_0_args()
	{
		checkOutput("", T("<?print isfirst()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isfirst_2_args()
	{
		checkOutput("", T("<?print isfirst(1, 2)?>"));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirst_null()
	{
		checkOutput("", T("<?print isfirst(data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirst_true()
	{
		checkOutput("", T("<?print isfirst(data)?>"), V("data", true));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirst_false()
	{
		checkOutput("", T("<?print isfirst(data)?>"), V("data", false));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirst_int()
	{
		checkOutput("", T("<?print isfirst(data)?>"), V("data", 42));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirst_float()
	{
		checkOutput("", T("<?print isfirst(data)?>"), V("data", 42.4));
	}

	@Test
	public void function_islast()
	{
		Template t = T("<?for (l, value) in islast(data)?>(<?print value?>)<?if l?>]<?end if?><?end for?>");

		checkOutput("", t, V("data", ""));
		checkOutput("(?)]", t, V("data", "?"));
		checkOutput("(f)(o)(o)]", t, V("data", "foo"));
		checkOutput("(foo)(bar)]", t, V("data", asList("foo", "bar")));
		checkOutput("(foo)]", t, V("data", V("foo", true)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_islast_bad_kwarg()
	{

		Template templatekw = T("<?for (l, value) in islast(iterable=data)?>(<?print value?>)<?if l?>]<?end if?><?end for?>");
		checkOutput("(f)(o)(o)]", templatekw, V("data", "foo"));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_islast_0_args()
	{
		checkOutput("", T("<?print islast()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_islast_2_args()
	{
		checkOutput("", T("<?print islast(1, 2)?>"));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_islast_null()
	{
		checkOutput("", T("<?print islast(data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_islast_true()
	{
		checkOutput("", T("<?print islast(data)?>"), V("data", true));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_islast_false()
	{
		checkOutput("", T("<?print islast(data)?>"), V("data", false));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_islast_int()
	{
		checkOutput("", T("<?print islast(data)?>"), V("data", 42));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_islast_float()
	{
		checkOutput("", T("<?print islast(data)?>"), V("data", 42.4));
	}

	@Test
	public void function_isundefined()
	{
		Template t = T("<?print isundefined(data)?>");

		checkOutput("True", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("True", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("False", t, V("data", V()));
		checkOutput("False", t, V("data", T("")));
		checkOutput("False", T("<?print isundefined(repr)?>"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isundefined_bad_kwarg()
	{
		T("<?print isundefined(obj=data)?>").renders(V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isundefined_0_args()
	{
		checkOutput("", T("<?print isundefined()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isundefined_2_args()
	{
		checkOutput("", T("<?print isundefined(1, 2)?>"));
	}

	@Test
	public void function_isdefined()
	{
		Template t = T("<?print isdefined(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("True", t, V("data", null));
		checkOutput("True", t, V("data", true));
		checkOutput("True", t, V("data", false));
		checkOutput("True", t, V("data", 42));
		checkOutput("True", t, V("data", 4.2));
		checkOutput("True", t, V("data", "foo"));
		checkOutput("True", t, V("data", new Date()));
		checkOutput("True", t, V("data", LocalDate.now()));
		checkOutput("True", t, V("data", LocalDateTime.now()));
		checkOutput("True", t, V("data", new TimeDelta(1)));
		checkOutput("True", t, V("data", new MonthDelta(1)));
		checkOutput("True", t, V("data", asList()));
		checkOutput("True", t, V("data", makeSet()));
		checkOutput("True", t, V("data", V()));
		checkOutput("True", t, V("data", T("")));
		checkOutput("True", T("<?print isdefined(repr)?>"));
		checkOutput("True", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isdefined_bad_kwarg()
	{
		T("<?print isdefined(obj=data)?>").renders(V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isdefined_0_args()
	{
		checkOutput("", T("<?print isdefined()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isdefined_2_args()
	{
		checkOutput("", T("<?print isdefined(1, 2)?>"));
	}

	@Test
	public void function_isnone()
	{
		Template t = T("<?print isnone(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("True", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("False", t, V("data", V()));
		checkOutput("False", t, V("data", T("")));
		checkOutput("False", T("<?print isnone(repr)?>"));
		checkOutput("False", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isnone_bad_kwarg()
	{
		checkOutput("True", T("<?print isnone(obj=data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isnone_0_args()
	{
		checkOutput("", T("<?print isnone()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isnone_2_args()
	{
		checkOutput("", T("<?print isnone(1, 2)?>"));
	}

	@Test
	public void function_isbool()
	{
		Template t = T("<?print isbool(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("True", t, V("data", true));
		checkOutput("True", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("False", t, V("data", V()));
		checkOutput("False", t, V("data", T("")));
		checkOutput("False", T("<?print isbool(repr)?>"));
		checkOutput("False", t, V("data", new Color(0, 0, 0)));
		}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isbool_bad_kwarg()
	{
		checkOutput("False", T("<?print isbool(obj=data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isbool_0_args()
	{
		checkOutput("", T("<?print isbool()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isbool_2_args()
	{
		checkOutput("", T("<?print isbool(1, 2)?>"));
	}

	@Test
	public void function_isint()
	{
		Template t = T("<?print isint(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("True", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("False", t, V("data", V()));
		checkOutput("False", t, V("data", T("")));
		checkOutput("False", T("<?print isint(repr)?>"));
		checkOutput("False", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isint_bad_kwarg()
	{
		checkOutput("False", T("<?print isint(obj=data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isint_0_args()
	{
		checkOutput("", T("<?print isint()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isint_2_args()
	{
		checkOutput("", T("<?print isint(1, 2)?>"));
	}

	@Test
	public void function_isfloat()
	{
		Template t = T("<?print isfloat(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("True", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("False", t, V("data", V()));
		checkOutput("False", t, V("data", T("")));
		checkOutput("False", T("<?print isfloat(repr)?>"));
		checkOutput("False", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isfloat_bad_kwarg()
	{
		checkOutput("False", T("<?print isfloat(obj=data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isfloat_0_args()
	{
		checkOutput("", T("<?print isfloat()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isfloat_2_args()
	{
		checkOutput("", T("<?print isfloat(1, 2)?>"));
	}

	@Test
	public void function_isstr()
	{
		Template t = T("<?print isstr(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("True", t, V("data", "foo"));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("False", t, V("data", V()));
		checkOutput("False", t, V("data", T("")));
		checkOutput("False", T("<?print isstr(repr)?>"));
		checkOutput("False", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isstr_bad_kwarg()
	{
		checkOutput("False", T("<?print isstr(obj=data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isstr_0_args()
	{
		checkOutput("", T("<?print isstr()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isstr_2_args()
	{
		checkOutput("", T("<?print isstr(1, 2)?>"));
	}

	@Test
	public void function_isdate()
	{
		Template t = T("<?print isdate(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("True", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("False", t, V("data", V()));
		checkOutput("False", t, V("data", T("")));
		checkOutput("False", T("<?print isdate(repr)?>"));
		checkOutput("False", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isdate_bad_kwarg()
	{
		checkOutput("False", T("<?print isdate(obj=data)?>"), V("data", null));
	}

	@Test
	public void function_isdatetime()
	{
		Template t = T("<?print isdatetime(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("True", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("True", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("False", t, V("data", V()));
		checkOutput("False", t, V("data", T("")));
		checkOutput("False", T("<?print isdate(repr)?>"));
		checkOutput("False", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isdatetime_bad_kwarg()
	{
		checkOutput("False", T("<?print isdate(obj=data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isdate_0_args()
	{
		checkOutput("", T("<?print isdate()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isdate_2_args()
	{
		checkOutput("", T("<?print isdate(1, 2)?>"));
	}

	@Test
	public void function_isexception()
	{
		Template t = T("<?print isexception(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("True", t, V("data", new RuntimeException("broken!")));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("False", t, V("data", V()));
		checkOutput("False", t, V("data", T("")));
		checkOutput("False", T("<?print isexception(repr)?>"));
		checkOutput("False", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isexception_bad_kwarg()
	{
		checkOutput("False", T("<?print isexception(obj=data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isexception_0_args()
	{
		checkOutput("", T("<?print isexception()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isexception_2_args()
	{
		checkOutput("", T("<?print isexception(1, 2)?>"));
	}

	@Test
	public void function_islist()
	{
		Template t = T("<?print islist(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("True", t, V("data", asList()));
		checkOutput("True", t, V("data", new Integer[]{1, 2, 3}));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("False", t, V("data", V()));
		checkOutput("False", t, V("data", T("")));
		checkOutput("False", T("<?print islist(repr)?>"));
		checkOutput("False", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_islist_bad_kwarg()
	{
		checkOutput("False", T("<?print islist(obj=data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_islist_0_args()
	{
		checkOutput("", T("<?print islist()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_islist_2_args()
	{
		checkOutput("", T("<?print islist(1, 2)?>"));
	}

	@Test
	public void function_isset()
	{
		Template t = T("<?print isset(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", new Integer[]{1, 2, 3}));
		checkOutput("False", t, V("data", V()));
		checkOutput("True", t, V("data", makeSet()));
		checkOutput("False", t, V("data", T("")));
		checkOutput("False", T("<?print isset(repr)?>"));
		checkOutput("False", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isset_bad_kwarg()
	{
		checkOutput("False", T("<?print isset(obj=data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isset_0_args()
	{
		checkOutput("", T("<?print isset()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isset_2_args()
	{
		checkOutput("", T("<?print isset(1, 2)?>"));
	}

	@Test
	public void function_isdict()
	{
		Template t = T("<?print isdict(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("True", t, V("data", V()));
		checkOutput("False", t, V("data", T("")));
		checkOutput("False", T("<?print isdict(repr)?>"));
		checkOutput("False", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isdict_bad_kwarg()
	{
		checkOutput("False", T("<?print isdict(obj=data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isdict_0_args()
	{
		checkOutput("", T("<?print isdict()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isdict_2_args()
	{
		checkOutput("", T("<?print isdict(1, 2)?>"));
	}

	@Test
	public void function_istemplate()
	{
		Template t = T("<?print istemplate(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("False", t, V("data", V()));
		checkOutput("True", t, V("data", T("")));
		checkOutput("False", T("<?print istemplate(repr)?>"));
		checkOutput("False", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_istemplate_bad_kwarg()
	{
		checkOutput("False", T("<?print istemplate(obj=data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_istemplate_0_args()
	{
		checkOutput("", T("<?print istemplate()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_istemplate_2_args()
	{
		checkOutput("", T("<?print istemplate(1, 2)?>"));
	}

	@Test
	public void function_isfunction()
	{
		Template t = T("<?print isfunction(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("False", t, V("data", V()));
		checkOutput("True", t, V("data", T("")));
		checkOutput("True", T("<?print isfunction(repr)?>"));
		checkOutput("False", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isfunction_bad_kwarg()
	{
		checkOutput("False", T("<?print isfunction(obj=data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isfunction_0_args()
	{
		checkOutput("", T("<?print isfunction()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isfunction_2_args()
	{
		checkOutput("", T("<?print isfunction(1, 2)?>"));
	}

	@Test
	public void function_iscolor()
	{
		Template t = T("<?print iscolor(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("False", t, V("data", V()));
		checkOutput("False", t, V("data", T("")));
		checkOutput("False", T("<?print iscolor(repr)?>"));
		checkOutput("True", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_iscolor_bad_kwarg()
	{
		checkOutput("False", T("<?print iscolor(obj=data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_iscolor_0_args()
	{
		checkOutput("", T("<?print iscolor()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_iscolor_2_args()
	{
		checkOutput("", T("<?print iscolor(1, 2)?>"));
	}

	@Test
	public void function_istimedelta()
	{
		Template t = T("<?print istimedelta(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("True", t, V("data", new TimeDelta(1)));
		checkOutput("False", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("False", t, V("data", V()));
		checkOutput("False", t, V("data", T("")));
		checkOutput("False", T("<?print istimedelta(repr)?>"));
		checkOutput("False", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_istimedelta_bad_kwarg()
	{
		checkOutput("False", T("<?print istimedelta(obj=data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_istimedelta_0_args()
	{
		checkOutput("", T("<?print istimedelta()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_istimedelta_2_args()
	{
		checkOutput("", T("<?print istimedelta(1, 2)?>"));
	}

	@Test
	public void function_ismonthdelta()
	{
		Template t = T("<?print ismonthdelta(data)?>");

		checkOutput("False", t, V("data", new UndefinedKey(null, "foo")));
		checkOutput("False", t, V("data", new UndefinedAttribute(null, "foo")));
		checkOutput("False", t, V("data", null));
		checkOutput("False", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("False", t, V("data", 42));
		checkOutput("False", t, V("data", 4.2));
		checkOutput("False", t, V("data", "foo"));
		checkOutput("False", t, V("data", new Date()));
		checkOutput("False", t, V("data", LocalDate.now()));
		checkOutput("False", t, V("data", LocalDateTime.now()));
		checkOutput("False", t, V("data", new TimeDelta(1)));
		checkOutput("True", t, V("data", new MonthDelta(1)));
		checkOutput("False", t, V("data", asList()));
		checkOutput("False", t, V("data", makeSet()));
		checkOutput("False", t, V("data", V()));
		checkOutput("False", t, V("data", T("")));
		checkOutput("False", T("<?print ismonthdelta(repr)?>"));
		checkOutput("False", t, V("data", new Color(0, 0, 0)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_ismonthdelta_bad_kwarg()
	{
		checkOutput("False", T("<?print ismonthdelta(obj=data)?>"), V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_ismonthdelta_0_args()
	{
		checkOutput("", T("<?print ismonthdelta()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_ismonthdelta_2_args()
	{
		checkOutput("", T("<?print ismonthdelta(1, 2)?>"));
	}

	@Test
	public void function_isinstance()
	{
		Map<String, List<Object>> info = makeMap(
			"type(None)", asList((Object)null), // Cast to {@code Object}: otherwise we'd get a {@code NullPointerException}
			"bool", asList(true, false),
			"int", asList(42),
			"float", asList(42.5),
			"str", asList("foo"),
			"exception", asList(new RuntimeException("broken!")),
			"date", asList(LocalDate.now()),
			"datetime", asList(new Date(), LocalDateTime.now()),
			"timedelta", asList(new TimeDelta(1)),
			"monthdelta", asList(new MonthDelta(1)),
			"list", asList(asList()),
			"set", asList(makeSet()),
			"dict", asList(V()),
			"ul4.Template", asList(T("")),
			"function", asList(FunctionRepr.function),
			"color.Color", asList(new Color(0, 0, 0))
		);

		for (String type : info.keySet())
		{
			if (!type.equals("exception"))
			{
				Template t = T("<?print isinstance(data, " + type + ")?>");
				for (Map.Entry<String, List<Object>> entryInstance : info.entrySet())
				{
					String output = type.equals(entryInstance.getKey()) ? "True" : "False";

					for (Object value : entryInstance.getValue())
						checkOutput(output, t, V("data", value));
				}
			}
		}
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_isinstance_bad_kwarg()
	{
		checkOutput("", T("<?print isinstance(obj=None, type=bool)?>"));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isinstance_0_args()
	{
		checkOutput("", T("<?print isinstance()?>"));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isinstance_2_args()
	{
		checkOutput("", T("<?print isinstance(1, 2)?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isinstance_3_args()
	{
		checkOutput("", T("<?print isinstance(1, 2, 3)?>"));
	}

	private String codePoint(int value)
	{
		return String.valueOf((char)value);
	}

	@Test
	public void function_repr()
	{
		Template t = T("<?print repr(data)?>");

		java.util.List list = new java.util.ArrayList();
		list.add(list);

		checkOutput("None", t, V("data", null));
		checkOutput("True", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("42", t, V("data", 42));
		checkOutput("42", t, V("data", new BigInteger("42")));
		checkOutput("42.0", t, V("data", 42.));
		checkOutput("42.5", t, V("data", 42.5));
		checkOutput("42.0", t, V("data", new BigDecimal("42.0")));
		checkOutput("42.5", t, V("data", new BigDecimal("42.5")));
		checkOutput("'foo'", t, V("data", "foo"));
		checkOutput("\"'\"", t, V("data", "'"));
		checkOutput("'\"'", t, V("data", "\""));
		checkOutput("'\\'\"'", t, V("data", "'\""));
		checkOutput("'\\r'", t, V("data", "\r"));
		checkOutput("'\\t'", t, V("data", "\t"));
		checkOutput("'\\n'", t, V("data", "\n"));
		checkOutput("'\\x00'", t, V("data", codePoint(0))); // category Cc
		checkOutput("'\\x7f'", t, V("data", codePoint(0x7f)));
		checkOutput("'\\x80'", t, V("data", codePoint(0x80)));
		checkOutput("'\\x9f'", t, V("data", codePoint(0x9f)));
		checkOutput("'\\xa0'", t, V("data", codePoint(0xa0))); // category Zs
		checkOutput("'\\xad'", t, V("data", codePoint(0xad))); // category Cf
		checkOutput("'\u00ff'", t, V("data", codePoint(0xff)));
		checkOutput("'\u0100'", t, V("data", codePoint(0x100)));
		checkOutput("'\\u0378'", t, V("data", codePoint(0x378))); // category Cn
		checkOutput("'\\u2028'", t, V("data", codePoint(0x2028))); // category Zl
		checkOutput("'\\u2029'", t, V("data", codePoint(0x2029))); // category Zp
		checkOutput("'\\ud800'", t, V("data", codePoint(0xd800))); // category Cs
		checkOutput("'\\ue000'", t, V("data", codePoint(0xe000))); // category Co
		checkOutput("'\u3042'", t, V("data", codePoint(0x3042)));
		checkOutput("'\\uffff'", t, V("data", codePoint(0xffff)));
		checkOutput("[]", t, V("data", asList()));
		checkOutput("[1, 2, 3]", t, V("data", asList(1, 2, 3)));
		checkOutput("[...]", t, V("data", list));
		checkOutput("[1, 2, 3]", t, V("data", new Integer[]{1, 2, 3}));
		checkOutput("{}", t, V("data", V()));
		checkOutput("{'a': 1}", t, V("data", V("a", 1)));
		checkOutput("{'a': 1, 'b': 2}", t, V("data", makeOrderedMap("a", 1, "b", 2)));
		checkOutput("{/}", t, V("data", makeSet()));
		checkOutput("{1}", t, V("data", makeSet(1)));
		checkOutput("@(2000-02-29T)", t, V("data", makeDate(2000, 2, 29)));
		checkOutput("@(2000-02-29T12:34)", t, V("data", makeDate(2000, 2, 29, 12, 34, 0)));
		checkOutput("@(2000-02-29T12:34:56)", t, V("data", makeDate(2000, 2, 29, 12, 34, 56)));
		checkOutput("@(2000-02-29T12:34:56.123000)", t, V("data", makeDate(2000, 2, 29, 12, 34, 56, 123456)));
		checkOutput("@(2000-02-29)", t, V("data", LocalDate.of(2000, 2, 29)));
		checkOutput("@(2000-02-29T)", t, V("data", LocalDateTime.of(2000, 2, 29, 0, 0)));
		checkOutput("@(2000-02-29T12:34)", t, V("data", LocalDateTime.of(2000, 2, 29, 12, 34)));
		checkOutput("@(2000-02-29T12:34:56)", t, V("data", LocalDateTime.of(2000, 2, 29, 12, 34, 56)));
		checkOutput("@(2000-02-29T12:34:56.123456)", t, V("data", LocalDateTime.of(2000, 2, 29, 12, 34, 56, 123456789)));
		checkOutput("timedelta(days=1, seconds=2, microseconds=3)", t, V("data", new TimeDelta(1, 2, 3)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_repr_bad_kwarg()
	{
		T("<?print repr(obj=data)?>").renders(V("data", null));
	}


	@Test
	public void function_ascii()
	{
		Template t = T("<?print ascii(data)?>");

		java.util.List list = new java.util.ArrayList();
		list.add(list);

		checkOutput("None", t, V("data", null));
		checkOutput("True", t, V("data", true));
		checkOutput("False", t, V("data", false));
		checkOutput("42", t, V("data", 42));
		checkOutput("42", t, V("data", new BigInteger("42")));
		checkOutput("42.0", t, V("data", 42.));
		checkOutput("42.5", t, V("data", 42.5));
		checkOutput("42.0", t, V("data", new BigDecimal("42.0")));
		checkOutput("42.5", t, V("data", new BigDecimal("42.5")));
		checkOutput("'foo'", t, V("data", "foo"));
		checkOutput("\"'\"", t, V("data", "'"));
		checkOutput("'\"'", t, V("data", "\""));
		checkOutput("'\\'\"'", t, V("data", "'\""));
		checkOutput("'\\r'", t, V("data", "\r"));
		checkOutput("'\\t'", t, V("data", "\t"));
		checkOutput("'\\n'", t, V("data", "\n"));
		checkOutput("'\\x00'", t, V("data", codePoint(0))); // category Cc
		checkOutput("'\\x7f'", t, V("data", codePoint(0x7f)));
		checkOutput("'\\x80'", t, V("data", codePoint(0x80)));
		checkOutput("'\\x9f'", t, V("data", codePoint(0x9f)));
		checkOutput("'\\xa0'", t, V("data", codePoint(0xa0))); // category Zs
		checkOutput("'\\xad'", t, V("data", codePoint(0xad))); // category Cf
		checkOutput("'\\xff'", t, V("data", codePoint(0xff)));
		checkOutput("'\\u0100'", t, V("data", codePoint(0x100)));
		checkOutput("'\\u0378'", t, V("data", codePoint(0x378))); // category Cn
		checkOutput("'\\u2028'", t, V("data", codePoint(0x2028))); // category Zl
		checkOutput("'\\u2029'", t, V("data", codePoint(0x2029))); // category Zp
		checkOutput("'\\ud800'", t, V("data", codePoint(0xd800))); // category Cs
		checkOutput("'\\ue000'", t, V("data", codePoint(0xe000))); // category Co
		checkOutput("'\\u3042'", t, V("data", codePoint(0x3042)));
		checkOutput("'\\uffff'", t, V("data", codePoint(0xffff)));
		checkOutput("[]", t, V("data", asList()));
		checkOutput("[1, 2, 3]", t, V("data", asList(1, 2, 3)));
		checkOutput("[...]", t, V("data", list));
		checkOutput("[1, 2, 3]", t, V("data", new Integer[]{1, 2, 3}));
		checkOutput("{}", t, V("data", V()));
		checkOutput("{'a': 1}", t, V("data", V("a", 1)));
		checkOutput("{'a': 1, 'b': 2}", t, V("data", makeOrderedMap("a", 1, "b", 2)));
		checkOutput("{/}", t, V("data", makeSet()));
		checkOutput("{1}", t, V("data", makeSet(1)));
		checkOutput("@(2000-02-29T)", t, V("data", makeDate(2000, 2, 29)));
		checkOutput("@(2000-02-29T12:34)", t, V("data", makeDate(2000, 2, 29, 12, 34, 0)));
		checkOutput("@(2000-02-29T12:34:56)", t, V("data", makeDate(2000, 2, 29, 12, 34, 56)));
		checkOutput("@(2000-02-29T12:34:56.123000)", t, V("data", makeDate(2000, 2, 29, 12, 34, 56, 123456)));
		checkOutput("@(2000-02-29)", t, V("data", LocalDate.of(2000, 2, 29)));
		checkOutput("@(2000-02-29T)", t, V("data", LocalDateTime.of(2000, 2, 29, 0, 0)));
		checkOutput("@(2000-02-29T12:34)", t, V("data", LocalDateTime.of(2000, 2, 29, 12, 34)));
		checkOutput("@(2000-02-29T12:34:56)", t, V("data", LocalDateTime.of(2000, 2, 29, 12, 34, 56)));
		checkOutput("@(2000-02-29T12:34:56.123456)", t, V("data", LocalDateTime.of(2000, 2, 29, 12, 34, 56, 123456789)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_ascii_bad_kwarg()
	{
		T("<?print repr(obj=data)?>").renders(V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_repr_0_args()
	{
		checkOutput("", T("<?print repr()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_repr_2_args()
	{
		checkOutput("", T("<?print repr(1, 2)?>"));
	}

	@Test
	public void function_format_date()
	{
		List dates = asList(
			makeDate(2011, 1, 25, 13, 34, 56, 987654),
			LocalDate.of(2011, 1, 25),
			LocalDateTime.of(2011, 1, 25, 13, 34, 56, 987654000)
		);

		Template t2 = T("<?print format(data, fmt)?>");
		Template t3 = T("<?print format(data, fmt, lang)?>");
		Template t3kw = T("<?print format(obj=data, fmt=fmt, lang=lang)?>");

		for (Object t : dates)
		{
			checkOutput("2011", t2, V("data", t, "fmt", "%Y"));
			checkOutput("01", t2, V("data", t, "fmt", "%m"));
			checkOutput("25", t2, V("data", t, "fmt", "%d"));
			checkOutput(t instanceof LocalDate ? "00" : "13", t2, V("data", t, "fmt", "%H"));
			checkOutput(t instanceof LocalDate ? "00" : "34", t2, V("data", t, "fmt", "%M"));
			checkOutput(t instanceof LocalDate ? "00" : "56", t2, V("data", t, "fmt", "%S"));
			checkOutput(t instanceof LocalDate ? "000000" : (t instanceof Date ? "987000" : "987654"), t2, V("data", t, "fmt", "%f"));
			checkOutput("Tue", t2, V("data", t, "fmt", "%a"));
			checkOutput("Tue", t3, V("data", t, "fmt", "%a", "lang", null));
			checkOutput("Tue", t3, V("data", t, "fmt", "%a", "lang", "en"));
			checkOutput("Di", t3, V("data", t, "fmt", "%a", "lang", "de"));
			checkOutput("Di", t3, V("data", t, "fmt", "%a", "lang", "de_DE"));
			checkOutput("Tuesday", t2, V("data", t, "fmt", "%A"));
			checkOutput("Tuesday", t3, V("data", t, "fmt", "%A", "lang", null));
			checkOutput("Tuesday", t3, V("data", t, "fmt", "%A", "lang", "en"));
			checkOutput("Dienstag", t3, V("data", t, "fmt", "%A", "lang", "de"));
			checkOutput("Dienstag", t3, V("data", t, "fmt", "%A", "lang", "de_DE"));
			checkOutput("Jan", t2, V("data", t, "fmt", "%b"));
			checkOutput("Jan", t3, V("data", t, "fmt", "%b", "lang", null));
			checkOutput("Jan", t3, V("data", t, "fmt", "%b", "lang", "en"));
			checkOutput("Jan", t3, V("data", t, "fmt", "%b", "lang", "de"));
			checkOutput("Jan", t3, V("data", t, "fmt", "%b", "lang", "de_DE"));
			checkOutput("January", t2, V("data", t, "fmt", "%B"));
			checkOutput("January", t3, V("data", t, "fmt", "%B", "lang", null));
			checkOutput("January", t3, V("data", t, "fmt", "%B", "lang", "en"));
			checkOutput("Januar", t3, V("data", t, "fmt", "%B", "lang", "de"));
			checkOutput("Januar", t3, V("data", t, "fmt", "%B", "lang", "de_DE"));
			checkOutput(t instanceof LocalDate ? "00" : "01", t2, V("data", t, "fmt", "%I"));
			checkOutput("025", t2, V("data", t, "fmt", "%j"));
			checkOutput(t instanceof LocalDate ? "AM" : "PM", t2, V("data", t, "fmt", "%p"));
			checkOutput("04", t2, V("data", t, "fmt", "%U"));
			checkOutput("2", t2, V("data", t, "fmt", "%w"));
			checkOutput("04", t2, V("data", t, "fmt", "%W"));
			checkOutput("11", t2, V("data", t, "fmt", "%y"));
			checkOutput(t instanceof LocalDate ? "Tue 25 Jan 2011 00:00:00 AM" : "Tue 25 Jan 2011 01:34:56 PM", t2, V("data", t, "fmt", "%c"));
			checkOutput("01/25/2011", t2, V("data", t, "fmt", "%x"));
			checkOutput("01/25/2011", t3, V("data", t, "fmt", "%x", "lang", null));
			checkOutput("01/25/2011", t3, V("data", t, "fmt", "%x", "lang", "en"));
			checkOutput("25.01.2011", t3, V("data", t, "fmt", "%x", "lang", "de"));
			checkOutput("25.01.2011", t3, V("data", t, "fmt", "%x", "lang", "de_DE"));
			checkOutput(t instanceof LocalDate ? "00:00:00" : "13:34:56", t2, V("data", t, "fmt", "%X"));
			checkOutput(t instanceof LocalDate ? "00:00:00" : "13:34:56", t3, V("data", t, "fmt", "%X", "lang", null));
			checkOutput(t instanceof LocalDate ? "00:00:00" : "13:34:56", t3, V("data", t, "fmt", "%X", "lang", "en"));
			checkOutput(t instanceof LocalDate ? "00:00:00" : "13:34:56", t3, V("data", t, "fmt", "%X", "lang", "de"));
			checkOutput(t instanceof LocalDate ? "00:00:00" : "13:34:56", t3, V("data", t, "fmt", "%X", "lang", "de_DE"));
			checkOutput("%", t2, V("fmt", "%%", "data", t));
			checkOutput("2011", t3kw, V("data", t, "fmt", "%Y", "lang", "de_DE"));
		}
	}

	@Test
	public void function_format_int()
	{
		Template t2 = T("<?print format(data, fmt)?>");
		Template t3 = T("<?print format(data, fmt, lang)?>");

		checkOutput("42", t2, V("data", 42, "fmt", ""));
		checkOutput("-42", t2, V("data", -42, "fmt", ""));
		checkOutput("   42", t2, V("data", 42, "fmt", "5"));
		checkOutput("49", T("<?print format(int(data), fmt)?>"), V("data", 49.955239, "fmt", "02"));
		checkOutput("00042", t2, V("data", 42, "fmt", "05"));
		checkOutput("-0042", t2, V("data", -42, "fmt", "05"));
		checkOutput("+0042", t2, V("data", 42, "fmt", "+05"));
		checkOutput(" +101010", t2, V("data", 42, "fmt", "+8b"));
		checkOutput(" +0b101010", t2, V("data", 42, "fmt", "+#10b"));
		checkOutput("52", t2, V("data", 42, "fmt", "o"));
		checkOutput("+0x2a", t2, V("data", 42, "fmt", "+#x"));
		checkOutput("+0X2A", t2, V("data", 42, "fmt", "+#X"));
		checkOutput("42   ", t2, V("data", 42, "fmt", "<5"));
		checkOutput("   42", t2, V("data", 42, "fmt", ">5"));
		checkOutput("???42", t2, V("data", 42, "fmt", "?>5"));
		checkOutput(" 42  ", t2, V("data", 42, "fmt", "^5"));
		checkOutput(" ??42", t2, V("data", 42, "fmt", "?= 5"));
		checkOutput(" 0b??101010", t2, V("data", 42, "fmt", "?= #11b"));
		checkOutput("00001", t2, V("data", true, "fmt", "05"));
		checkOutput("00042", t2, V("data", new BigInteger("42"), "fmt", "05"));
	}

	@Test
	public void function_chr()
	{
		Template t = T("<?print chr(data)?>");
		checkOutput("\u0000", t, V("data", 0));
		checkOutput("a", t, V("data", (int)'a'));
		checkOutput("\u20ac", t, V("data", 0x20ac));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_chr_bad_kwarg()
	{
		T("<?print chr(i=data)?>").renders(V("data", 0));
	}

	@Test
	public void function_ord()
	{
		Template t = T("<?print ord(data)?>");
		checkOutput("0", t, V("data", "\u0000"));
		checkOutput("97", t, V("data", "a"));
		checkOutput("8364", t, V("data", "\u20ac"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_ord_bad_kwarg()
	{
		T("<?print ord(c=data)?>").renders(V("data", "\u0000"));
	}

	@Test
	public void function_hex()
	{
		Template t = T("<?print hex(data)?>");
		checkOutput("0x0", t, V("data", 0));
		checkOutput("0xff", t, V("data", 0xff));
		checkOutput("0xffff", t, V("data", 0xffff));
		checkOutput("-0xffff", t, V("data", -0xffff));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_hex_bad_kwarg()
	{
		T("<?print hex(number=data)?>").renders(V("data", 0));
	}

	@Test
	public void function_oct()
	{
		Template t = T("<?print oct(data)?>");
		checkOutput("0o0", t, V("data", 0));
		checkOutput("0o77", t, V("data", 077));
		checkOutput("0o7777", t, V("data", 07777));
		checkOutput("-0o7777", t, V("data", -07777));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_oct_bad_kwarg()
	{
		T("<?print oct(number=data)?>").renders(V("data", 0));
	}

	@Test
	public void function_bin()
	{
		Template t = T("<?print bin(data)?>");

		checkOutput("0b0", t, V("data", 0));
		checkOutput("0b11", t, V("data", 3));
		checkOutput("-0b1111", t, V("data", -15));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_bin_bad_kwarg()
	{
		T("<?print bin(number=data)?>").renders(V("data", 0));
	}

	@Test
	public void function_abs()
	{
		Template t = T("<?print abs(data)?>");
		checkOutput("0", t, V("data", 0));
		checkOutput("42", t, V("data", 42));
		checkOutput("42", t, V("data", -42));
		checkOutput("1 month", t, V("data", new MonthDelta(-1)));
		checkOutput("1 day, 0:00:01.000001", t, V("data", new TimeDelta(-1, -1, -1)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_abs_bad_kwarg()
	{
		T("<?print abs(number=data)?>").renders(V("data", 0));
	}

	@Test
	public void function_min()
	{
		checkOutput("1", T("<?print min('123')?>"));
		checkOutput("1", T("<?print min(1, 2, 3)?>"));
		checkOutput("0", T("<?print min(0, False, 1, True)?>"));
		checkOutput("False", T("<?print min(False, 0, True, 1)?>"));
		checkOutput("False", T("<?print min([False, 0, True, 1])?>"));
		checkOutput("42", T("<?print min([], default=42)?>"));
		checkOutput("hinz", T("<?def key(s)?><?return s[1]?><?end def?><?print min(['gurk', 'hurz', 'hinz', 'kunz'], key=key)?>"));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_min_0_args()
	{
		checkOutput("", T("<?print min()?>"));
	}

	@Test
	public void function_max()
	{
		checkOutput("3", T("<?print max('123')?>"));
		checkOutput("3", T("<?print max(1, 2, 3)?>"));
		checkOutput("1", T("<?print max(0, False, 1, True)?>"));
		checkOutput("True", T("<?print max(False, 0, True, 1)?>"));
		checkOutput("True", T("<?print max([False, 0, True, 1])?>"));
		checkOutput("42", T("<?print max([], default=42)?>"));
		checkOutput("hurz", T("<?def key(s)?><?return s[2:]?><?end def?><?print max(['gurk', 'hurz', 'hinz', 'kunz'], key=key)?>"));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_max_0_args()
	{
		checkOutput("", T("<?print max()?>"));
	}

	@Test
	public void function_sum()
	{
		checkOutput("0", T("<?print sum([])?>"));
		checkOutput("6", T("<?print sum([1, 2, 3])?>"));
		checkOutput("12", T("<?print sum([1, 2, 3], 6)?>"));
		checkOutput("5050", T("<?print sum(range(101))?>"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_sum_bad_kwarg()
	{
		T("<?print sum(iterable=[1, 2, 3], start=6)?>").renders();
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_sum_0_args()
	{
		checkOutput("", T("<?print sum()?>"));
	}

	@Test
	public void function_first()
	{
		checkOutput("g", T("<?print first('gurk')?>"));
		checkOutput("None", T("<?print repr(first(''))?>"));
		checkOutput("x", T("<?print first('', 'x')?>"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_first_bad_kwarg()
	{
		T("<?print first(iterable='', default='x')?>").renders();
	}

	@Test
	public void function_last()
	{
		checkOutput("k", T("<?print last('gurk')?>"));
		checkOutput("None", T("<?print repr(last(''))?>"));
		checkOutput("x", T("<?print last('', 'x')?>"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_last_bad_kwarg()
	{
		T("<?print last(iterable='', default='x')?>").renders();
	}

	@Test
	public void function_sorted()
	{
		Template t = T("<?for i in sorted(data)?><?print i?><?end for?>");
		checkOutput("gkru", t, V("data", "gurk"));
		checkOutput("24679", t, V("data", "92746"));
		checkOutput("172342", t, V("data", asList(42, 17, 23)));
		checkOutput("012", t, V("data", V(0, "zero", 1, "one", 2, "two")));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_sorted_bad_kwarg()
	{
		Template templatekw = T("<?for i in sorted(iterable=data)?><?print i?><?end for?>");
		checkOutput("gkru", templatekw, V("data", "gurk"));
	}


	@Test
	public void function_range()
	{
		Template t1 = T("<?for i in range(data)?><?print i?>;<?end for?>");
		Template t2 = T("<?for i in range(data[0], data[1])?><?print i?>;<?end for?>");
		Template t3 = T("<?for i in range(data[0], data[1], data[2])?><?print i?>;<?end for?>");

		checkOutput("", t1, V("data", -10));
		checkOutput("", t1, V("data", 0));
		checkOutput("0;", t1, V("data", 1));
		checkOutput("0;1;2;3;4;", t1, V("data", 5));
		checkOutput("", t2, V("data", asList(0, -10)));
		checkOutput("", t2, V("data", asList(0, 0)));
		checkOutput("0;1;2;3;4;", t2, V("data", asList(0, 5)));
		checkOutput("-5;-4;-3;-2;-1;0;1;2;3;4;", t2, V("data", asList(-5, 5)));
		checkOutput("", t3, V("data", asList(0, -10, 1)));
		checkOutput("", t3, V("data", asList(0, 0, 1)));
		checkOutput("0;2;4;6;8;", t3, V("data", asList(0, 10, 2)));
		checkOutput("", t3, V("data", asList(0, 10, -2)));
		checkOutput("10;8;6;4;2;", t3, V("data", asList(10, 0, -2)));
		checkOutput("", t3, V("data", asList(10, 0, 2)));
		checkOutput("0;1;", T("<?for i in range(0, *[2, 1])?><?print i?>;<?end for?>"));
	}

	@Test
	public void function_slice()
	{
		Template t2 = T("<?for i in slice(data[0], data[1])?><?print i?>;<?end for?>");
		Template t3 = T("<?for i in slice(data[0], data[1], data[2])?><?print i?>;<?end for?>");
		Template t4 = T("<?for i in slice(data[0], data[1], data[2], data[3])?><?print i?>;<?end for?>");

		checkOutput("g;u;r;k;", t2, V("data", asList("gurk", null)));
		checkOutput("g;u;", t2, V("data", asList("gurk", 2)));
		checkOutput("u;r;", t3, V("data", asList("gurk", 1, 3)));
		checkOutput("u;r;k;", t3, V("data", asList("gurk", 1, null)));
		checkOutput("g;u;", t3, V("data", asList("gurk", null, 2)));
		checkOutput("u;u;", t4, V("data", asList("gurkgurk", 1, 6, 4)));
	}

	@Test
	public void function_zip()
	{
		Template t2 = T("<?for (ix, iy) in zip(x, y)?><?print ix?>-<?print iy?>;<?end for?>");
		Template t3 = T("<?for (ix, iy, iz) in zip(x, y, z)?><?print ix?>-<?print iy?>+<?print iz?>;<?end for?>");

		checkOutput("", t2, V("x", asList(), "y", asList()));
		checkOutput("1-3;2-4;", t2, V("x", asList(1, 2), "y", asList(3, 4)));
		checkOutput("1-4;2-5;", t2, V("x", asList(1, 2, 3), "y", asList(4, 5)));
		checkOutput("", t3, V("x", asList(), "y", asList(), "z", asList()));
		checkOutput("1-3+5;2-4+6;", t3, V("x", asList(1, 2), "y", asList(3, 4), "z", asList(5, 6)));
		checkOutput("1-4+6;", t3, V("x", asList(1, 2, 3), "y", asList(4, 5), "z", asList(6)));
	}

	@Test
	public void function_type2()
	{
		String source = "<?print 2*v?>";
		Template t = T("<?code t2 = ul4.Template(source)?><?render t2(v=v)?>");
		checkOutput("84", t, V("v", 42, "source", source));
	}

	@Test
	public void function_type()
	{
		Template t = T("<?print type(data)?>");

		checkOutput("<type undefinedvariable>", t);
		checkOutput("<type None>", t, V("data", null));
		checkOutput("<type bool>", t, V("data", false));
		checkOutput("<type bool>", t, V("data", true));
		checkOutput("<type int>", t, V("data", 42));
		checkOutput("<type float>", t, V("data", 4.2));
		checkOutput("<type str>", t, V("data", "foo"));
		checkOutput("<type datetime>", t, V("data", new Date()));
		checkOutput("<type date>", t, V("data", LocalDate.now()));
		checkOutput("<type datetime>", t, V("data", LocalDateTime.now()));
		checkOutput("<type list>", t, V("data", asList(1, 2)));
		checkOutput("<type dict>", t, V("data", V(1, 2)));
		checkOutput("<type set>", t, V("data", makeSet(1, 2)));
		checkOutput("<type ul4.Template>", t, V("data", T("")));
		checkOutput("<type color.Color>", t, V("data", new Color(0, 0, 0)));
		checkOutput("<type java.lang.RuntimeException>", t, V("data", new RuntimeException("broken")));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_type_bad_kwarg()
	{

		Template tkw = T("<?print type(obj=data)?>");
		checkOutput("<type None>", tkw, V("data", null));
	}

	@Test
	public void function_reversed()
	{
		Template t = T("<?for i in reversed(x)?>(<?print i?>)<?end for?>");
		checkOutput("(3)(2)(1)", t, V("x", "123"));
		checkOutput("(3)(2)(1)", t, V("x", asList(1, 2, 3)));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_reversed_bad_kwarg()
	{
		T("<?for i in reversed(sequence=x)?>(<?print i?>)<?end for?>").renders(V("x", "123"));
	}

	@Test
	public void function_urlquote()
	{
		checkOutput("gurk", T("<?print urlquote('gurk')?>"));
		checkOutput("%3C%3D%3E%20%2B%20%3F", T("<?print urlquote('<=> + ?')?>"));
		checkOutput("%7F%C3%BF%EF%BF%BF", T("<?print urlquote('\u007f\u00ff\uffff')?>"));

		checkOutput("gurk", T("<?print urlquote(string='gurk')?>"));
	}

	@Test
	public void function_urlunquote()
	{
		checkOutput("gurk", T("<?print urlunquote('gurk')?>"));
		checkOutput("<=>+?", T("<?print urlunquote('%3C%3D%3E%2B%3F')?>"));
		checkOutput("\u007f\u00ff\uffff", T("<?print urlunquote('%7F%C3%BF%EF%BF%BF')?>"));

		checkOutput("gurk", T("<?print urlunquote(string='gurk')?>"));
	}

	@Test
	public void function_rgb()
	{
		checkOutput("#369", T("<?print repr(rgb(0.2, 0.4, 0.6))?>"));
		checkOutput("#369c", T("<?print repr(rgb(0.2, 0.4, 0.6, 0.8))?>"));

		checkOutput("#369c", T("<?print repr(rgb(r=0.2, g=0.4, b=0.6, a=0.8))?>"));
	}

	@Test
	public void function_hls()
	{
		checkOutput("#fff", T("<?print repr(hls(0, 1, 0))?>"));
		checkOutput("#fff0", T("<?print repr(hls(0, 1, 0, 0))?>"));

		checkOutput("#fff0", T("<?print repr(hls(h=0, l=1, s=0, a=0))?>"));
	}

	@Test
	public void function_hsv()
	{
		checkOutput("#fff", T("<?print repr(hsv(0, 0, 1))?>"));
		checkOutput("#fff0", T("<?print repr(hsv(0, 0, 1, 0))?>"));

		checkOutput("#fff0", T("<?print repr(hsv(h=0, s=0, v=1, a=0))?>"));
	}

	@Test
	public void function_round()
	{
		checkOutput("True", T("<?print round(x) == 42?>"), V("x", 42));
		checkOutput("True", T("<?print round(x, 1) == 42?>"), V("x", 42));
		checkOutput("True", T("<?print round(x, -1) == 40?>"), V("x", 42));
		checkOutput("True", T("<?print round(x, -1) == 50?>"), V("x", 48));
		checkOutput("<type int>", T("<?print type(round(x))?>"), V("x", 42));
		checkOutput("<type int>", T("<?print type(round(x, 1))?>"), V("x", 42));
		checkOutput("<type int>", T("<?print type(round(x, -1))?>"), V("x", 42));

		checkOutput("True", T("<?print round(x) == 42?>"), V("x", new Long(42)));
		checkOutput("True", T("<?print round(x, 1) == 42?>"), V("x", new Long(42)));
		checkOutput("True", T("<?print round(x, -1) == 40?>"), V("x", new Long(42)));
		checkOutput("True", T("<?print round(x, -1) == 50?>"), V("x", new Long(48)));

		checkOutput("True", T("<?print round(x) == 42?>"), V("x", new BigInteger("42")));
		checkOutput("True", T("<?print round(x, 1) == 42?>"), V("x", new BigInteger("42")));
		checkOutput("True", T("<?print round(x, -1) == 40?>"), V("x", new BigInteger("42")));
		checkOutput("True", T("<?print round(x, -1) == 50?>"), V("x", new BigInteger("48")));

		checkOutput("True", T("<?print round(x) == 42?>"), V("x", 42.4));
		checkOutput("True", T("<?print round(x) == 43?>"), V("x", 42.6));
		checkOutput("True", T("<?print round(x) == -42?>"), V("x", -42.4));
		checkOutput("True", T("<?print round(x) == -43?>"), V("x", -42.6));
		checkOutput("<type int>", T("<?print type(round(x))?>"), V("x", 42.5));

		checkOutput("True", T("<?print round(x, -1) == 40?>"), V("x", 42.4));
		checkOutput("True", T("<?print round(x, -1) == 50?>"), V("x", 46.2));
		checkOutput("True", T("<?print round(x, -1) == -40?>"), V("x", -42.4));
		checkOutput("True", T("<?print round(x, -1) == -50?>"), V("x", -46.2));
		checkOutput("<type int>", T("<?print type(round(x, -1))?>"), V("x", 42.5));

		checkOutput("True", T("<?print round(x, 1) == 43.0?>"), V("x", 42.987));
		checkOutput("True", T("<?print round(x, 1) == 42.1?>"), V("x", 42.123));
		checkOutput("True", T("<?print round(x, 1) == -43.0?>"), V("x", -42.987));
		checkOutput("True", T("<?print round(x, 1) == -42.1?>"), V("x", -42.123));
		// checkOutput("True", T("<?print round(x, 2) == 42.59?>"), V("x", 42.589));
		checkOutput("True", T("<?print round(x, 2) == 42.12?>"), V("x", 42.123));
		// checkOutput("True", T("<?print round(x, 2) == -42.59?>"), V("x", -42.589));
		checkOutput("True", T("<?print round(x, 2) == -42.12?>"), V("x", -42.123));
		checkOutput("<type float>", T("<?print type(round(x, 1))?>"), V("x", 42.5));

		checkOutput("True", T("<?print round(x) == 42?>"), V("x", new BigDecimal("42")));
		checkOutput("True", T("<?print round(x, 1) == 42?>"), V("x", new BigDecimal("42")));
		checkOutput("True", T("<?print round(x, -1) == 40?>"), V("x", new BigDecimal("42")));
		checkOutput("True", T("<?print round(x, -1) == 50?>"), V("x", new BigDecimal("48")));

	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_round_0_args()
	{
		checkOutput("", T("<?print round()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_round_3_args()
	{
		checkOutput("", T("<?print round(1, 2, 3)?>"));
	}

	@Test
	public void function_floor()
	{
		checkOutput("42", T("<?print floor(x)?>"), V("x", 42));
		checkOutput("42", T("<?print floor(x, 1)?>"), V("x", 42));
		checkOutput("40", T("<?print floor(x, -1)?>"), V("x", 40));
		checkOutput("40", T("<?print floor(x, -1)?>"), V("x", 49));
		checkOutput("-50", T("<?print floor(x, -1)?>"), V("x", -41));
		checkOutput("-50", T("<?print floor(x, -1)?>"), V("x", -50));
		checkOutput("400", T("<?print floor(x, -2)?>"), V("x", 400));
		checkOutput("400", T("<?print floor(x, -2)?>"), V("x", 499));
		checkOutput("-500", T("<?print floor(x, -2)?>"), V("x", -401));
		checkOutput("-500", T("<?print floor(x, -2)?>"), V("x", -500));
		// Check int overflow
		checkOutput("-10000000000", T("<?print floor(x, -10)?>"), V("x", -2147483647));
		// Check long overflow
		checkOutput("-10000000000000000000", T("<?print floor(x, -19)?>"), V("x", -9223372036854775807L));

		checkOutput("<type int>", T("<?print type(floor(x))?>"), V("x", 42));
		checkOutput("<type int>", T("<?print type(floor(x, 1))?>"), V("x", 42));
		checkOutput("<type int>", T("<?print type(floor(x, -1))?>"), V("x", 49));

		checkOutput("True", T("<?print floor(x) == 42?>"), V("x", new Long(42)));
		checkOutput("True", T("<?print floor(x, 1) == 42?>"), V("x", new Long(42)));
		checkOutput("True", T("<?print floor(x, -1) == 40?>"), V("x", new Long(40)));
		checkOutput("True", T("<?print floor(x, -1) == 40?>"), V("x", new Long(49)));

		checkOutput("True", T("<?print floor(x) == 42?>"), V("x", new BigInteger("42")));
		checkOutput("True", T("<?print floor(x, 1) == 42?>"), V("x", new BigInteger("42")));
		checkOutput("True", T("<?print floor(x, -1) == 40?>"), V("x", new BigInteger("40")));
		checkOutput("True", T("<?print floor(x, -1) == 40?>"), V("x", new BigInteger("49")));
		checkOutput("True", T("<?print floor(x, -1) == -50?>"), V("x", new BigInteger("-41")));
		checkOutput("True", T("<?print floor(x, -1) == -50?>"), V("x", new BigInteger("-50")));
		checkOutput("True", T("<?print floor(x, -2) == 400?>"), V("x", new BigInteger("400")));
		checkOutput("True", T("<?print floor(x, -2) == 400?>"), V("x", new BigInteger("499")));
		checkOutput("True", T("<?print floor(x, -2) == -500?>"), V("x", new BigInteger("-401")));
		checkOutput("True", T("<?print floor(x, -2) == -500?>"), V("x", new BigInteger("-500")));
		checkOutput("1000000000000000000000000000000", T("<?print floor(x, -30)?>"), V("x", new BigInteger("1000000000000000000000000000000")));
		checkOutput("1000000000000000000000000000000", T("<?print floor(2*x-1, -30)?>"), V("x", new BigInteger("1000000000000000000000000000000")));
		checkOutput("-1000000000000000000000000000000", T("<?print floor(-x, -30)?>"), V("x", new BigInteger("1000000000000000000000000000000")));
		checkOutput("-2000000000000000000000000000000", T("<?print floor(-x-1, -30)?>"), V("x", new BigInteger("1000000000000000000000000000000")));
		checkOutput("-2000000000000000000000000000000", T("<?print floor(-2*x+1, -30)?>"), V("x", new BigInteger("1000000000000000000000000000000")));

		checkOutput("True", T("<?print floor(x) == 42?>"), V("x", 42.4));
		checkOutput("True", T("<?print floor(x) == 42?>"), V("x", 42.6));
		checkOutput("True", T("<?print floor(x) == -43?>"), V("x", -42.4));
		checkOutput("True", T("<?print floor(x) == -43?>"), V("x", -42.6));
		checkOutput("<type int>", T("<?print type(floor(x))?>"), V("x", 42.5));

		checkOutput("True", T("<?print floor(x, -1) == 40?>"), V("x", 42.4));
		checkOutput("True", T("<?print floor(x, -1) == 40?>"), V("x", 46.2));
		checkOutput("True", T("<?print floor(x, -1) == -50?>"), V("x", -42.4));
		checkOutput("True", T("<?print floor(x, -1) == -50?>"), V("x", -46.2));
		checkOutput("<type int>", T("<?print type(floor(x, -1))?>"), V("x", 42.5));

		checkOutput("True", T("<?print floor(x, 1) == 42.9?>"), V("x", 42.987));
		checkOutput("True", T("<?print floor(x, 1) == 42.1?>"), V("x", 42.123));
		checkOutput("True", T("<?print floor(x, 1) == -43.0?>"), V("x", -42.987));
		checkOutput("True", T("<?print floor(x, 1) == -42.2?>"), V("x", -42.123));
		// checkOutput("True", T("<?print floor(x, 2) == 42.58?>"), V("x", 42.589));
		checkOutput("True", T("<?print floor(x, 2) == 42.12?>"), V("x", 42.123));
		// checkOutput("True", T("<?print floor(x, 2) == -42.59?>"), V("x", -42.589));
		checkOutput("True", T("<?print floor(x, 2) == -42.13?>"), V("x", -42.123));
		checkOutput("<type float>", T("<?print type(floor(x, 1))?>"), V("x", 42.5));

		checkOutput("True", T("<?print floor(x) == 42?>"), V("x", new BigDecimal("42")));
		checkOutput("True", T("<?print floor(x, 1) == 42?>"), V("x", new BigDecimal("42")));
		checkOutput("True", T("<?print floor(x, -1) == 40?>"), V("x", new BigDecimal("40")));
		checkOutput("True", T("<?print floor(x, -1) == 40?>"), V("x", new BigDecimal("49")));
		checkOutput("True", T("<?print floor(x, -1) == -50?>"), V("x", new BigDecimal("-41")));
		checkOutput("True", T("<?print floor(x, -1) == -50?>"), V("x", new BigDecimal("-50")));
		checkOutput("True", T("<?print floor(x, -2) == 400?>"), V("x", new BigDecimal("400")));
		checkOutput("True", T("<?print floor(x, -2) == 400?>"), V("x", new BigDecimal("499")));
		checkOutput("True", T("<?print floor(x, -2) == -500?>"), V("x", new BigDecimal("-401")));
		checkOutput("True", T("<?print floor(x, -2) == -500?>"), V("x", new BigDecimal("-500")));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_floor_0_args()
	{
		checkOutput("", T("<?print floor()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_floor_3_args()
	{
		checkOutput("", T("<?print floor(1, 2, 3)?>"));
	}

	@Test
	public void function_ceil()
	{
		checkOutput("True", T("<?print ceil(x) == 42?>"), V("x", 42));
		checkOutput("True", T("<?print ceil(x, 1) == 42?>"), V("x", 42));
		checkOutput("True", T("<?print ceil(x, -1) == 50?>"), V("x", 41));
		checkOutput("True", T("<?print ceil(x, -1) == 50?>"), V("x", 50));
		checkOutput("True", T("<?print ceil(x, -1) == -50?>"), V("x", -50));
		checkOutput("True", T("<?print ceil(x, -1) == -40?>"), V("x", -41));
		checkOutput("True", T("<?print ceil(x, -2) == 500?>"), V("x", 401));
		checkOutput("True", T("<?print ceil(x, -2) == 500?>"), V("x", 500));
		checkOutput("True", T("<?print ceil(x, -1) == -500?>"), V("x", -500));
		checkOutput("True", T("<?print ceil(x, -1) == -400?>"), V("x", -401));
		checkOutput("<type int>", T("<?print type(ceil(x))?>"), V("x", 42));
		checkOutput("<type int>", T("<?print type(ceil(x, 1))?>"), V("x", 42));
		checkOutput("<type int>", T("<?print type(ceil(x, -1))?>"), V("x", 42));

		checkOutput("True", T("<?print ceil(x) == 42?>"), V("x", new Long(42)));
		checkOutput("True", T("<?print ceil(x, 1) == 42?>"), V("x", new Long(42)));
		checkOutput("True", T("<?print ceil(x, -1) == 50?>"), V("x", new Long(41)));
		checkOutput("True", T("<?print ceil(x, -1) == 50?>"), V("x", new Long(50)));

		checkOutput("True", T("<?print ceil(x) == 42?>"), V("x", new BigInteger("42")));
		checkOutput("True", T("<?print ceil(x, 1) == 42?>"), V("x", new BigInteger("42")));
		checkOutput("True", T("<?print ceil(x, -1) == 50?>"), V("x", new BigInteger("41")));
		checkOutput("True", T("<?print ceil(x, -1) == 50?>"), V("x", new BigInteger("50")));
		checkOutput("True", T("<?print ceil(x, -1) == -40?>"), V("x", new BigInteger("-41")));
		checkOutput("True", T("<?print ceil(x, -1) == -50?>"), V("x", new BigInteger("-50")));
		checkOutput("True", T("<?print ceil(x, -2) == 500?>"), V("x", new BigInteger("401")));
		checkOutput("True", T("<?print ceil(x, -2) == 500?>"), V("x", new BigInteger("500")));
		checkOutput("True", T("<?print ceil(x, -2) == -400?>"), V("x", new BigInteger("-401")));
		checkOutput("True", T("<?print ceil(x, -2) == -500?>"), V("x", new BigInteger("-500")));

		checkOutput("True", T("<?print ceil(x) == 43?>"), V("x", 42.4));
		checkOutput("True", T("<?print ceil(x) == 43?>"), V("x", 42.6));
		checkOutput("True", T("<?print ceil(x) == -42?>"), V("x", -42.4));
		checkOutput("True", T("<?print ceil(x) == -42?>"), V("x", -42.6));
		checkOutput("<type int>", T("<?print type(ceil(x))?>"), V("x", 42.5));

		checkOutput("True", T("<?print ceil(x, -1) == 50?>"), V("x", 42.4));
		checkOutput("True", T("<?print ceil(x, -1) == 50?>"), V("x", 46.2));
		checkOutput("True", T("<?print ceil(x, -1) == -40?>"), V("x", -42.4));
		checkOutput("True", T("<?print ceil(x, -1) == -40?>"), V("x", -46.2));
		checkOutput("<type int>", T("<?print type(ceil(x, -1))?>"), V("x", 42.5));

		checkOutput("True", T("<?print ceil(x, 1) == 43.0?>"), V("x", 42.987));
		checkOutput("True", T("<?print ceil(x, 1) == 42.2?>"), V("x", 42.123));
		checkOutput("True", T("<?print ceil(x, 1) == -42.9?>"), V("x", -42.987));
		checkOutput("True", T("<?print ceil(x, 1) == -42.1?>"), V("x", -42.123));
		// checkOutput("True", T("<?print ceil(x, 2) == 42.59?>"), V("x", 42.589));
		checkOutput("True", T("<?print ceil(x, 2) == 42.13?>"), V("x", 42.123));
		// checkOutput("True", T("<?print ceil(x, 2) == -42.58?>"), V("x", -42.589));
		checkOutput("True", T("<?print ceil(x, 2) == -42.12?>"), V("x", -42.123));
		checkOutput("<type float>", T("<?print type(ceil(x, 1))?>"), V("x", 42.5));

		checkOutput("True", T("<?print ceil(x) == 42?>"), V("x", new BigDecimal("42")));
		checkOutput("True", T("<?print ceil(x, 1) == 42?>"), V("x", new BigDecimal("42")));
		checkOutput("True", T("<?print ceil(x, -1) == 50?>"), V("x", new BigDecimal("41")));
		checkOutput("True", T("<?print ceil(x, -1) == 50?>"), V("x", new BigDecimal("50")));
		checkOutput("True", T("<?print ceil(x, -1) == -40?>"), V("x", new BigDecimal("-41")));
		checkOutput("True", T("<?print ceil(x, -1) == -50?>"), V("x", new BigDecimal("-50")));
		checkOutput("True", T("<?print ceil(x, -2) == 500?>"), V("x", new BigDecimal("401")));
		checkOutput("True", T("<?print ceil(x, -2) == 500?>"), V("x", new BigDecimal("500")));
		checkOutput("True", T("<?print ceil(x, -2) == -400?>"), V("x", new BigDecimal("-401")));
		checkOutput("True", T("<?print ceil(x, -2) == -500?>"), V("x", new BigDecimal("-500")));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_ceil_0_args()
	{
		checkOutput("", T("<?print ceil()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_ceil_3_args()
	{
		checkOutput("", T("<?print ceil(1, 2, 3)?>"));
	}

	@Test
	public void function_md5()
	{
		String result = "acbd18db4cc2f85cedef654fccc4a4d8";
		checkOutput(result, T("<?print md5('foo')?>"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_md5_bad_kwarg()
	{
		T("<?print md5(string='foo')?>").renders();
	}

	@Test
	public void function_scrypt()
	{
		String result = "468b5b132508a02f1868576247763abed96ac41db9287d21c8b5379ad71fbe2a2bf77fd3a738dda0572e0761938149f5b91b58d2ff87b9482680540606a710943d2a69f66fe89e2693361300c914b42c24abb29a80ef8840b6a0b67c96e5960292cc38cd959017931fe28e2a921107ade2f845e09a7590e9bf6755bd04ec51af";
		checkOutput(result, T("<?print scrypt('foo', 'bar')?>"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_scrypt_bad_kwarg()
	{
		T("<?print scrypt(string='foo', salt='bar')?>").renders();
	}

	@Test
	public void function_getattr()
	{
		checkOutput("GURK", T("<?print getattr('gurk', 'upper')()?>"));
		checkOutput("a:42;b:17;c:23;", T("<?for (key, value) in sorted(getattr(data, 'items')())?><?print key?>:<?print value?>;<?end for?>"), V("data", V("a", 42, "b", 17, "c", 23)));
		checkOutput("{/}", T("<?code getattr(data, 'clear')()?><?print data?>"), V("data", makeSet("a", "b", "c")));
		checkOutput("x=17, y=23", T("x=<?print getattr(data, 'x')?>, y=<?print getattr(data, 'y')?>"), V("data", new Point(17, 23)));
	}

	@Test
	public void function_hasattr()
	{
		checkOutput("True", T("<?print hasattr('gurk', 'upper')?>"));
		checkOutput("False", T("<?print hasattr('gurk', 'no')?>"));
		checkOutput("TrueFalseFalse", T("<?print hasattr(data, 'items')?><?print hasattr('data', 'a')?><?print hasattr('data', 'd')?>"), V("data", V("a", 42, "b", 17, "c", 23)));
		checkOutput("TrueFalse", T("<?print hasattr(data, 'clear')?><?print hasattr('data', 'a')?>"), V("data", makeSet("a", "b", "c")));
		checkOutput("TrueTrueFalse", T("<?print hasattr(data, 'x')?><?print hasattr(data, 'y')?><?print hasattr(data, 'z')?>"), V("data", new Point(17, 23)));
	}

	@Test
	public void function_setattr()
	{
		checkOutput("42", T("<?code setattr(data, 'x', 42)?><?print data.x?>"), V("data", new Point(17, 23)));
	}

	@CauseTest(expectedCause=ReadonlyException.class)
	public void function_setattr_readonly()
	{
		checkOutput("", T("<?code setattr(data, 'y', 42)?>"), V("data", new Point(17, 23)));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_setattr_wrongtype()
	{
		checkOutput("", T("<?code setattr(data, 'x', 'gurk')?>"), V("data", new Point(17, 23)));
	}

	@CauseTest(expectedCause=AttributeException.class)
	public void function_setattr_wrongattr()
	{
		checkOutput("", T("<?code setattr(data, 'z', 42)?>"), V("data", new Point(17, 23)));
	}

	@Test
	public void function_dir()
	{
		Object dataNull = null;
		Boolean dataBool = true;
		Integer dataInt = 42;
		Double dataFloat = 42.5;
		Date dataDate = new Date();
		LocalDate dataLocalDate = LocalDate.now();
		LocalDateTime dataLocalDateTime = LocalDateTime.now();
		Color dataColor = new Color(1, 2, 3, 4);
		List dataList = asList();
		Set dataSet = new HashSet();
		Map dataMap = makeMap("x", 17);
		Point dataPoint = new Point(17, 23);

		List dataAll = asList(dataNull, dataBool, dataInt, dataFloat, dataDate, dataColor, dataList, dataSet, dataMap, dataPoint);

		checkOutput("[]", T("<?print sorted(dir(data))?>"), V("data", dataNull));
		checkOutput("[]", T("<?print sorted(dir(data))?>"), V("data", dataBool));
		checkOutput("[]", T("<?print sorted(dir(data))?>"), V("data", dataInt));
		checkOutput("[]", T("<?print sorted(dir(data))?>"), V("data", dataFloat));
		String dateTimeAttrs = "['calendar', 'date', 'day', 'hour', 'isoformat', 'microsecond', 'mimeformat', 'minute', 'month', 'second', 'week', 'weekday', 'year', 'yearday']";
		checkOutput(dateTimeAttrs, T("<?print sorted(dir(data))?>"), V("data", dataDate));
		checkOutput(dateTimeAttrs, T("<?print sorted(dir(data))?>"), V("data", dataLocalDateTime));
		checkOutput("['calendar', 'date', 'day', 'isoformat', 'mimeformat', 'month', 'week', 'weekday', 'year', 'yearday']", T("<?print sorted(dir(data))?>"), V("data", dataLocalDate));
		checkOutput("['a', 'abslight', 'abslum', 'b', 'combine', 'g', 'hls', 'hlsa', 'hsv', 'hsva', 'hue', 'invert', 'light', 'lum', 'r', 'rellight', 'rellum', 'sat', 'witha', 'withhue', 'withlight', 'withlum', 'withsat']", T("<?print sorted(dir(data))?>"), V("data", dataColor));
		checkOutput("['append', 'count', 'find', 'insert', 'pop', 'rfind']", T("<?print sorted(dir(data))?>"), V("data", dataList));
		checkOutput("['add', 'clear']", T("<?print sorted(dir(data))?>"), V("data", dataSet));
		checkOutput("['clear', 'get', 'items', 'keys', 'pop', 'update', 'values']", T("<?print sorted(dir(data))?>"), V("data", dataMap));
		checkOutput("['x', 'y']", T("<?print sorted(dir(data))?>"), V("data", dataPoint));
		checkOutput("", T("<?for d in data?><?for an in dir(d)?><?if getattr(d, an, None) is None?><?print repr(d)?>.<?print an?>: FAIL<?end if?><?end for?><?end for?>"), V("data", dataAll));
	}

	@Test
	public void method_upper()
	{
		checkOutput("GURK", T("<?print 'gurk'.upper()?>"));
		checkOutput("GURK", T("<?code m = 'gurk'.upper?><?print m()?>"));
	}

	@Test
	public void method_lower()
	{
		checkOutput("gurk", T("<?print 'GURK'.lower()?>"));
		checkOutput("gurk", T("<?code m = 'GURK'.lower?><?print m()?>"));
	}

	@Test
	public void method_capitalize()
	{
		checkOutput("Gurk", T("<?print 'gURK'.capitalize()?>"));
		checkOutput("Gurk", T("<?code m = 'gURK'.capitalize?><?print m()?>"));
	}

	@Test
	public void method_startswith()
	{
		checkOutput("True", T("<?print 'gurkhurz'.startswith('gurk')?>"));
		checkOutput("False", T("<?print 'gurkhurz'.startswith('hurz')?>"));
		checkOutput("False", T("<?code m = 'gurkhurz'.startswith?><?print m('hurz')?>"));
		checkOutput("True", T("<?print 'gurkhurz'.startswith(['hu', 'gu'])?>"));
		checkOutput("False", T("<?print 'gurkhurz'.startswith(['rk', 'rz'])?>"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_startswith_bad_kwarg()
	{
		checkOutput("False", T("<?print 'gurkhurz'.startswith(prefix='hurz')?>"));
	}

	@Test
	public void method_endswith()
	{
		checkOutput("True", T("<?print 'gurkhurz'.endswith('hurz')?>"));
		checkOutput("False", T("<?print 'gurkhurz'.endswith('gurk')?>"));
		checkOutput("False", T("<?code m = 'gurkhurz'.endswith?><?print m('gurk')?>"));
		checkOutput("False", T("<?print 'gurkhurz'.endswith(['hu', 'gu'])?>"));
		checkOutput("True", T("<?print 'gurkhurz'.endswith(['rk', 'rz'])?>"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_endswith_bad_kwarg()
	{
		checkOutput("False", T("<?print 'gurkhurz'.endswith(suffix='gurk')?>"));
	}

	@Test
	public void method_strip()
	{
		checkOutput("gurk", T("<?print obj.strip()?>"), V("obj", " \t\r\ngurk \t\r\n"));
		checkOutput("gurk", T("<?print obj.strip('xyz')?>"), V("obj", "xyzzygurkxyzzy"));
		checkOutput("gurk", T("<?code m = obj.strip?><?print m('xyz')?>"), V("obj", "xyzzygurkxyzzy"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_strip_bad_kwarg()
	{
		checkOutput("gurk", T("<?print obj.strip(chars='xyz')?>"), V("obj", "xyzzygurkxyzzy"));
	}

	@Test
	public void method_lstrip()
	{
		checkOutput("gurk \t\r\n", T("<?print obj.lstrip()?>"), V("obj", " \t\r\ngurk \t\r\n"));
		checkOutput("gurkxyzzy", T("<?print obj.lstrip(arg)?>"), V("obj", "xyzzygurkxyzzy", "arg", "xyz"));
		checkOutput("gurkxyzzy", T("<?code m = obj.lstrip?><?print m(arg)?>"), V("obj", "xyzzygurkxyzzy", "arg", "xyz"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_lstrip_bad_kwarg()
	{
		checkOutput("gurkxyzzy", T("<?print obj.lstrip(chars=arg)?>"), V("obj", "xyzzygurkxyzzy", "arg", "xyz"));
	}

	@Test
	public void method_rstrip()
	{
		checkOutput(" \t\r\ngurk", T("<?print obj.rstrip()?>"), V("obj", " \t\r\ngurk \t\r\n"));
		checkOutput("xyzzygurk", T("<?print obj.rstrip(arg)?>"), V("obj", "xyzzygurkxyzzy", "arg", "xyz"));
		checkOutput("xyzzygurk", T("<?code m = obj.rstrip?><?print m(arg)?>"), V("obj", "xyzzygurkxyzzy", "arg", "xyz"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void function_rstrip_bad_kwarg()
	{
		checkOutput("xyzzygurk", T("<?print obj.rstrip(chars=arg)?>"), V("obj", "xyzzygurkxyzzy", "arg", "xyz"));
	}

	@Test
	public void method_split()
	{
		checkOutput("(f)(o)(o)", T("<?for item in obj.split()?>(<?print item?>)<?end for?>"), V("obj", " \t\r\nf \t\r\no \t\r\no \t\r\n"));
		checkOutput("(f)(o \t\r\no \t\r\n)", T("<?for item in obj.split(None, 1)?>(<?print item?>)<?end for?>"), V("obj", " \t\r\nf \t\r\no \t\r\no \t\r\n"));
		checkOutput("()(f)(o)(o)()", T("<?for item in obj.split(arg)?>(<?print item?>)<?end for?>"), V("obj", "xxfxxoxxoxx", "arg", "xx"));
		checkOutput("()(f)(o)(o)()", T("<?for item in obj.split(arg, None)?>(<?print item?>)<?end for?>"), V("obj", "xxfxxoxxoxx", "arg", "xx"));
		checkOutput("()(f)(oxxoxx)", T("<?for item in obj.split(arg, 2)?>(<?print item?>)<?end for?>"), V("obj", "xxfxxoxxoxx", "arg", "xx"));
		checkOutput("()(f)(oxxoxx)", T("<?code m = obj.split?><?for item in m(arg, 2)?>(<?print item?>)<?end for?>"), V("obj", "xxfxxoxxoxx", "arg", "xx"));
		checkOutput("()(f)(oxxoxx)", T("<?for item in obj.split(sep=arg, maxsplit=2)?>(<?print item?>)<?end for?>"), V("obj", "xxfxxoxxoxx", "arg", "xx"));
	}

	@Test
	public void method_rsplit()
	{
		checkOutput("(f)(o)(o)", T("<?for item in obj.rsplit()?>(<?print item?>)<?end for?>"), V("obj", " \t\r\nf \t\r\no \t\r\no \t\r\n"));
		checkOutput("( \t\r\nf \t\r\no)(o)", T("<?for item in obj.rsplit(None, 1)?>(<?print item?>)<?end for?>"), V("obj", " \t\r\nf \t\r\no \t\r\no \t\r\n"));
		checkOutput("()(f)(o)(o)()", T("<?for item in obj.rsplit(arg)?>(<?print item?>)<?end for?>"), V("obj", "xxfxxoxxoxx", "arg", "xx"));
		checkOutput("()(f)(o)(o)()", T("<?for item in obj.rsplit(arg, None)?>(<?print item?>)<?end for?>"), V("obj", "xxfxxoxxoxx", "arg", "xx"));
		checkOutput("(xxfxxo)(o)()", T("<?for item in obj.rsplit(arg, 2)?>(<?print item?>)<?end for?>"), V("obj", "xxfxxoxxoxx", "arg", "xx"));
		checkOutput("(xxfxxo)(o)()", T("<?code m = obj.rsplit?><?for item in m(arg, 2)?>(<?print item?>)<?end for?>"), V("obj", "xxfxxoxxoxx", "arg", "xx"));
		checkOutput("(xxfxxo)(o)()", T("<?for item in obj.rsplit(sep=arg, maxsplit=2)?>(<?print item?>)<?end for?>"), V("obj", "xxfxxoxxoxx", "arg", "xx"));
	}

	@Test
	public void method_splitlines()
	{
		checkOutput(
			"('a')('b')('c')('d')('e')('f')('g')('h')('i')('j')('k')",
			T("<?for item in obj.splitlines(keepends)?>(<?print repr(item)?>)<?end for?>"),
			V(
				"obj", "a\nb\rc\r\nd\u000be\u000cf\u001cg\u001dh\u001ei\u0085j\u2028k\u2029",
				"keepends", false
			)
		);

		checkOutput(
			"('a\\n')('b\\r')('c\\r\\n')('d\\x0b')('e\\x0c')('f\\x1c')('g\\x1d')('h\\x1e')('i\\x85')('j\\u2028')('k\\u2029')",
			T("<?for item in obj.splitlines(keepends)?>(<?print repr(item)?>)<?end for?>"),
			V(
				"obj", "a\nb\rc\r\nd\u000be\u000cf\u001cg\u001dh\u001ei\u0085j\u2028k\u2029",
				"keepends", true
			)
		);

		checkOutput("['a', 'b']", T("<?print obj.splitlines(keepends=false)?>"), V("obj", "a\nb"));
	}

	@Test
	public void method_replace()
	{
		checkOutput("goork", T("<?print 'gurk'.replace('u', 'oo')?>"));
		checkOutput("fuuuu", T("<?print 'foo'.replace('o', 'uu', None)?>"));
		checkOutput("fuuo", T("<?print 'foo'.replace('o', 'uu', 1)?>"));
		checkOutput("fuuo", T("<?code m = 'foo'.replace?><?print m('o', 'uu', 1)?>"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void method_replace_bad_kwarg()
	{
		T("<?print 'foo'.replace(old='o', new='uu', count=1)?>").renders();
	}

	@Test
	public void method_renders()
	{
		Template t1 = T("(<?print data?>)", "t1");

		checkOutput("(GURK)", T("<?print t.renders(data='gurk').upper()?>"), V("t", t1));
		checkOutput("(GURK)", T("<?code m = t.renders?><?print m(data='gurk').upper()?>"), V("t", t1));

		Template t2 = T("(gurk)", "t2");
		checkOutput("(GURK)", T("<?print t.renders().upper()?>"), V("t", t2));
	}

	@Test
	public void render()
	{
		checkOutput("gurk", T("<?def x?>gurk<?end def?><?render x()?>"));

		Template t1 = T("<?print prefix?><?print data?><?print suffix?>");
		Template t2 = T("<?print 'foo'?>");

		checkOutput("(f)(o)(o)", T("<?for c in data?><?render t(data=c, prefix='(', suffix=')')?><?end for?>"), V("t", t1, "data", "foo"));
		checkOutput("foo", T("<?render t()?>"), V("t", t2));
		checkOutput("foo", T("<?render t \n\t(\n \t)\n\t ?>"), V("t", t2));

		checkOutput("42", T("<?render globals.template(value=42)?>"), V("globals", V("template", T("<?print value?>"))));
		checkOutput("", T("<?render globals.template(value=42)?>"), V("globals", V("template", T(""))));

		checkOutput("42", T("<?def x()?><?print y?><?end def?><?code y = 42?><?render x()?>"));
	}

	@Test
	public void renderx()
	{
		checkOutput("&lt;&amp;&gt;", T("<?def x?><&><?end def?><?renderx x()?>"));
	}

	@Test
	public void renderblock() throws Exception
	{
		checkOutput("(gurk)", T("<?def bracket(content)?>(<?render content()?>)<?end def?><?renderblock bracket()?>gurk<?end renderblock?>"));
	}

	@Test
	public void renderblocks() throws Exception
	{
		checkOutput("(gurk)", T("<?def bracket(content, prefix='(', suffix=')')?><?printx prefix?><?render content()?><?printx suffix?><?end def?><?renderblocks bracket()?><?def content?>gurk<?end def?><?end renderblocks?>"));
	}

	@Test
	public void render_local_vars()
	{
		Template t = T("<?code x += 1?><?print x?>");

		checkOutput("42,43,42", T("<?print x?>,<?render t(x=x)?>,<?print x?>"), V("t", t, "x", 42));
	}

	@Test
	public void render_localtemplate()
	{
		checkOutput("foo", T("<?def lower?><?print x.lower()?><?end def?><?print lower.renders(x='FOO')?>"));
	}

	@Test
	public void deprecated_rendermethod()
	{
		checkOutput("gurk", T("<?def x?>gurk<?end def?><?code x.render()?>"));

		Template t1 = T("<?print prefix?><?print data?><?print suffix?>");
		Template t2 = T("<?print 'foo'?>");

		checkOutput("(f)(o)(o)", T("<?for c in data?><?code t.render(data=c, prefix='(', suffix=')')?><?end for?>"), V("t", t1, "data", "foo"));
		checkOutput("foo", T("<?render t()?>"), V("t", t2));
		checkOutput("foo", T("<?render t \n\t(\n \t)\n\t ?>"), V("t", t2));

		checkOutput("42", T("<?code globals.template.render(value=42)?>"), V("globals", V("template", T("<?print value?>"))));
		checkOutput("", T("<?code globals.template.render(value=42)?>"), V("globals", V("template", T(""))));

		checkOutput("42", T("<?def x()?><?print y?><?end def?><?code y = 42?><?code x.render()?>"));
	}

	@Test
	public void deprecated_rendermethod_local_vars()
	{
		Template t = T("<?code x += 1?><?print x?>");

		checkOutput("42,43,42", T("<?print x?>,<?code t.render(x=x)?>,<?print x?>"), V("t", t, "x", 42));
	}

	@Test
	public void render_nested()
	{
		Template t = T(
			"<?def outer?>" +
				"<?def inner?>" +
					"<?code x += 1?>" +
					"<?code y += 1?>" +
					"<?print x?>!" +
					"<?print y?>!" +
				"<?end def?>" +
				"<?code x += 1?>" +
				"<?code y += 1?>" +
				"<?render inner(x=x)?>" +
				"<?print x?>!" +
				"<?print y?>!" +
			"<?end def?>" +
			"<?code x += 1?>" +
			"<?code y += 1?>" +
			"<?render outer(x=x)?>" +
			"<?print x?>!" +
			"<?print y?>!"
		);
		checkOutput("45!45!44!44!43!43!", t, V("x", 42, "y", 42));
	}

	@Test
	public void method_mimeformat()
	{
		Date t = makeDate(2010, 2, 22, 12, 34, 56);
		checkOutput("Mon, 22 Feb 2010 12:34:56 GMT", T("<?print data.mimeformat()?>"), V("data", t));
		checkOutput("Mon, 22 Feb 2010 12:34:56 GMT", T("<?code m = data.mimeformat?><?print m()?>"), V("data", t));
	}

	@Test
	public void method_keys()
	{
		checkOutput("a;b;c;", T("<?for key in sorted(data.keys())?><?print key?>;<?end for?>"), V("data", V("a", 42, "b", 17, "c", 23)));
	}

	@Test
	public void method_items()
	{
		checkOutput("a:42;b:17;c:23;", T("<?for (key, value) in sorted(data.items())?><?print key?>:<?print value?>;<?end for?>"), V("data", V("a", 42, "b", 17, "c", 23)));
	}

	@Test
	public void method_values()
	{
		checkOutput("17;23;42;", T("<?for value in sorted(data.values())?><?print value?>;<?end for?>"), V("data", V("a", 42, "b", 17, "c", 23)));
	}

	@Test
	public void method_get()
	{
		checkOutput("42", T("<?print {}.get('foo', 42)?>"));
		checkOutput("17", T("<?print {'foo': 17}.get('foo', 42)?>"));
		checkOutput("", T("<?print {}.get('foo')?>"));
		checkOutput("17", T("<?print {'foo': 17}.get('foo')?>"));
		checkOutput("17", T("<?code m = {'foo': 17}.get?><?print m('foo')?>"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void method_get_bad_kwarg()
	{
		T("<?print {'foo': 17}.get(key='foo', default=42)?>").renders();
	}

	@Test
	public void method_clear()
	{
		checkOutput("{}", T("<?code d = {17: 23}?><?code d.clear()?><?print d?>"));
		checkOutput("{/}", T("<?code d = {17, 23}?><?code d.clear()?><?print d?>"));
	}

	@Test
	public void method_add()
	{
		checkOutput("[42, 43]", T("<?code s = {/}?><?code s.add(42, 43)?><?print sorted(s)?>"));
	}

	@Test
	public void method_r_g_b_a()
	{
		checkOutput("0x11", T("<?code c = #123?><?print hex(c.r())?>"));
		checkOutput("0x22", T("<?code c = #123?><?print hex(c.g())?>"));
		checkOutput("0x33", T("<?code c = #123?><?print hex(c.b())?>"));
		checkOutput("0xff", T("<?code c = #123?><?print hex(c.a())?>"));
		checkOutput("0x11", T("<?code c = #123?><?code m = c.r?><?print hex(m())?>"));
		checkOutput("0x22", T("<?code c = #123?><?code m = c.g?><?print hex(m())?>"));
		checkOutput("0x33", T("<?code c = #123?><?code m = c.b?><?print hex(m())?>"));
		checkOutput("0xff", T("<?code c = #123?><?code m = c.a?><?print hex(m())?>"));
	}

	@Test
	public void method_hls()
	{
		checkOutput("0", T("<?code c = #fff?><?print int(c.hls()[0])?>"));
		checkOutput("1", T("<?code c = #fff?><?print int(c.hls()[1])?>"));
		checkOutput("0", T("<?code c = #fff?><?print int(c.hls()[2])?>"));
		checkOutput("0", T("<?code c = #fff?><?code m = c.hls?><?print int(m()[0])?>"));
	}

	@Test
	public void method_hlsa()
	{
		checkOutput("0", T("<?code c = #fff?><?print int(c.hlsa()[0])?>"));
		checkOutput("1", T("<?code c = #fff?><?print int(c.hlsa()[1])?>"));
		checkOutput("0", T("<?code c = #fff?><?print int(c.hlsa()[2])?>"));
		checkOutput("1", T("<?code c = #fff?><?print int(c.hlsa()[3])?>"));
		checkOutput("0", T("<?code c = #fff?><?code m = c.hlsa?><?print int(m()[0])?>"));
	}

	@Test
	public void method_hsv()
	{
		checkOutput("0", T("<?code c = #fff?><?print int(c.hsv()[0])?>"));
		checkOutput("0", T("<?code c = #fff?><?print int(c.hsv()[1])?>"));
		checkOutput("1", T("<?code c = #fff?><?print int(c.hsv()[2])?>"));
		checkOutput("0", T("<?code c = #fff?><?code m = c.hsv?><?print int(m()[0])?>"));
	}

	@Test
	public void method_hsva()
	{
		checkOutput("0", T("<?code c = #fff?><?print int(c.hsva()[0])?>"));
		checkOutput("0", T("<?code c = #fff?><?print int(c.hsva()[1])?>"));
		checkOutput("1", T("<?code c = #fff?><?print int(c.hsva()[2])?>"));
		checkOutput("1", T("<?code c = #fff?><?print int(c.hsva()[3])?>"));
		checkOutput("0", T("<?code c = #fff?><?code m = c.hsva?><?print int(m()[0])?>"));
	}

	@Test
	public void method_hue()
	{
		checkOutput("True", T("<?print math.isclose(  0/360, #f00.hue())?>"));
		checkOutput("True", T("<?print math.isclose(120/360, #0f0.hue())?>"));
		checkOutput("True", T("<?print math.isclose(240/360, #00f.hue())?>"));
	}

	@Test
	public void method_sat()
	{
		checkOutput("True", T("<?print math.isclose(0.0, #fff.sat())?>"));
		checkOutput("True", T("<?print math.isclose(0.0, #000.sat())?>"));
		checkOutput("True", T("<?print math.isclose(1.0, #f00.sat())?>"));
		checkOutput("True", T("<?print math.isclose(1.0, #0f0.sat())?>"));
		checkOutput("True", T("<?print math.isclose(1.0, #00f.sat())?>"));
	}

	@Test
	public void method_light()
	{
		checkOutput("True", T("<?print #fff.light() == 1?>"));
		checkOutput("True", T("<?code m = #fff.light?><?print m() == 1?>"));
	}

	@Test
	public void method_lum()
	{
		checkOutput("True", T("<?print math.isclose(1.0   , #fff.lum())?>"));
		checkOutput("True", T("<?print math.isclose(0.0   , #000.lum())?>"));
		checkOutput("True", T("<?print math.isclose(0.2126, #f00.lum())?>"));
		checkOutput("True", T("<?print math.isclose(0.7152, #0f0.lum())?>"));
		checkOutput("True", T("<?print math.isclose(0.0722, #00f.lum())?>"));
	}

	@Test
	public void method_withhue()
	{
		checkOutput("#f00", T("<?print #0f0.withhue(1)?>"));
		checkOutput("#f00", T("<?code m = #0f0.withhue?><?print m(1)?>"));

		checkOutput("#f00", T("<?print #0f0.withhue(hue=1)?>"));
	}

	@Test
	public void method_withlight()
	{
		checkOutput("#fff", T("<?print #000.withlight(1)?>"));
		checkOutput("#fff", T("<?code m = #000.withlight?><?print m(1)?>"));

		checkOutput("#fff", T("<?print #000.withlight(light=1)?>"));
	}

	@Test
	public void method_withsat()
	{
		checkOutput("#7f7f7f", T("<?print #0f0.withsat(0)?>"));
		checkOutput("#7f7f7f", T("<?code m = #0f0.withsat?><?print m(0)?>"));

		checkOutput("#7f7f7f", T("<?print #0f0.withsat(sat=0)?>"));
	}

	@Test
	public void method_witha()
	{
		checkOutput("#0063a82a", T("<?print repr(#0063a8.witha(42))?>"));
		checkOutput("#0063a82a", T("<?code m =#0063a8.witha?><?print repr(m(42))?>"));

		checkOutput("#0063a82a", T("<?print repr(#0063a8.witha(a=42))?>"));
	}

	@Test
	public void method_abslight()
	{
		checkOutput("#fff", T("<?print #000.abslight(1)?>"));
		checkOutput("#fff", T("<?code m = #000.abslight?><?print m(1)?>"));
		checkOutput("#000", T("<?print #fff.abslight(-1)?>"));
	}

	@Test
	public void method_rellight()
	{
		checkOutput("#000", T("<?print #888.rellight(-1)?>"));
		checkOutput("#888", T("<?print #888.rellight(0)?>"));
		checkOutput("#fff", T("<?print #888.rellight(1)?>"));
		checkOutput("#000", T("<?code m = #888.rellight?><?print m(-1)?>"));
	}

	@Test
	public void method_withlum()
	{
		checkOutput("#fff", T("<?print #000.withlum(1)?>"));
		checkOutput("#fff", T("<?code m = #000.withlum?><?print m(1)?>"));

		checkOutput("#000", T("<?print #fff.withlum(0)?>"));
		checkOutput("#333", T("<?print #fff.withlum(0.2)?>"));
		checkOutput("#f00", T("<?print #f00.withlum(0.2126)?>"));
		checkOutput("#0f0", T("<?print #0f0.withlum(0.7152)?>"));
		checkOutput("#00f", T("<?print #00f.withlum(0.0722)?>"));

		// Make sure that the parameters have the same name in all implementations
		checkOutput("#fff", T("<?print #000.withlum(lum=1)?>"));
	}

	@Test
	public void method_abslum()
	{
		checkOutput("#fff", T("<?print #000.abslum(1)?>"));
		checkOutput("#fff", T("<?code m = #000.abslum?><?print m(1)?>"));

		checkOutput("#fff", T("<?print #fff.abslum(0)?>"));
		checkOutput("#000", T("<?print #fff.abslum(-1)?>"));

		// Make sure that the parameters have the same name in all implementations
		checkOutput("#fff", T("<?print #000.abslum(f=1)?>"));
	}

	@Test
	public void method_rellum()
	{
		checkOutput("#fff", T("<?print #000.rellum(1)?>"));
		checkOutput("#fff", T("<?code m = #000.rellum?><?print m(1)?>"));

		checkOutput("#fff", T("<?print #fff.rellum(0)?>"));
		checkOutput("#000", T("<?print #fff.rellum(-1)?>"));
		checkOutput("#888", T("<?print #888.rellum(0)?>"));
		checkOutput("#f33", T("<?print #f00.rellum(0.2)?>"));
		checkOutput("#3f3", T("<?print #0f0.rellum(0.2)?>"));
		checkOutput("#33f", T("<?print #00f.rellum(0.2)?>"));

		// Make sure that the parameters have the same name in all implementations
		checkOutput("#fff", T("<?print #000.rellum(f=1)?>"));
	}

	@Test
	public void method_invert()
	{
		checkOutput("#fff", T("<?code m = #000.invert?><?print m(1)?>"));

		checkOutput("#000", T("<?print #000.invert(0)?>"));
		checkOutput("#333", T("<?print #000.invert(0.2)?>"));
		checkOutput("#fff", T("<?print #000.invert(1)?>"));
		checkOutput("#fff", T("<?print #000.invert()?>"));
		checkOutput("#fff", T("<?print #fff.invert(0)?>"));
		checkOutput("#ccc", T("<?print #fff.invert(0.2)?>"));
		checkOutput("#000", T("<?print #fff.invert(1)?>"));
		checkOutput("#000", T("<?print #fff.invert()?>"));
		checkOutput("#0ff", T("<?print #f00.invert()?>"));
		checkOutput("#f0f", T("<?print #0f0.invert()?>"));
		checkOutput("#ff0", T("<?print #00f.invert()?>"));

		// Make sure that the parameters have the same name in all implementations
		checkOutput("#fff", T("<?print #000.invert(f=1)?>"));
	}

	@Test
	public void method_combine()
	{
		checkOutput("#783456", T("<?print repr(color.Color(0x12, 0x34, 0x56).combine(r=0x78))?>"));
		checkOutput("#127856", T("<?print repr(color.Color(0x12, 0x34, 0x56).combine(g=0x78))?>"));
		checkOutput("#123478", T("<?print repr(color.Color(0x12, 0x34, 0x56).combine(b=0x78))?>"));
		checkOutput("#12345678", T("<?print repr(color.Color(0x12, 0x34, 0x56).combine(a=0x78))?>"));
	}

	@Test
	public void method_join()
	{
		checkOutput("1,2,3,4", T("<?print ','.join('1234')?>"));
		checkOutput("1,2,3,4", T("<?print ','.join(['1', '2', '3', '4'])?>"));
		checkOutput("1,2,3,4", T("<?code m = ','.join?><?print m('1234')?>"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void method_join_bad_kwarg()
	{
		T("<?print ','.join(iterable='1234')?>").renders();
	}

	@Test
	public void method_count()
	{
		Template t = T("<?print haystack.count(needle, start, end)?>");

		checkOutput("3", t, V("haystack", "aaa", "needle", "a", "start", null, "end", null));
		checkOutput("0", t, V("haystack", "aaa", "needle", "b", "start", null, "end", null));
		checkOutput("3", t, V("haystack", "aaa", "needle", "a", "start", null, "end", null));
		checkOutput("0", t, V("haystack", "aaa", "needle", "b", "start", null, "end", null));
		checkOutput("3", t, V("haystack", "aaa", "needle", "a", "start", null, "end", null));
		checkOutput("0", t, V("haystack", "aaa", "needle", "b", "start", null, "end", null));
		checkOutput("0", t, V("haystack", "aaa", "needle", "b", "start", null, "end", null));
		checkOutput("2", t, V("haystack", "aaa", "needle", "a", "start", 1, "end", null));
		checkOutput("0", t, V("haystack", "aaa", "needle", "a", "start", 10, "end", null));
		checkOutput("1", t, V("haystack", "aaa", "needle", "a", "start", -1, "end", null));
		checkOutput("3", t, V("haystack", "aaa", "needle", "a", "start", -10, "end", null));
		checkOutput("1", t, V("haystack", "aaa", "needle", "a", "start", 0, "end", 1));
		checkOutput("3", t, V("haystack", "aaa", "needle", "a", "start", 0, "end", 10));
		checkOutput("2", t, V("haystack", "aaa", "needle", "a", "start", 0, "end", -1));
		checkOutput("0", t, V("haystack", "aaa", "needle", "a", "start", 0, "end", -10));
		checkOutput("3", t, V("haystack", "aaa", "needle", "", "start", 1, "end", null));
		checkOutput("1", t, V("haystack", "aaa", "needle", "", "start", 3, "end", null));
		checkOutput("0", t, V("haystack", "aaa", "needle", "", "start", 10, "end", null));
		checkOutput("2", t, V("haystack", "aaa", "needle", "", "start", -1, "end", null));
		checkOutput("4", t, V("haystack", "aaa", "needle", "", "start", -10, "end", null));

		checkOutput("1", t, V("haystack", "",  "needle", "", "start", null, "end", null));
		checkOutput("0", t, V("haystack", "",  "needle", "", "start", 1, "end", 1));
		checkOutput("0", t, V("haystack", "",  "needle", "", "start", 0x7fffffff, "end", 0));

		checkOutput("0", t, V("haystack", "",  "needle", "xx", "start", null, "end", null));
		checkOutput("0", t, V("haystack", "",  "needle", "xx", "start", 1, "end", 1));
		checkOutput("0", t, V("haystack", "",  "needle", "xx", "start", 0x7fffffff, "end", 0));

		checkOutput("1", t, V("haystack", "aba", "needle", "ab", "start", null, "end", 2));
		checkOutput("0", t, V("haystack", "aba", "needle", "ab", "start", null, "end", 1));

		// Matches are non overlapping
		checkOutput("1", t, V("haystack", "aaa", "needle", "aa", "start", null, "end", null));

		// Test the list version
		List list = asList(1, 2, 3, 2, 3, 4, 1, 2, 3);
		checkOutput("0", t, V("haystack", list, "needle", "a", "start", null, "end", null));
		checkOutput("3", t, V("haystack", list, "needle", 2, "start", null, "end", null));
		checkOutput("2", t, V("haystack", list, "needle", 2, "start", 2, "end", null));
		checkOutput("1", t, V("haystack", list, "needle", 2, "start", 2, "end", 7));
	}

	@Test
	public void method_find()
	{
		checkOutput("-1", T("<?print s.find('ks')?>"), V("s", "gurkgurk"));
		checkOutput("2", T("<?print s.find('rk')?>"), V("s", "gurkgurk"));
		checkOutput("2", T("<?print s.find('rk', 2)?>"), V("s", "gurkgurk"));
		checkOutput("2", T("<?print s.find('rk', 2, 4)?>"), V("s", "gurkgurk"));
		checkOutput("6", T("<?print s.find('rk', 4, 8)?>"), V("s", "gurkgurk"));
		checkOutput("5", T("<?print s.find('ur', -4, -1)?>"), V("s", "gurkgurk"));
		checkOutput("-1", T("<?print s.find('rk', 2, 3)?>"), V("s", "gurkgurk"));
		checkOutput("-1", T("<?print s.find('rk', 7)?>"), V("s", "gurkgurk"));
		checkOutput("-1", T("<?code m = s.find?><?print m('ks')?>"), V("s", "gurkgurk"));

		checkOutput("-1", T("<?print l.find('x')?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("2", T("<?print l.find('r')?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("2", T("<?print l.find('r', 2)?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("2", T("<?print l.find('r', 2, 4)?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("6", T("<?print l.find('r', 4, 8)?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("6", T("<?print l.find('r', -3, -1)?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("-1", T("<?print l.find('r', 2, 2)?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("-1", T("<?print l.find('r', 7)?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("2", T("<?print l.find(None)?>"), V("l", asList("g", "u", null, "k", "g", "u", "r", "k")));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void method_find_int_bad_kwarg()
	{
		T("<?print s.find(sub='rk', start=2, end=4)?>").renders(V("s", "gurkgurk"));
	}

	@Test
	public void method_rfind()
	{
		checkOutput("-1", T("<?print s.rfind('ks')?>"), V("s", "gurkgurk"));
		checkOutput("6", T("<?print s.rfind('rk')?>"), V("s", "gurkgurk"));
		checkOutput("6", T("<?print s.rfind('rk', 2)?>"), V("s", "gurkgurk"));
		checkOutput("2", T("<?print s.rfind('rk', 2, 4)?>"), V("s", "gurkgurk"));
		checkOutput("6", T("<?print s.rfind('rk', 4, 8)?>"), V("s", "gurkgurk"));
		checkOutput("5", T("<?print s.rfind('ur', -4, -1)?>"), V("s", "gurkgurk"));
		checkOutput("-1", T("<?print s.rfind('rk', 2, 3)?>"), V("s", "gurkgurk"));
		checkOutput("-1", T("<?print s.rfind('rk', 7)?>"), V("s", "gurkgurk"));
		checkOutput("-1", T("<?code m = s.rfind?><?print m('ks')?>"), V("s", "gurkgurk"));

		checkOutput("-1", T("<?print l.rfind('x')?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("6", T("<?print l.rfind('r')?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("6", T("<?print l.rfind('r', 2)?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("2", T("<?print l.rfind('r', 2, 4)?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("6", T("<?print l.rfind('r', 4, 8)?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("6", T("<?print l.rfind('r', -3, -1)?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("-1", T("<?print l.rfind('r', 2, 2)?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("-1", T("<?print l.rfind('r', 7)?>"), V("l", asList("g", "u", "r", "k", "g", "u", "r", "k")));
		checkOutput("2", T("<?print l.rfind(None)?>"), V("l", asList("g", "u", null, "k", "g", "u", "r", "k")));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void method_rfind_bad_kwarg()
	{
		T("<?print s.rfind(sub='rk', start=2, end=4)?>").renders(V("s", "gurkgurk"));
	}

	@Test
	public void method_day()
	{
		checkOutput("12", T("<?print @(2010-05-12).day()?>"));
		checkOutput("12", T("<?print @(2010-05-12T).day()?>"));
		checkOutput("12", T("<?code m = @(2010-05-12).day?><?print m()?>"));
		checkOutput("12", T("<?print d.day()?>"), V("d", makeDate(2010, 5, 12)));
		checkOutput("12", T("<?print d.day()?>"), V("d", LocalDate.of(2010, 5, 12)));
		checkOutput("12", T("<?print d.day()?>"), V("d", LocalDateTime.of(2010, 5, 12, 0, 0)));
	}

	@Test
	public void method_month()
	{
		checkOutput("5", T("<?print @(2010-05-12).month()?>"));
		checkOutput("5", T("<?print @(2010-05-12T).month()?>"));
		checkOutput("5", T("<?code m = @(2010-05-12).month?><?print m()?>"));
		checkOutput("5", T("<?print d.month()?>"), V("d", makeDate(2010, 5, 12)));
		checkOutput("5", T("<?print d.month()?>"), V("d", LocalDate.of(2010, 5, 12)));
		checkOutput("5", T("<?print d.month()?>"), V("d", LocalDateTime.of(2010, 5, 12, 0, 0)));
	}

	@Test
	public void method_year()
	{
		checkOutput("2010", T("<?print @(2010-05-12).year()?>"));
		checkOutput("2010", T("<?print @(2010-05-12T).year()?>"));
		checkOutput("2010", T("<?code m = @(2010-05-12).year?><?print m()?>"));
		checkOutput("2010", T("<?print d.year()?>"), V("d", makeDate(2010, 5, 12)));
		checkOutput("2010", T("<?print d.year()?>"), V("d", LocalDate.of(2010, 5, 12)));
		checkOutput("2010", T("<?print d.year()?>"), V("d", LocalDateTime.of(2010, 5, 12, 0, 0)));
	}

	@Test
	public void method_hour()
	{
		checkOutput("16", T("<?print @(2010-05-12T16:47:56).hour()?>"));
		checkOutput("16", T("<?code m = @(2010-05-12T16:47:56).hour?><?print m()?>"));
		checkOutput("16", T("<?print d.hour()?>"), V("d", makeDate(2010, 5, 12, 16, 47, 56)));
		checkOutput("16", T("<?print d.hour()?>"), V("d", LocalDateTime.of(2010, 5, 12, 16, 47, 56)));
	}

	@Test
	public void method_minute()
	{
		checkOutput("47", T("<?print @(2010-05-12T16:47:56).minute()?>"));
		checkOutput("47", T("<?code m = @(2010-05-12T16:47:56).minute?><?print m()?>"));
		checkOutput("47", T("<?print d.minute()?>"), V("d", makeDate(2010, 5, 12, 16, 47, 56)));
		checkOutput("47", T("<?print d.minute()?>"), V("d", LocalDateTime.of(2010, 5, 12, 16, 47, 56)));
	}

	@Test
	public void method_second()
	{
		checkOutput("56", T("<?print @(2010-05-12T16:47:56).second()?>"));
		checkOutput("56", T("<?code m = @(2010-05-12T16:47:56).second?><?print m()?>"));
		checkOutput("56", T("<?print d.second()?>"), V("d", makeDate(2010, 5, 12, 16, 47, 56)));
		checkOutput("56", T("<?print d.second()?>"), V("d", LocalDateTime.of(2010, 5, 12, 16, 47, 56)));
	}

	@Test
	public void method_microsecond()
	{
		checkOutput("123456", T("<?print @(2010-05-12T16:47:56.123456).microsecond()?>"));
		checkOutput("123456", T("<?code m = @(2010-05-12T16:47:56.123456).microsecond?><?print m()?>"));
		checkOutput("123000", T("<?print d.microsecond()?>"), V("d", makeDate(2010, 5, 12, 16, 47, 56, 123456)));
		checkOutput("123456", T("<?print d.microsecond()?>"), V("d", LocalDateTime.of(2010, 5, 12, 16, 47, 56, 123456789)));
	}

	@Test
	public void method_date()
	{
		checkOutput("2000-02-29", T("<?print d.date()?>"), V("d", makeDate(2000, 2, 29)));
		checkOutput("2000-02-29", T("<?print d.date()?>"), V("d", LocalDate.of(2000, 2, 29)));
		checkOutput("2000-02-29", T("<?print d.date()?>"), V("d", LocalDateTime.of(2000, 2, 29, 12, 34, 56)));
	}

	private List makeAllDateTimeVariants(int y, int m, int d)
	{
		return asList(makeDate(y, m, d), LocalDate.of(y, m, d), LocalDateTime.of(y, m, d, 0, 0));
	}

	private List makeDateTimeVariants(int y, int m, int d)
	{
		return asList(makeDate(y, m, d), LocalDateTime.of(y, m, d, 0, 0));
	}

	private List makeDateTimeVariants(int y, int m, int d, int h, int mi, int s)
	{
		return asList(makeDate(y, m, d, h, mi, s), LocalDateTime.of(y, m, d, h, mi, s));
	}

	private List makeDateTimeVariants(int y, int m, int d, int h, int mi, int s, int ms)
	{
		return asList(makeDate(y, m, d, h, mi, s, ms), LocalDateTime.of(y, m, d, h, mi, s, ms*1000));
	}

	@Test
	public void method_calendar()
	{
		Template t = T("<?print repr(d.calendar())?>");
		Template t_0_7 = T("<?print repr(d.calendar(0, 7))?>");
		Template t_6_1 = T("<?print repr(d.calendar(6, 1))?>");

		// 1996: Non-leap year, starting on Monday
		for (Object d : makeAllDateTimeVariants(1996, 1, 1))
			checkOutput("[1996, 1, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(1996, 1, 1))
			checkOutput("[1996, 1, 0]", t_6_1, V("d", d));
		for (Object d : makeAllDateTimeVariants(1996, 1, 1))
			checkOutput("[1996, 1, 0]", t_0_7, V("d", d));
		for (Object d : makeAllDateTimeVariants(1996, 1, 7))
			checkOutput("[1996, 1, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(1996, 1, 8))
			checkOutput("[1996, 2, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(1996, 12, 29))
			checkOutput("[1996, 52, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(1996, 12, 30))
			checkOutput("[1997, 1, 0]", t, V("d", d));

		// 2018: Leap year, starting on Monday
		for (Object d : makeAllDateTimeVariants(2018, 1, 1))
			checkOutput("[2018, 1, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2018, 1, 1))
			checkOutput("[2018, 1, 0]", t_6_1, V("d", d));
		for (Object d : makeAllDateTimeVariants(2018, 1, 1))
			checkOutput("[2018, 1, 0]", t_0_7, V("d", d));
		for (Object d : makeAllDateTimeVariants(2018, 1, 7))
			checkOutput("[2018, 1, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2018, 1, 8))
			checkOutput("[2018, 2, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2018, 5,28))
			checkOutput("[2018, 22, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2018, 12, 30))
			checkOutput("[2018, 52, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2018, 12, 31))
			checkOutput("[2019, 1, 0]", t, V("d", d));

		// 2013: Non-leap year, starting on Tuesday
		for (Object d : makeAllDateTimeVariants(2013, 1, 1))
			checkOutput("[2013, 1, 1]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2013, 1, 1))
			checkOutput("[2013, 1, 1]", t_6_1, V("d", d));
		for (Object d : makeAllDateTimeVariants(2013, 1, 1))
			checkOutput("[2012, 53, 1]", t_0_7, V("d", d));
		for (Object d : makeAllDateTimeVariants(2013, 1, 6))
			checkOutput("[2013, 1, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2013, 1, 7))
			checkOutput("[2013, 2, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2013, 12, 29))
			checkOutput("[2013, 52, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2013, 12, 30))
			checkOutput("[2014, 1, 0]", t, V("d", d));

		// 2008: Leap year, starting on Tuesday
		for (Object d : makeAllDateTimeVariants(2008, 1, 1))
			checkOutput("[2008, 1, 1]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2008, 1, 1))
			checkOutput("[2008, 1, 1]", t_6_1, V("d", d));
		for (Object d : makeAllDateTimeVariants(2008, 1, 1))
			checkOutput("[2007, 53, 1]", t_0_7, V("d", d));
		for (Object d : makeAllDateTimeVariants(2008, 1, 6))
			checkOutput("[2008, 1, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2008, 1, 7))
			checkOutput("[2008, 2, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2008, 12, 28))
			checkOutput("[2008, 52, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2008, 12, 29))
			checkOutput("[2009, 1, 0]", t, V("d", d));

		// 2014: Non-leap year, starting on Wednesday
		for (Object d : makeAllDateTimeVariants(2014, 1, 1))
			checkOutput("[2014, 1, 2]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2014, 1, 1))
			checkOutput("[2014, 1, 2]", t_6_1, V("d", d));
		for (Object d : makeAllDateTimeVariants(2014, 1, 1))
			checkOutput("[2013, 52, 2]", t_0_7, V("d", d));
		for (Object d : makeAllDateTimeVariants(2014, 1, 5))
			checkOutput("[2014, 1, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2014, 1, 6))
			checkOutput("[2014, 2, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2014, 12, 28))
			checkOutput("[2014, 52, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2014, 12, 29))
			checkOutput("[2015, 1, 0]", t, V("d", d));

		// 1992: Leap year, starting on Wednesday
		for (Object d : makeAllDateTimeVariants(1992, 1, 1))
			checkOutput("[1992, 1, 2]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(1992, 1, 1))
			checkOutput("[1992, 1, 2]", t_6_1, V("d", d));
		for (Object d : makeAllDateTimeVariants(1992, 1, 1))
			checkOutput("[1991, 52, 2]", t_0_7, V("d", d));
		for (Object d : makeAllDateTimeVariants(1992, 1, 5))
			checkOutput("[1992, 1, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(1992, 1, 6))
			checkOutput("[1992, 2, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(1992, 12, 27))
			checkOutput("[1992, 52, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(1992, 12, 28))
			checkOutput("[1992, 53, 0]", t, V("d", d));

		// 2015: Non-leap year, starting on Thursday
		for (Object d : makeAllDateTimeVariants(2015, 1, 1))
			checkOutput("[2015, 1, 3]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2015, 1, 1))
			checkOutput("[2015, 1, 3]", t_6_1, V("d", d));
		for (Object d : makeAllDateTimeVariants(2015, 1, 1))
			checkOutput("[2014, 52, 3]", t_0_7, V("d", d));
		for (Object d : makeAllDateTimeVariants(2015, 1, 4))
			checkOutput("[2015, 1, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2015, 01, 05))
			checkOutput("[2015, 2, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2015, 12, 27))
			checkOutput("[2015, 52, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2015, 12, 28))
			checkOutput("[2015, 53, 0]", t, V("d", d));

		// 2004: Leap year, starting on Thursday
		for (Object d : makeAllDateTimeVariants(2004, 1, 1))
			checkOutput("[2004, 1, 3]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2004, 1, 1))
			checkOutput("[2004, 1, 3]", t_6_1, V("d", d));
		for (Object d : makeAllDateTimeVariants(2004, 1, 1))
			checkOutput("[2003, 52, 3]", t_0_7, V("d", d));
		for (Object d : makeAllDateTimeVariants(2004, 1, 4))
			checkOutput("[2004, 1, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2004, 1, 5))
			checkOutput("[2004, 2, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2004, 12, 26))
			checkOutput("[2004, 52, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2004, 12, 27))
			checkOutput("[2004, 53, 0]", t, V("d", d));

		// 2010: Non-leap year, starting on Friday
		for (Object d : makeAllDateTimeVariants(2010, 1, 1))
			checkOutput("[2009, 53, 4]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2010, 1, 1))
			checkOutput("[2010, 1, 4]", t_6_1, V("d", d));
		for (Object d : makeAllDateTimeVariants(2010, 1, 1))
			checkOutput("[2009, 52, 4]", t_0_7, V("d", d));
		for (Object d : makeAllDateTimeVariants(2010, 1, 3))
			checkOutput("[2009, 53, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2010, 1, 4))
			checkOutput("[2010, 1, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2010, 12, 26))
			checkOutput("[2010, 51, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2010, 12, 27))
			checkOutput("[2010, 52, 0]", t, V("d", d));

		// 2016: Leap year, starting on Friday
		for (Object d : makeAllDateTimeVariants(2016, 1, 1))
			checkOutput("[2015, 53, 4]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2016, 1, 1))
			checkOutput("[2016, 1, 4]", t_6_1, V("d", d));
		for (Object d : makeAllDateTimeVariants(2016, 1, 1))
			checkOutput("[2015, 52, 4]", t_0_7, V("d", d));
		for (Object d : makeAllDateTimeVariants(2016, 1, 3))
			checkOutput("[2015, 53, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2016, 1, 4))
			checkOutput("[2016, 1, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2016, 12, 25))
			checkOutput("[2016, 51, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2016, 12, 26))
			checkOutput("[2016, 52, 0]", t, V("d", d));

		// 2011: Non-leap year, starting on Saturday
		for (Object d : makeAllDateTimeVariants(2011, 1, 1))
			checkOutput("[2010, 52, 5]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2011, 1, 1))
			checkOutput("[2011, 1, 5]", t_6_1, V("d", d));
		for (Object d : makeAllDateTimeVariants(2011, 1, 1))
			checkOutput("[2010, 52, 5]", t_0_7, V("d", d));
		for (Object d : makeAllDateTimeVariants(2011, 1, 2))
			checkOutput("[2010, 52, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2011, 1, 3))
			checkOutput("[2011, 1, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2011, 12, 25))
			checkOutput("[2011, 51, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2011, 12, 26))
			checkOutput("[2011, 52, 0]", t, V("d", d));

		// 2000: Leap year, starting on Saturday
		for (Object d : makeAllDateTimeVariants(2000, 1, 1))
			checkOutput("[1999, 52, 5]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2000, 1, 1))
			checkOutput("[2000, 1, 5]", t_6_1, V("d", d));
		for (Object d : makeAllDateTimeVariants(2000, 1, 1))
			checkOutput("[1999, 52, 5]", t_0_7, V("d", d));
		for (Object d : makeAllDateTimeVariants(2000, 1, 2))
			checkOutput("[1999, 52, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2000, 1, 3))
			checkOutput("[2000, 1, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2000, 12, 24))
			checkOutput("[2000, 51, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2000, 12, 25))
			checkOutput("[2000, 52, 0]", t, V("d", d));

		// 2017: Non-leap year, starting on Sunday
		for (Object d : makeAllDateTimeVariants(2017, 1, 1))
			checkOutput("[2016, 52, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2017, 1, 1))
			checkOutput("[2017, 1, 6]", t_6_1, V("d", d));
		for (Object d : makeAllDateTimeVariants(2017, 1, 1))
			checkOutput("[2016, 52, 6]", t_0_7, V("d", d));
		for (Object d : makeAllDateTimeVariants(2017, 1, 2))
			checkOutput("[2017, 1, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2017, 12, 24))
			checkOutput("[2017, 51, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2017, 12, 25))
			checkOutput("[2017, 52, 0]", t, V("d", d));

		// 2012: Leap year, starting on Sunday
		for (Object d : makeAllDateTimeVariants(2012, 1, 1))
			checkOutput("[2011, 52, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2012, 1, 1))
			checkOutput("[2012, 1, 6]", t_6_1, V("d", d));
		for (Object d : makeAllDateTimeVariants(2012, 1, 1))
			checkOutput("[2011, 52, 6]", t_0_7, V("d", d));
		for (Object d : makeAllDateTimeVariants(2012, 1, 2))
			checkOutput("[2012, 1, 0]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2012, 12, 30))
			checkOutput("[2012, 52, 6]", t, V("d", d));
		for (Object d : makeAllDateTimeVariants(2012, 12, 31))
			checkOutput("[2013, 1, 0]", t, V("d", d));

		// Make sure that the parameters have the same name in all implementations
		t = T("<?print repr(d.calendar(firstweekday=0, mindaysinfirstweek=4))?>");
		for (Object d : makeAllDateTimeVariants(2018, 1, 1))
			checkOutput("[2018, 1, 0]", t, V("d", d));
	}

	@Test
	public void method_weekday()
	{
		checkOutput("2", T("<?print @(2010-05-12).weekday()?>"));
		checkOutput("2", T("<?code m = @(2010-05-12).weekday?><?print m()?>"));

		Template t = T("<?print d.weekday()?>");
		for (Object d : makeAllDateTimeVariants(2010, 5, 21))
			checkOutput("4", t, V("d", d));
	}

	@Test
	public void method_yearday()
	{
		checkOutput("1", T("<?print @(2010-01-01).yearday()?>"));
		checkOutput("1", T("<?code m = @(2010-01-01).yearday?><?print m()?>"));
		checkOutput("366", T("<?print @(2008-12-31).yearday()?>"));
		checkOutput("365", T("<?print @(2010-12-31).yearday()?>"));
		checkOutput("132", T("<?print @(2010-05-12).yearday()?>"));
		checkOutput("132", T("<?print @(2010-05-12T16:47:56).yearday()?>"));
		Template t = T("<?print d.yearday()?>");
		for (Object d : makeAllDateTimeVariants(2010, 5, 12))
			checkOutput("132", t, V("d", d));
	}

	@Test
	public void method_append()
	{
		checkOutput("[17, 23, 42]", T("<?code l = [17]?><?code l.append(23, 42)?><?print l?>"));
		checkOutput("[17, 23, 42]", T("<?code l = [17]?><?code m = l.append?><?code m(23, 42)?><?print l?>"));
	}

	@Test
	public void method_insert()
	{
		checkOutput("[1, 2, 3, 4]", T("<?code l = [1,4]?><?code l.insert(1, 2, 3)?><?print l?>"));
		checkOutput("[1, 2, 3, 4]", T("<?code l = [1,4]?><?code m = l.insert?><?code m(1, 2, 3)?><?print l?>"));
	}

	@Test
	public void method_pop()
	{
		checkOutput("42;17;23;", T("<?code l = [17, 23, 42]?><?print l.pop()?>;<?print l.pop(-2)?>;<?print l.pop(0)?>;"));
		checkOutput("42;17;23;", T("<?code l = [17, 23, 42]?><?code m = l.pop?><?print m()?>;<?print m(-2)?>;<?print m(0)?>;"));
		checkOutput("23;73;{}", T("<?code d = {17: 23, 42: 73}?><?print d.pop(17)?>;<?print d.pop(42)?>;<?print d?>"));
		checkOutput("23;42;{42: 73}", T("<?code d = {17: 23, 42: 73}?><?print d.pop(17)?>;<?print d.pop(43, 42)?>;<?print d?>"));
	}

	@Test
	public void method_update()
	{
		checkOutput("0", T("<?code d = {}?><?code d.update()?><?print len(d)?>"));
		checkOutput("1", T("<?code d = {}?><?code d.update([['one', 1]])?><?print d.one?>"));
		checkOutput("1", T("<?code d = {}?><?code d.update({'one': 1})?><?print d.one?>"));
		checkOutput("1", T("<?code d = {}?><?code d.update(one=1)?><?print d.one?>"));
		checkOutput("1", T("<?code d = {}?><?code m = d.update?><?code m(one=1)?><?print d.one?>"));
		checkOutput("1", T("<?code d = {}?><?code d.update([['one', 0]], {'one': 0}, one=1)?><?print d.one?>"));
	}

	@Test
	public void set_lvalue()
	{
		checkOutput("bar", T("<?code d = {}?><?code d.foo = 'bar'?><?print d.foo?>"));
		checkOutput("bar", T("<?code d = {}?><?code d['foo'] = 'bar'?><?print d['foo']?>"));
		checkOutput("bar", T("<?code d = ['bar']?><?code d[0] = 'bar'?><?print d[0]?>"));
		checkOutput("baz", T("<?code d = {'foo': {}}?><?code d.foo.bar = 'baz'?><?print d.foo.bar?>"));
		checkOutput("baz", T("<?code d = {'foo': {}}?><?code d.foo['bar'] = 'baz'?><?print d.foo['bar']?>"));
		checkOutput("baz", T("<?code d = {'foo': ['bar']}?><?code d.foo[0] = 'baz'?><?print d.foo[0]?>"));
		checkOutput("baz", T("<?code d = ['bar']?><?def f?><?return d?><?end def?><?code f()[0] = 'baz'?><?print d[0]?>"));
	}

	@Test
	public void add_lvalue()
	{
		checkOutput("barbaz", T("<?code d = {'foo': 'bar'}?><?code d.foo += 'baz'?><?print d.foo?>"));
		checkOutput("barbaz", T("<?code d = {'foo': 'bar'}?><?code d['foo'] += 'baz'?><?print d['foo']?>"));
		checkOutput("barbaz", T("<?code d = ['bar']?><?code d[0] += 'baz'?><?print d[0]?>"));
		checkOutput("barbaz", T("<?code d = {'foo': {'bar' : 'bar'}}?><?code d.foo.bar += 'baz'?><?print d.foo.bar?>"));
		checkOutput("barbaz", T("<?code d = {'foo': {'bar' : 'bar'}}?><?code d.foo['bar'] += 'baz'?><?print d.foo['bar']?>"));
		checkOutput("barbaz", T("<?code d = {'foo': ['bar']}?><?code d.foo[0] += 'baz'?><?print d.foo[0]?>"));
		checkOutput("barbaz", T("<?code d = ['bar']?><?def f?><?return d?><?end def?><?code f()[0] += 'baz'?><?print d[0]?>"));
		checkOutput("[1, 2, 3, 4][1, 2, 3, 4]", T("<?code d = {'foo': [1, 2]}?><?code l = d.foo?><?code d.foo += [3, 4]?><?print d.foo?><?print l?>"));
	}

	@Test
	public void sub_lvalue()
	{
		checkOutput("6", T("<?code d = {'foo': 23}?><?code d.foo -= 17?><?print d.foo?>"));
		checkOutput("6", T("<?code d = {'foo': 23}?><?code d['foo'] -= 17?><?print d['foo']?>"));
		checkOutput("6", T("<?code d = [23]?><?code d[0] -= 17?><?print d[0]?>"));
		checkOutput("6", T("<?code d = {'foo': {'bar' : 23}}?><?code d.foo.bar -= 17?><?print d.foo.bar?>"));
		checkOutput("6", T("<?code d = {'foo': {'bar' : 23}}?><?code d.foo['bar'] -= 17?><?print d.foo['bar']?>"));
		checkOutput("6", T("<?code d = {'foo': [23]}?><?code d.foo[0] -= 17?><?print d.foo[0]?>"));
		checkOutput("6", T("<?code d = [23]?><?def f?><?return d?><?end def?><?code f()[0] -= 17?><?print d[0]?>"));
	}

	@Test
	public void mul_lvalue()
	{
		checkOutput("42", T("<?code d = {'foo': 6}?><?code d.foo *= 7?><?print d.foo?>"));
		checkOutput("42", T("<?code d = {'foo': 6}?><?code d['foo'] *= 7?><?print d['foo']?>"));
		checkOutput("42", T("<?code d = [6]?><?code d[0] *= 7?><?print d[0]?>"));
		checkOutput("42", T("<?code d = {'foo': {'bar' : 6}}?><?code d.foo.bar *= 7?><?print d.foo.bar?>"));
		checkOutput("42", T("<?code d = {'foo': {'bar' : 6}}?><?code d.foo['bar'] *= 7?><?print d.foo['bar']?>"));
		checkOutput("42", T("<?code d = {'foo': [6]}?><?code d.foo[0] *= 7?><?print d.foo[0]?>"));
		checkOutput("42", T("<?code d = [6]?><?def f?><?return d?><?end def?><?code f()[0] *= 7?><?print d[0]?>"));
	}

	@Test
	public void floordiv_lvalue()
	{
		checkOutput("2", T("<?code d = {'foo': 5}?><?code d.foo //= 2?><?print d.foo?>"));
		checkOutput("2", T("<?code d = {'foo': 5}?><?code d['foo'] //= 2?><?print d['foo']?>"));
		checkOutput("2", T("<?code d = [5]?><?code d[0] //= 2?><?print d[0]?>"));
		checkOutput("2", T("<?code d = {'foo': {'bar' : 5}}?><?code d.foo.bar //= 2?><?print d.foo.bar?>"));
		checkOutput("2", T("<?code d = {'foo': {'bar' : 5}}?><?code d.foo['bar'] //= 2?><?print d.foo['bar']?>"));
		checkOutput("2", T("<?code d = {'foo': [5]}?><?code d.foo[0] //= 2?><?print d.foo[0]?>"));
		checkOutput("2", T("<?code d = [5]?><?def f?><?return d?><?end def?><?code f()[0] //= 2?><?print d[0]?>"));
	}

	@Test
	public void truediv_lvalue()
	{
		checkOutput("2.5", T("<?code d = {'foo': 5}?><?code d.foo /= 2?><?print d.foo?>"));
		checkOutput("2.5", T("<?code d = {'foo': 5}?><?code d['foo'] /= 2?><?print d['foo']?>"));
		checkOutput("2.5", T("<?code d = [5]?><?code d[0] /= 2?><?print d[0]?>"));
		checkOutput("2.5", T("<?code d = {'foo': {'bar' : 5}}?><?code d.foo.bar /= 2?><?print d.foo.bar?>"));
		checkOutput("2.5", T("<?code d = {'foo': {'bar' : 5}}?><?code d.foo['bar'] /= 2?><?print d.foo['bar']?>"));
		checkOutput("2.5", T("<?code d = {'foo': [5]}?><?code d.foo[0] /= 2?><?print d.foo[0]?>"));
		checkOutput("2.5", T("<?code d = [5]?><?def f?><?return d?><?end def?><?code f()[0] /= 2?><?print d[0]?>"));
	}

	@Test
	public void mod_lvalue()
	{
		checkOutput("1", T("<?code d = {'foo': 5}?><?code d.foo %= 2?><?print d.foo?>"));
		checkOutput("1", T("<?code d = {'foo': 5}?><?code d['foo'] %= 2?><?print d['foo']?>"));
		checkOutput("1", T("<?code d = [5]?><?code d[0] %= 2?><?print d[0]?>"));
		checkOutput("1", T("<?code d = {'foo': {'bar' : 5}}?><?code d.foo.bar %= 2?><?print d.foo.bar?>"));
		checkOutput("1", T("<?code d = {'foo': {'bar' : 5}}?><?code d.foo['bar'] %= 2?><?print d.foo['bar']?>"));
		checkOutput("1", T("<?code d = {'foo': [5]}?><?code d.foo[0] %= 2?><?print d.foo[0]?>"));
		checkOutput("1", T("<?code d = [5]?><?def f?><?return d?><?end def?><?code f()[0] %= 2?><?print d[0]?>"));
	}

	@Test
	public void lvalue_with_context()
	{
		checkOutput("84", T("<?print double.x?>"), V("x", 42, "double", new DoubleIt()));
	}

	@Test
	public void parse()
	{
		checkOutput("42", T("<?print data.Noner?>"), V("data", V("Noner", 42)));
	}

	@CauseTest(expectedCause=SyntaxException.class)
	public void lexer_error()
	{
		checkOutput("", T("<?print ??>"));
	}

	@CauseTest(expectedCause=SyntaxException.class)
	public void parser_error()
	{
		checkOutput("", T("<?print 1++2?>"));
	}

	@Test
	public void tag_note()
	{
		checkOutput("foo", T("f<?note This is?>o<?note a comment?>o"));
	}

	@Test
	public void tag_doc()
	{
		Template t = T("<?doc foo?><?def inner?><?doc innerfoo?><?doc innerbar?><?end def?><?doc bar?><?printx inner.doc?>", "t");

		assertEquals("foo", t.getDoc());

		checkOutput("innerfoo", t);
	}

	@Test
	public void exception()
	{
		Template t = T("foo<?print 2*x?>bar", "t");

		checkOutput("broken", T("<?print exc?>"), V("exc", new RuntimeException("broken")));
		checkOutput("<java.lang.RuntimeException>", T("<?print repr(exc)?>"), V("exc", new RuntimeException("broken")));
		checkOutput("broken", T("<?print exc?>"), V("exc", new RuntimeException("broken")));
		checkOutput("None", T("<?print repr(exc.context)?>"), V("exc", new RuntimeException("broken")));
		checkOutput("because", T("<?print exc.context?>"), V("exc", new RuntimeException("broken", new RuntimeException("because"))));
		checkOutput("None", T("<?print repr(exc.context.context)?>"), V("exc", new RuntimeException("broken", new RuntimeException("because"))));

		try
		{
			String s = t.renders();
		}
		catch (Exception exc)
		{
			checkOutput("<com.livinglogic.ul4.MulAST pos=(11:14) line=1 column=12>", T("<?print repr(exc.context.location)?>"), V("exc", exc));
			checkOutput("1", T("<?print exc.context.location.startline?>"), V("exc", exc));
			checkOutput("12", T("<?print exc.context.location.startcol?>"), V("exc", exc));
			checkOutput("foo<?print ", T("<?print exc.context.location.startsourceprefix?>"), V("exc", exc));
			checkOutput("?>bar", T("<?print exc.context.location.startsourcesuffix?>"), V("exc", exc));
		}

		try
		{
			T("?<?if x?>?", "t");
		}
		catch (Exception exc)
		{
			checkOutput("<com.livinglogic.ul4.ConditionalBlocksAST pos=(1:9) line=1 column=2>", T("<?print repr(exc.context.location)?>"), V("exc", exc));
		}
	}

	@Test
	public void templateattributes_1()
	{
		String source = "<?print x?>";
		Template t = T(source, "t");

		checkOutput("<com.livinglogic.ul4.UndefinedAttribute 'foo' of <com.livinglogic.ul4.Template name='t' whitespace='strip'>>", T("<?print repr(template.foo)?>"), V("template", t));
		checkOutput(source, T("<?print template.source?>"), V("template", t));
		checkOutput("2", T("<?print len(template.content)?>"), V("template", t));
		checkOutput("t", T("<?print template.content[0].template.name?>"), V("template", t));
		// Test the second item, because the first one is an empty indent node
		checkOutput("print", T("<?print template.content[1].type?>"), V("template", t));
		checkOutput(source, T("<?print template.content[1].source?>"), V("template", t));
		checkOutput("x", T("<?print template.content[1].obj.source?>"), V("template", t));
		checkOutput("var", T("<?print template.content[1].obj.type?>"), V("template", t));
		checkOutput("x", T("<?print template.content[1].obj.name?>"), V("template", t));
	}

	@Test
	public void templateattributes_2()
	{
		Template t = T("<?printx 42?>");

		checkOutput("printx", T("<?print template.content[1].type?>"), V("template", t));
		checkOutput("const", T("<?print template.content[1].obj.type?>"), V("template", t));
		checkOutput("42", T("<?print template.content[1].obj.value?>"), V("template", t));
	}

	@Test
	public void templateattributes_3()
	{
		Template t = T("foo");

		checkOutput("text", T("<?print template.content[1].type?>"), V("template", t));
		checkOutput("foo", T("<?print template.content[1].text?>"), V("template", t));
	}

	@Test
	public void templateattributes_4()
	{
		Template t = T("<?doc foo?>");

		checkOutput("foo", T("<?print template.doc?>"), V("template", t));
	}

	@Test
	public void templateattributes_localtemplate()
	{
		String source = "<?def lower?><?print t.lower()?><?end def?>";

		checkOutput(source, T(source + "<?print lower.source?>"));
		checkOutput("<?def lower?>", T(source + "<?print lower.parenttemplate.source[lower.startpos]?>"));
		checkOutput("<?print t.lower()?>", T(source + "<?print lower.parenttemplate.source[lower.startpos.stop:lower.stoppos.start]?>"));
		checkOutput("lower", T(source + "<?print lower.name?>"));
		checkOutput("None", T(source + "<?print repr(lower.parenttemplate.name)?>"));
	}

	@Test
	public void nestedscopes()
	{
		checkOutput("0;1;2;", T("<?for i in range(3)?><?def x?><?print repr(i)?>;<?end def?><?render x()?><?end for?>"));
		checkOutput("2;2;2;", T("<?code fs = []?><?for i in range(3)?><?def x?><?print repr(i)?>;<?end def?><?code fs.append(x)?><?end for?><?for f in fs?><?render f()?><?end for?>"));
		checkOutput("2;", T("<?for i in range(3)?><?if i == 1?><?def x?><?print i?>;<?end def?><?end if?><?end for?><?render x()?>"));
		checkOutput("2", T("<?code i = 1?><?def x?><?print i?><?end def?><?code i = 2?><?render x()?>"));
		checkOutput("2", T("<?code i = 1?><?def x?><?def y?><?print i?><?end def?><?code i = 2?><?render y()?><?end def?><?code i = 3?><?render x()?>"));
	}

	@Test
	public void pass_functions()
	{
		checkOutput("&lt;", T("<?def x?><?print x('<')?><?end def?><?render x(x=xmlescape)?>"));
	}

	@Test
	public void function()
	{
		checkResult(42, T("<?return 42?>"));
	}

	@Test
	public void function_value()
	{
		checkResult(84, T("<?return 2*x?>"), V("x", 42));
	}

	@Test
	public void function_multiple_returnvalues()
	{
		checkResult(84, T("<?return 2*x?><?return 3*x?>"), V("x", 42));
	}

	@Test
	public void function_name()
	{
		checkResult("f", T("<?def f?><?return f.name?><?end def?><?return f(f=f)?>"));
	}

	@Test
	public void function_closure()
	{
		checkResult(24, T("<?code y=3?><?def inner?><?return 2*x*y?><?end def?><?return inner(x=4)?>"));
		checkResult(24, T("<?def outer()?><?code y=3?><?def inner(x)?><?return 2*x*y?><?end def?><?return inner?><?end def?><?return outer()(x=4)?>"));
	}

	@Test
	public void template_closure()
	{
		checkOutput("24", T("<?code f = []?><?def outer()?><?code y=3?><?def inner(x)?><?print 2*x*y?><?end def?><?code f.append(inner)?><?end def?><?code outer()?><?render f[0](x=4)?>"));
	}

	@Test
	public void template_closure_toplevel()
	{
		Template tOther = T("<?ul4 other(loc, x)?><?print loc()?>");

		Template tFinal = T("<?ul4 final(x)?><?return x == 42?>");

		Template tInitial = T("<?ul4 initial?><?def local()?><?if final(x)?><?return 'OK'?><?else?><?return 'FAIL'?><?end if?><?end def?><?render other(local, x)?>");

		checkOutput("OK", tInitial, V("x", 42, "other", tOther, "final", tFinal));
	}

	@Test
	public void template_closure_toplevel2()
	{
		Template tClosure = T("<?ul4 makeclosure(x)?><?def inner()?><?print x?><?end def?><?return {'inner': inner}?>");

		Template tMain = T("<?code data = makeclosure(42)?><?render data.inner()?>");

		checkOutput("42", tMain, V("makeclosure", tClosure));
	}

	@Test
	public void return_in_template()
	{
		checkOutput("gurk", T("gurk<?return 42?>hurz"));
	}

	@CauseTest(expectedCause=StackOverflowError.class)
	public void endless_recursion()
	{
		checkOutput("", T("<?def f(container)?><?for child in container?><?code f(container)?><?end for?><?end def?><?code x = []?><?code x.append(x)?><?code f(x)?>"));
	}

	@CauseTest(expectedCause=RuntimeExceededException.class)
	public void runtime_limit()
	{
		checkOutput("", T("<?while True?><?end while?>"), 10);
	}

	@Test
	public void stripWhitespace()
	{
		Template t1 = T("<?if True?> foo<?end if?>", Template.Whitespace.strip);
		assertEquals(t1.renders(), " foo");

		Template t2 = T("<?if True?> foo\n bar<?end if?>", Template.Whitespace.strip);
		assertEquals(t2.renders(), " foobar");

		Template t3 = T("<?if True?>\n foo\n bar<?end if?>", Template.Whitespace.strip);
		assertEquals(t3.renders(), "foobar");
	}

	@Test
	public void render_reindents() throws Exception
	{
		Template t = T("<?print 42?>\n<?print 43?>", "t", Template.Whitespace.keep);

		checkOutput("\t42\n\t43", T("\t<?render t()?>"), V("t", t));
	}

	@Test
	public void smart_whitespace() throws Exception
	{
		// Without linefeeds the text will be output as-is.
		checkOutput("\tTrue", T("<?if True?>\tTrue<?end if?>", Template.Whitespace.smart));

		// Line feeds will be removed from lines containing only a "control flow" tag.
		checkOutput("True\n", T("<?if True?>\nTrue\n<?end if?>\n", Template.Whitespace.smart));

		// Indentation will also be removed from those lines.
		checkOutput("True\n", T("    <?if True?>\nTrue\n         <?end if?>\n", Template.Whitespace.smart));

		// Additional text (before and after tag) will leave the line feeds intact.
		checkOutput("x\nTrue\n", T("x<?if True?>\nTrue\n<?end if?>\n", Template.Whitespace.smart));
		checkOutput(" \nTrue\n", T("<?if True?> \nTrue\n<?end if?>\n", Template.Whitespace.smart));

		// Multiple tags will also leave the line feeds intact.
		checkOutput("\nTrue\n\n", T("<?if True?><?if True?>\nTrue\n<?end if?><?end if?>\n", Template.Whitespace.smart));

		// For <?print?> and <?printx?> tags the indentation and line feed will not be stripped
		checkOutput(" 42\n", T(" <?print 42?>\n", Template.Whitespace.smart));
		checkOutput(" 42\n", T(" <?printx 42?>\n", Template.Whitespace.smart));

		// For <?render?> tags the line feed will be stripped, but the indentation will be reused for each line rendered by the call
		checkOutput("   x\r\n", T("<?def x?>\nx\r\n<?end def?>\n   <?render x()?>\n", Template.Whitespace.smart));

		// But of course "common" indentation will be ignored
		checkOutput("x\r\n", T("<?if True?>\n   <?def x?>\n   x\r\n   <?end def?>\n   <?render x()?>\n<?end if?>\n", Template.Whitespace.smart));

		// But not on the outermost level, which leads to an esoteric corner case:
		// The indentation will be output twice (once by the text itself, and once by the render call).
		checkOutput("      x\r\n", T("   <?def x?>\n   x\r\n   <?end def?>\n   <?render x()?>\n", Template.Whitespace.smart));

		// Additional indentation in the block will be removed.
		checkOutput("True\n", T("<?if True?>\n\tTrue\n<?end if?>\n", Template.Whitespace.smart));

		// Outer indentation will be kept.
		checkOutput(" True\n", T(" <?if True?>\n \tTrue\n <?end if?>\n", Template.Whitespace.smart));

		// Mixed indentation will not be recognized as indentation.
		checkOutput("\tTrue\n", T(" <?if True?>\n\tTrue\n <?end if?>\n", Template.Whitespace.smart));
	}

	@Test
	public void smart_whitespace_nested() throws Exception
	{
		Template t = T("<?whitespace smart?>\n<x>\n\t<?for i in range(2)?>\n\t\t<y>\n\t\t\t<z><?printx i?></z>\n\t\t</y>\n\t<?end for?>\n</x>");
		checkOutput("<x>\n\t<y>\n\t\t<z>0</z>\n\t</y>\n\t<y>\n\t\t<z>1</z>\n\t</y>\n</x>", t);
	}

	public static final class MakeVar extends Function
	{
		protected int value;

		public MakeVar()
		{
			value = 0;
		}

		@Override
		public String getNameUL4()
		{
			return "makevar";
		}

		private static final Signature signature = new Signature().addPositionalOnly("var");

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		@Override
		public Object evaluate(EvaluationContext context, BoundArguments args)
		{
			return call((Integer)args.get(0));
		}

		public int call(int value)
		{
			int result = this.value + value;
			this.value = value;
			return result;
		}
	}

	@Test
	public void keywordEvaluationOrder()
	{
		// Test that expressions for keyword arguments are evaluated in the order they are given
		Template t1 = T("<?def t?><?print x?>;<?print y?><?end def?><?render t(x=makevar(1), y=makevar(2))?>");
		String output1 = t1.renders(V("makevar", new MakeVar()));
		assertEquals("1;3", output1);

		Template t2 = T("<?def t?><?print x?>;<?print y?><?end def?><?render t(x=makevar(2), y=makevar(1))?>");
		String output2 = t2.renders(V("makevar", new MakeVar()));
		assertEquals("2;3", output2);
	}

	@Test
	public void notContainmentPrecedence()
	{
		// Check that {@code not x in y} is parsed as {@code not (x in y)}
		checkOutput("True", T("<?print not 'x' in 'gurk'?>"));
	}

	private Template universaltemplate()
	{
		return T(
			"text" +
			"<?xml version='1.0' encoding='utf-8'?>" + // will not be recognized as a tag
			"<?code x = 'gurk'?>" +
			"<?code x = 42?>" +
			"<?code x = 4.2?>" +
			"<?code x = None?>" +
			"<?code x = False?>" +
			"<?code x = True?>" +
			"<?code x = @(2009-01-04)?>" +
			"<?code x = @(2009-01-04T)?>" +
			"<?code x = @(2009-01-04T12:34)?>" +
			"<?code x = @(2009-01-04T12:34:56)?>" +
			"<?code x = @(2009-01-04T12:34:56.987654)?>" +
			"<?code x = #0063a8?>" +
			"<?code x = [42]?>" +
			"<?code x = {'fortytwo': 42}?>" +
			"<?code x = y?>" +
			"<?code x += 42?>" +
			"<?code x -= 42?>" +
			"<?code x *= 42?>" +
			"<?code x /= 42?>" +
			"<?code x //= 42?>" +
			"<?code x %= 42?>" +
			"<?print x.gurk?>" +
			"<?print x['gurk']?>" +
			"<?print x[1:2]?>" +
			"<?print x[1:]?>" +
			"<?print x[:2]?>" +
			"<?print x[:]?>" +
			"<?printx x?>" +
			"<?for x in '12'?><?print x?><?break?><?continue?><?end for?>" +
			"<?print not x?>" +
			"<?print -x?>" +
			"<?print x in y?>" +
			"<?print x not in y?>" +
			"<?print x==y?>" +
			"<?print x!=y?>" +
			"<?print x<y?>" +
			"<?print x<=y?>" +
			"<?print x>y?>" +
			"<?print x>=y?>" +
			"<?print x+y?>" +
			"<?print x*y?>" +
			"<?print x/y?>" +
			"<?print x//y?>" +
			"<?print x and y?>" +
			"<?print x or y?>" +
			"<?print x % y?>" +
			"<?print now()?>" +
			"<?print repr(1)?>" +
			"<?print range(1, 2)?>" +
			"<?print range(1, 2, 3)?>" +
			"<?print rgb(1, 2, 3, 4)?>" +
			"<?print x.r()?>" +
			"<?print x.find(1)?>" +
			"<?print x.find(1, 2)?>" +
			"<?print x.find(1, 2, 3)?>" +
			"<?if x?>gurk<?elif y?>hurz<?else?>hinz<?end if?>" +
			"<?def x?>foo<?end def?>"
		);
	}

	@Test
	public void template_str()
	{
		universaltemplate().toString();

		checkOutput("(x=17, y=@(2000-02-29))", T("<?def f(x=17, y=@(2000-02-29))?><?return x+y?><?end def?><?print str(f.signature)?>"));
	}

	@Test
	public void reader() throws IOException
	{
		String li = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
		Template t = T("<?for i in range(100)?>" + li + "<?end for?>", "reader");

		Reader reader = t.reader(null);

		int c;
		StringBuilder buffer = new StringBuilder();
		while ((c = reader.read()) != -1)
		{
			buffer.append((char)c);
		}

		StringBuilder expected = new StringBuilder();
		for (int i = 0; i < 100; i++)
			expected.append(li);

		assertEquals(buffer.toString(), expected.toString());
	}

	@Test
	public void db_query() throws Exception
	{
		Template t = T(
			"<?code db.execute('create table ul4test(ul4_int integer, ul4_char varchar2(1000), ul4_clob clob)')?>\n" +
			"<?code db.execute('insert into ul4test values(1, ', 'first', ', ', 10000*'first', ')')?>\n" +
			"<?code db.execute('insert into ul4test values(2, ', 'second', ', ', 10000*'second', ')')?>\n" +
			"<?code db.execute('insert into ul4test values(3, ', 'third', ', ', 10000*'third', ')')?>\n" +
			"<?code vin = db.int(2)?>\n" +
			"<?for row in db.query('select * from ul4test where ul4_int <= ', vin, ' order by ul4_int')?>\n" +
				"<?print row.ul4_int?>|\n" +
				"<?print row.ul4_char?>|\n" +
				"<?print row.ul4_clob?>|\n" +
			"<?end for?>\n" +
			"<?code db.execute('drop table ul4test')?>\n"
		);

		Connection db = getDatabaseConnection();

		if (db != null)
		{
			checkOutput(
				"1|first|" + StringUtils.repeat("first", 10000) + "|2|second|" + StringUtils.repeat("second", 10000) + "|",
				t,
				V("db", db)
			);
		}
	}

	@Test
	public void db_queryone_record() throws Exception
	{
		Template t = T(
			"<?code db.execute('create table ul4test(ul4_int integer, ul4_char varchar2(1000), ul4_clob clob)')?>\n" +
			"<?code db.execute('insert into ul4test values(1, ', 'first', ', ', 10000*'first', ')')?>\n" +
			"<?code vin = db.int(2)?>\n" +
			"<?code row = db.queryone('select * from ul4test where ul4_int <= ', vin, ' order by ul4_int')?>\n" +
			"<?print row.ul4_int?>|\n" +
			"<?print row.ul4_char?>|\n" +
			"<?print row.ul4_clob?>" +
			"<?code db.execute('drop table ul4test')?>\n"
		);

		Connection db = getDatabaseConnection();

		if (db != null)
		{
			checkOutput(
				"1|first|" + StringUtils.repeat("first", 10000),
				t,
				V("db", db)
			);
		}
	}

	@Test
	public void db_queryone_norecord() throws Exception
	{
		Template t = T(
			"<?code db.execute('create table ul4test(ul4_int integer, ul4_char varchar2(1000), ul4_clob clob)')?>\n" +
			"<?code vin = db.int(2)?>\n" +
			"<?code row = db.queryone('select * from ul4test where ul4_int <= ', vin, ' order by ul4_int')?>\n" +
			"<?print row is None?>" +
			"<?code db.execute('drop table ul4test')?>\n"
		);

		Connection db = getDatabaseConnection();

		if (db != null)
		{
			checkOutput("True", t, V("db", db));
		}
	}

	@Test
	public void db_execute_function() throws Exception
	{
		Template t = T(
			"<?code db.execute('create or replace function ul4test(p_arg integer) return integer as begin return 2*p_arg; end;')?>\n" + 
			"<?code vin = db.int(42)?>\n" + 
			"<?code vout = db.int()?>\n" + 
			"<?code db.execute('begin ', vout, ' := ul4test(', vin, '); end;')?>\n" + 
			"<?print vout.value?>" +
			"<?code db.execute('drop function ul4test')?>\n",
			Template.Whitespace.strip
		);

		Connection db = getDatabaseConnection();

		if (db != null)
			checkOutput("84", t, V("db", db));
	}

	@Test
	public void db_execute_procedure_out() throws Exception
	{
		Template t = T(
			"<?code db.execute('''\n" +
			" create or replace procedure ul4test(p_intarg out integer, p_numberarg out number, p_strarg out varchar2, p_clobarg out clob, p_datearg out timestamp)" +
			" as\n" +
			" begin\n" +
			"  p_intarg := 42;\n" +
			"  p_numberarg := 42.5;\n" +
			"  p_strarg := 'foo';\n" +
			"  dbms_lob.createtemporary(p_clobarg, true);\n" +
			"  for i in 1..100000 loop\n" +
			"   dbms_lob.writeappend(p_clobarg, 3, 'foo');\n" +
			"  end loop;\n" + 
			"  p_datearg := to_date('05.10.2014 16:17:18', 'DD.MM.YYYY HH24:MI:SS');\n" + 
			" end;\n" + 
			"''')?>\n" + 
			"<?code vint = db.int()?>\n" + 
			"<?code vnumber = db.number(42.5)?>\n" + 
			"<?code vstr = db.str()?>\n" + 
			"<?code vclob = db.clob()?>\n" + 
			"<?code vdate = db.date()?>\n" + 
			"<?code db.execute('call ul4test(', vint, ', ', vnumber, ', ', vstr, ', ', vclob, ', ', vdate, ')')?>\n" + 
			"<?print vint.value?>|<?print vnumber.value?>|<?print vstr.value?>|<?print vclob.value?>|<?print vdate.value?>" +
			"<?code db.execute('drop procedure ul4test')?>\n"
		);

		Connection db = getDatabaseConnection();

		if (db != null)
			checkOutput("42|42.5|foo|" + StringUtils.repeat("foo", 100000) + "|2014-10-05 16:17:18", t, V("db", db));
	}

	@Test
	public void db_execute_procedure_inout() throws Exception
	{
		Template t = T(
			"<?code db.execute('''\n" +
			" create or replace procedure ul4test(p_intarg in out integer, p_numberarg in out number, p_strarg in out varchar2, p_clobarg in out nocopy clob, p_datearg in out timestamp)\n" +
			" as\n" +
			" begin\n" +
			"  p_intarg := 2*p_intarg;\n" +
			"  p_numberarg := 2*p_numberarg;\n" +
			"  p_strarg := upper(p_strarg);\n" +
			"  for i in 0..99999 loop\n" +
			"   dbms_lob.write(p_clobarg, 3, 3*i+1, upper(dbms_lob.substr(p_clobarg, 3, 3*i+1)));\n" +
			"  end loop;\n" + 
			"  p_datearg := p_datearg + 1 + 1/24 + 1/24/60 + 1/24/60/60;\n" + 
			" end;\n" +
			"''')?>\n" + 
			"<?code vint = db.int(42)?>\n" + 
			"<?code vnumber = db.number(42.25)?>\n" + 
			"<?code vstr = db.str('foo')?>\n" + 
			"<?code vclob = db.clob(100000*'foo')?>\n" + 
			"<?code vdate = db.date(@(2014-10-05T16:17:18))?>\n" + 
			"<?code db.execute('call ul4test(', vint, ', ', vnumber, ', ', vstr, ', ', vclob, ', ', vdate, ')')?>\n" + 
			"<?print vint.value?>|<?print vnumber.value?>|<?print vstr.value?>|<?print vclob.value?>|<?print vdate.value?>" +
			"<?code db.execute('drop procedure ul4test')?>\n"
		);

		Connection db = getDatabaseConnection();

		if (db != null)
			checkOutput("84|84.5|FOO|" + StringUtils.repeat("FOO", 100000) + "|2014-10-06 17:18:19", t, V("db", db));
	}

	@Test
	public void db_query_scale() throws Exception
	{
		// Check that numbers that are not table fields don't get truncated to integer because the database doesn't know their scale
		Template t = T("<?for row in db.query('select 0.5 as x from dual')?><?print row.x > 0?><?end for?>");

		Connection db = getDatabaseConnection();

		if (db != null)
			checkOutput("True", t, V("db", db));
	}

	@Test
	public void empty_template()
	{
		checkOutput("", T(null));
	}

	@Test
	public void signature_positional_argument() throws Exception
	{
		Template t = T("<?def border_radius(radius)?>border-radius: <?print radius?>px;<?end def?><?render border_radius(5)?>");

		checkOutput("border-radius: 5px;", t);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_signature_directcall() throws Exception
	{
		Template function = T("<?return x?>", "func_with_sig", Template.Whitespace.strip, new Signature().addBoth("x"));

		function.call();
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_stringsignature_directcall() throws Exception
	{
		Template function = T("<?return x?>", "func_with_sig", Template.Whitespace.strip, "x");

		function.call();
	}

	@Test
	public void function_stringsignature_directcall_default() throws Exception
	{
		Template function = T("<?return x+y?>", "func_with_sig", Template.Whitespace.strip, "x=17, y=23");

		assertEquals(42, function.call(makeMap("y", 25)));
	}

	@Test
	public void function_stringsignature_directcall_remainingkwargs() throws Exception
	{
		Template function = T("<?return ', '.join(key + ': ' + str(value) for (key, value) in kwargs.items())?>", "func_with_sig", Template.Whitespace.strip, "**kwargs");

		assertEquals("y: 23, x: 17", function.call(makeOrderedMap("y", 23, "x", 17)));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_signature_templatecall() throws Exception
	{
		Template function = T("<?return x?>", "func_with_sig", Template.Whitespace.strip, new Signature().addBoth("x"));
		Template t = T("<?print func_with_sig()?>", "t", Template.Whitespace.strip);

		t.renders(V("func_with_sig", function));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void template_signature_directcall() throws Exception
	{
		Template t = T("<?print x?>", "t", Template.Whitespace.strip, new Signature().addBoth("x"));

		checkOutput("42", t);
	}

	@Test
	public void template_signature_nestedcall_directsig()
	{
		Template ti = T("<?return x + y?>", "inner", Template.Whitespace.strip, new Signature().addBoth("x").addBoth("y"));
		Template to = T("<?print inner(17, 23)?>", "outer", Template.Whitespace.strip);

		checkOutput("40", to, V("inner", ti));
	}

	@Test
	public void template_signature_nestedcall_stringsig()
	{
		Template ti = T("<?return x + y?>", "inner", Template.Whitespace.strip, "x, y");
		Template to = T("<?print inner(17, 23)?>", "outer", Template.Whitespace.strip);

		checkOutput("40", to, V("inner", ti));
	}

	@Test
	public void template_signature_nestedcall_tagsig()
	{
		Template ti = T("<?ul4 inner(x, y)?><?return x + y?>", "inner", Template.Whitespace.strip);
		Template to = T("<?print inner(17, 23)?>", "outer", Template.Whitespace.strip);

		checkOutput("40", to, V("inner", ti));
	}

	@Test
	public void template_signature_nestedrender_tagsig()
	{
		Template ti = T("<?ul4 inner(x, y)?><?print x + y?>", "inner", Template.Whitespace.strip);
		Template to = T("<?render inner(17, 23)?>", "outer", Template.Whitespace.strip);

		checkOutput("40", to, V("inner", ti));
	}

	@Test
	public void template_signature_directcall_default() throws Exception
	{
		Template t = T("<?print x?>", "t", Template.Whitespace.strip, new Signature().addBoth("x", 42));

		checkOutput("42", t);
	}

	@Test
	public void signature_serialization_varkwargs() throws Exception
	{
		Template t = T("<?ul4 t(a, **b)?><?print a?>,<?print b?>");

		checkOutput("gurk,{'b': 'hurz', 'c': 'hinz', 'd': 'kunz'}", t, V("a", "gurk", "b", "hurz", "c", "hinz", "d", "kunz"));
	}

	@Test
	public void signature_serialization_varargs() throws Exception
	{
		Template t = T("<?ul4 t(a, *b)?><?print a?>,<?print b?>");

		checkOutput("gurk,[]", t, V("a", "gurk"));
	}

	@Test
	public void signature_serialization_default() throws Exception
	{
		Template t = T("<?ul4 t(a, b='hurz')?><?print a?>,<?print b?>");

		checkOutput("gurk,hurz", t, V("a", "gurk"));
	}

	@Test
	public void function_subcall_default() throws Exception
	{
		checkOutput("42", T("<?def f(x=17, y=23)?><?return x+y?><?end def?><?print f(y=25)?>"));
	}

	@Test
	public void function_subcall_remainingkwargs() throws Exception
	{
		checkOutput("x: 17, y: 23", T("<?def f(**kwargs)?><?return ', '.join(key + ': ' + str(value) for (key, value) in sorted(kwargs.items()))?><?end def?><?print f(x=17, y=23)?>"));
	}

	@Test
	public void whitespace_smart_empty_block()
	{
		checkOutput("", T("<?whitespace smart?>\n<?if bug?>\n<?end if?>\n"));
	}

	@Test
	public void global_variables()
	{
		Template templateInner = T("<?print sum?>", "inner");
		Template templateOuter = T("<?render inner()?>", "outer");

		checkOutput("42", templateOuter, V("sum", 42), V("sum", 43, "inner", templateInner));
	}

	@Test
	public void template_repr()
	{
		Template t;

		t = T("<?print 42?>", "foo", Template.Whitespace.keep);
		assertEquals("<com.livinglogic.ul4.Template name='foo'>", FunctionRepr.call(t));

		t = T("<?print 42?>", "foo", Template.Whitespace.strip);
		assertEquals("<com.livinglogic.ul4.Template name='foo' whitespace='strip'>", FunctionRepr.call(t));

		t = T("<?print 42?>", "foo", Template.Whitespace.strip, "a, b=0xff");
		assertEquals("<com.livinglogic.ul4.Template name='foo' whitespace='strip' signature=(a, b=255)>", FunctionRepr.call(t));

		t = T("<?def x(a, b=0xff)?><?end def?><?print repr(x)?>", "foo", Template.Whitespace.keep);
		checkOutput("<com.livinglogic.ul4.TemplateClosure for <com.livinglogic.ul4.Template name='x' signatureAST=(a, b=0xff)>>", t);

		checkOutput("<com.livinglogic.ul4.Signature (x=17, y=@(2000-02-29))>", T("<?def f(x=17, y=@(2000-02-29))?><?return x+y?><?end def?><?print repr(f.signature)?>"));
		checkOutput("<com.livinglogic.ul4.Signature (bad=[...])>", T("<?code bad = []?><?code bad.append(bad)?><?def f(bad=bad)?><?end def?><?print repr(f.signature)?>"));
	}

	@Test
	public void module_ul4on_dumps()
	{
		checkOutput(dumps(null), T("<?print ul4on.dumps(data)?>"), V("data", null));
		checkOutput(dumps(false), T("<?print ul4on.dumps(data)?>"), V("data", false));
		checkOutput(dumps(true), T("<?print ul4on.dumps(data)?>"), V("data", true));
		checkOutput(dumps(42), T("<?print ul4on.dumps(data)?>"), V("data", 42));
		checkOutput(dumps(42.5), T("<?print ul4on.dumps(data)?>"), V("data", 42.5));
		checkOutput(dumps("abc"), T("<?print ul4on.dumps(data)?>"), V("data", "abc"));
		checkOutput(dumps(asList(1, 2, 3)), T("<?print ul4on.dumps(data)?>"), V("data", asList(1, 2, 3)));
		checkOutput(dumps(makeMap("one", 1)), T("<?print ul4on.dumps(data)?>"), V("data", V("one", 1)));
		// Check pretty printing
		checkOutput("L\n\ti1\n\ti2\n\ti3\n]\n", T("<?print ul4on.dumps([1, 2, 3], '\\t')?>"));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void module_ul4on_dumps_bad_kwarg()
	{
		T("<?print ul4on.dumps(obj=data)?>").renders(V("data", null));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	@Test
	public void module_ul4on_dumps_0_args()
	{
		checkOutput("", T("<?print ul4on.dumps()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void module_ul4on_dumps_3_args()
	{
		checkOutput("", T("<?print ul4on.dumps(1, 2, 3)?>"));
	}

	@Test
	public void module_ul4on_loads()
	{
		checkOutput("None", T("<?print repr(ul4on.loads(dump))?>"), V("dump", dumps(null)));
		checkOutput("False", T("<?print repr(ul4on.loads(dump))?>"), V("dump", dumps(false)));
		checkOutput("True", T("<?print repr(ul4on.loads(dump))?>"), V("dump", dumps(true)));
		checkOutput("42", T("<?print repr(ul4on.loads(dump))?>"), V("dump", dumps(42)));
		checkOutput("42.5", T("<?print repr(ul4on.loads(dump))?>"), V("dump", dumps(42.5)));
		checkOutput("'abc'", T("<?print repr(ul4on.loads(dump))?>"), V("dump", dumps("abc")));
		checkOutput("[1, 2, 3]", T("<?print repr(ul4on.loads(dump))?>"), V("dump", dumps(asList(1, 2, 3))));
		checkOutput("{'one': 1}", T("<?print repr(ul4on.loads(dump))?>"), V("dump", dumps(V("one", 1))));
	}

	@CauseTest(expectedCause=UnsupportedArgumentNameException.class)
	public void module_ul4on_loads_bad_kwarg()
	{
		T("<?print repr(ul4on.loads(dump=data))?>").renders(V("data", dumps(null)));
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	@Test
	public void module_ul4on_loads_0_args()
	{
		checkOutput("", T("<?print ul4on.loads()?>"));
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void module_ul4on_loads_2_args()
	{
		checkOutput("", T("<?print ul4on.loads(1, 2)?>"));
	}

	@Test
	public void module_encoder_multiple_dumps()
	{
		checkOutput("S'gurk' S'hurz' ^0 ^1", T("<?code e = ul4on.Encoder()?><?print e.dumps('gurk')?> <?print e.dumps('hurz')?> <?print e.dumps('gurk')?> <?print e.dumps('hurz')?>"));
	}

	@Test
	public void module_decoder_multiple_loads()
	{
		checkOutput("gurk hurz gurk hurz", T("<?code d = ul4on.Decoder()?><?print d.loads('S\"gurk\"')?> <?print d.loads('S\"hurz\"')?> <?print d.loads('^0')?> <?print d.loads('^1')?>"));
	}

	@Test
	public void narrow_big_integer()
	{
		BigInteger x = new BigInteger("99999999999999999999999999999999999999");
		checkOutput("99999999999999999999999999999999999999", T("<?print int(x)?>"), V("x", x));
		checkOutput("100000000000000000000000000000000000000", T("<?print int(x+1)?>"), V("x", x));
	}

	private void expect_arithmetic_exception(Template t, String message, Object dividend, Object divisor)
	{
		try
		{
			t.renders(V("dividend", dividend, "divisor", divisor));
		}
		catch (ArithmeticException exc)
		{
			return;
		}
		throw new RuntimeException(Utils.formatMessage(message, dividend, divisor, dividend, divisor));
	}

	@Test
	public void zerodivision_truediv()
	{
		Template t = T("<?print dividend / divisor?>");
		String message = "true division {!r} / {!r} (with types {!t} and {!t}) didn't raise ArithmeticException";

		List<Object> dividends = asList(true, (byte)1, (short)2, 3, 4l, 5.5f, 6.5d, new BigInteger("7"), new BigDecimal("8.5"), new TimeDelta(9));
		List<Object> divisors = asList(false, (byte)0, (short)0, 0, 0l, 0.0f, 0.0d, new BigInteger("0"), new BigDecimal("0"));

		for (Object dividend : dividends)
			for (Object divisor : divisors)
				expect_arithmetic_exception(t, message, dividend, divisor);
	}

	@Test
	public void zerodivision_floordiv()
	{
		Template t = T("<?print dividend // divisor?>");
		String message = "floor division {!r} // {!r} (with types {!t} and {!t}) didn't raise ArithmeticException";

		List<Object> dividends = asList(true, (byte)1, (short)2, 3, 4l, 5.5f, 6.5d, new BigInteger("7"), new BigDecimal("8.5"));
		List<Object> divisors = asList(false, (byte)0, (short)0, 0, 0l, 0.0f, 0.0d, new BigInteger("0"), new BigDecimal("0"));

		for (Object dividend : dividends)
			for (Object divisor : divisors)
				expect_arithmetic_exception(t, message, dividend, divisor);

		TimeDelta timeDelta = new TimeDelta(42);
		List<Object> divisorsTimeDelta = asList(false, (byte)0, (short)0, 0, 0l, new BigInteger("0"));

		for (Object divisor : divisorsTimeDelta)
			expect_arithmetic_exception(t, message, timeDelta, divisor);
	}

	@Test
	public void module_operator_attrgetter()
	{
		checkOutput("17", T("<?print operator.attrgetter('x')(p)?>"), V("p", new Point(17, 23)));
		checkOutput("[17, 23]", T("<?print operator.attrgetter('x', 'y')(p)?>"), V("p", new Point(17, 23)));
		Template t = T("<?print x?>");
		checkOutput("[slice(0, 11, None), 0, 11]", T("<?print operator.attrgetter('pos', 'pos.start', 'pos.stop')(t.content[-1])?>"), V("t", t));
	}

	@Test
	public void module_math_pi()
	{
		checkOutput("True", T("<?print 3.14 < math.pi and math.pi < 3.15?>"));
	}

	@Test
	public void module_math_e()
	{
		checkOutput("True", T("<?print 2.71 < math.e and math.e < 2.72?>"));
	}

	@Test
	public void module_math_isclose()
	{
		checkOutput("True", T("<?print math.isclose(math.pi, 3.14, abs_tol=0.1)?>"));
		checkOutput("False", T("<?print math.isclose(math.pi, 3.03, abs_tol=0.1)?>"));
	}

	@Test
	public void template_ul4_template()
	{
		checkOutput("gurk;hurz", T("<?code t = ul4.Template('<?print x?' + '>', name='gurk', signature='x')?><?print t.name?>;<?render t('hurz')?>"));
	}
}
