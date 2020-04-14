package tests;

import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import static java.util.Arrays.asList;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.livinglogic.ul4on.DecoderException;

import com.livinglogic.ul4on.UL4ONSerializable;
import com.livinglogic.ul4on.ObjectFactory;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;
import static com.livinglogic.ul4on.Utils.dumps;
import static com.livinglogic.ul4on.Utils.loads;
import static com.livinglogic.utils.MapUtils.makeMap;
import static com.livinglogic.utils.SetUtils.makeSet;

import com.livinglogic.ul4.Color;
import com.livinglogic.ul4.InterpretedTemplate;
import com.livinglogic.ul4.MonthDelta;
import com.livinglogic.ul4.TimeDelta;
import com.livinglogic.ul4.Slice;

import org.junit.runner.RunWith;

@RunWith(CauseTestRunner.class)
public class UL4ONTest
{
	static
	{
		InterpretedTemplate.register4UL4ON();
	}

	private static class Point implements UL4ONSerializable
	{
		int x;
		int y;

		Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		public int identity()
		{
			return 1;
		}

		public String getUL4ONName()
		{
			return "de.livingapps.appdd.test.point";
		}

		public void dumpUL4ON(Encoder encoder) throws IOException
		{
			encoder.dump(x);
			encoder.dump(y);
		}

		public void loadUL4ON(Decoder decoder) throws IOException
		{
			x = (int)decoder.load();
			y = (int)decoder.load();
		}
	}

	private static class Point2 extends Point
	{
		public Point2(int x, int y)
		{
			super(x, y);
		}

		public int identity()
		{
			return 2;
		}
	}

	private static class PointContent implements UL4ONSerializable
	{
		int x;
		int y;

		PointContent(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		public int identity()
		{
			return 3;
		}

		public String getUL4ONName()
		{
			return "de.livingapps.appdd.test.pointcontent";
		}

		public void dumpUL4ON(Encoder encoder) throws IOException
		{
			if (x != 0)
			{
				encoder.dump(x);
				if (y != 0)
					encoder.dump(y);
			}
		}

		public void loadUL4ON(Decoder decoder) throws IOException
		{
			int index = -1;

			for (Object item : decoder)
			{
				switch (++index)
				{
					case 0:
						x = (int)item;
						break;
					case 1:
						y = (int)item;
						break;
				}
			}

			switch (index)
			{
				case -1:
					x = 0;
				case 0:
					y = 0;
			}
		}
	}

	private static InterpretedTemplate getTemplate(String source, String name)
	{
		InterpretedTemplate template = new InterpretedTemplate(source, name, InterpretedTemplate.Whitespace.keep, null, null, (String)null);
		// System.out.println(template);
		return template;
	}

	private static InterpretedTemplate getTemplate(String source)
	{
		return getTemplate(source, null);
	}

	private static void checkRoundtrip(Object object)
	{
		String output = dumps(object);
		Object recreated = loads(output, null);

		// If we have an InterpretedTemplate, check the output instead
		if ((recreated instanceof InterpretedTemplate) && (object instanceof InterpretedTemplate))
		{
			recreated = ((InterpretedTemplate)recreated).renders((Map<String, Object>)null);
			object = ((InterpretedTemplate)object).renders((Map<String, Object>)null);
		}
		assertEquals(object, recreated);
	}

	@Test
	public void roundtrip()
	{
		InterpretedTemplate template = getTemplate("<?for i in range(10)?>[<?print i?>]<?end for?>");

		checkRoundtrip(null);
		checkRoundtrip(true);
		checkRoundtrip(false);
		checkRoundtrip(42);
		checkRoundtrip(42.666);
		checkRoundtrip("gurk<>'\"");
		checkRoundtrip(new Color(0x66, 0x99, 0xcc, 0xff));
		checkRoundtrip(LocalDate.now());
		checkRoundtrip(LocalDateTime.now());
		checkRoundtrip(new TimeDelta(-1, 1, 1));
		checkRoundtrip(new MonthDelta(-1));
		checkRoundtrip(new Slice(false, false, -1, -1));
		checkRoundtrip(new Slice(false, true, -1, 3));
		checkRoundtrip(new Slice(true, false, 1, -1));
		checkRoundtrip(new Slice(true, true, 1, 3));
		checkRoundtrip(asList(1, 2, 3));
		checkRoundtrip(makeMap("eins", 1, "zwei", 2, "drei", 3));
		checkRoundtrip(makeSet());
		checkRoundtrip(makeSet(1, 2, 3));
		checkRoundtrip(template);
		checkRoundtrip(asList(asList(1, 2, 3), asList(4, 5, 6), asList(7, 8, 9)));
	}

