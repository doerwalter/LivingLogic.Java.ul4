/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
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


public class InterpretedTemplate extends BlockAST implements UL4Name, UL4CallWithContext, UL4RenderWithContext, UL4Type, UL4GetAttr, UL4Dir
{
	/**
	 * The version number used in the UL4ON dump of the template.
	 */
	public static final String VERSION = "47";

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
	 * The slice for the docstring (from a {@code <?doc?>} tag).
	 */
	public Slice docPos = null;

	/**
	 * The template/function source (of the top-level template, i.e. subtemplates always get the full source).
	 */
	public String source = null;

	/**
	 * If this is a nested template, {@code parentTemplate} references the outer template
	 */
	public InterpretedTemplate parentTemplate = null;

	/**
	 * Used for deserializing an UL4ON dump (of top level and sub templates). Doesn't compile any source
	 */
	private InterpretedTemplate()
	{
		super(null, new Slice(0, 0), null);
		this.source = null;
		this.name = null;
		this.whitespace = Whitespace.keep;
		this.startdelim = startdelim != null ? startdelim : "<?";
		this.enddelim = enddelim != null ? enddelim : "?>";
		this.signature = null;
		this.signatureAST = null;
		this.docPos = null;
		this.parentTemplate = null;
	}

	/**
	 * Create of toplevel template without a signature
	 */
	public InterpretedTemplate(String source, String name, Whitespace whitespace, String startdelim, String enddelim)
	{
		super(null, new Slice(0, 0), null);
		int stop = source != null ? source.length() : 0; 
		setStopPos(stop, stop);
		this.template = this;
		// Make sure that the source is always a string (so that {@code getSource()} works)
		this.source = source != null ? source : "";
		this.name = name;
		this.whitespace = whitespace;
		this.startdelim = startdelim != null ? startdelim : "<?";
		this.enddelim = enddelim != null ? enddelim : "?>";
		this.signature = null;
		this.signatureAST = null;
		this.docPos = null;
		compile();
	}

	private static String makeSource(String source, String name, String startdelim, String enddelim, String signature)
	{
		if (signature != null)
		{
			StringBuilder buffer = new StringBuilder();
			buffer.append(startdelim != null ? startdelim : "<?");
			buffer.append("ul4 ");
			if (name != null)
				buffer.append(name);
			buffer.append("(");
			buffer.append(signature);
			buffer.append(")");
			buffer.append(enddelim != null ? enddelim : "?>");
			buffer.append(source);
			return buffer.toString();
		}
		else
			return source;
	}

	/**
	 * Create of toplevel template with a specified signature
	 */
	public InterpretedTemplate(String source, String name, Whitespace whitespace, String startdelim, String enddelim, Signature signature)
	{
		this(source, name, whitespace, startdelim, enddelim);
		if (this.signature == null) // signature from <?ul4?> tag wins
			this.signature = signature;
	}

	/**
	 * Create of toplevel template with a signature compiled from a string
	 */
	public InterpretedTemplate(String source, String name, Whitespace whitespace, String startdelim, String enddelim, String signature)
	{
		this(makeSource(source, name, startdelim, enddelim, signature), name, whitespace, startdelim, enddelim);
	}

	/**
	 * Creates an {@code InterpretedTemplate} object. Used for subtemplates.
	 */
	InterpretedTemplate(InterpretedTemplate template, String name, Whitespace whitespace, String startdelim, String enddelim, SignatureAST signature)
	{
		super(template, new Slice(0, 0), null);
		// Copy the full source instead of calling {@link getSource} (the full source is the source of the outermost template)
		this.source = template.getFullSource();
		int stop = source.length();
		setStopPos(stop, stop);
		this.name = name;
		this.whitespace = whitespace;
		this.startdelim = startdelim != null ? startdelim : "<?";
		this.enddelim = enddelim != null ? enddelim : "?>";
		this.signature = null;
		this.signatureAST = signature;
		this.docPos = null;
	}

