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
package org.deri.iris.evaluation.topdown.oldt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.builtins.EqualBuiltin;
import org.deri.iris.builtins.ExactEqualBuiltin;
import org.deri.iris.evaluation.topdown.FirstLiteralSelector;
import org.deri.iris.evaluation.topdown.ILiteralSelector;
import org.deri.iris.evaluation.topdown.IPredicateTagger;
import org.deri.iris.evaluation.topdown.ITopDownEvaluator;
import org.deri.iris.evaluation.topdown.RecursivePredicateTagger;
import org.deri.iris.evaluation.topdown.SafeStandardLiteralSelector;
import org.deri.iris.evaluation.topdown.TopDownHelper;
import org.deri.iris.factory.Factory;
import org.deri.iris.facts.IFacts;
import org.deri.iris.rules.RuleManipulator;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.simple.SimpleRelationFactory;
import org.deri.iris.utils.TermMatchingAndSubstitution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the OLDT evaluator.
 * 
 * For details see 'Deduktive Datenbanken' by Cremers, Griefahn and Hinze (ISBN
 * 978-3528047009).
 * 
 * @author gigi
 * 
 */
public class OLDTEvaluator implements ITopDownEvaluator {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private IQuery mInitialQuery;
	private IFacts mFacts;
	private List<IRule> mRules;
	private MemoTable mMemoTable;
	private Set<IPredicate> mMemoPredicates;
	private ILiteralSelector mLiteralSelector;

	public static final SimpleRelationFactory srf = new SimpleRelationFactory();

	static final RuleManipulator rm = new RuleManipulator();

	/**
	 * Constructor
	 * 
	 * @param facts
	 *            one or many facts
	 * @param rules
	 *            list of rules
	 */
	public OLDTEvaluator(IFacts facts, List<IRule> rules) {
		// Initialize Facts and Rules
		mFacts = facts;
		mRules = rules;

		// Initialize Memo Predicates
		mMemoPredicates = new HashSet<IPredicate>();

		// Initialize Memo Table
		mMemoTable = new MemoTable();

		// Initialize Literal Selector
		mLiteralSelector = new SafeStandardLiteralSelector();
	}

	public MemoTable getMemoTable() {
		return mMemoTable;
	}

	/**
	 * Evaluate given query
	 */
	public IRelation evaluate(IQuery query) throws EvaluationException {

		mInitialQuery = query;

		// Tag and get memo predicates
		IPredicateTagger predicateTagger = new RecursivePredicateTagger(mRules,
				query);
		// IPredicateTagger predicateTagger = new AllPredicateTagger(mRules);
		mMemoPredicates.addAll(predicateTagger.getMemoPredicates());

		logger.debug("------------");
		logger.debug("Memo predicates: " + mMemoPredicates);
		logger.debug("------------");

		Node root = new Node(query);
		root.registerAtMemoTable();
		root.evaluate();
		IRelation relation = root.getEvaluation();

		logger.debug("------------");
		logger.debug("Relation " + relation);
		logger.debug("Original Query: " + query);
		logger.debug("Memo Table: " + mMemoTable);

		return relation;
	}

	/**
	 * Return variables of the initial query
	 */
	public List<IVariable> getOutputVariables() {
		return mInitialQuery.getVariables();
	}

	/**
	 * Inner class representing a node in the proof tree.
	 */
	class Node {
		private Node predecessor;
		private List<Node> successors;
		private IQuery query;
		private Map<IVariable, ITerm> substitution;
		private NodeState state;
		private IRelation evaluation;
		private ILiteral selectedLiteral;
		private int pointer;
		private NodeType type;
		private int numLiteralsLeft;

		/**
		 * Literal count of parenting answer node - 1. If reached, the
		 * evaluation is pushed up to the answer node
		 */

		/**
		 * Root node. Has no substitution and no predecessor.
		 * 
		 * @param query
		 *            the initial query
		 * @throws EvaluationException
		 *             on failure
		 */
		public Node(IQuery query) throws EvaluationException {
			this.query = query;
			this.substitution = null;
			this.predecessor = null;
			this.state = NodeState.INITIALIZED;
			this.evaluation = srf.createRelation();
			this.selectedLiteral = mLiteralSelector.select(query.getLiterals());
			this.pointer = 0;
			this.type = NodeType.NORMAL;
			this.numLiteralsLeft = 0;

			this.successors = new LinkedList<Node>();
		}

