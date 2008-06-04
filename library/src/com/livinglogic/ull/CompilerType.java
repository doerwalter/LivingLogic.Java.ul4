package com.livinglogic.ull;

public interface CompilerType
{
	public Template compile(String source, String startdelim, String enddelim);
}