package tests;

import static com.livinglogic.ul4on.Utils.dumps;
import static com.livinglogic.ul4on.Utils.loads;
import static com.livinglogic.utils.MapUtils.makeMap;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import com.livinglogic.ul4.Color;
import com.livinglogic.ul4.InterpretedTemplate;
import com.livinglogic.ul4.MonthDelta;
import com.livinglogic.ul4.TimeDelta;

public class UL4ONTest
{
	private static InterpretedTemplate getTemplate(String source, String name)
	{
		try
		{
			InterpretedTemplate template = new InterpretedTemplate(source, name, true);
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

	private static void checkRoundtrip(Object object)
	{
		String output = dumps(object);
		Object recreated = loads(output);

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
		checkRoundtrip(asList(1, 2, 3));
		checkRoundtrip(makeMap("eins", 1, "zwei", 2, "drei", 3));
		checkRoundtrip(template);
		checkRoundtrip(asList(asList(1, 2, 3), asList(4, 5, 6), asList(7, 8, 9)));
	}
}