		/**
		 * Standard constructor.
		 * 
		 * @param predecessor
		 *            predecessor node
		 * @param query
		 *            sub-query related to this node
		 * @param substitution
		 *            substitution that was applied to form this node
		 * @throws EvaluationException
		 *             on failure
		 */
		public Node(Node predecessor, IQuery query,
				Map<IVariable, ITerm> substitution) throws EvaluationException {
			this(query);
			this.substitution = substitution;
			this.predecessor = predecessor;

			/*
			 * Count the literals of parent node. If a child node has
			 * ($PARENT_LITERALS - 1) literals, the selected literal was
			 * successfully eliminated.
			 * 
			 * This is needed for the solo refutation, hence literals are only
			 * counted for answer nodes.
			 */
			if (predecessor.isAnswerNode()) {
				this.numLiteralsLeft = predecessor.getQuery().getLiterals()
						.size() - 1;
			}
			if (predecessor.numLiteralsLeft > 0) {
				this.numLiteralsLeft = predecessor.numLiteralsLeft;
			}

			if (this.getNumLiteralsLeft() == this.getQuery().getLiterals()
					.size())
				this.soloRefutation(this.getSubstitution());
		}

		@Override
		public String toString() {
			// return ((this.getPredecessor() == null) ? "" :
			// this.getPredecessor() + " -" + this.getSubstitution() + "-> ") +
			// this.getQuery();
			return this.getQuery().toString();
		}

		protected IRelation evaluate() throws EvaluationException {
			this.extendNodeOLD();
			this.extendNodeLink();
			return this.getEvaluation();
		}

		/**
		 * Extend the node and it's child-nodes by 'classical' OLD resolution.
		 * 
		 * If the literal selection rule selects a memo predicate, the node is
		 * paused. If the selected literal can be evaluated, this node becomes a
		 * success node, a failure node, or a node containing child nodes.
		 * 
		 * 
		 * @throws EvaluationException
		 */
		private void extendNodeOLD() throws EvaluationException {
			if (this.isEvaluated() || this.isPaused())
				return;

			this.setState(NodeState.EVALUATING);

			if (this.getQuery().getLiterals().isEmpty()) {
				this.setState(NodeState.SUCCESS);
				this.printNodeDebug();

				this.pushTupleUp(this.getSubstitution()); // Push tuple up
				// recursively

				return; // Success node
			}

			ILiteral selectedLiteral = this.getSelectedLiteral();

			if (selectedLiteral == null) {
				// Additional selection
				// If SafeStandardLiteralSelector failed, take any literal
				ILiteralSelector firstSelector = new FirstLiteralSelector();
				selectedLiteral = firstSelector.select(this.getQuery()
						.getLiterals());

				if (selectedLiteral != null)
					this.setSelectedLiteral(selectedLiteral);
			}

			if (selectedLiteral == null) {
				this.setState(NodeState.FAILURE);
				this.printNodeDebug();
				logger.debug(" (literal selection not possible)");
				return; // Literal selection not possible
			}

			if (!selectedLiteral.isPositive()) {
				// Negate the literal again to get a positive literal
				ILiteral negatedLiteral = Factory.BASIC.createLiteral(true,
						selectedLiteral.getAtom());

				// Build up the NAF query
				List<ILiteral> newLiteralList = new LinkedList<ILiteral>();
				newLiteralList.add(negatedLiteral);
				newLiteralList.addAll(this.getQuery().getLiterals());
				newLiteralList.remove(selectedLiteral);

				IQuery newQuery = Factory.BASIC.createQuery(newLiteralList);
				Node nafNode = new Node(newQuery);

				logger.debug("\n===================================");
				IRelation evaluation = nafNode.evaluate();

				logger.debug("===================================\n");

				this.printNodeDebug();

				logger.debug("\t NAF evaluation: " + evaluation);

				if (evaluation == null || evaluation.size() == 0) {
					// Since NAF failed, we can remove the negated literal
					newLiteralList.clear();
					newLiteralList.addAll(this.getQuery().getLiterals());
					newLiteralList.remove(selectedLiteral);

					newQuery = Factory.BASIC.createQuery(newLiteralList);
					Node childNode = new Node(this, newQuery,
							new HashMap<IVariable, ITerm>());
					this.addChildNode(childNode);
				} else {
					this.setState(NodeState.FAILURE);
					this.printNodeDebug();
					return; // NAF returned a result, so this node fails.
				}
			}

			if (this.isPaused()) {
				if (logger.isDebugEnabled()) {
					this.printNodeDebug();
				}
				logger.debug(" paused.");
				return;
			}

			// Do the OLD extension if not done already
			if (!this.isEvaluated()) {
				this.addChildNodesOLD();
			}

			// Do the table registration for all new nodes
			for (Node child : this.getSuccessors()) {
				if (child.getSelectedLiteral() != null) {
					child.registerAtMemoTable();
				}
			}

			// If the node has no successors and is no success node, it is a
			// failure node
			if (this.getSuccessors().isEmpty()) {
				this.setState(NodeState.FAILURE);
				this.printNodeDebug();
				return;
			}

			this.evaluateChilds();
		}

