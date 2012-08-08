package tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

// This is from http://stackoverflow.com/questions/871216/junit-possible-to-expect-a-wrapped-exception
public class CauseTestRunner extends BlockJUnit4ClassRunner
{
	public CauseTestRunner(Class<?> clazz) throws InitializationError
	{
		super(clazz);
	}

	@Override
	protected Statement possiblyExpectingExceptions(FrameworkMethod method, Object test, Statement next)
	{
		CauseTest annotation = method.getAnnotation(CauseTest.class);
		return expectsCauseException(annotation) ?
				new ExpectCauseException(next, getExpectedCauseException(annotation)) :
				super.possiblyExpectingExceptions(method, test, next);
	}

	@Override
	protected List<FrameworkMethod> computeTestMethods()
	{
		List<FrameworkMethod> testMethods = new ArrayList<FrameworkMethod>(super.computeTestMethods());
		testMethods.addAll(getTestClass().getAnnotatedMethods(CauseTest.class));
		return testMethods;
	}

	@Override
	protected void validateTestMethods(List<Throwable> errors)
	{
		super.validateTestMethods(errors);
		validatePublicVoidNoArgMethods(CauseTest.class, false, errors);
	}

	private Class<? extends Throwable> getExpectedCauseException(CauseTest annotation)
	{
		if (annotation == null || annotation.expectedCause() == CauseTest.None.class)
			return null;
		else
			return annotation.expectedCause();
	}

	private boolean expectsCauseException(CauseTest annotation)
	{
		return getExpectedCauseException(annotation) != null;
	}

}