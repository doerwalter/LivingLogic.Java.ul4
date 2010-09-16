package com.livinglogic.ul4;

public class Opcode
{
	public static final int OC_TEXT = 0;
	public static final int OC_LOADNONE = 1;
	public static final int OC_LOADFALSE = 2;
	public static final int OC_LOADTRUE = 3;
	public static final int OC_LOADINT = 4;
	public static final int OC_LOADFLOAT = 5;
	public static final int OC_LOADSTR = 6;
	public static final int OC_LOADDATE = 7;
	public static final int OC_LOADCOLOR = 8;
	public static final int OC_BUILDLIST = 9;
	public static final int OC_BUILDDICT = 10;
	public static final int OC_ADDLIST = 11;
	public static final int OC_ADDDICT = 12;
	public static final int OC_UPDATEDICT = 13;
	public static final int OC_LOADVAR = 14;
	public static final int OC_STOREVAR = 15;
	public static final int OC_ADDVAR = 16;
	public static final int OC_SUBVAR = 17;
	public static final int OC_MULVAR = 18;
	public static final int OC_TRUEDIVVAR = 19;
	public static final int OC_FLOORDIVVAR = 20;
	public static final int OC_MODVAR = 21;
	public static final int OC_DELVAR = 22;
	public static final int OC_GETATTR = 23;
	public static final int OC_GETITEM = 24;
	public static final int OC_GETSLICE12 = 25;
	public static final int OC_GETSLICE1 = 26;
	public static final int OC_GETSLICE2 = 27;
	public static final int OC_PRINT = 28;
	public static final int OC_PRINTX = 29;
	public static final int OC_NOT = 30;
	public static final int OC_NEG = 31;
	public static final int OC_CONTAINS = 32;
	public static final int OC_NOTCONTAINS = 33;
	public static final int OC_EQ = 34;
	public static final int OC_NE = 35;
	public static final int OC_LT = 36;
	public static final int OC_LE = 37;
	public static final int OC_GT = 38;
	public static final int OC_GE = 39;
	public static final int OC_ADD = 40;
	public static final int OC_SUB = 41;
	public static final int OC_MUL = 42;
	public static final int OC_FLOORDIV = 43;
	public static final int OC_TRUEDIV = 44;
	public static final int OC_AND = 45;
	public static final int OC_OR = 46;
	public static final int OC_MOD = 47;
	public static final int OC_CALLFUNC0 = 48;
	public static final int OC_CALLFUNC1 = 49;
	public static final int OC_CALLFUNC2 = 50;
	public static final int OC_CALLFUNC3 = 51;
	public static final int OC_CALLFUNC4 = 52;
	public static final int OC_CALLMETH0 = 53;
	public static final int OC_CALLMETH1 = 54;
	public static final int OC_CALLMETH2 = 55;
	public static final int OC_CALLMETH3 = 56;
	public static final int OC_CALLMETHKW = 57;
	public static final int OC_IF = 58;
	public static final int OC_ELSE = 59;
	public static final int OC_ENDIF = 60;
	public static final int OC_FOR = 61;
	public static final int OC_ENDFOR = 62;
	public static final int OC_BREAK = 63;
	public static final int OC_CONTINUE = 64;
	public static final int OC_RENDER = 65;
	public static final int OC_DEF = 66;
	public static final int OC_ENDDEF = 67;

	public static final int CF0_NOW = 0;
	public static final int CF0_UTCNOW = 1;
	public static final int CF0_VARS = 2;
	public static final int CF0_RANDOM = 3;

