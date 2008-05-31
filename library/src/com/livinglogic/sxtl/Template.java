package com.livinglogic.sxtl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

enum OpcodeCode
{
	TEXT,
	LOADNONE,
	LOADFALSE,
	LOADTRUE,
	LOADINT,
	LOADFLOAT,
	LOADSTR,
	LOADVAR,
	STOREVAR,
	ADDVAR,
	SUBVAR,
	MULVAR,
	TRUEDIVVAR,
	FLOORDIVVAR,
	MODVAR,
	DELVAR,
	GETATTR,
	GETITEM,
	GETSLICE12,
	GETSLICE1,
	GETSLICE2,
	GETSLICE,
	PRINT,
	FOR,
	ENDFOR,
	NOT,
	NEG,
	CONTAINS,
	NOTCONTAINS,
	EQUALS,
	NOTEQUALS,
	ADD,
	SUB,
	MUL,
	FLOORDIV,
	TRUEDIV,
	AND,
	OR,
	MOD,
	CALLFUNC0,
	CALLFUNC1,
	CALLFUNC2,
	CALLFUNC3,
	CALLMETH0,
	CALLMETH1,
	CALLMETH2,
	CALLMETH3,
	IF,
	ELSE,
	ENDIF,
	RENDER
};

public class Template
{
	public static class Location
	{
		public String source;
		protected String type;
		public int starttag;
		public int endtag;
		public int startcode;
		public int endcode;

		public Location(String source, String type, int starttag, int endtag, int startcode, int endcode)
		{
			this.source = source;
			this.type = type;
			this.starttag = starttag;
			this.endtag = endtag;
			this.startcode = startcode;
			this.endcode = endcode;
		}

		public String getType()
		{
			return type;
		}
		public String getTag()
		{
			return source.substring(starttag, endtag);
		}

		public String getCode()
		{
			return source.substring(startcode, endcode);
		}
	}

	public static class Opcode
	{
		public OpcodeCode name;
		public int r1;
		public int r2;
		public int r3;
		public int r4;
		public int r5;
		public String arg;
		public Location location;
		public int jump;
		
