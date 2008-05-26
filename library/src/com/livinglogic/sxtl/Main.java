package com.livinglogic.sxtl;

import java.util.Date;

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
		Template template = factory.compile("<?if data?><ul><?for item in data?><li>(<?print xmlescape(item)?>)</li><?end for?></ul><?end if?>");
		System.out.println("compiled " + (time()-start));
		String output = template.render("<gu&rk> & 'foo'");
		System.out.println("rendered " + (time()-start));
		System.out.println(output);
	}
}
