package tests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CauseTest
{
	/**
	 * Default empty exception
	 */
	static class None extends Throwable
	{
		private static final long serialVersionUID= 1L;
		private None()
		{
		}
	}

	Class<? extends Throwable> expectedCause() default None.class;
}