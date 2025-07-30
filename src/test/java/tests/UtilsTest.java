package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.livinglogic.ul4.Utils;

@RunWith(CauseTestRunner.class)
public class UtilsTest
{
	@Test
	public void formatMessage()
	{
		assertEquals("gurk", Utils.formatMessage("gurk"));
		assertEquals("x y z", Utils.formatMessage("x {} z", "y"));
		assertEquals("x 'y' z", Utils.formatMessage("x {!r} z", "y"));
		assertEquals("x `y` z", Utils.formatMessage("x {!`} z", "y"));
		assertEquals("x ``(`)`` z", Utils.formatMessage("x {!`} z", "(`)"));
	}
}
