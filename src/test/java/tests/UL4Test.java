package tests;

import java.util.Date;
import static java.util.Arrays.*;
import org.junit.Test;
import static org.junit.Assert.*;

import com.livinglogic.ul4.Color;
import com.livinglogic.ul4.InterpretedTemplate;
import com.livinglogic.ul4.Compiler;
import static com.livinglogic.utils.MapUtils.*;
import static com.livinglogic.ul4on.Utils.*;

public class UL4Test
{
	private static void checkTemplateOutput(String expected, String source, Object... args)
	{
		InterpretedTemplate template = Compiler.compile(source);
		String output = template.renders(makeMap(args));
		assertEquals(expected, output);
	}

	@Test
	public void text()
	{
		checkTemplateOutput("gurk", "gurk");
	}

	@Test
	public void none()
	{
		checkTemplateOutput("", "<?print None?>");
		checkTemplateOutput("no", "<?if None?>yes<?else?>no<?end if?>");
	}

	@Test
	public void false_()
	{
		checkTemplateOutput("False", "<?print False?>");
		checkTemplateOutput("no", "<?if False?>yes<?else?>no<?end if?>");
	}

	@Test
	public void true_()
	{
		checkTemplateOutput("True", "<?print True?>");
		checkTemplateOutput("yes", "<?if True?>yes<?else?>no<?end if?>");
	}

	@Test
	public void int_()
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
	public void float_()
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
	public void str()
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
	public void date()
	{
		checkTemplateOutput("2000-02-29", "<?print @2000-02-29T.isoformat()?>");
		checkTemplateOutput("2000-02-29T12:34:00", "<?print @2000-02-29T12:34.isoformat()?>");
		checkTemplateOutput("2000-02-29T12:34:56", "<?print @2000-02-29T12:34:56.isoformat()?>");
		checkTemplateOutput("2000-02-29T12:34:56.987000", "<?print @2000-02-29T12:34:56.987000.isoformat()?>");
		checkTemplateOutput("yes", "<?if @2000-02-29T12:34:56.987654?>yes<?else?>no<?end if?>");
	}

	@Test
	public void color()
	{
		checkTemplateOutput("255,255,255,255", "<?code c = #fff?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("255,255,255,255", "<?code c = #ffffff?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("18,52,86,255", "<?code c = #123456?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("17,34,51,68", "<?code c = #1234?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("18,52,86,120", "<?code c = #12345678?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("yes", "<?if #fff?>yes<?else?>no<?end if?>");
	}

	@Test
	public void list()
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
	public void dict()
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
	public void storevar()
	{
		checkTemplateOutput("42", "<?code x = 42?><?print x?>");
		checkTemplateOutput("xyzzy", "<?code x = 'xyzzy'?><?print x?>");
	}

	@Test
	public void addvar()
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
	public void subvar()
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
	public void mulvar()
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
	public void floordivvar()
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
	public void truedivvar()
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
	public void modvar()
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
}
