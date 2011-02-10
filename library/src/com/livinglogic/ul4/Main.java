package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.math.BigInteger;


public class Main
{
	public static void main(String[] args)
	{
		InterpretedTemplate tmpl = Compiler.compile("<?print 'a<=><=>b<=>c'.rsplit('<=>', 10)?>");
		System.out.println(tmpl);

		Map vars = new HashMap<String, Object>();
		vars.put("t", tmpl);
		long start = System.nanoTime();
		String output = tmpl.renders(vars);
		System.out.println("rendered " + ((System.nanoTime()-start)/1000) + "us");
		System.out.println("output " + output);
		System.out.println(new JavaSource4Template(tmpl));
	}
}
