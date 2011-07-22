package com.livinglogic.ul4;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Date;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;

public class InterpretedTemplate implements Template
{
	// used by the code tokenizer
	private static Pattern tokenPattern;
	private static Pattern namePattern;
	private static Pattern floatPattern;
	private static Pattern hexintPattern;
	private static Pattern octintPattern;
	private static Pattern binintPattern;
	private static Pattern intPattern;
	private static Pattern datePattern;
	private static Pattern color3Pattern;
	private static Pattern color4Pattern;
	private static Pattern color6Pattern;
	private static Pattern color8Pattern;
	private static Pattern whitespacePattern;
	private static Pattern escaped8BitCharPattern;
	private static Pattern escaped16BitCharPattern;
	private static Pattern escaped32BitCharPattern;

	static
	{
		// Initializes regular expressions
		tokenPattern = Pattern.compile("\\(|\\)|\\[|\\]|\\{|\\}|\\.|,|==|\\!=|<=|<|>=|>|=|\\+=|\\-=|\\*=|/=|//=|%=|%|:|\\+|-|\\*\\*|\\*|//|/");
		namePattern = Pattern.compile("[a-zA-Z_][\\w]*");
		// We don't have negative numbers, this is handled by constant folding in the AST for unary minus
		floatPattern = Pattern.compile("(\\d+(\\.\\d*)?[eE][+-]?\\d+|\\d+\\.\\d*([eE][+-]?\\d+)?)");
		hexintPattern = Pattern.compile("0[xX][\\da-fA-F]+");
		octintPattern = Pattern.compile("0[oO][0-7]+");
		binintPattern = Pattern.compile("0[bB][01]+");
		intPattern = Pattern.compile("\\d+");
		datePattern = Pattern.compile("@\\d{4}-\\d{2}-\\d{2}T(\\d{2}:\\d{2}(:\\d{2}(.\\d{6})?)?)?");
		color3Pattern = Pattern.compile("[#][0-9a-zA-Z]{3}");
		color4Pattern = Pattern.compile("[#][0-9a-zA-Z]{4}");
		color6Pattern = Pattern.compile("[#][0-9a-zA-Z]{6}");
		color8Pattern = Pattern.compile("[#][0-9a-zA-Z]{8}");
		whitespacePattern = Pattern.compile("\\s+");
		escaped8BitCharPattern = Pattern.compile("\\\\x[0-9a-fA-F]{2}");
		escaped16BitCharPattern = Pattern.compile("\\\\u[0-9a-fA-F]{4}");
		escaped32BitCharPattern = Pattern.compile("\\\\U[0-9a-fA-F]{8}");
	}

	/**
	 * Contains information about the currently running loops during rendering
	 * of the template.
	 */
	class IteratorStackEntry
	{
		/**
		 * The register number where the loop variable has to be stored for each
		 * run through the loop body.
		 */
		public int iteratorRegSpec;

		/**
		 * The program counter (i.e. the index of the opcode in the
		 * {@link #opcodes} list) where the loop started (i.e. the location of the
		 * <code>for</code> opcode).
		 */
		public int pcFor;

		/**
		 * The program counter (i.e. the index of the opcode in the
		 * {@link #opcodes} list) where the loop ends (i.e. the location of the
		 * <code>endfor</code> opcode).
		 */
		public int pcEndFor;

		/**
		 * The iterator producing the values for the loop variable.
		 */
		public Iterator iterator;

		public IteratorStackEntry(int iteratorRegSpec, int pcFor, int pcEndFor, Iterator iterator)
		{
			this.iteratorRegSpec = iteratorRegSpec;
			this.pcFor = pcFor;
			this.pcEndFor = pcEndFor;
			this.iterator = iterator;
		}
	}

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
	public String name;

	/**
	 * The start delimiter for tags (defaults to <code>&lt;?</code>)
	 */
	public String startdelim;

	/**
	 * The end delimiter for tags (defaults to <code>?&gt;</code>)
	 */
	public String enddelim;

	/**
	 * The template source (of the top-level template, i.e. subtemplates always get the full source).
	 */
	public String source;

	/**
	 * The list of opcodes.
	 */
	public List<Opcode> opcodes;

	/**
	 * Offsets into <code>source</code>, where the real source code starts and ends (used for subtemplates)
	 */
	private int sourceStartIndex;
	private int sourceEndIndex;
	/**
	 * Offsets into <code>opcodes</code>, where the real opcodes start and end (used for subtemplates)
	 */
	private int opcodeStartIndex;
	private int opcodeEndIndex;

	/**
	 * The locale to be used when formatting int, float or date objects.
	 */
	private Locale defaultLocale;

	/**
	 * Has {@link annotate} been called for this template?
	 */
	private boolean annotated = false;

	/**
	 * Creates an empty template object.
	 */
	public InterpretedTemplate()
	{
		this.source = null;
		this.name = null;
		this.opcodes = new LinkedList<Opcode>();
		this.defaultLocale = Locale.ENGLISH;
	}

	/**
	 * Creates an template object for a source string and a list of opcodes.
	 */
	public InterpretedTemplate(String source, String name, List<Opcode> opcodes, String startdelim, String enddelim)
	{
		this.source = source;
		this.name = name;
		this.startdelim = startdelim;
		this.enddelim = enddelim;
		this.opcodes = opcodes;
		this.sourceStartIndex = 0;
		this.sourceEndIndex = source.length();
		this.opcodeStartIndex = 0;
		this.opcodeEndIndex = opcodes.size();
		this.defaultLocale = Locale.ENGLISH;
	}