	protected void handleSpecialTags(List<Line> lines)
	{
		for (Line line : lines)
		{
			for (AST part : line)
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
						Definition definition;
						try
						{
							definition = parser.definition();
						}
						catch (RuntimeException exc)
						{
							decorateException(exc);
							throw exc;
						}
						catch (Exception exc)
						{
							RuntimeException newexc = new RuntimeException(exc);
							decorateException(newexc);
							throw newexc;
						}
						name = definition.getName();
						SignatureAST signatureAST = definition.getSignature();
						if (signatureAST != null)
						{
							try (EvaluationContext context = new EvaluationContext())
							{
								signature = signatureAST.evaluate(context);
							}
						}
						else
							signature = null;
					}
				}
			}
		}
	}

	private List<AST> handleWhitespaceKeep(List<Line> lines)
	{
		List<AST> parts = new LinkedList<AST>();
		for (Line line : lines)
		{
			for (AST part : line)
				parts.add(part);
		}
		return parts;
	}

	private List<AST> handleWhitespaceStrip(List<Line> lines)
	{
		List<AST> parts = new LinkedList<AST>();

		boolean first = true;
		for (Line line : lines)
		{
			for (AST part : line)
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

	private List<AST> handleWhitespaceSmart(List<Line> lines)
	{
		// Step 1: Determine the block structure of the lines
		List<Block> blocks = new LinkedList<Block>(); // List of all blocks
		List<Block> stack = new LinkedList<Block>(); // Stack of currently "open" blocks

		List<BlockLine> newlines = new LinkedList<BlockLine>();

		int i = 0;
		for (Line line : lines)
		{
			String tagName = line.blockTagName();
			if (tagName == null)
			{
				newlines.add(new BlockLine(line, stack));
			}
			else
			{
				// Tags "closing" a block
				if ("elif".equals(tagName) || "else".equals(tagName) || "end".equals(tagName))
				{
					if (stack.size() > 0)
					{
						stack.get(stack.size()-1).endLine = i;
						stack.remove(stack.size()-1);
					}
				}
				newlines.add(new BlockLine(line, stack));
				// Tags "opening" a block
				if ("for".equals(tagName) || "if".equals(tagName) || "def".equals(tagName) || "elif".equals(tagName) || "else".equals(tagName) || "renderblock".equals(tagName))
				{
					Block block = new Block(i+1); // Block starts on the next line
					stack.add(block);
					blocks.add(block);
				}
			}
			++i;
		}

		// Close open blocks (shouldn't be necessary for properly nested templates, i.e. stack should be empty)
		int lineCount = lines.size();
		for (Block block : stack)
			block.endLine = lineCount;

		// Step 2: Find the outer and inner indentation of all blocks
		for (Block block : blocks)
			block.setIndent(lines);

		// Step 3: Fix the indentation
		Map<String, String> allIndents = new HashMap<String, String>(); // for "interning" indentation
		for (BlockLine blockLine : newlines)
		{
			Line line = blockLine.line;
			List<Block> lineStack = blockLine.stack;

			// use all character for indentation that are not part of the "artificial" indentation introduced in each block
			String oldIndent = line.indent();
			if (oldIndent.length() > 0)
			{
				StringBuilder newIndentBuilder = new StringBuilder();
				for (int j = 0; j < oldIndent.length(); ++j)
				{
					boolean ok = true;
					for (Block block : lineStack)
					{
						if (block.containsCol(j))
						{
							ok = false;
							break;
						}
					}
					if (ok)
						newIndentBuilder.append(oldIndent.charAt(j));
				}
				String newIndent = newIndentBuilder.toString();
				if (allIndents.get(newIndent) != null)
					newIndent = allIndents.get(newIndent);
				else
					allIndents.put(newIndent, newIndent);
				((IndentAST)line.get(0)).setText(newIndent);
			}
		}

		// Step 4: Drop whitespace from empty lines or lines that only contain indentation and block tags
		List<AST> parts = new LinkedList<AST>();
		for (Line line : lines)
		{
			if (line.size() == 2)
			{
				if (line.get(0) instanceof IndentAST)
				{
					if (line.get(1) instanceof LineEndAST)
					{
						parts.add(line.get(1));
						continue;
					}
					else if (line.get(1) instanceof Tag)
					{
						Tag tag = (Tag)line.get(1);
						if (!tag.tag.equals("print") && !tag.tag.equals("printx") && !tag.tag.equals("render") && !tag.tag.equals("renderx"))
						{
							parts.add(tag);
							continue;
						}
					}
				}
			}
			else if (line.size() == 3)
			{
				if (line.get(0) instanceof IndentAST && line.get(2) instanceof LineEndAST)
				{
					if (line.get(1) instanceof Tag)
					{
						Tag tag = (Tag)line.get(1);
						if (tag.tag.equals("render") || tag.tag.equals("renderx"))
						{
							parts.add(line.get(0)); // This will be moved into the render tag later
							parts.add(tag);
							continue;
						}
						else if (!tag.tag.equals("print") && !tag.tag.equals("printx"))
						{
							parts.add(tag);
							continue;
						}
					}
				}
			}
			// We get here when we never run into a "continue", i.e. when we didn't encounter a special case
			for (AST part : line)
				parts.add(part);
		}
		return parts;
	}

	protected void compile()
	{
		List<Line> lines = tokenizeTags();

		handleSpecialTags(lines);

		List<AST> parts;

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
		Stack<BlockLike> blockStack = new Stack<BlockLike>();
		blockStack.push(this);

		// Stack of currently active templates
		Stack<InterpretedTemplate> templateStack = new Stack<InterpretedTemplate>();
		templateStack.push(this);

		for (AST part : parts)
		{
			BlockLike innerBlock = blockStack.peek();
			if (part instanceof TextAST)
			{
				innerBlock.append((TextAST)part);
				((TextAST)part).setTemplate(templateStack.peek());
			}
			else
			{
				Tag tag = (Tag)part;
				// Update {@code tag.template} to reference the innermost template
				// (Originally it referenced the outermost one)
				tag.setTemplate(templateStack.peek());
				try
				{
					String tagtype = tag.getTag();
					switch (tagtype)
					{
						case "ul4":
						{
							// already handled
							break;
						}
						case "whitespace":
						{
							// already handled
							break;
						}
						case "doc":
						{
							// Only use the first {@code <?doc?>} tag in each template, ignore all later ones
							InterpretedTemplate innerTemplate = templateStack.peek();
							if (innerTemplate.docPos == null)
								innerTemplate.docPos = tag.getCodePos();
							break;
						}
						case "print":
						{
							UL4Parser parser = getParser(tag);
							innerBlock.append(new PrintAST(tag.getTemplate(), tag.getStartPos(), parser.expression()));
							break;
						}
						case "printx":
						{
							UL4Parser parser = getParser(tag);
							innerBlock.append(new PrintXAST(tag.getTemplate(), tag.getStartPos(), parser.expression()));
							break;
						}
						case "code":
						{
							UL4Parser parser = getParser(tag);
							innerBlock.append(parser.stmt());
							break;
						}
						case "if":
						{
							UL4Parser parser = getParser(tag);
							ConditionalBlocks node = new ConditionalBlocks(tag.getTemplate(), tag.getStartPos(), null, new IfBlockAST(tag.getTemplate(), tag.getStartPos(), null, parser.expression()));
							innerBlock.append(node);
							blockStack.push(node);
							break;
						}
						case "elif":
						{
							if (innerBlock instanceof ConditionalBlocks)
							{
								UL4Parser parser = getParser(tag);
								((ConditionalBlocks)innerBlock).startNewBlock(new ElIfBlockAST(tag.getTemplate(), tag.getStartPos(), null, parser.expression()));
							}
							else
								throw new BlockException("elif doesn't match any if");
							break;
						}
						case "else":
						{
							if (innerBlock instanceof ConditionalBlocks)
							{
								((ConditionalBlocks)innerBlock).startNewBlock(new ElseBlockAST(tag.getTemplate(), tag.getStartPos(), null));
							}
							else
								throw new BlockException("else doesn't match any if");
							break;
						}
						case "end":
						{
							if (blockStack.size() > 1)
							{
								innerBlock.finish(tag);
								blockStack.pop();
								if (innerBlock instanceof InterpretedTemplate)
									templateStack.pop();
							}
							else
								throw new BlockException("not in any block");
							break;
						}
						case "for":
						{
							UL4Parser parser = getParser(tag);
							BlockAST node = parser.for_();
							innerBlock.append(node);
							blockStack.push(node);
							break;
						}
						case "while":
						{
							UL4Parser parser = getParser(tag);
							WhileBlockAST node = new WhileBlockAST(tag.getTemplate(), tag.getStartPos(), null, parser.expression());
							innerBlock.append(node);
							blockStack.push(node);
							break;
						}
						case "break":
						{
							for (int i = blockStack.size()-1; i >= 0; --i)
							{
								if (blockStack.get(i).handleLoopControl("break"))
									break;
							}
							innerBlock.append(new BreakAST(tag.getTemplate(), tag.getStartPos()));
							break;
						}
						case "continue":
						{
							for (int i = blockStack.size()-1; i >= 0; --i)
							{
								if (blockStack.get(i).handleLoopControl("continue"))
									break;
							}
							innerBlock.append(new ContinueAST(tag.getTemplate(), tag.getStartPos()));
							break;
						}
						case "return":
						{
							UL4Parser parser = getParser(tag);
							innerBlock.append(new ReturnAST(tag.getTemplate(), tag.getStartPos(), parser.expression()));
							break;
						}
						case "def":
						{
							UL4Parser parser = getParser(tag);
							Definition definition = parser.definition();
							// Copy over all the attributes, however passing an {@link InterpretedTemplate} will prevent compilation
							InterpretedTemplate subtemplate = new InterpretedTemplate(tag.getTemplate(), definition.getName(), whitespace, startdelim, enddelim, definition.getSignature());
							innerBlock.append(subtemplate);
							blockStack.push(subtemplate);
							subtemplate.parentTemplate = tag.getTemplate();
							subtemplate.setTemplate(subtemplate);
							subtemplate.setStartPos(tag.getStartPos());
							tag.setTemplate(subtemplate);
							templateStack.push(subtemplate);
							break;
						}
						case "render":
						case "renderx":
						{
							UL4Parser parser = getParser(tag);
							CodeAST code = parser.expression();
							if (!(code instanceof CallAST))
								throw new RuntimeException("render call required");
							RenderAST render;
							switch (tagtype)
							{
								case "render":
									render = new RenderAST((CallAST)code);
									break;
								case "renderx":
									render = new RenderXAST((CallAST)code);
									break;
								// can't happen
								default:
									render = null;
									break;
							}
							render.stealIndent(innerBlock);
							innerBlock.append(render);
							break;
						}
						case "renderblock":
						{
							UL4Parser parser = getParser(tag);
							CodeAST code = parser.expression();
							if (!(code instanceof CallAST))
								throw new RuntimeException("render call required");
							RenderBlockAST render = new RenderBlockAST(templateStack.peek(), (CallAST)code, whitespace, startdelim, enddelim);
							render.setStartPos(tag.getStartPos());
							render.stealIndent(innerBlock);
							innerBlock.append(render);
							blockStack.push(render);
							break;
						}
						case "renderblocks":
						{
							UL4Parser parser = getParser(tag);
							CodeAST code = parser.expression();
							if (!(code instanceof CallAST))
								throw new RuntimeException("render call required");
							RenderBlocksAST render = new RenderBlocksAST((CallAST)code);
							render.setStartPos(tag.getStartPos());
							render.stealIndent(innerBlock);
							innerBlock.append(render);
							blockStack.push(render);
							break;
						}
						default:
							// Can't happen
							throw new RuntimeException("unknown tag " + tagtype);
					}
				}
				catch (RuntimeException ex)
				{
					tag.decorateException(ex);
					throw ex;
				}
				catch (Exception ex)
				{
					RuntimeException newex = new RuntimeException(ex);
					tag.decorateException(newex);
					throw newex;
				}
			}
		}
		if (blockStack.size() > 1) // the template itself is still on the stack
		{
			BlockLike innerBlock = blockStack.peek();
			BlockException ex = new BlockException(innerBlock.getType() + " block unclosed");
			innerBlock.decorateException(ex);
			throw ex;
		}
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

	public InterpretedTemplate getParentTemplate()
	{
		return parentTemplate;
	}

	// We have to implement this, otherwise {@code AST.getFullSource()} would lead to infinite recursions.
	@Override
	public String getFullSource()
	{
		return source;
	}

	public Whitespace getWhitespace()
	{
		return whitespace;
	}

	public String getDoc()
	{
		return docPos != null ? docPos.getFrom(source) : null;
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

	public void toString(AST.Formatter formatter)
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
		return (InterpretedTemplate)Utils.loads(data, null);
	}

	/**
	 * loads a template from a reader in the UL4ON serialization format.
	 * @param reader The Reader object from which to read the template.
	 * @return The template object.
	 * @throws IOException if reading from the stream fails
	 */
	public static InterpretedTemplate load(Reader reader) throws IOException
	{
		return (InterpretedTemplate)Utils.load(reader, null);
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
		try (EvaluationContext context = new EvaluationContext())
		{
			return renders(context, null);
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
		renderBound(context, null, arguments.byName());
		// no cleanup here, as the render call might leak a closure to the outside world
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
		try (EvaluationContext context = new EvaluationContext())
		{
			return renders(context, variables);
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
	 */
	public void render(EvaluationContext context, Writer writer, Map<String, Object> variables)
	{
		BoundArguments arguments = new BoundArguments(signature, this, null, variables);
		context.registerCloseable(arguments);
		renderBound(context, writer, arguments.byName());
		// no cleanup here, as the render call might leak a closure to the outside world
	}

	/**
	 * Internal method that renders the template when all variables are already
	 * bound.
	 * @param context   the EvaluationContext. May not be null.
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
		result = callBound(context, arguments.byName());
		// no cleanup here, as the result might be a closure that still needs the local variables
		return result;
	}

	/**
	 * Executes the function.
	 * @return the return value of the function
	 */
	public Object call()
	{
		try (EvaluationContext context = new EvaluationContext())
		{
			return call(context, null);
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
		return callBound(context, arguments.byName());
		// no cleanup here, as the result might be a closure that still needs the local variables
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

	@Override
	public String getType()
	{
		return "template";
	}

	public String typeUL4()
	{
		return "template";
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		if (name != null)
		{
			formatter.append(" name=");
			formatter.visit(name);
		}
		if (whitespace != Whitespace.keep)
		{
			formatter.append(" whitespace=");
			formatter.visit(whitespace.toString());
		}
		if (!"<?".equals(startdelim))
		{
			formatter.append(" startdelim=");
			formatter.visit(startdelim);
		}
		if (!"?>".equals(enddelim))
		{
			formatter.append(" enddelim=");
			formatter.visit(enddelim);
		}
		if (signature != null)
		{
			formatter.append(" signature=");
			formatter.append(signature.toString());
		}
		if (signatureAST != null)
		{
			formatter.append(" signatureAST=");
			formatter.append(signatureAST.toString());
		}
		formatter.append(">");
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

	private void addPart2Lines(List<Line> lines, AST part)
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
		{
			int start = part.getStartPos().getStart();
			lastLine.add(new IndentAST(this, new Slice(start, start), null));
		}
		lastLine.add(part);
		// If we added a line end append a new empty line
		if (part instanceof LineEndAST)
			lines.add(new Line());
	}

	private void addText2Lines(List<Line> lines, TextAST text, int initialState)
	{
		int startPos = text.getStartPos().getStart();
		int stopPos = text.getStartPos().getStop();
		int pos = startPos;
		int state = initialState; // 0 for indentation, 1 for text and 2 for lineend
		boolean wasR = false;

		while (pos < stopPos)
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
						addPart2Lines(lines, new IndentAST(this, new Slice(startPos, pos), null));
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
						addPart2Lines(lines, new TextAST(this, new Slice(startPos, pos)));
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
							addPart2Lines(lines, new LineEndAST(this, new Slice(startPos, pos)));
						startPos = pos++;
					}
					wasR = (c == '\r');
				}
				else
				{
					addPart2Lines(lines, new LineEndAST(this, new Slice(startPos, pos)));
					state = (Character.isWhitespace(c)) ? 0 : 1;
					startPos = pos++;
				}
			}
		}
		if (startPos < stopPos)
		{
			if (state == 0)
				addPart2Lines(lines, new IndentAST(this, new Slice(startPos, pos), null));
			else if (state == 1)
				addPart2Lines(lines, new TextAST(this, new Slice(startPos, pos)));
			else
				addPart2Lines(lines, new LineEndAST(this, new Slice(startPos, pos)));
		}
	}

	/**
	 * Split the template source into tags and literal text.
	 * @return A list of lines containing {@link Tag} or {@link TextAST} objects
	 */
	public List<Line> tokenizeTags()
	{
		Pattern tagPattern = Pattern.compile(escapeREchars(startdelim) + "\\s*(ul4|whitespace|printx|print|code|for|while|if|elif|else|end|break|continue|def|return|note|doc|renderblocks|renderblock|renderx|render)(\\s*(.*?)\\s*)?" + escapeREchars(enddelim), Pattern.DOTALL);
		LinkedList<Line> lines = new LinkedList<Line>();
		boolean wasTag = false;
		if (source != null)
		{
			Matcher matcher = tagPattern.matcher(source);
			int pos = 0;

			int tagStartPos;
			int tagStopPos;
			while (matcher.find())
			{
				tagStartPos = matcher.start();
				tagStopPos = tagStartPos + matcher.group().length();
				if (pos != tagStartPos)
				{
					addText2Lines(lines, new TextAST(this, new Slice(pos, tagStartPos)), wasTag ? 1 : 0);
					wasTag = false;
				}
				int codeStartPos = matcher.start(3);
				int codeStopPos = codeStartPos + matcher.group(3).length();
				String type = matcher.group(1);
				if (!type.equals("note"))
					addPart2Lines(lines, new Tag(this, matcher.group(1), new Slice(tagStartPos, tagStopPos), new Slice(codeStartPos, codeStopPos)));
				pos = tagStopPos;
				wasTag = true;
			}
			tagStopPos = source.length();
			if (pos != tagStopPos)
			{
				addText2Lines(lines, new TextAST(this, new Slice(pos, tagStopPos)), wasTag ? 1 : 0);
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

	@Override
	public void finish(Tag endtag)
	{
		String type = endtag.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("def"))
			throw new BlockException("def ended by end" + type);
		super.finish(endtag);
	}

	public boolean handleLoopControl(String name)
	{
		throw new BlockException(name + " outside of for/while loop");
	}

	private static class Line implements Iterable<AST>
	{
		private List<AST> parts;

		public Line()
		{
			parts = new LinkedList<AST>();
		}

		public String indent()
		{
			if (size() > 0)
			{
				AST start = get(0);
				if (start instanceof IndentAST)
					return ((IndentAST)start).getText();
			}
			return "";
		}

		public void add(AST part)
		{
			parts.add(part);
		}

		public Iterator<AST> iterator()
		{
			return parts.iterator();
		}

		public int size()
		{
			return parts.size();
		}

		public AST get(int i)
		{
			return parts.get(i);
		}

		public boolean isEmpty()
		{
			for (AST part : parts)
			{
				if (!(part instanceof IndentAST) && !(part instanceof LineEndAST))
					return false;
			}
			return true;
		}

		public String blockTagName()
		{
			int size = size();
			if (2 <= size && size <= 3)
			{
				if (get(0) instanceof IndentAST && get(1) instanceof Tag)
				{
					Tag tag = (Tag)get(1);
					String tagName = tag.tag;
					if ("def".equals(tagName) || "for".equals(tagName) || "if".equals(tagName) || "elif".equals(tagName) || "else".equals(tagName) || "end".equals(tagName))
					{
						if (size == 2 || get(2) instanceof LineEndAST)
							return tagName;
					}
				}
			}
			return null;
		}
	}

	private static String commonPrefix(String s1, String s2)
	{
		int length1 = s1.length();
		int length2 = s2.length();
		int commonLength = Math.min(s1.length(), s2.length());
		for (int i = 0; i < commonLength; ++i)
		{
			if (s1.charAt(i) != s2.charAt(i))
				return s1.substring(0, i);
		}
		return s1.substring(0, commonLength);
	}

	private static class Block
	{
		public int startLine;
		public int endLine;
		public int indentStartCol;
		public int indentEndCol;

		public Block(int startLine)
		{
			this.startLine = startLine;
			this.endLine = startLine;
			this.indentStartCol = -1;
			this.indentEndCol = -1;
		}

		public void setIndent(List<Line> lines)
		{
			// outer indent, i.e. the indentation of the start tag of the block
			indentStartCol = startLine == 0 ? 0 : lines.get(startLine-1).indent().length();

			// inner indentation (ignoring lines that only contain whitespace)
			String innerIndent = null;
			for (int i = startLine; i < endLine; ++i)
			{
				Line line = lines.get(i);
				if (!line.isEmpty())
				{
					String indentString = line.indent();
					if (innerIndent == null)
						innerIndent = indentString;
					else
						innerIndent = commonPrefix(innerIndent, indentString);
				}
			}
			indentEndCol = innerIndent.length();
		}

		public boolean containsCol(int col)
		{
			return indentStartCol <= col && col < indentEndCol;
		}
	}

	private static class BlockLine
	{
		public Line line;
		public List<Block> stack;

		BlockLine(Line line, List<Block> stack)
		{
			this.line = line;
			this.stack = new LinkedList<Block>(stack);
		}
	}

	static public void register4UL4ON()
	{
		Utils.register("de.livinglogic.ul4.text", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.TextAST(null, null); }});
		Utils.register("de.livinglogic.ul4.indent", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.IndentAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.lineend", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LineEndAST(null, null); }});
		Utils.register("de.livinglogic.ul4.const", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ConstAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.seqitem", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SeqItemAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.unpackseqitem", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.UnpackSeqItemAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.list", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ListAST(null, null); }});
		Utils.register("de.livinglogic.ul4.listcomp", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ListComprehensionAST(null, null, null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.set", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SetAST(null, null); }});
		Utils.register("de.livinglogic.ul4.setcomp", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SetComprehensionAST(null, null, null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.dictitem", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.DictItemAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.unpackdictitem", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.UnpackDictItemAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.dict", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.DictAST(null, null); }});
		Utils.register("de.livinglogic.ul4.dictcomp", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.DictComprehensionAST(null, null, null, null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.genexpr", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GeneratorExpressionAST(null, null, null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.var", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.VarAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.condblock", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ConditionalBlocks(null, null, null); }});
		Utils.register("de.livinglogic.ul4.ifblock", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.IfBlockAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.elifblock", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ElIfBlockAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.elseblock", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ElseBlockAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.forblock", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ForBlockAST(null, null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.whileblock", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.WhileBlockAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.break", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BreakAST(null, null); }});
		Utils.register("de.livinglogic.ul4.continue", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ContinueAST(null, null); }});
		Utils.register("de.livinglogic.ul4.attr", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.AttrAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.slice", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SliceAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.not", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.NotAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.if", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.IfAST(null, null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.neg", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.NegAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.bitnot", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BitNotAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.print", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.PrintAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.printx", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.PrintXAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.return", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ReturnAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.item", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ItemAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.shiftleft", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ShiftLeftAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.shiftright", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ShiftRightAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.bitand", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BitAndAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.bitxor", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BitXOrAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.bitor", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BitOrAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.is", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.IsAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.isnot", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.IsNotAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.eq", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.EQAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.ne", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.NEAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.lt", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LTAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.le", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LEAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.gt", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GTAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.ge", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GEAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.contains", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ContainsAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.notcontains", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.NotContainsAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.add", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.AddAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.sub", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SubAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.mul", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.MulAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.floordiv", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.FloorDivAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.truediv", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.TrueDivAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.or", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.OrAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.and", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.AndAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.mod", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ModAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.setvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SetVarAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.addvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.AddVarAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.subvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SubVarAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.mulvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.MulVarAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.floordivvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.FloorDivVarAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.truedivvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.TrueDivVarAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.modvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ModVarAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.shiftleftvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ShiftLeftVarAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.shiftrightvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ShiftRightVarAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.bitandvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BitAndVarAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.bitxorvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BitXOrVarAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.bitorvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.BitOrVarAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.posarg", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.PositionalArgumentAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.keywordarg", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.KeywordArgumentAST(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.unpacklistarg", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.UnpackListArgumentAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.unpackdictarg", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.UnpackDictArgumentAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.call", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.CallAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.render", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.RenderAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.renderx", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.RenderXAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.renderblock", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.RenderBlockAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.renderblocks", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.RenderBlocksAST(null, null, null); }});
		Utils.register("de.livinglogic.ul4.template", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.InterpretedTemplate(); }});
		Utils.register("de.livinglogic.ul4.signature", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SignatureAST(null, null); }});
	}

	static
	{
		register4UL4ON();
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(VERSION);
		encoder.dump(name);
		encoder.dump(source);
		encoder.dump(whitespace.toString());
		encoder.dump(startdelim);
		encoder.dump(enddelim);
		encoder.dump(docPos);
		encoder.dump(parentTemplate);

		dumpSignatureUL4ON(encoder);

		super.dumpUL4ON(encoder);
	}

	private void dumpSignatureUL4ON(Encoder encoder) throws IOException
	{
		if (signature != null)
		{
			List paramsDump = null;
			paramsDump = new LinkedList();
			for (ParameterDescription paramdesc : signature)
			{
				switch (paramdesc.getType())
				{
					case REQUIRED:
						paramsDump.add(paramdesc.getName());
						break;
					case DEFAULT:
						paramsDump.add(paramdesc.getName() + "=");
						paramsDump.add(paramdesc.getDefaultValue());
						break;
					case VAR_POSITIONAL:
						paramsDump.add("*" + paramdesc.getName());
						break;
					case VAR_KEYWORD:
						paramsDump.add("**" + paramdesc.getName());
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
		if (version == null) // this is a "source" version of the UL4ON dump
		{
			String name = (String)decoder.load();
			String source = (String)decoder.load();
			String signature = (String)decoder.load();
			String whitespace = (String)decoder.load();
			String startdelim = (String)decoder.load();
			String enddelim = (String)decoder.load();
			if (startdelim == null)
				startdelim = "<?";
			if (enddelim == null)
				enddelim = "?>";

			// remove old attributes, set new ones, and recompile the template
			setStartPos(0, 0);
			int stop = source.length();
			setStopPos(stop, stop);
			this.template = this;
			this.content.clear();
			this.name = name;
			this.whitespace = Whitespace.fromString(whitespace);
			this.startdelim = startdelim;
			this.enddelim = enddelim;
			this.signature = null;
			this.signatureAST = null;
			this.docPos = null;
			this.source = makeSource(source, name, startdelim, enddelim, signature);
			compile();
		}
		else // this is a "compiled" version of the UL4ON dump
		{
			if (!VERSION.equals(version))
			{
				throw new RuntimeException("Invalid version, expected " + VERSION + ", got " + version);
			}
			name = (String)decoder.load();
			source = (String)decoder.load();
			whitespace = Whitespace.fromString((String)decoder.load());
			startdelim = (String)decoder.load();
			enddelim = (String)decoder.load();
			docPos = (Slice)decoder.load();
			parentTemplate = (InterpretedTemplate)decoder.load();

			loadSignatureUL4ON(decoder);

			super.loadUL4ON(decoder);
		}
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
					signature.add(paramName, ParameterDescription.Type.DEFAULT, param);
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
						signature.add(paramName.substring(2), ParameterDescription.Type.VAR_KEYWORD, null);
					else if (paramName.startsWith("*"))
						signature.add(paramName.substring(1), ParameterDescription.Type.VAR_POSITIONAL, null);
					else
						signature.add(paramName, ParameterDescription.Type.REQUIRED, null);
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

	protected static Set<String> attributes = makeExtendedSet(BlockAST.attributes, "name", "whitespace", "startdelim", "enddelim", "signature", "doc", "source", "parenttemplate", "renders", "render");

	public Set<String> dirUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "name":
				return name;
			case "whitespace":
				return whitespace.toString();
			case "startdelim":
				return startdelim;
			case "enddelim":
				return enddelim;
			case "signature":
				return signature;
			case "doc":
				return getDoc();
			case "parenttemplate":
				return parentTemplate;
			case "renders":
				return new BoundMethodRenderS(this);
			case "render":
				return new BoundMethodRender(this);
			default:
				return super.getAttrUL4(key);
		}
	}
}
