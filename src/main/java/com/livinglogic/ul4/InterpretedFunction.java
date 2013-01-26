/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.lang.StringEscapeUtils;

import static com.livinglogic.utils.StringUtils.removeWhitespace;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.ObjectFactory;
import com.livinglogic.ul4on.UL4ONSerializable;
import com.livinglogic.ul4on.Utils;

public class InterpretedFunction extends InterpretedCode implements UL4CallableWithContext, UL4Type
{
	/**
	 * Creates an empty {@code InterpretedFunction} object. Must be filled in later (used for creating subfunctions)
	 */
	public InterpretedFunction(Location location, String source, String name, boolean keepWhitespace, String startdelim, String enddelim)
	{
		super(location, source, name, keepWhitespace, startdelim, enddelim);
	}

	public InterpretedFunction(String source) throws RecognitionException
	{
		this(source, null, true, "<?", "?>");
	}

	public InterpretedFunction(String source, boolean keepWhitespace) throws RecognitionException
	{
		this(source, null, keepWhitespace, "<?", "?>");
	}

	public InterpretedFunction(String source, String name) throws RecognitionException
	{
		this(source, name, true, "<?", "?>");
	}

	public InterpretedFunction(String source, String name, boolean keepWhitespace) throws RecognitionException
	{
		this(source, name, keepWhitespace, "<?", "?>");
	}

	public InterpretedFunction(String source, String startdelim, String enddelim) throws RecognitionException
	{
		this(source, null, true, startdelim, enddelim);
	}

	public InterpretedFunction(String source, boolean keepWhitespace, String startdelim, String enddelim) throws RecognitionException
	{
		this(source, null, keepWhitespace, startdelim, enddelim);
	}

	public InterpretedFunction(String source, String name, boolean keepWhitespace, String startdelim, String enddelim) throws RecognitionException
	{
		super(null, source, name, keepWhitespace, startdelim, enddelim);
		compile();
	}

	/**
	 * loads a template from a string in the UL4ON serialization format.
	 * @param data The template in serialized form.
	 * @return The template object.
	 */
	public static InterpretedFunction loads(String data)
	{
		return (InterpretedFunction)Utils.loads(data);
	}

	/**
	 * loads a template from a reader in the UL4ON serialization format.
	 * @param reader The Reader object from which to read the template.
	 * @return The template object.
	 * @throws IOException if reading from the stream fails
	 */
	public static InterpretedFunction load(Reader reader) throws IOException
	{
		return (InterpretedFunction)Utils.load(reader);
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.put(name, new FunctionClosure(this, context.getVariables()));
		return null;
	}

	public Object callUL4(EvaluationContext context, Object[] args, Map<String, Object> kwargs)
	{
		if (args.length > 0)
			throw new PositionalArgumentsNotSupportedException(name);
		return call(context, kwargs);
	}

	/**
	 * Executes the function.
	 * @param context   the EvaluationContext.
	 * @return the return value of the function
	 */
	public Object call(EvaluationContext context)
	{
		InterpretedCode oldCode = context.setCode(this);
		try
		{
			super.evaluate(context);
			return null;
		}
		catch (IOException ex)
		{
			throw new ReturnException(ex); // can't happen, as a function can't produce any output
		}
		catch (BreakException ex)
		{
			throw ex;
		}
		catch (ContinueException ex)
		{
			throw ex;
		}
		catch (ReturnException ex)
		{
			return ex.getValue();
		}
		catch (Exception ex)
		{
			if (location == null)
				throw new CodeException(ex, this);
			else
				throw new TagException(ex, location);
		}
		finally
		{
			context.setCode(oldCode);
		}
	}

	/**
	 * Executes the function using the passed in variables.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the function code. May be null.
	 * @return the return value of the function
	 */
	public Object call(EvaluationContext context, Map<String, Object> variables)
	{
		Map<String, Object> oldVariables = context.setVariables(variables);
		try
		{
			return call(context);
		}
		finally
		{
			context.setVariables(oldVariables);
		}
	}

	/**
	 * Executes the function.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the function code. May be null.
	 * @return the return value of the function
	 */
	public Object call(Map<String, Object> variables)
	{
		return call(new EvaluationContext(null, variables));
	}

	public String getType()
	{
		return "function";
	}

	public String typeUL4()
	{
		return "function";
	}
}
