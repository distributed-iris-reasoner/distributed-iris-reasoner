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

/**
 * Used to classify nodes.
 * 
 * LINK		A link node which is a special form of a memo node.
 * 			A link node is not evaluated by resolution, but 
 * 			previously computed answers are used to expand the node.
 * 			By trying to expand the link node using resolution,
 * 			the node gets paused.
 * 			
 * ANSWER	A answer node which is a special form of a memo node.
 * 			A answer node is evaluated by resolution. The answers
 * 			are stored in the corresponding entry in the memo table.
 * 
 * NORMAL	A ordinary node (not a memo node).
 * 
 * @author gigi
 *
 */
public enum NodeType {
	LINK, ANSWER, NORMAL
}
