package com.livinglogic.sxtl;

import java.util.Properties;
import java.util.Date;

import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class CompilerFactory
{
	public CompilerFactory()
	{
		Properties props = new Properties();
		props.setProperty("python.path", "/home/walter/jython/Lib:/var/home/walter/checkouts/LivingLogic.Java.sxtl/library/src/com/livinglogic/sxtl");
		PythonInterpreter.initialize(System.getProperties(), props, new String[] {""});
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("from l4c import Compiler");
		PyObject compilerclass = interpreter.get("Compiler");
		PyObject compilerObj = compilerclass.__call__();
		compiler = (L4CompilerType)compilerObj.__tojava__(L4CompilerType.class);
	}

	public String compile(String source)
	{
		System.out.println(new Date().getTime());
		String result = compiler.compile(source);
		System.out.println(new Date().getTime());
		return result;
	}

	private L4CompilerType compiler;
}