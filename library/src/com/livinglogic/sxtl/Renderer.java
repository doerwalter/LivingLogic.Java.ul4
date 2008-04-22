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
	public String iteratorName;
	public int pc;
	public Iterator<Object> iterator;
	
	public IteratorStackEntry(String iteratorName, int pc, Iterator<Object> iterator)
	{
		this.iteratorName = iteratorName;
		this.pc = pc;
		this.iterator = iterator;
	}
}

public class Renderer
{
	protected static class Opcode
	{
		protected String name;
		protected int r1;
		protected int r2;
		protected int r3;
		protected int r4;
		protected String arg;
		protected int jump;
		
		public Opcode(String name)
		{
			this(name, null);
		}
		
		public Opcode(String name, String arg)
		{
			this(name, -1, arg);
		}
		
		public Opcode(String name, int r1)
		{
			this(name, r1, null);
		}
		
		public Opcode(String name, int r1, String arg)
		{
			this(name, r1, -1, arg);
		}
		
		public Opcode(String name, int r1, int r2)
		{
			this(name, r1, r2, null);
		}
		
		public Opcode(String name, int r1, int r2, String arg)
		{
			this(name, r1, r2, -1, arg);
		}
		
		public Opcode(String name, int r1, int r2, int r3)
		{
			this(name, r1, r2, r3, -1, null);
		}
		
		public Opcode(String name, int r1, int r2, int r3, String arg)
		{
			this(name, r1, r2, r3, -1, arg);
		}

		public Opcode(String name, int r1, int r2, int r3, int r4)
		{
			this(name, r1, r2, r3, r4, null);
		}

		public Opcode(String name, int r1, int r2, int r3, int r4, String arg)
		{
			this.name = name;
			this.r1 = r1;
			this.r2 = r2;
			this.r3 = r3;
			this.r4 = r4;
			this.arg = arg;
			this.jump = -1;
		}
	}

	private List<Opcode> codes;
	private int pc = 0;

	public Renderer(List<Opcode> codes)
	{
		this.codes = codes;
		annotate();
	}