	public static final int CF1_XMLESCAPE = 0;
	public static final int CF1_STR = 1;
	public static final int CF1_REPR = 2;
	public static final int CF1_INT = 3;
	public static final int CF1_FLOAT = 4;
	public static final int CF1_BOOL = 5;
	public static final int CF1_LEN = 6;
	public static final int CF1_ENUMERATE = 7;
	public static final int CF1_ISNONE = 8;
	public static final int CF1_ISSTR = 9;
	public static final int CF1_ISINT = 10;
	public static final int CF1_ISFLOAT = 11;
	public static final int CF1_ISBOOL = 12;
	public static final int CF1_ISDATE = 13;
	public static final int CF1_ISLIST = 14;
	public static final int CF1_ISDICT = 15;
	public static final int CF1_ISTEMPLATE = 16;
	public static final int CF1_ISCOLOR = 17;
	public static final int CF1_CHR = 18;
	public static final int CF1_ORD = 19;
	public static final int CF1_HEX = 20;
	public static final int CF1_OCT = 21;
	public static final int CF1_BIN = 22;
	public static final int CF1_ABS = 23;
	public static final int CF1_SORTED = 24;
	public static final int CF1_RANGE = 25;
	public static final int CF1_TYPE = 26;
	public static final int CF1_CSV = 27;
	public static final int CF1_GET = 28;
	public static final int CF1_JSON = 29;
	public static final int CF1_REVERSED = 30;
	public static final int CF1_RANDRANGE = 31;

	public static final int CF2_RANGE = 0;
	public static final int CF2_GET = 1;
	public static final int CF2_ZIP = 2;
	public static final int CF2_INT = 3;
	public static final int CF2_RANDRANGE = 4;

	public static final int CF3_RANGE = 0;
	public static final int CF3_ZIP = 1;
	public static final int CF3_RGB = 2;
	public static final int CF3_HLS = 3;
	public static final int CF3_HSV = 4;
	public static final int CF3_RANDRANGE = 5;

	public static final int CF4_RGB = 0;
	public static final int CF4_HLS = 1;
	public static final int CF4_HSV = 2;

	public static final int CM0_SPLIT = 0;
	public static final int CM0_STRIP = 1;
	public static final int CM0_LSTRIP = 2;
	public static final int CM0_RSTRIP = 3;
	public static final int CM0_LOWER = 4;
	public static final int CM0_UPPER = 5;
	public static final int CM0_CAPITALIZE = 6;
	public static final int CM0_ITEMS = 7;
	public static final int CM0_ISOFORMAT = 8;
	public static final int CM0_MIMEFORMAT = 9;
	public static final int CM0_HLS = 10;
	public static final int CM0_HLSA = 11;
	public static final int CM0_HSV = 12;
	public static final int CM0_HSVA = 13;
	public static final int CM0_LUM = 14;
	public static final int CM0_DAY = 15;
	public static final int CM0_MONTH = 16;
	public static final int CM0_YEAR = 17;
	public static final int CM0_HOUR = 18;
	public static final int CM0_MINUTE = 19;
	public static final int CM0_SECOND = 20;
	public static final int CM0_MICROSECOND = 21;
	public static final int CM0_WEEKDAY = 22;
	public static final int CM0_YEARDAY = 23;

	public static final int CM1_SPLIT = 0;
	public static final int CM1_RSPLIT = 1;
	public static final int CM1_STRIP = 2;
	public static final int CM1_LSTRIP = 3;
	public static final int CM1_RSTRIP = 4;
	public static final int CM1_STARTSWITH = 5;
	public static final int CM1_ENDSWITH = 6;
	public static final int CM1_FIND = 7;
	public static final int CM1_RFIND = 8;
	public static final int CM1_FORMAT = 9;
	public static final int CM1_GET = 10;
	public static final int CM1_WITHLUM = 11;
	public static final int CM1_WITHA = 12;
	public static final int CM1_JOIN = 13;

	public static final int CM2_SPLIT = 0;
	public static final int CM2_RSPLIT = 1;
	public static final int CM2_FIND = 2;
	public static final int CM2_REPLACE = 3;
	public static final int CM2_GET = 4;

	public static final int CM3_FIND = 0;

	public static final int CMKW_RENDER = 0;

	public int name;
	public int r1;
	public int r2;
	public int r3;
	public int r4;
	public int r5;
	public String arg;
	public int argcode;
	public Location location;
	public int jump;

