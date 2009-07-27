package com.livinglogic.ul4;

import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

/**
 * Copyright 2009 by LivingLogic AG, Bayreuth/Germany
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

	public void renderjsp(JspWriter out, Map<String, Object> variables) throws java.io.IOException;
}
