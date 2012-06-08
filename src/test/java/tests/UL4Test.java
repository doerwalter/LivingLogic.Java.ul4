package tests;

import java.util.Date;
import java.util.List;
import static java.util.Arrays.*;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.antlr.runtime.RecognitionException;

import com.livinglogic.ul4.Color;
import com.livinglogic.ul4.InterpretedTemplate;
import static com.livinglogic.utils.MapUtils.*;
import static com.livinglogic.ul4on.Utils.*;
import static com.livinglogic.ul4.Utils.*;
import com.livinglogic.ul4.KeyException;
import com.livinglogic.ul4.BlockException;
import com.livinglogic.ul4.UnknownFunctionException;
import com.livinglogic.ul4.ArgumentCountMismatchException;
import com.livinglogic.ul4.SyntaxException;

@RunWith(CauseTestRunner.class)
public class UL4Test
{
	private static InterpretedTemplate getTemplate(String source, String name)
	{
		try
		{
			InterpretedTemplate template = new InterpretedTemplate(source, name);
			// System.out.println(template);
			return template;
		}
		catch (RecognitionException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private static InterpretedTemplate getTemplate(String source)
	{
		return getTemplate(source, null);
	}

	private static String getTemplateOutput(String source, Object... args)
	{
		InterpretedTemplate template = getTemplate(source);
		return template.renders(makeMap(args));
	}

	private static void checkTemplateOutput(String expected, String source, Object... args)
	{
		// Render the template once by directly compliing and rendering it
		InterpretedTemplate template1 = getTemplate(source);
		String output1 = template1.renders(makeMap(args));
		assertEquals(expected, output1);

		// Recreate the template from the dump of the compiled template
		InterpretedTemplate template2 = InterpretedTemplate.loads(template1.dumps());

		// Check that the templates format the same
		assertEquals(template1.toString(), template2.toString());

		// Check that theyhave the same output
		String output2 = template2.renders(makeMap(args));
		assertEquals(expected, output2);
	}

	private static void checkTemplateOutput2(String expected1, String expected2, String source, Object... args)
	{
		// Render the template once by directly compliing and rendering it
		InterpretedTemplate template1 = getTemplate(source);
		String output1 = template1.renders(makeMap(args));
		if (!output1.equals(expected1) && !output1.equals(expected2))
			fail("expected <" + expected1 + "> or <" + expected2 + ">, got <" + output1 + ">");

		// Recreate the template from the dump of the compiled template
		InterpretedTemplate template2 = InterpretedTemplate.loads(template1.dumps());

		// Check that the templates format the same
		assertEquals(template1.toString(), template2.toString());

		// Check that theyhave the same output
		String output2 = template2.renders(makeMap(args));
		if (!output1.equals(expected1) && !output1.equals(expected2))
			fail("expected <" + expected1 + "> or <" + expected2 + ">, got <" + output2 + ">");
	}

	@Test
	public void tag_text()
	{
		checkTemplateOutput("gurk", "gurk");
	}

	@Test
	public void type_none()
	{
		checkTemplateOutput("no", "<?if None?>yes<?else?>no<?end if?>");
		checkTemplateOutput("", "<?print None?>");

	}

	@Test
	public void type_bool()
	{
		checkTemplateOutput("False", "<?print False?>");
		checkTemplateOutput("no", "<?if False?>yes<?else?>no<?end if?>");
		checkTemplateOutput("True", "<?print True?>");
		checkTemplateOutput("yes", "<?if True?>yes<?else?>no<?end if?>");
	}

	@Test
	public void type_int()
	{
		checkTemplateOutput("0", "<?print 0?>");
		checkTemplateOutput("42", "<?print 42?>");
		checkTemplateOutput("-42", "<?print -42?>");
		checkTemplateOutput("134217727", "<?print 134217727?>");
		checkTemplateOutput("134217728", "<?print 134217728?>");
		checkTemplateOutput("-134217728", "<?print -134217728?>");
		checkTemplateOutput("-134217729", "<?print -134217729?>");
		checkTemplateOutput("576460752303423487", "<?print 576460752303423487?>");
		checkTemplateOutput("576460752303423488", "<?print 576460752303423488?>");
		checkTemplateOutput("-576460752303423488", "<?print -576460752303423488?>");
		checkTemplateOutput("-576460752303423489", "<?print -576460752303423489?>");
		checkTemplateOutput("9999999999", "<?print 9999999999?>");
		checkTemplateOutput("-9999999999", "<?print -9999999999?>");
		checkTemplateOutput("99999999999999999999", "<?print 99999999999999999999?>");
		checkTemplateOutput("-99999999999999999999", "<?print -99999999999999999999?>");
		checkTemplateOutput("255", "<?print 0xff?>");
		checkTemplateOutput("255", "<?print 0Xff?>");
		checkTemplateOutput("-255", "<?print -0xff?>");
		checkTemplateOutput("-255", "<?print -0Xff?>");
		checkTemplateOutput("63", "<?print 0o77?>");
		checkTemplateOutput("63", "<?print 0O77?>");
		checkTemplateOutput("-63", "<?print -0o77?>");
		checkTemplateOutput("-63", "<?print -0O77?>");
		checkTemplateOutput("7", "<?print 0b111?>");
		checkTemplateOutput("7", "<?print 0B111?>");
		checkTemplateOutput("-7", "<?print -0b111?>");
		checkTemplateOutput("-7", "<?print -0B111?>");
		checkTemplateOutput("no", "<?if 0?>yes<?else?>no<?end if?>");
		checkTemplateOutput("yes", "<?if 1?>yes<?else?>no<?end if?>");
		checkTemplateOutput("yes", "<?if -1?>yes<?else?>no<?end if?>");
	}

	@Test
	public void type_float()
	{
		checkTemplateOutput("0.0", "<?print 0.?>");
		checkTemplateOutput("42.0", "<?print 42.?>");
		checkTemplateOutput("-42.0", "<?print -42.?>");
		checkTemplateOutput("-42.5", "<?print -42.5?>");
		checkTemplateOutput("1e42", "<?print 1E42?>");
		checkTemplateOutput("1e42", "<?print 1e42?>");
		checkTemplateOutput("-1e42", "<?print -1E42?>");
		checkTemplateOutput("-1e42", "<?print -1e42?>");
		checkTemplateOutput("no", "<?if 0.?>yes<?else?>no<?end if?>");
		checkTemplateOutput("yes", "<?if 1.?>yes<?else?>no<?end if?>");
		checkTemplateOutput("yes", "<?if -1.?>yes<?else?>no<?end if?>");
	}

	@Test
	public void type_string()
	{
		checkTemplateOutput("foo", "<?print \"foo\"?>");
		checkTemplateOutput("\n", "<?print \"\\n\"?>");
		checkTemplateOutput("\r", "<?print \"\\r\"?>");
		checkTemplateOutput("\t", "<?print \"\\t\"?>");
		checkTemplateOutput("\f", "<?print \"\\f\"?>");
		checkTemplateOutput("\u0008", "<?print \"\\b\"?>");
		checkTemplateOutput("\u0007", "<?print \"\\a\"?>");
		checkTemplateOutput("\u001b", "<?print \"\\e\"?>");
		checkTemplateOutput("\u0000", "<?print \"\\x00\"?>");
		checkTemplateOutput("\"", "<?print \"\\\"\"?>");
		checkTemplateOutput("'", "<?print \"\\'\"?>");
		checkTemplateOutput("\u20ac", "<?print \"\u20ac\"?>");
		checkTemplateOutput("\u00ff", "<?print \"\\xff\"?>");
		checkTemplateOutput("\u20ac", "<?print \"\\u20ac\"?>");
		checkTemplateOutput("a\nb", "<?print \"a\nb\"?>");
		checkTemplateOutput("gu\n\r\trk", "<?print 'gu\n\r\trk'?>");
		checkTemplateOutput("gu\n\r\t\\rk", "<?print 'gu\\n\\r\\t\\\\rk'?>");
		checkTemplateOutput("no", "<?if ''?>yes<?else?>no<?end if?>");
		checkTemplateOutput("yes", "<?if 'foo'?>yes<?else?>no<?end if?>");
	}

	@Test
	public void type_date()
	{
		checkTemplateOutput("2000-02-29", "<?print @(2000-02-29).isoformat()?>");
		checkTemplateOutput("2000-02-29", "<?print @(2000-02-29T).isoformat()?>");
		checkTemplateOutput("2000-02-29T12:34:00", "<?print @(2000-02-29T12:34).isoformat()?>");
		checkTemplateOutput("2000-02-29T12:34:56", "<?print @(2000-02-29T12:34:56).isoformat()?>");
		checkTemplateOutput("2000-02-29T12:34:56.987000", "<?print @(2000-02-29T12:34:56.987000).isoformat()?>");
		checkTemplateOutput("yes", "<?if @(2000-02-29T12:34:56.987654)?>yes<?else?>no<?end if?>");
	}

	@Test
	public void type_color()
	{
		checkTemplateOutput("255,255,255,255", "<?code c = #fff?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("255,255,255,255", "<?code c = #ffffff?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("18,52,86,255", "<?code c = #123456?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("17,34,51,68", "<?code c = #1234?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("18,52,86,120", "<?code c = #12345678?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("yes", "<?if #fff?>yes<?else?>no<?end if?>");
	}

	@Test
	public void type_list()
	{
		checkTemplateOutput("", "<?for item in []?><?print item?>;<?end for?>");
		checkTemplateOutput("1;", "<?for item in [1]?><?print item?>;<?end for?>");
		checkTemplateOutput("1;", "<?for item in [1,]?><?print item?>;<?end for?>");
		checkTemplateOutput("1;2;", "<?for item in [1, 2]?><?print item?>;<?end for?>");
		checkTemplateOutput("1;2;", "<?for item in [1, 2,]?><?print item?>;<?end for?>");
		checkTemplateOutput("no", "<?if []?>yes<?else?>no<?end if?>");
		checkTemplateOutput("yes", "<?if [1]?>yes<?else?>no<?end if?>");
	}

	@Test
	public void type_dict()
	{
		checkTemplateOutput("", "<?for (key, value) in {}.items()?><?print key?>:<?print value?>\n<?end for?>");
		checkTemplateOutput("1:2\n", "<?for (key, value) in {1:2}.items()?><?print key?>:<?print value?>\n<?end for?>");
		checkTemplateOutput("1:2\n", "<?for (key, value) in {1:2,}.items()?><?print key?>:<?print value?>\n<?end for?>");
		// With duplicate keys, later ones simply overwrite earlier ones
		checkTemplateOutput("1:3\n", "<?for (key, value) in {1:2, 1: 3}.items()?><?print key?>:<?print value?>\n<?end for?>");
		// Test **
		checkTemplateOutput("1:2\n", "<?for (key, value) in {**{1:2}}.items()?><?print key?>:<?print value?>\n<?end for?>");
		checkTemplateOutput("1:4\n", "<?for (key, value) in {1:1, **{1:2}, 1:3, **{1:4}}.items()?><?print key?>:<?print value?>\n<?end for?>");
		checkTemplateOutput("no", "<?if {}?>yes<?else?>no<?end if?>");
		checkTemplateOutput("yes", "<?if {1:2}?>yes<?else?>no<?end if?>");
	}

	@Test
	public void tag_storevar()
	{
		checkTemplateOutput("42", "<?code x = 42?><?print x?>");
		checkTemplateOutput("xyzzy", "<?code x = 'xyzzy'?><?print x?>");
	}

	@Test
	public void tag_addvar()
	{
		String source = "<?code x += y?><?print x?>";
		checkTemplateOutput("40", source, "x", 17, "y", 23);
		checkTemplateOutput("40.0", source, "x", 17, "y", 23.0);
		checkTemplateOutput("40.0", source, "x", 17.0, "y", 23);
		checkTemplateOutput("40.0", source, "x", 17.0, "y", 23.0);
		checkTemplateOutput("17", source, "x", 17, "y", false);
		checkTemplateOutput("18", source, "x", 17, "y", true);
		checkTemplateOutput("23", source, "x", false, "y", 23);
		checkTemplateOutput("24", source, "x", true, "y", 23);
	}

	@Test
	public void tag_subvar()
	{
		String source = "<?code x -= y?><?print x?>";
		checkTemplateOutput("-6", source, "x", 17, "y", 23);
		checkTemplateOutput("-6.0", source, "x", 17, "y", 23.0);
		checkTemplateOutput("-6.0", source, "x", 17.0, "y", 23);
		checkTemplateOutput("-6.0", source, "x", 17.0, "y", 23.0);
		checkTemplateOutput("17", source, "x", 17, "y", false);
		checkTemplateOutput("16", source, "x", 17, "y", true);
		checkTemplateOutput("-23", source, "x", false, "y", 23);
		checkTemplateOutput("-22", source, "x", true, "y", 23);
	}

	@Test
	public void tag_mulvar()
	{
		String source = "<?code x *= y?><?print x?>";
		checkTemplateOutput("391", source, "x", 17, "y", 23);
		checkTemplateOutput("391.0", source, "x", 17, "y", 23.0);
		checkTemplateOutput("391.0", source, "x", 17.0, "y", 23);
		checkTemplateOutput("391.0", source, "x", 17.0, "y", 23.0);
		checkTemplateOutput("0", source, "x", 17, "y", false);
		checkTemplateOutput("17", source, "x", 17, "y", true);
		checkTemplateOutput("0", source, "x", false, "y", 23);
		checkTemplateOutput("23", source, "x", true, "y", 23);
		checkTemplateOutput("xyzzyxyzzyxyzzy", source, "x", 3, "y", "xyzzy");
		checkTemplateOutput("", source, "x", false, "y", "xyzzy");
		checkTemplateOutput("xyzzy", source, "x", true, "y", "xyzzy");
		checkTemplateOutput("xyzzyxyzzyxyzzy", source, "x", "xyzzy", "y", 3);
		checkTemplateOutput("", source, "x", "xyzzy", "y", false);
		checkTemplateOutput("xyzzy", source, "x", "xyzzy", "y", true);
	}

	@Test
	public void tag_floordivvar()
	{
		String source = "<?code x //= y?><?print x?>";
		checkTemplateOutput("2", source, "x", 5, "y", 2);
		checkTemplateOutput("-3", source, "x", 5, "y", -2);
		checkTemplateOutput("-3", source, "x", -5, "y", 2);
		checkTemplateOutput("2", source, "x", -5, "y", -2);
		checkTemplateOutput("2.0", source, "x", 5., "y", 2.);
		checkTemplateOutput("-3.0", source, "x", 5., "y", -2.);
		checkTemplateOutput("-3.0", source, "x", -5., "y", 2.);
		checkTemplateOutput("2.0", source, "x", -5., "y", -2.);
		checkTemplateOutput("1", source, "x", true, "y", 1);
		checkTemplateOutput("0", source, "x", false, "y", 1);
	}

	@Test
	public void tag_truedivvar()
	{
		String source = "<?code x /= y?><?print x?>";
		checkTemplateOutput("2.5", source, "x", 5, "y", 2);
		checkTemplateOutput("-2.5", source, "x", 5, "y", -2);
		checkTemplateOutput("-2.5", source, "x", -5, "y", 2);
		checkTemplateOutput("2.5", source, "x", -5, "y", -2);
		checkTemplateOutput("2.5", source, "x", 5., "y", 2.);
		checkTemplateOutput("-2.5", source, "x", 5., "y", -2.);
		checkTemplateOutput("-2.5", source, "x", -5., "y", 2.);
		checkTemplateOutput("2.5", source, "x", -5., "y", -2.);
		checkTemplateOutput("1.0", source, "x", true, "y", 1);
		checkTemplateOutput("0.0", source, "x", false, "y", 1);
	}


	@Test
	public void tag_modvar()
	{
		String source = "<?code x %= y?><?print x?>";
		checkTemplateOutput("4", source, "x", 1729, "y", 23);
		checkTemplateOutput("19", source, "x", -1729, "y", 23);
		checkTemplateOutput("19", source, "x", -1729, "y", 23);
		checkTemplateOutput("-4", source, "x", -1729, "y", -23);
		checkTemplateOutput("1.5", source, "x", 6.5, "y", 2.5);
		checkTemplateOutput("1.0", source, "x", -6.5, "y", 2.5);
		checkTemplateOutput("-1.0", source, "x", 6.5, "y", -2.5);
		checkTemplateOutput("-1.5", source, "x", -6.5, "y", -2.5);
		checkTemplateOutput("1", source, "x", true, "y", 23);
		checkTemplateOutput("0", source, "x", false, "y", 23);
	}

	@CauseTest(expectedCause=KeyException.class)
	public void tag_delvar()
	{
		checkTemplateOutput("", "<?code x = 1729?><?code del x?><?print x?>");
	}

	@Test
	public void tag_for_string()
	{
		String source = "<?for c in data?>(<?print c?>)<?end for?>";
		checkTemplateOutput("", source, "data", "");
		checkTemplateOutput("(g)(u)(r)(k)", source, "data", "gurk");
	}

	@Test
	public void tag_for_list()
	{
		String source = "<?for c in data?>(<?print c?>)<?end for?>";
		checkTemplateOutput("", source, "data", asList());
		checkTemplateOutput("(g)(u)(r)(k)", source, "data", asList("g", "u", "r", "k"));
	}

	@Test
	public void tag_for_dict()
	{
		String source = "<?for c in sorted(data)?>(<?print c?>)<?end for?>";
		checkTemplateOutput("", source, "data", makeMap());
		checkTemplateOutput("(a)(b)(c)", source, "data", makeMap("a", 1, "b", 2, "c", 3));
	}

	@Test
	public void tag_for_nested()
	{
		String source = "<?for list in data?>[<?for n in list?>(<?print n?>)<?end for?>]<?end for?>";
		checkTemplateOutput("[(1)(2)][(3)(4)]", source, "data", asList(asList(1, 2), asList(3, 4)));
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

		checkTemplateOutput("(spam)(gurk)(hinz)", "<?for (a,) in data?>(<?print a?>)<?end for?>", "data", data1);
		checkTemplateOutput("(spam,eggs)(gurk,hurz)(hinz,kunz)", "<?for (a, b) in data?>(<?print a?>,<?print b?>)<?end for?>", "data", data2);
		checkTemplateOutput("(spam,eggs,17)(gurk,hurz,23)(hinz,kunz,42)", "<?for (a, b, c) in data?>(<?print a?>,<?print b?>,<?print c?>)<?end for?>", "data", data3);
		checkTemplateOutput("(spam,eggs,17,)(gurk,hurz,23,False)(hinz,kunz,42,True)", "<?for (a, b, c, d) in data?>(<?print a?>,<?print b?>,<?print c?>,<?print d?>)<?end for?>", "data", data4);
	}

	@Test
	public void tag_break()
	{
		checkTemplateOutput("1, 2, ", "<?for i in [1,2,3]?><?print i?>, <?if i==2?><?break?><?end if?><?end for?>");
	}

	@Test
	public void tag_break_nested()
	{
		checkTemplateOutput("1, 1, 2, 1, 2, 3, ", "<?for i in [1,2,3,4]?><?for j in [1,2,3,4]?><?print j?>, <?if j>=i?><?break?><?end if?><?end for?><?if i>=3?><?break?><?end if?><?end for?>");
	}

	@CauseTest(expectedCause=BlockException.class)
	public void tag_break_outside_loop()
	{
		checkTemplateOutput("", "<?break?>");
	}

	@CauseTest(expectedCause=BlockException.class)
	public void tag_break_outside_loop_in_def()
	{
		checkTemplateOutput("", "<def gurk?><?break?><?end def?>");
	}

	@Test
	public void tag_continue()
	{
		checkTemplateOutput("1, 3, ", "<?for i in [1,2,3]?><?if i==2?><?continue?><?end if?><?print i?>, <?end for?>");
	}

	@Test
	public void tag_continue_nested()
	{
		checkTemplateOutput("1, 3, \n1, 3, \n", "<?for i in [1,2,3]?><?if i==2?><?continue?><?end if?><?for j in [1,2,3]?><?if j==2?><?continue?><?end if?><?print j?>, <?end for?>\n<?end for?>");
	}

	@CauseTest(expectedCause=BlockException.class)
	public void tag_continue_outside_loop()
	{
		checkTemplateOutput("", "<?continue?>");
	}

	@CauseTest(expectedCause=BlockException.class)
	public void tag_continue_outside_loop_in_def()
	{
		checkTemplateOutput("", "<def gurk?><?continue?><?end def?>");
	}

	@Test
	public void tag_if()
	{
		checkTemplateOutput("42", "<?if data?><?print data?><?end if?>", "data", 42);
	}

	@Test
	public void tag_else()
	{
		String source = "<?if data?><?print data?><?else?>no<?end if?>";
		checkTemplateOutput("42", source, "data", 42);
		checkTemplateOutput("no", source, "data", 0);
	}

	// // FIXME: Doesn't work, because of chained exceptions, needs to be split into n tests
	// // @Test(expected=BlockException)
	// // public void block_errors()
	// // {
	// // 	checkTemplateOutput("", "<?for x in data?>"); // "BlockError: block unclosed"
	// // 	checkTemplateOutput("", "<?for x in data?><?end if?>"); // "BlockError: endif doesn't match any if"
	// // 	checkTemplateOutput("", "<?end?>"); // "BlockError: not in any block"
	// // 	checkTemplateOutput("", "<?end for?>"); // "BlockError: not in any block"
	// // 	checkTemplateOutput("", "<?end if?>"); // "BlockError: not in any block"
	// // 	checkTemplateOutput("", "<?else?>"); // "BlockError: else doesn't match any if"
	// // 	checkTemplateOutput("", "<?if data?>"); // "BlockError: block unclosed"
	// // 	checkTemplateOutput("", "<?if data?><?else?>"); // "BlockError: block unclosed"
	// // 	checkTemplateOutput("", "<?if data?><?else?><?else?>"); // "BlockError: duplicate else"
	// // 	checkTemplateOutput("", "<?if data?><?else?><?elif data?>"); // "BlockError: else already seen in elif"
	// // 	checkTemplateOutput("", "<?if data?><?elif data?><?elif data?><?else?><?elif data?>"); // "BlockError: else already seen in elif"
	// // }


	// // FIXME: Doesn't work, because of chained exceptions, needs to be split into n tests
	// // @Test(expected=BlockException)
	// // public void empty()
	// // {
	// // 	checkTemplateOutput("", "<?print?>"); // "expression required"
	// // 	checkTemplateOutput("", "<?if?>"); // "expression required"
	// // 	checkTemplateOutput("", "<?if x?><?elif?><?end if?>"); // "expression required"
	// // 	checkTemplateOutput("", "<?for?>"); // "loop expression required"
	// // 	checkTemplateOutput("", "<?code?>"); // "statement required"
	// // }

	@Test
	public void operator_add()
	{
		String source = "<?print x + y?>";

		checkTemplateOutput("0", source, "x", false, "y", false);
		checkTemplateOutput("1", source, "x", false, "y", true);
		checkTemplateOutput("1", source, "x", true, "y", false);
		checkTemplateOutput("2", source, "x", true, "y", true);
		checkTemplateOutput("18", source, "x", 17, "y", true);
		checkTemplateOutput("40", source, "x", 17, "y", 23);
		checkTemplateOutput("18.0", source, "x", 17, "y", 1.0);
		checkTemplateOutput("24", source, "x", true, "y", 23);
		checkTemplateOutput("22.0", source, "x", -1.0, "y", 23);
		checkTemplateOutput("foobar", source, "x", "foo", "y", "bar");
		// List addition is not implemented: checkTemplateOutput("(foo)(bar)(gurk)(hurz)", "<?for i in a+b?>(<?print i?>)<?end for?>", "a", asList("foo", "bar"), "b", asList("gurk", "hurz"));
		// This checks constant folding
		checkTemplateOutput("3", "<?print 1+2?>");
		checkTemplateOutput("2", "<?print 1+True?>");
		checkTemplateOutput("3.0", "<?print 1+2.0?>");
	}

	@Test
	public void operator_sub()
	{
		String source = "<?print x - y?>";

		checkTemplateOutput("0", source, "x", false, "y", false);
		checkTemplateOutput("-1", source, "x", false, "y", true);
		checkTemplateOutput("1", source, "x", true, "y", false);
		checkTemplateOutput("0", source, "x", true, "y", true);
		checkTemplateOutput("16", source, "x", 17, "y", true);
		checkTemplateOutput("-6", source, "x", 17, "y", 23);
		checkTemplateOutput("16.0", source, "x", 17, "y", 1.0);
		checkTemplateOutput("-22", source, "x", true, "y", 23);
		checkTemplateOutput("-24.0", source, "x", -1.0, "y", 23);
		// This checks constant folding
		checkTemplateOutput("-1", "<?print 1-2?>");
		checkTemplateOutput("1", "<?print 2-True?>");
		checkTemplateOutput("-1.0", "<?print 1-2.0?>");
	}

	@Test
	public void operator_mul()
	{
		String source = "<?print x * y?>";

		checkTemplateOutput("0", source, "x", false, "y", false);
		checkTemplateOutput("0", source, "x", false, "y", true);
		checkTemplateOutput("0", source, "x", true, "y", false);
		checkTemplateOutput("1", source, "x", true, "y", true);
		checkTemplateOutput("17", source, "x", 17, "y", true);
		checkTemplateOutput("391", source, "x", 17, "y", 23);
		checkTemplateOutput("17.0", source, "x", 17, "y", 1.0);
		checkTemplateOutput("23", source, "x", true, "y", 23);
		checkTemplateOutput("-23.0", source, "x", -1.0, "y", 23);
		checkTemplateOutput("foofoofoo", source, "x", 3, "y", "foo");
		checkTemplateOutput("foofoofoo", source, "x", "foo", "y", 3);
		checkTemplateOutput("(foo)(bar)(foo)(bar)(foo)(bar)", "<?for i in 3*data?>(<?print i?>)<?end for?>", "data", asList("foo", "bar"));
		// This checks constant folding
		checkTemplateOutput("391", "<?print 17*23?>");
		checkTemplateOutput("17", "<?print 17*True?>");
		checkTemplateOutput("391.0", "<?print 17.0*23.0?>");
	}

	@Test
	public void operator_truediv()
	{
		String source = "<?print x / y?>";

		checkTemplateOutput("0.0", source, "x", false, "y", true);
		checkTemplateOutput("1.0", source, "x", true, "y", true);
		checkTemplateOutput("17.0", source, "x", 17, "y", true);
		checkTemplateOutput("17.0", source, "x", 391, "y", 23);
		checkTemplateOutput("17.0", source, "x", 17, "y", 1.0);
		checkTemplateOutput("0.5", source, "x", 1, "y", 2);
		// This checks constant folding
		checkTemplateOutput("0.5", "<?print 1/2?>");
		checkTemplateOutput("2.0", "<?print 2.0/True?>");
	}

	@Test
	public void operator_floordiv()
	{
		String source = "<?print x // y?>";

		checkTemplateOutput("0", source, "x", false, "y", true);
		checkTemplateOutput("1", source, "x", true, "y", true);
		checkTemplateOutput("17", source, "x", 17, "y", true);
		checkTemplateOutput("17", source, "x", 392, "y", 23);
		checkTemplateOutput("17.0", source, "x", 17, "y", 1.0);
		checkTemplateOutput("0", source, "x", 1, "y", 2);
		// This checks constant folding
		checkTemplateOutput("0.5", "<?print 1/2?>");
		checkTemplateOutput("2.0", "<?print 2.0/True?>");
	}

	@Test
	public void operator_mod()
	{
		String source = "<?print x % y?>";

		checkTemplateOutput("0", source, "x", false, "y", true);
		checkTemplateOutput("0", source, "x", true, "y", true);
		checkTemplateOutput("0", source, "x", 17, "y", true);
		checkTemplateOutput("6", source, "x", 23, "y", 17);
		checkTemplateOutput("0.5", source, "x", 5.5, "y", 2.5);
		// This checks constant folding
		checkTemplateOutput("6", "<?print 23 % 17?>");
	}

	@Test
	public void operator_eq()
	{
		String source = "<?print x == y?>";

		checkTemplateOutput("False", source, "x", false, "y", true);
		checkTemplateOutput("True", source, "x", true, "y", true);
		checkTemplateOutput("True", source, "x", 1, "y", true);
		checkTemplateOutput("False", source, "x", 1, "y", false);
		checkTemplateOutput("False", source, "x", 17, "y", 23);
		checkTemplateOutput("True", source, "x", 17, "y", 17);
		checkTemplateOutput("True", source, "x", 17, "y", 17.0);
		// This checks constant folding
		checkTemplateOutput("False", "<?print 17 == 23?>");
		checkTemplateOutput("True", "<?print 17 == 17.?>");
	}

	@Test
	public void operator_ne()
	{
		String source = "<?print x != y?>";

		checkTemplateOutput("True", source, "x", false, "y", true);
		checkTemplateOutput("False", source, "x", true, "y", true);
		checkTemplateOutput("False", source, "x", 1, "y", true);
		checkTemplateOutput("True", source, "x", 1, "y", false);
		checkTemplateOutput("True", source, "x", 17, "y", 23);
		checkTemplateOutput("False", source, "x", 17, "y", 17);
		checkTemplateOutput("False", source, "x", 17, "y", 17.0);
		// This checks constant folding
		checkTemplateOutput("True", "<?print 17 != 23?>");
		checkTemplateOutput("False", "<?print 17 != 17.?>");
	}

	@Test
	public void operator_lt()
	{
		String source = "<?print x < y?>";

		checkTemplateOutput("True", source, "x", false, "y", true);
		checkTemplateOutput("False", source, "x", true, "y", true);
		checkTemplateOutput("False", source, "x", 1, "y", true);
		checkTemplateOutput("True", source, "x", true, "y", 2);
		checkTemplateOutput("True", source, "x", 17, "y", 23);
		checkTemplateOutput("False", source, "x", 23, "y", 17);
		checkTemplateOutput("False", source, "x", 17, "y", 17.0);
		checkTemplateOutput("True", source, "x", 17, "y", 23.0);
		// This checks constant folding
		checkTemplateOutput("True", "<?print 17 < 23?>");
		checkTemplateOutput("False", "<?print 17 < 17.?>");
	}

	@Test
	public void operator_le()
	{
		String source = "<?print x <= y?>";

		checkTemplateOutput("True", source, "x", false, "y", true);
		checkTemplateOutput("True", source, "x", true, "y", true);
		checkTemplateOutput("True", source, "x", 1, "y", true);
		checkTemplateOutput("True", source, "x", true, "y", 2);
		checkTemplateOutput("True", source, "x", 17, "y", 23);
		checkTemplateOutput("False", source, "x", 23, "y", 17);
		checkTemplateOutput("True", source, "x", 17, "y", 17);
		checkTemplateOutput("True", source, "x", 17, "y", 17.0);
		// This checks constant folding
		checkTemplateOutput("True", "<?print 17 <= 23?>");
		checkTemplateOutput("True", "<?print 17 <= 17.?>");
		checkTemplateOutput("True", "<?print 17 <= 23.?>");
		checkTemplateOutput("False", "<?print 18 <= 17.?>");
	}

	@Test
	public void operator_gt()
	{
		String source = "<?print x > y?>";

		checkTemplateOutput("False", source, "x", false, "y", true);
		checkTemplateOutput("False", source, "x", true, "y", true);
		checkTemplateOutput("False", source, "x", 1, "y", true);
		checkTemplateOutput("True", source, "x", 2, "y", true);
		checkTemplateOutput("False", source, "x", 17, "y", 23);
		checkTemplateOutput("True", source, "x", 23, "y", 17);
		checkTemplateOutput("False", source, "x", 17, "y", 17.0);
		checkTemplateOutput("True", source, "x", 23.0, "y", 17);
		// This checks constant folding
		checkTemplateOutput("False", "<?print 17 > 23?>");
		checkTemplateOutput("False", "<?print 17 > 17.?>");
		checkTemplateOutput("False", "<?print 17 > 23.?>");
		checkTemplateOutput("True", "<?print 18 > 17.?>");
	}

	@Test
	public void operator_ge()
	{
		String source = "<?print x >= y?>";

		checkTemplateOutput("False", source, "x", false, "y", true);
		checkTemplateOutput("True", source, "x", true, "y", true);
		checkTemplateOutput("True", source, "x", 1, "y", true);
		checkTemplateOutput("False", source, "x", true, "y", 2);
		checkTemplateOutput("False", source, "x", 17, "y", 23);
		checkTemplateOutput("True", source, "x", 23, "y", 17);
		checkTemplateOutput("True", source, "x", 17, "y", 17);
		checkTemplateOutput("True", source, "x", 17, "y", 17.0);
		// This checks constant folding
		checkTemplateOutput("False", "<?print 17 >= 23?>");
		checkTemplateOutput("True", "<?print 17 >= 17.?>");
		checkTemplateOutput("False", "<?print 17 >= 23.?>");
		checkTemplateOutput("True", "<?print 18 >= 17.?>");
	}

	@Test
	public void operator_contains()
	{
		String source = "<?print x in y?>";

		checkTemplateOutput("True", source, "x", 2, "y", asList(1, 2, 3));
		checkTemplateOutput("False", source, "x", 4, "y", asList(1, 2, 3));
		checkTemplateOutput("True", source, "x", "ur", "y", "gurk");
		checkTemplateOutput("False", source, "x", "un", "y", "gurk");
		checkTemplateOutput("True", source, "x", "a", "y", makeMap("a", 1, "b", 2));
		checkTemplateOutput("False", source, "x", "c", "y", makeMap("a", 1, "b", 2));
		checkTemplateOutput("True", source, "x", 0xff, "y", new Color(0x00, 0x80, 0xff, 0x42));
		checkTemplateOutput("False", source, "x", 0x23, "y", new Color(0x00, 0x80, 0xff, 0x42));
	}

	@Test
	public void operator_notcontains()
	{
		String source = "<?print x not in y?>";

		checkTemplateOutput("False", source, "x", 2, "y", asList(1, 2, 3));
		checkTemplateOutput("True", source, "x", 4, "y", asList(1, 2, 3));
		checkTemplateOutput("False", source, "x", "ur", "y", "gurk");
		checkTemplateOutput("True", source, "x", "un", "y", "gurk");
		checkTemplateOutput("False", source, "x", "a", "y", makeMap("a", 1, "b", 2));
		checkTemplateOutput("True", source, "x", "c", "y", makeMap("a", 1, "b", 2));
		checkTemplateOutput("False", source, "x", 0xff, "y", new Color(0x00, 0x80, 0xff, 0x42));
		checkTemplateOutput("True", source, "x", 0x23, "y", new Color(0x00, 0x80, 0xff, 0x42));
	}

	@Test
	public void operator_and()
	{
		String source = "<?print x and y?>";

		checkTemplateOutput("False", source, "x", false, "y", false);
		checkTemplateOutput("False", source, "x", false, "y", true);
		checkTemplateOutput("0", source, "x", 0, "y", true);
	}

	@Test
	public void operator_or()
	{
		String source = "<?print x or y?>";

		checkTemplateOutput("False", source, "x", false, "y", false);
		checkTemplateOutput("True", source, "x", false, "y", true);
		checkTemplateOutput("42", source, "x", 42, "y", true);
	}

	@Test
	public void operator_not()
	{
		String source = "<?print not x?>";

		checkTemplateOutput("True", source, "x", false);
		checkTemplateOutput("False", source, "x", 42);
	}

	@Test
	public void operator_getitem()
	{
		checkTemplateOutput("u", "<?print 'gurk'[1]?>");
		checkTemplateOutput("u", "<?print x[1]?>", "x", "gurk");
		checkTemplateOutput("u", "<?print 'gurk'[-3]?>");
		checkTemplateOutput("u", "<?print x[-3]?>", "x", "gurk");
	}

	@CauseTest(expectedCause=StringIndexOutOfBoundsException.class)
	public void operator_getitem_fail_1()
	{
		checkTemplateOutput("u", "<?print 'gurk'[4]?>");
	}

	@CauseTest(expectedCause=StringIndexOutOfBoundsException.class)
	public void operator_getitem_fail_2()
	{
		checkTemplateOutput("u", "<?print x[4]?>", "x", "gurk");
	}

	@CauseTest(expectedCause=StringIndexOutOfBoundsException.class)
	public void operator_getitem_fail_3()
	{
		checkTemplateOutput("u", "<?print 'gurk'[-5]?>");
	}

	@CauseTest(expectedCause=StringIndexOutOfBoundsException.class)
	public void operator_getitem_fail_4()
	{
		checkTemplateOutput("u", "<?print x[-5]?>", "x", "gurk");
	}

	@Test
	public void operator_getslice()
	{
		checkTemplateOutput("ur", "<?print 'gurk'[1:3]?>");
		checkTemplateOutput("ur", "<?print x[1:3]?>", "x", "gurk");
		checkTemplateOutput("ur", "<?print 'gurk'[-3:-1]?>");
		checkTemplateOutput("ur", "<?print x[-3:-1]?>", "x", "gurk");
		checkTemplateOutput("", "<?print 'gurk'[4:10]?>");
		checkTemplateOutput("", "<?print x[4:10]?>", "x", "gurk");
		checkTemplateOutput("", "<?print 'gurk'[-10:-5]?>");
		checkTemplateOutput("", "<?print x[-10:-5]?>", "x", "gurk");
		checkTemplateOutput("urk", "<?print 'gurk'[1:]?>");
		checkTemplateOutput("urk", "<?print x[1:]?>", "x", "gurk");
		checkTemplateOutput("urk", "<?print 'gurk'[-3:]?>");
		checkTemplateOutput("urk", "<?print x[-3:]?>", "x", "gurk");
		checkTemplateOutput("", "<?print 'gurk'[4:]?>");
		checkTemplateOutput("", "<?print x[4:]?>", "x", "gurk");
		checkTemplateOutput("gurk", "<?print 'gurk'[-10:]?>");
		checkTemplateOutput("gurk", "<?print x[-10:]?>", "x", "gurk");
		checkTemplateOutput("gur", "<?print 'gurk'[:3]?>");
		checkTemplateOutput("gur", "<?print x[:3]?>", "x", "gurk");
		checkTemplateOutput("gur", "<?print 'gurk'[:-1]?>");
		checkTemplateOutput("gur", "<?print x[:-1]?>", "x", "gurk");
		checkTemplateOutput("gurk", "<?print 'gurk'[:10]?>");
		checkTemplateOutput("gurk", "<?print x[:10]?>", "x", "gurk");
		checkTemplateOutput("", "<?print 'gurk'[:-5]?>");
		checkTemplateOutput("", "<?print x[:-5]?>", "x", "gurk");
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
		checkTemplateOutput(expected, "<?print " + sc + "?>");
		checkTemplateOutput(expected, "<?print " + sv + "?>", "x", 4);
	}

	@Test
	public void precedence()
	{
		checkTemplateOutput("10", "<?print 2*3+4?>");
		checkTemplateOutput("14", "<?print 2+3*4?>");
		checkTemplateOutput("20", "<?print (2+3)*4?>");
		checkTemplateOutput("10", "<?print -2+-3*-4?>");
		checkTemplateOutput("14", "<?print --2+--3*--4?>");
		checkTemplateOutput("14", "<?print (-(-2))+(-((-3)*-(-4)))?>");
		checkTemplateOutput("42", "<?print 2*data.value?>", "data", makeMap("value", 21));
		checkTemplateOutput("42", "<?print data.value[0]?>", "data", makeMap("value", asList(42)));
		checkTemplateOutput("42", "<?print data[0].value?>", "data", asList(makeMap("value", 42)));
		checkTemplateOutput("42", "<?print data[0][0][0]?>", "data", asList(asList(asList(42))));
		checkTemplateOutput("42", "<?print data.value.value[0]?>", "data", makeMap("value", makeMap("value", asList(42))));
		checkTemplateOutput("42", "<?print data.value.value[0].value.value[0]?>", "data", makeMap("value", makeMap("value", asList(makeMap("value", makeMap("value", asList(42)))))));
	}

	@Test
	public void associativity()
	{
		checkTemplateOutput("9", "<?print 2+3+4?>");
		checkTemplateOutput("-5", "<?print 2-3-4?>");
		checkTemplateOutput("24", "<?print 2*3*4?>");
		checkTemplateOutput("2.0", "<?print 24/6/2?>");
		checkTemplateOutput("2", "<?print 24//6//2?>");
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

		checkTemplateOutput("42", "<?print " + sc + "?>");
		checkTemplateOutput("42", "<?print " + sv + "?>", "x", 42);
	}

	@Test
	public void function_now()
	{
		String output = getTemplateOutput("<?print now()?>");
		assertTrue(output.compareTo("2012-03-28") > 0);
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_now_1_args()
	{
		checkTemplateOutput("", "<?print now(1)?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_now_2_args()
	{
		checkTemplateOutput("", "<?print now(1, 2)?>");
	}

	@Test
	public void function_utcnow()
	{
		String output = getTemplateOutput("<?print utcnow()?>");
		assertTrue(output.compareTo("2012-03-28") > 0);
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_utcnow_1_args()
	{
		checkTemplateOutput("", "<?print utcnow(1)?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_utcnow_2_args()
	{
		checkTemplateOutput("", "<?print utcnow(1, 2)?>");
	}

	@Test
	public void function_vars()
	{
		String source = "<?if var in vars()?>yes<?else?>no<?end if?>";

		checkTemplateOutput("yes", source, "var", "spam", "spam", "eggs");
		checkTemplateOutput("no", source, "var", "nospam", "spam", "eggs");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_vars_1_args()
	{
		checkTemplateOutput("", "<?print vars(1)?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_vars_2_args()
	{
		checkTemplateOutput("", "<?print vars(1, 2)?>");
	}

	@Test
	public void function_random()
	{
		checkTemplateOutput("ok", "<?code r = random()?><?if r>=0 and r<1?>ok<?else?>fail<?end if?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_random_0_args()
	{
		checkTemplateOutput("", "<?print random(1)?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_random_2_args()
	{
		checkTemplateOutput("", "<?print random(1, 2)?>");
	}

	@Test
	public void function_randrange()
	{
		checkTemplateOutput("ok", "<?code r = randrange(4)?><?if r>=0 and r<4?>ok<?else?>fail<?end if?>");
		checkTemplateOutput("ok", "<?code r = randrange(17, 23)?><?if r>=17 and r<23?>ok<?else?>fail<?end if?>");
		checkTemplateOutput("ok", "<?code r = randrange(17, 23, 2)?><?if r>=17 and r<23 and r%2?>ok<?else?>fail<?end if?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_randrange_0_args()
	{
		checkTemplateOutput("", "<?print randrange()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_randrange_4_args()
	{
		checkTemplateOutput("", "<?print randrange(1, 2, 3, 4)?>");
	}

	@Test
	public void function_randchoice()
	{
		checkTemplateOutput("ok", "<?code r = randchoice('abc')?><?if r in 'abc'?>ok<?else?>fail<?end if?>");
		checkTemplateOutput("ok", "<?code s = [17, 23, 42]?><?code r = randchoice(s)?><?if r in s?>ok<?else?>fail<?end if?>");
		checkTemplateOutput("ok", "<?code s = #12345678?><?code sl = [0x12, 0x34, 0x56, 0x78]?><?code r = randchoice(s)?><?if r in sl?>ok<?else?>fail<?end if?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	@Test
	public void function_randchoice_0_args()
	{
		checkTemplateOutput("", "<?print randchoice()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	@Test
	public void function_randchoice_2_args()
	{
		checkTemplateOutput("", "<?print randchoice(1, 2)?>");
	}

	@Test
	public void function_xmlescape()
	{
		checkTemplateOutput("&lt;&lt;&gt;&gt;&amp;&#39;&quot;gurk", "<?print xmlescape(data)?>", "data", "<<>>&'\"gurk");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	@Test
	public void function_xmlescape_0_args()
	{
		checkTemplateOutput("", "<?print xmlescape()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	@Test
	public void function_xmlescape_2_args()
	{
		checkTemplateOutput("", "<?print xmlescape(1, 2)?>");
	}

	@Test
	public void function_csv()
	{
		checkTemplateOutput("", "<?print csv(data)?>", "data", null);
		checkTemplateOutput("False", "<?print csv(data)?>", "data", false);
		checkTemplateOutput("True", "<?print csv(data)?>", "data", true);
		checkTemplateOutput("42", "<?print csv(data)?>", "data", 42);
		// no check for float
		checkTemplateOutput("abc", "<?print csv(data)?>", "data", "abc");
		checkTemplateOutput("\"a,b,c\"", "<?print csv(data)?>", "data", "a,b,c");
		checkTemplateOutput("\"a\"\"b\"\"c\"", "<?print csv(data)?>", "data", "a\"b\"c");
		checkTemplateOutput("\"a\nb\nc\"", "<?print csv(data)?>", "data", "a\nb\nc");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	@Test
	public void function_csv_0_args()
	{
		checkTemplateOutput("", "<?print csv()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	@Test
	public void function_csv_2_args()
	{
		checkTemplateOutput("", "<?print csv(1, 2)?>");
	}

	@Test
	public void function_json()
	{
		checkTemplateOutput("null", "<?print json(data)?>", "data", null);
		checkTemplateOutput("false", "<?print json(data)?>", "data", false);
		checkTemplateOutput("true", "<?print json(data)?>", "data", true);
		checkTemplateOutput("42", "<?print json(data)?>", "data", 42);
		// no check for float
		checkTemplateOutput("\"abc\"", "<?print json(data)?>", "data", "abc");
		checkTemplateOutput("[1, 2, 3]", "<?print json(data)?>", "data", asList(1, 2, 3));
		checkTemplateOutput("{\"one\": 1}", "<?print json(data)?>", "data", makeMap("one", 1));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	@Test
	public void function_json_0_args()
	{
		checkTemplateOutput("", "<?print json()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	@Test
	public void function_json_2_args()
	{
		checkTemplateOutput("", "<?print json(1, 2)?>");
	}

	@Test
	public void function_ul4on()
	{
		checkTemplateOutput(dumps(null), "<?print ul4on(data)?>", "data", null);
		checkTemplateOutput(dumps(false), "<?print ul4on(data)?>", "data", false);
		checkTemplateOutput(dumps(true), "<?print ul4on(data)?>", "data", true);
		checkTemplateOutput(dumps(42), "<?print ul4on(data)?>", "data", 42);
		checkTemplateOutput(dumps(42.5), "<?print ul4on(data)?>", "data", 42.5);
		checkTemplateOutput(dumps("abc"), "<?print ul4on(data)?>", "data", "abc");
		checkTemplateOutput(dumps(asList(1, 2, 3)), "<?print ul4on(data)?>", "data", asList(1, 2, 3));
		checkTemplateOutput(dumps(makeMap("one", 1)), "<?print ul4on(data)?>", "data", makeMap("one", 1));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	@Test
	public void function_ul4on_0_args()
	{
		checkTemplateOutput("", "<?print ul4on()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	@Test
	public void function_ul4on_2_args()
	{
		checkTemplateOutput("", "<?print ul4on(1, 2)?>");
	}

	@Test
	public void function_str()
	{
		String source = "<?print str(data)?>";

		checkTemplateOutput("", "<?print str()?>");
		checkTemplateOutput("", source, "data", null);
		checkTemplateOutput("True", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("42", source, "data", 42);
		checkTemplateOutput("4.2", source, "data", 4.2);
		checkTemplateOutput("foo", source, "data", "foo");
		checkTemplateOutput("2011-02-09", source, "data", makeDate(2011, 2, 9));
		checkTemplateOutput("2011-02-09 12:34:56", source, "data", makeDate(2011, 2, 9, 12, 34, 56));
		checkTemplateOutput("2011-02-09 12:34:56.987000", source, "data", makeDate(2011, 2, 9, 12, 34, 56, 987000));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	@Test
	public void function_str_2_args()
	{
		checkTemplateOutput("", "<?print str(1, 2)?>");
	}

	@Test
	public void function_bool()
	{
		checkTemplateOutput("False", "<?print bool()?>");
		checkTemplateOutput("True", "<?print bool(data)?>", "data", true);
		checkTemplateOutput("False", "<?print bool(data)?>", "data", false);
		checkTemplateOutput("False", "<?print bool(data)?>", "data", 0);
		checkTemplateOutput("True", "<?print bool(data)?>", "data", 42);
		checkTemplateOutput("False", "<?print bool(data)?>", "data", 0.0);
		checkTemplateOutput("True", "<?print bool(data)?>", "data", 4.2);
		checkTemplateOutput("False", "<?print bool(data)?>", "data", "");
		checkTemplateOutput("True", "<?print bool(data)?>", "data", "foo");
		checkTemplateOutput("False", "<?print bool(data)?>", "data", asList());
		checkTemplateOutput("True", "<?print bool(data)?>", "data", asList("foo", "bar"));
		checkTemplateOutput("False", "<?print bool(data)?>", "data", makeMap());
		checkTemplateOutput("True", "<?print bool(data)?>", "data", makeMap("foo", "bar"));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	@Test
	public void function_bool_2_args()
	{
		checkTemplateOutput("", "<?print bool(1, 2)?>");
	}

	@Test
	public void function_int()
	{
		checkTemplateOutput("0", "<?print int()?>");
		checkTemplateOutput("1", "<?print int(data)?>", "data", true);
		checkTemplateOutput("0", "<?print int(data)?>", "data", false);
		checkTemplateOutput("42", "<?print int(data)?>", "data", 42);
		checkTemplateOutput("4", "<?print int(data)?>", "data", 4.2);
		checkTemplateOutput("42", "<?print int(data)?>", "data", "42");
		checkTemplateOutput("66", "<?print int(data, 16)?>", "data", "42");
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_int_null()
	{
		checkTemplateOutput("", "<?print int(data)?>", "data", null);
	}

	@CauseTest(expectedCause=NumberFormatException.class)
	public void function_int_badstring()
	{
		checkTemplateOutput("", "<?print int(data)?>", "data", "foo");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	@Test
	public void function_int_3_args()
	{
		checkTemplateOutput("", "<?print int(1, 2, 3)?>");
	}

	@Test
	public void function_float()
	{
		String source = "<?print float(data)?>";

		checkTemplateOutput("0.0", "<?print float()?>");
		checkTemplateOutput("1.0", source, "data", true);
		checkTemplateOutput("0.0", source, "data", false);
		checkTemplateOutput("42.0", source, "data", 42);
		checkTemplateOutput("42.0", source, "data", "42");
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_float_null()
	{
		checkTemplateOutput("", "<?print float(data)?>", "data", null);
	}

	@CauseTest(expectedCause=NumberFormatException.class)
	public void function_float_badstring()
	{
		checkTemplateOutput("", "<?print float(data)?>", "data", "foo");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_float_2_args()
	{
		checkTemplateOutput("", "<?print float(1, 2)?>");
	}

	@Test
	public void function_len()
	{
		String source = "<?print len(data)?>";

		checkTemplateOutput("3", source, "data", "foo");
		checkTemplateOutput("3", source, "data", asList(1, 2, 3));
		checkTemplateOutput("3", source, "data", makeMap("a", 1, "b", 2, "c", 3));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_len_0_args()
	{
		checkTemplateOutput("", "<?print len()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_len_2_args()
	{
		checkTemplateOutput("", "<?print len(1, 2)?>");
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_len_null()
	{
		checkTemplateOutput("", "<?print len(data)?>", "data", null);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_len_true()
	{
		checkTemplateOutput("", "<?print len(data)?>", "data", true);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_len_false()
	{
		checkTemplateOutput("", "<?print len(data)?>", "data", false);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_len_int()
	{
		checkTemplateOutput("", "<?print len(data)?>", "data", 42);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_len_float()
	{
		checkTemplateOutput("", "<?print len(data)?>", "data", 42.4);
	}

	@Test
	public void function_enumerate()
	{
		String source1 = "<?for (i, value) in enumerate(data)?>(<?print value?>=<?print i?>)<?end for?>";

		checkTemplateOutput("(f=0)(o=1)(o=2)", source1, "data", "foo");
		checkTemplateOutput("(foo=0)(bar=1)", source1, "data", asList("foo", "bar"));
		checkTemplateOutput("(foo=0)", source1, "data", makeMap("foo", true));

		String source2 = "<?for (i, value) in enumerate(data, 42)?>(<?print value?>=<?print i?>)<?end for?>";

		checkTemplateOutput("(f=42)(o=43)(o=44)", source2, "data", "foo");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_enumerate_0_args()
	{
		checkTemplateOutput("", "<?print enumerate()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_enumerate_3_args()
	{
		checkTemplateOutput("", "<?print enumerate(1, 2, 3)?>");
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_enumerate_null()
	{
		checkTemplateOutput("", "<?print enumerate(data)?>", "data", null);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_enumerate_true()
	{
		checkTemplateOutput("", "<?print enumerate(data)?>", "data", true);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_enumerate_false()
	{
		checkTemplateOutput("", "<?print enumerate(data)?>", "data", false);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_enumerate_int()
	{
		checkTemplateOutput("", "<?print enumerate(data)?>", "data", 42);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_enumerate_float()
	{
		checkTemplateOutput("", "<?print enumerate(data)?>", "data", 42.4);
	}

	@Test
	public void function_enumfl()
	{
		String source1 = "<?for (i, f, l, value) in enumfl(data)?><?if f?>[<?end if?>(<?print value?>=<?print i?>)<?if l?>]<?end if?><?end for?>";

		checkTemplateOutput("", source1, "data", "");
		checkTemplateOutput("[(?=0)]", source1, "data", "?");
		checkTemplateOutput("[(f=0)(o=1)(o=2)]", source1, "data", "foo");
		checkTemplateOutput("[(foo=0)(bar=1)]", source1, "data", asList("foo", "bar"));
		checkTemplateOutput("[(foo=0)]", source1, "data", makeMap("foo", true));

		String source2 = "<?for (i, f, l, value) in enumfl(data, 42)?><?if f?>[<?end if?>(<?print value?>=<?print i?>)<?if l?>]<?end if?><?end for?>";

		checkTemplateOutput("[(f=42)(o=43)(o=44)]", source2, "data", "foo");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_enumfl_0_args()
	{
		checkTemplateOutput("", "<?print enumfl()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_enumfl_3_args()
	{
		checkTemplateOutput("", "<?print enumfl(1, 2, 3)?>");
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_enumfl_null()
	{
		checkTemplateOutput("", "<?print enumfl(data)?>", "data", null);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_enumfl_true()
	{
		checkTemplateOutput("", "<?print enumfl(data)?>", "data", true);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_enumfl_false()
	{
		checkTemplateOutput("", "<?print enumfl(data)?>", "data", false);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_enumfl_int()
	{
		checkTemplateOutput("", "<?print enumfl(data)?>", "data", 42);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_enumfl_float()
	{
		checkTemplateOutput("", "<?print enumfl(data)?>", "data", 42.4);
	}

	@Test
	public void function_isfirstlast()
	{
		String source = "<?for (f, l, value) in isfirstlast(data)?><?if f?>[<?end if?>(<?print value?>)<?if l?>]<?end if?><?end for?>";

		checkTemplateOutput("", source, "data", "");
		checkTemplateOutput("[(?)]", source, "data", "?");
		checkTemplateOutput("[(f)(o)(o)]", source, "data", "foo");
		checkTemplateOutput("[(foo)(bar)]", source, "data", asList("foo", "bar"));
		checkTemplateOutput("[(foo)]", source, "data", makeMap("foo", true));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isfirstlast_0_args()
	{
		checkTemplateOutput("", "<?print isfirstlast()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isfirstlast_2_args()
	{
		checkTemplateOutput("", "<?print isfirstlast(1, 2)?>");
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_isfirstlast_null()
	{
		checkTemplateOutput("", "<?print isfirstlast(data)?>", "data", null);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_isfirstlast_true()
	{
		checkTemplateOutput("", "<?print isfirstlast(data)?>", "data", true);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_isfirstlast_false()
	{
		checkTemplateOutput("", "<?print isfirstlast(data)?>", "data", false);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_isfirstlast_int()
	{
		checkTemplateOutput("", "<?print isfirstlast(data)?>", "data", 42);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_isfirstlast_float()
	{
		checkTemplateOutput("", "<?print isfirstlast(data)?>", "data", 42.4);
	}

	@Test
	public void function_isfirst()
	{
		String source = "<?for (f, value) in isfirst(data)?><?if f?>[<?end if?>(<?print value?>)<?end for?>";

		checkTemplateOutput("", source, "data", "");
		checkTemplateOutput("[(?)", source, "data", "?");
		checkTemplateOutput("[(f)(o)(o)", source, "data", "foo");
		checkTemplateOutput("[(foo)(bar)", source, "data", asList("foo", "bar"));
		checkTemplateOutput("[(foo)", source, "data", makeMap("foo", true));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isfirst_0_args()
	{
		checkTemplateOutput("", "<?print isfirst()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isfirst_2_args()
	{
		checkTemplateOutput("", "<?print isfirst(1, 2)?>");
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_isfirst_null()
	{
		checkTemplateOutput("", "<?print isfirst(data)?>", "data", null);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_isfirst_true()
	{
		checkTemplateOutput("", "<?print isfirst(data)?>", "data", true);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_isfirst_false()
	{
		checkTemplateOutput("", "<?print isfirst(data)?>", "data", false);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_isfirst_int()
	{
		checkTemplateOutput("", "<?print isfirst(data)?>", "data", 42);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_isfirst_float()
	{
		checkTemplateOutput("", "<?print isfirst(data)?>", "data", 42.4);
	}

	@Test
	public void function_islast()
	{
		String source = "<?for (l, value) in islast(data)?>(<?print value?>)<?if l?>]<?end if?><?end for?>";

		checkTemplateOutput("", source, "data", "");
		checkTemplateOutput("(?)]", source, "data", "?");
		checkTemplateOutput("(f)(o)(o)]", source, "data", "foo");
		checkTemplateOutput("(foo)(bar)]", source, "data", asList("foo", "bar"));
		checkTemplateOutput("(foo)]", source, "data", makeMap("foo", true));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_islast_0_args()
	{
		checkTemplateOutput("", "<?print islast()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_islast_2_args()
	{
		checkTemplateOutput("", "<?print islast(1, 2)?>");
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_islast_null()
	{
		checkTemplateOutput("", "<?print islast(data)?>", "data", null);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_islast_true()
	{
		checkTemplateOutput("", "<?print islast(data)?>", "data", true);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_islast_false()
	{
		checkTemplateOutput("", "<?print islast(data)?>", "data", false);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_islast_int()
	{
		checkTemplateOutput("", "<?print islast(data)?>", "data", 42);
	}

	@CauseTest(expectedCause=UnsupportedOperationException.class)
	public void function_islast_float()
	{
		checkTemplateOutput("", "<?print islast(data)?>", "data", 42.4);
	}

	@Test
	public void function_isnone()
	{
		String source = "<?print isnone(data)?>";

		checkTemplateOutput("True", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isnone_0_args()
	{
		checkTemplateOutput("", "<?print isnone()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isnone_2_args()
	{
		checkTemplateOutput("", "<?print isnone(1, 2)?>");
	}

	@Test
	public void function_isbool()
	{
		String source = "<?print isbool(data)?>";

		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("True", source, "data", true);
		checkTemplateOutput("True", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isbool_0_args()
	{
		checkTemplateOutput("", "<?print isbool()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isbool_2_args()
	{
		checkTemplateOutput("", "<?print isbool(1, 2)?>");
	}

	@Test
	public void function_isint()
	{
		String source = "<?print isint(data)?>";

		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("True", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isint_0_args()
	{
		checkTemplateOutput("", "<?print isint()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isint_2_args()
	{
		checkTemplateOutput("", "<?print isint(1, 2)?>");
	}

	@Test
	public void function_isfloat()
	{
		String source = "<?print isfloat(data)?>";

		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("True", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isfloat_0_args()
	{
		checkTemplateOutput("", "<?print isfloat()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isfloat_2_args()
	{
		checkTemplateOutput("", "<?print isfloat(1, 2)?>");
	}

	@Test
	public void function_isstr()
	{
		String source = "<?print isstr(data)?>";

		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("True", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isstr_0_args()
	{
		checkTemplateOutput("", "<?print isstr()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isstr_2_args()
	{
		checkTemplateOutput("", "<?print isstr(1, 2)?>");
	}

	@Test
	public void function_isdate()
	{
		String source = "<?print isdate(data)?>";

		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("True", source, "data", new Date());
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isdate_0_args()
	{
		checkTemplateOutput("", "<?print isdate()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isdate_2_args()
	{
		checkTemplateOutput("", "<?print isdate(1, 2)?>");
	}

	@Test
	public void function_islist()
	{
		String source = "<?print islist(data)?>";

		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("True", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_islist_0_args()
	{
		checkTemplateOutput("", "<?print islist()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_islist_2_args()
	{
		checkTemplateOutput("", "<?print islist(1, 2)?>");
	}

	@Test
	public void function_isdict()
	{
		String source = "<?print isdict(data)?>";

		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("True", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isdict_0_args()
	{
		checkTemplateOutput("", "<?print isdict()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_isdict_2_args()
	{
		checkTemplateOutput("", "<?print isdict(1, 2)?>");
	}

	@Test
	public void function_istemplate()
	{
		String source = "<?print istemplate(data)?>";

		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("True", source, "data", getTemplate(""));
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_istemplate_0_args()
	{
		checkTemplateOutput("", "<?print istemplate()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_istemplate_2_args()
	{
		checkTemplateOutput("", "<?print istemplate(1, 2)?>");
	}

	@Test
	public void function_iscolor()
	{
		String source = "<?print iscolor(data)?>";

		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("True", source, "data", new Color(0, 0, 0));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_iscolor_0_args()
	{
		checkTemplateOutput("", "<?print iscolor()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_iscolor_2_args()
	{
		checkTemplateOutput("", "<?print iscolor(1, 2)?>");
	}

	@Test
	public void function_get()
	{
		checkTemplateOutput("", "<?print get('x')?>");
		checkTemplateOutput("42", "<?print get('x')?>", "x", 42);
		checkTemplateOutput("17", "<?print get('x', 17)?>");
		checkTemplateOutput("42", "<?print get('x', 17)?>", "x", 42);
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_get_0_args()
	{
		checkTemplateOutput("", "<?print get()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_get_3_args()
	{
		checkTemplateOutput("", "<?print get(1, 2, 3)?>");
	}

	@Test
	public void function_repr()
	{
		String source = "<?print repr(data)?>";

		checkTemplateOutput("None", source, "data", null);
		checkTemplateOutput("True", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("42", source, "data", 42);
		checkTemplateOutput("42.5", source, "data", 42.5);
		checkTemplateOutput("\"foo\"", source, "data", "foo");
		checkTemplateOutput("[1, 2, 3]", source, "data", asList(1, 2, 3));
		checkTemplateOutput("{\"a\": 1}", source, "data", makeMap("a", 1));
		checkTemplateOutput2("{\"a\": 1, \"b\": 2}", "{\"b\": 2, \"a\": 1}", source, "data", makeMap("a", 1, "b", 2));
		checkTemplateOutput("@(2011-02-07T12:34:56.123000)", source, "data", makeDate(2011, 2, 7, 12, 34, 56, 123000));
		checkTemplateOutput("@(2011-02-07T12:34:56)", source, "data", makeDate(2011, 2, 7, 12, 34, 56));
		checkTemplateOutput("@(2011-02-07)", source, "data", makeDate(2011, 2, 7));
		checkTemplateOutput("@(2011-02-07)", source, "data", makeDate(2011, 2, 7));
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_repr_0_args()
	{
		checkTemplateOutput("", "<?print repr()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_repr_2_args()
	{
		checkTemplateOutput("", "<?print repr(1, 2)?>");
	}

	@Test
	public void function_format()
	{
		Date t = makeDate(2011, 2, 6, 12, 34, 56, 987000);

		String source = "<?print format(data, format)?>";

		checkTemplateOutput("2011", source, "format", "%Y", "data", t);
		checkTemplateOutput("02", source, "format", "%m", "data", t);
		checkTemplateOutput("06", source, "format", "%d", "data", t);
		checkTemplateOutput("12", source, "format", "%H", "data", t);
		checkTemplateOutput("34", source, "format", "%M", "data", t);
		checkTemplateOutput("56", source, "format", "%S", "data", t);
		checkTemplateOutput("987000", source, "format", "%f", "data", t);
		//checkTemplateOutput("Sun", source, "format", "%a", "data", t);
		//checkTemplateOutput("Sunday", source, "format", "%A", "data", t);
		checkTemplateOutput("Feb", source, "format", "%b", "data", t);
		//checkTemplateOutput("February", source, "format", "%B", "data", t);
		checkTemplateOutput("12", source, "format", "%I", "data", t);
		checkTemplateOutput("037", source, "format", "%j", "data", t);
		checkTemplateOutput("PM", source, "format", "%p", "data", t);
		checkTemplateOutput("06", source, "format", "%U", "data", t);
		checkTemplateOutput("0", source, "format", "%w", "data", t);
		checkTemplateOutput("05", source, "format", "%W", "data", t);
		checkTemplateOutput("11", source, "format", "%y", "data", t);
		//checkTemplateOutput("Sun Feb  6 12:34:56 2011", source, "format", "%c", "data", t);
		checkTemplateOutput("02/06/11", source, "format", "%x", "data", t);
		checkTemplateOutput("12:34:56", source, "format", "%X", "data", t);
		checkTemplateOutput("%", source, "format", "%%", "data", t);
	}

	@Test
	public void function_chr()
	{
		String source = "<?print chr(data)?>";

		checkTemplateOutput("\u0000", source, "data", 0);
		checkTemplateOutput("a", source, "data", (int)'a');
		checkTemplateOutput("\u20ac", source, "data", 0x20ac);
	}

	@Test
	public void function_ord()
	{
		String source = "<?print ord(data)?>";

		checkTemplateOutput("0", source, "data", "\u0000");
		checkTemplateOutput("97", source, "data", "a");
		checkTemplateOutput("8364", source, "data", "\u20ac");
	}

	@Test
	public void function_hex()
	{
		String source = "<?print hex(data)?>";

		checkTemplateOutput("0x0", source, "data", 0);
		checkTemplateOutput("0xff", source, "data", 0xff);
		checkTemplateOutput("0xffff", source, "data", 0xffff);
		checkTemplateOutput("-0xffff", source, "data", -0xffff);
	}

	@Test
	public void function_oct()
	{
		String source = "<?print oct(data)?>";

		checkTemplateOutput("0o0", source, "data", 0);
		checkTemplateOutput("0o77", source, "data", 077);
		checkTemplateOutput("0o7777", source, "data", 07777);
		checkTemplateOutput("-0o7777", source, "data", -07777);
	}

	@Test
	public void function_bin()
	{
		String source = "<?print bin(data)?>";

		checkTemplateOutput("0b0", source, "data", 0);
		checkTemplateOutput("0b11", source, "data", 3);
		checkTemplateOutput("-0b1111", source, "data", -15);
	}

	@Test
	public void function_abs()
	{
		String source = "<?print abs(data)?>";

		checkTemplateOutput("0", source, "data", 0);
		checkTemplateOutput("42", source, "data", 42);
		checkTemplateOutput("42", source, "data", -42);
	}

	@Test
	public void function_sorted()
	{
		String source = "<?for i in sorted(data)?><?print i?><?end for?>";

		checkTemplateOutput("gkru", source, "data", "gurk");
		checkTemplateOutput("24679", source, "data", "92746");
		checkTemplateOutput("172342", source, "data", asList(42, 17, 23));
		checkTemplateOutput("012", source, "data", makeMap(0, "zero", 1, "one", 2, "two"));
	}

	@Test
	public void function_range()
	{
		String source1 = "<?for i in range(data)?><?print i?>;<?end for?>";
		String source2 = "<?for i in range(data[0], data[1])?><?print i?>;<?end for?>";
		String source3 = "<?for i in range(data[0], data[1], data[2])?><?print i?>;<?end for?>";

		checkTemplateOutput("", source1, "data", -10);
		checkTemplateOutput("", source1, "data", 0);
		checkTemplateOutput("0;", source1, "data", 1);
		checkTemplateOutput("0;1;2;3;4;", source1, "data", 5);
		checkTemplateOutput("", source2, "data", asList(0, -10));
		checkTemplateOutput("", source2, "data", asList(0, 0));
		checkTemplateOutput("0;1;2;3;4;", source2, "data", asList(0, 5));
		checkTemplateOutput("-5;-4;-3;-2;-1;0;1;2;3;4;", source2, "data", asList(-5, 5));
		checkTemplateOutput("", source3, "data", asList(0, -10, 1));
		checkTemplateOutput("", source3, "data", asList(0, 0, 1));
		checkTemplateOutput("0;2;4;6;8;", source3, "data", asList(0, 10, 2));
		checkTemplateOutput("", source3, "data", asList(0, 10, -2));
		checkTemplateOutput("10;8;6;4;2;", source3, "data", asList(10, 0, -2));
		checkTemplateOutput("", source3, "data", asList(10, 0, 2));
	}

	@Test
	public void function_zip()
	{
		String source2 = "<?for (ix, iy) in zip(x, y)?><?print ix?>-<?print iy?>;<?end for?>";
		String source3 = "<?for (ix, iy, iz) in zip(x, y, z)?><?print ix?>-<?print iy?>+<?print iz?>;<?end for?>";

		checkTemplateOutput("", source2, "x", asList(), "y", asList());
		checkTemplateOutput("1-3;2-4;", source2, "x", asList(1, 2), "y", asList(3, 4));
		checkTemplateOutput("1-4;2-5;", source2, "x", asList(1, 2, 3), "y", asList(4, 5));
		checkTemplateOutput("", source3, "x", asList(), "y", asList(), "z", asList());
		checkTemplateOutput("1-3+5;2-4+6;", source3, "x", asList(1, 2), "y", asList(3, 4), "z", asList(5, 6));
		checkTemplateOutput("1-4+6;", source3, "x", asList(1, 2, 3), "y", asList(4, 5), "z", asList(6));
	}

	@Test
	public void function_type()
	{
		String source = "<?print type(data)?>";

		checkTemplateOutput("none", source, "data", null);
		checkTemplateOutput("bool", source, "data", false);
		checkTemplateOutput("bool", source, "data", true);
		checkTemplateOutput("int", source, "data", 42);
		checkTemplateOutput("float", source, "data", 4.2);
		checkTemplateOutput("str", source, "data", "foo");
		checkTemplateOutput("date", source, "data", new Date());
		checkTemplateOutput("list", source, "data", asList(1, 2));
		checkTemplateOutput("dict", source, "data", makeMap(1, 2));
		checkTemplateOutput("template", source, "data", getTemplate(""));
		checkTemplateOutput("color", source, "data", new Color(0, 0, 0));
	}

	@Test
	public void function_reversed()
	{
		String source = "<?for i in reversed(x)?>(<?print i?>)<?end for?>";

		checkTemplateOutput("(3)(2)(1)", source, "x", "123");
		checkTemplateOutput("(3)(2)(1)", source, "x", asList(1, 2, 3));
	}

	@Test
	public void function_rgb()
	{
		checkTemplateOutput("#369", "<?print repr(rgb(0.2, 0.4, 0.6))?>");
		checkTemplateOutput("#369c", "<?print repr(rgb(0.2, 0.4, 0.6, 0.8))?>");
	}

	@Test
	public void function_hls()
	{
		checkTemplateOutput("#fff", "<?print repr(hls(0, 1, 0))?>");
		checkTemplateOutput("#fff0", "<?print repr(hls(0, 1, 0, 0))?>");
	}

	@Test
	public void function_hsv()
	{
		checkTemplateOutput("#fff", "<?print repr(hsv(0, 0, 1))?>");
		checkTemplateOutput("#fff0", "<?print repr(hsv(0, 0, 1, 0))?>");
	}

	@Test
	public void method_upper()
	{
		checkTemplateOutput("GURK", "<?print 'gurk'.upper()?>");
	}

	@Test
	public void method_lower()
	{
		checkTemplateOutput("gurk", "<?print 'GURK'.lower()?>");
	}

	@Test
	public void method_capitalize()
	{
		checkTemplateOutput("Gurk", "<?print 'gURK'.capitalize()?>");
	}

	@Test
	public void method_startswith()
	{
		checkTemplateOutput("True", "<?print 'gurkhurz'.startswith('gurk')?>");
		checkTemplateOutput("False", "<?print 'gurkhurz'.startswith('hurz')?>");
	}

	@Test
	public void method_endswith()
	{
		checkTemplateOutput("True", "<?print 'gurkhurz'.endswith('hurz')?>");
		checkTemplateOutput("False", "<?print 'gurkhurz'.endswith('gurk')?>");
	}

	@Test
	public void method_strip()
	{
		checkTemplateOutput("gurk", "<?print obj.strip()?>", "obj", " \t\r\ngurk \t\r\n");
		checkTemplateOutput("gurk", "<?print obj.strip('xyz')?>", "obj", "xyzzygurkxyzzy");
	}

	@Test
	public void method_lstrip()
	{
		checkTemplateOutput("gurk \t\r\n", "<?print obj.lstrip()?>", "obj", " \t\r\ngurk \t\r\n");
		checkTemplateOutput("gurkxyzzy", "<?print obj.lstrip(arg)?>", "obj", "xyzzygurkxyzzy", "arg", "xyz");
	}

	@Test
	public void method_rstrip()
	{
		checkTemplateOutput(" \t\r\ngurk", "<?print obj.rstrip()?>", "obj", " \t\r\ngurk \t\r\n");
		checkTemplateOutput("xyzzygurk", "<?print obj.rstrip(arg)?>", "obj", "xyzzygurkxyzzy", "arg", "xyz");
	}

	@Test
	public void method_split()
	{
		checkTemplateOutput("(f)(o)(o)", "<?for item in obj.split()?>(<?print item?>)<?end for?>", "obj", " \t\r\nf \t\r\no \t\r\no \t\r\n");
		checkTemplateOutput("(f)(o \t\r\no \t\r\n)", "<?for item in obj.split(None, 1)?>(<?print item?>)<?end for?>", "obj", " \t\r\nf \t\r\no \t\r\no \t\r\n");
		checkTemplateOutput("()(f)(o)(o)()", "<?for item in obj.split(arg)?>(<?print item?>)<?end for?>", "obj", "xxfxxoxxoxx", "arg", "xx");
		checkTemplateOutput("()(f)(oxxoxx)", "<?for item in obj.split(arg, 2)?>(<?print item?>)<?end for?>", "obj", "xxfxxoxxoxx", "arg", "xx");
	}

	@Test
	public void method_rsplit()
	{
		checkTemplateOutput("(f)(o)(o)", "<?for item in obj.rsplit()?>(<?print item?>)<?end for?>", "obj", " \t\r\nf \t\r\no \t\r\no \t\r\n");
		checkTemplateOutput("( \t\r\nf \t\r\no)(o)", "<?for item in obj.rsplit(None, 1)?>(<?print item?>)<?end for?>", "obj", " \t\r\nf \t\r\no \t\r\no \t\r\n");
		checkTemplateOutput("()(f)(o)(o)()", "<?for item in obj.rsplit(arg)?>(<?print item?>)<?end for?>", "obj", "xxfxxoxxoxx", "arg", "xx");
		checkTemplateOutput("(xxfxxo)(o)()", "<?for item in obj.rsplit(arg, 2)?>(<?print item?>)<?end for?>", "obj", "xxfxxoxxoxx", "arg", "xx");
	}

	@Test
	public void method_replace()
	{
		checkTemplateOutput("goork", "<?print 'gurk'.replace('u', 'oo')?>");
	}

	@Test
	public void method_renders()
	{
		InterpretedTemplate t1 = getTemplate("(<?print data?>)", "t1");

		checkTemplateOutput("(GURK)", "<?print t.renders(data='gurk').upper()?>", "t", t1);
		checkTemplateOutput("(GURK)", "<?print t.renders(**{'data': 'gurk'}).upper()?>", "t", t1);

		InterpretedTemplate t2 = getTemplate("(gurk)", "t2");
		checkTemplateOutput("(GURK)", "<?print t.renders().upper()?>", "t", t2);
	}

	@Test
	public void method_render()
	{
		InterpretedTemplate t1 = getTemplate("<?print prefix?><?print data?><?print suffix?>");
		InterpretedTemplate t2 = getTemplate("<?print 'foo'?>");

		checkTemplateOutput("(f)(o)(o)", "<?for c in data?><?render t.render(data=c, prefix='(', suffix=')')?><?end for?>", "t", t1, "data", "foo");
		checkTemplateOutput("(f)(o)(o)", "<?for c in data?><?render t.render(data=c, **{'prefix': '(', 'suffix': ')'})?><?end for?>", "t", t1, "data", "foo");
		checkTemplateOutput("foo", "<?print t.render()?>", "t", t2);
		checkTemplateOutput("foo", "<?print t.render \n\t(\n \t)\n\t ?>", "t", t2);

		checkTemplateOutput("42", "<?render globals.template.render(value=42)?>", "globals", makeMap("template", getTemplate("<?print value?>")));
		checkTemplateOutput("", "<?render globals.template.render(value=42)?>", "globals", makeMap("template", getTemplate("")));
	}

	@Test
	public void method_render_local_vars()
	{
		InterpretedTemplate t = getTemplate("<?code x += 1?><?print x?>");

		checkTemplateOutput("42,43,42", "<?print x?>,<?render t.render(x=x)?>,<?print x?>", "t", t, "x", 42);
	}

	@Test
	public void method_render_localtemplate()
	{
		checkTemplateOutput("foo", "<?def lower?><?print x.lower()?><?end def?><?print lower.renders(x='FOO')?>");
	}

	@Test
	public void method_render_nested()
	{
		checkTemplateOutput("45?44?43", "<?def outer?><?def inner?><?code x += 1?><?print x?>?<?end def?><?code x += 1?><?render inner.render(x=x)?><?print x?>?<?end def?><?code x += 1?><?render outer.render(x=x)?><?print x?>", "x", 42);
	}

	@Test
	public void method_mimeformat()
	{
		Date t = makeDate(2010, 2, 22, 12, 34, 56);
		checkTemplateOutput("Mon, 22 Feb 2010 12:34:56 GMT", "<?print data.mimeformat()?>", "data", t);
	}

	@Test
	public void method_get()
	{
		checkTemplateOutput("42", "<?print {}.get('foo', 42)?>");
		checkTemplateOutput("17", "<?print {'foo': 17}.get('foo', 42)?>");
		checkTemplateOutput("", "<?print {}.get('foo')?>");
		checkTemplateOutput("17", "<?print {'foo': 17}.get('foo')?>");
	}

	@Test
	public void method_r_g_b_a()
	{
		checkTemplateOutput("0x11", "<?code c = #123?><?print hex(c.r())?>");
		checkTemplateOutput("0x22", "<?code c = #123?><?print hex(c.g())?>");
		checkTemplateOutput("0x33", "<?code c = #123?><?print hex(c.b())?>");
		checkTemplateOutput("0xff", "<?code c = #123?><?print hex(c.a())?>");
	}

	@Test
	public void method_hls()
	{
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hls()[0])?>");
		checkTemplateOutput("1", "<?code c = #fff?><?print int(c.hls()[1])?>");
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hls()[2])?>");
	}

	@Test
	public void method_hlsa()
	{
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hlsa()[0])?>");
		checkTemplateOutput("1", "<?code c = #fff?><?print int(c.hlsa()[1])?>");
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hlsa()[2])?>");
		checkTemplateOutput("1", "<?code c = #fff?><?print int(c.hlsa()[3])?>");
	}

	@Test
	public void method_hsv()
	{
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hsv()[0])?>");
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hsv()[1])?>");
		checkTemplateOutput("1", "<?code c = #fff?><?print int(c.hsv()[2])?>");
	}

	@Test
	public void method_hsva()
	{
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hsva()[0])?>");
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hsva()[1])?>");
		checkTemplateOutput("1", "<?code c = #fff?><?print int(c.hsva()[2])?>");
		checkTemplateOutput("1", "<?code c = #fff?><?print int(c.hsva()[3])?>");
	}

	@Test
	public void method_lum()
	{
		checkTemplateOutput("True", "<?print #fff.lum() == 1?>");
	}

	@Test
	public void method_withlum()
	{
		checkTemplateOutput("#fff", "<?print #000.withlum(1)?>");
	}

	@Test
	public void method_witha()
	{
		checkTemplateOutput("#0063a82a", "<?print repr(#0063a8.witha(42))?>");
	}

	@Test
	public void method_join()
	{
		checkTemplateOutput("1,2,3,4", "<?print ','.join('1234')?>");
		checkTemplateOutput("1,2,3,4", "<?print ','.join([1, 2, 3, 4])?>");
	}

	@Test
	public void method_find()
	{
		checkTemplateOutput("-1", "<?print s.find('ks')?>", "s", "gurkgurk");
		checkTemplateOutput("2", "<?print s.find('rk')?>", "s", "gurkgurk");
		checkTemplateOutput("2", "<?print s.find('rk', 2)?>", "s", "gurkgurk");
		checkTemplateOutput("2", "<?print s.find('rk', 2, 4)?>", "s", "gurkgurk");
		checkTemplateOutput("6", "<?print s.find('rk', 4, 8)?>", "s", "gurkgurk");
		checkTemplateOutput("-1", "<?print s.find('rk', 2, 3)?>", "s", "gurkgurk");
		checkTemplateOutput("-1", "<?print s.find('rk', 7)?>", "s", "gurkgurk");
	}

	@Test
	public void method_rfind()
	{
		checkTemplateOutput("-1", "<?print s.rfind('ks')?>", "s", "gurkgurk");
		checkTemplateOutput("6", "<?print s.rfind('rk')?>", "s", "gurkgurk");
		checkTemplateOutput("6", "<?print s.rfind('rk', 2)?>", "s", "gurkgurk");
		checkTemplateOutput("2", "<?print s.rfind('rk', 2, 4)?>", "s", "gurkgurk");
		checkTemplateOutput("6", "<?print s.rfind('rk', 4, 8)?>", "s", "gurkgurk");
		checkTemplateOutput("-1", "<?print s.rfind('rk', 2, 3)?>", "s", "gurkgurk");
		checkTemplateOutput("-1", "<?print s.rfind('rk', 7)?>", "s", "gurkgurk");
	}

	@Test
	public void method_day()
	{
		checkTemplateOutput("12", "<?print @(2010-05-12).day()?>");
		checkTemplateOutput("12", "<?print d.day()?>", "d", makeDate(2010, 5, 12));
	}

	@Test
	public void method_month()
	{
		checkTemplateOutput("5", "<?print @(2010-05-12).month()?>");
		checkTemplateOutput("5", "<?print d.month()?>", "d", makeDate(2010, 5, 12));
	}

	@Test
	public void method_year()
	{
		checkTemplateOutput("5", "<?print @(2010-05-12).month()?>");
		checkTemplateOutput("5", "<?print d.month()?>", "d", makeDate(2010, 5, 12));
	}

	@Test
	public void method_hour()
	{
		checkTemplateOutput("16", "<?print @(2010-05-12T16:47:56).hour()?>");
		checkTemplateOutput("16", "<?print d.hour()?>", "d", makeDate(2010, 5, 12, 16, 47, 56));
	}

	@Test
	public void method_minute()
	{
		checkTemplateOutput("47", "<?print @(2010-05-12T16:47:56).minute()?>");
		checkTemplateOutput("47", "<?print d.minute()?>", "d", makeDate(2010, 5, 12, 16, 47, 56));
	}

	@Test
	public void method_second()
	{
		checkTemplateOutput("56", "<?print @(2010-05-12T16:47:56).second()?>");
		checkTemplateOutput("56", "<?print d.second()?>", "d", makeDate(2010, 5, 12, 16, 47, 56));
	}

	@Test
	public void method_microsecond()
	{
		checkTemplateOutput("123000", "<?print @(2010-05-12T16:47:56.123000).microsecond()?>");
		checkTemplateOutput("123000", "<?print d.microsecond()?>", "d", makeDate(2010, 5, 12, 16, 47, 56, 123000));
	}

	@Test
	public void method_weekday()
	{
		checkTemplateOutput("2", "<?print @(2010-05-12).weekday()?>");
		checkTemplateOutput("2", "<?print d.weekday()?>", "d", makeDate(2010, 5, 12));
	}

	@Test
	public void method_yearday()
	{
		checkTemplateOutput("1", "<?print @(2010-01-01).yearday()?>");
		checkTemplateOutput("366", "<?print @(2008-12-31).yearday()?>");
		checkTemplateOutput("365", "<?print @(2010-12-31).yearday()?>");
		checkTemplateOutput("132", "<?print @(2010-05-12).yearday()?>");
		checkTemplateOutput("132", "<?print @(2010-05-12T16:47:56).yearday()?>");
		checkTemplateOutput("132", "<?print d.yearday()?>", "d", makeDate(2010, 5, 12));
		checkTemplateOutput("132", "<?print d.yearday()?>", "d", makeDate(2010, 5, 12, 16, 47, 56));
	}

	@Test
	public void parse()
	{
		checkTemplateOutput("42", "<?print data.Noner?>", "data", makeMap("Noner", 42));
	}

	@CauseTest(expectedCause=SyntaxException.class)
	public void lexer_error()
	{
		checkTemplateOutput("", "<?print ??>");
	}

	@CauseTest(expectedCause=SyntaxException.class)
	public void parser_error()
	{
		checkTemplateOutput("", "<?print 1++2?>");
	}

	@Test
	public void tag_note()
	{
		checkTemplateOutput("foo", "f<?note This is?>o<?note a comment?>o");
	}

	@Test
	public void templateattributes_1()
	{
		String source = "<?print x?>";
		InterpretedTemplate t = getTemplate(source);

		checkTemplateOutput("<?", "<?print template.startdelim?>", "template", t);
		checkTemplateOutput("?>", "<?print template.enddelim?>", "template", t);
		checkTemplateOutput(source, "<?print template.source?>", "template", t);
		checkTemplateOutput("1", "<?print len(template.content)?>", "template", t);
		checkTemplateOutput("print", "<?print template.content[0].type?>", "template", t);
		checkTemplateOutput(source, "<?print template.content[0].location.tag?>", "template", t);
		checkTemplateOutput("x", "<?print template.content[0].location.code?>", "template", t);
		checkTemplateOutput("var", "<?print template.content[0].obj.type?>", "template", t);
		checkTemplateOutput("x", "<?print template.content[0].obj.name?>", "template", t);
	}

	@Test
	public void templateattributes_2()
	{
		String source = "<?printx 42?>";
		InterpretedTemplate t = getTemplate(source);

		checkTemplateOutput("printx", "<?print template.content[0].type?>", "template", t);
		checkTemplateOutput("int", "<?print template.content[0].obj.type?>", "template", t);
		checkTemplateOutput("42", "<?print template.content[0].obj.value?>", "template", t);
	}

	@Test
	public void templateattributes_localtemplate()
	{
		String source = "<?def lower?><?print t.lower()?><?end def?>";

		checkTemplateOutput(source + "<?print lower.source?>", source + "<?print lower.source?>");
		checkTemplateOutput(source, source + "<?print lower.source[lower.location.starttag:lower.endlocation.endtag]?>");
		checkTemplateOutput("<?print t.lower()?>", source + "<?print lower.source[lower.location.endtag:lower.endlocation.starttag]?>");
		checkTemplateOutput("lower?>", source + "<?print lower.name?>");
	}

	private InterpretedTemplate universaltemplate()
	{
		return getTemplate(
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
			"<?code x = {**{'fortytwo': 42}}?>" +
			"<?code x = y?>" +
			"<?code x += 42?>" +
			"<?code x -= 42?>" +
			"<?code x *= 42?>" +
			"<?code x /= 42?>" +
			"<?code x //= 42?>" +
			"<?code x %= 42?>" +
			"<?code del x?>" +
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
	}

	@Test
	public void template_javaSource()
	{
		universaltemplate().javaSource();
	}

	@Test
	public void template_javascriptSource()
	{
		universaltemplate().javascriptSource();
	}
}