	public static int name2code(String name)
	{
		if (name == null)
			return OC_TEXT;
		else if (name.equals("loadnone"))
			return OC_LOADNONE;
		else if (name.equals("loadfalse"))
			return OC_LOADFALSE;
		else if (name.equals("loadtrue"))
			return OC_LOADTRUE;
		else if (name.equals("loadint"))
			return OC_LOADINT;
		else if (name.equals("loadfloat"))
			return OC_LOADFLOAT;
		else if (name.equals("loadstr"))
			return OC_LOADSTR;
		else if (name.equals("loaddate"))
			return OC_LOADDATE;
		else if (name.equals("buildlist"))
			return OC_BUILDLIST;
		else if (name.equals("builddict"))
			return OC_BUILDDICT;
		else if (name.equals("addlist"))
			return OC_ADDLIST;
		else if (name.equals("adddict"))
			return OC_ADDDICT;
		else if (name.equals("updatedict"))
			return OC_UPDATEDICT;
		else if (name.equals("loadvar"))
			return OC_LOADVAR;
		else if (name.equals("storevar"))
			return OC_STOREVAR;
		else if (name.equals("addvar"))
			return OC_ADDVAR;
		else if (name.equals("subvar"))
			return OC_SUBVAR;
		else if (name.equals("mulvar"))
			return OC_MULVAR;
		else if (name.equals("truedivvar"))
			return OC_TRUEDIVVAR;
		else if (name.equals("floordivvar"))
			return OC_FLOORDIVVAR;
		else if (name.equals("modvar"))
			return OC_MODVAR;
		else if (name.equals("delvar"))
			return OC_DELVAR;
		else if (name.equals("getattr"))
			return OC_GETATTR;
		else if (name.equals("getitem"))
			return OC_GETITEM;
		else if (name.equals("getslice12"))
			return OC_GETSLICE12;
		else if (name.equals("getslice1"))
			return OC_GETSLICE1;
		else if (name.equals("getslice2"))
			return OC_GETSLICE2;
		else if (name.equals("print"))
			return OC_PRINT;
		else if (name.equals("printx"))
			return OC_PRINTX;
		else if (name.equals("not"))
			return OC_NOT;
		else if (name.equals("neg"))
			return OC_NEG;
		else if (name.equals("contains"))
			return OC_CONTAINS;
		else if (name.equals("notcontains"))
			return OC_NOTCONTAINS;
		else if (name.equals("eq"))
			return OC_EQ;
		else if (name.equals("ne"))
			return OC_NE;
		else if (name.equals("lt"))
			return OC_LT;
		else if (name.equals("le"))
			return OC_LE;
		else if (name.equals("gt"))
			return OC_GT;
		else if (name.equals("ge"))
			return OC_GE;
		else if (name.equals("add"))
			return OC_ADD;
		else if (name.equals("sub"))
			return OC_SUB;
		else if (name.equals("mul"))
			return OC_MUL;
		else if (name.equals("floordiv"))
			return OC_FLOORDIV;
		else if (name.equals("truediv"))
			return OC_TRUEDIV;
		else if (name.equals("and"))
			return OC_AND;
		else if (name.equals("or"))
			return OC_OR;
		else if (name.equals("mod"))
			return OC_MOD;
		else if (name.equals("callfunc0"))
			return OC_CALLFUNC0;
		else if (name.equals("callfunc1"))
			return OC_CALLFUNC1;
		else if (name.equals("callfunc2"))
			return OC_CALLFUNC2;
		else if (name.equals("callfunc3"))
			return OC_CALLFUNC3;
		else if (name.equals("callfunc4"))
			return OC_CALLFUNC4;
		else if (name.equals("callmeth0"))
			return OC_CALLMETH0;
		else if (name.equals("callmeth1"))
			return OC_CALLMETH1;
		else if (name.equals("callmeth2"))
			return OC_CALLMETH2;
		else if (name.equals("callmeth3"))
			return OC_CALLMETH3;
		else if (name.equals("callmethkw"))
			return OC_CALLMETHKW;
		else if (name.equals("if"))
			return OC_IF;
		else if (name.equals("else"))
			return OC_ELSE;
		else if (name.equals("endif"))
			return OC_ENDIF;
		else if (name.equals("for"))
			return OC_FOR;
		else if (name.equals("endfor"))
			return OC_ENDFOR;
		else if (name.equals("break"))
			return OC_BREAK;
		else if (name.equals("continue"))
			return OC_CONTINUE;
		else if (name.equals("render"))
			return OC_RENDER;
		else if (name.equals("def"))
			return OC_DEF;
		else if (name.equals("enddef"))
			return OC_ENDDEF;
		else
			throw new IllegalArgumentException("Opcode name " + name + " unknown!");
	}

