package tests;

import java.util.Date;
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
		checkRoundtrip("gurk");
		checkRoundtrip(new Color(0x66, 0x99, 0xcc, 0xff));
		checkRoundtrip(new Date());
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

		assertEquals(l2.size(), 1);
		assertTrue(l2.get(0) == l2);
	}

	@Test
	public void custom_class()
	{
		Point p1 = new Point(17, 23);

		Point p2 = (Point)loads(dumps(p1), makeMap("de.livingapps.appdd.test.point", new ObjectFactory(){ public UL4ONSerializable create() { return new Point(0, 0); }}));

		assertEquals(p2.x, 17);
		assertEquals(p2.y, 23);
		assertEquals(p2.identity(), 1);
	}

	@Test
	public void custom_class_registry()
	{
		Point p1 = new Point(17, 23);

		Point p2 = (Point)loads(dumps(p1), makeMap("de.livingapps.appdd.test.point", new ObjectFactory(){ public UL4ONSerializable create() { return new Point2(0, 0); }}));

		assertEquals(p2.x, 17);
		assertEquals(p2.y, 23);
		assertEquals(p2.identity(), 2);
	}

	@CauseTest(expectedCause=DecoderException.class)
	public void broken()
	{
		Object x = loads("l i42 k23 ]", null);
	}
}