	@Test
	public void xss()
	{
		assertEquals("S'\\x3c'", dumps("<"));
	}

	@Test
	public void template_from_source()
	{
		InterpretedTemplate template = (InterpretedTemplate)loads("o s'de.livinglogic.ul4.template' n s'test' s'<?print x + y?>' s'x, y=23' s'keep' n n )", null);
		assertEquals("40", template.renders(makeMap("x", 17)));
	}

	@Test
	public void recursion()
	{
		List l1 = new ArrayList();
		l1.add(l1);

		List l2 = (List)loads(dumps(l1), null);

		assertEquals(1, l2.size());
		assertTrue(l2.get(0) == l2);
	}

	@Test
	public void custom_class()
	{
		Map<String, ObjectFactory> registry = makeMap("de.livingapps.appdd.test.point", new ObjectFactory(){ public UL4ONSerializable create() { return new Point(0, 0); }});

		Point p1 = new Point(17, 23);

		Point p2 = (Point)loads(dumps(p1), registry);

		assertEquals(17, p2.x);
		assertEquals(23, p2.y);
		assertEquals(1, p2.identity());
	}

	@Test
	public void custom_class_content()
	{
		Map<String, ObjectFactory> registry = makeMap("de.livingapps.appdd.test.pointcontent", new ObjectFactory(){ public UL4ONSerializable create() { return new PointContent(0, 0); }});

		PointContent p1;
		PointContent p2;

		p1 = new PointContent(17, 23);
		p2 = (PointContent)loads(dumps(p1), registry);

		assertEquals(17, p2.x);
		assertEquals(23, p2.y);
		assertEquals(3, p2.identity());

		p1 = new PointContent(17, 0);
		p2 = (PointContent)loads(dumps(p1), registry);

		assertEquals(17, p2.x);
		assertEquals(0, p2.y);
		assertEquals(3, p2.identity());

		p1 = new PointContent(0, 0);
		p2 = (PointContent)loads(dumps(p1), registry);

		assertEquals(0, p2.x);
		assertEquals(0, p2.y);
		assertEquals(3, p2.identity());
	}

	@Test
	public void custom_class_registry()
	{
		Map<String, ObjectFactory> registry = makeMap("de.livingapps.appdd.test.point", new ObjectFactory(){ public UL4ONSerializable create() { return new Point2(0, 0); }});

		Point p1 = new Point(17, 23);

		Point p2 = (Point)loads(dumps(p1), registry);

		assertEquals(17, p2.x);
		assertEquals(23, p2.y);
		assertEquals(2, p2.identity());
	}

	@CauseTest(expectedCause=DecoderException.class)
	public void broken()
	{
		Object x = loads("l i42 k23 ]", null);
	}

	@Test
	public void multiple_encoder_calls()
	{
		Encoder encoder = new Encoder();
		String s1 = "gurk";
		String s2 = "hurz";

		assertEquals("S'gurk'", encoder.dumps(s1));
		assertEquals("S'hurz'", encoder.dumps(s2));
		assertEquals("^0", encoder.dumps(s1));
		assertEquals("^1", encoder.dumps(s2));
	}

	@Test
	public void multiple_decoder_calls()
	{
		Decoder decoder = new Decoder();
		String s1 = "gurk";
		String s2 = "hurz";

		assertEquals("gurk", decoder.loads("S'gurk'"));
		assertEquals("hurz", decoder.loads("S'hurz'"));
		assertEquals("gurk", decoder.loads("^0"));
		assertEquals("hurz", decoder.loads("^1"));
	}
}
