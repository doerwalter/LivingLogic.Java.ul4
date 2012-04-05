package tests;

import java.util.Date;
import java.util.List;
import static java.util.Arrays.*;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

import com.livinglogic.ul4.Color;
import com.livinglogic.ul4.InterpretedTemplate;
import com.livinglogic.ul4.Compiler;
import static com.livinglogic.utils.MapUtils.*;
import static com.livinglogic.ul4on.Utils.*;
import com.livinglogic.ul4.KeyException;
import com.livinglogic.ul4.BlockException;
import com.livinglogic.ul4.UnknownFunctionException;

@RunWith(CauseTestRunner.class)
public class UL4Test
{
	private static String getTemplateOutput(String source, Object... args) throws org.antlr.runtime.RecognitionException
	{
		InterpretedTemplate template = new InterpretedTemplate(source);
		// System.out.println(template);
		// InterpretedTemplate template = Compiler.compile(source);
		return template.renders(makeMap(args));
	}

	private static void checkTemplateOutput(String expected, String source, Object... args) throws org.antlr.runtime.RecognitionException
	{
		String output = getTemplateOutput(source, args);
		assertEquals(expected, output);
	}

	@Test
	public void text() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("gurk", "gurk");
	}

	@Test
	public void none() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("", "<?print None?>");
		checkTemplateOutput("no", "<?if None?>yes<?else?>no<?end if?>");
	}

	@Test
	public void false_() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("False", "<?print False?>");
		checkTemplateOutput("no", "<?if False?>yes<?else?>no<?end if?>");
	}

	@Test
	public void true_() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("True", "<?print True?>");
		checkTemplateOutput("yes", "<?if True?>yes<?else?>no<?end if?>");
	}

	@Test
	public void int_() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("0", "<?print 0?>");
		checkTemplateOutput("42", "<?print 42?>");
		checkTemplateOutput("-42", "<?print -42?>");
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
	public void float_() throws org.antlr.runtime.RecognitionException
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
	public void str() throws org.antlr.runtime.RecognitionException
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

	// @Test
	// public void date() throws org.antlr.runtime.RecognitionException
	// {
	// 	checkTemplateOutput("2000-02-29", "<?print @2000-02-29T.isoformat()?>");
	// 	checkTemplateOutput("2000-02-29T12:34:00", "<?print @2000-02-29T12:34.isoformat()?>");
	// 	checkTemplateOutput("2000-02-29T12:34:56", "<?print @2000-02-29T12:34:56.isoformat()?>");
	// 	checkTemplateOutput("2000-02-29T12:34:56.987000", "<?print @2000-02-29T12:34:56.987000.isoformat()?>");
	// 	checkTemplateOutput("yes", "<?if @2000-02-29T12:34:56.987654?>yes<?else?>no<?end if?>");
	// }

	@Test
	public void color() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("255,255,255,255", "<?code c = #fff?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("255,255,255,255", "<?code c = #ffffff?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("18,52,86,255", "<?code c = #123456?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("17,34,51,68", "<?code c = #1234?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("18,52,86,120", "<?code c = #12345678?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("yes", "<?if #fff?>yes<?else?>no<?end if?>");
	}

	@Test
	public void list() throws org.antlr.runtime.RecognitionException
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
	public void dict() throws org.antlr.runtime.RecognitionException
	{
		// checkTemplateOutput("", "<?for (key, value) in {}.items()?><?print key?>:<?print value?>\n<?end for?>");
		// checkTemplateOutput("1:2\n", "<?for (key, value) in {1:2}.items()?><?print key?>:<?print value?>\n<?end for?>");
		// checkTemplateOutput("1:2\n", "<?for (key, value) in {1:2,}.items()?><?print key?>:<?print value?>\n<?end for?>");
		// // With duplicate keys, later ones simply overwrite earlier ones
		// checkTemplateOutput("1:3\n", "<?for (key, value) in {1:2, 1: 3}.items()?><?print key?>:<?print value?>\n<?end for?>");
		// // Test **
		// checkTemplateOutput("1:2\n", "<?for (key, value) in {**{1:2}}.items()?><?print key?>:<?print value?>\n<?end for?>");
		// checkTemplateOutput("1:4\n", "<?for (key, value) in {1:1, **{1:2}, 1:3, **{1:4}}.items()?><?print key?>:<?print value?>\n<?end for?>");
		checkTemplateOutput("no", "<?if {}?>yes<?else?>no<?end if?>");
		checkTemplateOutput("yes", "<?if {1:2}?>yes<?else?>no<?end if?>");
	}

	@Test
	public void storevar() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("42", "<?code x = 42?><?print x?>");
		checkTemplateOutput("xyzzy", "<?code x = 'xyzzy'?><?print x?>");
	}

	@Test
	public void addvar() throws org.antlr.runtime.RecognitionException
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
	public void subvar() throws org.antlr.runtime.RecognitionException
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
	public void mulvar() throws org.antlr.runtime.RecognitionException
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
	public void floordivvar() throws org.antlr.runtime.RecognitionException
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
	public void truedivvar() throws org.antlr.runtime.RecognitionException
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
	public void modvar() throws org.antlr.runtime.RecognitionException
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
	public void delvar() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("", "<?code x = 1729?><?code del x?><?print x?>");
	}

	@Test
	public void for_string() throws org.antlr.runtime.RecognitionException
	{
		String source = "<?for c in data?>(<?print c?>)<?end for?>";
		checkTemplateOutput("", source, "data", "");
		checkTemplateOutput("(g)(u)(r)(k)", source, "data", "gurk");
	}

	@Test
	public void for_list() throws org.antlr.runtime.RecognitionException
	{
		String source = "<?for c in data?>(<?print c?>)<?end for?>";
		checkTemplateOutput("", source, "data", java.util.Arrays.asList());
		checkTemplateOutput("(g)(u)(r)(k)", source, "data", java.util.Arrays.asList("g", "u", "r", "k"));
	}

	// @Test
	// public void for_dict() throws org.antlr.runtime.RecognitionException
	// {
	// 	String source = "<?for c in sorted(data)?>(<?print c?>)<?end for?>";
	// 	checkTemplateOutput("", source, "data", makeMap());
	// 	checkTemplateOutput("(a)(b)(c)", source, "data", makeMap("a", 1, "b", 2, "c", 3));
	// }

	@Test
	public void for_nested() throws org.antlr.runtime.RecognitionException
	{
		String source = "<?for list in data?>[<?for n in list?>(<?print n?>)<?end for?>]<?end for?>";
		checkTemplateOutput("[(1)(2)][(3)(4)]", source, "data", java.util.Arrays.asList(java.util.Arrays.asList(1, 2), java.util.Arrays.asList(3, 4)));
	}

	@Test
	public void for_unpacking() throws org.antlr.runtime.RecognitionException
	{
		Object data = java.util.Arrays.asList(
			java.util.Arrays.asList("spam", "eggs", 17, null),
			java.util.Arrays.asList("gurk", "hurz", 23, false),
			java.util.Arrays.asList("hinz", "kunz", 42, true)
		);

		checkTemplateOutput("(spam)(gurk)(hinz)", "<?for (a,) in data?>(<?print a?>)<?end for?>", "data", data);
		checkTemplateOutput("(spam,eggs)(gurk,hurz)(hinz,kunz)", "<?for (a, b) in data?>(<?print a?>,<?print b?>)<?end for?>", "data", data);
		checkTemplateOutput("(spam,eggs,17)(gurk,hurz,23)(hinz,kunz,42)", "<?for (a, b, c) in data?>(<?print a?>,<?print b?>,<?print c?>)<?end for?>", "data", data);
		checkTemplateOutput("(spam,eggs,17,)(gurk,hurz,23,False)(hinz,kunz,42,True)", "<?for (a, b, c, d) in data?>(<?print a?>,<?print b?>,<?print c?>,<?print d?>)<?end for?>", "data", data);
	}

	// @Test
	// public void break_() throws org.antlr.runtime.RecognitionException
	// {
	// 	checkTemplateOutput("1, 2, ", "<?for i in [1,2,3]?><?print i?>, <?if i==2?><?break?><?end if?><?end for?>");
	// }

	// @Test
	// public void break_nested() throws org.antlr.runtime.RecognitionException
	// {
	// 	checkTemplateOutput("1, 1, 2, 1, 2, 3, ", "<?for i in [1,2,3,4]?><?for j in [1,2,3,4]?><?print j?>, <?if j>=i?><?break?><?end if?><?end for?><?if i>=3?><?break?><?end if?><?end for?>");
	// }

	// @Test
	// public void continue_() throws org.antlr.runtime.RecognitionException
	// {
	// 	checkTemplateOutput("1, 3, ", "<?for i in [1,2,3]?><?if i==2?><?continue?><?end if?><?print i?>, <?end for?>");
	// }

	// @Test
	// public void continue_nested() throws org.antlr.runtime.RecognitionException
	// {
	// 	checkTemplateOutput("1, 3, \n1, 3, \n", "<?for i in [1,2,3]?><?if i==2?><?continue?><?end if?><?for j in [1,2,3]?><?if j==2?><?continue?><?end if?><?print j?>, <?end for?>\n<?end for?>");
	// }

	@Test
	public void if_() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("42", "<?if data?><?print data?><?end if?>", "data", 42);
	}

	@Test
	public void else_() throws org.antlr.runtime.RecognitionException
	{
		String source = "<?if data?><?print data?><?else?>no<?end if?>";
		checkTemplateOutput("42", source, "data", 42);
		checkTemplateOutput("no", source, "data", 0);
	}

	// // FIXME: Doesn't work, because of chained exceptions, needs to be split into n tests
	// // @Test(expected=BlockException)
	// // public void block_errors() throws org.antlr.runtime.RecognitionException
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
	// // public void empty() throws org.antlr.runtime.RecognitionException
	// // {
	// // 	checkTemplateOutput("", "<?print?>"); // "expression required"
	// // 	checkTemplateOutput("", "<?if?>"); // "expression required"
	// // 	checkTemplateOutput("", "<?if x?><?elif?><?end if?>"); // "expression required"
	// // 	checkTemplateOutput("", "<?for?>"); // "loop expression required"
	// // 	checkTemplateOutput("", "<?code?>"); // "statement required"
	// // 	checkTemplateOutput("", "<?render?>"); // "render statement required"
	// // }

	@Test
	public void add() throws org.antlr.runtime.RecognitionException
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
		// List addition is not implemented: checkTemplateOutput("(foo)(bar)(gurk)(hurz)", "<?for i in a+b?>(<?print i?>)<?end for?>", "a", java.util.Arrays.asList("foo", "bar"), "b", java.util.Arrays.asList("gurk", "hurz"));
		// This checks constant folding
		checkTemplateOutput("3", "<?print 1+2?>");
		checkTemplateOutput("2", "<?print 1+True?>");
		checkTemplateOutput("3.0", "<?print 1+2.0?>");
	}

	@Test
	public void sub() throws org.antlr.runtime.RecognitionException
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
	public void mul() throws org.antlr.runtime.RecognitionException
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
		checkTemplateOutput("(foo)(bar)(foo)(bar)(foo)(bar)", "<?for i in 3*data?>(<?print i?>)<?end for?>", "data", java.util.Arrays.asList("foo", "bar"));
		// This checks constant folding
		checkTemplateOutput("391", "<?print 17*23?>");
		checkTemplateOutput("17", "<?print 17*True?>");
		checkTemplateOutput("391.0", "<?print 17.0*23.0?>");
	}

	@Test
	public void truediv() throws org.antlr.runtime.RecognitionException
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
	public void floordiv() throws org.antlr.runtime.RecognitionException
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
	public void mod() throws org.antlr.runtime.RecognitionException
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
	public void eq() throws org.antlr.runtime.RecognitionException
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
	public void ne() throws org.antlr.runtime.RecognitionException
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
	public void lt() throws org.antlr.runtime.RecognitionException
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
	public void le() throws org.antlr.runtime.RecognitionException
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
	public void gt() throws org.antlr.runtime.RecognitionException
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
	public void ge() throws org.antlr.runtime.RecognitionException
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
	public void contains() throws org.antlr.runtime.RecognitionException
	{
		String source = "<?print x in y?>";

		checkTemplateOutput("True", source, "x", 2, "y", java.util.Arrays.asList(1, 2, 3));
		checkTemplateOutput("False", source, "x", 4, "y", java.util.Arrays.asList(1, 2, 3));
		checkTemplateOutput("True", source, "x", "ur", "y", "gurk");
		checkTemplateOutput("False", source, "x", "un", "y", "gurk");
		checkTemplateOutput("True", source, "x", "a", "y", makeMap("a", 1, "b", 2));
		checkTemplateOutput("False", source, "x", "c", "y", makeMap("a", 1, "b", 2));
		checkTemplateOutput("True", source, "x", 0xff, "y", new Color(0x00, 0x80, 0xff, 0x42));
		checkTemplateOutput("False", source, "x", 0x23, "y", new Color(0x00, 0x80, 0xff, 0x42));
	}

	@Test
	public void notcontains() throws org.antlr.runtime.RecognitionException
	{
		String source = "<?print x not in y?>";

		checkTemplateOutput("False", source, "x", 2, "y", java.util.Arrays.asList(1, 2, 3));
		checkTemplateOutput("True", source, "x", 4, "y", java.util.Arrays.asList(1, 2, 3));
		checkTemplateOutput("False", source, "x", "ur", "y", "gurk");
		checkTemplateOutput("True", source, "x", "un", "y", "gurk");
		checkTemplateOutput("False", source, "x", "a", "y", makeMap("a", 1, "b", 2));
		checkTemplateOutput("True", source, "x", "c", "y", makeMap("a", 1, "b", 2));
		checkTemplateOutput("False", source, "x", 0xff, "y", new Color(0x00, 0x80, 0xff, 0x42));
		checkTemplateOutput("True", source, "x", 0x23, "y", new Color(0x00, 0x80, 0xff, 0x42));
	}

	@Test
	public void and() throws org.antlr.runtime.RecognitionException
	{
		String source = "<?print x and y?>";

		checkTemplateOutput("False", source, "x", false, "y", false);
		checkTemplateOutput("False", source, "x", false, "y", true);
		checkTemplateOutput("0", source, "x", 0, "y", true);
	}

	@Test
	public void or() throws org.antlr.runtime.RecognitionException
	{
		String source = "<?print x or y?>";

		checkTemplateOutput("False", source, "x", false, "y", false);
		checkTemplateOutput("True", source, "x", false, "y", true);
		checkTemplateOutput("42", source, "x", 42, "y", true);
	}

	@Test
	public void not() throws org.antlr.runtime.RecognitionException
	{
		String source = "<?print not x?>";

		checkTemplateOutput("True", source, "x", false);
		checkTemplateOutput("False", source, "x", 42);
	}

	@Test
	public void getitem() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("u", "<?print 'gurk'[1]?>");
		checkTemplateOutput("u", "<?print x[1]?>", "x", "gurk");
		checkTemplateOutput("u", "<?print 'gurk'[-3]?>");
		checkTemplateOutput("u", "<?print x[-3]?>", "x", "gurk");
	}

	@CauseTest(expectedCause=StringIndexOutOfBoundsException.class)
	public void getitem1() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("u", "<?print 'gurk'[4]?>");
	}

	@CauseTest(expectedCause=StringIndexOutOfBoundsException.class)
	public void getitem2() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("u", "<?print x[4]?>", "x", "gurk");
	}

	@CauseTest(expectedCause=StringIndexOutOfBoundsException.class)
	public void getitem3() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("u", "<?print 'gurk'[-5]?>");
	}

	@CauseTest(expectedCause=StringIndexOutOfBoundsException.class)
	public void getitem4() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("u", "<?print x[-5]?>", "x", "gurk");
	}

	@Test
	public void getslice() throws org.antlr.runtime.RecognitionException
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
	public void nested() throws org.antlr.runtime.RecognitionException
	{
		String sc = "4";
		String sv = "x";
		int n = 4;
		// when using 10 compiling the variable will run out of registers
		int depth = 9;
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

	// @Test
	// public void precedence() throws org.antlr.runtime.RecognitionException
	// {
	// 	checkTemplateOutput("14", "<?print 2+3*4?>");
	// 	checkTemplateOutput("20", "<?print (2+3)*4?>");
	// 	checkTemplateOutput("10", "<?print -2+-3*-4?>");
	// 	checkTemplateOutput("14", "<?print --2+--3*--4?>");
	// 	checkTemplateOutput("14", "<?print (-(-2))+(-((-3)*-(-4)))?>");
	// 	checkTemplateOutput("42", "<?print 2*data.value?>", "data", makeMap("value", 21));
	// 	checkTemplateOutput("42", "<?print data.value[0]?>", "data", makeMap("value", java.util.Arrays.asList(42)));
	// 	checkTemplateOutput("42", "<?print data[0].value?>", "data", java.util.Arrays.asList(makeMap("value", 42)));
	// 	checkTemplateOutput("42", "<?print data[0][0][0]?>", "data", java.util.Arrays.asList(java.util.Arrays.asList(java.util.Arrays.asList(42))));
	// 	checkTemplateOutput("42", "<?print data.value.value[0]?>", "data", makeMap("value", makeMap("value", java.util.Arrays.asList(42))));
	// 	checkTemplateOutput("42", "<?print data.value.value[0].value.value[0]?>", "data", makeMap("value", makeMap("value", java.util.Arrays.asList(makeMap("value", makeMap("value", java.util.Arrays.asList(42)))))));
	// }

	@Test
	public void bracket() throws org.antlr.runtime.RecognitionException
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
	public void now() throws org.antlr.runtime.RecognitionException
	{
		String output = getTemplateOutput("<?print now()?>");
		assertTrue(output.compareTo("2012-03-28") > 0);
	}

	@CauseTest(expectedCause=UnknownFunctionException.class)
	public void now1() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("", "<?print now(1)?>");
	}

	@CauseTest(expectedCause=UnknownFunctionException.class)
	public void now2() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("", "<?print now(1, 2)?>");
	}

	@Test
	public void utcnow() throws org.antlr.runtime.RecognitionException
	{
		String output = getTemplateOutput("<?print utcnow()?>");
		assertTrue(output.compareTo("2012-03-28") > 0);
	}

	@CauseTest(expectedCause=UnknownFunctionException.class)
	public void utcnow1() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("", "<?print utcnow(1)?>");
	}

	@CauseTest(expectedCause=UnknownFunctionException.class)
	public void utcnow2() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("", "<?print utcnow(1, 2)?>");
	}

	// @Test
	// public void vars() throws org.antlr.runtime.RecognitionException
	// {
	// 	String source = "<?if var in vars()?>yes<?else?>no<?end if?>";

	// 	checkTemplateOutput("yes", source, "var", "spam", "spam", "eggs");
	// 	checkTemplateOutput("no", source, "var", "nospam", "spam", "eggs");
	// }

	@CauseTest(expectedCause=UnknownFunctionException.class)
	public void vars1() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("", "<?print vars(1)?>");
	}

	@CauseTest(expectedCause=UnknownFunctionException.class)
	public void vars2() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("", "<?print vars(1, 2)?>");
	}

	// @Test
	// public void random() throws org.antlr.runtime.RecognitionException
	// {
	// 	checkTemplateOutput("ok", "<?code r = random()?><?if r>=0 and r<1?>ok<?else?>fail<?end if?>");
	// }

	@CauseTest(expectedCause=UnknownFunctionException.class)
	public void random1() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("", "<?print random(1)?>");
	}

	@CauseTest(expectedCause=UnknownFunctionException.class)
	public void random2() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("", "<?print random(1, 2)?>");
	}

	// @Test
	// public void randrange() throws org.antlr.runtime.RecognitionException
	// {
	// 	checkTemplateOutput("ok", "<?code r = randrange(4)?><?if r>=0 and r<4?>ok<?else?>fail<?end if?>");
	// 	checkTemplateOutput("ok", "<?code r = randrange(17, 23)?><?if r>=17 and r<23?>ok<?else?>fail<?end if?>");
	// 	checkTemplateOutput("ok", "<?code r = randrange(17, 23, 2)?><?if r>=17 and r<23 and r%2?>ok<?else?>fail<?end if?>");
	// }

	@CauseTest(expectedCause=UnknownFunctionException.class)
	public void randrange1() throws org.antlr.runtime.RecognitionException
	{
		checkTemplateOutput("", "<?print randrange()?>");
	}
}
