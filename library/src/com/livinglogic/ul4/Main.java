package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;


public class Main
{
	public static void main(String[] args)
	{
		Template x = Compiler.compile("<?print y?>");
		Template tmpl = Compiler.compile("<?render x(**{'y':42})?>");
		long start = System.currentTimeMillis();
		Map map = new HashMap();
		map.put("x", x);
		String output = tmpl.renders(map);
		System.out.println("rendered " + (System.currentTimeMillis()-start));
		System.out.println(output);
	}
}