	public static int callfunc0name2code(String name)
	{
		if (name.equals("now"))
			return CF0_NOW;
		else if (name.equals("utcnow"))
			return CF0_UTCNOW;
		else if (name.equals("vars"))
			return CF0_VARS;
		else if (name.equals("random"))
			return CF0_RANDOM;
		else
			throw new UnknownFunctionException(name);
	}

	public static int callfunc1name2code(String name)
	{
		if (name.equals("xmlescape"))
			return CF1_XMLESCAPE;
		else if (name.equals("str"))
			return CF1_STR;
		else if (name.equals("repr"))
			return CF1_REPR;
		else if (name.equals("int"))
			return CF1_INT;
		else if (name.equals("float"))
			return CF1_FLOAT;
		else if (name.equals("bool"))
			return CF1_BOOL;
		else if (name.equals("len"))
			return CF1_LEN;
		else if (name.equals("enumerate"))
			return CF1_ENUMERATE;
		else if (name.equals("isnone"))
			return CF1_ISNONE;
		else if (name.equals("isstr"))
			return CF1_ISSTR;
		else if (name.equals("isint"))
			return CF1_ISINT;
		else if (name.equals("isfloat"))
			return CF1_ISFLOAT;
		else if (name.equals("isbool"))
			return CF1_ISBOOL;
		else if (name.equals("isdate"))
			return CF1_ISDATE;
		else if (name.equals("islist"))
			return CF1_ISLIST;
		else if (name.equals("isdict"))
			return CF1_ISDICT;
		else if (name.equals("istemplate"))
			return CF1_ISTEMPLATE;
		else if (name.equals("iscolor"))
			return CF1_ISCOLOR;
		else if (name.equals("chr"))
			return CF1_CHR;
		else if (name.equals("ord"))
			return CF1_ORD;
		else if (name.equals("hex"))
			return CF1_HEX;
		else if (name.equals("oct"))
			return CF1_OCT;
		else if (name.equals("bin"))
			return CF1_BIN;
		else if (name.equals("abs"))
			return CF1_ABS;
		else if (name.equals("sorted"))
			return CF1_SORTED;
		else if (name.equals("range"))
			return CF1_RANGE;
		else if (name.equals("type"))
			return CF1_TYPE;
		else if (name.equals("csv"))
			return CF1_CSV;
		else if (name.equals("get"))
			return CF1_GET;
		else if (name.equals("json"))
			return CF1_JSON;
		else if (name.equals("reversed"))
			return CF1_REVERSED;
		else if (name.equals("randrange"))
			return CF1_RANDRANGE;
		else
			throw new UnknownFunctionException(name);
	}

	public static int callfunc2name2code(String name)
	{
		if (name.equals("range"))
			return CF2_RANGE;
		else if (name.equals("get"))
			return CF2_GET;
		else if (name.equals("zip"))
			return CF2_ZIP;
		else if (name.equals("int"))
			return CF2_INT;
		else if (name.equals("randrange"))
			return CF2_RANDRANGE;
		else
			throw new UnknownFunctionException(name);
	}

	public static int callfunc3name2code(String name)
	{
		if (name.equals("range"))
			return CF3_RANGE;
		if (name.equals("zip"))
			return CF3_ZIP;
		else if (name.equals("rgb"))
			return CF3_RGB;
		else if (name.equals("hls"))
			return CF3_HLS;
		else if (name.equals("hsv"))
			return CF3_HSV;
		else if (name.equals("randrange"))
			return CF3_RANDRANGE;
		else
			throw new UnknownFunctionException(name);
	}

