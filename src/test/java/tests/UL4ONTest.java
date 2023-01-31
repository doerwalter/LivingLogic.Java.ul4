package tests;

import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
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
import com.livinglogic.ul4.Template;
import com.livinglogic.ul4.MonthDelta;
import com.livinglogic.ul4.TimeDelta;
import com.livinglogic.ul4.Slice;

import org.junit.runner.RunWith;

@RunWith(CauseTestRunner.class)
public class UL4ONTest
{
	static
	{
		Template.register4UL4ON();
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

		@Override
		public String getUL4ONName()
		{
			return "de.livingapps.appdd.test.point";
		}

		@Override
		public void dumpUL4ON(Encoder encoder) throws IOException
		{
			encoder.dump(x);
			encoder.dump(y);
		}

		@Override
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

		@Override
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

		@Override
		public String getUL4ONName()
		{
			return "de.livingapps.appdd.test.pointcontent";
		}

		@Override
		public void dumpUL4ON(Encoder encoder) throws IOException
		{
			if (x != 0)
			{
				encoder.dump(x);
				if (y != 0)
					encoder.dump(y);
			}
		}

		@Override
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

	private static class PersistentPoint implements UL4ONSerializable
	{
		String id;
		int x;
		int y;

		PersistentPoint(String id, int x, int y)
		{
			this.id = id;
			this.x = x;
			this.y = y;
		}

		public int identity()
		{
			return 4;
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livingapps.appdd.test.persistentpoint";
		}

		@Override
		public String getUL4ONID()
		{
			return id;
		}

		@Override
		public void dumpUL4ON(Encoder encoder) throws IOException
		{
			encoder.dump(x);
			encoder.dump(y);
		}

		@Override
		public void loadUL4ON(Decoder decoder) throws IOException
		{
			x = (int)decoder.load();
			y = (int)decoder.load();
		}
	}

	private static class PersistentPoint2 extends PersistentPoint
	{
		PersistentPoint2(String id, int x, int y)
		{
			super(id, x, y);
		}

		@Override
		public int identity()
		{
			return 5;
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livingapps.appdd.test.persistentpoint2";
		}
	}

	private static Template getTemplate(String source, String name)
	{
		Template template = new Template(source, name, Template.Whitespace.keep, (String)null);
		// System.out.println(template);
		return template;
	}

	private static Template getTemplate(String source)
	{
		return getTemplate(source, null);
	}

	private static void checkRoundtrip(Object object)
	{
		String output = dumps(object);
		Object recreated = loads(output, null);

		// If we have an Template, check the output instead
		if ((recreated instanceof Template) && (object instanceof Template))
		{
			recreated = ((Template)recreated).renders((Map<String, Object>)null);
			object = ((Template)object).renders((Map<String, Object>)null);
		}
		assertEquals(object, recreated);
	}

//	@Test
//	public void roundtrip()
//	{
//		Template template = getTemplate("<?for i in range(10)?>[<?print i?>]<?end for?>");
//
//		checkRoundtrip(null);
//		checkRoundtrip(true);
//		checkRoundtrip(false);
//		checkRoundtrip(42);
//		checkRoundtrip(42.666);
//		checkRoundtrip("gurk<>'\"");
//		checkRoundtrip(new Color(0x66, 0x99, 0xcc, 0xff));
//		checkRoundtrip(LocalDate.now());
//		checkRoundtrip(LocalDateTime.now());
//		checkRoundtrip(new TimeDelta(-1, 1, 1));
//		checkRoundtrip(new MonthDelta(-1));
//		checkRoundtrip(new Slice(false, false, -1, -1));
//		checkRoundtrip(new Slice(false, true, -1, 3));
//		checkRoundtrip(new Slice(true, false, 1, -1));
//		checkRoundtrip(new Slice(true, true, 1, 3));
//		checkRoundtrip(asList(1, 2, 3));
//		checkRoundtrip(makeMap("eins", 1, "zwei", 2, "drei", 3));
//		checkRoundtrip(makeSet());
//		checkRoundtrip(makeSet(1, 2, 3));
//		checkRoundtrip(template);
//		checkRoundtrip(asList(asList(1, 2, 3), asList(4, 5, 6), asList(7, 8, 9)));
//	}

	@Test
	public void roundtripNull()
	{
		checkRoundtrip(null);
	}
	@Test
	public void roundtripTrue()
		{
			checkRoundtrip(true);
		}
	@Test
	public void roundtripFalse()
			{
				checkRoundtrip(false);
			}
	@Test
	public void roundtrip42()
				{
					checkRoundtrip(42);
				}
	@Test
	public void roundtrip42_666()
					{
						checkRoundtrip(42.666);
					}
	@Test
	public void roundtripGurk()
						{
							checkRoundtrip("gurk<>'\"");
						}
	@Test
	public void roundtripColor()
							{
								checkRoundtrip(new Color(0x66, 0x99, 0xcc, 0xff));
							}
	@Test
	public void roundtripLocalDate()
								{
									checkRoundtrip(LocalDate.now());
								}
	@Test
	public void roundtripLocalDateTime()
									{
										checkRoundtrip(LocalDateTime.now());
									}
	@Test
	public void roundtripTimeDelta()
										{
											checkRoundtrip(new TimeDelta(-1, 1, 1));
										}
	@Test
	public void roundtripMonthDelta()
											{
												checkRoundtrip(new MonthDelta(-1));
											}
	@Test
	public void roundtripSlice1()
												{
													checkRoundtrip(new Slice(false, false, -1, -1));
												}
	@Test
	public void roundtripSlice2()
													{
														checkRoundtrip(new Slice(false, true, -1, 3));
													}
	@Test
	public void roundtripSlice3()
														{
															checkRoundtrip(new Slice(true, false, 1, -1));
														}
	@Test
	public void roundtripSlice4()
															{
																checkRoundtrip(new Slice(true, true, 1, 3));
															}
	@Test
	public void roundtripList()
																{
																	checkRoundtrip(asList(1, 2, 3));
																}
	@Test
	public void roundtripMap()
																	{
																		checkRoundtrip(makeMap("eins", 1, "zwei", 2, "drei", 3));
																	}
	@Test
	public void roundtripSet1()
																		{
																			checkRoundtrip(makeSet());
																		}
	@Test
	public void roundtripSet2()
																			{
																				checkRoundtrip(makeSet(1, 2, 3));
																			}
	@Test
	public void roundtripTemplate()
																				{
																					Template template = getTemplate("<?for i in range(10)?>[<?print i?>]<?end for?>");
																					checkRoundtrip(template);
																				}
	@Test
	public void roundtripListOfLists()
																					{
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
		Template template = (Template)loads("o s'de.livinglogic.ul4.template' n s'test' s'<?ul4 test(x, y=23)?><?print x + y?>' s'x, y=23' s'keep' )", null);
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
		Map<String, ObjectFactory> registry = makeMap("de.livingapps.appdd.test.point", new ObjectFactory(){ public UL4ONSerializable create(String id) { return new Point(0, 0); }});

		Point p1 = new Point(17, 23);

		Point p2 = (Point)loads(dumps(p1), registry);

		assertEquals(17, p2.x);
		assertEquals(23, p2.y);
		assertEquals(1, p2.identity());
	}

	@Test
	public void custom_class_content()
	{
		Map<String, ObjectFactory> registry = makeMap("de.livingapps.appdd.test.pointcontent", new ObjectFactory(){ public UL4ONSerializable create(String id) { return new PointContent(0, 0); }});

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
		Map<String, ObjectFactory> registry = makeMap("de.livingapps.appdd.test.point", new ObjectFactory(){ public UL4ONSerializable create(String id) { return new Point2(0, 0); }});

		Point p1 = new Point(17, 23);

		Point p2 = (Point)loads(dumps(p1), registry);

		assertEquals(17, p2.x);
		assertEquals(23, p2.y);
		assertEquals(2, p2.identity());
	}

	@Test
	public void custom_persistent_class()
	{
		Map<String, ObjectFactory> registry = makeMap("de.livingapps.appdd.test.persistentpoint", new ObjectFactory(){ public UL4ONSerializable create(String id) { return new PersistentPoint(id, 0, 0); }});

		PersistentPoint p1 = new PersistentPoint("foo", 17, 23);

		Encoder encoder = new Encoder();
		String dump = encoder.dumps(p1);

		Decoder decoder = new Decoder(registry);
		PersistentPoint p2 = (PersistentPoint)decoder.loads(dump);
		assert(p1 != p2);
		assertEquals(17, p2.x);
		assertEquals(23, p2.y);
		assertEquals("foo", p2.getUL4ONID());
		assertEquals(4, p2.identity());

		decoder.reset();

		dump = dump.replace(" i23 ", " i24 ");
		PersistentPoint p3 = (PersistentPoint)decoder.loads(dump);
		assert(p2 == p3);
		assertEquals(17, p3.x);
		assertEquals(24, p3.y);
		assertEquals("foo", p3.getUL4ONID());
		assertEquals(4, p3.identity());
	}

	private Set setFromIterator(Iterator iterator)
	{
		Set set = new HashSet();
		while (iterator.hasNext())
			set.add(iterator.next());
		return set;
	}

	@Test
	public void persistent_object_cache()
	{
		PersistentPoint p1 = new PersistentPoint("hinz", 17, 23);
		PersistentPoint p2 = new PersistentPoint("kunz", 23, 42);
		PersistentPoint p3 = new PersistentPoint2("gurk", 42, 105);
		PersistentPoint p4 = new PersistentPoint2("hurz", 105, 17);

		Decoder decoder = new Decoder();
		assertEquals(setFromIterator(decoder.allPersistentObjects()), makeSet());

		decoder.storePersistentObject(p1);
		assertEquals(setFromIterator(decoder.allPersistentObjects()), makeSet(p1));

		decoder.storePersistentObject(p2);
		assertEquals(setFromIterator(decoder.allPersistentObjects()), makeSet(p1, p2));

		decoder.storePersistentObject(p3);
		assertEquals(setFromIterator(decoder.allPersistentObjects()), makeSet(p1, p2, p3));

		decoder.storePersistentObject(p4);
		assertEquals(setFromIterator(decoder.allPersistentObjects()), makeSet(p1, p2, p3, p4));
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
