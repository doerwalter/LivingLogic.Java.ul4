/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Iterator;
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


public class InterpretedTemplate extends BlockAST implements UL4Name, UL4CallWithContext, UL4RenderWithContext, UL4Type, UL4GetItemString, UL4Attributes
{
	/**
	 * The version number used in the UL4ON dump of the template.
	 */
	public static final String VERSION = "33";

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
				throw new WhitespaceException(value);
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
			try
			{
				Signature sig = ast.evaluate(context);
				this.signature = sig;
			}
			finally
			{
				context.close();
			}
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

	protected void handleSpecialTags(List<Line> lines) throws RecognitionException
	{
		for (Line line : lines)
		{
			for (SourcePart part : line)
			{
				if (part instanceof Tag)
				{
					Tag tag = (Tag)part;
					String tagtype = tag.getTag();
					if (tagtype.equals("whitespace"))
						whitespace = Whitespace.fromString(tag.getCode());
					else if (tagtype.equals("ul4"))
					{
						UL4Parser parser = getParser(tag);
						Definition definition = parser.definition();
						name = definition.getName();
						SignatureAST signatureAST = definition.getSignature();
						if (signatureAST != null)
						{
							EvaluationContext context = new EvaluationContext();
							try
							{
								signature = signatureAST.evaluate(context);
							}
							finally
							{
								context.close();
							}
						}
						else
							signature = null;
					}
				}
			}
		}
	}

	private List<SourcePart> handleWhitespaceKeep(List<Line> lines)
	{
		List<SourcePart> parts = new LinkedList<SourcePart>();
		for (Line line : lines)
		{
			for (SourcePart part : line)
				parts.add(part);
		}
		return parts;
	}

	private List<SourcePart> handleWhitespaceStrip(List<Line> lines)
	{
		List<SourcePart> parts = new LinkedList<SourcePart>();

		boolean first = true;
		for (Line line : lines)
		{
			for (SourcePart part : line)
			{
				if (first || !(part instanceof IndentAST || part instanceof LineEndAST))
				{
					parts.add(part);
					first = false;
				}
			}
		}
		return parts;
	}

	private List<SourcePart> handleWhitespaceSmart(List<Line> lines)
	{
		// FIXME: Implement this
		return handleWhitespaceKeep(lines);
	}