	public static int callfunc4name2code(String name)
	{
		if (name.equals("rgb"))
			return CF4_RGB;
		else if (name.equals("hls"))
			return CF4_HLS;
		else if (name.equals("hsv"))
			return CF4_HSV;
		else
			throw new UnknownFunctionException(name);
	}

	public static int callmeth0name2code(String name)
	{
		if (name.equals("split"))
			return CM0_SPLIT;
		else if (name.equals("rsplit"))
			return CM0_SPLIT;
		else if (name.equals("strip"))
			return CM0_STRIP;
		else if (name.equals("lstrip"))
			return CM0_LSTRIP;
		else if (name.equals("rstrip"))
			return CM0_RSTRIP;
		else if (name.equals("upper"))
			return CM0_UPPER;
		else if (name.equals("lower"))
			return CM0_LOWER;
		else if (name.equals("capitalize"))
			return CM0_CAPITALIZE;
		else if (name.equals("items"))
			return CM0_ITEMS;
		else if (name.equals("isoformat"))
			return CM0_ISOFORMAT;
		else if (name.equals("mimeformat"))
			return CM0_MIMEFORMAT;
		else if (name.equals("hls"))
			return CM0_HLS;
		else if (name.equals("hlsa"))
			return CM0_HLSA;
		else if (name.equals("hsv"))
			return CM0_HSV;
		else if (name.equals("hsva"))
			return CM0_HSVA;
		else if (name.equals("lum"))
			return CM0_LUM;
		else if (name.equals("day"))
			return CM0_DAY;
		else if (name.equals("month"))
			return CM0_MONTH;
		else if (name.equals("year"))
			return CM0_YEAR;
		else if (name.equals("hour"))
			return CM0_HOUR;
		else if (name.equals("minute"))
			return CM0_MINUTE;
		else if (name.equals("second"))
			return CM0_SECOND;
		else if (name.equals("microsecond"))
			return CM0_MICROSECOND;
		else if (name.equals("weekday"))
			return CM0_WEEKDAY;
		else if (name.equals("yearday"))
			return CM0_YEARDAY;
		else
			throw new UnknownMethodException(name);
	}

	public static int callmeth1name2code(String name)
	{
		if (name.equals("split"))
			return CM1_SPLIT;
		else if (name.equals("rsplit"))
			return CM1_RSPLIT;
		else if (name.equals("strip"))
			return CM1_STRIP;
		else if (name.equals("lstrip"))
			return CM1_LSTRIP;
		else if (name.equals("rstrip"))
			return CM1_RSTRIP;
		else if (name.equals("startswith"))
			return CM1_STARTSWITH;
		else if (name.equals("endswith"))
			return CM1_ENDSWITH;
		else if (name.equals("find"))
			return CM1_FIND;
		else if (name.equals("rfind"))
			return CM1_RFIND;
		else if (name.equals("format"))
			return CM1_FORMAT;
		else if (name.equals("get"))
			return CM1_GET;
		else if (name.equals("withlum"))
			return CM1_WITHLUM;
		else if (name.equals("witha"))
			return CM1_WITHA;
		else if (name.equals("join"))
			return CM1_JOIN;
		else
			throw new UnknownMethodException(name);
	}

	public static int callmeth2name2code(String name)
	{
		if (name.equals("replace"))
			return CM2_REPLACE;
		else if (name.equals("get"))
			return CM2_GET;
		else
			throw new UnknownMethodException(name);
	}

	public static int callmeth3name2code(String name)
	{
		throw new UnknownMethodException(name);
	}

	public static int callmethkwname2code(String name)
	{
		if (name.equals("render"))
			return CMKW_RENDER;
		else
			throw new UnknownMethodException(name);
	}