		/**
		 * Extend this node and it's child-nodes using answers stored in the
		 * memo table.
		 * 
		 * @throws EvaluationException
		 *             on failure
		 */
		private void extendNodeLink() throws EvaluationException {

			if (this.isLinkNode()) {
				while (this.hasUnprocessedMemoAnswer()) {
					// Use stored answers to evaluate paused nodes (aka link
					// nodes)
					ITuple answer = mMemoTable.get(this.getSelectedLiteral()
							.getAtom(), this.getPointer());
					this.incrementPointer();

					if (logger.isDebugEnabled()) {
						printNodeDebug();
					}
					logger.debug("\t link extension with " + answer);

					List<IVariable> varList = TopDownHelper.getVariables(this
							.getSelectedLiteral().getAtom().getTuple());

					if (answer.size() == varList.size()) {
						Map<IVariable, ITerm> variableMapForSubstitution = new HashMap<IVariable, ITerm>();

						int j = 0;
						for (IVariable var : varList) {
							variableMapForSubstitution
									.put(var, answer.get(j++));
						}

						// Substitute variable bindings into query
						IQuery newQuery = TopDownHelper
								.substituteVariablesInToQuery(this.getQuery(),
										variableMapForSubstitution);

						// Remove selected Literal
						List<ILiteral> newQueryLiterals = new LinkedList<ILiteral>();
						newQueryLiterals.addAll(newQuery.getLiterals());
						int indexOfLiteralToRemove = this.getQuery()
								.getLiterals().indexOf(
										this.getSelectedLiteral());

						newQueryLiterals.remove(indexOfLiteralToRemove);

						// Create a brand new query and add it as a child of the
						// current node
						newQuery = Factory.BASIC.createQuery(newQueryLiterals);
						Node childNode = new Node(this, newQuery,
								variableMapForSubstitution);
						childNode.registerAtMemoTable();
						this.addChildNode(childNode);
						this.evaluateChild(childNode);

						// Update the memo table
						this.updateMemoTable();

					} else {
						// Answer does not match selected literal
						if (logger.isDebugEnabled()) {
							printNodeDebug();
						}
						logger.debug(" has stored answer " + answer + " for "
								+ this.getSelectedLiteral());
					}
				}
			} else if (this.isParentOfLinkNode()) {
				for (Node c : this.getSuccessors())
					c.extendNodeLink();
			}
		}

