package com.livinglogic.ul4;

import java.util.List;

public interface CompilerType
{
	public Template compile(String source, List tags, String startdelim, String enddelim);
}