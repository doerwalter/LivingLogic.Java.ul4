package com.livinglogic.sxtl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Vector;

class IteratorStackEntry
{
	public int iteratorRegSpec;
	public int pc;
	public Iterator<Object> iterator;
	
	public IteratorStackEntry(int iteratorRegSpec, int pc, Iterator<Object> iterator)
	{
		this.iteratorRegSpec = iteratorRegSpec;
		this.pc = pc;
		this.iterator = iterator;
	}
}

public class Renderer
{
	private List<Template.Opcode> codes;
	private int pc = 0;

	public Renderer(List<Template.Opcode> codes)
	{
		this.codes = codes;
		annotate();
	}

	private void annotate()
	{
		LinkedList<Integer> stack = new LinkedList<Integer>();
		for (int i = 0; i < codes.size(); ++i)
		{
			Template.Opcode code = codes.get(i);
			if (code.name.equals("if"))
			{
				stack.add(i);
			}
			else if (code.name.equals("else"))
			{
				codes.get(stack.getLast()).jump = i;
				stack.set(stack.size()-1, i);
			}
			else if (code.name.equals("endif"))
			{
				codes.get(stack.getLast()).jump = i;
				stack.removeLast();
			}
			else if (code.name.equals("for"))
			{
				stack.add(i);
			}
			else if (code.name.equals("endfor"))
			{
				codes.get(stack.getLast()).jump = i;
				stack.removeLast();
			}
		}
	}

	public String render(HashMap<String, Object> variables)
	{
		LinkedList<IteratorStackEntry> iterators = new LinkedList<IteratorStackEntry>();
		
		Object[] reg = new Object[10];
		
		StringBuilder output = new StringBuilder();
		while (pc < codes.size())
		{
			Template.Opcode code = codes.get(pc);

			if (null == code.name)
			{
				output.append(code.arg);
			}
			else if (code.name.equals("print"))
			{
				output.append(reg[code.r1]);
			}
			else if (code.name.equals("loadnone"))
			{
				reg[code.r1] = null;
			}
			else if (code.name.equals("loadfalse"))
			{
				reg[code.r1] = Boolean.FALSE;
			}
			else if (code.name.equals("loadtrue"))
			{
				reg[code.r1] = Boolean.TRUE;
			}
			else if (code.name.equals("loadstr"))
			{
				reg[code.r1] = code.arg;
			}
			else if (code.name.equals("loadint"))
			{
				reg[code.r1] = Integer.parseInt(code.arg);
			}
			else if (code.name.equals("loadfloat"))
			{
				reg[code.r1] = Double.parseDouble(code.arg);
			}
			else if (code.name.equals("loadvar"))
			{
				reg[code.r1] = variables.get(code.arg);
			}
			else if (code.name.equals("storevar"))
			{
				variables.put(code.arg, reg[code.r1]);
			}
			else if (code.name.equals("addvar"))
			{
				variables.put(code.arg, Utils.add(variables.get(code.arg), reg[code.r1]));
			}
			else if (code.name.equals("subvar"))
			{
				variables.put(code.arg, Utils.sub(variables.get(code.arg), reg[code.r1]));
			}
			else if (code.name.equals("mulvar"))
			{
				variables.put(code.arg, Utils.mul(variables.get(code.arg), reg[code.r1]));
			}
			else if (code.name.equals("truedivvar"))
			{
				variables.put(code.arg, Utils.truediv(variables.get(code.arg), reg[code.r1]));
			}
			else if (code.name.equals("floordivvar"))
			{
				variables.put(code.arg, Utils.floordiv(variables.get(code.arg), reg[code.r1]));
			}
			else if (code.name.equals("modvar"))
			{
				variables.put(code.arg, Utils.mod(variables.get(code.arg), reg[code.r1]));
			}
			else if (code.name.equals("delvar"))
			{
				variables.remove(code.arg);
			}
			else if (code.name.equals("for"))
			{
				Iterator<Object> iterator = Utils.iterator(reg[code.r2]);
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
			}
			else if (code.name.equals("endfor"))
			{
				IteratorStackEntry entry = iterators.getLast();
				if (entry.iterator.hasNext())
				{
					reg[entry.iteratorRegSpec] = entry.iterator.next();
					pc = entry.pc;
				}
				else
				{
					iterators.removeLast();
				}
			}
			else if (code.name.equals("if"))
			{
				if (!Utils.getBool(reg[code.r1]))
				{
					pc = code.jump+1;
					continue;
				}
			}
			else if (code.name.equals("else"))
			{
				pc = code.jump+1;
				continue;
			}
			else if (code.name.equals("endif"))
			{
				//Skip to next opcode
			}
			else if (code.name.equals("getattr"))
			{
				reg[code.r1] = ((Map)reg[code.r2]).get(code.arg);
			}
			else if (code.name.equals("getitem"))
			{
				reg[code.r1] = Utils.getItem(reg[code.r2], reg[code.r3]);
			}
			else if (code.name.equals("getslice12"))
			{
				reg[code.r1] = Utils.getSlice(reg[code.r2], reg[code.r3], reg[code.r4]);
			}
			else if (code.name.equals("getslice1"))
			{
				reg[code.r1] = Utils.getSlice(reg[code.r2], reg[code.r3], null);
			}
			else if (code.name.equals("getslice2"))
			{
				reg[code.r1] = Utils.getSlice(reg[code.r2], null, reg[code.r4]);
			}
			else if (code.name.equals("getslice"))
			{
				reg[code.r1] = Utils.getSlice(reg[code.r2], null, null);
			}
			else if (code.name.equals("not"))
			{
				reg[code.r1] = Utils.getBool(reg[code.r2]) ? Boolean.FALSE : Boolean.TRUE;
			}
			else if (code.name.equals("equals"))
			{
				reg[code.r1] = Utils.equals(reg[code.r2], reg[code.r3]) ? Boolean.TRUE : Boolean.FALSE;
			}
			else if (code.name.equals("notequals"))
			{
				reg[code.r1] = Utils.equals(reg[code.r2], reg[code.r3]) ? Boolean.FALSE : Boolean.TRUE;
			}
			else if (code.name.equals("contains"))
			{
				reg[code.r1] = Utils.contains(reg[code.r2], reg[code.r3]) ? Boolean.TRUE : Boolean.FALSE;
			}
			else if (code.name.equals("notcontains"))
			{
				reg[code.r1] = Utils.contains(reg[code.r2], reg[code.r3]) ? Boolean.FALSE : Boolean.TRUE;
			}
			else if (code.name.equals("or"))
			{
				reg[code.r1] = (Utils.getBool(reg[code.r2]) || Utils.getBool(reg[code.r3])) ? Boolean.TRUE : Boolean.FALSE;
			}
			else if (code.name.equals("and"))
			{
				reg[code.r1] = (Utils.getBool(reg[code.r2]) && Utils.getBool(reg[code.r3])) ? Boolean.TRUE : Boolean.FALSE;
			}
			else if (code.name.equals("add"))
			{
				reg[code.r1] = Utils.add(reg[code.r2], reg[code.r3]);
			}
			else if (code.name.equals("sub"))
			{
				reg[code.r1] = Utils.sub(reg[code.r2], reg[code.r3]);
			}
			else if (code.name.equals("mul"))
			{
				reg[code.r1] = Utils.mul(reg[code.r2], reg[code.r3]);
			}
			else if (code.name.equals("truediv"))
			{
				reg[code.r1] = Utils.truediv(reg[code.r2], reg[code.r3]);
			}
			else if (code.name.equals("floordiv"))
			{
				reg[code.r1] = Utils.floordiv(reg[code.r2], reg[code.r3]);
			}
			else if (code.name.equals("mod"))
			{
				reg[code.r1] = Utils.mod(reg[code.r2], reg[code.r3]);
			}
			else if (code.name.equals("callfunc0"))
			{
				throw new RuntimeException("No function '" + code.arg + "' defined!");
			}
			else if (code.name.equals("callfunc1"))
			{
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
				else
				{
					throw new RuntimeException("No function '" + code.arg + "' defined!");
				}
			}
			else if (code.name.equals("callfunc2"))
			{
				throw new RuntimeException("No function '" + code.arg + "' defined!");
			}
			else if (code.name.equals("callmeth0"))
			{
				if (code.arg.equals("split") || code.arg.equals("rsplit"))
				{
					reg[code.r1] = Utils.split(reg[code.r2]);
				}
				/*
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
				*/
				else if (code.arg.equals("items"))
				{
					reg[code.r1] = Utils.items(reg[code.r2]);
				}
				else
				{
					throw new RuntimeException("No method '" + code.arg + "' defined!");
				}
			}
			else
			{
				throw new RuntimeException("Unknown opcode '" + code.name + "'!");
			}
			++pc;
		}
		return output.toString();
	}

