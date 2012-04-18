/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Date;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.antlr.runtime.*;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import com.livinglogic.utils.ObjectAsMap;

public class InterpretedTemplate extends Block implements Template
{
	/**
	 * The header used in the compiled format of the template.
	 */
	public static final String HEADER = "ul4";

	/**
	 * The version number used in the compiled format of the template.
	 */
	public static final String VERSION = "16";

	/**
	 * The name of the template (defaults to <code>unnamed</code>)
	 */
	public String name = null;

	/**
	 * The start delimiter for tags (defaults to <code>&lt;?</code>)
	 */
	public String startdelim = "<?";

	/**
	 * The end delimiter for tags (defaults to <code>?&gt;</code>)
	 */
	public String enddelim = "?>";

	/**
	 * The template source (of the top-level template, i.e. subtemplates always get the full source).
	 */
	public String source = null;

	/**
	 * the compiled content of template
	 */
	public LinkedList<AST> content = new LinkedList<AST>();

	/**
	 * The locale to be used when formatting int, float or date objects.
	 */
	private Locale defaultLocale = Locale.ENGLISH;

	/**
	 * Creates an empty template object. Must be filled in later (use for creating subtemplates)
	 */
	public InterpretedTemplate()
	{
	}

	public InterpretedTemplate(String source) throws RecognitionException
	{
		this(source, null, "<?", "?>");
	}

	public InterpretedTemplate(String source, String name) throws RecognitionException
	{
		this(source, name, "<?", "?>");
	}

	public InterpretedTemplate(String source, String startdelim, String enddelim) throws RecognitionException
	{
		this(source, null, startdelim, enddelim);
	}

	private static class StackItem
	{
		public Block block;
		public Location location;

		public StackItem(Block block, Location location)
		{
			this.block = block;
			this.location = location;
		}
	}

