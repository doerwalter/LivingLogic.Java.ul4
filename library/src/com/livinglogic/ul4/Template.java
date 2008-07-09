package com.livinglogic.ul4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.StringReader;
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

public class Template
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
	private static Pattern whitespacePattern;
	private static Pattern escaped8BitCharPattern;
	private static Pattern escaped16BitCharPattern;
	private static Pattern escaped32BitCharPattern;

	static
	{
		// Initializes regular expressions
		tokenPattern = Pattern.compile("\\(|\\)|\\[|\\]|\\{|\\}|\\.|,|==|\\!=|<=|<|>=|>|=|\\+=|\\-=|\\*=|/=|//=|%=|%|:|\\+|-|\\*|//|/");
		namePattern = Pattern.compile("[a-zA-Z_][\\w]*");
		// We don't have negatve numbers, this is handled by constant folding in the AST for unary minus
		floatPattern = Pattern.compile("(\\d+(\\.\\d*)?[eE][+-]?\\d+|\\d+\\.\\d*([eE][+-]?\\d+)?)");
		hexintPattern = Pattern.compile("0[xX][\\da-fA-F]+");
		octintPattern = Pattern.compile("0[oO][0-7]+");
		binintPattern = Pattern.compile("0[bB][01]+");
		intPattern = Pattern.compile("\\d+");
		datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T(\\d{2}:\\d{2}(:\\d{2}(.\\d{6})?)?)?");
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
	public static final String VERSION = "4";

	/**
	 * The start delimiter for tags (defaults to <code>&lt;?</code>)
	 */ 
	public String startdelim;

	/**
	 * The end delimiter for tags (defaults to <code>?&gt;</code>)
	 */ 
	public String enddelim;

	/**
	 * The template source.
	 */ 
	public String source;

	/**
	 * The list of opcodes.
	 */ 
	public List opcodes;
	
	/**
	 * The locale to be used when formatting int, float or date objects.
	 */ 
	public Locale defaultLocale;

	/**
	 * Has {@link annotate} been called for this template?
	 */
	private boolean annotated = false;

	/**
	 * Creates an empty template object.
	 */
	public Template()
	{
		this.source = null;
		this.opcodes = new LinkedList();
		this.defaultLocale = Locale.GERMANY;
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, Location location)
	{
		opcodes.add(new Opcode(name, -1, -1, -1, -1, -1, null, location));
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, String arg, Location location)
	{
		opcodes.add(new Opcode(name, -1, -1, -1, -1, -1, arg, location));
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, Location location)
	{
		opcodes.add(new Opcode(name, r1, -1, -1, -1, -1, null, location));
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, -1, -1, -1, -1, arg, location));
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, -1, -1, -1, null, location));
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, -1, -1, -1, arg, location));
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, int r3, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, -1, -1, null, location));
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, int r3, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, -1, -1, arg, location));
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, int r3, int r4, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, -1, null, location));
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, int r3, int r4, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, -1, arg, location));
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, int r3, int r4, int r5, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, r5, null, location));
	}

	/**
	 * Appends a new opcode to {@link opcodes}.
	 */
	public void opcode(int name, int r1, int r2, int r3, int r4, int r5, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, r5, arg, location));
	}

	protected static int readintInternal(Reader reader, char terminator) throws IOException
	{
		int retVal = 0;
		boolean digitFound = false;
		boolean terminatorFound = false;
		int readInt = reader.read();
		char charValue;
		int intValue;
		while ((-1 < readInt) && !terminatorFound)
		{
			charValue = (char)readInt;
			intValue = Character.digit(charValue, 10);
			if (-1 < intValue)
			{
				retVal = retVal * 10 + intValue;
				digitFound = true;
			}
			else if (charValue == terminator)
			{
				terminatorFound = true;
				if (!digitFound)
					retVal = -1;
			}
			else
			{
				throw new RuntimeException("Invalid terminator, expected " + terminator + ", got " + charValue);
			}
			if (!terminatorFound)
			{
				readInt = reader.read();
			}
		}
		return retVal;
	}

	/**
	 * Reads a (non-negative) integer value from a stream.
	 * @param reader the reader from which the value is read.
	 * @param terminator The character after the digits of the integer value
	 * @return The integer value
	 * @throws IOException if reading from the stream fails
	 * @throws RuntimeException if the integer value is malformed (i.e. the
	 *                          terminator is missing)
	 */
	protected static int readint(Reader reader, char terminator) throws IOException
	{
		int retVal = readintInternal(reader, terminator);
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
	 * @param terminator The terminator character that was used in {@link writestr}.
	 * @return The string or <code>null</code>
	 * @throws IOException if reading from the stream fails
	 * @throws RuntimeException if the integer value is malformed
	 */
	protected static String readstr(Reader reader, char terminator) throws IOException
	{
		String retVal = null;
		int stringLength = readintInternal(reader, terminator);
		if (-1 < stringLength)
		{
			char[] retValChars = new char[stringLength];
			int readCharSize = reader.read(retValChars);
			if (readCharSize != stringLength)
			{
				throw new RuntimeException("Short read!");
			}
			retVal = new String(retValChars);
		}
		return retVal;
	}

	/**
	 * Reads a register specification from a stream. A register specification is
	 * either a digit or the character '-'.
	 * @param reader the reader from which the specification is read.
	 * @return The register number or -1.
	 * @throws IOException if reading from the stream fails
	 * @throws RuntimeException if the register specification is malformed
	 */
	protected static int readspec(Reader reader) throws IOException
	{
		int retVal;
		int readInt = reader.read();
		char charValue;
		if (-1 < readInt)
		{
			charValue = (char)readInt;
			if ('-' == charValue)
			{
				retVal = -1;
			}
			else
			{
				retVal = Character.digit(charValue, 10);
				if (0 > retVal)
				{
					throw new RuntimeException("Invalid register spec " + charValue);
				}
			}
		}
		else
		{
			throw new RuntimeException("Short read!");
		}
		return retVal;
	}

	/**
	 * Reads a linefeed from a stream.
	 * @param reader the reader from which the linefeed is read.
	 * @throws IOException if reading from the stream fails
	 * @throws RuntimeException if the character read from the stream is not a
	 *                          linefeed
	 */
	protected static void readcr(Reader reader) throws IOException
	{
		int readInt = reader.read();
		if (-1 < readInt)
		{
			char charValue = (char)readInt;
			if ('\n' != charValue)
			{
				throw new RuntimeException("Invalid linefeed " + charValue);
			}
		}
		else
		{
			throw new RuntimeException("Short read!");
		}
	}

	/**
	 * loads the source of a template from a reader, whichout checking the version
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
		readstr(bufferedReader, '<'); // skip start delimiter
		readcr(bufferedReader);
		readstr(bufferedReader, '>'); // skip end delimiter
		readcr(bufferedReader);
		return readstr(bufferedReader, '"');
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
	public static Template load(Reader reader) throws IOException
	{
		Template retVal = new Template();
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
		retVal.startdelim = readstr(bufferedReader, '<');
		readcr(bufferedReader);
		retVal.enddelim = readstr(bufferedReader, '>');
		readcr(bufferedReader);
		retVal.source = readstr(bufferedReader, '"');
		readcr(bufferedReader);
		int count = readint(bufferedReader, '#');
		readcr(bufferedReader);
		Location location = null;
		for (int i = 0; i < count; i++)
		{
			int r1 = readspec(bufferedReader);
			int r2 = readspec(bufferedReader);
			int r3 = readspec(bufferedReader);
			int r4 = readspec(bufferedReader);
			int r5 = readspec(bufferedReader);
			String code = readstr(bufferedReader, ':');
			String arg = readstr(bufferedReader, '.');
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
					location = new Location(retVal.source, readstr(bufferedReader, '='),
						readint(bufferedReader, '('), readint(bufferedReader, ')'),
						readint(bufferedReader, '{'), readint(bufferedReader, '}'));
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
			retVal.opcodes.add(new Opcode(code, r1, r2, r3, r4, r5, arg, location));
			readcr(bufferedReader);
		}
		return retVal;
	}

	/**
	 * loads a template from a string.
	 * @param bytecode of the compiled template.
	 * @return The template object.
	 * @throws IOException if reading from the stream fails
	 */
	public static Template load(String bytecode)
	{
		try
		{
			return load(new StringReader(bytecode));
		}
		catch (IOException ex) // can not happen, when reading from a StringReader
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
	protected static void writeint(Writer writer, int value, char terminator) throws IOException
	{
		writer.write(String.valueOf(value));
		writer.write(terminator);
	}

	/**
	 * writes a string to a stream in such a way that the string can be reliable read again.
	 * @param writer the stream to which to write.
	 * @param value the string value to be written (may be null).
	 * @param terminator a terminating character written after the string length.
	 * @throws IOException if writing to the stream fails
	 */
	protected static void writestr(Writer writer, String value, char terminator) throws IOException
	{
		if (value == null)
		{
			writer.write(terminator);
		}
		else
		{
			writer.write(String.valueOf(value.length()));
			writer.write(terminator);
			writer.write(value);
		}
	}

	/**
	 * writes a register specification to a stream (which is either a digit or '-' in case the register spec is empty.
	 * @param writer the stream to which to write.
	 * @param spec the register number or -1 in case the register spec is empty.
	 * @throws IOException if writing to the stream fails
	 */
	protected static void writespec(Writer writer, int spec) throws IOException
	{
		if (spec == -1)
			writer.write("-");
		else
			writer.write(String.valueOf(spec));
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
		writestr(writer, startdelim, '<');
		writer.write("\n");
		writestr(writer, enddelim, '>');
		writer.write("\n");
		writestr(writer, source, '"');
		writer.write("\n");
		writeint(writer, opcodes.size(), '#');
		writer.write("\n");
		Location lastLocation = null;
		for (int i = 0; i < opcodes.size(); ++i)
		{
			Opcode opcode = (Opcode)opcodes.get(i);
			writespec(writer, opcode.r1);
			writespec(writer, opcode.r2);
			writespec(writer, opcode.r3);
			writespec(writer, opcode.r4);
			writespec(writer, opcode.r5);
			writestr(writer, Opcode.code2name(opcode.name), ':');
			writestr(writer, opcode.arg, '.');
			if (opcode.location != lastLocation)
			{
				writer.write("*");
				writestr(writer, opcode.location.type, '=');
				writeint(writer, opcode.location.starttag, '(');
				writeint(writer, opcode.location.endtag, ')');
				writeint(writer, opcode.location.startcode, '{');
				writeint(writer, opcode.location.endcode, '}');
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
		catch (IOException ex) // can not happen, when dumping to a StringWriter
		{
		}
		return writer.toString();
	}

	/**
	 * Annotates all control flow opcodes in the template with a jump location.
	 * <ul>
	 * <li>(a <code>for</code> opcode gets annotated with the location of the
	 * associated <code>endfor</code> opcode;</li>
	 * <li>an <code>if</code> opcode gets annotated with the location of the
	 * associated <code>else</code> or <code>endif</code> opcode;</li>
	 * <li>an <code>else</code> opcode gets annotated with the location of
	 * <code>endif</code> opcode;</li>
	 * <li>a <code>break</code> opcode gets annotated with the location of next
	 * opcode after the associated <code>endfor</code> opcode.</li>
	 * <li>a <code>continue</code> opcode gets annotated with the location of next
	 * opcode after the associated ENDFOR opcode.</li>
	 * </ul>
	 */
	protected void annotate()
	{
		if (!annotated)
		{
			LinkedList stack = new LinkedList();
			for (int i = 0; i < opcodes.size(); ++i)
			{
				Opcode opcode = (Opcode)opcodes.get(i);
				switch (opcode.name)
				{
					case Opcode.OC_IF:
						i = annotateIf(i, 0);
						break;
					case Opcode.OC_FOR:
						i = annotateFor(i, 0);
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
				}
			}
			annotated = true;
		}
	}

	protected int annotateIf(int ifStart, int forDepth)
	{
		int jump = ifStart;
		for (int i = ifStart+1; i < opcodes.size(); ++i)
		{
			Opcode opcode = (Opcode)opcodes.get(i);
			switch (opcode.name)
			{
				case Opcode.OC_IF:
					i = annotateIf(i, forDepth);
					break;
				case Opcode.OC_FOR:
					i = annotateFor(i, forDepth);
					break;
				case Opcode.OC_ELSE:
					((Opcode)opcodes.get(jump)).jump = i;
					jump = i;
					break;
				case Opcode.OC_ENDIF:
					((Opcode)opcodes.get(jump)).jump = i;
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
			}
		}
		throw new BlockException("unclosed if block");
	}

	protected int annotateFor(int loopStart, int forDepth)
	{
		++forDepth;
		LinkedList breaks = new LinkedList();
		LinkedList continues = new LinkedList();

		for (int i = loopStart+1; i < opcodes.size(); ++i)
		{
			Opcode opcode = (Opcode)opcodes.get(i);
			switch (opcode.name)
			{
				case Opcode.OC_IF:
					i = annotateIf(i, forDepth);
					break;
				case Opcode.OC_FOR:
					i = annotateFor(i, forDepth);
					break;
				case Opcode.OC_ELSE:
					throw new BlockException("else in for loop");
				case Opcode.OC_ENDIF:
					throw new BlockException("endif in for loop");
				case Opcode.OC_BREAK:
					breaks.add(new Integer(i));
					break;
				case Opcode.OC_CONTINUE:
					continues.add(new Integer(i));
					break;
				case Opcode.OC_ENDFOR:
					int j;
					int jump;
					for (j = 0; i < breaks.size(); ++i)
					{
						jump = ((Integer)breaks.get(i)).intValue();
						((Opcode)opcodes.get(jump)).jump = i;
					}
					for (j = 0; i < continues.size(); ++i)
					{
						jump = ((Integer)continues.get(i)).intValue();
						((Opcode)opcodes.get(jump)).jump = i;
					}
					((Opcode)opcodes.get(loopStart)).jump = i;
					return i;
			}
		}
		throw new BlockException("unclosed loop");
	}

	public Iterator render()
	{
		return new Renderer(null, null);
	}

	public Iterator render(Map variables)
	{
		return new Renderer(variables, null);
	}

	/**
	 * Renders the template.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code.
	 * @param templates a map containing other template objects that can be called
	 *                  by the template via the <code>&lt;?render?&gt;</code> tag.
	 * @return An iterator that returns the string output piece by piece.
	 */
	public Iterator render(Map variables, Map templates)
	{
		return new Renderer(variables, templates);
	}

	public String renders()
	{
		return renders(null, null);
	}

	public String renders(Map variables)
	{
		return renders(variables, null);
	}

	/**
	 * Renders the template and returns the resulting string.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code.
	 * @param templates a map containing other template objects that can be called
	 *                  by the template via the <code>&lt;?render?&gt;</code> tag.
	 * @return The render output as a string.
	 */
	public String renders(Map variables, Map templates)
	{
		StringBuffer output = new StringBuffer();

		for (Iterator iterator = render(variables, templates); iterator.hasNext();)
		{
			output.append((String)iterator.next());
		}
		return output.toString();
	}

	class Renderer implements Iterator
	{
		/**
		 * The current program counter
		 */
		private int pc = 0;

		/**
		 * The ten registers of our CPU
		 */
		private Object[] reg = new Object[10];

		/**
		 * The variables passed to the {@com.livinglogic.ul4.Template#render} call
		 * During the run of the iterator loop variables and variables from
		 * <code>&lt;?code>&gt;</code> tag will be stored here
		 */
		private Map variables;

		/**
		 * A map containing other template object that can be called by the
		 * template code via the <code>&lt;?render?&gt;</code> tag.
		 */
		private Map templates;

		/**
		 * The stack of active for loops
		 */
		private LinkedList iterators = new LinkedList();

		/**
		 * If a subtemplate is running (i.e. if we're inside a
		 * <code>&lt;?render?&gt;</code> tag), this variable references the
		 * active part iterator for the subtemplate.
		 */
		private Iterator subTemplateIterator = null;

		/**
		 * Since we implement the iterator interface we have to support both
		 * <code>next</code> and <code>hasNext</code>. This means that neither
		 * of the two methods can directly run the opcodes to get the next output
		 * chunk. Instead of that we have a method {@link getNextChunk} that runs
		 * the opcodes until the next output chunk is produced and stores it in
		 * <code>nextChunk</code>, when both <code>next</code> and
		 * <code>hasNext</code> can refer to it.
		 */
		private String nextChunk = null;

		public Renderer(Map variables, Map templates)
		{
			annotate();
			if (variables == null)
				variables = new HashMap();
			this.variables = variables;
			if (templates == null)
				templates = new HashMap();
			this.templates = templates;
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

		public Object next()
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
				if (subTemplateIterator.hasNext())
				{
					nextChunk = (String)subTemplateIterator.next();
					return;
				}
				else
				{
					subTemplateIterator = null;
				}
			}
			while (pc < opcodes.size())
			{
				Opcode code = (Opcode)opcodes.get(pc);

				try
				{
					switch (code.name)
					{
						case Opcode.OC_TEXT:
							nextChunk = code.location.getCode();
							++pc;
							return;
						case Opcode.OC_PRINT:
							nextChunk = Utils.toString(reg[code.r1]);
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
							reg[code.r1] = new Integer(Integer.parseInt(code.arg));
							break;
						case Opcode.OC_LOADFLOAT:
							reg[code.r1] = new Double(Double.parseDouble(code.arg));
							break;
						case Opcode.OC_LOADDATE:
							reg[code.r1] = Utils.isoDateFormatter.parse(code.arg);
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
						case Opcode.OC_LOADVAR:
							reg[code.r1] = variables.get(code.arg);
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
							IteratorStackEntry entry = (IteratorStackEntry)iterators.getLast();
							pc = entry.pcEndFor;
							iterators.removeLast();
							break;
						}
						case Opcode.OC_CONTINUE:
						{
							IteratorStackEntry entry = (IteratorStackEntry)iterators.getLast();
							pc = entry.pcEndFor;
							// Fall through
						}
						case Opcode.OC_ENDFOR:
						{
							IteratorStackEntry entry = (IteratorStackEntry)iterators.getLast();
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
							reg[code.r1] = ((Map)reg[code.r2]).get(code.arg);
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
							reg[code.r1] = Utils.getSlice(reg[code.r2], null, reg[code.r4]);
							break;
						case Opcode.OC_NOT:
							reg[code.r1] = Utils.getBool(reg[code.r2]) ? Boolean.FALSE : Boolean.TRUE;
							break;
						case Opcode.OC_NEG:
							reg[code.r1] = Utils.neg(reg[code.r2]);
							break;
						case Opcode.OC_EQ:
							reg[code.r1] = Utils.eq(reg[code.r2], reg[code.r3]) ? Boolean.TRUE : Boolean.FALSE;
							break;
						case Opcode.OC_NE:
							reg[code.r1] = Utils.eq(reg[code.r2], reg[code.r3]) ? Boolean.FALSE : Boolean.TRUE;
							break;
						case Opcode.OC_LT:
							reg[code.r1] = Utils.lt(reg[code.r2], reg[code.r3]) ? Boolean.TRUE : Boolean.FALSE;
							break;
						case Opcode.OC_LE:
							reg[code.r1] = Utils.le(reg[code.r2], reg[code.r3]) ? Boolean.TRUE : Boolean.FALSE;
							break;
						case Opcode.OC_GT:
							reg[code.r1] = Utils.le(reg[code.r2], reg[code.r3]) ? Boolean.FALSE : Boolean.TRUE;
							break;
						case Opcode.OC_GE:
							reg[code.r1] = Utils.lt(reg[code.r2], reg[code.r3]) ? Boolean.FALSE : Boolean.TRUE;
							break;
						case Opcode.OC_NOTCONTAINS:
							reg[code.r1] = Utils.contains(reg[code.r2], reg[code.r3]) ? Boolean.FALSE : Boolean.TRUE;
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
							}
							break;
						case Opcode.OC_CALLFUNC1:
							switch (code.argcode)
							{
								case Opcode.CF1_XMLESCAPE:
									reg[code.r1] = Utils.xmlescape(reg[code.r2]);
									break;
								case Opcode.CF1_STR:
									reg[code.r1] = Utils.toString(reg[code.r2]);
									break;
								case Opcode.CF1_INT:
									reg[code.r1] = Utils.toInteger(reg[code.r2]);
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
								case Opcode.CF1_SORTED:
									reg[code.r1] = Utils.sorted(reg[code.r2]);
									break;
								case Opcode.CF1_RANGE:
									reg[code.r1] = Utils.range(reg[code.r2]);
									break;
							}
							break;
						case Opcode.OC_CALLFUNC2:
							switch (code.argcode)
							{
								case Opcode.CF2_RANGE:
									reg[code.r1] = Utils.range(reg[code.r2], reg[code.r3]);
									break;
							}
							break;
						case Opcode.OC_CALLFUNC3:
							switch (code.argcode)
							{
								case Opcode.CF3_RANGE:
									reg[code.r1] = Utils.range(reg[code.r2], reg[code.r3], reg[code.r4]);
									break;
							}
							break;
						case Opcode.OC_CALLMETH0:
							switch (code.argcode)
							{
								case Opcode.CM0_SPLIT:
									reg[code.r1] = Utils.split(reg[code.r2]);
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
								case Opcode.CM0_ITEMS:
									reg[code.r1] = Utils.items(reg[code.r2]);
									break;
								case Opcode.CM0_ISOFORMAT:
									reg[code.r1] = Utils.isoformat(reg[code.r2]);
									break;
							}
							break;
						case Opcode.OC_CALLMETH1:
							switch (code.argcode)
							{
								case Opcode.CM1_SPLIT:
									reg[code.r1] = Utils.split(reg[code.r2], reg[code.r3]);
									break;
/*								case Opcode.CM1_RSPLIT:
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
									reg[code.r1] = Utils.items(reg[code.r2], reg[code.r3]);
									break;*/
								case Opcode.CM1_FORMAT:
									reg[code.r1] = Utils.format(reg[code.r2], reg[code.r3], defaultLocale);
									break;
							}
							break;
						case Opcode.OC_CALLMETH2:
								throw new UnknownMethodException(code.arg);
						case Opcode.OC_CALLMETH3:
								throw new UnknownMethodException(code.arg);
						case Opcode.OC_RENDER:
							subTemplateIterator = ((Template)templates.get(code.arg)).render((Map)reg[code.r1], templates);
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
						default:
							throw new RuntimeException("Unknown opcode '" + code.name + "'!");
					}
				}
				catch (Exception ex)
				{
					throw new LocationException(ex, code.location);
				}
				++pc;
			}
			// finished => no next chunk available
			nextChunk = null;
		}
	}

	public static List tokenizeTags(String source, String startdelim, String enddelim)
	{
		Pattern tagPattern = Pattern.compile(escapeREchars(startdelim) + "(print|code|for|if|elif|else|end|break|continue|render)(\\s*((.|\\n)*?)\\s*)?" + escapeREchars(enddelim));
		LinkedList tags = new LinkedList();
		Matcher matcher = tagPattern.matcher(source);
		int pos = 0;

		int start;
		int end;
		while (matcher.find())
		{
			start = matcher.start();
			end = start + matcher.group().length();
			if (pos != start)
				tags.add(new Location(source, null, pos, start, pos, start));
			int codestart = matcher.start(3);
			int codeend = codestart + matcher.group(3).length();
			tags.add(new Location(source, matcher.group(1), start, end, codestart, codeend));
			pos = end;
		}
		end = source.length();
		if (pos != end)
			tags.add(new Location(source, null, pos, end, pos, end));
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
						tokens.add(new None(pos, pos+len));
					else if (name.equals("True"))
						tokens.add(new True(pos, pos+len));
					else if (name.equals("False"))
						tokens.add(new False(pos, pos+len));
					else
						tokens.add(new Name(pos, pos+len, name));
				}
				else if (stringMode==0 && dateMatcher.lookingAt())
				{
					len = dateMatcher.end();
					tokens.add(new com.livinglogic.ul4.Date(pos, pos+len, Utils.isoDateFormatter.parse(dateMatcher.group())));
				}
				else if (stringMode==0 && floatMatcher.lookingAt())
				{
					len = floatMatcher.end();
					tokens.add(new Float(pos, pos+len, Double.parseDouble(floatMatcher.group())));
				}
				else if (stringMode==0 && hexintMatcher.lookingAt())
				{
					len = hexintMatcher.end();
					tokens.add(new Int(pos, pos+len, Integer.parseInt(hexintMatcher.group().substring(2), 16)));
				}
				else if (stringMode==0 && octintMatcher.lookingAt())
				{
					len = octintMatcher.end();
					tokens.add(new Int(pos, pos+len, Integer.parseInt(octintMatcher.group().substring(2), 8)));
				}
				else if (stringMode==0 && binintMatcher.lookingAt())
				{
					len = binintMatcher.end();
					tokens.add(new Int(pos, pos+len, Integer.parseInt(binintMatcher.group().substring(2), 2)));
				}
				else if (stringMode==0 && intMatcher.lookingAt())
				{
					len = intMatcher.end();
					tokens.add(new Int(pos, pos+len, Integer.parseInt(intMatcher.group())));
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
					tokens.add(new Str(stringStartPos, pos+len, collectString.toString()));
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
}