		/**
		 * Determines the node type, as defined in <code>NodeType</code>
		 * 
		 * NORMAL: default node type LINK: A paused node (link node). If the
		 * selected literal is a memo literal and is already in the memo table.
		 * ANSWER: A answer node. If the selected literal is a memo literal but
		 * occurred for the first time.
		 * 
		 * Note: Actually, the predicates are stored in the memo table. Speaking
		 * of 'memo literals' refers to literals which consist of memo
		 * predicates.
		 */
		private void classifyNodeType() {
			ILiteral selectedLiteral = this.getSelectedLiteral();

			if (selectedLiteral == null)
				return;

			IPredicate selectedPredicate = selectedLiteral.getAtom()
					.getPredicate();

			if (logger.isDebugEnabled()) {
				printNodeDebug();
			}

			if (mMemoPredicates.contains(selectedPredicate)) {
				logger.debug("\t" + selectedLiteral
						+ " is a memo predicate! Already in Memo Table? ");

				if (mMemoTable.containsKey(selectedLiteral.getAtom())) {

					// Memo predicate is already in memo table
					logger.debug(" Yes. ");

					// In memo table ==> Link Node
					this.setState(NodeState.PAUSED_N);
					this.setType(NodeType.LINK);

					logger.debug("Changed type to [" + this.getType() + "] ");

				} else {

					// Memo predicate is not in memo table
					logger.debug(" No. ");

					// In memo table ==> Answer Node
					this.setType(NodeType.ANSWER);

					logger.debug("Changed type to [" + this.getType() + "] ");
				}

			}
		}

		private void soloRefutation(Map<IVariable, ITerm> inputMap) {
			if (inputMap == null || this.getSelectedLiteral() == null)
				return;

			ITuple tupleToAddToRelation = TopDownHelper.resolveTuple(this
					.getSelectedLiteral(), inputMap);
			IAtom atom = this.getSelectedLiteral().getAtom();

			if (this.isAnswerNode()) {
				logger.debug("\t SR: adding " + tupleToAddToRelation + " to "
						+ atom + " by reverse substituting "
						+ TopDownHelper.getVariables(atom.getTuple())
						+ " with " + inputMap + " in " + this.getQuery());

				if (tupleToAddToRelation.getVariables().isEmpty()) {
					// If there are variables in the tuple
					// due to pushing the tuple upwards in the tree,
					// do not add it to the memo table since it is irrelevant
					// (tuple was relevant for another node, further down in the
					// tree)
					mMemoTable.add(atom, tupleToAddToRelation);
				}
			}

			if (this.getSubstitution() != null)
				inputMap = TopDownHelper.mergeSubstitutions(inputMap, this
						.getSubstitution());

			if (this.getPredecessor() != null)
				this.getPredecessor().soloRefutation(inputMap); // Push the
			// tuple
			// (actually the
			// variable map)
			// up one level
		}

		/**
		 * Add the tuple which is generated relative to the unique variables of
		 * the query to the relation of this node
		 * 
		 * @param inputMap
		 *            variable map where the current substitutions are stored
		 */
		private void pushTupleUp(Map<IVariable, ITerm> inputMap) {

			ITuple tupleToAddToRelation = TopDownHelper.resolveTuple(this
					.getQuery(), inputMap);

			logger.debug("\t ...adding " + tupleToAddToRelation + " to "
					+ this.getQuery() + " by reverse substituting "
					+ TopDownHelper.getVariables(this.getQuery()) + " with "
					+ inputMap);

			if (this.getEvaluation().contains(tupleToAddToRelation))
				return; // Node computed tuple earlier, no need to push it up
			// further

			if (tupleToAddToRelation.getVariables().isEmpty())
				this.addToEvaluation(tupleToAddToRelation);

			if (this.getSubstitution() != null)
				inputMap = TopDownHelper.mergeSubstitutions(inputMap, this
						.getSubstitution());

			if (this.getPredecessor() != null)
				this.getPredecessor().pushTupleUp(inputMap); // Push the tuple
			// (actually the
			// variable map)
			// up one level

		}

		/**
		 * Get all successor nodes (child nodes) and evaluate them. Add the
		 * evaluations to the current node and update this node's state.
		 * 
		 * @throws EvaluationException
		 */
		private void evaluateChilds() throws EvaluationException {
			boolean hasPausedChilds = false;

			// Expand child nodes
			if (!this.isEvaluated()
					|| (this.isLinkNode() && this.hasUnprocessedMemoAnswer()))
				for (Node childNode : this.getSuccessors()) {
					NodeState childState = childNode.getState();
					if (!childNode.isEvaluated()
							|| (childNode.isPaused() && childNode
									.hasUnprocessedMemoAnswer()))
						childState = evaluateChild(childNode);
					if (childState == NodeState.PAUSED_N
							|| childState == NodeState.PAUSED_C) {
						hasPausedChilds = true;
					}
				}

			if (!this.isPaused()) {
				// If this node is paused, no matter what,
				// the node stays paused so more and more memo answers can be
				// pushed into the node

				// If this node is NOT paused, it is either a parent of a paused
				// node (CONTAINSPAUSED)
				// or a parent of success- and/or failure nodes (DONE).
				printNodeDebug();
				this.setState(hasPausedChilds ? NodeState.PAUSED_C
						: NodeState.DONE);

				logger.debug(" changed state to [" + this.getState() + "]");
			}
		}

