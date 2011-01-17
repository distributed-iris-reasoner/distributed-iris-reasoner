/**
 * 
 */
package eu.larkc.iris.rules.stratification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.rules.IRuleStratifier;
import org.deri.iris.rules.RuleHeadEquality;

/**
 * TODO
 * @author Florian Fischer, fisf, 14.01.2011
 */
public class DependencyMinimizingStratifier implements IRuleStratifier {

	@Override
	public List<List<IRule>> stratify(List<IRule> rules) {
		
		//TODO: adapt so that it orders according to dependencies / recursion
		//normally single strata are only created when negation is present, we want a single strata per dependency in order to isolate recursion
		
		
		
		
		final int ruleCount = rules.size();
		int highest = 0;
		boolean change = true;

		// Clear the strata map, i.e. set all strata to 0
		mStrata.clear();
		
		while ((highest <= ruleCount ) && change)
		{
			change = false;
			for (final IRule r : rules)
			{
				for (final ILiteral hl : r.getHead())
				{
					final IPredicate hp = hl.getAtom().getPredicate();

					if( r.getBody().size() == 0 )
					{
						// Has the effect of setting to stratum to zero if this predicate
						// has not been seen before
						getStratum( hp );
					}
					else
					{
						for (final ILiteral bl : r.getBody())
						{
							final IPredicate bp = bl.getAtom().getPredicate();
	
							if (bl.isPositive())
							{
								int greater = Math.max(getStratum(hp), 
												getStratum(bp));
								if (getStratum(hp) < greater)
								{
									setStratum(hp, greater);
									change = true;
								}
								highest = Math.max(highest, greater);
							}
							else
							{
								int current = getStratum(bp);
								if (current >= getStratum(hp))
								{
									setStratum(hp, current + 1);
									highest = Math.max(highest, current + 1);
									change = true;
								}
							}
						}
					}
				}
			}
		}
		
		if( highest <= ruleCount )
		{
			List<List<IRule>> result = new ArrayList<List<IRule>>();
			
			for( int stratum = 0; stratum <= highest; ++stratum )
				result.add( new ArrayList<IRule>() );

			for( Map.Entry<IPredicate, Integer> entry : mStrata.entrySet() )
			{
				// Identify the stratum and predicate
				int stratum = entry.getValue();
				IPredicate predicate = entry.getKey();
				
				// Now search for all rules that have this predicate as the head
				for( IRule rule : rules )
				{			
					
					if( rule.getHead().get( 0 ).getAtom().getPredicate().equals( predicate ) )
					{
						result.get( stratum ).add( rule );
					}
				}
			}
			
			return result;
		}
		else
			return null;
	}
	
	/**
	 * Get the stratum for a particular (rule head) predicate.
	 * @param predicate The rule-head predicate.
	 * @return The stratum level.
	 */
	private int getStratum( final IPredicate predicate )
	{
		assert predicate!= null;
		
		Integer stratum = mStrata.get( predicate );
		
		if( stratum == null )
		{
			stratum = 0;
			mStrata.put( predicate, stratum );
		}
		
		return stratum;
	}
	
	/**
	 * Set the stratum for a (rule-head) predicate.
	 * @param predicate predicate
	 * @param stratum stratum level
	 */
	private void setStratum(final IPredicate predicate, final int stratum)
	{
		assert predicate != null;
		assert stratum >= 0 : "The stratum must not be negative, but was: " + stratum;
		
		mStrata.put(predicate, Integer.valueOf(stratum));
	}
		

	/** Map for the strata of the different predicates. */
	private final Map<IPredicate, Integer> mStrata = new HashMap<IPredicate, Integer>();
}
