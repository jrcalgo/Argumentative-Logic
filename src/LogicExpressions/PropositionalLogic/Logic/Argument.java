package src.LogicExpressions.PropositionalLogic.Logic;

import java.rmi.StubNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import src.DataStructures.DirectedDeductionGraph;
import src.DataStructures.DeductionGraphNode;

import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicExpressions.PropositionalLogic.Models.*;

public class Argument<M extends Model> {
    private M[] knowledgeBase;
    private ArrayList<ArrayList<String>> trueKBModels;

    private char[] operands;
    private byte operandCount;

    private String[][] allTruthTable;
    private Boolean[][] allTruthValues;

    // private String[][] currentTruthTable;
    // private Boolean[][] currentTruthValues;

    public Argument(M[] knowledgeBase) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        validateKnowledgeBase(knowledgeBase);
        setTruthTable();
    }

    private void validateKnowledgeBase(M[] kb) {
        if (kb == null || kb.length == 0)
            throw new IllegalArgumentException("Knowledge base cannot be null or empty.");

        StringBuilder operandString = new StringBuilder();
        for (M m : kb) {
            char[] modelOperands = m.getOperands();
            for (int j = 0; j < modelOperands.length; j++) {
                if (operandString.indexOf(String.valueOf(modelOperands[j])) == -1) {
                    operandString.append(modelOperands[j]);
                }
            }
        }
        if (operandString.length() > 15)
            throw new IllegalArgumentException("Too many total operands in knowledge base; only 15 total allowed.");

        this.operands = operandString.toString().toCharArray();
        this.operandCount = (byte) operandString.length();
        this.knowledgeBase = kb;
    }

    private void setTruthTable() throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        int boolRowsCount = (int) Math.pow(2, this.operandCount);
        int boolColsCount = this.operandCount + this.knowledgeBase.length + 1;

        // set all truth table
        TruthTableBuilder ttb = new TruthTableBuilder(this.operands, boolRowsCount, boolColsCount);
        this.allTruthTable = ttb.getTruthTable();
        this.allTruthValues = ttb.getValueTable();
        ttb.close();

        for (int i = operandCount, j = 0; j < this.knowledgeBase.length; i++, j++)
            this.allTruthTable[0][i] = this.knowledgeBase[j].getExpression();
        
        this.allTruthTable[0][boolColsCount-1] = "KB";

        this.trueKBModels = new ArrayList<>();
        ArrayList<String> titleRow = new ArrayList<>();
        for (int i = 0; i < boolColsCount; i++)
            titleRow.add(this.allTruthTable[0][i]);

        this.trueKBModels.add(titleRow);
        titleRow = null;
        HashMap<Character, Character> valueMap = new HashMap<>();
        boolean[] modelEvaluations = new boolean[this.knowledgeBase.length];
        ArrayList<String> trueKBModelPlaceholder;
        for (int rows = 0; rows < boolRowsCount; rows++) {
            // setting base table values
            for (int i = 0; i < operandCount; i++)
                valueMap.put(operands[i], this.allTruthTable[rows + 1][i].charAt(0));

            for (int i = 0; i < this.knowledgeBase.length; i++) {
                Proposition p = new Proposition(this.knowledgeBase[i].getExpression());
                this.allTruthValues[rows][operandCount+i] = p.evaluateExpression(valueMap);
                this.allTruthTable[rows+1][operandCount+i] = this.allTruthValues[rows][operandCount+i] ? "T" : "F";
            }
            valueMap.clear();
            
            // setting KB table values, including KB evaluation(s)
            int i = 0;
            while (operandCount+i < allTruthValues[rows].length-1) {
                modelEvaluations[i] = this.allTruthValues[rows][operandCount+i];
                i++;
            }
            this.allTruthValues[rows][boolColsCount-1] = evaluateKnowledgeBase(modelEvaluations);
            this.allTruthTable[rows+1][boolColsCount-1] = this.allTruthValues[rows][boolColsCount-1] ? "T" : "F";

            if (allTruthValues[rows][boolColsCount-1]) {
                trueKBModelPlaceholder = new ArrayList<>();
                for (int j = 0; j < boolColsCount; j++)
                    trueKBModelPlaceholder.add(this.allTruthValues[rows][j] ? "T" : "F");

                this.trueKBModels.add(trueKBModelPlaceholder);
            }
        }
    }

    private boolean evaluateKnowledgeBase(boolean[] modelEvaluations) {
        boolean rowAnswer = true;
        for (int i = 0; i < modelEvaluations.length; i++) 
            rowAnswer = rowAnswer && modelEvaluations[i];
        
        return rowAnswer;
    }

    public String checkAllTTModels(String query) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (query == null || query.length() == 0)
            throw new IllegalArgumentException("String query cannot be null or empty.");
        if (query.contains(",") && !query.startsWith(",") && !query.endsWith(",")) {
            String[] queries = query.split(",");
            StringBuilder answer = new StringBuilder();
            for (String q : queries) {
                answer.append(checkAllTTModels(new Proposition(q)) + ", ");
            }
            return answer.toString().substring(0, answer.length()-2);
        }
        return checkAllTTModels(new Proposition(query));
    }

    public String checkAllTTModels(Proposition query) throws InvalidExpressionException {
        if (query == null)
            throw new IllegalArgumentException("Proposition query cannot be null or empty.");

        ArrayList<String> qOperands = query.getSentences(0, query.getOperandCount()-1);
        boolean commonOperand = false;
        for (String qOp : qOperands) {
            for (char op : this.operands) {
                if (qOp.charAt(0) == op) {
                    commonOperand = true;
                    continue;
                }
            }
            if (commonOperand)
                continue;
        }
        if (!commonOperand)
            throw new IllegalArgumentException("No common operand found in query when compared with knowledge base.");
        
        String answer = null;
        ArrayList<Boolean> queryValues = new ArrayList<>();
        HashMap<Character, Character> valueMap = new HashMap<>();
        int valueRows = 1;
        do {
            for (int i = 0; i < operandCount; i++) {
                valueMap.put(this.operands[i], this.trueKBModels.get(valueRows).get(i).charAt(0));
            }
            queryValues.add(query.evaluateExpression(valueMap));
            if (queryValues.get(valueRows-1))
                answer = "True";
            else
                answer = "False";
            
            if (valueRows > 1 && queryValues.get(valueRows-1) != queryValues.get(valueRows-2)) {
                return "Uncertain";
            }
            valueRows++;
        } while (valueRows <= this.trueKBModels.size()-1);

        return answer;
    }


    // public String checkCurrentTTModels(String query) {
    //     if (query == null)
    //         throw new IllegalArgumentException("String query cannot be null or empty.");

    //     return checkCurrentTTModels(new Proposition(query));
    // }

    // public String checkCurrentTTModels(Proposition query) {
    //     if (query == null)
    //         throw new IllegalArgumentException("Proposition query cannot be null");
        
    //     ArrayList<String> qOperands = query.getSentences(0, query.getOperandCount()-1);
    //     boolean commonOperand = false;
    //     for (String qOp : qOperands) {
    //         for (char op : this.operands) {
    //             if (qOp.charAt(0) == op) {
    //                 commonOperand = true;
    //                 continue;
    //             }
    //         }
    //         if (commonOperand)
    //             continue;
    //     }
    //     if (!commonOperand)
    //         throw new IllegalArgumentException("No common operand found in query when compared with knowledge base.");

    // }

    public ArrayList<ArrayList<String>> deduce(String query) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (query == null || query.length() == 0)
            throw new IllegalArgumentException("String query cannot be null or empty.");
        if (query.contains(",") && !query.startsWith(",") && !query.endsWith(",")) {
            String[] queries = query.split(",");
            ArrayList<ArrayList<String>> answer = new ArrayList<>();
            for (String q : queries) {
                answer.addAll(deduce(new Proposition(q)));
            }
            return answer;
        }
        return deduce(new Proposition(query));
    }

    public ArrayList<ArrayList<String>> deduce(Proposition query) {
        if (query == null)
            throw new IllegalArgumentException("Proposition query cannot be null or empty.");
        
        ArrayList<String> qOperands = query.getSentences(0, query.getOperandCount()-1);
        boolean commonOperand = false;
        for (String qOp : qOperands) {
            for (char op : this.operands) {
                if (qOp.charAt(0) == op) {
                    commonOperand = true;
                    continue;
                }
            }
            if (commonOperand)
                continue;
        }
        if (!commonOperand)
            throw new IllegalArgumentException("No common operand found in query when compared with knowledge base.");

        String[] knowledgeExpressions = new String[this.knowledgeBase.length];
        for (int i = 0; i < this.knowledgeBase.length; i++) {
            knowledgeExpressions[i] = this.knowledgeBase[i].getExpression();
        }

        ArrayList<ArrayList<String>> deductionPaths = new ArrayList<>();

        for ()
        DeductionTree<String> dt = new DeductionTree<String>(knowledgeExpressions, query);
        return dt.search(query.getExpression());

    }

    public String[][] getAllTruthTable() {
        return this.allTruthTable;
    }

    public Boolean[][] getAllTableValues() {
        return this.allTruthValues;
    }

    public void printTruthTable() {
        System.out.println();
        for (int i = 0; i < allTruthTable[i].length; i++) {
            System.out.print(allTruthTable[0][i] + "\s\s\s");
        }
        System.out.println();
        for (int i = 1; i < allTruthTable.length; i++) {
            for (int j = 0; j < allTruthTable[i].length; j++) {
                System.out.print(allTruthTable[i][j] + "\s\s\s\s\s");
            }
            System.out.println();
        }
    }

    public void printTruthTable(int fromCol, int toCol) {
        if (fromCol > toCol)
            throw new IndexOutOfBoundsException(fromCol + " is out of bounds.");

        for (int i = 0; i < allTruthTable[i].length; i++) {
            System.out.print(i + ".\s");
            System.out.print(allTruthTable[0][i] + "\s\s\s");
        }

        for (int i = 1; i < allTruthTable.length; i++) {
            for (int j = fromCol; j < toCol; j++) {
                System.out.print(allTruthTable[i][j] + "\s\s\s\s\s");
            }
            System.out.println();
        }
    }

    public M[] getKnowledgeBaseModels() {
        return this.knowledgeBase;
    }

    public String[] getKnowledgeBaseExpressions() {
        String[] expressions = new String[this.knowledgeBase.length];
        for (int i = 0; i < this.knowledgeBase.length; i++) {
            expressions[i] = this.knowledgeBase[i].getExpression();
        }
        return expressions;
    }

    public Proposition[] getKnowledgeBasePropositions() {
        Proposition[] propositions = new Proposition[this.knowledgeBase.length];
        for (int i = 0; i < this.knowledgeBase.length; i++) {
            propositions[i] = this.knowledgeBase[i].getProposition();
        }
        return propositions;
    }

    public ArrayList<ArrayList<String>> getAllTrueKBModels() {
        return this.trueKBModels;
    }

    public void printAllTrueKBModels() {
        for (int i = 0; i < this.trueKBModels.size(); i++) {
            for (int j = 0; j < this.trueKBModels.get(i).size(); j++) {
                System.out.print(this.trueKBModels.get(i).get(j) + "\s");
            }
            System.out.println();
        }
    }

    /* Used for constructing argumentative inference */
    static class InferenceLaws {

        private final Map<String, String> inferenceLaws = new HashMap<>() {
            {
                put("ModusPonens", "P, P->Q entails Q");
            }
        };

        public InferenceLaws() {
            super();
        }


        public ArrayList<String> checkInferenceLaws(Argument arg) {
            Proposition[] kbPropositions = arg.getKnowledgeBasePropositions();
            String[] kbConversions = new String[kbPropositions.length]
            for (int i = 0; i < kbPropositions.length; i++) {
                kbConversions[i] = e.getConvertedExpression();
            }

            int iterations = 0;
            ArrayList<String> applicableLaws = new ArrayList<>();
            while (true) {
                if ()
            }
                
        }

        /* Rules of Argument Inference */

        /**
         * Rule: P, P->Q entails Q
         * @param cE
         * @return
         */
        private String modusPonens(String cE) {
            if
        }

        /**
         * Rule: ~Q, P->Q entails ~P
         * @param cE
         * @return
         */
        private String modusTollens(String cE) {
            if
        }

        /**
         * Rule: P entails P|Q
         * @param cE
         * @return
         */
        private String addition(String cE) {
            if
        }

        /**
         * Rule: P&Q entails P
         * @param cE
         * @return
         */
        private String simplification(String cE) {
            if
        }

        /**
         * Rule: P, Q entails P&Q
         * @param cE
         * @return
         */
        private String conjunction(String cE) {
            if
        }

        /**
         * Rule: P->Q, Q->R entails P->R
         * @param cE
         * @return
         */
        private String hypotheticalSyllogism(String cE) {
            if
        }

        /**
         * Rule: P|Q, ~P entails Q
         * @param cE
         * @return
         */
        private String disjunctiveSyllogism(String cE) {
            if
        }

        /**
         * Rule: P|Q, ~P|R entails Q|R
         * this one is powerful
         * @param cE
         * @return
         */
        private String resolution(String cE) {
            if
        }
    }

    /**
     * Popular logic equivalencies used for inference and argumentation; propositional logic inference
     */
    static class LogicalEquivalencyLaws {

        private final Map<String, String> equivalencyLaws = new HashMap<>() {
            {

            }
        }

        public LogicalEquivalencyLaws() {
            super();
        }

        public ArrayList<String> checkEquivalencyLaws(Proposition p) {
            String cE = p.getConvertedExpression();

            ArrayList<String> applicableLaws = new ArrayList<>();
            // parse cE and store similar substrings (if any) into variables to then construct applicable rules. This will use key-value mapping, and then
            // using those mapped values to evaluate applicable rules, i.e. P|Q is P, therefore (P|Q)|(P|Q) => P|Q or, alternatively, P|P => P.
            // also check if there are any operand propositional equivalencies as well.

            /* cE preprocessing,
             * First, cycle through cE and check for similar substrings, and if found, map them to a new operand in . 
             * 
             */

            /* For each law,
             * First, cycle through cE and check if there are any operand equivalencies. If so, evaluate and store result in applicableLaws.
             * Second, cycle through cE and check for similar substrings, and map them to operand if found. 
             * Third, replace similar substrings in cE with mapped operands.
             * Fourth, 
             * Then evaluate and store result in applicableLaws.
             * Third, return applicableLaws.
             */
        }

        /**
         * Rule: [P|P => P] OR [P&P => P]
         * @throws InvalidLogicOperatorException
         * @throws InvalidOperandException
         * @throws InvalidExpressionException
         */
        private String idempotentLaw(String cE)
                throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
            Proposition[] iL = new Proposition[this.mOperands.length];
            for (int i = 0; i < this.mOperands.length; i++) {
                if (this.cE.contains(this.mOperands[i] + "a" + this.mOperands[i])
                        || this.cE.contains(this.mOperands[i] + "o" + this.mOperands[i])) {
                    iL[i] = new Proposition(this.mOperands[i] + "");
                } else
                    continue;
            }
            return iL;
        }

        /**
         * Rule: [(P|Q)|R => P|(Q|R)] OR [(P&Q)&R => P&(Q&R)]
         * @param cE
         * @return
         * @throws InvalidExpressionException
         * @throws InvalidOperandException
         * @throws InvalidLogicOperatorException
         */
        private String associativeLaw(String cE) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
            Proposition[] aL = new Proposition[this.mOperands.length];
            for (int i = 0; i < this.mOperands.length; i++) {
                for (int j = 0; j < this.mOperands.length; j++) {
                    for (int k = 0; k < this.mOperands.length; k++) {
                        if (this.cE.contains(this.mOperands))
                    }
                }
                if (this.cE.contains(this.mOperands[i] + "a" + this.mOperands[i]) || this.cE.contains(this.mOperands[i] + "o" + this.mOperands[i])) {
                    aL[i] = new Proposition(this.mOperands[i] + "");
                } else
                    continue;
            }
            return aL;
        }

        /**
         * Rule: [P|Q => Q|P] OR [P&Q => Q&P]
         * @param cE
         * @return
         * @throws InvalidExpressionException
         * @throws InvalidOperandException
         * @throws InvalidLogicOperatorException
         */
        private String commutativeLaw(String cE)
                throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
            Proposition[] cL = new Proposition[this.mOperands.length];
            for (int i = 0; i < this.mOperands.length; i++) {
                if (this.cE.contains(this.mOperands[i] + "a" + this.mOperands[i])
                        || this.cE.contains(this.mOperands[i] + "o" + this.mOperands[i])) {
                    cL[i] = new Proposition(this.mOperands[i] + "");
                } else
                    continue;
            }
            return cL;
        }

        /**
         * Rule: [P|(Q&R) => (P|Q)&(P|R)] OR [P&(Q|R) => (P&Q)|(P&R)]
         * @param cE
         * @return
         * @throws InvalidExpressionException
         * @throws InvalidOperandException
         * @throws InvalidLogicOperatorException
         */
        private String distributiveLaw(String cE)
                throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
            Proposition[] iL = new Proposition[this.mOperands.length];
            for (int i = 0; i < this.mOperands.length; i++) {
                if (this.cE.contains(this.mOperands[i] + "a" + this.mOperands[i])
                        || this.cE.contains(this.mOperands[i] + "o" + this.mOperands[i])) {
                    iL[i] = new Proposition(this.mOperands[i] + "");
                } else
                    continue;
            }
            return iL;
        }

        /**
         * Rule: [P|F => P] OR [P&T => P]
         * @param cE
         * @return
         * @throws InvalidExpressionException
         * @throws InvalidOperandException
         * @throws InvalidLogicOperatorException
         */
        private String identityLaw(String cE)
                throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
            Proposition[] iL = new Proposition[this.mOperands.length];
            for (int i = 0; i < this.mOperands.length; i++) {
                if (this.cE.contains(this.mOperands[i] + "oF") || (this.cE.contains(this.mOperands[i] + "aT"))) {
                    iL[i] = new Proposition(this.mOperands[i] + "");
                } else
                    continue;
            }
            return iL;
        }

        /**
         * Rule: [P&F => F] OR [P|T => T]
         * @param cE
         * @return
         * @throws InvalidExpressionException
         * @throws InvalidOperandException
         * @throws InvalidLogicOperatorException
         */
        private String dominationLaw(String cE)
                throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
            Proposition[] iL = new Proposition[this.mOperands.length];
            for (int i = 0; i < this.mOperands.length; i++) {
                if (this.cE.contains(this.mOperands[i] + "aF")) {
                    iL[i] = new Proposition("F");
                } else if (this.cE.contains(this.mOperands[i] + "oT")) {
                    iL[i] = new Proposition("T");
                }
                continue;
            }
            return iL;
        }

        /**
         * Rule: [~~P => P]
         * @param cE
         * @return
         * @throws InvalidExpressionException
         * @throws InvalidOperandException
         * @throws InvalidLogicOperatorException
         */
        private String doubleNegationLaw(String cE)
                throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
            Proposition[] dNL = new Proposition[this.mOperands.length];
            for (int i = 0; i < this.mOperands.length; i++) {
                if (this.cE.contains("nn" + this.mOperands[i])) {
                    dNL[i] = new Proposition(this.mOperands[i] + "");
                } else
                    continue;
            }
            return dNL;
        }

        /**
         * Rule: [P&~P => F, ~T => F] OR [P|~P => T, ~F => T]
         * @param cE
         * @return
         * @throws InvalidExpressionException
         * @throws InvalidOperandException
         * @throws InvalidLogicOperatorException
         */
        private String complementLaw(String cE)
                throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
            Proposition[] iL = new Proposition[this.mOperands.length];
            for (int i = 0; i < this.mOperands.length; i++) {
                if (this.cE.contains(this.mOperands[i] + "an" + this.mOperands[i])) {
                    iL[i] = new Proposition("F");
                } else if (this.cE.contains(this.mOperands[i] + "on" + this.mOperands[i])) {
                    iL[i] = new Proposition("T");
                }
                continue;
            }

            for (int i = 0; i < this.cE.length(); i++) {
                if (this.cE.charAt(i) == 'n' && this.cE.charAt(i + 1) == 'F') {
                    iL[i] = new Proposition("F");
                } else if (this.cE.charAt(i) == 'n' && this.cE.charAt(i + 1) == 'T') {
                    iL[i] = new Proposition("T");
                } else
                    break;
            }
            return iL;
        }

        /**
         * Rule: [~(P|Q) => ~P & ~Q] OR [~(P&Q) => ~P | ~Q]
         * @param cE
         * @return
         */
        private String deMorgansLaw(String cE) {
            Proposition[] iL = new Proposition[this.mOperands.length];
            for (int i = 0; i < this.mOperands.length; i++) {
                if (this.cE.contains(this.mOperands[i] + "a" + this.mOperands[i])
                        || this.cE.contains(this.mOperands[i] + "o" + this.mOperands[i])) {
                    iL[i] = new Proposition(this.mOperands[i] + "");
                } else
                    continue;
            }
            return iL;
        }

        /**
         * Rule: [P|(P&Q) => P] OR [P&(P|Q) => P]
         * @param cE
         * @return
         */
        private String absorptionLaw(String cE) {
            Proposition[] iL = new Proposition[this.mOperands.length];
            for (int i = 0; i < this.mOperands.length; i++) {
                if (this.cE.contains(this.mOperands[i] + "a" + this.mOperands[i])
                        || this.cE.contains(this.mOperands[i] + "o" + this.mOperands[i])) {
                    iL[i] = new Proposition(this.mOperands[i] + "");
                } else
                    continue;
            }
            return iL;
        }

        /**
         * Rule: [P->Q => ~P|Q] OR [P<>Q => (P->Q)&(Q->P)]
         * @param cE
         * @return
         */
        private String conditionalIdentity(String cE) {
            Proposition[] iL = new Proposition[this.mOperands.length];
            for (int i = 0; i < this.mOperands.length; i++) {
                if (this.cE.contains(this.mOperands[i] + "a" + this.mOperands[i])
                        || this.cE.contains(this.mOperands[i] + "o" + this.mOperands[i])) {
                    iL[i] = new Proposition(this.mOperands[i] + "");
                } else
                    continue;
            }
            return iL;
        }
    }
}