		/**
		 * Evaluate a child node and add the evaluation to this node.
		 * 
		 * @param childNode
		 * @return state of the child node
		 * @throws EvaluationException
		 *             on failure
		 */
		private NodeState evaluateChild(Node childNode)
				throws EvaluationException {
			childNode.evaluate();
			NodeState childState = childNode.getState();

			this.printNodeDebug();
			return childState;
		}

		/**
		 * Checks if this node has a answer stored in the memo table.
		 * 
		 * @return <code>true</code> if a answer was found, <code>false</code>
		 *         otherwise
		 */
		private boolean hasUnprocessedMemoAnswer() {

			if (this.getSelectedLiteral() == null)
				return false;

			HashMap<IVariable, ITerm> variableMap = new HashMap<IVariable, ITerm>();
			IRelation memoRelation = mMemoTable.get(this.getSelectedLiteral()
					.getAtom(), variableMap);
			if (memoRelation != null && memoRelation.size() > 0) {
				int pointer = this.getPointer();
				if (pointer >= 0 && pointer < memoRelation.size())
					return true;
			}

			return false;
		}

		/**
		 * Registers the given query and relation (answers) at the link- and/or
		 * memo table.
		 * 
		 * If the selected literal is already in the memo table, a part of the
		 * query is already being computed and thus the query is paused.
		 * 
		 * Otherwise, the predicate of the selected literal is added to the memo
		 * table, including the answers given in <code>relation</code>.
		 */
		private void registerAtMemoTable() {
			// Initialize tables if necessary
			mMemoTable = (mMemoTable == null ? new MemoTable() : mMemoTable);

			// Classify the node type to save some effort later
			this.classifyNodeType();

			// Update numbers of literals left for solo refutation
			if (this.isAnswerNode())
				this
						.setNumLiteralsLeft(this.getQuery().getLiterals()
								.size() - 1);

			ILiteral selectedLiteral = this.getSelectedLiteral();

			if (selectedLiteral == null
					|| !mMemoPredicates.contains(selectedLiteral.getAtom()
							.getPredicate()))
				return; // This is not a memo predicate

			if (mMemoTable.containsKey(selectedLiteral.getAtom())) {
				// 1) selected literal is already in the memo table
			} else {
				// 2) selected literal is not in the memo table
				mMemoTable.add(selectedLiteral.getAtom(), null);
			}
		}

		/**
		 * Add child nodes by evaluating built-ins, facts and rules.
		 * 
		 * @throws EvaluationException
		 *             on failure
		 */
		private void addChildNodesOLD() throws EvaluationException {

			if (isEvaluated())
				return;

			ILiteral selectedLiteral = this.getSelectedLiteral();

			if (selectedLiteral == null)
				return; // Literal selection not possible (no literals, no
			// executable literal)

			IAtom queryLiteralAtom = selectedLiteral.getAtom();

			if (queryLiteralAtom instanceof IBuiltinAtom) { // BuiltIn
				this.addChildNodesByEvaluatingBuiltins();
			} else { // Not BuiltIn
				this.addChildNodesByFacts();
				this.addChildNodesByRules();
			}
		}