	public InterpretedTemplate(String source, String name, String startdelim, String enddelim) throws RecognitionException
	{
		this.source = source;
		this.name = name;
		this.startdelim = startdelim;
		this.enddelim = enddelim;

		List<Location> tags = InterpretedTemplate.tokenizeTags(source, name, startdelim, enddelim);

		Stack<StackItem> stack = new Stack<StackItem>();

		stack.push(new StackItem(this, null)); // Stack of currently active blocks

		for (Location location : tags)
		{
			try
			{
				Block innerBlock = stack.peek().block;
				String type = location.getType();
				// FIXME: use a switch in Java 7
				if (type == null)
					innerBlock.append(new Text(location.getCode()));
				else if (type.equals("print"))
				{
					UL4Parser parser = getParser(location);
					AST node = parser.expression();
					innerBlock.append(new Print(node));
				}
				else if (type.equals("printx"))
				{
					UL4Parser parser = getParser(location);
					AST node = parser.expression();
					innerBlock.append(new PrintX(node));
				}
				else if (type.equals("code"))
				{
					UL4Parser parser = getParser(location);
					AST node = parser.stmt();
					innerBlock.append(node);
				}
				else if (type.equals("if"))
				{
					UL4Parser parser = getParser(location);
					AST node = parser.expression();
					ConditionalBlockBlock blockBlock = new ConditionalBlockBlock(new If(node));
					innerBlock.append(blockBlock);
					stack.push(new StackItem(blockBlock, location));
				}
				else if (type.equals("elif"))
				{
					if (innerBlock instanceof ConditionalBlockBlock)
					{
						UL4Parser parser = getParser(location);
						AST node = parser.expression();
						((ConditionalBlockBlock)innerBlock).startNewBlock(new ElIf(node));
					}
					else
						throw new BlockException("elif doesn't match any if");
				}
				else if (type.equals("else"))
				{
					if (innerBlock instanceof ConditionalBlockBlock)
					{
						((ConditionalBlockBlock)innerBlock).startNewBlock(new Else());
					}
					else
						throw new BlockException("else doesn't match any if");
				}
				else if (type.equals("end"))
				{
					if (stack.size() > 1)
					{
						innerBlock.finish(this, stack.peek().location, location);
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
					stack.push(new StackItem(node, location));
				}
				else if (type.equals("break"))
				{
					for (int i = stack.size()-1; i >= 0; --i)
					{
						if (stack.get(i).block.handleLoopControl("break"))
							break;
					}
					innerBlock.append(new Break());
				}
				else if (type.equals("continue"))
				{
					for (int i = stack.size()-1; i >= 0; --i)
					{
						if (stack.get(i).block.handleLoopControl("continue"))
							break;
					}
					innerBlock.append(new Continue());
				}
				else if (type.equals("def"))
				{
					InterpretedTemplate subtemplate = new InterpretedTemplate();
					// Copy over the attributes that we know now, the source is set once the <?end?> tag is encountered
					subtemplate.name = location.getCode();
					subtemplate.startdelim = startdelim;
					subtemplate.enddelim = enddelim;
					innerBlock.append(subtemplate);
					stack.push(new StackItem(subtemplate, location));
				}
				else
				{
					// Can't happen
					throw new RuntimeException("unknown tag " + type);
				}
			}
			catch (LocationException ex)
			{
				throw ex; // we have no info to add
			}
			catch (Exception ex)
			{
				throw new LocationException(ex, location);
			}
		}
		if (stack.size() > 1) // the template itself is still on the stack
			throw new LocationException(new BlockException(stack.peek().block.getType() + " block unclosed"), stack.peek().location);
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("def " + (name != null ? name : "unnamed") + "(**vars)\n");
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("{\n");
		++indent;
		for (AST item : content)
			buffer.append(item.toString(indent));
		--indent;
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("}\n");
		return buffer.toString();
	}

	protected static String read(Reader reader, int length) throws IOException
	{
		char[] chars = new char[length];
		int readlength = reader.read(chars);
		return new String(chars);
	}

	/**
	 * Reads a character from a stream.
	 * @param reader the reader from which the linefeed is read.
	 * @throws IOException if reading from the stream fails
	 * @throws RuntimeException if the character read from the stream is not a
	 *                          linefeed
	 */
	protected static void readchar(Reader reader, char expected) throws IOException
	{
		int readInt = reader.read();
		if (-1 < readInt)
		{
			char charValue = (char)readInt;
			if (expected != charValue)
			{
				throw new RuntimeException("Invalid character, expected '" + expected + "', got '" + charValue + "'");
			}
		}
		else
		{
			throw new RuntimeException("Short read!");
		}
	}

	protected static int readintInternal(Reader reader, String prefix) throws IOException
	{
		int retVal = 0;
		boolean digitFound = false;
		if (prefix != null)
		{
			String prefixread = read(reader, prefix.length());
			if (!prefixread.equals(prefix))
				throw new RuntimeException("Invalid prefix, expected '" + prefix + "', got '" + prefixread + "'");
		}
		while (true)
		{
			int readInt = reader.read();
			char charValue = (char)readInt;
			int intValue = Character.digit(charValue, 10);
			if (-1 < intValue)
			{
				retVal = retVal * 10 + intValue;
				digitFound = true;
			}
			else if (charValue == '|')
			{
				if (!digitFound)
					retVal = -1;
				return retVal;
			}
			else
			{
				throw new RuntimeException("Invalid terminator, expected '|', got '" + charValue + "'");
			}
		}
	}

	/**
	 * Reads a (non-negative) integer value from a stream.
	 * @param reader the reader from which the value is read.
	 * @param prefix The string before the digits of the integer value
	 * @return The integer value
	 * @throws IOException if reading from the stream fails
	 * @throws RuntimeException if the integer value is malformed (i.e. the
	 *                          terminator is missing)
	 */
	protected static int readint(Reader reader, String prefix) throws IOException
	{
		int retVal = readintInternal(reader, prefix);
		if (0 > retVal)
		{
			throw new RuntimeException("Invalid integer read!");
		}
		return retVal;
	}

	/**
	 * Reads a string value (or <code>null</code>) from a stream.
	 * <code>readstr</code> is the inverse operation of {@link writestr}.
	 * @param reader the reader from which the string is read.
	 * @param prefix The string that was used in {@link writestr}.
	 * @return The string or <code>null</code>
	 * @throws IOException if reading from the stream fails
	 * @throws RuntimeException if the integer value is malformed
	 */
	protected static String readstr(Reader reader, String prefix) throws IOException
	{
		int stringLength = readintInternal(reader, prefix);
		if (-1 == stringLength)
			return null;

		String retVal = read(reader, stringLength);
		if (retVal.length() != stringLength)
			throw new RuntimeException("Short read!");
		readchar(reader, '|');
		return retVal;
	}

	/**
	 * loads the source of a template from a reader, without checking the version
	 * number of the binary file. This is helpful when updating an old stored source.
	 * @param reader the reader from which the source is read.
	 * @return The source as a string.
	 * @throws IOException if reading from the stream fails
	 */
	public static String loadsource(Reader reader) throws IOException
	{
		BufferedReader bufferedReader = new BufferedReader(reader);
		bufferedReader.readLine(); // skip header (without checking)
		bufferedReader.readLine(); // skip version number (with checking)
		readstr(bufferedReader, "N"); // skip name
		readchar(bufferedReader, '\n');
		readstr(bufferedReader, "SD"); // skip start delimiter
		readchar(bufferedReader, '\n');
		readstr(bufferedReader, "ED"); // skip end delimiter
		readchar(bufferedReader, '\n');
		return readstr(bufferedReader, "SRC");
	}

	/**
	 * loads the source of a template from a string containing the compiled
	 * template.
	 * @param bytecode of the compiled template.
	 * @return The source as a string.
	 */
	public static String loadsource(String bytecode)
	{
		try
		{
			return loadsource(new StringReader(bytecode));
		}
		catch (IOException ex) // can not happen when reading from a StringReader
		{
			return null;
		}
	}

	/**
	 * loads a template from a reader.
	 * @param reader the reader from which the template is read.
	 * @return The template object.
	 * @throws IOException if reading from the stream fails
	 */
	public static InterpretedTemplate load(Reader reader) throws IOException
	{
		InterpretedTemplate retVal = new InterpretedTemplate();
		BufferedReader bufferedReader = new BufferedReader(reader);
		String header = bufferedReader.readLine();
		if (!HEADER.equals(header))
		{
			throw new RuntimeException("Invalid header, expected " + HEADER + ", got " + header);
		}
		String version = bufferedReader.readLine();
		if (!VERSION.equals(version))
		{
			throw new RuntimeException("Invalid version, expected " + VERSION + ", got " + version);
		}
		retVal.name = readstr(bufferedReader, "N");
		readchar(bufferedReader, '\n');
		retVal.startdelim = readstr(bufferedReader, "SD");
		readchar(bufferedReader, '\n');
		retVal.enddelim = readstr(bufferedReader, "ED");
		readchar(bufferedReader, '\n');
		retVal.source = readstr(bufferedReader, "SRC");
		readchar(bufferedReader, '\n');
		int count = readint(bufferedReader, "n");
		readchar(bufferedReader, '\n');
		Location location = null;
		for (int i = 0; i < count; i++)
		{
			int r1 = readintInternal(bufferedReader, null);
			int r2 = readintInternal(bufferedReader, null);
			int r3 = readintInternal(bufferedReader, null);
			int r4 = readintInternal(bufferedReader, null);
			int r5 = readintInternal(bufferedReader, null);
			String code = readstr(bufferedReader, "C");
			String arg = readstr(bufferedReader, "A");
			int readInt = bufferedReader.read();
			if (-1 < readInt)
			{
				char charValue = (char)readInt;
				if ('^' == charValue)
				{
					if (null == location)
					{
						throw new RuntimeException("No previous location!");
					}
				}
				else if ('*' == charValue)
				{
					int readInt2 = bufferedReader.read();
					char charValue2 = (char)readInt2;
					if ('|' != charValue2)
					{
						throw new RuntimeException("Invalid location spec " + charValue + charValue2);
					}
					// Use null for the name, this will be fixed by annotate()
					location = new Location(retVal.source, null,
						readstr(bufferedReader, "T"),
						readint(bufferedReader, "st"), readint(bufferedReader, "et"),
						readint(bufferedReader, "sc"), readint(bufferedReader, "ec"));
				}
				else
				{
					throw new RuntimeException("Invalid location spec " + charValue);
				}
			}
			else
			{
				throw new RuntimeException("Short read!");
			}
			//retVal.opcode(code, r1, r2, r3, r4, r5, arg, location);
			readchar(bufferedReader, '\n');
		}
		return retVal;
	}

	/**
	 * loads a template from a string.
	 * @param bytecode of the compiled template.
	 * @return The template object.
	 * @throws IOException if reading from the stream fails
	 */
	public static InterpretedTemplate load(String bytecode)
	{
		try
		{
			return load(new StringReader(bytecode));
		}
		catch (IOException ex) // can not happen when reading from a StringReader
		{
			return null;
		}
	}

	/**
	 * writes an int to a stream in such a way that the int can be reliable read
	 * again. This is done by writing the digits of the integer followed by a
	 * terminator character (that may not be a digit).
	 * @param writer the stream to which to write.
	 * @param value the int value to be written.
	 * @param terminator a terminating character written after the value.
	 * @throws IOException if writing to the stream fails
	 */
	protected static void writeint(Writer writer, String prefix, int value) throws IOException
	{
		writer.write(prefix);
		writer.write(String.valueOf(value));
		writer.write("|");
	}

	/**
	 * writes a string to a stream in such a way that the string can be reliable read again.
	 * @param writer the stream to which to write.
	 * @param value the string value to be written (may be null).
	 * @param terminator a terminating character written after the string length.
	 * @throws IOException if writing to the stream fails
	 */
	protected static void writestr(Writer writer, String prefix, String value) throws IOException
	{
		writer.write(prefix);
		if (value != null)
		{
			writer.write(String.valueOf(value.length()));
			writer.write("|");
			writer.write(value);
		}
		writer.write("|");
	}

	/**
	 * writes a register specification to a stream (which is either a digit or '-' in case the register spec is empty.
	 * @param writer the stream to which to write.
	 * @param spec the register number or -1 in case the register spec is empty.
	 * @throws IOException if writing to the stream fails
	 */
	protected static void writespec(Writer writer, int spec) throws IOException
	{
		if (spec != -1)
			writer.write(String.valueOf(spec));
		writer.write("|");
	}

	/**
	 * writes the Template object to a stream.
	 * @param writer the stream to which to write.
	 * @throws IOException if writing to the stream fails
	 */
	public void dump(Writer writer) throws IOException
	{
		writer.write(HEADER);
		writer.write("\n");
		writer.write(VERSION);
		writer.write("\n");
		writestr(writer, "N", name);
		writer.write("\n");
		writestr(writer, "SD", startdelim);
		writer.write("\n");
		writestr(writer, "ED", enddelim);
		writer.write("\n");
		writestr(writer, "SRC", source);
		writer.write("\n");
		Location lastLocation = null;
	}

	/**
	 * writes the Template object to a string.
	 * @return The string containing the template in compiled format.
	 */
	public String dumps()
	{
		StringWriter writer = new StringWriter();
		try
		{
			dump(writer);
		}
		catch (IOException ex) // can not happen when dumping to a StringWriter
		{
		}
		return writer.toString();
	}

	// public Reader reader(Map<String, Object> variables)
	// {
	// 	return new IteratorReader(new Renderer(variables));
	// }

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.put(name, this);
		return null;
	}

