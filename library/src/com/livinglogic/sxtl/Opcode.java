package com.livinglogic.sxtl;

public class Opcode
{
	public enum Type
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

	public Type name;
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
		Type type;

		if (name == null)
			type = Type.TEXT;
		else if (name.equals("loadnone"))
			type = Type.LOADNONE;
		else if (name.equals("loadfalse"))
			type = Type.LOADFALSE;
		else if (name.equals("loadtrue"))
			type = Type.LOADTRUE;
		else if (name.equals("loadint"))
			type = Type.LOADINT;
		else if (name.equals("loadfloat"))
			type = Type.LOADFLOAT;
		else if (name.equals("loadstr"))
			type = Type.LOADSTR;
		else if (name.equals("loadvar"))
			type = Type.LOADVAR;
		else if (name.equals("storevar"))
			type = Type.STOREVAR;
		else if (name.equals("addvar"))
			type = Type.ADDVAR;
		else if (name.equals("subvar"))
			type = Type.SUBVAR;
		else if (name.equals("mulvar"))
			type = Type.MULVAR;
		else if (name.equals("truedivvar"))
			type = Type.TRUEDIVVAR;
		else if (name.equals("floordivvar"))
			type = Type.FLOORDIVVAR;
		else if (name.equals("modvar"))
			type = Type.MODVAR;
		else if (name.equals("delvar"))
			type = Type.DELVAR;
		else if (name.equals("getattr"))
			type = Type.GETATTR;
		else if (name.equals("getitem"))
			type = Type.GETITEM;
		else if (name.equals("getslice12"))
			type = Type.GETSLICE12;
		else if (name.equals("getslice1"))
			type = Type.GETSLICE1;
		else if (name.equals("getslice2"))
			type = Type.GETSLICE2;
		else if (name.equals("getslice"))
			type = Type.GETSLICE;
		else if (name.equals("print"))
			type = Type.PRINT;
		else if (name.equals("for"))
			type = Type.FOR;
		else if (name.equals("endfor"))
			type = Type.ENDFOR;
		else if (name.equals("not"))
			type = Type.NOT;
		else if (name.equals("neg"))
			type = Type.NEG;
		else if (name.equals("contains"))
			type = Type.CONTAINS;
		else if (name.equals("notcontains"))
			type = Type.NOTCONTAINS;
		else if (name.equals("equals"))
			type = Type.EQUALS;
		else if (name.equals("notequals"))
			type = Type.NOTEQUALS;
		else if (name.equals("add"))
			type = Type.ADD;
		else if (name.equals("sub"))
			type = Type.SUB;
		else if (name.equals("mul"))
			type = Type.MUL;
		else if (name.equals("floordiv"))
			type = Type.FLOORDIV;
		else if (name.equals("truediv"))
			type = Type.TRUEDIV;
		else if (name.equals("and"))
			type = Type.AND;
		else if (name.equals("or"))
			type = Type.OR;
		else if (name.equals("mod"))
			type = Type.MOD;
		else if (name.equals("callfunc0"))
			type = Type.CALLFUNC0;
		else if (name.equals("callfunc1"))
			type = Type.CALLFUNC1;
		else if (name.equals("callfunc2"))
			type = Type.CALLFUNC2;
		else if (name.equals("callfunc3"))
			type = Type.CALLFUNC3;
		else if (name.equals("callmeth0"))
			type = Type.CALLMETH0;
		else if (name.equals("callmeth1"))
			type = Type.CALLMETH1;
		else if (name.equals("callmeth2"))
			type = Type.CALLMETH2;
		else if (name.equals("callmeth3"))
			type = Type.CALLMETH3;
		else if (name.equals("if"))
			type = Type.IF;
		else if (name.equals("else"))
			type = Type.ELSE;
		else if (name.equals("endif"))
			type = Type.ENDIF;
		else if (name.equals("render"))
			type = Type.RENDER;
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

	public Opcode(Type name, int r1, int r2, int r3, int r4, int r5, String arg, Location location)
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
