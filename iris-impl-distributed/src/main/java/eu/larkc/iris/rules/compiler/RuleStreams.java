/**
 * 
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
	
	private PipeFields headStream = null;
	private List<PipeFields> bodyStreams = new ArrayList<PipeFields>();
	
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
		headStream = new LiteralFields(mainPipe, new LiteralId(), headLiteral);
		for (int i = 0; i < rule.getBody().size(); i++) {
			ILiteral literal = rule.getBody().get(i);
			
			// we shouldn't even have to check for that if we do not deal
			// with negation, this is basically a double check for the
			// parser
			if (!literal.isPositive()) {
				throw new IllegalArgumentException("Negation is not supported: " + literal);
			}
			
			bodyStreams.add(new LiteralFields(mainPipe, new LiteralId(i), literal));
		}
	}

	public PipeFields getHeadStream() {
		return headStream;
	}
	
	public List<PipeFields> getBodyStreams() {
		return bodyStreams;
	}

	public ListIterator<PipeFields> getBodyStreamIterator() {
		return bodyStreams.listIterator();
	}
}
