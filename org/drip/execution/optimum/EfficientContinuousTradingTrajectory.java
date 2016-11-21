
package org.drip.execution.optimum;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2016 Lakshmi Krishnamurthy
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
 * EfficientContinuousTradingTrajectory contains the Efficient Trading Trajectory generated by one of the
 *  Methods outlined in the Almgren (2003) Scheme for Continuous Trading Approximation. The References are:
 * 
 * 	- Almgren, R., and N. Chriss (1999): Value under Liquidation, Risk 12 (12).
 * 
 * 	- Almgren, R., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3 (2)
 * 		5-39.
 * 
 * 	- Almgren, R. (2003): Optimal Execution with Nonlinear Impact Functions and Trading-Enhanced Risk,
 * 		Applied Mathematical Finance 10 (1) 1-18.
 * 
 * 	- Almgren, R., and N. Chriss (2003): Bidding Principles, Risk 97-102.
 * 
 * 	- Bertsimas, D., and A. W. Lo (1998): Optimal Control of Execution Costs, Journal of Financial Markets,
 * 		1, 1-50.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class EfficientContinuousTradingTrajectory extends
	org.drip.execution.strategy.ContinuousTradingTrajectory implements
		org.drip.execution.optimum.EfficientTradingTrajectory {
	private double _dblCharacteristicTime = java.lang.Double.NaN;
	private double _dblTransactionCostVariance = java.lang.Double.NaN;
	private double _dblTransactionCostExpectation = java.lang.Double.NaN;

	/**
	 * Construct the Standard EfficientContinuousTradingTrajectory Instance
	 * 
	 * @param dblExecutionTime The Execution Time
	 * @param dblTransactionCostExpectation The Expected Transaction Cost
	 * @param dblTransactionCostVariance The Variance of the Transaction Cost
	 * @param dblCharacteristicTime The Optimal Trajectory's "Characteristic" Time
	 * @param r1ToR1Holdings The Optimal Trajectory R^1 To R^1 Holdings Function
	 * 
	 * @return The Standard EfficientContinuousTradingTrajectory Instance
	 */

	public static EfficientContinuousTradingTrajectory Standard (
		final double dblExecutionTime,
		final double dblTransactionCostExpectation,
		final double dblTransactionCostVariance,
		final double dblCharacteristicTime,
		final org.drip.function.definition.R1ToR1 r1ToR1Holdings)
	{
		if (null == r1ToR1Holdings) return null;

		try {
			org.drip.function.definition.R1ToR1 r1ToR1TradeRate = new org.drip.function.definition.R1ToR1
				(null) {
				@Override public double evaluate (
					final double dblVariate)
					throws java.lang.Exception
				{
					return r1ToR1Holdings.derivative (dblVariate, 1);
				}
			};

			return new EfficientContinuousTradingTrajectory (dblExecutionTime, dblTransactionCostExpectation,
				dblTransactionCostVariance, dblCharacteristicTime, r1ToR1Holdings, r1ToR1TradeRate);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * EfficientContinuousTradingTrajectory Constructor
	 * 
	 * @param dblExecutionTime The Execution Time
	 * @param dblTransactionCostExpectation The Expected Transaction Cost
	 * @param dblTransactionCostVariance The Variance of the Transaction Cost
	 * @param dblCharacteristicTime The Optimal Trajectory's "Characteristic" Time
	 * @param r1ToR1Holdings The Optimal Trajectory R^1 To R^1 Holdings Function
	 * @param r1ToR1TradeRate The Optimal Trajectory R^1 To R^1 Trade Rate Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public EfficientContinuousTradingTrajectory (
		final double dblExecutionTime,
		final double dblTransactionCostExpectation,
		final double dblTransactionCostVariance,
		final double dblCharacteristicTime,
		final org.drip.function.definition.R1ToR1 r1ToR1Holdings,
		final org.drip.function.definition.R1ToR1 r1ToR1TradeRate)
		throws java.lang.Exception
	{
		super (dblExecutionTime, r1ToR1Holdings, r1ToR1TradeRate);

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblTransactionCostExpectation =
			dblTransactionCostExpectation) || !org.drip.quant.common.NumberUtil.IsValid
				(_dblTransactionCostVariance = dblTransactionCostVariance) ||
					!org.drip.quant.common.NumberUtil.IsValid (_dblCharacteristicTime =
						dblCharacteristicTime))
			throw new java.lang.Exception
				("EfficientContinuousTradingTrajectory Constructor => Invalid Inputs");
	}

	@Override public double transactionCostExpectation()
	{
		return _dblTransactionCostExpectation;
	}

	@Override public double transactionCostVariance()
	{
		return _dblTransactionCostVariance;
	}

	/**
	 * Retrieve the Optimal Trajectory Characteristic Time
	 * 
	 * @return The Optimal Trajectory Characteristic Time
	 */

	public double characteristicTime()
	{
		return _dblCharacteristicTime;
	}
}