	/**
	 * Creates an template object as a subtemplate of another template.
	 */
	public InterpretedTemplate(InterpretedTemplate parent, String name, int sourceStartIndex, int sourceEndIndex, int opcodeStartIndex, int opcodeEndIndex)
	{
		this.source = parent.source;
		this.name = name;
		this.startdelim = parent.startdelim;
		this.enddelim = parent.enddelim;
		this.opcodes = parent.opcodes;
		this.sourceStartIndex = sourceStartIndex;
		this.sourceEndIndex = sourceEndIndex;
		this.opcodeStartIndex = opcodeStartIndex;
		this.opcodeEndIndex = opcodeEndIndex;
		this.defaultLocale = Locale.ENGLISH;
		// The subtemplate must always be annotated, because if it isn't it would
		// annotate all opcodes/locations even those of the parent and would
		// store its name in the parents location. As subtemplates are created
		// during the annotate() call of the parent, we can savely set our own
		// annotate flag to true to prevent reannotation.
		this.annotated = true;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, Location location)
	{
		opcodes.add(new Opcode(name, -1, -1, -1, -1, -1, null, location));
		++opcodeEndIndex;
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, String arg, Location location)
	{
		opcodes.add(new Opcode(name, -1, -1, -1, -1, -1, arg, location));
		++opcodeEndIndex;
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, Location location)
	{
		opcodes.add(new Opcode(name, r1, -1, -1, -1, -1, null, location));
		++opcodeEndIndex;
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, -1, -1, -1, -1, arg, location));
		++opcodeEndIndex;
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, -1, -1, -1, null, location));
		++opcodeEndIndex;
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, -1, -1, -1, arg, location));
		++opcodeEndIndex;
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, int r3, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, -1, -1, null, location));
		++opcodeEndIndex;
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, int r3, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, -1, -1, arg, location));
		++opcodeEndIndex;
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, int r3, int r4, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, -1, null, location));
		++opcodeEndIndex;
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, int r3, int r4, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, -1, arg, location));
		++opcodeEndIndex;
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, int r3, int r4, int r5, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, r5, null, location));
		++opcodeEndIndex;
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, int r3, int r4, int r5, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, r5, arg, location));
		++opcodeEndIndex;
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(String name, int r1, int r2, int r3, int r4, int r5, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, r5, arg, location));
		++opcodeEndIndex;
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
		retVal.sourceEndIndex = retVal.source.length();
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
			retVal.opcode(code, r1, r2, r3, r4, r5, arg, location);
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
		writeint(writer, "n", opcodes.size());
		writer.write("\n");
		Location lastLocation = null;
		int size = opcodes.size();
		for (int i = 0; i < size; ++i)
		{
			Opcode opcode = opcodes.get(i);
			writespec(writer, opcode.r1);
			writespec(writer, opcode.r2);
			writespec(writer, opcode.r3);
			writespec(writer, opcode.r4);
			writespec(writer, opcode.r5);
			writestr(writer, "C", Opcode.code2name(opcode.name));
			writestr(writer, "A", opcode.arg);
			if (opcode.location != lastLocation)
			{
				writer.write("*|");
				writestr(writer, "T", opcode.location.type);
				writeint(writer, "st", opcode.location.starttag);
				writeint(writer, "et", opcode.location.endtag);
				writeint(writer, "sc", opcode.location.startcode);
				writeint(writer, "ec", opcode.location.endcode);
				lastLocation = opcode.location;
			}
			else
			{
				writer.write("^");
			}
			writer.write("\n");
		}
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

	/**
	 * Annotates all control flow opcodes in the template with a jump location and
	 * fixes the template name in the location objects.
	 * <ul>
	 * <li>(a <code>for</code> opcode gets annotated with the location of the
	 * associated <code>endfor</code> opcode;</li>
	 * <li>a <code>def</code> opcode gets annotated with the location of the
	 * associated <code>enddef</code> opcode;</li>
	 * <li>an <code>if</code> opcode gets annotated with the location of the
	 * associated <code>else</code> or <code>endif</code> opcode;</li>
	 * <li>an <code>else</code> opcode gets annotated with the location of
	 * <code>endif</code> opcode;</li>
	 * <li>a <code>break</code> opcode gets annotated with the location of next
	 * opcode after the associated <code>endfor</code> opcode.</li>
	 * <li>a <code>continue</code> opcode gets annotated with the location of next
	 * opcode after the associated <code>endfor</code> opcode.</li>
	 * </ul>
	 */
	protected void annotate()
	{
		if (!annotated)
		{
			int size = opcodes.size();
			for (int i = 0; i < size; ++i)
			{
				Opcode opcode = opcodes.get(i);
				opcode.location.fixName(name);
				switch (opcode.name)
				{
					case Opcode.OC_IF:
						i = annotateIf(i, 0, name);
						break;
					case Opcode.OC_FOR:
						i = annotateFor(i, 0, name);
						break;
					case Opcode.OC_DEF:
						i = annotateDef(i, 0, name);
						break;
					case Opcode.OC_ELSE:
						throw new BlockException("else outside if block");
					case Opcode.OC_ENDIF:
						throw new BlockException("endif outside if block");
					case Opcode.OC_ENDFOR:
						throw new BlockException("endfor outside for loop");
					case Opcode.OC_BREAK:
						throw new BlockException("break outside for loop");
					case Opcode.OC_CONTINUE:
						throw new BlockException("continue outside for loop");
					case Opcode.OC_ENDDEF:
						throw new BlockException("enddef outside def block");
				}
			}
			annotated = true;
		}
	}

	protected int annotateIf(int ifStart, int forDepth, String name)
	{
		int jump = ifStart;
		int size = opcodes.size();
		for (int i = ifStart+1; i < size; ++i)
		{
			Opcode opcode = opcodes.get(i);
			opcode.location.fixName(name);
			switch (opcode.name)
			{
				case Opcode.OC_IF:
					i = annotateIf(i, forDepth, name);
					break;
				case Opcode.OC_FOR:
					i = annotateFor(i, forDepth, name);
					break;
				case Opcode.OC_DEF:
					i = annotateDef(i, forDepth, name);
					break;
				case Opcode.OC_ELSE:
					opcodes.get(jump).jump = i;
					jump = i;
					break;
				case Opcode.OC_ENDIF:
					opcodes.get(jump).jump = i;
					return i;
				case Opcode.OC_BREAK:
					if (forDepth == 0)
						throw new BlockException("break outside for loop");
					break;
				case Opcode.OC_CONTINUE:
					if (forDepth == 0)
						throw new BlockException("continue outside for loop");
					break;
				case Opcode.OC_ENDFOR:
					throw new BlockException("endfor in if block");
				case Opcode.OC_ENDDEF:
					throw new BlockException("enddef in if block");
			}
		}
		throw new BlockException("unclosed if block");
	}

	protected int annotateDef(int defStart, int forDepth, String name)
	{
		int jump = defStart;
		int size = opcodes.size();
		Opcode defOpcode = opcodes.get(defStart);
		String defName = defOpcode.arg;
		for (int i = defStart+1; i < size; ++i)
		{
			Opcode opcode = opcodes.get(i);
			opcode.location.fixName(opcode.name != Opcode.OC_ENDDEF ? defName : name);
			switch (opcode.name)
			{
				case Opcode.OC_IF:
					i = annotateIf(i, 0, defName);
					break;
				case Opcode.OC_FOR:
					i = annotateFor(i, 0, defName);
					break;
				case Opcode.OC_DEF:
					i = annotateDef(i, 0, defName);
					break;
				case Opcode.OC_ELSE:
					throw new BlockException("else in def");
				case Opcode.OC_ENDIF:
					throw new BlockException("endif in def");
				case Opcode.OC_BREAK:
					throw new BlockException("break in def");
				case Opcode.OC_CONTINUE:
					throw new BlockException("continue in def");
				case Opcode.OC_ENDFOR:
					throw new BlockException("endfor in def");
				case Opcode.OC_ENDDEF:
					defOpcode.jump = i;
					defOpcode.template = new InterpretedTemplate(
						this,
						defOpcode.arg,
						defOpcode.location.endtag, // end of the <?def?> tag
						opcode.location.starttag, // start of the <?end def?> tag
						defStart+1, // first opcode after the def
						i // the enddef opcode, i.e. one after the <?def?> content
					);
					return i;
			}
		}
		throw new BlockException("unclosed def block");
	}

	protected int annotateFor(int loopStart, int forDepth, String name)
	{
		++forDepth;
		LinkedList<Integer> breaks = new LinkedList<Integer>();
		LinkedList<Integer> continues = new LinkedList<Integer>();

		int size = opcodes.size();
		for (int i = loopStart+1; i < size; ++i)
		{
			Opcode opcode = opcodes.get(i);
			opcode.location.fixName(name);
			switch (opcode.name)
			{
				case Opcode.OC_IF:
					i = annotateIf(i, forDepth, name);
					break;
				case Opcode.OC_FOR:
					i = annotateFor(i, forDepth, name);
					break;
				case Opcode.OC_DEF:
					i = annotateDef(i, forDepth, name);
					break;
				case Opcode.OC_ELSE:
					throw new BlockException("else in for loop");
				case Opcode.OC_ENDIF:
					throw new BlockException("endif in for loop");
				case Opcode.OC_BREAK:
					breaks.add(i);
					break;
				case Opcode.OC_CONTINUE:
					continues.add(i);
					break;
				case Opcode.OC_ENDFOR:
					int j;
					int jump;
					for (j = 0; j < breaks.size(); ++j)
					{
						jump = breaks.get(j);
						opcodes.get(jump).jump = i;
					}
					for (j = 0; j < continues.size(); ++j)
					{
						jump = continues.get(j);
						opcodes.get(jump).jump = i;
					}
					opcodes.get(loopStart).jump = i;
					return i;
				case Opcode.OC_ENDDEF:
					throw new BlockException("enddef in for loop");
			}
		}
		throw new BlockException("unclosed loop");
	}

	/**
	 * Renders the template.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code.
	 * @return An iterator that returns the string output piece by piece.
	 */
	public Iterator<String> render(Map<String, Object> variables)
	{
		return new Renderer(variables);
	}

	/**
	 * Renders the template.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code.
	 * @return A java.io.Reader object from which the template output can be read.
	 */
	public Reader reader(Map<String, Object> variables)
	{
		return new IteratorReader(new Renderer(variables));
	}

	/**
	 * Renders the template to a java.io.Writer object.
	 * @param writer    the java.io.Writer object to which the output is written.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code.
	 */
	public void render(java.io.Writer writer, Map<String, Object> variables) throws java.io.IOException
	{
		for (Iterator<String> iterator = render(variables); iterator.hasNext();)
		{
			writer.write(iterator.next());
		}
	}

	/**
	 * Renders the template and returns the resulting string.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code.
	 * @return The render output as a string.
	 */
	public String renders(Map<String, Object> variables)
	{
		StringBuffer output = new StringBuffer();

		for (Iterator<String> iterator = render(variables); iterator.hasNext();)
		{
			output.append(iterator.next());
		}
		return output.toString();
	}

	class Renderer implements Iterator<String>
	{
		/**
		 * The current program counter
		 */
		private int pc;

		/**
		 * The ten registers of our CPU
		 */
		private Object[] reg = new Object[10];

		/**
		 * The variables passed to the {@com.livinglogic.ul4.InterpretedTemplate#render} call
		 * During the run of the iterator loop variables and variables from
		 * <code>&lt;?code>&gt;</code> tag will be stored here
		 */
		private Map<String, Object> variables;

		/**
		 * The stack of active for loops
		 */
		private LinkedList<IteratorStackEntry> iterators = new LinkedList<IteratorStackEntry>();

		/**
		 * If a subtemplate is running (i.e. if we're inside a
		 * <code>&lt;?render?&gt;</code> tag), this variable references the
		 * active part iterator for the subtemplate.
		 */
		private Iterator<String> subTemplateIterator = null;

		/**
		 * Since we implement the iterator interface we have to support both
		 * <code>next</code> and <code>hasNext</code>. This means that neither
		 * of the two methods can directly execute the opcodes to get the next
		 * output chunk. Instead of that we have a method {@link getNextChunk}
		 * that executes the opcodes until the next output chunk is produced and
		 * stores it in <code>nextChunk</code>, where both <code>next</code> and
		 * <code>hasNext</code> can refer to it.
		 */
		private String nextChunk = null;

		public Renderer(Map<String, Object> variables)
		{
			annotate();
			if (variables == null)
				variables = new HashMap<String, Object>();
			this.variables = variables;
			pc = opcodeStartIndex;
			getNextChunk();
		}

		public void remove()
		{
			throw new UnsupportedOperationException("remove() not supported for " + getClass() + "!");
		}

		public boolean hasNext()
		{
			return nextChunk != null;
		}

		public String next()
		{
			String result = nextChunk;
			getNextChunk();
			return result;
		}

		/**
		 * Gets the next output chunk and stores it in {@link nextChunk}
		 */
		private void getNextChunk()
		{
			if (subTemplateIterator != null)
			{
				try
				{
					if (subTemplateIterator.hasNext())
					{
						nextChunk = subTemplateIterator.next();
						return;
					}
					else
					{
						subTemplateIterator = null;
					}
				}
				catch (Exception ex)
				{
					Opcode code = opcodes.get(pc-1);
					throw new LocationException(ex, code, pc-1);
				}
			}
			int lastOpcode = opcodeEndIndex;
			while (pc < lastOpcode)
			{
				Opcode code = opcodes.get(pc);

				try
				{
					switch (code.name)
					{
						case Opcode.OC_TEXT:
							nextChunk = code.location.getCode();
							++pc;
							return;
						case Opcode.OC_PRINT:
							nextChunk = Utils.str(reg[code.r1]);
							++pc;
							return;
						case Opcode.OC_PRINTX:
							nextChunk = Utils.xmlescape(reg[code.r1]);
							++pc;
							return;
						case Opcode.OC_LOADNONE:
							reg[code.r1] = null;
							break;
						case Opcode.OC_LOADFALSE:
							reg[code.r1] = Boolean.FALSE;
							break;
						case Opcode.OC_LOADTRUE:
							reg[code.r1] = Boolean.TRUE;
							break;
						case Opcode.OC_LOADSTR:
							reg[code.r1] = code.arg;
							break;
						case Opcode.OC_LOADINT:
							reg[code.r1] = Integer.parseInt(code.arg);
							break;
						case Opcode.OC_LOADFLOAT:
							reg[code.r1] = Double.parseDouble(code.arg);
							break;
						case Opcode.OC_LOADDATE:
							reg[code.r1] = Utils.isoparse(code.arg);
							break;
						case Opcode.OC_LOADCOLOR:
							reg[code.r1] = Color.fromdump(code.arg);
							break;
						case Opcode.OC_BUILDLIST:
							reg[code.r1] = new ArrayList();
							break;
						case Opcode.OC_BUILDDICT:
							reg[code.r1] = new HashMap();
							break;
						case Opcode.OC_ADDLIST:
							((List)reg[code.r1]).add(reg[code.r2]);
							break;
						case Opcode.OC_ADDDICT:
							((Map)reg[code.r1]).put(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_UPDATEDICT:
							((Map)reg[code.r1]).putAll((Map)reg[code.r2]);
							break;
						case Opcode.OC_LOADVAR:
							reg[code.r1] = Utils.getItem(variables, code.arg);
							break;
						case Opcode.OC_STOREVAR:
							variables.put(code.arg, reg[code.r1]);
							break;
						case Opcode.OC_ADDVAR:
							variables.put(code.arg, Utils.add(variables.get(code.arg), reg[code.r1]));
							break;
						case Opcode.OC_SUBVAR:
							variables.put(code.arg, Utils.sub(variables.get(code.arg), reg[code.r1]));
							break;
						case Opcode.OC_MULVAR:
							variables.put(code.arg, Utils.mul(variables.get(code.arg), reg[code.r1]));
							break;
						case Opcode.OC_TRUEDIVVAR:
							variables.put(code.arg, Utils.truediv(variables.get(code.arg), reg[code.r1]));
							break;
						case Opcode.OC_FLOORDIVVAR:
							variables.put(code.arg, Utils.floordiv(variables.get(code.arg), reg[code.r1]));
							break;
						case Opcode.OC_MODVAR:
							variables.put(code.arg, Utils.mod(variables.get(code.arg), reg[code.r1]));
							break;
						case Opcode.OC_DELVAR:
							variables.remove(code.arg);
							break;
						case Opcode.OC_FOR:
							Iterator iterator = Utils.iterator(reg[code.r2]);
							if (iterator.hasNext())
							{
								reg[code.r1] = iterator.next();
								iterators.add(new IteratorStackEntry(code.r1, pc, code.jump, iterator));
							}
							else
							{
								pc = code.jump+1;
								continue;
							}
							break;
						case Opcode.OC_BREAK:
						{
							IteratorStackEntry entry = iterators.getLast();
							pc = entry.pcEndFor;
							iterators.removeLast();
							break;
						}
						case Opcode.OC_CONTINUE:
						{
							IteratorStackEntry entry = iterators.getLast();
							pc = entry.pcEndFor;
							// Fall through
						}
						case Opcode.OC_ENDFOR:
						{
							IteratorStackEntry entry = iterators.getLast();
							if (entry.iterator.hasNext())
							{
								reg[entry.iteratorRegSpec] = entry.iterator.next();
								pc = entry.pcFor;
							}
							else
							{
								iterators.removeLast();
							}
							break;
						}
						case Opcode.OC_IF:
							if (!Utils.getBool(reg[code.r1]))
							{
								pc = code.jump+1;
								continue;
							}
							break;
						case Opcode.OC_ELSE:
							pc = code.jump+1;
							continue;
						case Opcode.OC_ENDIF:
							// Skip to next opcode
							break;
						case Opcode.OC_GETATTR:
							reg[code.r1] = Utils.getItem(reg[code.r2], code.arg);
							break;
						case Opcode.OC_GETITEM:
							reg[code.r1] = Utils.getItem(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_GETSLICE12:
							reg[code.r1] = Utils.getSlice(reg[code.r2], reg[code.r3], reg[code.r4]);
							break;
						case Opcode.OC_GETSLICE1:
							reg[code.r1] = Utils.getSlice(reg[code.r2], reg[code.r3], null);
							break;
						case Opcode.OC_GETSLICE2:
							reg[code.r1] = Utils.getSlice(reg[code.r2], null, reg[code.r3]);
							break;
						case Opcode.OC_NOT:
							reg[code.r1] = !Utils.getBool(reg[code.r2]);
							break;
						case Opcode.OC_NEG:
							reg[code.r1] = Utils.neg(reg[code.r2]);
							break;
						case Opcode.OC_EQ:
							reg[code.r1] = Utils.eq(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_NE:
							reg[code.r1] = Utils.ne(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_LT:
							reg[code.r1] = Utils.lt(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_LE:
							reg[code.r1] = Utils.le(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_GT:
							reg[code.r1] = Utils.gt(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_GE:
							reg[code.r1] = Utils.ge(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_CONTAINS:
							reg[code.r1] = Utils.contains(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_NOTCONTAINS:
							reg[code.r1] = !Utils.contains(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_OR:
							reg[code.r1] = Utils.getBool(reg[code.r2]) ? reg[code.r2] : reg[code.r3];
							break;
						case Opcode.OC_AND:
							reg[code.r1] = Utils.getBool(reg[code.r3]) ? reg[code.r2] : reg[code.r3];
							break;
						case Opcode.OC_ADD:
							reg[code.r1] = Utils.add(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_SUB:
							reg[code.r1] = Utils.sub(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_MUL:
							reg[code.r1] = Utils.mul(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_TRUEDIV:
							reg[code.r1] = Utils.truediv(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_FLOORDIV:
							reg[code.r1] = Utils.floordiv(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_MOD:
							reg[code.r1] = Utils.mod(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_CALLFUNC0:
							switch (code.argcode)
							{
								case Opcode.CF0_NOW:
									reg[code.r1] = new Date();
									break;
								case Opcode.CF0_UTCNOW:
									reg[code.r1] = Utils.utcnow();
									break;
								case Opcode.CF0_VARS:
									reg[code.r1] = variables;
									break;
								case Opcode.CF0_RANDOM:
									reg[code.r1] = Utils.random();
									break;
								default:
									throw new UnknownFunctionException(code.arg);
							}
							break;
						case Opcode.OC_CALLFUNC1:
							switch (code.argcode)
							{
								case Opcode.CF1_XMLESCAPE:
									reg[code.r1] = Utils.xmlescape(reg[code.r2]);
									break;
								case Opcode.CF1_CSV:
									reg[code.r1] = Utils.csv(reg[code.r2]);
									break;
								case Opcode.CF1_STR:
									reg[code.r1] = Utils.str(reg[code.r2]);
									break;
								case Opcode.CF1_REPR:
									reg[code.r1] = Utils.repr(reg[code.r2]);
									break;
								case Opcode.CF1_INT:
									reg[code.r1] = Utils.toInteger(reg[code.r2]);
									break;
								case Opcode.CF1_FLOAT:
									reg[code.r1] = Utils.toFloat(reg[code.r2]);
									break;
								case Opcode.CF1_BOOL:
									reg[code.r1] = Utils.getBool(reg[code.r2]) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_LEN:
									reg[code.r1] = Utils.length(reg[code.r2]);
									break;
								case Opcode.CF1_ENUMERATE:
									reg[code.r1] = Utils.enumerate(reg[code.r2]);
									break;
								case Opcode.CF1_ISNONE:
									reg[code.r1] = (null == reg[code.r2]) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_ISSTR:
									reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof String)) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_ISINT:
									reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof Integer)) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_ISFLOAT:
									reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof Double)) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_ISBOOL:
									reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof Boolean)) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_ISDATE:
									reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof Date)) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_ISLIST:
									reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof List)) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_ISDICT:
									reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof Map)) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_ISTEMPLATE:
									reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof Template)) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_ISCOLOR:
									reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof Color)) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_CHR:
									reg[code.r1] = Utils.chr(reg[code.r2]);
									break;
								case Opcode.CF1_ORD:
									reg[code.r1] = Utils.ord(reg[code.r2]);
									break;
								case Opcode.CF1_HEX:
									reg[code.r1] = Utils.hex(reg[code.r2]);
									break;
								case Opcode.CF1_OCT:
									reg[code.r1] = Utils.oct(reg[code.r2]);
									break;
								case Opcode.CF1_BIN:
									reg[code.r1] = Utils.bin(reg[code.r2]);
									break;
								case Opcode.CF1_ABS:
									reg[code.r1] = Utils.abs(reg[code.r2]);
									break;
								case Opcode.CF1_SORTED:
									reg[code.r1] = Utils.sorted(reg[code.r2]);
									break;
								case Opcode.CF1_RANGE:
									reg[code.r1] = Utils.range(reg[code.r2]);
									break;
								case Opcode.CF1_TYPE:
									reg[code.r1] = Utils.type(reg[code.r2]);
									break;
								case Opcode.CF1_GET:
									reg[code.r1] = variables.get(reg[code.r2]);
									break;
								case Opcode.CF1_JSON:
									reg[code.r1] = Utils.json(reg[code.r2]);
									break;
								case Opcode.CF1_REVERSED:
									reg[code.r1] = Utils.reversed(reg[code.r2]);
									break;
								case Opcode.CF1_RANDRANGE:
									reg[code.r1] = Utils.randrange(reg[code.r2]);
									break;
								case Opcode.CF1_RANDCHOICE:
									reg[code.r1] = Utils.randchoice(reg[code.r2]);
									break;
								default:
									throw new UnknownFunctionException(code.arg);
							}
							break;
						case Opcode.OC_CALLFUNC2:
							switch (code.argcode)
							{
								case Opcode.CF2_FORMAT:
									reg[code.r1] = Utils.format(reg[code.r2], reg[code.r3], defaultLocale);
									break;
								case Opcode.CF2_RANGE:
									reg[code.r1] = Utils.range(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CF2_GET:
									reg[code.r1] = variables.containsKey(reg[code.r2]) ? variables.get(reg[code.r2]) : reg[code.r3];
									break;
								case Opcode.CF2_ZIP:
									reg[code.r1] = Utils.zip(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CF2_INT:
									reg[code.r1] = Utils.toInteger(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CF2_RANDRANGE:
									reg[code.r1] = Utils.randrange(reg[code.r2], reg[code.r3]);
									break;
								default:
									throw new UnknownFunctionException(code.arg);
							}
							break;
						case Opcode.OC_CALLFUNC3:
							switch (code.argcode)
							{
								case Opcode.CF3_RANGE:
									reg[code.r1] = Utils.range(reg[code.r2], reg[code.r3], reg[code.r4]);
									break;
								case Opcode.CF3_ZIP:
									reg[code.r1] = Utils.zip(reg[code.r2], reg[code.r3], reg[code.r4]);
									break;
								case Opcode.CF3_RGB:
									reg[code.r1] = Utils.rgb(reg[code.r2], reg[code.r3], reg[code.r4]);
									break;
								case Opcode.CF3_HLS:
									reg[code.r1] = Utils.hls(reg[code.r2], reg[code.r3], reg[code.r4]);
									break;
								case Opcode.CF3_HSV:
									reg[code.r1] = Utils.hsv(reg[code.r2], reg[code.r3], reg[code.r4]);
									break;
								case Opcode.CF3_RANDRANGE:
									reg[code.r1] = Utils.randrange(reg[code.r2], reg[code.r3], reg[code.r4]);
									break;
								default:
									throw new UnknownFunctionException(code.arg);
							}
							break;
						case Opcode.OC_CALLFUNC4:
							switch (code.argcode)
							{
								case Opcode.CF4_RGB:
									reg[code.r1] = Utils.rgb(reg[code.r2], reg[code.r3], reg[code.r4], reg[code.r5]);
									break;
								case Opcode.CF4_HLS:
									reg[code.r1] = Utils.hls(reg[code.r2], reg[code.r3], reg[code.r4], reg[code.r5]);
									break;
								case Opcode.CF4_HSV:
									reg[code.r1] = Utils.hsv(reg[code.r2], reg[code.r3], reg[code.r4], reg[code.r5]);
									break;
								default:
									throw new UnknownFunctionException(code.arg);
							}
							break;
						case Opcode.OC_CALLMETH0:
							switch (code.argcode)
							{
								case Opcode.CM0_SPLIT:
									reg[code.r1] = Utils.split(reg[code.r2]);
									break;
								case Opcode.CM0_RSPLIT:
									reg[code.r1] = Utils.rsplit(reg[code.r2]);
									break;
								case Opcode.CM0_STRIP:
									reg[code.r1] = Utils.strip(reg[code.r2]);
									break;
								case Opcode.CM0_LSTRIP:
									reg[code.r1] = Utils.lstrip(reg[code.r2]);
									break;
								case Opcode.CM0_RSTRIP:
									reg[code.r1] = Utils.rstrip(reg[code.r2]);
									break;
								case Opcode.CM0_UPPER:
									reg[code.r1] = Utils.upper(reg[code.r2]);
									break;
								case Opcode.CM0_LOWER:
									reg[code.r1] = Utils.lower(reg[code.r2]);
									break;
								case Opcode.CM0_CAPITALIZE:
									reg[code.r1] = Utils.capitalize(reg[code.r2]);
									break;
								case Opcode.CM0_ITEMS:
									reg[code.r1] = Utils.items(reg[code.r2]);
									break;
								case Opcode.CM0_ISOFORMAT:
									reg[code.r1] = Utils.isoformat(reg[code.r2]);
									break;
								case Opcode.CM0_MIMEFORMAT:
									reg[code.r1] = Utils.mimeformat(reg[code.r2]);
									break;
								case Opcode.CM0_R:
									reg[code.r1] = ((Color)reg[code.r2]).getR();
									break;
								case Opcode.CM0_G:
									reg[code.r1] = ((Color)reg[code.r2]).getG();
									break;
								case Opcode.CM0_B:
									reg[code.r1] = ((Color)reg[code.r2]).getB();
									break;
								case Opcode.CM0_A:
									reg[code.r1] = ((Color)reg[code.r2]).getA();
									break;
								case Opcode.CM0_HLS:
									reg[code.r1] = ((Color)reg[code.r2]).hls();
									break;
								case Opcode.CM0_HLSA:
									reg[code.r1] = ((Color)reg[code.r2]).hlsa();
									break;
								case Opcode.CM0_HSV:
									reg[code.r1] = ((Color)reg[code.r2]).hsv();
									break;
								case Opcode.CM0_HSVA:
									reg[code.r1] = ((Color)reg[code.r2]).hsva();
									break;
								case Opcode.CM0_LUM:
									reg[code.r1] = ((Color)reg[code.r2]).lum();
									break;
								case Opcode.CM0_DAY:
									reg[code.r1] = Utils.day(reg[code.r2]);
									break;
								case Opcode.CM0_MONTH:
									reg[code.r1] = Utils.month(reg[code.r2]);
									break;
								case Opcode.CM0_YEAR:
									reg[code.r1] = Utils.year(reg[code.r2]);
									break;
								case Opcode.CM0_HOUR:
									reg[code.r1] = Utils.hour(reg[code.r2]);
									break;
								case Opcode.CM0_MINUTE:
									reg[code.r1] = Utils.minute(reg[code.r2]);
									break;
								case Opcode.CM0_SECOND:
									reg[code.r1] = Utils.second(reg[code.r2]);
									break;
								case Opcode.CM0_MICROSECOND:
									reg[code.r1] = Utils.microsecond(reg[code.r2]);
									break;
								case Opcode.CM0_WEEKDAY:
									reg[code.r1] = Utils.weekday(reg[code.r2]);
									break;
								case Opcode.CM0_YEARDAY:
									reg[code.r1] = Utils.yearday(reg[code.r2]);
									break;
								case Opcode.CM0_RENDER:
									reg[code.r1] = ((Template)reg[code.r2]).renders(null);
									break;
								default:
									throw new UnknownMethodException(code.arg);
							}
							break;
						case Opcode.OC_CALLMETH1:
							switch (code.argcode)
							{
								case Opcode.CM1_SPLIT:
									reg[code.r1] = Utils.split(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_RSPLIT:
									reg[code.r1] = Utils.rsplit(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_STRIP:
									reg[code.r1] = Utils.strip(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_LSTRIP:
									reg[code.r1] = Utils.lstrip(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_RSTRIP:
									reg[code.r1] = Utils.rstrip(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_STARTSWITH:
									reg[code.r1] = Utils.startswith(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_ENDSWITH:
									reg[code.r1] = Utils.endswith(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_FIND:
									reg[code.r1] = Utils.find(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_RFIND:
									reg[code.r1] = Utils.rfind(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_GET:
									reg[code.r1] = ((Map)reg[code.r2]).get(reg[code.r3]);
									break;
								case Opcode.CM1_WITHLUM:
									reg[code.r1] = Utils.withlum(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_WITHA:
									reg[code.r1] = Utils.witha(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_JOIN:
									reg[code.r1] = Utils.join(reg[code.r2], reg[code.r3]);
									break;
								default:
									throw new UnknownMethodException(code.arg);
							}
							break;
						case Opcode.OC_CALLMETH2:
							switch (code.argcode)
							{
								case Opcode.CM2_SPLIT:
									reg[code.r1] = Utils.split(reg[code.r2], reg[code.r3], reg[code.r4]);
									break;
								case Opcode.CM2_RSPLIT:
									reg[code.r1] = Utils.rsplit(reg[code.r2], reg[code.r3], reg[code.r4]);
									break;
								case Opcode.CM2_FIND:
									reg[code.r1] = Utils.find(reg[code.r2], reg[code.r3], reg[code.r4]);
									break;
								case Opcode.CM2_RFIND:
									reg[code.r1] = Utils.rfind(reg[code.r2], reg[code.r3], reg[code.r4]);
									break;
								case Opcode.CM2_REPLACE:
									reg[code.r1] = Utils.replace(reg[code.r2], reg[code.r3], reg[code.r4]);
									break;
								case Opcode.CM2_GET:
									reg[code.r1] = ((Map)reg[code.r2]).containsKey(reg[code.r3]) ? ((Map)reg[code.r2]).get(reg[code.r3]) : reg[code.r4];
									break;
								default:
									throw new UnknownMethodException(code.arg);
							}
							break;
						case Opcode.OC_CALLMETH3:
							switch (code.argcode)
							{
								case Opcode.CM3_FIND:
									reg[code.r1] = Utils.find(reg[code.r2], reg[code.r3], reg[code.r4], reg[code.r5]);
									break;
								case Opcode.CM3_RFIND:
									reg[code.r1] = Utils.rfind(reg[code.r2], reg[code.r3], reg[code.r4], reg[code.r5]);
									break;
								default:
									throw new UnknownMethodException(code.arg);
							}
							break;
						case Opcode.OC_CALLMETHKW:
							switch (code.argcode)
							{
								case Opcode.CMKW_RENDER:
									reg[code.r1] = ((Template)reg[code.r2]).renders((Map)reg[code.r3]);
									break;
								default:
									throw new UnknownMethodException(code.arg);
							}
							break;
						case Opcode.OC_RENDER:
							if (reg[code.r1] instanceof InterpretedTemplate)
							{
								subTemplateIterator = ((InterpretedTemplate)reg[code.r1]).render((Map)reg[code.r2]);
								if (subTemplateIterator.hasNext())
								{
									nextChunk = (String)subTemplateIterator.next();
									++pc;
									return;
								}
								else
								{
									subTemplateIterator = null;
								}
								break;
							}
							else
							{
								String output = ((Template)reg[code.r1]).renders((Map)reg[code.r2]);
								if (output.length() != 0)
								{
									nextChunk = output;
									++pc;
									return;
								}
								break;
							}
						case Opcode.OC_DEF:
							variables.put(code.arg, code.template);
							pc = code.jump+1;
							continue;
						case Opcode.OC_ENDDEF:
							// Skip to next opcode
							break;
						default:
							throw new RuntimeException("Unknown opcode '" + code.name + "'!");
					}
				}
				catch (Exception ex)
				{
					throw new LocationException(ex, code, pc);
				}
				++pc;
			}
			// finished => no next chunk available
			nextChunk = null;
		}
	}

	class IteratorReader extends Reader
	{
		private Iterator<String> iterator;
		private StringBuffer buffered;

		public IteratorReader(Iterator<String> iterator)
		{
			this.iterator = iterator;
			this.buffered = new StringBuffer();
		}

		public int read(char[] cbuf, int off, int len)
		{
			if (iterator == null)
				return -1;
			while (buffered.length() < len)
			{
				if (!iterator.hasNext())
					break;
				buffered.append(iterator.next());
			}
			int resultlen = buffered.length();
			if (resultlen > len) // don't return more than we have to.
				resultlen = len;
			buffered.getChars(0, resultlen, cbuf, off); // copy the chars
			buffered.delete(0, resultlen); // remove output from buffer
			return resultlen>0 ? resultlen : -1;
		}

		public void close()
		{
			this.iterator = null;
		}
	}

	public static List<Location> tokenizeTags(String source, String name, String startdelim, String enddelim)
	{
		Pattern tagPattern = Pattern.compile(escapeREchars(startdelim) + "(printx|print|code|for|if|elif|else|end|break|continue|render|def|note)(\\s*(.*?)\\s*)?" + escapeREchars(enddelim), Pattern.DOTALL);
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

	public static List tokenizeCode(Location location) throws LexicalException
	{
		String source = location.getCode();

		LinkedList tokens = new LinkedList();

		int pos = 0;
		int stringStartPos = 0; // The starting position of a string constant
		int stringMode = 0; // 0 == default; 1 == single-quoted string; 2 == double-quoted strings
		StringBuffer collectString = null; // characters are collected here, while we're in a string constant

		try
		{
			while (source.length() != 0)
			{
				Matcher tokenMatcher = tokenPattern.matcher(source);
				Matcher nameMatcher = namePattern.matcher(source);
				Matcher floatMatcher = floatPattern.matcher(source);
				Matcher hexintMatcher = hexintPattern.matcher(source);
				Matcher octintMatcher = octintPattern.matcher(source);
				Matcher binintMatcher = binintPattern.matcher(source);
				Matcher intMatcher = intPattern.matcher(source);
				Matcher dateMatcher = datePattern.matcher(source);
				Matcher color3Matcher = color3Pattern.matcher(source);
				Matcher color4Matcher = color4Pattern.matcher(source);
				Matcher color6Matcher = color6Pattern.matcher(source);
				Matcher color8Matcher = color8Pattern.matcher(source);
				Matcher whitespaceMatcher = whitespacePattern.matcher(source);
				Matcher escaped8BitCharMatcher = escaped8BitCharPattern.matcher(source);
				Matcher escaped16BitCharMatcher = escaped16BitCharPattern.matcher(source);
				Matcher escaped32BitCharMatcher = escaped32BitCharPattern.matcher(source);

				int len;
				if (stringMode==0 && tokenMatcher.lookingAt())
				{
					len = tokenMatcher.end();
					tokens.add(new Token(pos, pos+len, tokenMatcher.group()));
				}
				else if (stringMode==0 && nameMatcher.lookingAt())
				{
					len = nameMatcher.end();
					String name = nameMatcher.group();
					if (name.equals("in") || name.equals("not") || name.equals("or") || name.equals("and") || name.equals("del"))
						tokens.add(new Token(pos, pos+len, name));
					else if (name.equals("None"))
						tokens.add(new LoadNone(pos, pos+len));
					else if (name.equals("True"))
						tokens.add(new LoadTrue(pos, pos+len));
					else if (name.equals("False"))
						tokens.add(new LoadFalse(pos, pos+len));
					else
						tokens.add(new Name(pos, pos+len, name));
				}
				else if (stringMode==0 && dateMatcher.lookingAt())
				{
					len = dateMatcher.end();
					tokens.add(new LoadDate(pos, pos+len, Utils.isoparse(dateMatcher.group().substring(1))));
				}
				else if (stringMode==0 && color8Matcher.lookingAt())
				{
					len = color8Matcher.end();
					String value = color8Matcher.group();
					int r = Integer.valueOf(value.substring(1, 3), 16);
					int g = Integer.valueOf(value.substring(3, 5), 16);
					int b = Integer.valueOf(value.substring(5, 7), 16);
					int a = Integer.valueOf(value.substring(7, 9), 16);
					tokens.add(new LoadColor(pos, pos+len, new Color(r, g, b, a)));
				}
				else if (stringMode==0 && color6Matcher.lookingAt())
				{
					len = color6Matcher.end();
					String value = color6Matcher.group();
					int r = Integer.valueOf(value.substring(1, 3), 16);
					int g = Integer.valueOf(value.substring(3, 5), 16);
					int b = Integer.valueOf(value.substring(5, 7), 16);
					tokens.add(new LoadColor(pos, pos+len, new Color(r, g, b)));
				}
				else if (stringMode==0 && color4Matcher.lookingAt())
				{
					len = color4Matcher.end();
					String value = color4Matcher.group();
					int r = 17*Integer.valueOf(value.substring(1, 2), 16);
					int g = 17*Integer.valueOf(value.substring(2, 3), 16);
					int b = 17*Integer.valueOf(value.substring(3, 4), 16);
					int a = 17*Integer.valueOf(value.substring(4, 5), 16);
					tokens.add(new LoadColor(pos, pos+len, new Color(r, g, b, a)));
				}
				else if (stringMode==0 && color3Matcher.lookingAt())
				{
					len = color3Matcher.end();
					String value = color3Matcher.group();
					int r = 17*Integer.valueOf(value.substring(1, 2), 16);
					int g = 17*Integer.valueOf(value.substring(2, 3), 16);
					int b = 17*Integer.valueOf(value.substring(3, 4), 16);
					tokens.add(new LoadColor(pos, pos+len, new Color(r, g, b)));
				}
				else if (stringMode==0 && floatMatcher.lookingAt())
				{
					len = floatMatcher.end();
					tokens.add(new LoadFloat(pos, pos+len, Double.parseDouble(floatMatcher.group())));
				}
				else if (stringMode==0 && hexintMatcher.lookingAt())
				{
					len = hexintMatcher.end();
					tokens.add(new LoadInt(pos, pos+len, Integer.parseInt(hexintMatcher.group().substring(2), 16)));
				}
				else if (stringMode==0 && octintMatcher.lookingAt())
				{
					len = octintMatcher.end();
					tokens.add(new LoadInt(pos, pos+len, Integer.parseInt(octintMatcher.group().substring(2), 8)));
				}
				else if (stringMode==0 && binintMatcher.lookingAt())
				{
					len = binintMatcher.end();
					tokens.add(new LoadInt(pos, pos+len, Integer.parseInt(binintMatcher.group().substring(2), 2)));
				}
				else if (stringMode==0 && intMatcher.lookingAt())
				{
					len = intMatcher.end();
					tokens.add(new LoadInt(pos, pos+len, Integer.parseInt(intMatcher.group())));
				}
				else if (stringMode==0 && source.startsWith("'"))
				{
					stringStartPos = pos;
					len = 1;
					stringMode = 1;
					collectString = new StringBuffer();
				}
				else if (stringMode==0 && source.startsWith("\""))
				{
					stringStartPos = pos;
					len = 1;
					stringMode = 2;
					collectString = new StringBuffer();
				}
				else if (stringMode==1 && source.startsWith("'") || (stringMode==2 && source.startsWith("\"")))
				{
					len = 1;
					stringMode = 0;
					tokens.add(new LoadStr(stringStartPos, pos+len, collectString.toString()));
					collectString = null;
				}
				else if (stringMode==0 && whitespaceMatcher.lookingAt())
				{
					len = whitespaceMatcher.end();
				}
				else if (stringMode!=0 && source.startsWith("\\\\"))
				{
					len = 2;
					collectString.append("\\");
				}
				else if (stringMode!=0 && source.startsWith("\\'"))
				{
					len = 2;
					collectString.append("'");
				}
				else if (stringMode!=0 && source.startsWith("\\\""))
				{
					len = 2;
					collectString.append("\"");
				}
				else if (stringMode!=0 && source.startsWith("\\a"))
				{
					len = 2;
					collectString.append("\u0007");
				}
				else if (stringMode!=0 && source.startsWith("\\b"))
				{
					len = 2;
					collectString.append("\u0008");
				}
				else if (stringMode!=0 && source.startsWith("\\f"))
				{
					len = 2;
					collectString.append("\u000c");
				}
				else if (stringMode!=0 && source.startsWith("\\n"))
				{
					len = 2;
					collectString.append("\n");
				}
				else if (stringMode!=0 && source.startsWith("\\r"))
				{
					len = 2;
					collectString.append("\r");
				}
				else if (stringMode!=0 && source.startsWith("\\t"))
				{
					len = 2;
					collectString.append("\t");
				}
				else if (stringMode!=0 && source.startsWith("\\v"))
				{
					len = 2;
					collectString.append("\u000b");
				}
				else if (stringMode!=0 && source.startsWith("\\e"))
				{
					len = 2;
					collectString.append("\u001b");
				}
				else if (stringMode!=0 && escaped8BitCharMatcher.lookingAt())
				{
					len = 4;
					collectString.append((char)Integer.parseInt(escaped8BitCharMatcher.group().substring(2), 16));
				}
				else if (stringMode!=0 && escaped16BitCharMatcher.lookingAt())
				{
					len = 6;
					collectString.append((char)Integer.parseInt(escaped16BitCharMatcher.group().substring(2), 16));
				}
				else if (stringMode!=0 && escaped32BitCharMatcher.lookingAt())
				{
					len = 10;
					throw new RuntimeException("character " + escaped32BitCharMatcher.group() + " (outside the BMP) not supported");
				}
				else if (stringMode!=0)
				{
					len = 1;
					collectString.append(source.charAt(0));
				}
				else
				{
					throw new LexicalException(pos, pos+1, source.substring(0, 1));
				}
				pos += len;
				source = source.substring(len);
			}
			if (stringMode != 0)
				throw new UnterminatedStringException();
		}
		catch (LocationException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			// decorate inner exception with location information
			throw new LocationException(ex, location);
		}
		return tokens;
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

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		int indent = 1;

		buffer.append("def " + name + "(**vars) {\n");
		int size = opcodes.size();
		for (int i = 0; i < size; ++i)
		{
			Opcode code = opcodes.get(i);

			if (code.name == Opcode.OC_ELSE || code.name == Opcode.OC_ENDIF || code.name == Opcode.OC_ENDFOR || code.name == Opcode.OC_ENDDEF)
				--indent;
			for (int j = 0; j < indent; ++j)
				buffer.append("\t");
			if (code.name == Opcode.OC_ENDIF || code.name == Opcode.OC_ENDFOR)
				buffer.append("}");
			else if (code.name == Opcode.OC_FOR || code.name == Opcode.OC_IF || code.name == Opcode.OC_DEF)
				buffer.append(code + " {");
			else if (code.name == Opcode.OC_ELSE)
				buffer.append("} else {");
			else if (code.name == Opcode.OC_ENDDEF)
				buffer.append("}");
			else
				buffer.append(code);
			buffer.append("\n");
			if (code.name == Opcode.OC_FOR || code.name == Opcode.OC_IF || code.name == Opcode.OC_ELSE || code.name == Opcode.OC_DEF)
				++indent;
		}
		buffer.append("}\n");
		return buffer.toString();
	}

	public String javaSource()
	{
		return new JavaSource4Template(this).toString();
	}

	public String javascriptSource()
	{
		return new JavascriptSource4Template(this).toString();
	}
}
