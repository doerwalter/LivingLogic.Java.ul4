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
import com.livinglogic.utils.AbstractCombiningMapChain;
import com.livinglogic.utils.MapChain;

@RunWith(CauseTestRunner.class)
public class CombiningMapChainTest extends MapChainTest
{
	public static class CombiningMapChain<K, V> extends AbstractCombiningMapChain<K, V>
	{
		private Map<K, V> second;

		public CombiningMapChain(Map<K, V> first, Map<K, V> second)
		{
			super(first);
			this.second = second;
		}

		@Override
		public Map<K, V> getSecond()
		{
			return second;
		}

		@Override
		public V getCombinedValue(Object key, V firstValue, V secondValue)
		{
			if (firstValue instanceof Integer && secondValue instanceof Integer)
			{
				int result = ((Integer)firstValue) + ((Integer)secondValue);
				return (V)(Integer)result;
			}
			else if (firstValue instanceof String && secondValue instanceof String)
				return (V)(((String)firstValue) + ((String)secondValue));
			return firstValue;
		}
	}

	protected Map.Entry entry(Object key, Object value)
	{
		return new AbstractMap.SimpleImmutableEntry(key, value);
	}

	public static AbstractMapChain makeMapChain(Map map1, Map map2)
	{
		return new CombiningMapChain(map1, map2);
	}

	// containsValue() ist not supported

	@Test
	public void entrySet_contains()
	{
		super.entrySet_contains();

		AbstractMapChain c = makeMapChain(makeMap(1, "one", 2, "two"), makeMap(2, "zwei", 3, "three"));
		assertTrue(c.entrySet().contains(entry(1, "one")));
		assertTrue(c.entrySet().contains(entry(2, "twozwei")));
		assertTrue(c.entrySet().contains(entry(3, "three")));
		assertFalse(c.entrySet().contains(entry(3, "drei")));
		assertFalse(c.entrySet().contains(entry(4, "four")));
	}

	@Test
	public void entrySet_containsAll()
	{
		super.entrySet_containsAll();

		AbstractMapChain c = makeMapChain(makeMap(1, "one", 2, "two"), makeMap(2, "zwei", 3, "three"));
		Set set = makeSet(entry(1, "one"));
		assertTrue(c.entrySet().containsAll(set));
		set.add(entry(2, "twozwei"));
		assertTrue(c.entrySet().containsAll(set));
		set.add(entry(3, "three"));
		assertTrue(c.entrySet().containsAll(set));
		set.add(entry(4, "four"));
		assertFalse(c.entrySet().containsAll(set));
	}

	@Test
	public void entrySet_equals()
	{
		super.entrySet_equals();

		AbstractMapChain c = makeMapChain(makeMap(1, "one", 2, "two"), makeMap(2, "zwei", 3, "three"));
		assertTrue(c.entrySet().equals(Set.of(entry(1, "one"), entry(2, "twozwei"), entry(3, "three"))));
	}

	@Test
	public void entrySetIterator()
	{
		HashSet keys = new HashSet();
		HashSet values = new HashSet();
		AbstractMapChain<Integer, String> c = new CombiningMapChain<Integer, String>(makeMap(1, "one", 2, "two"), makeMap(2, "zwei", 3, "three"));
		for (Map.Entry<Integer, String> entry : c.entrySet())
		{
			keys.add(entry.getKey());
			values.add(entry.getValue());
		}
		assertEquals(makeSet(1, 2, 3), keys);
		assertEquals(makeSet("one", "twozwei", "three"), values);
	}

	@Test
	public void equals()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2, 3, 4), makeMap(3, 4, 5, 6));
		assertFalse(c.equals(null));
		assertFalse(c.equals(makeMap()));
		assertFalse(c.equals(makeMap(1, 2)));
		assertFalse(c.equals(makeMap(1, 2, 3, 4)));
		assertFalse(c.equals(makeMap(1, 2, 3, 4)));
		assertFalse(c.equals(makeMap(1, 2, 3, 4, 5, 6)));
		assertTrue(c.equals(makeMap(1, 2, 3, 8, 5, 6)));
	}

	@Test
	public void get()
	{
		AbstractMapChain c = makeMapChain(makeMap(1, 2, 3, 4), makeMap(3, 4, 5, 6));
		assertEquals(c.get(1), 2);
		assertEquals(c.get(3), 8);
		assertEquals(c.get(5), 6);
		assertEquals(c.get(6), null);
	}
}
