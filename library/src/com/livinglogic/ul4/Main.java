package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;


public class Main
{
	public static void main(String[] args)
	{
		Template tmpl = Compiler.compile("<?print {**{'y':42}}?>");
		long start = System.currentTimeMillis();
		Map map = new HashMap();
		String output = tmpl.renders(map);
		System.out.println("rendered " + (System.currentTimeMillis()-start));
		System.out.println(output);
	}
}
