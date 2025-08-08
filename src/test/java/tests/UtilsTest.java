package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.livinglogic.utils.StringUtils;

@RunWith(CauseTestRunner.class)
public class UtilsTest
{
	@Test
	public void formatMessage()
	{
		assertEquals("gurk", StringUtils.formatMessage("gurk"));
		assertEquals("x y z", StringUtils.formatMessage("x {} z", "y"));
		assertEquals("x 'y' z", StringUtils.formatMessage("x {!r} z", "y"));
		assertEquals("x `y` z", StringUtils.formatMessage("x {!`} z", "y"));
		assertEquals("x ``(`)`` z", StringUtils.formatMessage("x {!`} z", "(`)"));
	}
}
