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

public class InterpretedTemplate extends InterpretedCode implements Template, UL4Type
{
	/**
	 * Creates an empty {@code InterpretedTemplate} object. Must be filled in later (used for creating subtemplates)
	 */
	public InterpretedTemplate(Location location, String source, String name, boolean keepWhitespace, String startdelim, String enddelim)
	{
		super(location, source, name, keepWhitespace, startdelim, enddelim);
	}

	public InterpretedTemplate(String source) throws RecognitionException
	{
		this(source, null, true, "<?", "?>");
	}

	public InterpretedTemplate(String source, boolean keepWhitespace) throws RecognitionException
	{
		this(source, null, keepWhitespace, "<?", "?>");
	}

	public InterpretedTemplate(String source, String name) throws RecognitionException
	{
		this(source, name, true, "<?", "?>");
	}

	public InterpretedTemplate(String source, String name, boolean keepWhitespace) throws RecognitionException
	{
		this(source, name, keepWhitespace, "<?", "?>");
	}

	public InterpretedTemplate(String source, String startdelim, String enddelim) throws RecognitionException
	{
		this(source, null, true, startdelim, enddelim);
	}

	public InterpretedTemplate(String source, boolean keepWhitespace, String startdelim, String enddelim) throws RecognitionException
	{
		this(source, null, keepWhitespace, startdelim, enddelim);
	}

	public InterpretedTemplate(String source, String name, boolean keepWhitespace, String startdelim, String enddelim) throws RecognitionException
	{
		super(null, source, name, keepWhitespace, startdelim, enddelim);
		compile();
	}

	/**
	 * loads a template from a string in the UL4ON serialization format.
	 * @param data The template in serialized form.
	 * @return The template object.
	 */
	public static InterpretedTemplate loads(String data)
	{
		return (InterpretedTemplate)Utils.loads(data);
	}

	/**
	 * loads a template from a reader in the UL4ON serialization format.
	 * @param reader The Reader object from which to read the template.
	 * @return The template object.
	 * @throws IOException if reading from the stream fails
	 */
	public static InterpretedTemplate load(Reader reader) throws IOException
	{
		return (InterpretedTemplate)Utils.load(reader);
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.put(name, new TemplateClosure(this, context.getVariables()));
		return null;
	}

	/**
	 * Renders the template.
	 * @param context   the EvaluationContext.
	 */
	public void render(EvaluationContext context) throws IOException
	{
		InterpretedCode oldCode = context.setCode(this);
		try
		{
			super.evaluate(context);
		}
		catch (BreakException ex)
		{
			throw ex;
		}
		catch (ContinueException ex)
		{
			throw ex;
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
	 * Renders the template using the passed in variables.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code. May be null.
	 */
	public void render(EvaluationContext context, Map<String, Object> variables) throws IOException
	{
		Map<String, Object> oldVariables = context.setVariables(variables);
		try
		{
			render(context);
		}
		finally
		{
			context.setVariables(oldVariables);
		}
	}

	/**
	 * Renders the template to a java.io.Writer object.
	 * @param writer    the java.io.Writer object to which the output is written.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code. May be null.
	 */
	public void render(java.io.Writer writer, Map<String, Object> variables) throws IOException
	{
		render(new EvaluationContext(writer, variables));
	}

	/**
	 * Renders the template and returns the resulting string.
	 * @return The render output as a string.
	 */
	public String renders(EvaluationContext context)
	{
		StringWriter output = new StringWriter();
		Writer oldWriter = context.setWriter(output);
		try
		{
			render(context);
		}
		catch (IOException ex)
		{
			// can't happen
		}
		finally
		{
			context.setWriter(oldWriter);
		}
		return output.toString();
	}

	/**
	 * Renders the template using the passed in variables and returns the resulting string.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code. May be null
	 * @return The render output as a string.
	 */
	public String renders(EvaluationContext context, Map<String, Object> variables)
	{
		Map<String, Object> oldVariables = context.setVariables(variables);
		try
		{
			return renders(context);
		}
		finally
		{
			context.setVariables(oldVariables);
		}
	}

	/**
	 * Renders the template and returns the resulting string.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code. May be null
	 * @return The render output as a string.
	 */
	public String renders(Map<String, Object> variables)
	{
		StringWriter output = new StringWriter();
		try
		{
			render(output, variables);
		}
		catch (IOException ex)
		{
			// can't happen
		}
		return output.toString();
	}

	private static class RenderRunnable implements Runnable
	{
		protected InterpretedTemplate template;
		protected Writer writer;
		protected Map<String, Object> variables;
		
		public RenderRunnable(InterpretedTemplate template, Writer writer, Map<String, Object> variables)
		{
			this.template = template;
			this.writer = writer;
			this.variables = variables;
		}

		@Override
		public void run()
		{
			try
			{
				template.render(writer, variables);
				writer.close();
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Renders the template and returns a Reader object from which the template
	 * output can be read.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code. May be null
	 * @return The reader from which the template output can be read.
	 * @throws IOException 
	 */
	public Reader reader(Map<String, Object> variables) throws IOException
	{
		PipedReader reader = new PipedReader(10);
		PipedWriter writer = new PipedWriter(reader);
		new Thread(new RenderRunnable(this, writer, variables)).start();
		return reader;
	}

	public String getType()
	{
		return "template";
	}

	public String typeUL4()
	{
		return "template";
	}
}
