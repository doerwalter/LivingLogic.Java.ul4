package tests;

import static com.livinglogic.utils.MapUtils.makeMap;
import static com.livinglogic.utils.SetUtils.makeSet;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.livinglogic.utils.AbstractMapChain;
import com.livinglogic.utils.MapChain;

@RunWith(CauseTestRunner.class)
public class MapChainTest
{
	protected Map.Entry entry(Object key, Object value)
	{
		return new AbstractMap.SimpleImmutableEntry(key, value);
	}

	public static AbstractMapChain makeMapChain(Map map1, Map map2)
	{
		return new MapChain(map1, map2);
	}

	@Test
	public void clear()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		c.clear();
		assertFalse(c.containsKey(1));
		assertTrue(c.containsKey(3));
	}

	@Test
	public void containsKey()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.containsKey(1));
		assertTrue(c.containsKey(3));
		assertFalse(c.containsKey(5));
	}

	@Test
	public void containsValue()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.containsValue(2));
		assertTrue(c.containsValue(4));
		assertFalse(c.containsValue(6));
	}

	@Test
	public void entrySet_contains()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.entrySet().contains(entry(1, 2)));
		assertTrue(c.entrySet().contains(entry(3, 4)));
		assertFalse(c.entrySet().contains(entry(3, 5)));
		assertFalse(c.entrySet().contains(entry(6, 7)));
	}

	@Test
	public void entrySet_containsAll()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.entrySet().containsAll(makeSet(entry(1, 2))));
		assertTrue(c.entrySet().containsAll(makeSet(entry(1, 2), entry(3, 4))));
		assertFalse(c.entrySet().containsAll(makeSet(entry(1, 2), entry(3, 5))));
		assertFalse(c.entrySet().containsAll(makeSet(entry(1, 2), entry(3, 4), entry(5, 6))));
	}

	@Test
	public void entrySet_equals()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.entrySet().equals(makeSet(entry(1, 2), entry(3, 4))));
	}

	@Test
	public void entrySet_isEmpty()
	{
		MapChain c1 = new MapChain(makeMap(), makeMap());
		assertTrue(c1.entrySet().isEmpty());
		MapChain c2 = new MapChain(makeMap(1, 2), makeMap(3, 4));
		assertFalse(c2.entrySet().isEmpty());
	}

	@Test
	public void entrySet_size()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		assertEquals(c.entrySet().size(), 2);
	}

	@Test
	public void entrySetIterator()
	{
		HashSet keys = new HashSet();
		HashSet values = new HashSet();
		MapChain<Integer, Integer> c = new MapChain<Integer, Integer>(makeMap(1, 2), makeMap(3, 4));
		for (Map.Entry<Integer, Integer> entry : c.entrySet())
		{
			keys.add(entry.getKey());
			values.add(entry.getValue());
		}
		assertEquals(keys, makeSet(1, 3));
		assertEquals(values, makeSet(2, 4));
	}

	@Test
	public void equals()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		assertFalse(c.equals(null));
		assertFalse(c.equals(makeMap()));
		assertFalse(c.equals(makeMap(1, 2)));
		assertTrue(c.equals(makeMap(1, 2, 3, 4)));
		assertFalse(c.equals(makeMap(1, 2, 3, 5)));
		assertFalse(c.equals(makeMap(1, 2, 3, 4, 5, 6)));
	}

	@Test
	public void get()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		assertEquals(c.get(1), 2);
		assertEquals(c.get(3), 4);
		assertEquals(c.get(5), null);
	}

	@Test
	public void isEmpty()
	{
		AbstractMapChain c1 = makeMapChain(makeMap(), makeMap());
		assertTrue(c1.isEmpty());

		AbstractMapChain c2 = makeMapChain(makeMap(1, 2), makeMap());
		assertFalse(c2.isEmpty());

		AbstractMapChain c3 = makeMapChain(makeMap(), makeMap(1, 2));
		assertFalse(c3.isEmpty());
	}

	@Test
	public void keySet_contains()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.keySet().contains(1));
		assertTrue(c.keySet().contains(3));
		assertFalse(c.keySet().contains(5));
	}

	@Test
	public void keySet_containsAll()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.keySet().containsAll(asList(1)));
		assertTrue(c.keySet().containsAll(asList(1, 3)));
		assertFalse(c.keySet().containsAll(asList(1, 3, 5)));
	}

	@Test
	public void keySet_equals()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.keySet().equals(makeSet(1, 3)));
	}

	@Test
	public void keySet_isEmpty()
	{
		MapChain c1 = new MapChain(makeMap(), makeMap());
		assertTrue(c1.keySet().isEmpty());
		MapChain c2 = new MapChain(makeMap(1, 2), makeMap(3, 4));
		assertFalse(c2.keySet().isEmpty());
	}

	@Test
	public void keySet_size()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		assertEquals(c.keySet().size(), 2);
	}

	@Test
	public void keySetIterator()
	{
		HashSet keys = new HashSet();
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		for (Object key : c.keySet())
			keys.add(key);
		assertEquals(keys, makeSet(1, 3));
	}

	@Test
	public void put()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		c.put(5, 6);
		assertTrue(c.containsKey(5));
		assertTrue(c.getFirst().containsKey(5));
		assertFalse(c.getSecond().containsKey(5));
	}

	@Test
	public void putAll()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		c.putAll(makeMap(5, 6, 7, 8));
		assertTrue(c.containsKey(5));
		assertTrue(c.containsKey(7));
		assertTrue(c.getFirst().containsKey(5));
		assertTrue(c.getFirst().containsKey(7));
		assertFalse(c.getSecond().containsKey(5));
		assertFalse(c.getSecond().containsKey(7));
	}

	@Test
	public void remove()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(3, 4));
		c.remove(1);
		assertFalse(c.containsKey(1));
		c.remove(3);
		assertTrue(c.containsKey(3));
	}

	@Test
	public void size()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2), makeMap(1, 2, 3, 4));
		assertEquals(c.size(), 2);
	}
}