		/**
		 * Add new child nodes by evaluating the selected literal, which has to
		 * be a builtin.
		 * 
		 * @throws EvaluationException
		 *             on failure
		 */
		private void addChildNodesByEvaluatingBuiltins()
				throws EvaluationException {

			if (!(this.getSelectedLiteral().getAtom() instanceof IBuiltinAtom))
				return; // Selected literal is not a builtin

			IBuiltinAtom builtinAtom = (IBuiltinAtom) this.getSelectedLiteral()
					.getAtom();
			ITuple builtinTuple = builtinAtom.getTuple();

			ITuple builtinEvaluation = null;
			boolean unifyable = false;
			boolean evaluationNeeded = false;

			Map<IVariable, ITerm> varMapCTarg = new HashMap<IVariable, ITerm>();

			if (builtinAtom instanceof EqualBuiltin
					|| builtinAtom instanceof ExactEqualBuiltin) {
				// UNIFICATION

				assert builtinTuple.size() == 2;
				unifyable = TermMatchingAndSubstitution.unify(builtinTuple
						.get(0), builtinTuple.get(1), varMapCTarg);

			} else {
				// EVALUATION - every builtin except EqualBuiltin
				evaluationNeeded = true;
			}

			try {
				builtinEvaluation = builtinAtom.evaluate(builtinTuple);
			} catch (IllegalArgumentException iae) {
				// The builtin can't be evaluated yet, simply continue
			}

			List<ILiteral> literalsWithoutBuiltin = new LinkedList<ILiteral>(
					query.getLiterals());
			literalsWithoutBuiltin.remove(this.getSelectedLiteral());
			IQuery newQuery = Factory.BASIC.createQuery(literalsWithoutBuiltin);

			if (builtinEvaluation != null) {

				if (builtinTuple.getVariables().isEmpty()) {
					// Builtin tuple contained no variables, the result is
					// true or false, e.g. ADD(1, 2, 3) = true
					Node childNode = new Node(this, newQuery,
							new HashMap<IVariable, ITerm>());
					this.addChildNode(childNode);

				} else {
					// Builtin tuple contained variables, so there is a
					// computed answer, e.g. ADD(1, 2, ?X) => ?X = 3
					Map<IVariable, ITerm> varMap = new HashMap<IVariable, ITerm>();
					Set<IVariable> variablesPreEvaluation = builtinTuple
							.getVariables();

					if (variablesPreEvaluation.size() != builtinEvaluation
							.size())
						throw new EvaluationException(
								"Builtin Evaluation failed. Expected "
										+ variablesPreEvaluation.size()
										+ " results, got "
										+ builtinEvaluation.size());

					// Add every computed variable to the mapping
					int variableIndex = 0;
					for (IVariable var : variablesPreEvaluation) {

						if (unifyable) { // unification
							varMap.putAll(varMapCTarg);
						} else if (evaluationNeeded) { // evaluation
							ITerm termPostEvaluation = builtinEvaluation
									.get(variableIndex); // get evaluated term
							varMap.put(var, termPostEvaluation);
						} else { // no new query / branch
							variableIndex++;
							continue;
						}

						// Add the new query to the query list
						newQuery = TopDownHelper.substituteVariablesInToQuery(
								newQuery, varMap);
						Node childNode = new Node(this, newQuery, varMap);
						this.addChildNode(childNode);

						variableIndex++;
					}
				}
			} else if (unifyable) {
				// Builtin evaluation failed, unification succeeded
				// Take unify result as mapping
				Map<IVariable, ITerm> varMap = new HashMap<IVariable, ITerm>();

				varMap.putAll(varMapCTarg);

				// Add the new query to the query list
				newQuery = TopDownHelper.substituteVariablesInToQuery(newQuery,
						varMap);
				Node childNode = new Node(this, newQuery, varMap);
				this.addChildNode(childNode);
			}
		}

