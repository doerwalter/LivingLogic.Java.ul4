package com.livinglogic.ul4;

import java.util.List;

public interface CompilerType
{
	public InterpretedTemplate compile(String source, String name, List tags, String startdelim, String enddelim);
}
