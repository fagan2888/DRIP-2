
package org.drip.sample.almgren2012;

import org.drip.execution.adaptive.*;
import org.drip.execution.hjb.NonDimensionalCostEvolverSystemic;
import org.drip.execution.latent.*;
import org.drip.execution.risk.MeanVarianceObjectiveUtility;
import org.drip.execution.strategy.OrderSpecification;
import org.drip.execution.tradingtime.CoordinatedVariation;
import org.drip.measure.dynamics.DiffusionEvaluatorOrnsteinUhlenbeck;
import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for buy/side financial/trading model
 *  	libraries targeting analysts and developers
 *  	https://lakshmidrip.github.io/DRIP/
 *  
 *  DRIP is composed of four main libraries:
 *  
 *  - DRIP Fixed Income - https://lakshmidrip.github.io/DRIP-Fixed-Income/
 *  - DRIP Asset Allocation - https://lakshmidrip.github.io/DRIP-Asset-Allocation/
 *  - DRIP Numerical Optimizer - https://lakshmidrip.github.io/DRIP-Numerical-Optimizer/
 *  - DRIP Statistical Learning - https://lakshmidrip.github.io/DRIP-Statistical-Learning/
 * 
 *  - DRIP Fixed Income: Library for Instrument/Trading Conventions, Treasury Futures/Options,
 *  	Funding/Forward/Overnight Curves, Multi-Curve Construction/Valuation, Collateral Valuation and XVA
 *  	Metric Generation, Calibration and Hedge Attributions, Statistical Curve Construction, Bond RV
 *  	Metrics, Stochastic Evolution and Option Pricing, Interest Rate Dynamics and Option Pricing, LMM
 *  	Extensions/Calibrations/Greeks, Algorithmic Differentiation, and Asset Backed Models and Analytics.
 * 
 *  - DRIP Asset Allocation: Library for model libraries for MPT framework, Black Litterman Strategy
 *  	Incorporator, Holdings Constraint, and Transaction Costs.
 * 
 *  - DRIP Numerical Optimizer: Library for Numerical Optimization and Spline Functionality.
 * 
 *  - DRIP Statistical Learning: Library for Statistical Evaluation and Machine Learning.
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
 * AdaptiveZeroInitialHoldings simulates the Outstanding Holdings from the Sample Realization of the Adaptive
 *  Cost Strategy using the Market State Trajectory the follows the Zero Mean Ornstein-Uhlenbeck Evolution
 *  Dynamics. The Initial Dynamics is derived from the "Mean Market State" Initial Static Trajectory. The
 *  Initial Dynamics corresponds to the Zero Cost, Zero Cost Sensitivities, and Zero Trade Rate. The
 *  References are:
 * 
 * 	- Almgren, R. F., and N. Chriss (2000): Optimal Execution of Portfolio Transactions, Journal of Risk 3
 * 		(2) 5-39.
 *
 * 	- Almgren, R. F. (2009): Optimal Trading in a Dynamic Market
 * 		https://www.math.nyu.edu/financial_mathematics/content/02_financial/2009-2.pdf.
 *
 * 	- Almgren, R. F. (2012): Optimal Trading with Stochastic Liquidity and Volatility, SIAM Journal of
 * 		Financial Mathematics  3 (1) 163-181.
 * 
 * 	- Geman, H., D. B. Madan, and M. Yor (2001): Time Changes for Levy Processes, Mathematical Finance 11 (1)
 * 		79-96.
 * 
 * 	- Walia, N. (2006): Optimal Trading: Dynamic Stock Liquidation Strategies, Senior Thesis, Princeton
 * 		University.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class AdaptiveZeroInitialHoldings {

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double dblSize = 1.;
		int iNumTimeNode = 51;
		double dblBurstiness = 1.;
		double dblExecutionTime = 10.;
		double dblRelaxationTime = 1.;
		double dblReferenceLiquidity = 1.;
		double dblReferenceVolatility = 1.;
		double dblInitialMarketState = -0.5;
		double[] adblRiskAversion = new double[] {
			0.01,
			0.04,
			0.09,
			0.16,
			0.36,
			0.64,
			1.00
		};

		double dblNonDimensionalTimeInterval = dblExecutionTime / (iNumTimeNode - 1) / dblRelaxationTime;
		double[][] aadblNonDimensionalHoldings = new double[adblRiskAversion.length][];

		OrderSpecification os = new OrderSpecification (
			dblSize,
			dblExecutionTime
		);

		CoordinatedVariation cv = new CoordinatedVariation (
			dblReferenceVolatility,
			dblReferenceLiquidity
		);

		DiffusionEvaluatorOrnsteinUhlenbeck oup1D = DiffusionEvaluatorOrnsteinUhlenbeck.ZeroMean (
			dblBurstiness,
			dblRelaxationTime
		);

		MarketState[] aMS = OrnsteinUhlenbeckSequence.Systemic (
			oup1D,
			dblNonDimensionalTimeInterval * dblRelaxationTime,
			dblInitialMarketState,
			iNumTimeNode
		).realizedMarketState();

		for (int i = 0; i < adblRiskAversion.length; ++i)
			aadblNonDimensionalHoldings[i] = new CoordinatedVariationTrajectoryGenerator (
				os,
				cv,
				new MeanVarianceObjectiveUtility (adblRiskAversion[i]),
				NonDimensionalCostEvolverSystemic.Standard (oup1D),
				CoordinatedVariationTrajectoryGenerator.TRADE_RATE_ZERO_INITIALIZATION
			).adaptive (aMS).nonDimensionalHoldings();

		System.out.println();

		System.out.println ("\t||-----------------------------------------------------------------------------||");

		System.out.println ("\t||                     ADAPTIVE OPTIMAL TRAJECTORY HOLDINGS                    ||");

		System.out.println ("\t||-----------------------------------------------------------------------------||");

		System.out.println ("\t||     L -> R:                                                                 ||");

		System.out.println ("\t||             - Time                                                          ||");

		for (int j = 0; j < adblRiskAversion.length; ++j)
			System.out.println (
				"\t||             - Non Dimensional Risk Aversion =>" +
				FormatUtil.FormatDouble (dblRelaxationTime * dblReferenceVolatility * Math.sqrt (adblRiskAversion[j] / dblReferenceLiquidity), 1, 2, 1.) +
				"                         ||"
			);

		System.out.println ("\t||-----------------------------------------------------------------------------||");

		for (int i = 0; i < iNumTimeNode - 1; ++i) {
			String strDump = "\t|| " + FormatUtil.FormatDouble (i * dblNonDimensionalTimeInterval * dblRelaxationTime, 1, 2, 1.);

			for (int j = 0; j < adblRiskAversion.length; ++j)
				strDump = strDump + " | " + FormatUtil.FormatDouble (aadblNonDimensionalHoldings[j][i], 1, 4, 1.);

			System.out.println (strDump + " ||");
		}

		System.out.println ("\t||-----------------------------------------------------------------------------||");

		System.out.println();
	}
}