		/**
		 * Scans the knowledge base for rules that match the selected query
		 * literal. If a unifiable match was found the substitution and the new
		 * query will be saved and added to a list of new queries, which is
		 * returned.
		 * 
		 * @param query
		 *            the whole query
		 * @param selectedLiteral
		 *            the selected literal
		 * 
		 * @throws EvaluationException
		 *             on failure
		 */
		private void addChildNodesByRules() throws EvaluationException {

			for (IRule rule : mRules) {
				ILiteral ruleHead = rule.getHead().get(0);

				// Potential match?
				if (TopDownHelper.match(ruleHead, this.getSelectedLiteral())) {
					Map<IVariable, ITerm> mgu = new HashMap<IVariable, ITerm>();
					ITuple queryTuple = this.getSelectedLiteral().getAtom()
							.getTuple();

					// Replace all variables of the rule head with unused ones
					// (variables that are not in the query)
					Map<IVariable, ITerm> variableRenaming = TopDownHelper
							.getVariableMapForVariableRenaming(rule, this
									.getQuery());
					IRule ruleAfterVariableRenaming = TopDownHelper
							.replaceVariablesInRule(rule, variableRenaming);
					ITuple ruleHeadTuple = ruleAfterVariableRenaming.getHead()
							.get(0).getAtom().getTuple(); // ruleHead changed

					// Get most general unifier
					boolean unifyable = TermMatchingAndSubstitution.unify(
							queryTuple, ruleHeadTuple, mgu);

					IQuery newQuery = this.getQuery();
					if (unifyable) {
						// Replace the rule head with the rule body
						newQuery = TopDownHelper.substituteRuleHeadWithBody(
								newQuery, this.getSelectedLiteral(),
								ruleAfterVariableRenaming);

						// Substitute the whole query
						newQuery = TopDownHelper.substituteVariablesInToQuery(
								newQuery, mgu);
					}

					Node childNode = new Node(this, newQuery, mgu);
					this.addChildNode(childNode);

				}
			}
		}

		/**
		 * Scans the knowledge base for facts that match the selected query
		 * literal. If a unifiable match was found the substitution and the new
		 * query will be saved and added to a list of new queries, which is
		 * returned.
		 * 
		 * @param query
		 *            the whole query
		 * @param queryLiteral
		 *            the selected literal
		 * 
		 * @return list of queries with substitutions
		 * @throws EvaluationException
		 */
		private void addChildNodesByFacts() throws EvaluationException {

			List<Map<IVariable, ITerm>> variableMapList = new LinkedList<Map<IVariable, ITerm>>();
			if (this.getMatchingFacts(variableMapList)) {
				for (Map<IVariable, ITerm> variableMap : variableMapList) {
					// For every fact

					// Remove the fact, ...
					LinkedList<ILiteral> literalsWithoutMatch = new LinkedList<ILiteral>(
							this.getQuery().getLiterals());
					literalsWithoutMatch.remove(this.getSelectedLiteral());

					// Add the new query to the query list
					IQuery newQuery = Factory.BASIC
							.createQuery(literalsWithoutMatch);

					// Substitute the whole query
					IQuery substitutedQuery = TopDownHelper
							.substituteVariablesInToQuery(newQuery, variableMap);

					Node childNode = new Node(this, substitutedQuery,
							variableMap);
					this.addChildNode(childNode);
				}
			}
		}

		/**
		 * Tries to find a fact that matches the given query. The variableMap
		 * will be populated if a matching fact was found.
		 * 
		 * @param queryLiteral
		 *            the given query
		 * @param variableMapList
		 *            a <i>List</i> of <i>Maps</i> that stores the resulting
		 *            substitution if a fact was found
		 * 
		 * @return true if a matching fact is found, false otherwise
		 */
		private boolean getMatchingFacts(
				List<Map<IVariable, ITerm>> variableMapList) {

			// Check all the facts
			for (IPredicate factPredicate : mFacts.getPredicates()) {
				// Check if the predicate and the arity matches
				if (TopDownHelper.match(this.getSelectedLiteral(),
						factPredicate)) {
					// We've found a match (predicates and arity match)
					// Is the QueryTuple unifiable with one of the FactTuples?

					IRelation factRelation = mFacts.get(factPredicate);

					// Substitute variables into the query
					for (int i = 0; i < factRelation.size(); i++) {
						ITuple queryTuple = this.getSelectedLiteral().getAtom()
								.getTuple();
						boolean tupleUnifyable = false;
						ITuple factTuple = factRelation.get(i);
						Map<IVariable, ITerm> variableMap = new HashMap<IVariable, ITerm>();
						tupleUnifyable = TermMatchingAndSubstitution.unify(
								queryTuple, factTuple, variableMap);
						if (tupleUnifyable) {
							queryTuple = TermMatchingAndSubstitution
									.substituteVariablesInToTuple(queryTuple,
											variableMap);
							variableMapList.add(variableMap);
						}
					}
				}
			}
			if (variableMapList.isEmpty())
				return false; // No fact found

			return true;
		}

