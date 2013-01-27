/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
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
	public void render(EvaluationContext context, Map<String, Object> variables) throws java.io.IOException;

	public String renders(EvaluationContext context, Map<String, Object> variables);

	public String formatText(String text);
}
