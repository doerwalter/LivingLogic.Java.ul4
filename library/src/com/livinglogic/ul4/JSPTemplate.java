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
 * Interface for JSP code that has been put into a method to be reusable.
 *
 * @author W. Dörwald
 * @version $Revision$ $Date$
 */

public interface JSPTemplate
{
	public void execute(JspWriter out, Map variables) throws java.io.IOException, javax.servlet.ServletException;
}
