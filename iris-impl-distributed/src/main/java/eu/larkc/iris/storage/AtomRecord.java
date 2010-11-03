package eu.larkc.iris.storage;

import org.deri.iris.api.basics.IAtom;

import cascading.tuple.Tuple;

public abstract class AtomRecord {

	protected Tuple tuple;
	
	public AtomRecord() {}
	
	public AtomRecord(Tuple tuple) {
		this.tuple = tuple;
	}
	
	public abstract void write(FactsStorage storage);
	
	public abstract void read(FactsStorage storage, IAtom atom);

	public Tuple getTuple() {
		return tuple;
	}

	public void setTuple(Tuple tuple) {
		this.tuple = tuple;
	}

}
