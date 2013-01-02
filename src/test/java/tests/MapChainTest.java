package tests;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import static com.livinglogic.utils.MapUtils.makeMap;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.livinglogic.utils.MapChain;

@RunWith(CauseTestRunner.class)
public class MapChainTest
{
	private Set set(Object... objects)
	{
		return new HashSet(asList(objects));
	}

	private Map.Entry entry(Object key, Object value)
	{
		return new AbstractMap.SimpleImmutableEntry(key, value);
	}

	@Test
	public void clear()
	{
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
		c.clear();
		assertFalse(c.containsKey(1));
		assertTrue(c.containsKey(3));
	}

	@Test
	public void containsKey()
	{
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.containsKey(1));
		assertTrue(c.containsKey(3));
		assertFalse(c.containsKey(5));
	}

	@Test
	public void containsValue()
	{
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.containsValue(2));
		assertTrue(c.containsValue(4));
		assertFalse(c.containsValue(6));
	}

	@Test
	public void entrySet_contains()
	{
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.entrySet().contains(entry(1, 2)));
		assertTrue(c.entrySet().contains(entry(3, 4)));
		assertFalse(c.entrySet().contains(entry(3, 5)));
		assertFalse(c.entrySet().contains(entry(6, 7)));
	}

	@Test
	public void entrySet_containsAll()
	{
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.entrySet().containsAll(set(entry(1, 2))));
		assertTrue(c.entrySet().containsAll(set(entry(1, 2), entry(3, 4))));
		assertFalse(c.entrySet().containsAll(set(entry(1, 2), entry(3, 5))));
		assertFalse(c.entrySet().containsAll(set(entry(1, 2), entry(3, 4), entry(5, 6))));
	}

	@Test
	public void entrySet_equals()
	{
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.entrySet().equals(set(entry(1, 2), entry(3, 4))));
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
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
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
		assertEquals(keys, set(1, 3));
		assertEquals(values, set(2, 4));
	}

	@Test
	public void equals()
	{
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
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
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
		assertEquals(c.get(1), 2);
		assertEquals(c.get(3), 4);
		assertEquals(c.get(5), null);
	}

	@Test
	public void isEmpty()
	{
		MapChain c1 = new MapChain(makeMap(), makeMap());
		assertTrue(c1.isEmpty());

		MapChain c2 = new MapChain(makeMap(1, 2), makeMap());
		assertFalse(c2.isEmpty());

		MapChain c3 = new MapChain(makeMap(), makeMap(1, 2));
		assertFalse(c3.isEmpty());
	}

	@Test
	public void keySet_contains()
	{
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.keySet().contains(1));
		assertTrue(c.keySet().contains(3));
		assertFalse(c.keySet().contains(5));
	}

	@Test
	public void keySet_containsAll()
	{
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.keySet().containsAll(asList(1)));
		assertTrue(c.keySet().containsAll(asList(1, 3)));
		assertFalse(c.keySet().containsAll(asList(1, 3, 5)));
	}

	@Test
	public void keySet_equals()
	{
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
		assertTrue(c.keySet().equals(set(1, 3)));
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
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
		assertEquals(c.keySet().size(), 2);
	}

	@Test
	public void keySetIterator()
	{
		HashSet keys = new HashSet();
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
		for (Object key : c.keySet())
			keys.add(key);
		assertEquals(keys, set(1, 3));
	}

	@Test
	public void put()
	{
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
		c.put(5, 6);
		assertTrue(c.containsKey(5));
		assertTrue(c.getFirst().containsKey(5));
		assertFalse(c.getSecond().containsKey(5));
	}

	@Test
	public void putAll()
	{
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
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
		MapChain c = new MapChain(makeMap(1, 2), makeMap(3, 4));
		c.remove(1);
		assertFalse(c.containsKey(1));
		c.remove(3);
		assertTrue(c.containsKey(3));
	}

	@Test
	public void size()
	{
		MapChain c = new MapChain(makeMap(1, 2), makeMap(1, 2, 3, 4));
		assertEquals(c.size(), 2);
	}
}
