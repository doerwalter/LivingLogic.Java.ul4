package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;


public class Main
{
	public static void main(String[] args)
	{
		Template tmpl = Compiler.compile("<?print isfloat(float(42))?>");
		long start = System.currentTimeMillis();
		Map vars = new HashMap();
		vars.put("t", "123");
		String output = tmpl.renders(vars);
		System.out.println("rendered " + (System.currentTimeMillis()-start));
		System.out.println(output);
	}
}