	private void annotate()
	{
		LinkedList<Integer> stack = new LinkedList<Integer>();
		for (int i = 0; i < codes.size(); ++i)
		{
			Opcode code = codes.get(i);
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
			Opcode code = codes.get(pc);

			if (code.name.equals("text"))
			{
				output.append(code.arg);
			}
			else if (code.name.equals("loadstr"))
			{
				reg[code.r1] = code.arg;
			}
			else if (code.name.equals("loadint"))
			{
				reg[code.r1] = Integer.parseInt(code.arg);
			}
			else if (code.name.equals("loadnone"))
			{
				reg[code.r1] = null;
			}
			else if (code.name.equals("loadtrue"))
			{
				reg[code.r1] = Boolean.TRUE;
			}
			else if (code.name.equals("loadfalse"))
			{
				reg[code.r1] = Boolean.FALSE;
			}
			else if (code.name.equals("loadvar"))
			{
				// FIXME Exception
				// raise SXTLUnknownVariableError(self, text)
				reg[code.r1] = variables.get(code.arg);
			}
			else if (code.name.equals("storevar"))
			{
				//raise SXTLVariableExistsError(self, text)
				variables.put(code.arg, reg[code.r1]);
			}
			else if (code.name.equals("addvar"))
			{
				//raise SXTLVariableExistsError(self, text)
				int varInt = ((Integer)variables.get(code.arg)).intValue();
				int regInt = ((Integer)reg[code.r1]).intValue();
				variables.put(code.arg, new Integer(varInt + regInt));
			}
			else if (code.name.equals("delvar"))
			{
				//raise SXTLUnknownVariableError(self, text)
				variables.remove(code.arg);
			}
			else if (code.name.equals("getattr"))
			{
				//raise SXTLAttributeError(self, reg1, text)
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
			else if (code.name.equals("printtext"))
			{
				output.append(String.valueOf(reg[code.r1]).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;"));
			}
			else if (code.name.equals("printattr"))
			{
				output.append(String.valueOf(reg[code.r1]).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
			}
			else if (code.name.equals("printliteral"))
			{
				output.append(reg[code.r1]);
			}
			else if (code.name.equals("delvar"))
			{
				variables.remove(code.arg);
			}
			else if (code.name.equals("for"))
			{
				Iterator<Object> iterator = null;
				if (reg[code.r1] instanceof List)
				{
					iterator = ((List)reg[code.r1]).iterator();
				}
				else
				{
					iterator = ((Map)reg[code.r1]).keySet().iterator();
				}
				if (iterator.hasNext())
				{
					variables.put(code.arg, iterator.next());
					iterators.add(new IteratorStackEntry(code.arg, pc, iterator));
				}
				else
				{
					pc = code.jump+1;
					continue;
				}
			}
			else if (code.name.equals("foritems"))
			{
				Iterator<Object> iterator = new MapItemIterator((Map)reg[code.r1]);
				if (iterator.hasNext())
				{
					variables.put(code.arg, iterator.next());
					iterators.add(new IteratorStackEntry(code.arg, pc, iterator));
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
					variables.put(entry.iteratorName, entry.iterator.next());
					pc = entry.pc;
				}
				else
				{
					variables.remove(entry.iteratorName);
					iterators.removeLast();
				}
			}
			else if (code.name.equals("contains"))
			{
				reg[code.r1] = Utils.contains(reg[code.r2], reg[code.r3]) ? Boolean.TRUE : Boolean.FALSE;
			}
			else if (code.name.equals("notcontains"))
			{
				reg[code.r1] = Utils.contains(reg[code.r2], reg[code.r3]) ? Boolean.FALSE : Boolean.TRUE;
			}
			else if (code.name.equals("equals"))
			{
				reg[code.r1] = Utils.equals(reg[code.r2], reg[code.r3]) ? Boolean.TRUE : Boolean.FALSE;
			}
			else if (code.name.equals("notequals"))
			{
				reg[code.r1] = Utils.equals(reg[code.r2], reg[code.r3]) ? Boolean.FALSE : Boolean.TRUE;
			}
			else if (code.name.equals("not"))
			{
				reg[code.r1] = Utils.getBool(reg[code.r2]) ? Boolean.FALSE : Boolean.TRUE;
			}
			else if (code.name.equals("or"))
			{
				reg[code.r1] = (Utils.getBool(reg[code.r2]) || Utils.getBool(reg[code.r3])) ? Boolean.TRUE : Boolean.FALSE;
			}
			else if (code.name.equals("and"))
			{
				reg[code.r1] = (Utils.getBool(reg[code.r2]) && Utils.getBool(reg[code.r3])) ? Boolean.TRUE : Boolean.FALSE;
			}
			else if (code.name.equals("mod"))
			{
				int reg2Int = ((Integer)reg[code.r2]).intValue();
				int reg3Int = ((Integer)reg[code.r3]).intValue();
				reg[code.r1] = new Integer(reg2Int % reg3Int);
			}
			else if (code.name.equals("callfunc0"))
			{
				throw new RuntimeException("No function '" + code.arg + "' defined!");
			}
			else if (code.name.equals("callfunc1"))
			{
				if (code.arg.equals("str"))
				{
					reg[code.r1] = String.valueOf(reg[code.r2]);
				}
				else if (code.arg.equals("int"))
				{
					reg[code.r1] = Integer.valueOf(String.valueOf(reg[code.r2]));
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
				else
				{
					throw new RuntimeException("No function '" + code.arg + "' defined!");
				}
			}
			else if (code.name.equals("callfunc2"))
			{
				throw new RuntimeException("No function '" + code.arg + "' defined!");
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
		LinkedList<Opcode> codes = new LinkedList<Opcode>();
		String name;
		int r1;
		int r2;
		int r3;
		int r4;
		String arg;
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@10.10.10.44:1521:DEV3", "ricci", "ll");
		try
		{
			PreparedStatement stmt = con.prepareStatement("select lc_code, lc_r1, lc_r2, lc_r3, lc_r4, lc_arg from layoutcode where lay_id=? order by lc_order");
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
						arg = rs.getString("lc_arg");
						codes.add(new Opcode(name, r1, r2, r3, r4, arg));
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
