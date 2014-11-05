/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
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


public class InterpretedTemplate extends BlockAST implements UL4Name, UL4CallWithContext, UL4Type, UL4Attributes
{
	/**
	 * The version number used in the UL4ON dump of the template.
	 */
	public static final String VERSION = "31";

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
		Stack<BlockAST> stack = new Stack<BlockAST>();
		stack.push(this);

		for (Location location : tags)
		{
			try
			{
				BlockAST innerBlock = stack.peek();
				String type = location.getType();
				// FIXME: use a switch in Java 7
				if (type == null)
				{
					innerBlock.append(new TextAST(location, location.getStartCode(), location.getEndCode()));
				}
				else if (type.equals("print"))
				{
					UL4Parser parser = getParser(location);
					innerBlock.append(new PrintAST(location, location.getStartCode(), location.getEndCode(), parser.expression()));
				}
				else if (type.equals("printx"))
				{
					UL4Parser parser = getParser(location);
					innerBlock.append(new PrintXAST(location, location.getStartCode(), location.getEndCode(), parser.expression()));
				}
				else if (type.equals("code"))
				{
					UL4Parser parser = getParser(location);
					innerBlock.append(parser.stmt());
				}
				else if (type.equals("if"))
				{
					UL4Parser parser = getParser(location);
					ConditionalBlocks node = new ConditionalBlocks(location, location.getStartCode(), location.getEndCode(), new IfBlockAST(location, location.getStartCode(), location.getEndCode(), parser.expression()));
					innerBlock.append(node);
					stack.push(node);
				}
				else if (type.equals("elif"))
				{
					if (innerBlock instanceof ConditionalBlocks)
					{
						UL4Parser parser = getParser(location);
						((ConditionalBlocks)innerBlock).startNewBlock(new ElIfBlockAST(location, location.getStartCode(), location.getEndCode(), parser.expression()));
					}
					else
						throw new BlockException("elif doesn't match any if");
				}
				else if (type.equals("else"))
				{
					if (innerBlock instanceof ConditionalBlocks)
					{
						((ConditionalBlocks)innerBlock).startNewBlock(new ElseBlockAST(location, location.getStartCode(), location.getEndCode()));
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
					BlockAST node = parser.for_();
					innerBlock.append(node);
					stack.push(node);
				}
				else if (type.equals("while"))
				{
					UL4Parser parser = getParser(location);
					WhileBlockAST node = new WhileBlockAST(location, location.getStartCode(), location.getEndCode(), parser.expression());
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
					innerBlock.append(new BreakAST(location, location.getStartCode(), location.getEndCode()));
				}
				else if (type.equals("continue"))
				{
					for (int i = stack.size()-1; i >= 0; --i)
					{
						if (stack.get(i).handleLoopControl("continue"))
							break;
					}
					innerBlock.append(new ContinueAST(location, location.getStartCode(), location.getEndCode()));
				}
				else if (type.equals("return"))
				{
					UL4Parser parser = getParser(location);
					innerBlock.append(new ReturnAST(location, location.getStartCode(), location.getEndCode(), parser.expression()));
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
			BlockAST innerBlock = stack.peek();
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

	public Object callUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		if (args.size() > 0)
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
		context.set(name, new TemplateClosure(this, context.getVariables()));
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
		Pattern tagPattern = Pattern.compile(escapeREchars(startdelim) + "(printx|print|code|for|while|if|elif|else|end|break|continue|def|return|note)(\\s*(.*?)\\s*)?" + escapeREchars(enddelim), Pattern.DOTALL);
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
		throw new BlockException(name + " outside of for/while loop");
	}

	static public void register4UL4ON()
	{
		Utils.register("de.livinglogic.ul4.location", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Location(null, null, null, -1, -1, -1, -1); }});
		Utils.register("de.livinglogic.ul4.text", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.TextAST(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.const", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ConstAST(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.list", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ListAST(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.listcomp", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ListComprehensionAST(null, -1, -1, null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.set", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SetAST(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.setcomp", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SetComprehensionAST(null, -1, -1, null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.dict", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.DictAST(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.dictcomp", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.DictComprehensionAST(null, -1, -1, null, null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.genexpr", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GeneratorExpressionAST(null, -1, -1, null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.var", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.VarAST(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.condblock", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ConditionalBlocks(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.ifblock", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.IfBlockAST(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.elifblock", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ElIfBlockAST(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.elseblock", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ElseBlockAST(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.forblock", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ForBlockAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.whileblock", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.WhileBlockAST(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.break", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BreakAST(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.continue", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ContinueAST(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.attr", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.AttrAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.slice", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SliceAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.not", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.NotAST(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.if", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.IfAST(null, -1, -1, null, null, null); }});
		Utils.register("de.livinglogic.ul4.neg", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.NegAST(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.bitnot", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BitNotAST(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.print", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.PrintAST(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.printx", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.PrintXAST(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.return", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ReturnAST(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.item", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ItemAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.shiftleft", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ShiftLeftAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.shiftright", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ShiftRightAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.bitand", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BitAndAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.bitxor", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BitXOrAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.bitor", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BitOrAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.eq", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.EQAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.ne", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.NEAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.lt", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LTAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.le", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LEAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.gt", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GTAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.ge", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GEAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.contains", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ContainsAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.notcontains", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.NotContainsAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.add", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.AddAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.sub", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SubAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.mul", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.MulAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.floordiv", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.FloorDivAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.truediv", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.TrueDivAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.or", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.OrAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.and", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.AndAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.mod", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ModAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.setvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SetVarAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.addvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.AddVarAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.subvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SubVarAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.mulvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.MulVarAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.floordivvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.FloorDivVarAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.truedivvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.TrueDivVarAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.modvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ModVarAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.shiftleftvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ShiftLeftVarAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.shiftrightvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ShiftRightVarAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.bitandvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BitAndVarAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.bitxorvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BitXOrVarAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.bitorvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BitOrVarAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.call", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.CallAST(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.template", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.InterpretedTemplate(null, null, null, false, null, null); }});
	}

	static
	{
		register4UL4ON();
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
		public BoundMethodRenderS(InterpretedTemplate object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "template.renders";
		}

		private static final Signature signature = new Signature("kwargs", Signature.remainingKeywordArguments);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(EvaluationContext context, List<Object> args)
		{
			return object.renders(context, (Map<String, Object>)args.get(0));
		}
	}

	private static class BoundMethodRender extends BoundMethodWithContext<InterpretedTemplate>
	{
		public BoundMethodRender(InterpretedTemplate object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "template.render";
		}

		private static final Signature signature = new Signature("kwargs", Signature.remainingKeywordArguments);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(EvaluationContext context, List<Object> args)
		{
			object.render(context, (Map<String, Object>)args.get(0));
			return null;
		}
	}

	protected static Set<String> attributes = makeExtendedSet(BlockAST.attributes, "name", "keepws", "startdelim", "enddelim", "source", "render", "renders");

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