	public static void main(String[] args) throws Exception
	{
		int layoutId = Integer.parseInt(args[0]);
		LinkedList<Template.Opcode> codes = new LinkedList<Template.Opcode>();
		String name;
		int r1;
		int r2;
		int r3;
		int r4;
		int r5;
		String arg;
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.44:1521:DEV3", "ricci", "ll");
		try
		{
			PreparedStatement stmt = con.prepareStatement("select lc_code, lc_r1, lc_r2, lc_r3, lc_r4, lc_r5, lc_arg from layoutcode where lay_id=? order by lc_order");
			try
			{
				stmt.setInt(1, layoutId);
				ResultSet rs = stmt.executeQuery();
				try
				{
					while (rs.next())
					{
						name = rs.getString("lc_code");
						r1 = rs.getInt("lc_r1");
						if (rs.wasNull())
						{
							r1 = -1;
						}
						r2 = rs.getInt("lc_r2");
						if (rs.wasNull())
						{
							r2 = -1;
						}
						r3 = rs.getInt("lc_r3");
						if (rs.wasNull())
						{
							r3 = -1;
						}
						r4 = rs.getInt("lc_r4");
						if (rs.wasNull())
						{
							r4 = -1;
						}
						r5 = rs.getInt("lc_r5");
						if (rs.wasNull())
						{
							r5 = -1;
						}
						arg = rs.getString("lc_arg");
						codes.add(new Template.Opcode(name, r1, r2, r3, r4, r5, arg, null));
					}
				}
				finally
				{
					rs.close();
				}
			}
			finally
			{
				stmt.close();
			}
		}
		finally
		{
			con.close();
		}
		HashMap<String, Object> variables = new HashMap<String, Object>();
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put("grp_name", "gurk");
		fields.put("grp_identifier", "hurz");
		variables.put("fields", fields);
		HashMap<String, List> fielderrors = new HashMap<String, List>();
		List grpnameerrors = new LinkedList();
		grpnameerrors.add("Alles Mist");
		fielderrors.put("grp_name", grpnameerrors);
		variables.put("fielderrors", fielderrors);
		HashMap<String, String> globalerrors = new HashMap<String, String>();
		variables.put("globalerrors", globalerrors);
		HashMap<String, String> lookups = new HashMap<String, String>();
		variables.put("lookups", lookups);
		System.out.println(new Renderer(codes).render(variables));
	}
}
