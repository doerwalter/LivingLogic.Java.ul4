package com.livinglogic.sxtl;

import java.util.Properties;

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

	public Template compile(String source)
	{
		return compiler.compile(source, "<?", "?>");
	}

	private L4CompilerType compiler;
}