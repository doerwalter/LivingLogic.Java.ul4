package com.livinglogic.ul4;

import java.util.Map;
import java.io.Writer;
import java.io.IOException;

/**
 * Copyright 2009-2010 by LivingLogic AG, Bayreuth/Germany
 *
 * All Rights Reserved
 *
 * See LICENSE for the license
 *
 * Interface for various methods for generating template output.
 *
 * @author W. Dörwald
 * @version $Revision$ $Date$
 */

public interface Template
{
	public String renders(Map<String, Object> variables);

	public void render(Writer out, Map<String, Object> variables) throws java.io.IOException;
}
