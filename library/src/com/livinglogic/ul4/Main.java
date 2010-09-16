package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.math.BigInteger;


public class Main
{
	public static void main(String[] args)
	{
		Template tmpl = Compiler.compile("<?for i in range(20)?><?print randrange(100, 200, 10)?>\n<?end for?>");
		System.out.println(tmpl);

		Map vars = new HashMap<String, Object>();
		vars.put("t", new Date());
		long start = System.currentTimeMillis();
		String output = tmpl.renders(vars);
		System.out.println("rendered " + (System.currentTimeMillis()-start));
		System.out.println(output);
	}
}
