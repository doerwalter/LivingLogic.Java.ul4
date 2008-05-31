package com.livinglogic.sxtl;

public interface CompilerType
{
	public Template compile(String source, String startdelim, String enddelim);
}