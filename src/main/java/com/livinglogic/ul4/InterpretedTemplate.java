/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.ObjectFactory;
import com.livinglogic.ul4on.UL4ONSerializable;
import com.livinglogic.ul4on.Utils;


public class InterpretedTemplate extends Block implements UL4Name, UL4CallWithContext, UL4Type, UL4Attributes
{
	/**
	 * The version number used in the UL4ON dump of the template.
	 */
	public static final String VERSION = "25";

	/**
	 * The name of the template/function (defaults to {@code null})
	 */
	public String name = null;

	/**
	 * Should whitespace be skipped when outputting text nodes?
	 * (i.e. linefeed and the whitespace after the linefeed will be skipped. Other spaces/tabs etc. will not be skipped)
	 */
	public boolean keepWhitespace = true;

	/**
	 * The start delimiter for tags (defaults to {@code "<?"})
	 */
	public String startdelim = "<?";

	/**
	 * The end delimiter for tags (defaults to {@code "?>"})
	 */
	public String enddelim = "?>";

	/**
	 * The template/function source (of the top-level template, i.e. subtemplates always get the full source).
	 */
	public String source = null;

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
		this(null, source, name, keepWhitespace, startdelim, enddelim);
		compile();
	}

	/**
	 * Creates an {@code InterpretedTemplate} object. The content will be filled later through a call to {@link #compile)
	 */
	public InterpretedTemplate(Location location, String source, String name, boolean keepWhitespace, String startdelim, String enddelim)
	{
		super(location, 0, 0);
		this.source = source;
		this.name = name;
		this.keepWhitespace = keepWhitespace;
		this.startdelim = startdelim;
		this.enddelim = enddelim;
	}

	protected void compile() throws RecognitionException
	{
		if (source == null)
			return;

		List<Location> tags = tokenizeTags(source, startdelim, enddelim);

		// Stack of currently active blocks
		Stack<Block> stack = new Stack<Block>();
		stack.push(this);

		for (Location location : tags)
		{
			try
			{
				Block innerBlock = stack.peek();
				String type = location.getType();
				// FIXME: use a switch in Java 7
				if (type == null)
				{
					innerBlock.append(new Text(location, location.getStartCode(), location.getEndCode()));
				}
				else if (type.equals("print"))
				{
					UL4Parser parser = getParser(location);
					innerBlock.append(new Print(location, location.getStartCode(), location.getEndCode(), parser.expression()));
				}
				else if (type.equals("printx"))
				{
					UL4Parser parser = getParser(location);
					innerBlock.append(new PrintX(location, location.getStartCode(), location.getEndCode(), parser.expression()));
				}
				else if (type.equals("code"))
				{
					UL4Parser parser = getParser(location);
					innerBlock.append(parser.stmt());
				}
				else if (type.equals("if"))
				{
					UL4Parser parser = getParser(location);
					ConditionalBlockBlock node = new ConditionalBlockBlock(location, location.getStartCode(), location.getEndCode(), new If(location, location.getStartCode(), location.getEndCode(), parser.expression()));
					innerBlock.append(node);
					stack.push(node);
				}
				else if (type.equals("elif"))
				{
					if (innerBlock instanceof ConditionalBlockBlock)
					{
						UL4Parser parser = getParser(location);
						((ConditionalBlockBlock)innerBlock).startNewBlock(new ElIf(location, location.getStartCode(), location.getEndCode(), parser.expression()));
					}
					else
						throw new BlockException("elif doesn't match any if");
				}
				else if (type.equals("else"))
				{
					if (innerBlock instanceof ConditionalBlockBlock)
					{
						((ConditionalBlockBlock)innerBlock).startNewBlock(new Else(location, location.getStartCode(), location.getEndCode()));
					}
					else
						throw new BlockException("else doesn't match any if");
				}
				else if (type.equals("end"))
				{
					if (stack.size() > 1)
					{
						innerBlock.finish(location);
						stack.pop();
					}
					else
						throw new BlockException("not in any block");
				}
				else if (type.equals("for"))
				{
					UL4Parser parser = getParser(location);
					Block node = parser.for_();
					innerBlock.append(node);
					stack.push(node);
				}
				else if (type.equals("break"))
				{
					for (int i = stack.size()-1; i >= 0; --i)
					{
						if (stack.get(i).handleLoopControl("break"))
							break;
					}
					innerBlock.append(new Break(location, location.getStartCode(), location.getEndCode()));
				}
				else if (type.equals("continue"))
				{
					for (int i = stack.size()-1; i >= 0; --i)
					{
						if (stack.get(i).handleLoopControl("continue"))
							break;
					}
					innerBlock.append(new Continue(location, location.getStartCode(), location.getEndCode()));
				}
				else if (type.equals("return"))
				{
					UL4Parser parser = getParser(location);
					innerBlock.append(new Return(location, location.getStartCode(), location.getEndCode(), parser.expression()));
				}
				else if (type.equals("def"))
				{
					// Copy over all the attributes, however passing a {@link Location} will prevent compilation
					InterpretedTemplate subtemplate = new InterpretedTemplate(location, source, location.getCode(), keepWhitespace, startdelim, enddelim);
					innerBlock.append(subtemplate);
					stack.push(subtemplate);
				}
				else
				{
					// Can't happen
					throw new RuntimeException("unknown tag " + type);
				}
			}
			catch (Exception ex)
			{
				throw new TemplateException(new LocationException(ex, location), this);
			}
		}
		if (stack.size() > 1) // the template itself is still on the stack
		{
			Block innerBlock = stack.peek();
			throw new ASTException(new BlockException(innerBlock.getType() + " block unclosed"), innerBlock);
		}
	}

	private UL4Parser getParser(Location location)
	{
		return getParser(location, location.getCode());
	}

	private UL4Parser getParser(Location location, String source)
	{
		ANTLRStringStream input = new ANTLRStringStream(source);
		UL4Lexer lexer = new UL4Lexer(location, input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		UL4Parser parser = new UL4Parser(location, tokens);
		return parser;
	}

	public String nameUL4()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSource()
	{
		return source;
	}

	public boolean getKeepWhitespace()
	{
		return keepWhitespace;
	}

	public String getStartDelim()
	{
		return startdelim;
	}

	public String getEndDelim()
	{
		return enddelim;
	}

	public void toString(Formatter formatter)
	{
		formatter.write("def ");
		formatter.write(name != null ? name : "unnamed");
		formatter.write(":");
		formatter.lf();
		formatter.indent();
		super.toString(formatter);
		formatter.dedent();
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

	/**
	 * writes the {@code InterpretedTemplate} object to a string in the UL4ON serialization format.
	 * @return The string containing the template/function in serialized form.
	 */
	public String dumps()
	{
		return Utils.dumps(this);
	}

	/**
	 * Renders the template.
	 * @param context   the EvaluationContext.
	 */
	public void render(EvaluationContext context)
	{
		InterpretedTemplate oldTemplate = context.setTemplate(this);
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
		catch (ReturnException ex)
		{
			// ignore return value and end rendering
		}
		catch (Exception ex)
		{
			throw new TemplateException(ex, this);
		}
		finally
		{
			context.setTemplate(oldTemplate);
		}
 	}

	/**
	 * Renders the template using the passed in variables.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code. May be null.
	 */
	public void render(EvaluationContext context, Map<String, Object> variables)
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
	public void render(java.io.Writer writer, Map<String, Object> variables)
	{
		EvaluationContext context = new EvaluationContext(writer, variables);
		try
		{
			render(context);
		}
		finally
		{
			context.close();
		}
	}

	/**
	 * Renders the template and returns the resulting string.
	 * @return The render output as a string.
	 */
	public String renders()
	{
		EvaluationContext context = new EvaluationContext(null, null);
		try
		{
			return renders(context);
		}
		finally
		{
			context.close();
		}
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
		render(output, variables);
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
			template.render(writer, variables);
			try
			{
				writer.close();
			}
			catch (IOException exc)
			{
				throw new RuntimeException(exc);
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
		InterpretedTemplate oldTemplate = context.setTemplate(this);
		try
		{
			super.evaluate(context);
			return null;
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
			throw new TemplateException(ex, this);
		}
		finally
		{
			context.setTemplate(oldTemplate);
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
		EvaluationContext context = new EvaluationContext(null, variables);
		try
		{
			return call(context);
		}
		finally
		{
			context.close();
		}
	}

	public Object evaluate(EvaluationContext context)
	{
		context.put(name, new TemplateClosure(this, context.getVariables()));
		return null;
	}

	public String getType()
	{
		return "template";
	}

	public String typeUL4()
	{
		return "template";
	}

	private static String removeWhitespace(String string)
	{
		StringBuilder buffer = new StringBuilder();
		boolean keepWS = true;

		for (int i = 0; i < string.length(); ++i)
		{
			char c = string.charAt(i);

			if (c == '\n')
				keepWS = false;
			else if (Character.isWhitespace(c))
			{
				if (keepWS)
					buffer.append(c);
			}
			else
			{
				buffer.append(c);
				keepWS = true;
			}
		}

		return buffer.toString();
	}

	public String formatText(String text)
	{
		return keepWhitespace ? text : removeWhitespace(text);
	}

	/**
	 * Split the template source into tags and literal text.
	 * @param source The sourcecode of the template
	 * @param startdelim The start delimiter for template tags (usually {@code "<?"})
	 * @param enddelim The end delimiter for template tags (usually {@code "?>"})
	 * @return A list of {@link Location} objects
	 */
	public List<Location> tokenizeTags(String source, String startdelim, String enddelim)
	{
		Pattern tagPattern = Pattern.compile(escapeREchars(startdelim) + "(printx|print|code|for|if|elif|else|end|break|continue|def|return|note)(\\s*(.*?)\\s*)?" + escapeREchars(enddelim), Pattern.DOTALL);
		LinkedList<Location> tags = new LinkedList<Location>();
		if (source != null)
		{
			Matcher matcher = tagPattern.matcher(source);
			int pos = 0;

			int start;
			int end;
			while (matcher.find())
			{
				start = matcher.start();
				end = start + matcher.group().length();
				if (pos != start)
					tags.add(new Location(this, source, null, pos, start, pos, start));
				int codestart = matcher.start(3);
				int codeend = codestart + matcher.group(3).length();
				String type = matcher.group(1);
				if (!type.equals("note"))
					tags.add(new Location(this, source, matcher.group(1), start, end, codestart, codeend));
				pos = end;
			}
			end = source.length();
			if (pos != end)
				tags.add(new Location(this, source, null, pos, end, pos, end));
		}
		return tags;
	}

	private static String escapeREchars(String input)
	{
		int len = input.length();

		StringBuilder buffer = new StringBuilder(len);

		for (int i = 0; i < len; ++i)
		{
			char c = input.charAt(i);
			if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9')))
				buffer.append('\\');
			buffer.append(c);
		}
		return buffer.toString();
	}

	public void finish(Location endlocation)
	{
		super.finish(endlocation);
		String type = endlocation.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("def"))
			throw new BlockException("def ended by end" + type);
	}

	public boolean handleLoopControl(String name)
	{
		throw new BlockException(name + " outside of for loop");
	}

	static
	{
		Utils.register("de.livinglogic.ul4.location", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Location(null, null, null, -1, -1, -1, -1); }});
		Utils.register("de.livinglogic.ul4.text", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Text(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.const", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Const(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.list", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.List(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.listcomp", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ListComprehension(null, -1, -1, null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.dict", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Dict(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.dictcomp", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.DictComprehension(null, -1, -1, null, null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.genexpr", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GeneratorExpression(null, -1, -1, null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.var", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Var(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.ieie", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ConditionalBlockBlock(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.if", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.If(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.elif", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ElIf(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.else", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Else(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.for", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.For(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.break", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Break(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.continue", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Continue(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.getattr", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GetAttr(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.getslice", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GetSlice(null, -1, -1, null, null, null); }});
		Utils.register("de.livinglogic.ul4.not", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Not(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.neg", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Neg(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.print", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Print(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.printx", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.PrintX(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.return", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Return(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.getitem", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GetItem(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.eq", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.EQ(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.ne", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.NE(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.lt", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LT(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.le", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LE(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.gt", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GT(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.ge", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GE(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.contains", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Contains(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.notcontains", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.NotContains(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.add", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Add(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.sub", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Sub(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.mul", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Mul(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.floordiv", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.FloorDiv(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.truediv", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.TrueDiv(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.or", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Or(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.and", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.And(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.mod", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Mod(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.storevar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.StoreVar(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.addvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.AddVar(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.subvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SubVar(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.mulvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.MulVar(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.floordivvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.FloorDivVar(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.truedivvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.TrueDivVar(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.modvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ModVar(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.call", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Call(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.template", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.InterpretedTemplate(null, null, null, false, null, null); }});
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(VERSION);
		encoder.dump(source);
		encoder.dump(name);
		encoder.dump(keepWhitespace);
		encoder.dump(startdelim);
		encoder.dump(enddelim);
		super.dumpUL4ON(encoder);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		String version = (String)decoder.load();
		if (!VERSION.equals(version))
		{
			throw new RuntimeException("Invalid version, expected " + VERSION + ", got " + version);
		}
		source = (String)decoder.load();
		name = (String)decoder.load();
		keepWhitespace = (Boolean)decoder.load();
		startdelim = (String)decoder.load();
		enddelim = (String)decoder.load();
		super.loadUL4ON(decoder);
	}

	private static class BoundMethodRenderS extends BoundMethodWithContext<InterpretedTemplate>
	{
		private static final Signature signature = new Signature("renders", "kwargs", Signature.remainingKeywordArguments);

		public BoundMethodRenderS(InterpretedTemplate object)
		{
			super(object);
		}

		public Signature getSignature()
		{
			return signature;
		}

		public Object callUL4(EvaluationContext context, Object[] args)
		{
			return object.renders(context, (Map<String, Object>)args[0]);
		}
	}

	private static class BoundMethodRender extends BoundMethodWithContext<InterpretedTemplate>
	{
		private static final Signature signature = new Signature("render", "kwargs", Signature.remainingKeywordArguments);

		public BoundMethodRender(InterpretedTemplate object)
		{
			super(object);
		}

		public Signature getSignature()
		{
			return signature;
		}

		public Object callUL4(EvaluationContext context, Object[] args)
		{
			object.render(context, (Map<String, Object>)args[0]);
			return null;
		}
	}

	protected static Set<String> attributes = makeExtendedSet(Block.attributes, "name", "keepws", "startdelim", "enddelim", "source", "render", "renders");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("name".equals(key))
			return name;
		else if ("keepws".equals(key))
			return keepWhitespace;
		else if ("startdelim".equals(key))
			return startdelim;
		else if ("enddelim".equals(key))
			return enddelim;
		else if ("source".equals(key))
			return source;
		else if ("render".equals(key))
			return new BoundMethodRender(this);
		else if ("renders".equals(key))
			return new BoundMethodRenderS(this);
		else
			return super.getItemStringUL4(key);
	}
}
