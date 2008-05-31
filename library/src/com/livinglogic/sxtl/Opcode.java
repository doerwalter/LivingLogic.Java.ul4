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
		if (name == null)
			this.name = Type.TEXT;
		else if (name.equals("loadnone"))
			this.name = Type.LOADNONE;
		else if (name.equals("loadfalse"))
			this.name = Type.LOADFALSE;
		else if (name.equals("loadtrue"))
			this.name = Type.LOADTRUE;
		else if (name.equals("loadint"))
			this.name = Type.LOADINT;
		else if (name.equals("loadfloat"))
			this.name = Type.LOADFLOAT;
		else if (name.equals("loadstr"))
			this.name = Type.LOADSTR;
		else if (name.equals("loadvar"))
			this.name = Type.LOADVAR;
		else if (name.equals("storevar"))
			this.name = Type.STOREVAR;
		else if (name.equals("addvar"))
			this.name = Type.ADDVAR;
		else if (name.equals("subvar"))
			this.name = Type.SUBVAR;
		else if (name.equals("mulvar"))
			this.name = Type.MULVAR;
		else if (name.equals("truedivvar"))
			this.name = Type.TRUEDIVVAR;
		else if (name.equals("floordivvar"))
			this.name = Type.FLOORDIVVAR;
		else if (name.equals("modvar"))
			this.name = Type.MODVAR;
		else if (name.equals("delvar"))
			this.name = Type.DELVAR;
		else if (name.equals("getattr"))
			this.name = Type.GETATTR;
		else if (name.equals("getitem"))
			this.name = Type.GETITEM;
		else if (name.equals("getslice12"))
			this.name = Type.GETSLICE12;
		else if (name.equals("getslice1"))
			this.name = Type.GETSLICE1;
		else if (name.equals("getslice2"))
			this.name = Type.GETSLICE2;
		else if (name.equals("getslice"))
			this.name = Type.GETSLICE;
		else if (name.equals("print"))
			this.name = Type.PRINT;
		else if (name.equals("for"))
			this.name = Type.FOR;
		else if (name.equals("endfor"))
			this.name = Type.ENDFOR;
		else if (name.equals("not"))
			this.name = Type.NOT;
		else if (name.equals("neg"))
			this.name = Type.NEG;
		else if (name.equals("contains"))
			this.name = Type.CONTAINS;
		else if (name.equals("notcontains"))
			this.name = Type.NOTCONTAINS;
		else if (name.equals("equals"))
			this.name = Type.EQUALS;
		else if (name.equals("notequals"))
			this.name = Type.NOTEQUALS;
		else if (name.equals("add"))
			this.name = Type.ADD;
		else if (name.equals("sub"))
			this.name = Type.SUB;
		else if (name.equals("mul"))
			this.name = Type.MUL;
		else if (name.equals("floordiv"))
			this.name = Type.FLOORDIV;
		else if (name.equals("truediv"))
			this.name = Type.TRUEDIV;
		else if (name.equals("and"))
			this.name = Type.AND;
		else if (name.equals("or"))
			this.name = Type.OR;
		else if (name.equals("mod"))
			this.name = Type.MOD;
		else if (name.equals("callfunc0"))
			this.name = Type.CALLFUNC0;
		else if (name.equals("callfunc1"))
			this.name = Type.CALLFUNC1;
		else if (name.equals("callfunc2"))
			this.name = Type.CALLFUNC2;
		else if (name.equals("callfunc3"))
			this.name = Type.CALLFUNC3;
		else if (name.equals("callmeth0"))
			this.name = Type.CALLMETH0;
		else if (name.equals("callmeth1"))
			this.name = Type.CALLMETH1;
		else if (name.equals("callmeth2"))
			this.name = Type.CALLMETH2;
		else if (name.equals("callmeth3"))
			this.name = Type.CALLMETH3;
		else if (name.equals("if"))
			this.name = Type.IF;
		else if (name.equals("else"))
			this.name = Type.ELSE;
		else if (name.equals("endif"))
			this.name = Type.ENDIF;
		else if (name.equals("render"))
			this.name = Type.RENDER;
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

