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
package eu.larkc.iris.storage;

import org.apache.hadoop.io.WritableComparable;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IIri;

/**
 * @author valer
 *
 */
public class WritableFactory {

	public static WritableComparable fromTerm(ITerm term) {
		if (term instanceof IIri) {
			return new IRIWritable((IIri) term);
		} else if (term instanceof IStringTerm) {
			return new StringTermWritable((IStringTerm) term);
		}
		return null;
	}

	public static WritableComparable fromPredicate(IPredicate predicate) {
		return new IRIWritable(predicate);
	}

}
