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

public class UL4ONTest
{
	private static void checkRoundtrip(Object object)
	{
		String output = dumps(object);
		Object recreated = load(output);

		// If we have an InterpretedTemplate, check the output instead
		if ((recreated instanceof InterpretedTemplate) && (object instanceof InterpretedTemplate))
		{
			recreated = ((InterpretedTemplate)recreated).renders(null);
			object = ((InterpretedTemplate)object).renders(null);
		}
		assertEquals(object, recreated);
	}

	@Test
	public void roundtrip()
	{
		InterpretedTemplate template = Compiler.compile("<?for i in range(10)?>[<?print i?>]<?end for?>");

		checkRoundtrip(null);
		checkRoundtrip(true);
		checkRoundtrip(false);
		checkRoundtrip(42);
		checkRoundtrip(42.666);
		checkRoundtrip("gurk");
		checkRoundtrip(new Color(0x66, 0x99, 0xcc, 0xff));
		checkRoundtrip(new Date());
		checkRoundtrip(asList(1, 2, 3));
		checkRoundtrip(makeMap("eins", 1, "zwei", 2, "drei", 3));
		checkRoundtrip(template);
		checkRoundtrip(asList(asList(1, 2, 3), asList(4, 5, 6), asList(7, 8, 9)));
	}
}
