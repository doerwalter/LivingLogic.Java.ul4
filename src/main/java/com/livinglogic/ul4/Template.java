/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.Writer;
import java.util.Map;

/**
 * Interface for various methods for generating template output.
 *
 * @author W. Doerwald
 */

public interface Template
{
	public String getName();

	public String renders(Map<String, Object> variables);

	public void render(EvaluationContext context) throws java.io.IOException;

	public void render(Writer out, Map<String, Object> variables) throws java.io.IOException;
}
