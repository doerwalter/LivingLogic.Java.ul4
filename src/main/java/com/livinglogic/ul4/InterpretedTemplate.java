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

import static java.util.Arrays.asList;

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
	public static final String VERSION = "32";

	/**
	 * The name of the template/function (defaults to {@code null})
	 */
	public String name = null;

	public enum Whitespace
	{
		keep, strip, smart;

		public static Whitespace fromString(String value)
		{
			if ("keep".equals(value))
				return keep;
			else if ("strip".equals(value))
				return strip;
			else if ("smart".equals(value))
				return smart;
			else
				throw new RuntimeException("unknown whitespace value " + value);
		}
	};

	/**
	 * Should whitespace be skipped when outputting text nodes?
	 * (i.e. linefeed and the whitespace after the linefeed will be skipped. Other spaces/tabs etc. will not be skipped)
	 */
	public Whitespace whitespace = Whitespace.keep;

	/**
	 * The start delimiter for tags (defaults to {@code "<?"})
	 */
	public String startdelim = "<?";

	/**
	 * The end delimiter for tags (defaults to {@code "?>"})
	 */
	public String enddelim = "?>";

	/**
	 * The signature of the template if it is a top level template  ({@code null} mean that all variables are allowed)
	 */
	public Signature signature = null;

	/**
	 * The signature of the template if it is a subtemplate ({@code null} mean that all variables are allowed)
	 */
	public SignatureAST signatureAST = null;

	/**
	 * The template/function source (of the top-level template, i.e. subtemplates always get the full source).
	 */
	public String source = null;

	/**
	 * Used for deserializing an UL4ON dump (of top level and sub templates). Doesn't compile any source
	 */
	private InterpretedTemplate()
	{
		super(null, 0, 0);
		this.source = null;
		this.name = null;
		this.whitespace = Whitespace.keep;
		this.startdelim = startdelim != null ? startdelim : "<?";
		this.enddelim = enddelim != null ? enddelim : "?>";
		this.signature = null;
		this.signatureAST = null;
	}

	/**
	 * Create of toplevel template without a signature
	 */
	public InterpretedTemplate(String source, String name, Whitespace whitespace, String startdelim, String enddelim) throws RecognitionException
	{
		super(null, 0, 0);
		this.source = source;
		this.name = name;
		this.whitespace = whitespace;
		this.startdelim = startdelim != null ? startdelim : "<?";
		this.enddelim = enddelim != null ? enddelim : "?>";
		this.signature = null;
		this.signatureAST = null;
		compile();
	}

	/**
	 * Create of toplevel template with a specified signature
	 */
	public InterpretedTemplate(String source, String name, Whitespace whitespace, String startdelim, String enddelim, Signature signature) throws RecognitionException
	{
		this(source, name, whitespace, startdelim, enddelim);
		this.signature = signature;
	}

	/**
	 * Create of toplevel template with a signature compiled from a string
	 */
	public InterpretedTemplate(String source, String name, Whitespace whitespace, String startdelim, String enddelim, String signature) throws RecognitionException
	{
		this(source, name, whitespace, startdelim, enddelim);
		if (signature != null)
		{
			UL4Parser parser = getSignatureParser(signature);
			SignatureAST ast = parser.signature();
			EvaluationContext context = new EvaluationContext();
			Signature sig = ast.evaluate(context);
			this.signature = sig;
		}
	}

	/**
	 * Creates an {@code InterpretedTemplate} object. Used for subtemplates.
	 */
	private InterpretedTemplate(Tag tag, String name, Whitespace whitespace, String startdelim, String enddelim, SignatureAST signature)
	{
		super(tag, 0, 0);
		this.source = tag.getSource();
		this.name = name;
		this.whitespace = whitespace;
		this.startdelim = startdelim != null ? startdelim : "<?";
		this.enddelim = enddelim != null ? enddelim : "?>";
		this.signature = null;
		this.signatureAST = signature;
	}

	protected void compile() throws RecognitionException
	{
		if (source == null)
			return;

		List<Object> tagtexts = tokenizeTags(source, startdelim, enddelim);

		// Stack of currently active blocks
		Stack<BlockAST> stack = new Stack<BlockAST>();
		stack.push(this);

		for (Object tagtext : tagtexts)
		{
			try
			{
				BlockAST innerBlock = stack.peek();
				if (tagtext instanceof TextAST)
				{
					innerBlock.append((TextAST)tagtext);
				}
				else
				{
					Tag tag = (Tag)tagtext;
					String tagtype = tag.getTag();
					// FIXME: use a switch in Java 7
					if (tagtype.equals("print"))
					{
						UL4Parser parser = getParser(tag);
						innerBlock.append(new PrintAST(tag, tag.getStartPosCode(), tag.getEndPosCode(), parser.expression()));
					}
					else if (tagtype.equals("printx"))
					{
						UL4Parser parser = getParser(tag);
						innerBlock.append(new PrintXAST(tag, tag.getStartPosCode(), tag.getEndPosCode(), parser.expression()));
					}
					else if (tagtype.equals("code"))
					{
						UL4Parser parser = getParser(tag);
						innerBlock.append(parser.stmt());
					}
					else if (tagtype.equals("if"))
					{
						UL4Parser parser = getParser(tag);
						ConditionalBlocks node = new ConditionalBlocks(tag, tag.getStartPosCode(), tag.getEndPosCode(), new IfBlockAST(tag, tag.getStartPosCode(), tag.getEndPosCode(), parser.expression()));
						innerBlock.append(node);
						stack.push(node);
					}
					else if (tagtype.equals("elif"))
					{
						if (innerBlock instanceof ConditionalBlocks)
						{
							UL4Parser parser = getParser(tag);
							((ConditionalBlocks)innerBlock).startNewBlock(new ElIfBlockAST(tag, tag.getStartPosCode(), tag.getEndPosCode(), parser.expression()));
						}
						else
							throw new BlockException("elif doesn't match any if");
					}
					else if (tagtype.equals("else"))
					{
						if (innerBlock instanceof ConditionalBlocks)
						{
							((ConditionalBlocks)innerBlock).startNewBlock(new ElseBlockAST(tag, tag.getStartPosCode(), tag.getEndPosCode()));
						}
						else
							throw new BlockException("else doesn't match any if");
					}
					else if (tagtype.equals("end"))
					{
						if (stack.size() > 1)
						{
							innerBlock.finish(tag);
							stack.pop();
						}
						else
							throw new BlockException("not in any block");
					}
					else if (tagtype.equals("for"))
					{
						UL4Parser parser = getParser(tag);
						BlockAST node = parser.for_();
						innerBlock.append(node);
						stack.push(node);
					}
					else if (tagtype.equals("while"))
					{
						UL4Parser parser = getParser(tag);
						WhileBlockAST node = new WhileBlockAST(tag, tag.getStartPosCode(), tag.getEndPosCode(), parser.expression());
						innerBlock.append(node);
						stack.push(node);
					}
					else if (tagtype.equals("break"))
					{
						for (int i = stack.size()-1; i >= 0; --i)
						{
							if (stack.get(i).handleLoopControl("break"))
								break;
						}
						innerBlock.append(new BreakAST(tag, tag.getStartPosCode(), tag.getEndPosCode()));
					}
					else if (tagtype.equals("continue"))
					{
						for (int i = stack.size()-1; i >= 0; --i)
						{
							if (stack.get(i).handleLoopControl("continue"))
								break;
						}
						innerBlock.append(new ContinueAST(tag, tag.getStartPosCode(), tag.getEndPosCode()));
					}
					else if (tagtype.equals("return"))
					{
						UL4Parser parser = getParser(tag);
						innerBlock.append(new ReturnAST(tag, tag.getStartPosCode(), tag.getEndPosCode(), parser.expression()));
					}
					else if (tagtype.equals("def"))
					{
						UL4Parser parser = getParser(tag);
						Definition definition = parser.definition();
						// Copy over all the attributes, however passing a {@link Tag} will prevent compilation
						InterpretedTemplate subtemplate = new InterpretedTemplate(tag, definition.getName(), whitespace, startdelim, enddelim, definition.getSignature());
						innerBlock.append(subtemplate);
						stack.push(subtemplate);
					}
					else
					{
						// Can't happen
						throw new RuntimeException("unknown tag " + tagtype);
					}
				}
			}
			catch (Exception ex)
			{
				throw new TemplateException(new TagException(ex, tag), this);
			}
		}
		if (stack.size() > 1) // the template itself is still on the stack
		{
			BlockAST innerBlock = stack.peek();
			throw new ASTException(new BlockException(innerBlock.getType() + " block unclosed"), innerBlock);
		}
	}

	private UL4Parser getSignatureParser(String signature)
	{
		signature = "(" + signature + ")";
		Tag tag = new Tag(signature, "signature", 0, signature.length(), 0, signature.length());
		return getParser(tag);
	}

	private static UL4Parser getParser(Tag tag)
	{
		ANTLRStringStream input = new ANTLRStringStream(tag.getCode());
		UL4Lexer lexer = new UL4Lexer(tag, input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		UL4Parser parser = new UL4Parser(tag, tokens);
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

	public Whitespace getWhitespace()
	{
		return whitespace;
	}

	public String getStartDelim()
	{
		return startdelim;
	}

	public String getEndDelim()
	{
		return enddelim;
	}

	public Signature getSignature()
	{
		return signature;
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
		render(context, null, null);
 	}

	/**
	 * Renders the template to a java.io.Writer object.
	 * @param writer    the java.io.Writer object to which the output is written.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the function code. May be null.
	 *                  These variables will be checked against the signature
	 *                  of the template (if a signature is defined, otherwise
	 *                  all variables will be accepted)
	 */
	public void render(java.io.Writer writer, Map<String, Object> variables)
	{
		render(null, writer, variables);
	}

	/**
	 * Renders the template and returns the resulting string.
	 * @return The rendered output as a string.
	 */
	public String renders()
	{
		return renders(null, null);
	}

	/**
	 * Renders the template and returns the resulting string.
	 * @param context   the EvaluationContext. May be null.
	 * @return The rendered output as a string.
	 */
	public String renders(EvaluationContext context)
	{
		return renders(context, null);
	}

	/**
	 * Renders the template and returns the resulting string.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the function code. May be null.
	 *                  These variables will be checked against the signature
	 *                  of the template (if a signature is defined, otherwise
	 *                  all variables will be accepted)
	 * @return The rendered output as a string.
	 */
	public String renders(Map<String, Object> variables)
	{
		return renders(null, variables);
	}

	/**
	 * Renders the template using the passed in variables and returns the resulting string.
	 * @param context   the EvaluationContext. May be null.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the function code. May be null.
	 *                  These variables will be checked against the signature
	 *                  of the template (if a signature is defined, otherwise
	 *                  all variables will be accepted)
	 * @return The rendered output as a string.
	 */
	public String renders(EvaluationContext context, Map<String, Object> variables)
	{
		StringWriter output = new StringWriter();
		render(context, output, variables);
		return output.toString();
	}

	/**
	 * Renders the template using the passed in variables.
	 * @param context   the EvaluationContext. May be null.
	 * @param writer    the java.io.Writer object to which the output is written.
	 *                  Maybe null, then the context's writer will be used.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the function code. May be null.
	 *                  These variables will be checked against the signature
	 *                  of the template (if a signature is defined, otherwise
	 *                  all variables will be accepted)
	 * @return the return value of the function
	 */
	public void render(EvaluationContext context, Writer writer, Map<String, Object> variables)
	{
		BoundArguments arguments = new BoundArguments(signature, this, null, variables);
		try
		{
			renderBound(context, writer, arguments.byName());
		}
		finally
		{
			arguments.cleanup();
		}
	}

	/**
	 * Internal method that renders the template when all variables are already
	 * bound.
	 * @param context   the EvaluationContext. May be null.
	 * @param writer    the java.io.Writer object to which the output is written.
	 *                  Maybe null, then the context's writer will be used.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the function code. May be null.
	 */
	public void renderBound(EvaluationContext context, java.io.Writer writer, Map<String, Object> variables)
	{
		boolean contextIsLocal = (context == null);
		Writer oldWriter = null;
		if (context == null)
			context = new EvaluationContext(writer);

		Map<String, Object> oldVariables = context.setVariables(variables);

		if (writer != null)
			oldWriter = context.setWriter(writer);

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

			if (writer != null)
				context.setWriter(oldWriter);

			context.setVariables(oldVariables);

			// If we created the context locally, we are responsible for cleaning up, otherwise it's the responsibility of our caller
			if (contextIsLocal)
				context.close();
		}
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
		BoundArguments arguments = new BoundArguments(signature, this, args, kwargs);
		Object result = null;
		try
		{
			result = callBound(context, arguments.byName());
		}
		finally
		{
			arguments.cleanup();
		}
		return result;
	}

	/**
	 * Executes the function.
	 * @return the return value of the function
	 */
	public Object call()
	{
		return call(null, null);
	}

	/**
	 * Executes the function.
	 * @param context   the EvaluationContext.
	 * @return the return value of the function
	 */
	public Object call(EvaluationContext context)
	{
		return call(context, null);
	}

	/**
	 * Executes the function.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the function code. May be null.
	 *                  These variables will be checked against the signature
	 *                  of the template (if a signature is defined, otherwise
	 *                  all variables will be accepted)
	 *                 
	 * @return the return value of the function
	 */
	public Object call(Map<String, Object> variables)
	{
		return call(null, variables);
	}

	/**
	 * Executes the function using the passed in variables.
	 * @param context   the EvaluationContext. May be null.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the function code. May be null.
	 *                  These variables will be checked against the signature
	 *                  of the template (if a signature is defined, otherwise
	 *                  all variables will be accepted)
	 * @return the return value of the function
	 */
	public Object call(EvaluationContext context, Map<String, Object> variables)
	{
		BoundArguments arguments = new BoundArguments(signature, this, null, variables);
		Object result = null;
		try
		{
			result = callBound(context, arguments.byName());
		}
		finally
		{
			arguments.cleanup();
		}
		return result;
	}

	/**
	 * Internal method that executes the function when all variables are already
	 * bound.
	 * @param context   the EvaluationContext. May be null.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the function code. May be null.
	 * @return the return value of the function
	 */
	public Object callBound(EvaluationContext context, Map<String, Object> variables)
	{
		boolean contextIsLocal = (context == null);

		if (context == null)
			context = new EvaluationContext(null);

		Map<String, Object> oldVariables = context.setVariables(variables);

		Writer oldWriter = context.setWriter(null);

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

			context.setWriter(oldWriter);

			context.setVariables(oldVariables);

			// If we created the context locally, we are responsible for cleaning up, otherwise it's the responsibility of our caller
			if (contextIsLocal)
				context.close();
		}
	}

	public Object evaluate(EvaluationContext context)
	{
		context.set(name, new TemplateClosure(this, context));
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

	/**
	 * Split the template source into tags and literal text.
	 * @param source The sourcecode of the template
	 * @param startdelim The start delimiter for template tags (usually {@code "<?"})
	 * @param enddelim The end delimiter for template tags (usually {@code "?>"})
	 * @return A list of {@link Tag} or {@link TextAST} objects
	 */
	public List<Object> tokenizeTags(String source, String startdelim, String enddelim)
	{
		Pattern tagPattern = Pattern.compile(escapeREchars(startdelim) + "\\s*(printx|print|code|for|while|if|elif|else|end|break|continue|def|return|note)(\\s*(.*?)\\s*)?" + escapeREchars(enddelim), Pattern.DOTALL);
		LinkedList<Object> tags = new LinkedList<Object>();
		if (source != null)
		{
			Matcher matcher = tagPattern.matcher(source);
			int pos = 0;

			int startPos;
			int endPos;
			while (matcher.find())
			{
				startPos = matcher.start();
				endPos = startPos + matcher.group().length();
				if (pos != startPos)
					tags.add(new TextAST(source, pos, startPos));
				int startPosCode = matcher.start(3);
				int endPosCode = startPosCode + matcher.group(3).length();
				String type = matcher.group(1);
				if (!type.equals("note"))
					tags.add(new Tag(source, matcher.group(1), startPos, endPos, startPosCode, endPosCode));
				pos = endPos;
			}
			endPos = source.length();
			if (pos != endPos)
				tags.add(new TextAST(source, pos, endPos));
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

	public void finish(Tag endtag)
	{
		super.finish(endtag);
		String type = endtag.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("def"))
			throw new BlockException("def ended by end" + type);
	}

	public boolean handleLoopControl(String name)
	{
		throw new BlockException(name + " outside of for/while loop");
	}

	static public void register4UL4ON()
	{
		Utils.register("de.livinglogic.ul4.tag", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Tag(null, null, -1, -1, -1, -1); }});
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
		Utils.register("de.livinglogic.ul4.template", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.InterpretedTemplate(); }});
		Utils.register("de.livinglogic.ul4.signature", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SignatureAST(null, -1, -1); }});
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
		encoder.dump(whitespace.toString());
		encoder.dump(startdelim);
		encoder.dump(enddelim);

		dumpSignatureUL4ON(encoder);

		super.dumpUL4ON(encoder);
	}

	private void dumpSignatureUL4ON(Encoder encoder) throws IOException
	{
		if (signature != null)
		{
			List paramsDump = null;
			paramsDump = new LinkedList();
			for (ArgumentDescription argdesc : signature)
			{
				if (argdesc.hasDefaultValue())
				{
					paramsDump.add(argdesc.getName() + "=");
					paramsDump.add(argdesc.getDefaultValue());
				}
				else
					paramsDump.add(argdesc.getName());
			}
			if (signature.remainingParametersName != null)
				paramsDump.add("*" + signature.remainingParametersName);
			if (signature.remainingKeywordParametersName != null)
				paramsDump.add("**" + signature.remainingKeywordParametersName);
			encoder.dump(paramsDump);
		}
		else
			encoder.dump(signatureAST);
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
		whitespace = Whitespace.fromString((String)decoder.load());
		startdelim = (String)decoder.load();
		enddelim = (String)decoder.load();

		loadSignatureUL4ON(decoder);

		super.loadUL4ON(decoder);
	}

	private void loadSignatureUL4ON(Decoder decoder) throws IOException
	{
		Object paramsDump = decoder.load();
		Signature signature;

		if (paramsDump == null)
		{
			this.signature = null;
			this.signatureAST = null;
		}
		else if (paramsDump instanceof SignatureAST)
		{
			this.signature = null;
			this.signatureAST = (SignatureAST)paramsDump;
		}
		else
		{
			signature = new Signature();
			boolean nextDefault = false;
			String paramName = null;
			for (Object param : (List)paramsDump)
			{
				if (nextDefault)
				{
					signature.add(paramName, param);
					nextDefault = false;
				}
				else
				{
					paramName = (String)param;
					if (paramName.endsWith("="))
					{
						paramName = paramName.substring(0, paramName.length()-1);
						nextDefault = true;
					}
					else if (paramName.startsWith("**"))
						signature.setRemainingKeywordParameters(paramName.substring(2));
					else if (paramName.startsWith("*"))
						signature.setRemainingParameters(paramName.substring(1));
					else
						signature.add(paramName);
				}
			}
			this.signature = signature;
			this.signatureAST = null;
		}
	}

	private static class BoundMethodRenderS extends BoundMethodWithContext<InterpretedTemplate>
	{
		public BoundMethodRenderS(InterpretedTemplate object)
		{
			super(object);
		}

		public String nameUL4()
		{
			String name = object.nameUL4();
			return (name != null ? name : "template") + ".renders";
		}

		public Signature getSignature()
		{
			return object.signature;
		}

		public Object evaluate(EvaluationContext context, BoundArguments arguments)
		{
			Writer writer = new StringWriter();
			object.renderBound(context, writer, arguments.byName());
			return writer.toString();
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
			String name = object.nameUL4();
			return (name != null ? name : "template") + ".render";
		}

		public Signature getSignature()
		{
			return object.signature;
		}

		public Object evaluate(EvaluationContext context, BoundArguments arguments)
		{
			object.renderBound(context, null, arguments.byName());
			return null;
		}
	}

	protected static Set<String> attributes = makeExtendedSet(BlockAST.attributes, "name", "whitespace", "startdelim", "enddelim", "source", "render", "renders");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("name".equals(key))
			return name;
		else if ("whitespace".equals(key))
			return whitespace.toString();
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
