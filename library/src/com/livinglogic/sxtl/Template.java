package com.livinglogic.sxtl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;

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
		public String type;
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

	public static final String SXTL_HEADER = "sxtl";

	public static final String SXTL_VERSION = "1";
	
	public String source;

	public List opcodes;

	public Template()
	{
		this.source = null;
		this.opcodes = new LinkedList();
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
}
