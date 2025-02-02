package ai.knowlej.PropositionalLogic.Models.LogicModels;

import java.util.Map;

import ai.knowlej.Exceptions.InvalidExpressionException;
import ai.knowlej.PropositionalLogic.Logic.Proposition;

public abstract class ModelAbstract {
    public abstract String getModelName();

    public abstract Proposition getProposition();

    public abstract String[][] getTruthTable() throws InvalidExpressionException;

    public abstract String getExpression();

    public abstract char[] getOperands();

    public abstract char getOperand(int index);

    public abstract Map<Character, String> getOperandSymbolicRepresentation();

    public abstract char[] getAllPredicateCharValues();

    public abstract boolean[] getAllPredicateBooleanValues();

    public abstract String getValidityEvaluation();

    public abstract String getSymbolicRepresentation();

    public abstract Boolean getPredicateEvaluation();
}
