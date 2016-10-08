package edu.cmu.pact.miss.userDef.algebra.expression.decomposers;

import java.util.Vector;

import edu.cmu.pact.miss.Decomposer;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ComplexFraction;
import edu.cmu.pact.miss.userDef.algebra.expression.ConstantFraction;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class AlgExpFractionDecomposer extends Decomposer {

    public Vector decompose(String foa) {

        Vector decomposedElements = null;

        try {
            AlgExp exp=AlgExp.parseExp(foa);

            if(exp.isFraction()) {
                decomposedElements = new Vector();
                AlgExp numerator;
                AlgExp denominator;

                if(exp.isConstant()) {
                    ConstantFraction f = (ConstantFraction)exp;
                    numerator=f.getNumerator();
                    denominator=f.getDenominator();
                } else {
                    ComplexFraction f=(ComplexFraction)exp;
                    numerator=f.getNumerator();
                    denominator=f.getDenominator();
                }
                decomposedElements.add(numerator.toString());
                decomposedElements.add(denominator.toString());
            }
            
        } catch(ExpParseException e) {
            e.printStackTrace();
        }

        return decomposedElements;
    }
}