	public static String code2name(int code)
	{
		if (code == OC_TEXT)
			return null;
		else if (code == OC_LOADNONE)
			return "loadnone";
		else if (code == OC_LOADFALSE)
			return "loadfalse";
		else if (code == OC_LOADTRUE)
			return "loadtrue";
		else if (code == OC_LOADINT)
			return "loadint";
		else if (code == OC_LOADFLOAT)
			return "loadfloat";
		else if (code == OC_LOADSTR)
			return "loadstr";
		else if (code == OC_LOADDATE)
			return "loaddate";
		else if (code == OC_BUILDLIST)
			return "buildlist";
		else if (code == OC_BUILDDICT)
			return "builddict";
		else if (code == OC_ADDLIST)
			return "addlist";
		else if (code == OC_ADDDICT)
			return "adddict";
		else if (code == OC_UPDATEDICT)
			return "updatedict";
		else if (code == OC_LOADVAR)
			return "loadvar";
		else if (code == OC_STOREVAR)
			return "storevar";
		else if (code == OC_ADDVAR)
			return "addvar";
		else if (code == OC_SUBVAR)
			return "subvar";
		else if (code == OC_MULVAR)
			return "mulvar";
		else if (code == OC_TRUEDIVVAR)
			return "truedivvar";
		else if (code == OC_FLOORDIVVAR)
			return "floordivvar";
		else if (code == OC_MODVAR)
			return "modvar";
		else if (code == OC_DELVAR)
			return "delvar";
		else if (code == OC_GETATTR)
			return "getattr";
		else if (code == OC_GETITEM)
			return "getitem";
		else if (code == OC_GETSLICE12)
			return "getslice12";
		else if (code == OC_GETSLICE1)
			return "getslice1";
		else if (code == OC_GETSLICE2)
			return "getslice2";
		else if (code == OC_PRINT)
			return "print";
		else if (code == OC_PRINTX)
			return "printx";
		else if (code == OC_NOT)
			return "not";
		else if (code == OC_NEG)
			return "neg";
		else if (code == OC_EQ)
			return "eq";
		else if (code == OC_NE)
			return "ne";
		else if (code == OC_LT)
			return "lt";
		else if (code == OC_LE)
			return "le";
		else if (code == OC_GT)
			return "gt";
		else if (code == OC_GE)
			return "ge";
		else if (code == OC_CONTAINS)
			return "contains";
		else if (code == OC_NOTCONTAINS)
			return "notcontains";
		else if (code == OC_ADD)
			return "add";
		else if (code == OC_SUB)
			return "sub";
		else if (code == OC_MUL)
			return "mul";
		else if (code == OC_FLOORDIV)
			return "floordiv";
		else if (code == OC_TRUEDIV)
			return "truediv";
		else if (code == OC_AND)
			return "and";
		else if (code == OC_OR)
			return "or";
		else if (code == OC_MOD)
			return "mod";
		else if (code == OC_CALLFUNC0)
			return "callfunc0";
		else if (code == OC_CALLFUNC1)
			return "callfunc1";
		else if (code == OC_CALLFUNC2)
			return "callfunc2";
		else if (code == OC_CALLFUNC3)
			return "callfunc3";
		else if (code == OC_CALLFUNC4)
			return "callfunc4";
		else if (code == OC_CALLMETH0)
			return "callmeth0";
		else if (code == OC_CALLMETH1)
			return "callmeth1";
		else if (code == OC_CALLMETH2)
			return "callmeth2";
		else if (code == OC_CALLMETH3)
			return "callmeth3";
		else if (code == OC_CALLMETHKW)
			return "callmethkw";
		else if (code == OC_IF)
			return "if";
		else if (code == OC_ELSE)
			return "else";
		else if (code == OC_ENDIF)
			return "endif";
		else if (code == OC_FOR)
			return "for";
		else if (code == OC_ENDFOR)
			return "endfor";
		else if (code == OC_BREAK)
			return "break";
		else if (code == OC_CONTINUE)
			return "continue";
		else if (code == OC_RENDER)
			return "render";
		else if (code == OC_DEF)
			return "def";
		else if (code == OC_ENDDEF)
			return "enddef";
		else
			throw new IllegalArgumentException("Opcode code " + code + " unknown!");
	}