	/**
	 * Renders the template to a java.io.Writer object.
	 * @param writer    the java.io.Writer object to which the output is written.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code. May be null.
	 */
	public void render(java.io.Writer writer, Map<String, Object> variables) throws IOException
	{
		if (variables == null)
			variables = new HashMap<String, Object>();
		EvaluationContext context = new EvaluationContext(writer, variables, defaultLocale);
		super.evaluate(context);
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

	public static List<Location> tokenizeTags(String source, String name, String startdelim, String enddelim)
	{
		Pattern tagPattern = Pattern.compile(escapeREchars(startdelim) + "(printx|print|code|for|if|elif|else|end|break|continue|def|note)(\\s*(.*?)\\s*)?" + escapeREchars(enddelim), Pattern.DOTALL);
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
					tags.add(new Location(source, name, null, pos, start, pos, start));
				int codestart = matcher.start(3);
				int codeend = codestart + matcher.group(3).length();
				String type = matcher.group(1);
				if (!type.equals("note"))
					tags.add(new Location(source, name, matcher.group(1), start, end, codestart, codeend));
				pos = end;
			}
			end = source.length();
			if (pos != end)
				tags.add(new Location(source, name, null, pos, end, pos, end));
		}
		return tags;
	}

	private static String escapeREchars(String input)
	{
		int len = input.length();

		StringBuffer output = new StringBuffer(len);

		for (int i = 0; i < len; ++i)
		{
			char c = input.charAt(i);
			if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9')))
				output.append('\\');
			output.append(c);
		}
		return output.toString();
	}

	public JSPTemplate compileToJava() throws java.io.IOException
	{
		StringBuffer source = new StringBuffer();
		source.append("\tpublic String getName()\n");
		source.append("\t{\n");
		source.append("\t\treturn \"" + StringEscapeUtils.escapeJava(name) + "\";\n");
		source.append("\t}\n");
		source.append("\n");
		source.append("\tpublic void render(java.io.Writer out, java.util.Map<String, Object> variables) throws java.io.IOException\n");
		source.append("\t{\n");
		source.append(javaSource());
		source.append("\t}\n");

		Class clazz = Utils.compileToJava(source.toString(), "com.livinglogic.ul4.JSPTemplate", null);
		try
		{
			return (JSPTemplate)clazz.newInstance();
		}
		catch (InstantiationException ex)
		{
			// Can't happen
			throw new RuntimeException(ex);
		}
		catch (IllegalAccessException ex)
		{
			// Can't happen
			throw new RuntimeException(ex);
		}
	}

	public String getType()
	{
		return "template";
	}

	public void finish(InterpretedTemplate template, Location startLocation, Location endLocation)
	{
		String type = endLocation.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("def"))
			throw new BlockException("def ended by end" + type);
		source = template.source.substring(startLocation.starttag, endLocation.endtag);
	}

	public boolean handleLoopControl(String name)
	{
		throw new BlockException(name + " outside of for loop");
	}

	public String javaSource()
	{
		return new JavaSource4Template(this).toString();
	}

	public String javascriptSource()
	{
		return new JavascriptSource4Template(this).toString();
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("name", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)object).name;}});
			v.put("startdelim", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)object).startdelim;}});
			v.put("enddelim", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)object).enddelim;}});
			v.put("source", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)object).source;}});
			// v.put("opcodes", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)object).opcodes;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
