/*
** Copyright 2013-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

public interface CloseableRegistry
{
	public void registerCloseable(AutoCloseable closeable);
}
