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
		props.setProperty("python.path", "/Users/walter/jython/Lib:/Users/walter/checkouts/LivingLogic.Java.sxtl/library/src/com/livinglogic/sxtl");
		PythonInterpreter.initialize(System.getProperties(), props, new String[] {""});
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("from l4c import Compiler");
		PyObject compilerclass = interpreter.get("Compiler");
		PyObject compilerObj = compilerclass.__call__();
		compiler = (CompilerType)compilerObj.__tojava__(CompilerType.class);
	}

	public Template compile(String source)
	{
		return compiler.compile(source, "<?", "?>");
	}

	private CompilerType compiler;
}