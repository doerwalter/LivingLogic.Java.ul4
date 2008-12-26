package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;


public class Main
{
	public static void main(String[] args)
	{
		Color c = new Color(0x00, 0x63, 0xa8, 0x33);
		Color b = new Color(0x00, 0x00, 0x00, 0xff);
		Template tmpl = Compiler.compile("<?for c in rgb(-1,2,0.5, 1)?><?print hex(c)?>,<?end for?>");
		System.out.println(tmpl);
		long start = System.currentTimeMillis();
		Map vars = new HashMap();
		String output = tmpl.renders(vars);
		System.out.println("rendered " + (System.currentTimeMillis()-start));
		System.out.println(output);
	}
}
