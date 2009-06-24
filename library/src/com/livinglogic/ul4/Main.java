package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;


public class Main
{
	public static void main(String[] args)
	{
		Long c = new Long(42);
		Template tmpl = Compiler.compile("<?def x?><?def y?><?print arg?><?end?><?render y(arg=arg)?><?end def?><?render x(arg='gurk')?>");
		System.out.println(tmpl);
		long start = System.currentTimeMillis();
		Map vars = new HashMap();
		vars.put("t", "123");
		String output = tmpl.renders(vars);
		System.out.println("rendered " + (System.currentTimeMillis()-start));
		System.out.println(output);
	}
}
