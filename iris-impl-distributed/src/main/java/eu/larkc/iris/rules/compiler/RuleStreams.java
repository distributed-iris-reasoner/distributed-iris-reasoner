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
package eu.larkc.iris.rules.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;

import cascading.pipe.Pipe;


/**
 * @author valer
 *
 */
public class RuleStreams {
	
	private static final String HEAD_LITERAL_PREFIX = "HL";
	private static final String BODY_LITERAL_PREFIX = "L";
	
	private LiteralFields headStream = null;
	private List<LiteralFields> bodyStreams = new ArrayList<LiteralFields>();
	
	public class LiteralId {
		private int index = -1;

		public LiteralId() {}

		public LiteralId(int index) {
			this.index = index;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return getPrefix() + ((index == -1) ? "" : index);
		}
		
		private String getPrefix() {
			if (index == -1) {
				return HEAD_LITERAL_PREFIX;
			} else {
				return BODY_LITERAL_PREFIX;
			}			
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof LiteralId)) {
				return false;
			}
			LiteralId literalId = (LiteralId) obj;
			return new EqualsBuilder().append(getPrefix(), literalId.getPrefix())
				.append(index, literalId.index).isEquals();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(getPrefix()).append(index).hashCode();
		}
	}
	
	public RuleStreams(Pipe mainPipe, IRule rule) {
		ILiteral headLiteral = rule.getHead().get(0);
		if (mainPipe != null) {
			headStream = new LiteralFields(mainPipe, new LiteralId(), headLiteral);
		} else {
			headStream = new LiteralFields(new LiteralId(), headLiteral);
		}
		for (int i = 0; i < rule.getBody().size(); i++) {
			ILiteral literal = rule.getBody().get(i);
			
			// we shouldn't even have to check for that if we do not deal
			// with negation, this is basically a double check for the
			// parser
			if (!literal.isPositive()) {
				throw new IllegalArgumentException("Negation is not supported: " + literal);
			}
			
			if (mainPipe != null) {
				bodyStreams.add(new LiteralFields(mainPipe, new LiteralId(i), literal));
			} else {
				bodyStreams.add(new LiteralFields(new LiteralId(i), literal));
			}
		}
	}

	public RuleStreams(IRule rule) {
		this(null, rule);
	}

	public LiteralFields getHeadStream() {
		return headStream;
	}
	
	public List<LiteralFields> getBodyStreams() {
		return bodyStreams;
	}

	public ListIterator<LiteralFields> getBodyStreamIterator() {
		return bodyStreams.listIterator();
	}
}
