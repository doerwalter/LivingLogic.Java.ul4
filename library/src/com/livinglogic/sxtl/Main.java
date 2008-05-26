package com.livinglogic.sxtl;

import java.util.Date;
import java.util.HashMap;

public class Main
{
	public static long time()
	{
		return new Date().getTime();
	}

	public static void main(String[] args)
	{
		long start = new Date().getTime();
		CompilerFactory factory = new CompilerFactory();
		System.out.println("made Factory " + (time()-start));
		Template tmpl = factory.compile("<?if data?><ul><?for item in data?><?render line(item)?><?end for?></ul><?end if?>");
		Template linetmpl = factory.compile("<li>(<?print xmlescape(data)?>)</li>");
		System.out.println("compiled " + (time()-start));
		HashMap<String, Template> templates = new HashMap<String, Template>();
		templates.put("line", linetmpl);
		String output = tmpl.renders("<gu&rk> & 'foo'", templates);
		System.out.println("rendered " + (time()-start));
		System.out.println(output);
	}
}
