/*
 * Copyright 2010 Softgress - http://www.softgress.com/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.larkc.iris.evaluation;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * Maintains information about the evaluation context (under which context a rule is evaluated, stratum, iteration, rule identifier)
 * For now it stores the iteration number through the set of rules evaluations and the number of rule evaluated in the rules list
 * 
 * @author vroman@softgress.com
 *
 */
public class EvaluationContext {

	private int stratumNumber;
	private int iterationNumber;
	private int ruleNumber;
	
	public EvaluationContext(int stratumNumber, int iterationNumber, int ruleNumber) {
		this.stratumNumber = stratumNumber;
		this.iterationNumber = iterationNumber;
		this.ruleNumber = ruleNumber;
	}
	
	public int getIterationNumber() {
		return iterationNumber;
	}
	public int getRuleNumber() {
		return ruleNumber;
	}

	/**
	 * @return the stratumNumber
	 */
	public int getStratumNumber() {
		return stratumNumber;
	}

	public void setStratumNumber(int stratumNumber) {
		this.stratumNumber = stratumNumber;
	}

	public void setIterationNumber(int iterationNumber) {
		this.iterationNumber = iterationNumber;
	}

	public void setRuleNumber(int ruleNumber) {
		this.ruleNumber = ruleNumber;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
}
