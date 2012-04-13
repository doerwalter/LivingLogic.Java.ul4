package tests;

import org.junit.runners.model.Statement;

public class ExpectCauseException extends Statement
{
	private Statement fNext;
	private final Class<? extends Throwable> fExpected;

	public ExpectCauseException( Statement next, Class<? extends Throwable> expected )
	{
		fNext= next;
		fExpected= expected;
	}

	@Override
	public void evaluate() throws Exception
	{
		boolean complete = false;
		try
		{
			fNext.evaluate();
			complete = true;
		}
		catch (Throwable e)
		{
			Throwable original = e;
			boolean found = false;
			while (e != null)
			{
				if (fExpected.isAssignableFrom(e.getClass()))
				{
					found = true;
					break;
				}
				e = e.getCause();
			}
			if (!found)
			{
				String message = "Unexpected exception cause, expected<"
				                 + fExpected.getName() + "> but not found in cause chain, got <" + original + ">";
				throw new Exception(message, original);
			}
		}
		if (complete)
			throw new AssertionError( "Expected exception cause: " + fExpected.getName());
	}
}