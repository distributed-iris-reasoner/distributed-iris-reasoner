/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.deri.iris.terms.concrete;

import java.net.URI;

import javax.xml.datatype.Duration;

import org.deri.iris.api.terms.concrete.IYearMonthDuration;

/*
 * W3C specification: http://www.w3.org/TR/xpath-functions/#dt-yearMonthDuration
 */

/**
 * <p>
 * An interface for representing the xs:yearMonthDuration data-type.
 * xs:yearMonthDuration is derived from xs:duration by restricting its lexical
 * representation to contain only the year and month components.
 * </p>
 * <p>
 * Example: _yearMonthDuration(1, 6) represents the duration of <b>one and a
 * half years</b>.
 * </p>
 * <p>
 * Remark: IRIS supports datatypes according to the standard specification for
 * primitive XML Schema datatypes.
 * </p>
 * 
 * @author gigi
 * 
 */
public class YearMonthDuration extends org.deri.iris.terms.concrete.Duration
		implements IYearMonthDuration {

	/**
	 * All values except from sign, year and month are initialized with zero.
	 */
	YearMonthDuration(boolean positive, int year, int month) {
		super(positive, year, month, 0, 0, 0, 0);
	}

	/**
	 * The canonical representation of yearMonthDuration as defined in
	 * http://www.w3.org/TR/xpath-functions/#canonical-yearMonthDuration.
	 */
	public YearMonthDuration toCanonical() {
		int allMonths = getYear() * 12 + getMonth();

		int years = allMonths / 12;
		int months = allMonths % 12;
		boolean positive = getValue().getSign() > -1;

		return new YearMonthDuration(positive, years, months);
	}

	/**
	 * Shortened representation
	 * 
	 * The xs:yearMonthDuration type represents a restriction of the xs:duration
	 * type, with only year (Y) and month (M) components allowed. Its lexical
	 * representation is PnYnM, where an uppercase P starts the expression, and
	 * n indicates the quantity of each component. For example, the value P3Y5M
	 * represents a period of three years and five months. You can omit
	 * components whose quantity is zero, but at least one component is
	 * required. All of the lexical rules for xs:duration also apply to the
	 * xs:yearMonthDuration type. This includes allowing a negative sign at the
	 * beginning of the value.
	 * 
	 * source:
	 * http://my.safaribooksonline.com/0596006349/orm9780596006341-APP-B-SECT-50
	 */
	public String toCanonicalString() {
		Duration duration = this.getValue();
		if (duration.getSign() == 0)
			return "P0M";

		StringBuffer repr = new StringBuffer();

		if (duration.getSign() == -1)
			repr.append("-");

		repr.append("P");

		if (duration.getYears() > 0) {
			repr.append(duration.getYears());
			repr.append("Y");
		}

		if (duration.getMonths() > 0) {
			repr.append(duration.getMonths());
			repr.append("M");
		}

		return repr.toString();
	}

	public URI getDatatypeIRI() {
		return URI.create("http://www.w3.org/2001/XMLSchema#yearMonthDuration");
	}

	public String toString() {
		return toCanonicalString();
	}
}