	protected void compile() throws RecognitionException
	{
		if (source == null)
			return;

		List<Line> lines = tokenizeTags(source, startdelim, enddelim);

		handleSpecialTags(lines);

		List<SourcePart> parts;

		switch (whitespace)
		{
			case keep:
				parts = handleWhitespaceKeep(lines);
				break;
			case strip:
				parts = handleWhitespaceStrip(lines);
				break;
			case smart:
				parts = handleWhitespaceSmart(lines);
				break;
			default: // This can not happen, but prevents a "variable parts might not have been initialized" compiler error.
				parts = null;
				break;
		}

		// Stack of currently active blocks
		Stack<BlockAST> stack = new Stack<BlockAST>();
		stack.push(this);

		for (SourcePart part : parts)
		{
			BlockAST innerBlock = stack.peek();
			if (part instanceof TextAST)
			{
				innerBlock.append((TextAST)part);
			}
			else
			{
				Tag tag = (Tag)part;
				try
				{
					String tagtype = tag.getTag();
					// FIXME: use a switch in Java 7
					if (tagtype.equals("ul4"))
					{
						// already handled
					}
					else if (tagtype.equals("whitespace"))
					{
						// already handled
					}
					else if (tagtype.equals("print"))
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
					else if (tagtype.equals("render"))
					{
						UL4Parser parser = getParser(tag);
						CodeAST code = parser.expression();
						if (!(code instanceof CallAST))
							throw new RuntimeException("render call required");
						RenderAST render = new RenderAST((CallAST)code);
						innerBlock.append(render);
					}
					else
					{
						// Can't happen
						throw new RuntimeException("unknown tag " + tagtype);
					}
				}
				catch (Exception ex)
				{
					throw new SourceException(ex, this, tag);
				}
			}
		}
		if (stack.size() > 1) // the template itself is still on the stack
		{
			BlockAST innerBlock = stack.peek();
			throw new SourceException(new BlockException(innerBlock.getType() + " block unclosed"), this, innerBlock);
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
		if (signatureAST != null)
			signatureAST.toString(formatter);
		else if (signature != null)
			formatter.write(signature.toString());
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
		EvaluationContext context = new EvaluationContext();
		try
		{
			render(context, writer, variables);
		}
		finally
		{
			context.close();
		}
	}

	/**
	 * Renders the template and returns the resulting string.
	 * @return The rendered output as a string.
	 */
	public String renders()
	{
		EvaluationContext context = new EvaluationContext();
		try
		{
			return renders(context, null);
		}
		finally
		{
			context.close();
		}
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

	public void renderUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		BoundArguments arguments = new BoundArguments(signature, this, args, kwargs);
		context.registerCloseable(arguments);
		try
		{
			renderBound(context, null, arguments.byName());
		}
		finally
		{
			// no cleanup here, as the render call might leak a closure to the outside world
		}
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
		EvaluationContext context = new EvaluationContext();
		try
		{
			return renders(context, variables);
		}
		finally
		{
			context.close();
		}
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
		context.registerCloseable(arguments);
		try
		{
			renderBound(context, writer, arguments.byName());
		}
		finally
		{
			// no cleanup here, as the render call might leak a closure to the outside world
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
		Writer oldWriter = null;

		Map<String, Object> oldVariables = context.setVariables(variables);

		if (writer != null)
			oldWriter = context.setWriter(writer);

		InterpretedTemplate oldTemplate = context.setTemplate(this);

		try
		{
			super.evaluate(context);
		}
		catch (ReturnException ex)
		{
			// ignore return value and end rendering
		}
		finally
		{
			context.setTemplate(oldTemplate);

			if (writer != null)
				context.setWriter(oldWriter);

			context.setVariables(oldVariables);
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
		context.registerCloseable(arguments);
		Object result = null;
		try
		{
			result = callBound(context, arguments.byName());
		}
		finally
		{
			// no cleanup here, as the result might be a closure that still needs the local variables
		}
		return result;
	}

	/**
	 * Executes the function.
	 * @return the return value of the function
	 */
	public Object call()
	{
		EvaluationContext context = new EvaluationContext();
		try
		{
			return call(context, null);
		}
		finally
		{
			context.close();
		}
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
		EvaluationContext context = new EvaluationContext();
		try
		{
			return call(context, variables);
		}
		finally
		{
			context.close();
		}
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
		context.registerCloseable(arguments);
		Object result = null;
		try
		{
			return callBound(context, arguments.byName());
		}
		finally
		{
			// no cleanup here, as the result might be a closure that still needs the local variables
		}
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
		Map<String, Object> oldVariables = context.setVariables(variables);

		Writer oldWriter = context.setWriter(null);

		InterpretedTemplate oldTemplate = context.setTemplate(this);

		try
		{
			super.evaluate(context);
			return null;
		}
		catch (ReturnException ex)
		{
			return ex.getValue();
		}
		finally
		{
			context.setTemplate(oldTemplate);

			context.setWriter(oldWriter);

			context.setVariables(oldVariables);
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

	/**
	 * Internal method that returns wether a character is a line ending character
	 * or not.
	 * @param c   The character to test
	 * @return {@code true} if {@code c} is a line ending character, else {@code false}
	 */
	private static boolean isLineEnd(char c)
	{
		return c == '\u2007' || c == '\u202F' || c == '\f' || c == '\r' || c == '\n';
	}

	private void addPart2Lines(List<Line> lines, SourcePart part)
	{
		int lineCount = lines.size();
		Line lastLine;

		if (lineCount > 0)
			lastLine = lines.get(lines.size()-1);
		else
		{
			lastLine = new Line();
			lines.add(lastLine);
		}

		// If we're adding to an empty line ensure that it starts with an indentation
		if (!(part instanceof IndentAST) && lastLine.size() == 0)
			lastLine.add(new IndentAST(part.getSource(), part.getStartPos(), part.getStartPos(), null));
		lastLine.add(part);
		// If we added a line end append a new empty line
		if (part instanceof LineEndAST)
			lines.add(new Line());
	}

	private void addText2Lines(List<Line> lines, TextAST text, int initialState)
	{
		String source = text.getSource();
		int startPos = text.getStartPos();
		int endPos = text.getEndPos();
		int pos = startPos;
		int state = initialState; // 0 for indentation, 1 for text and 2 for lineend
		boolean wasR = false;

		while (pos < endPos)
		{
			char c = source.charAt(pos);
			if (state == 0)
			{
				if (!isLineEnd(c) && Character.isWhitespace(c))
				{
					++pos;
				}
				else
				{
					if (pos != startPos)
						addPart2Lines(lines, new IndentAST(source, startPos, pos, null));
					startPos = pos++;
					state = isLineEnd(c) ? 2 : 1;
					wasR = (c == '\r');
				}
			}
			else if (state == 1)
			{
				if (isLineEnd(c))
				{
					if (pos != startPos)
						addPart2Lines(lines, new TextAST(source, startPos, pos));
					startPos = pos++;
					state = 2;
					wasR = (c == '\r');
				}
				else
				{
					++pos;
				}
			}
			else
			{
				if (isLineEnd(c))
				{
					if (wasR && c == '\n')
						++pos;
					else
					{
						if (pos != startPos)
							addPart2Lines(lines, new LineEndAST(source, startPos, pos));
						startPos = pos++;
					}
					wasR = (c == '\r');
				}
				else
				{
					addPart2Lines(lines, new LineEndAST(source, startPos, pos));
					state = (Character.isWhitespace(c)) ? 0 : 1;
					startPos = pos++;
				}
			}
		}
		if (startPos < endPos)
		{
			if (state == 0)
				addPart2Lines(lines, new IndentAST(source, startPos, pos, null));
			else if (state == 1)
				addPart2Lines(lines, new TextAST(source, startPos, pos));
			else
				addPart2Lines(lines, new LineEndAST(source, startPos, pos));
		}
	}

	/**
	 * Split the template source into tags and literal text.
	 * @param source The sourcecode of the template
	 * @param startdelim The start delimiter for template tags (usually {@code "<?"})
	 * @param enddelim The end delimiter for template tags (usually {@code "?>"})
	 * @return A list of lines containing {@link Tag} or {@link TextAST} objects
	 */
	public List<Line> tokenizeTags(String source, String startdelim, String enddelim)
	{
		Pattern tagPattern = Pattern.compile(escapeREchars(startdelim) + "\\s*(ul4|whitespace|printx|print|code|for|while|if|elif|else|end|break|continue|def|return|note|render)(\\s*(.*?)\\s*)?" + escapeREchars(enddelim), Pattern.DOTALL);
		LinkedList<Line> lines = new LinkedList<Line>();
		boolean wasTag = false;
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
				{
					addText2Lines(lines, new TextAST(source, pos, startPos), wasTag ? 1 : 0);
					wasTag = false;
				}
				int startPosCode = matcher.start(3);
				int endPosCode = startPosCode + matcher.group(3).length();
				String type = matcher.group(1);
				if (!type.equals("note"))
					addPart2Lines(lines, new Tag(source, matcher.group(1), startPos, endPos, startPosCode, endPosCode));
				pos = endPos;
				wasTag = true;
			}
			endPos = source.length();
			if (pos != endPos)
			{
				addText2Lines(lines, new TextAST(source, pos, endPos), wasTag ? 1 : 0);
				wasTag = false;
			}
		}
		return lines;
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

	private static class Line implements Iterable<SourcePart>
	{
		private List<SourcePart> parts;

		public Line()
		{
			parts = new LinkedList<SourcePart>();
		}

		public String indent()
		{
			if (size() > 0)
			{
				SourcePart start = get(0);
				if (start instanceof IndentAST)
					return ((IndentAST)start).getText();
			}
			return "";
		}

		public void add(SourcePart part)
		{
			parts.add(part);
		}

		public Iterator<SourcePart> iterator()
		{
			return parts.iterator();
		}

		public int size()
		{
			return parts.size();
		}

		public SourcePart get(int i)
		{
			return parts.get(i);
		}

		public boolean isEmpty()
		{
			for (SourcePart part : parts)
			{
				if (!(part instanceof IndentAST) && !(part instanceof LineEndAST))
					return false;
			}
			return true;
		}
	}

	static public void register4UL4ON()
	{
		Utils.register("de.livinglogic.ul4.tag", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Tag(null, null, -1, -1, -1, -1); }});
		Utils.register("de.livinglogic.ul4.text", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.TextAST(null, -1, -1); }});
		Utils.register("de.livinglogic.ul4.indent", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.IndentAST(null, -1, -1, null); }});
		Utils.register("de.livinglogic.ul4.lineend", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LineEndAST(null, -1, -1); }});
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
		Utils.register("de.livinglogic.ul4.is", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.IsAST(null, -1, -1, null, null); }});
		Utils.register("de.livinglogic.ul4.isnot", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.IsNotAST(null, -1, -1, null, null); }});
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
		Utils.register("de.livinglogic.ul4.render", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.RenderAST(null, -1, -1, null); }});
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
				switch (argdesc.getType())
				{
					case REQUIRED:
						paramsDump.add(argdesc.getName());
						break;
					case DEFAULT:
						paramsDump.add(argdesc.getName() + "=");
						paramsDump.add(argdesc.getDefaultValue());
						break;
					case VAR_POSITIONAL:
						paramsDump.add("*" + argdesc.getName());
						break;
					case VAR_KEYWORD:
						paramsDump.add("**" + argdesc.getName());
						break;
				}
			}
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
					signature.add(paramName, ArgumentDescription.Type.DEFAULT, param);
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
						signature.add(paramName.substring(2), ArgumentDescription.Type.VAR_KEYWORD, null);
					else if (paramName.startsWith("*"))
						signature.add(paramName.substring(1), ArgumentDescription.Type.VAR_POSITIONAL, null);
					else
						signature.add(paramName, ArgumentDescription.Type.REQUIRED, null);
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

	protected static Set<String> attributes = makeExtendedSet(BlockAST.attributes, "name", "whitespace", "startdelim", "enddelim", "source", "renders");

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
		else if ("renders".equals(key))
			return new BoundMethodRenderS(this);
		else
			return super.getItemStringUL4(key);
	}
}
