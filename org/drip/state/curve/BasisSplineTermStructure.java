
package org.drip.state.curve;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for fixed income analysts and developers -
 * 		http://www.credit-trader.org/Begin.html
 * 
 *  DRIP is a free, full featured, fixed income rates, credit, and FX analytics library with a focus towards
 *  	pricing/valuation, risk, and market making.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   	you may not use this file except in compliance with the License.
 *   
 *  You may obtain a copy of the License at
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  	distributed under the License is distributed on an "AS IS" BASIS,
 *  	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  
 *  See the License for the specific language governing permissions and
 *  	limitations under the License.
 */

/**
 * BasisSplineTermStructure implements the TermStructure Interface - if holds the latent state's Term
 * 	Structure Parameters.
 *
 * @author Lakshmi Krishnamurthy
 */

public class BasisSplineTermStructure extends org.drip.analytics.definition.NodeStructure {
	private org.drip.spline.grid.Span _span = null;

	/**
	 * BasisSplineTermStructure Constructor
	 * 
	 * @param iEpochDate The Epoch Date
	 * @param label Term Structure Latent State Label
	 * @param strCurrency The Currency
	 * @param span The Latent State Span
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public BasisSplineTermStructure (
		final int iEpochDate,
		final org.drip.state.identifier.CustomLabel label,
		final java.lang.String strCurrency,
		final org.drip.spline.grid.Span span)
		throws java.lang.Exception
	{
		super (iEpochDate, label, strCurrency);

		_span = span;
	}

	@Override public double node (
		final int iPredictorOrdinate)
		throws java.lang.Exception
	{
		double dblSpanLeft = _span.left();

		if (dblSpanLeft >= iPredictorOrdinate) return _span.calcResponseValue (dblSpanLeft);

		double dblSpanRight = _span.right();

		if (dblSpanRight <= iPredictorOrdinate) return _span.calcResponseValue (dblSpanRight);

		return _span.calcResponseValue (iPredictorOrdinate);
	}

	@Override public double nodeDerivative (
		final int iPredictorOrdinate,
		final int iOrder)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (iPredictorOrdinate))
			throw new java.lang.Exception ("BasisSplineTermStructure::nodeDerivative => Invalid Inputs");

		double dblSpanLeft = _span.left();

		if (dblSpanLeft >= iPredictorOrdinate)
			return _span.calcResponseValueDerivative (dblSpanLeft, iOrder);

		double dblSpanRight = _span.right();

		if (dblSpanRight <= iPredictorOrdinate)
			return _span.calcResponseValueDerivative (dblSpanRight, iOrder);

		return _span.calcResponseValueDerivative (iPredictorOrdinate, iOrder);
	}
}