		/**
		 * Add a tuple to this nodes evaluation
		 * 
		 * @param tuple
		 *            the <code>ITuple</code> to add
		 */
		private void addToEvaluation(ITuple tuple) {
			IRelation evaluation = this.getEvaluation();
			evaluation.add(tuple);
			this.setEvaluation(evaluation);

			// If this node is a answer node, add the evaluation to the memo
			// table as well
			if (this.isAnswerNode()) {
				Map<IVariable, ITerm> memoTupleVarMap = TopDownHelper
						.createVariableMapFromTupleAndQuery(this.getQuery(),
								tuple);
				ITuple memoTuple = TopDownHelper.resolveTuple(this
						.getSelectedLiteral(), memoTupleVarMap);
				mMemoTable.add(this.getSelectedLiteral().getAtom(), memoTuple);
			}
		}

		/**
		 * Update the corresponding entry of the memo table which is related to
		 * the selected literal.
		 */
		private void updateMemoTable() {
			if (this.getSelectedLiteral() == null)
				return;

			IAtom atom = this.getSelectedLiteral().getAtom();
			if (mMemoTable.get(atom) != null) {
				IRelation eval = this.getEvaluation();

				for (int i = 0; i < eval.size(); i++) {
					ITuple tuple = eval.get(i);
					Map<IVariable, ITerm> nodeVarMap = TopDownHelper
							.createVariableMapFromTupleAndQuery(
									this.getQuery(), tuple);
					ITuple nodeMemoTuple = TopDownHelper.resolveTuple(this
							.getSelectedLiteral(), nodeVarMap);

					if (!mMemoTable.get(atom).contains(nodeMemoTuple)) {
						mMemoTable.add(atom, nodeMemoTuple);
					}
				}
			}
		}

		private int getNumLiteralsLeft() {
			return numLiteralsLeft;
		}

		private void setNumLiteralsLeft(int numLiteralsLeft) {
			this.numLiteralsLeft = numLiteralsLeft;
		}

		private IQuery getQuery() {
			return query;
		}

		private int getPointer() {
			return pointer;
		}

		private Map<IVariable, ITerm> getSubstitution() {
			return substitution;
		}

		private Node getPredecessor() {
			return predecessor;
		}

		private List<Node> getSuccessors() {
			return successors;
		}

		private NodeState getState() {
			return state;
		}

		private IRelation getEvaluation() {
			return evaluation;
		}

		/**
		 * Checks if this node was already fully evaluated.
		 * 
		 * @return true if node was fully evaluated (DONE, SUCCESS or FAILURE),
		 *         false otherwise
		 */
		private boolean isEvaluated() {
			if (this.getState() == NodeState.DONE
					|| this.getState() == NodeState.SUCCESS
					|| this.getState() == NodeState.FAILURE) {

				return true;
			}

			return false;
		}

		private boolean isPaused() {
			return (this.getState() == NodeState.PAUSED_N);
		}

		private boolean isLinkNode() {
			return (this.getType() == NodeType.LINK);
		}

		private boolean isAnswerNode() {
			return (this.getType() == NodeType.ANSWER);
		}

		private boolean isParentOfLinkNode() {
			return (this.getState() == NodeState.PAUSED_C);
		}

		private NodeType getType() {
			return this.type;
		}

		private void addChildNode(Node successor) {
			this.successors.add(successor);
		}

		private void incrementPointer() {
			this.pointer++;
		}

		private void setState(NodeState state) {
			this.state = state;
		}

		private void setType(NodeType type) {
			this.type = type;
		}

		private void setEvaluation(IRelation evaluation) {
			this.evaluation = evaluation;
		}

		private ILiteral getSelectedLiteral() {
			return selectedLiteral;
		}

		private void setSelectedLiteral(ILiteral selectedLiteral) {
			this.selectedLiteral = selectedLiteral;
		}

		private void printNodeDebug() {
			logger.debug(this.getNumLiteralsLeft() + " "
					+ this.getQuery().getLiterals().size() + " [" + getType()
					+ "\t" + getState() + "]\t" + this.getQuery() + "\t= "
					+ getEvaluation());
		}

	}

}
