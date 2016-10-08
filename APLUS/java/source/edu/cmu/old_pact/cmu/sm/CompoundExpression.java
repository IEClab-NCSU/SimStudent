package edu.cmu.old_pact.cmu.sm;

//CompoundExpression is an interface that indicates that the expression type
//has constituent components
//Although getComponents, buildFromComponents, etc. are methods that are most useful for
//CompoundExpressions, they are defined on Expression rather than CompoundExpression,
//since they are supported for all Expressions (non-compound expressions just have a single
//component). This leaves CompoundExpression as just a datatype, with no methods

public interface CompoundExpression {
}