		public Opcode(String name, int r1, int r2, int r3, int r4, int r5, String arg, Location location)
		{
			if (name == null)
				this.name = OpcodeCode.TEXT;
			else if (name.equals("loadnone"))
				this.name = OpcodeCode.LOADNONE;
			else if (name.equals("loadfalse"))
				this.name = OpcodeCode.LOADFALSE;
			else if (name.equals("loadtrue"))
				this.name = OpcodeCode.LOADTRUE;
			else if (name.equals("loadint"))
				this.name = OpcodeCode.LOADINT;
			else if (name.equals("loadfloat"))
				this.name = OpcodeCode.LOADFLOAT;
			else if (name.equals("loadstr"))
				this.name = OpcodeCode.LOADSTR;
			else if (name.equals("loadvar"))
				this.name = OpcodeCode.LOADVAR;
			else if (name.equals("storevar"))
				this.name = OpcodeCode.STOREVAR;
			else if (name.equals("addvar"))
				this.name = OpcodeCode.ADDVAR;
			else if (name.equals("subvar"))
				this.name = OpcodeCode.SUBVAR;
			else if (name.equals("mulvar"))
				this.name = OpcodeCode.MULVAR;
			else if (name.equals("truedivvar"))
				this.name = OpcodeCode.TRUEDIVVAR;
			else if (name.equals("floordivvar"))
				this.name = OpcodeCode.FLOORDIVVAR;
			else if (name.equals("modvar"))
				this.name = OpcodeCode.MODVAR;
			else if (name.equals("delvar"))
				this.name = OpcodeCode.DELVAR;
			else if (name.equals("getattr"))
				this.name = OpcodeCode.GETATTR;
			else if (name.equals("getitem"))
				this.name = OpcodeCode.GETITEM;
			else if (name.equals("getslice12"))
				this.name = OpcodeCode.GETSLICE12;
			else if (name.equals("getslice1"))
				this.name = OpcodeCode.GETSLICE1;
			else if (name.equals("getslice2"))
				this.name = OpcodeCode.GETSLICE2;
			else if (name.equals("getslice"))
				this.name = OpcodeCode.GETSLICE;
			else if (name.equals("print"))
				this.name = OpcodeCode.PRINT;
			else if (name.equals("for"))
				this.name = OpcodeCode.FOR;
			else if (name.equals("endfor"))
				this.name = OpcodeCode.ENDFOR;
			else if (name.equals("not"))
				this.name = OpcodeCode.NOT;
			else if (name.equals("neg"))
				this.name = OpcodeCode.NEG;
			else if (name.equals("contains"))
				this.name = OpcodeCode.CONTAINS;
			else if (name.equals("notcontains"))
				this.name = OpcodeCode.NOTCONTAINS;
			else if (name.equals("equals"))
				this.name = OpcodeCode.EQUALS;
			else if (name.equals("notequals"))
				this.name = OpcodeCode.NOTEQUALS;
			else if (name.equals("add"))
				this.name = OpcodeCode.ADD;
			else if (name.equals("sub"))
				this.name = OpcodeCode.SUB;
			else if (name.equals("mul"))
				this.name = OpcodeCode.MUL;
			else if (name.equals("floordiv"))
				this.name = OpcodeCode.FLOORDIV;
			else if (name.equals("truediv"))
				this.name = OpcodeCode.TRUEDIV;
			else if (name.equals("and"))
				this.name = OpcodeCode.AND;
			else if (name.equals("or"))
				this.name = OpcodeCode.OR;
			else if (name.equals("mod"))
				this.name = OpcodeCode.MOD;
			else if (name.equals("callfunc0"))
				this.name = OpcodeCode.CALLFUNC0;
			else if (name.equals("callfunc1"))
				this.name = OpcodeCode.CALLFUNC1;
			else if (name.equals("callfunc2"))
				this.name = OpcodeCode.CALLFUNC2;
			else if (name.equals("callfunc3"))
				this.name = OpcodeCode.CALLFUNC3;
			else if (name.equals("callmeth0"))
				this.name = OpcodeCode.CALLMETH0;
			else if (name.equals("callmeth1"))
				this.name = OpcodeCode.CALLMETH1;
			else if (name.equals("callmeth2"))
				this.name = OpcodeCode.CALLMETH2;
			else if (name.equals("callmeth3"))
				this.name = OpcodeCode.CALLMETH3;
			else if (name.equals("if"))
				this.name = OpcodeCode.IF;
			else if (name.equals("else"))
				this.name = OpcodeCode.ELSE;
			else if (name.equals("endif"))
				this.name = OpcodeCode.ENDIF;
			else if (name.equals("render"))
				this.name = OpcodeCode.RENDER;
			else
				throw new IllegalArgumentException("Opcode " + name + " unknown!"); 
			this.r1 = r1;
			this.r2 = r2;
			this.r3 = r3;
			this.r4 = r4;
			this.r5 = r5;
			this.arg = arg;
			this.location = location;
			this.jump = -1;
		}
	}

	class IteratorStackEntry
	{
		public int iteratorRegSpec;
		public int pc;
		public Iterator iterator;
	
		public IteratorStackEntry(int iteratorRegSpec, int pc, Iterator iterator)
		{
			this.iteratorRegSpec = iteratorRegSpec;
			this.pc = pc;
			this.iterator = iterator;
		}
	}

	public static final String SXTL_HEADER = "sxtl";

	public static final String SXTL_VERSION = "1";
	
	public String source;

	public List opcodes;

	private boolean annotated = false;

	public Template()
	{
		this.source = null;
		this.opcodes = new LinkedList();
	}

	public void opcode(String name, Location location)
	{
		opcodes.add(new Opcode(name, -1, -1, -1, -1, -1, null, location));
	}

