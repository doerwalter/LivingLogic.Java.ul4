package com.livinglogic.sxtl;

public class Opcode
{
	public static final int OC_TEXT = 0;
	public static final int OC_LOADNONE = 1;
	public static final int OC_LOADFALSE = 2;
	public static final int OC_LOADTRUE = 3;
	public static final int OC_LOADINT = 4;
	public static final int OC_LOADFLOAT = 5;
	public static final int OC_LOADSTR = 6;
	public static final int OC_LOADVAR = 7;
	public static final int OC_STOREVAR = 8;
	public static final int OC_ADDVAR = 9;
	public static final int OC_SUBVAR = 10;
	public static final int OC_MULVAR = 11;
	public static final int OC_TRUEDIVVAR = 12;
	public static final int OC_FLOORDIVVAR = 13;
	public static final int OC_MODVAR = 14;
	public static final int OC_DELVAR = 15;
	public static final int OC_GETATTR = 16;
	public static final int OC_GETITEM = 17;
	public static final int OC_GETSLICE12 = 18;
	public static final int OC_GETSLICE1 = 19;
	public static final int OC_GETSLICE2 = 20;
	public static final int OC_GETSLICE = 21;
	public static final int OC_PRINT = 22;
	public static final int OC_FOR = 23;
	public static final int OC_ENDFOR = 24;
	public static final int OC_NOT = 25;
	public static final int OC_NEG = 26;
	public static final int OC_CONTAINS = 27;
	public static final int OC_NOTCONTAINS = 28;
	public static final int OC_EQUALS = 29;
	public static final int OC_NOTEQUALS = 30;
	public static final int OC_ADD = 31;
	public static final int OC_SUB = 32;
	public static final int OC_MUL = 33;
	public static final int OC_FLOORDIV = 34;
	public static final int OC_TRUEDIV = 35;
	public static final int OC_AND = 36;
	public static final int OC_OR = 37;
	public static final int OC_MOD = 38;
	public static final int OC_CALLFUNC0 = 39;
	public static final int OC_CALLFUNC1 = 40;
	public static final int OC_CALLFUNC2 = 41;
	public static final int OC_CALLFUNC3 = 42;
	public static final int OC_CALLMETH0 = 43;
	public static final int OC_CALLMETH1 = 44;
	public static final int OC_CALLMETH2 = 45;
	public static final int OC_CALLMETH3 = 46;
	public static final int OC_IF = 47;
	public static final int OC_ELSE = 48;
	public static final int OC_ENDIF = 49;
	public static final int OC_RENDER = 50;

	public int name;
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
		int type;

		if (name == null)
			type = OC_TEXT;
		else if (name.equals("loadnone"))
			type = OC_LOADNONE;
		else if (name.equals("loadfalse"))
			type = OC_LOADFALSE;
		else if (name.equals("loadtrue"))
			type = OC_LOADTRUE;
		else if (name.equals("loadint"))
			type = OC_LOADINT;
		else if (name.equals("loadfloat"))
			type = OC_LOADFLOAT;
		else if (name.equals("loadstr"))
			type = OC_LOADSTR;
		else if (name.equals("loadvar"))
			type = OC_LOADVAR;
		else if (name.equals("storevar"))
			type = OC_STOREVAR;
		else if (name.equals("addvar"))
			type = OC_ADDVAR;
		else if (name.equals("subvar"))
			type = OC_SUBVAR;
		else if (name.equals("mulvar"))
			type = OC_MULVAR;
		else if (name.equals("truedivvar"))
			type = OC_TRUEDIVVAR;
		else if (name.equals("floordivvar"))
			type = OC_FLOORDIVVAR;
		else if (name.equals("modvar"))
			type = OC_MODVAR;
		else if (name.equals("delvar"))
			type = OC_DELVAR;
		else if (name.equals("getattr"))
			type = OC_GETATTR;
		else if (name.equals("getitem"))
			type = OC_GETITEM;
		else if (name.equals("getslice12"))
			type = OC_GETSLICE12;
		else if (name.equals("getslice1"))
			type = OC_GETSLICE1;
		else if (name.equals("getslice2"))
			type = OC_GETSLICE2;
		else if (name.equals("getslice"))
			type = OC_GETSLICE;
		else if (name.equals("print"))
			type = OC_PRINT;
		else if (name.equals("for"))
			type = OC_FOR;
		else if (name.equals("endfor"))
			type = OC_ENDFOR;
		else if (name.equals("not"))
			type = OC_NOT;
		else if (name.equals("neg"))
			type = OC_NEG;
		else if (name.equals("contains"))
			type = OC_CONTAINS;
		else if (name.equals("notcontains"))
			type = OC_NOTCONTAINS;
		else if (name.equals("equals"))
			type = OC_EQUALS;
		else if (name.equals("notequals"))
			type = OC_NOTEQUALS;
		else if (name.equals("add"))
			type = OC_ADD;
		else if (name.equals("sub"))
			type = OC_SUB;
		else if (name.equals("mul"))
			type = OC_MUL;
		else if (name.equals("floordiv"))
			type = OC_FLOORDIV;
		else if (name.equals("truediv"))
			type = OC_TRUEDIV;
		else if (name.equals("and"))
			type = OC_AND;
		else if (name.equals("or"))
			type = OC_OR;
		else if (name.equals("mod"))
			type = OC_MOD;
		else if (name.equals("callfunc0"))
			type = OC_CALLFUNC0;
		else if (name.equals("callfunc1"))
			type = OC_CALLFUNC1;
		else if (name.equals("callfunc2"))
			type = OC_CALLFUNC2;
		else if (name.equals("callfunc3"))
			type = OC_CALLFUNC3;
		else if (name.equals("callmeth0"))
			type = OC_CALLMETH0;
		else if (name.equals("callmeth1"))
			type = OC_CALLMETH1;
		else if (name.equals("callmeth2"))
			type = OC_CALLMETH2;
		else if (name.equals("callmeth3"))
			type = OC_CALLMETH3;
		else if (name.equals("if"))
			type = OC_IF;
		else if (name.equals("else"))
			type = OC_ELSE;
		else if (name.equals("endif"))
			type = OC_ENDIF;
		else if (name.equals("render"))
			type = OC_RENDER;
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

	public Opcode(int name, int r1, int r2, int r3, int r4, int r5, String arg, Location location)
	{
		this.name = name;
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
