/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;

import com.livinglogic.ul4.FunctionRepr;
import com.livinglogic.ul4.Template;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HexFormat;
import java.util.Map;

import static com.livinglogic.ul4on.Utils.dumps;
import static com.livinglogic.ul4on.Utils.loads;

public class Tester
{
	public static String readStdIn() throws IOException, UnsupportedEncodingException
	{
		StringBuilder buffer = new StringBuilder();

		java.io.InputStreamReader reader = new java.io.InputStreamReader(System.in, "utf-8");

		for (;;)
		{
			int c = reader.read();

			if (c == -1)
				break;
			buffer.append((char)c);
		}
		return buffer.toString();
	}

	public static String executeCommand(Map<String, String> data)
	{
		String dump = data.get("dump");
		String indent = data.get("indent");
		Object objects = loads(dump, null);
		return dumps(objects, indent);
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, UnsupportedEncodingException
	{
		Template.register4UL4ON();

		Map<String, String> data = (Map<String, String>)loads(readStdIn(), null);

		String output = executeCommand(data);

		// We can't use {@code System.out.print} here
		// because this gives us no control over the encoding.
		// We also can't use {@code System.out.write} with UTF-8 bytes
		// because this won't work reliably when running under Gradle.
		// For example if we output a single CR (i.e. "\r")
		// this will get swallowed by Gradle.
		byte[] outputBytes = output.getBytes("utf-8");
		System.out.println("hexdump:" + HexFormat.of().formatHex(outputBytes));
	}
}