	public void opcode(String name, String arg, Location location)
	{
		opcodes.add(new Opcode(name, -1, -1, -1, -1, -1, arg, location));
	}

	public void opcode(String name, int r1, Location location)
	{
		opcodes.add(new Opcode(name, r1, -1, -1, -1, -1, null, location));
	}

	public void opcode(String name, int r1, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, -1, -1, -1, -1, arg, location));
	}

	public void opcode(String name, int r1, int r2, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, -1, -1, -1, null, location));
	}

	public void opcode(String name, int r1, int r2, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, -1, -1, -1, arg, location));
	}

	public void opcode(String name, int r1, int r2, int r3, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, -1, -1, null, location));
	}

	public void opcode(String name, int r1, int r2, int r3, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, -1, -1, arg, location));
	}

	public void opcode(String name, int r1, int r2, int r3, int r4, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, -1, null, location));
	}

	public void opcode(String name, int r1, int r2, int r3, int r4, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, -1, arg, location));
	}

	public void opcode(String name, int r1, int r2, int r3, int r4, int r5, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, r5, null, location));
	}

	public void opcode(String name, int r1, int r2, int r3, int r4, int r5, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, r5, arg, location));
	}

	protected static int readintInternal(Reader reader, char terminator) throws IOException
	{
		int retVal = 0;
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
			}
			else if (charValue == terminator)
			{
				terminatorFound = true;
			}
			else if (Character.toLowerCase(charValue) == terminator)
			{
				terminatorFound = true;
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

	protected static int readint(Reader reader, char terminator) throws IOException
	{
		int retVal = readintInternal(reader, terminator);
		if (0 > retVal)
		{
			throw new RuntimeException("Invalid integer read!");
		}
		return retVal;
	}

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

	public static Template frombin(Reader reader) throws IOException
	{
		Template retVal = new Template();
		BufferedReader bufferedReader = new BufferedReader(reader);
		String header = bufferedReader.readLine();
		if (!SXTL_HEADER.equals(header))
		{
			throw new RuntimeException("Invalid header, expected " + SXTL_HEADER + ", got " + header);
		}
		String version = bufferedReader.readLine();
		if (!SXTL_VERSION.equals(version))
		{
			throw new RuntimeException("Invalid version, expected " + SXTL_VERSION + ", got " + version);
		}
		retVal.source = readstr(bufferedReader, 's');
		readcr(bufferedReader);
		int count = readint(bufferedReader, '#');
		readcr(bufferedReader);
		for (int i = 0; i < count; i++)
		{
			int r1 = readspec(bufferedReader);
			int r2 = readspec(bufferedReader);
			int r3 = readspec(bufferedReader);
			int r4 = readspec(bufferedReader);
			int r5 = readspec(bufferedReader);
			String code = readstr(bufferedReader, 'c');
			String arg = readstr(bufferedReader, 'a');
			Location location = null;
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
					location = new Location(retVal.source, readstr(bufferedReader, 't'),
						readint(bufferedReader, '<'), readint(bufferedReader, '>'),
						readint(bufferedReader, '['), readint(bufferedReader, ']'));
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
		}
		return retVal;
	}

	protected void annotate()
	{
		if (!annotated)
		{
			LinkedList stack = new LinkedList();
			for (int i = 0; i < opcodes.size(); ++i)
			{
				Template.Opcode opcode = (Template.Opcode)opcodes.get(i);
				switch (opcode.name)
				{
					case IF:
						stack.add(i);
						break;
					case ELSE:
						((Template.Opcode)opcodes.get(((Integer)stack.getLast()).intValue())).jump = i;
						stack.set(stack.size()-1, i);
						break;
					case ENDIF:
						((Template.Opcode)opcodes.get(((Integer)stack.getLast()).intValue())).jump = i;
						stack.removeLast();
						break;
					case FOR:
						stack.add(i);
						break;
					case ENDFOR:
						((Template.Opcode)opcodes.get(((Integer)stack.getLast()).intValue())).jump = i;
						stack.removeLast();
						break;
				}
			}
			annotated = true;
		}
	}

	public Iterator render(Object data)
	{
		return new Renderer(data, null);
	}

	public Iterator render(Object data, Map templates)
	{
		return new Renderer(data, templates);
	}

	public String renders(Object data)
	{
		return renders(data, null);
	}

	public String renders(Object data, Map templates)
	{
		StringBuilder output = new StringBuilder();

		for (Iterator iterator = render(data, templates); iterator.hasNext();)
		{
			output.append((String)iterator.next());
		}
		return output.toString();
	}

	class Renderer implements Iterator
	{
		private int pc = 0;
		private Object[] reg = new Object[10];
		private HashMap variables = new HashMap();
		private Map templates;
		private LinkedList iterators = new LinkedList();
		private Iterator subTemplateIterator = null;

		private String nextChunk = null;

		public Renderer(Object data, Map templates)
		{
			annotate();
			variables.put("data", data);
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

		public String next()
		{
			String result = nextChunk;
			getNextChunk();
			return result;
		}

		public void getNextChunk()
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
				Template.Opcode code = (Template.Opcode)opcodes.get(pc);

				switch (code.name)
				{
					case TEXT:
						nextChunk = code.location.getCode();
						++pc;
						return;
					case PRINT:
						nextChunk = Utils.toString(reg[code.r1]);
						++pc;
						return;
					case LOADNONE:
						reg[code.r1] = null;
						break;
					case LOADFALSE:
						reg[code.r1] = Boolean.FALSE;
						break;
					case LOADTRUE:
						reg[code.r1] = Boolean.TRUE;
						break;
					case LOADSTR:
						reg[code.r1] = code.arg;
						break;
					case LOADINT:
						reg[code.r1] = Integer.parseInt(code.arg);
						break;
					case LOADFLOAT:
						reg[code.r1] = Double.parseDouble(code.arg);
						break;
					case LOADVAR:
						reg[code.r1] = variables.get(code.arg);
						break;
					case STOREVAR:
						variables.put(code.arg, reg[code.r1]);
						break;
					case ADDVAR:
						variables.put(code.arg, Utils.add(variables.get(code.arg), reg[code.r1]));
						break;
					case SUBVAR:
						variables.put(code.arg, Utils.sub(variables.get(code.arg), reg[code.r1]));
						break;
					case MULVAR:
						variables.put(code.arg, Utils.mul(variables.get(code.arg), reg[code.r1]));
						break;
					case TRUEDIVVAR:
						variables.put(code.arg, Utils.truediv(variables.get(code.arg), reg[code.r1]));
						break;
					case FLOORDIVVAR:
						variables.put(code.arg, Utils.floordiv(variables.get(code.arg), reg[code.r1]));
						break;
					case MODVAR:
						variables.put(code.arg, Utils.mod(variables.get(code.arg), reg[code.r1]));
						break;
					case DELVAR:
						variables.remove(code.arg);
						break;
					case FOR:
						Iterator iterator = Utils.iterator(reg[code.r2]);
						if (iterator.hasNext())
						{
							reg[code.r1] = iterator.next();
							iterators.add(new IteratorStackEntry(code.r1, pc, iterator));
						}
						else
						{
							pc = code.jump+1;
							continue;
						}
						break;
					case ENDFOR:
						IteratorStackEntry entry = (IteratorStackEntry)iterators.getLast();
						if (entry.iterator.hasNext())
						{
							reg[entry.iteratorRegSpec] = entry.iterator.next();
							pc = entry.pc;
						}
						else
						{
							iterators.removeLast();
						}
						break;
					case IF:
						if (!Utils.getBool(reg[code.r1]))
						{
							pc = code.jump+1;
							continue;
						}
						break;
					case ELSE:
						pc = code.jump+1;
						continue;
					case ENDIF:
						//Skip to next opcode
						break;
					case GETATTR:
						reg[code.r1] = ((Map)reg[code.r2]).get(code.arg);
						break;
					case GETITEM:
						reg[code.r1] = Utils.getItem(reg[code.r2], reg[code.r3]);
						break;
					case GETSLICE12:
						reg[code.r1] = Utils.getSlice(reg[code.r2], reg[code.r3], reg[code.r4]);
						break;
					case GETSLICE1:
						reg[code.r1] = Utils.getSlice(reg[code.r2], reg[code.r3], null);
						break;
					case GETSLICE2:
						reg[code.r1] = Utils.getSlice(reg[code.r2], null, reg[code.r4]);
						break;
					case GETSLICE:
						reg[code.r1] = Utils.getSlice(reg[code.r2], null, null);
						break;
					case NOT:
						reg[code.r1] = Utils.getBool(reg[code.r2]) ? Boolean.FALSE : Boolean.TRUE;
						break;
					case NEG:
						reg[code.r1] = Utils.neg(reg[code.r2]);
						break;
					case EQUALS:
						reg[code.r1] = Utils.equals(reg[code.r2], reg[code.r3]) ? Boolean.TRUE : Boolean.FALSE;
						break;
					case NOTEQUALS:
						reg[code.r1] = Utils.equals(reg[code.r2], reg[code.r3]) ? Boolean.FALSE : Boolean.TRUE;
						break;
					case CONTAINS:
						reg[code.r1] = Utils.contains(reg[code.r2], reg[code.r3]) ? Boolean.TRUE : Boolean.FALSE;
						break;
					case NOTCONTAINS:
						reg[code.r1] = Utils.contains(reg[code.r2], reg[code.r3]) ? Boolean.FALSE : Boolean.TRUE;
						break;
					case OR:
						reg[code.r1] = (Utils.getBool(reg[code.r2]) || Utils.getBool(reg[code.r3])) ? Boolean.TRUE : Boolean.FALSE;
						break;
					case AND:
						reg[code.r1] = (Utils.getBool(reg[code.r2]) && Utils.getBool(reg[code.r3])) ? Boolean.TRUE : Boolean.FALSE;
						break;
					case ADD:
						reg[code.r1] = Utils.add(reg[code.r2], reg[code.r3]);
						break;
					case SUB:
						reg[code.r1] = Utils.sub(reg[code.r2], reg[code.r3]);
						break;
					case MUL:
						reg[code.r1] = Utils.mul(reg[code.r2], reg[code.r3]);
						break;
					case TRUEDIV:
						reg[code.r1] = Utils.truediv(reg[code.r2], reg[code.r3]);
						break;
					case FLOORDIV:
						reg[code.r1] = Utils.floordiv(reg[code.r2], reg[code.r3]);
						break;
					case MOD:
						reg[code.r1] = Utils.mod(reg[code.r2], reg[code.r3]);
						break;
					case CALLFUNC0:
						throw new RuntimeException("No function '" + code.arg + "' defined!");
					case CALLFUNC1:
						if (code.arg.equals("xmlescape"))
						{
							reg[code.r1] = Utils.xmlescape(reg[code.r2]);
						}
						else if (code.arg.equals("str"))
						{
							reg[code.r1] = Utils.toString(reg[code.r2]);
						}
						else if (code.arg.equals("int"))
						{
							reg[code.r1] = Utils.toInteger(reg[code.r2]);
						}
						else if (code.arg.equals("len"))
						{
							reg[code.r1] = Utils.length(reg[code.r2]);
						}
						else if (code.arg.equals("enumerate"))
						{
							reg[code.r1] = Utils.enumerate(reg[code.r2]);
						}
						else if (code.arg.equals("isnone"))
						{
							reg[code.r1] = (null == reg[code.r2]) ? Boolean.TRUE : Boolean.FALSE;
						}
						else if (code.arg.equals("isstr"))
						{
							reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof String)) ? Boolean.TRUE : Boolean.FALSE;
						}
						else if (code.arg.equals("isint"))
						{
							reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof Integer)) ? Boolean.TRUE : Boolean.FALSE;
						}
						else if (code.arg.equals("isfloat"))
						{
							reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof Double)) ? Boolean.TRUE : Boolean.FALSE;
						}
						else if (code.arg.equals("isbool"))
						{
							reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof Boolean)) ? Boolean.TRUE : Boolean.FALSE;
						}
						else if (code.arg.equals("islist"))
						{
							reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof List)) ? Boolean.TRUE : Boolean.FALSE;
						}
						else if (code.arg.equals("isdict"))
						{
							reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof Map)) ? Boolean.TRUE : Boolean.FALSE;
						}
						else if (code.arg.equals("chr"))
						{
							reg[code.r1] = Utils.chr(reg[code.r2]);
						}
						else if (code.arg.equals("ord"))
						{
							reg[code.r1] = Utils.ord(reg[code.r2]);
						}
						else if (code.arg.equals("hex"))
						{
							reg[code.r1] = Utils.hex(reg[code.r2]);
						}
						else if (code.arg.equals("oct"))
						{
							reg[code.r1] = Utils.oct(reg[code.r2]);
						}
						else if (code.arg.equals("bin"))
						{
							reg[code.r1] = Utils.bin(reg[code.r2]);
						}
						else if (code.arg.equals("sorted"))
						{
							reg[code.r1] = Utils.sorted(reg[code.r2]);
						}
						else if (code.arg.equals("range"))
						{
							reg[code.r1] = Utils.range(reg[code.r2]);
						}
						else
						{
							throw new RuntimeException("No function '" + code.arg + "' defined!");
						}
						break;
					case CALLFUNC2:
						if (code.arg.equals("range"))
						{
							reg[code.r1] = Utils.range(reg[code.r2], reg[code.r3]);
						}
						else
						{
							throw new RuntimeException("No function '" + code.arg + "' defined!");
						}
						break;
					case CALLFUNC3:
						if (code.arg.equals("range"))
						{
							reg[code.r1] = Utils.range(reg[code.r2], reg[code.r3], reg[code.r4]);
						}
						else
						{
							throw new RuntimeException("No function '" + code.arg + "' defined!");
						}
						break;
					case CALLMETH0:
						if (code.arg.equals("split") || code.arg.equals("rsplit"))
						{
							reg[code.r1] = Utils.split(reg[code.r2]);
						}
						else if (code.arg.equals("strip"))
						{
							reg[code.r1] = Utils.strip(reg[code.r2]);
						}
						else if (code.arg.equals("lstrip"))
						{
							reg[code.r1] = Utils.lstrip(reg[code.r2]);
						}
						else if (code.arg.equals("rstrip"))
						{
							reg[code.r1] = Utils.rstrip(reg[code.r2]);
						}
						else if (code.arg.equals("upper"))
						{
							reg[code.r1] = Utils.upper(reg[code.r2]);
						}
						else if (code.arg.equals("lower"))
						{
							reg[code.r1] = Utils.lower(reg[code.r2]);
						}
						else if (code.arg.equals("items"))
						{
							reg[code.r1] = Utils.items(reg[code.r2]);
						}
						else
						{
							throw new RuntimeException("No method '" + code.arg + "' defined!");
						}
						break;
					case CALLMETH1:
						if (code.arg.equals("upper"))
						{
							reg[code.r1] = ((String)reg[code.r2]).toUpperCase();
						}
						else if (code.arg.equals("lower"))
						{
							reg[code.r1] = ((String)reg[code.r2]).toLowerCase();
						}
						else
						{
							throw new RuntimeException("No method '" + code.arg + "' defined!");
						}
						break;
					case RENDER:
						subTemplateIterator = ((Template)templates.get(code.arg)).render(reg[code.r1], templates);
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
				++pc;
			}
			// finished => no next chunk available
			nextChunk = null;
		}
	}
}
