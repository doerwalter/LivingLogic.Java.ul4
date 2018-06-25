/*
** Copyright 2012-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

interface LValue
{
	public void evaluateSet(EvaluationContext context, Object value);
	public void evaluateAdd(EvaluationContext context, Object value);
	public void evaluateSub(EvaluationContext context, Object value);
	public void evaluateMul(EvaluationContext context, Object value);
	public void evaluateFloorDiv(EvaluationContext context, Object value);
	public void evaluateTrueDiv(EvaluationContext context, Object value);
	public void evaluateMod(EvaluationContext context, Object value);
	public void evaluateShiftLeft(EvaluationContext context, Object value);
	public void evaluateShiftRight(EvaluationContext context, Object value);
	public void evaluateBitAnd(EvaluationContext context, Object value);
	public void evaluateBitXOr(EvaluationContext context, Object value);
	public void evaluateBitOr(EvaluationContext context, Object value);
}