	public Opcode(String name, int r1, int r2, int r3, int r4, int r5, String arg, Location location)
	{
		this(name2code(name), r1, r2, r3, r4, r5, arg, location);
	}

	public Opcode(int name, int r1, int r2, int r3, int r4, int r5, String arg, Location location)
	{
		this.name = name;
		this.r1 = r1;
		this.r2 = r2;
		this.r3 = r3;
		this.r4 = r4;
		this.r5 = r5;
		this.arg = arg;
		switch (name)
		{
			case OC_CALLFUNC0:
				this.argcode = callfunc0name2code(arg);
				break;
			case OC_CALLFUNC1:
				this.argcode = callfunc1name2code(arg);
				break;
			case OC_CALLFUNC2:
				this.argcode = callfunc2name2code(arg);
				break;
			case OC_CALLFUNC3:
				this.argcode = callfunc3name2code(arg);
				break;
			case OC_CALLFUNC4:
				this.argcode = callfunc4name2code(arg);
				break;
			case OC_CALLMETH0:
				this.argcode = callmeth0name2code(arg);
				break;
			case OC_CALLMETH1:
				this.argcode = callmeth1name2code(arg);
				break;
			case OC_CALLMETH2:
				this.argcode = callmeth2name2code(arg);
				break;
			case OC_CALLMETH3:
				this.argcode = callmeth3name2code(arg);
				break;
			case OC_CALLMETHKW:
				this.argcode = callmethkwname2code(arg);
				break;
		}
		this.location = location;
		this.jump = -1;
	}

