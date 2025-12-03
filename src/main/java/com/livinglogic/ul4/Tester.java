/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.HexFormat;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.antlr.runtime.RecognitionException;

import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;

import static com.livinglogic.ul4on.Utils.loads;
import static com.livinglogic.ul4on.Utils.dumps;

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

	public static Template compileTemplate(String source, String name, Template.Whitespace whitespace, String signature)
	{
		return new Template(source, name, "test", whitespace, signature);
	}

	public static String executeCommand(Map<String, Object> data)
	{
		String command = (String)data.get("command");
		Object templateString = data.get("template");
		Template template = null;

		if (templateString instanceof String)
			template = compileTemplate((String)templateString, (String)data.get("name"), Template.Whitespace.fromString((String)data.get("whitespace")), (String)data.get("signature"));
		else
			template = (Template)templateString;

		Map<String, Object> globalVariables = (Map<String, Object>)data.get("globalvariables");
		Map<String, Object> variables = (Map<String, Object>)data.get("variables");
		Integer milliSecondsObj = (Integer)data.get("milliseconds");
		int milliSeconds = milliSecondsObj != null ? ((int)milliSecondsObj) : -1;

		String output;

		if (command.equals("render"))
		{
			java.io.Writer writer = new java.io.StringWriter();
			template.render(writer, milliSeconds, globalVariables, variables);
			output = writer.toString();
		}
		else if (command.equals("renders"))
		{
			output = template.renders(milliSeconds, globalVariables, variables);
		}
		else if (command.equals("call"))
		{
			Object outputObject = template.call(milliSeconds, globalVariables, variables);
			output = com.livinglogic.ul4on.Utils.dumps(outputObject);
		}
		else
			throw new RuntimeException("unknown command " + FunctionRepr.call(command));
		return output;
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, UnsupportedEncodingException
	{
		Template.register4UL4ON();

		Map<String, Object> data = (Map<String, Object>)loads(readStdIn(), null);

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
