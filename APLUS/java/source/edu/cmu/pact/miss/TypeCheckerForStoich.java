//package Bootstrapping.Stoichiometry;
package edu.cmu.pact.miss;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.stoichiometry.StoFeatPredicate;

public class TypeCheckerForStoich {

    //these are the names of the reasons
    public static String[] validReasonNames = {
        "Given Value", "Unit Conversion", "Molecular Weight", "Avogadro's Number",
        "Composition Stoichiometry", "Solution Concentration"
    };

    public static String[] validUnitNames = {
        "atoms", "cm3", "fl oz", "g", "gal", "kg", "kL", "km3",
        "L", "lb", "mg", "mL", "mmol", "mol", "molecules", "$",
        "{mu}g" //the "{mu}" needs to be replaced with the Greek letter mu.
    };

    public static String[] validSubstanceNames = {
        "As", "aspirin", "AsO2-", "Ba", "blood", "C", "Canadian", 
        "C2H6", 
        "C6H12O6", "CCl4", "Cl", "COH4", "coke", "ethanol", "Fe",
        "FeCl2", "Fe2O3", "fertilizer", "furan", "H", "H2", "H2O", "hemoglobin", "KCl", "MgSO4",
        "milk", "Na+", "Na3PO4", "Na2HPO4", "NaCl", "NH3", "O2", "O", "P4O10", "petrol",
        "potatoes", "sea water", "solution", "splenda", "syrup", "U.S.", "YBa2Cu3O7"
    };

    public static boolean isValidReason(String s) {
        for(int i=0; i < validReasonNames.length; i++)
            if (s.equalsIgnoreCase(validReasonNames[i]))
                return true;
        return false;
    }

    public static boolean isNumber(String s){
        return EqFeaturePredicate.isFloatingPointNumber(s);
    }

    public static boolean isValidUnit(String s){
        if (s.charAt(s.length()-1)=='g')
            return true;

        for(int i=0; i < validUnitNames.length; i++)
            if (s.equalsIgnoreCase(validUnitNames[i]))
                return true;
        return false;
    }

    public static boolean isSubstance(String s){
        for(int i=0; i < validSubstanceNames.length; i++)
            if (s.equalsIgnoreCase(validSubstanceNames[i]))
                return true;
        return false;
    }

    public static boolean isCancel(String s){
        return s.equalsIgnoreCase(": true");
    }

    public static Integer valueType(String value){
        int valueType = 0;
        if (isValidReason(value)) {
            valueType = StoFeatPredicate.TYPE_REASON;
        } else if (isNumber(value)) {
            valueType = StoFeatPredicate.TYPE_ARITH_EXP;
        } else if (isValidUnit(value)) {
            valueType = StoFeatPredicate.TYPE_UNIT;
        } else if (isSubstance(value)) { 
            valueType = StoFeatPredicate.TYPE_SUBSTANCE;
        } else if (isCancel(value)) {
            valueType = StoFeatPredicate.TYPE_CANCEL;
        }
        return new Integer(valueType);
    }
}
