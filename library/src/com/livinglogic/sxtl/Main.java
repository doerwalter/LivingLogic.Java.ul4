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
		System.out.println("make Factory " + (time()-start));
		Template bytecode = factory.compile("<?if data?><ul><?for item in data?><li><?print xmlescape(item)?></li><?end for?></ul><?end if?>");
		System.out.println("Compile " + (time()-start));
		Renderer renderer = new Renderer(bytecode.opcodes);
		System.out.println("Make Renderer " + (time()-start));
		String output = renderer.render("gurk");
		System.out.println("Render " + (time()-start));
		System.out.println(output);
	}
}
