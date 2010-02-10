package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;


public class Main
{
	public static void main(String[] args)
	{
		Template tmpl = Compiler.compile("<?print json(now())?>, <?print json(#fff)?>");
		System.out.println(tmpl);

		Map vars = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		String output = tmpl.renders(vars);
		System.out.println("rendered " + (System.currentTimeMillis()-start));
		System.out.println(output);
	}
}