	public String toString()
	{
		switch (name)
		{
			case OC_TEXT:
				return "print " + Utils.repr(location.getCode());
			case OC_LOADNONE:
				return "r" + r1 + " = None";
			case OC_LOADFALSE:
				return "r" + r1 + " = False";
			case OC_LOADTRUE:
				return "r" + r1 + " = True";
			case OC_LOADINT:
				return "r" + r1 + " = " + arg;
			case OC_LOADFLOAT:
				return "r" + r1 + " = " + arg;
			case OC_LOADSTR:
				return "r" + r1 + " = " + Utils.repr(arg);
			case OC_LOADDATE:
				return "r" + r1 + " = " + Utils.repr(arg);
			case OC_LOADCOLOR:
				return "r" + r1 + " = " + Color.fromdump(arg).repr();
			case OC_BUILDLIST:
				return "r" + r1 + " = []";
			case OC_BUILDDICT:
				return "r" + r1 + " = {}";
			case OC_ADDLIST:
				return "r" + r1 + ".append(r" + r2 + ")";
			case OC_ADDDICT:
				return "r" + r1 + "[r" + r2 + "] = r" + r3;
			case OC_UPDATEDICT:
				return "r" + r1 + ".update(r" + r2 + ")";
			case OC_LOADVAR:
				return "r" + r1 + " = vars[" + Utils.repr(arg) + "]";
			case OC_STOREVAR:
				return "vars[" + Utils.repr(arg) + "] = r" + r1;
			case OC_ADDVAR:
				return "vars[" + Utils.repr(arg) + "] += r" + r1;
			case OC_SUBVAR:
				return "vars[" + Utils.repr(arg) + "] -= r" + r1;
			case OC_MULVAR:
				return "vars[" + Utils.repr(arg) + "] *= r" + r1;
			case OC_TRUEDIVVAR:
				return "vars[" + Utils.repr(arg) + "] /= r" + r1;
			case OC_FLOORDIVVAR:
				return "vars[" + Utils.repr(arg) + "] //= r" + r1;
			case OC_MODVAR:
				return "vars[" + Utils.repr(arg) + "] %= r" + r1;
			case OC_DELVAR:
				return "del vars[" + Utils.repr(arg) + "]";
			case OC_GETATTR:
				return "r" + r1 + " = getattr(r" + r2 + ", " + Utils.repr(arg) + ")";
			case OC_GETITEM:
				return "r" + r1 + " = r" + r2 + "[r" + r3 + "]";
			case OC_GETSLICE12:
				return "r" + r1 + " = r" + r2 + "[r" + r3 + ":r" + r4 + "]";
			case OC_GETSLICE1:
				return "r" + r1 + " = r" + r2 + "[r" + r3 + ":]";
			case OC_GETSLICE2:
				return "r" + r1 + " = r" + r2 + "[:r" + r4 + "]";
			case OC_PRINT:
				return "print r" + r1;
			case OC_PRINTX:
				return "print xmlescape(r" + r1 + ")";
			case OC_NOT:
				return "r" + r1 + " = not r" + r2;
			case OC_NEG:
				return "r" + r1 + " = -r" + r2;
			case OC_EQ:
				return "r" + r1 + " = r" + r2 + " == r" + r3;
			case OC_NE:
				return "r" + r1 + " = r" + r2 + " != r" + r3;
			case OC_LT:
				return "r" + r1 + " = r" + r2 + " < r" + r3;
			case OC_LE:
				return "r" + r1 + " = r" + r2 + " <= r" + r3;
			case OC_GT:
				return "r" + r1 + " = r" + r2 + " > r" + r3;
			case OC_GE:
				return "r" + r1 + " = r" + r2 + " >= r" + r3;
			case OC_CONTAINS:
				return "r" + r1 + " = r" + r2 + " in r" + r3;
			case OC_NOTCONTAINS:
				return "r" + r1 + " = r" + r2 + " not in r" + r3;
			case OC_ADD:
				return "r" + r1 + " = r" + r2 + " + r" + r3;
			case OC_SUB:
				return "r" + r1 + " = r" + r2 + " - r" + r3;
			case OC_MUL:
				return "r" + r1 + " = r" + r2 + " == r" + r3;
			case OC_FLOORDIV:
				return "r" + r1 + " = r" + r2 + " // r" + r3;
			case OC_TRUEDIV:
				return "r" + r1 + " = r" + r2 + " / r" + r3;
			case OC_AND:
				return "r" + r1 + " = r" + r2 + " and r" + r3;
			case OC_OR:
				return "r" + r1 + " = r" + r2 + " or r" + r3;
			case OC_MOD:
				return "r" + r1 + " = r" + r2 + " % r" + r3;
			case OC_CALLFUNC0:
				return "r" + r1 + " = " + arg + "()";
			case OC_CALLFUNC1:
				return "r" + r1 + " = " + arg + "(r" + r2 + ")";
			case OC_CALLFUNC2:
				return "r" + r1 + " = " + arg + "(r" + r2 + ", r" + r3 + ")";
			case OC_CALLFUNC3:
				return "r" + r1 + " = " + arg + "(r" + r2 + ", r" + r3 + ", r" + r4 + ")";
			case OC_CALLFUNC4:
				return "r" + r1 + " = " + arg + "(r" + r2 + ", r" + r3 + ", r" + r4 + ", r" + r5 + ")";
			case OC_CALLMETH0:
				return "r" + r1 + " = r" + r2 + "." + arg + "()";
			case OC_CALLMETH1:
				return "r" + r1 + " = r" + r2 + "." + arg + "(r" + r3 + ")";
			case OC_CALLMETH2:
				return "r" + r1 + " = r" + r2 + "." + arg + "(r" + r3 + ", r" + r4 + ")";
			case OC_CALLMETH3:
				return "r" + r1 + " = r" + r2 + "." + arg + "(r" + r3 + ", r" + r4 + ", r" + r5 + ")";
			case OC_IF:
				return "if r" + r1;
			case OC_ELSE:
				return "else";
			case OC_ENDIF:
				return "endif";
			case OC_FOR:
				return "for r" + r1 + " in r" + r2;
			case OC_ENDFOR:
				return "endfor";
			case OC_BREAK:
				return "break";
			case OC_CONTINUE:
				return "continue";
			case OC_RENDER:
				return "render r" + r1 + "(r" + r2 + ")";
			case OC_DEF:
				return "def " + arg + "(**vars)";
			case OC_ENDDEF:
				return "enddef";
			default:
				throw new IllegalArgumentException("Opcode code " + name + " unknown!");
		}
	}
}
