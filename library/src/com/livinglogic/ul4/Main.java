package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;


public class Main
{
	public static void main(String[] args)
	{
		Template tmpl1 = Compiler.compile("<?print 2*x?>");
		Template tmpl2 = Compiler.compile("<?print 2*t.render(x=42)?>");
		long start = System.currentTimeMillis();
		Map vars = new HashMap();
		vars.put("t", tmpl1);
		String output = tmpl2.renders(vars);
		System.out.println("rendered " + (System.currentTimeMillis()-start));
		System.out.println(output);
	}
}
