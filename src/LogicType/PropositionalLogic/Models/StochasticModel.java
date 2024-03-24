package src.LogicType.PropositionalLogic.Models;

import java.util.Map;
import java.util.Stack;

import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicType.PropositionalLogic.Logic.Proposition;
import src.LogicType.PropositionalLogic.Logic.Validity;

public class StochasticModel extends Model {
    private String modelName;
    private Proposition expression;

    private char[] operands;
    private Map<Character, Double> defaultOperandProbabilityValues;
    private Map<Character, Character> defaultOperandTruthValues;

    private double defaultTruthThreshold;
    private Map<Character, Double> operandTruthThresholds;

    private String predicateModel;
    private boolean predicateEvaluation;
    private double[] allPredicateProbabilityValues;
    private boolean[] allPredicateBooleanValues;
    private String validityEvaluation;

    private String symbolRepresentation;
    private Map<Character, String> operandSymbolicRepresentation;

    public StochasticModel(String modelName, String expression) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty()) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);
    }

    public StochasticModel(String modelName, Proposition expression) {
        if (expression == null) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;
    }

    public StochasticModel(String modelName, String expression, Map<Character, String> operandSymbolicRepresentation) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty()) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);
    }

    public StochasticModel(String modelName, Proposition expression, Map<Character, String> operandSymbolicRepresentation) {
        if (expression == null) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);
    }

    public StochasticModel(String modelName, String expression, Map<Character, String> operandSymbolicRepresentation, double defaultTruthThreshold, Map<Character, Double> defaultOperandProbabilityValues) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty()) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);

        setOperands(defaultTruthThreshold, defaultOperandProbabilityValues, null);
        this.defaultOperandProbabilityValues = defaultOperandProbabilityValues;
        this.defaultTruthThreshold = defaultTruthThreshold;

        setPredicateProbabilityString(this.defaultOperandProbabilityValues);
        setPredicateBooleanString(this.defaultOperandProbabilityValues, defaultTruthThreshold, null);
        this.predicateEvaluation = this.expression.evaluateExpression(defaultOperandTruthValues);
        setAllPredicateValues();
        setValidityEvaluation();
    }

    public StochasticModel(String modelName, Proposition expression, Map<Character, String> operandSymbolicRepresentation, double defaultTruthThreshold, Map<Character, Double> defaultOperandProbabilityValues) throws InvalidExpressionException {
        if (expression == null) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);

        setOperands(defaultTruthThreshold, defaultOperandProbabilityValues, null);
        this.defaultOperandProbabilityValues = defaultOperandProbabilityValues;
        this.defaultTruthThreshold = defaultTruthThreshold;

        setPredicateProbabilityString(this.defaultOperandProbabilityValues);
        setPredicateBooleanString(this.defaultOperandProbabilityValues, defaultTruthThreshold, null);
        this.predicateEvaluation = this.expression.evaluateExpression(defaultOperandTruthValues);
        setAllPredicateValues();
        setValidityEvaluation();
    }
    
    public StochasticModel(String modelName, String expression, Map<Character, String> operandSymbolicRepresentation, double defaultTruthThreshold, Map<Character, Double> defaultOperandProbabilityValues, Map<Character, Double> operandTruthThresholds) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty()) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);

        setOperands(defaultTruthThreshold, defaultOperandProbabilityValues, operandTruthThresholds);
        this.defaultOperandProbabilityValues = defaultOperandProbabilityValues;
        this.defaultTruthThreshold = defaultTruthThreshold;

        setPredicateProbabilityString(this.defaultOperandProbabilityValues);
        setPredicateBooleanString(this.defaultOperandProbabilityValues, defaultTruthThreshold, operandTruthThresholds);
        this.predicateEvaluation = this.expression.evaluateExpression(defaultOperandTruthValues);
        setAllPredicateValues();
        setValidityEvaluation();
    }

    public StochasticModel(String modelName, Proposition expression, Map<Character, String> operandSymbolicRepresentation, double defaultTruthThreshold, Map<Character, Double> defaultOperandProbabilityValues, Map<Character, Double> operandTruthThresholds) throws InvalidExpressionException {
        if (expression == null) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);

        setOperands(defaultTruthThreshold, defaultOperandProbabilityValues, operandTruthThresholds);
        this.defaultOperandProbabilityValues = defaultOperandProbabilityValues;
        this.defaultTruthThreshold = defaultTruthThreshold;

        setPredicateProbabilityString(this.defaultOperandProbabilityValues);
        setPredicateBooleanString(this.defaultOperandProbabilityValues, defaultTruthThreshold, operandTruthThresholds);
        this.predicateEvaluation = this.expression.evaluateExpression(defaultOperandTruthValues);
        setAllPredicateValues();
        setValidityEvaluation();
    }

    private void setOperands(double defaultTruthThreshold, Map<Character, Double> defaultOperandProbabilityValues, Map<Character, Double> operandTruthThresholds) {
        operands = new char[this.expression.getOperandCount()];
        for (int i = 0; i < this.expression.getOperandCount(); i++) {
            String operand = this.expression.getSentence(i);
            operands[i] = operand.charAt(0);
        }
        for (int i = 0; i < operands.length; i++) {
            if (!defaultOperandProbabilityValues.containsKey(operands[i]))
                throw new IllegalArgumentException(operands[i] + " not in expression.");
        }
    }

    private void setPredicateProbabilityString(Map<Character, Double> defaultOperandProbabilityValues) {

    }

    private void setPredicateBooleanString(Map<Character, Double> defaultOperandProbabilityValues, double defaultTruthThreshold, Map<Character, Double> operandTruthThresholds) {
            
    }

    private void setAllPredicateValues() {

    }

    private void setValidityEvaluation() {
        Validity validity = new Validity();

        if (validity.isTautology(this.allPredicateBooleanValues))
            this.validityEvaluation = "Tautology";
        else if (validity.isContradiction(this.allPredicateBooleanValues))
            this.validityEvaluation = "Contradiction";
        else if (validity.isContingency(this.allPredicateBooleanValues))
            this.validityEvaluation = "Contingency";
        else
            this.validityEvaluation = null;
    }

    private void setSymbolicString(Map<Character, String> operandSymbolicRepresentation) {
                this.symbolRepresentation = this.expression.getConvertedExpression();

        Stack<Integer> parenthesesStack = new Stack<>();
        for (int i = 0; i < this.symbolRepresentation.length(); i++) {
            if (Character.isDigit(this.symbolRepresentation.charAt(i))
                    && !parenthesesStack.contains(this.symbolRepresentation.charAt(i) - '0')) {
                parenthesesStack.push(this.symbolRepresentation.charAt(i) - '0');
                this.symbolRepresentation = this.symbolRepresentation
                        .replaceFirst(this.symbolRepresentation.charAt(i) + "", '(' + "");
            } else if (parenthesesStack.contains(this.symbolRepresentation.charAt(i) - '0')) {
                parenthesesStack.pop();
                this.symbolRepresentation = this.symbolRepresentation
                        .replaceFirst(this.symbolRepresentation.charAt(i) + "", ')' + "");
            }
        }
        parenthesesStack.clear();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.symbolRepresentation.length(); i++) {
            if (this.operandSymbolicRepresentation.containsKey(this.symbolRepresentation.charAt(i))) {
                if ((i+1 < this.symbolRepresentation.length()) && this.symbolRepresentation.charAt(i + 1) == ')') {
                    sb.append("'" + this.operandSymbolicRepresentation.get(this.symbolRepresentation.charAt(i)) + "'");
                } else
                    sb.append("'" + this.operandSymbolicRepresentation.get(this.symbolRepresentation.charAt(i)) + "' ");
            } else {
                switch (this.symbolRepresentation.charAt(i)) {
                    case '(':
                        sb.append("(");
                        break;
                    case ')':
                        sb.append(") ");
                        break;
                    case 'n':
                        sb.append("not ");
                        break;
                    case 'a':
                        sb.append("and ");
                        break;
                    case 'o':
                        sb.append("or ");
                        break;
                    case 'x':
                        sb.append("xor ");
                        break;
                    case 'm':
                        sb.append("implies ");
                        break;
                    case 'i':
                        sb.append("iff ");
                        break;
                    default:
                        break;
                }
            }
        }

        this.symbolRepresentation = sb.toString();
    }

    @Override
    public String getModelName() {
        return this.modelName;
    }

    @Override
    public Proposition getProposition() {
        return this.expression;
    }

    @Override
    public String[][] getTruthTable() throws InvalidExpressionException {
        return this.expression.getTruthTable();
    }

    @Override
    public String getExpression() {
        return this.expression.getExpression();
    }

    @Override
    public char[] getOperands() {
        return this.operands;
    }

    @Override
    public char getOperand(int index) {
        if (index < 0 || index >= this.operands.length)
            throw new IllegalArgumentException("Operand index out of bounds");

        return this.operands[index];
    }

    
    public Map<Character, Double> getOperandTruthValues() {
        return this.defaultOperandProbabilityValues;
    }

    @Override
    public Map<Character, String> getOperandSymbolicRepresentation() {
        return this.operandSymbolicRepresentation;
    }

    @Override
    public double[] getAllPredicateTruthValues() {
        return this.allPredicateProbabilityValues;
    }

    @Override
    public boolean[] getAllPredicateBooleanValues() {
        return this.allPredicateBooleanValues;
    }

    @Override
    public String getValidityEvaluation() {
        return this.validityEvaluation;
    }

    @Override
    public String getPredicateModel() {
        return this.predicateModel;
    }

    @Override
    public String getSymbolicRepresentation() {
        return this.symbolRepresentation;
    }

    @Override
    public Boolean getPredicateEvaluation() {
        return this.predicateEvaluation;
    }
}
