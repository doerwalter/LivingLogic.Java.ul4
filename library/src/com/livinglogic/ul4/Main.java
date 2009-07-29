package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;


public class Main
{
	public static void main(String[] args)
	{
		Long c = new Long(42);
		Template tmpl = Compiler.compile("<?print repr({'a': 1, 'b': 2})?><?print rgb(0,0.2,0.5)?>");
		System.out.println(tmpl);
		long start = System.currentTimeMillis();
		Map vars = new HashMap<String, Object>();
		vars.put("t", "123");
		String output = tmpl.renders(vars);
		System.out.println("rendered " + (System.currentTimeMillis()-start));
		System.out.println(output);
	}
}
