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
			while (e.getCause() != null)
				e = e.getCause();
			if (!fExpected.isAssignableFrom(e.getClass()))
			{
				String message = "Unexpected exception cause, expected<"
				                 + fExpected.getName() + "> but was<"
				                 + e.getClass().getName() + ">";
				throw new Exception(message, e);
			}
		}
		if (complete)
			throw new AssertionError( "Expected exception cause: " + fExpected.getName());
	}
}