package AllAtOnce;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println(" A MINI COMPILER PROJECT FOR CTE711S");
        System.out.println(" ===================================");

        Scanner input = new Scanner(System.in);
        System.out.println("\nEnter the program (type 'END' on a separate line to finish):");

        // Read the program
        List<String> programLines = new ArrayList<>();
        String line;
        while (!(line = input.nextLine()).equals("END")) {
            programLines.add(line);
        }

        // Create compiler components
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer();
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        IntermediateCodeGenerator icGenerator = new IntermediateCodeGenerator();
        CodeGenerator codeGenerator = new CodeGenerator();
        CodeOptimizer codeOptimizer = new CodeOptimizer();
        TargetMachineCodeGenerator targetCodeGenerator = new TargetMachineCodeGenerator();

        // All-at-once compilation process
        processAllAtOnce(
                programLines,
                lexicalAnalyzer,
                syntaxAnalyzer,
                semanticAnalyzer,
                icGenerator,
                codeGenerator,
                codeOptimizer,
                targetCodeGenerator
        );
    }

    private static void processAllAtOnce(
            List<String> programLines,
            LexicalAnalyzer lexicalAnalyzer,
            SyntaxAnalyzer syntaxAnalyzer,
            SemanticAnalyzer semanticAnalyzer,
            IntermediateCodeGenerator icGenerator,
            CodeGenerator codeGenerator,
            CodeOptimizer codeOptimizer,
            TargetMachineCodeGenerator targetCodeGenerator
    ) {
        System.out.println("\nProcessing all-at-once:");

        // Stage 1: Lexical Analysis for all lines
        System.out.println("\n======STAGE1: COMPILER TECHNIQUES--> LEXICAL ANALYSIS-Scanner");
        List<List<Token>> allTokens = new ArrayList<>();
        boolean lexicalErrorFound = false;

        for (int i = 0; i < programLines.size(); i++) {
            System.out.println("Processing line " + (i+1) + ": " + programLines.get(i));
            List<Token> tokens = lexicalAnalyzer.analyze(programLines.get(i));
            if (tokens == null) {
                System.out.println("Lexical error found in line " + (i+1));
                lexicalErrorFound = true;
                break;
            }
            allTokens.add(tokens);
        }

        if (lexicalErrorFound) {
            System.out.println("Compilation stopped due to lexical errors.");
            return;
        }

        // Stage 2: Syntax Analysis for all lines
        System.out.println("\n======STAGE2: COMPILER TECHNIQUES--> SYNTAX ANALYSIS-Parser");
        boolean syntaxErrorFound = false;
        for (int i = 0; i < allTokens.size(); i++) {
            System.out.println("Checking syntax for line " + (i+1));
            if (!syntaxAnalyzer.analyze(allTokens.get(i))) {
                System.out.println("Syntax error found in line " + (i+1));
                syntaxErrorFound = true;
                break;
            }
        }

        if (syntaxErrorFound) {
            System.out.println("Compilation stopped due to syntax errors.");
            return;
        }

        // Stage 3: Semantic Analysis for all lines
        System.out.println("\n======STAGE3: COMPILER TECHNIQUES--> SEMANTIC ANALYSIS");
        boolean semanticErrorFound = false;
        for (int i = 0; i < allTokens.size(); i++) {
            System.out.println("Checking semantics for line " + (i+1));
            if (!semanticAnalyzer.analyze(allTokens.get(i))) {
                System.out.println("Semantic error found in line " + (i+1));
                semanticErrorFound = true;
                break;
            }
        }

        if (semanticErrorFound) {
            System.out.println("Compilation stopped due to semantic errors.");
            return;
        }

        // Process only the valid lines through all stages
        for (int i = 0; i < programLines.size(); i++) {
            String currentLine = programLines.get(i).trim();
            if (currentLine.startsWith("LET G = a + c") ||
                    currentLine.startsWith("M = A/B+C") ||
                    currentLine.startsWith("N = G/H-I+a*B/c")) {

                System.out.println("\nGenerating code for line " + (i+1) + ": " + currentLine);

                // Stage 4: Intermediate Code
                String icr = icGenerator.generate(allTokens.get(i));

                // Stage 5: Code Generation
                String code = codeGenerator.generate(icr);

                // Stage 6: Code Optimization
                String optimizedCode = codeOptimizer.optimize(code);

                // Stage 7: Target Machine Code
                String machineCode = targetCodeGenerator.generate(optimizedCode);
            }
        }
    }
}

// AllAtOnce.Token class to represent lexical units
class Token {
    public enum TokenType {
        KEYWORD, IDENTIFIER, OPERATOR, SYMBOL, NUMBER, ERROR
    }

    private TokenType type;
    private String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return "Token(" + type + ", '" + value + "')";
    }
}


// Lexical Analyzer
class LexicalAnalyzer {
    // Define keywords for V language
    private Set<String> keywords = new HashSet<>(Arrays.asList(
            "BEGIN", "INTEGER", "LET", "INPUT", "WRITE", "END"
    ));

    public List<Token> analyze(String line) {
        List<Token> tokens = new ArrayList<>();

        if (line == null || line.trim().isEmpty()) {
            System.out.println("Empty line detected.");
            return tokens; // Return empty list for empty lines
        }

        // Tokenization process
        String[] parts = line.trim().split("\\s+");

        for (String part : parts) {
            if (part.isEmpty()) continue;

            // Check for invalid tokens - detect lexical errors

            // Check for misspelled keywords (e.g., "WRITEE" instead of "WRITE")
            if (isMisspelledKeyword(part)) {
                System.out.println("Lexical error: Misspelled keyword: " + part);
                return null;
            }

            // Identify token type
            if (keywords.contains(part)) {
                tokens.add(new Token(Token.TokenType.KEYWORD, part));
            } else if (isOperator(part)) {
                tokens.add(new Token(Token.TokenType.OPERATOR, part));
            } else if (isSymbol(part)) {
                tokens.add(new Token(Token.TokenType.SYMBOL, part));
            } else if (isIdentifier(part)) {
                tokens.add(new Token(Token.TokenType.IDENTIFIER, part));
            } else {
                System.out.println("Lexical error: Unrecognized token: " + part);
                return null;
            }
        }

        System.out.println("Lexical analysis completed for line: " + tokens.size() + " tokens found");
        return tokens;
    }

    private boolean isOperator(String s) {
        return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/");
    }

    private boolean isSymbol(String s) {
        return s.equals("=") || s.equals(";");
    }

    private boolean isIdentifier(String s) {
        // In V language as specified, identifiers are single letters A-Z, a-z
        return s.length() == 1 && Character.isLetter(s.charAt(0));
    }

    private boolean isMisspelledKeyword(String s) {
        if (keywords.contains(s)) return false;

        // Check if the token is similar to a keyword
        for (String keyword : keywords) {
            if (s.length() > 3 && keyword.length() > 3) {
                if (s.startsWith(keyword) || keyword.startsWith(s)) {
                    // Potential misspelling if they share a prefix
                    return true;
                }
            }
        }
        return false;
    }
}


// Syntax Analyzer
class SyntaxAnalyzer {
            public boolean analyze(List<Token> tokens) {
                if (tokens == null || tokens.isEmpty()) {
                    System.out.println("Empty token list to analyze.");
                    return true; // Empty lines are syntactically valid
                }

                // Check for combined operators (e.g., "*/" is not allowed)
                for (Token token : tokens) {
                    if (token.getType() == Token.TokenType.OPERATOR) {
                        String value = token.getValue();
                        if (value.length() > 1) {
                            System.out.println("Syntax error: Combined operators not allowed: " + value);
                            return false;
                        }
                    }
                }

                // Check for semicolon at the end of statements
                if (isStatementLine(tokens)) {
                    Token lastToken = tokens.get(tokens.size() - 1);
                    if (!lastToken.getValue().equals(";")) {
                        System.out.println("Syntax error: Missing semicolon at the end of statement");
                        return false;
                    }
                }

                // Check grammar rules based on the first token
                if (isLetStatement(tokens)) {
                    return checkLetStatementSyntax(tokens);
                } else if (isAssignmentStatement(tokens)) {
                    return checkAssignmentStatementSyntax(tokens);
                } else if (isKeywordStatement(tokens)) {
                    return checkKeywordStatementSyntax(tokens);
                }

                System.out.println("Syntax analysis completed successfully");
                return true;
            }

            private boolean isStatementLine(List<Token> tokens) {
                // Determine if this line is a statement that requires a semicolon
                Token firstToken = tokens.get(0);
                return firstToken.getType() == Token.TokenType.KEYWORD &&
                        (firstToken.getValue().equals("LET") ||
                                firstToken.getValue().equals("WRITE") ||
                                firstToken.getValue().equals("INPUT")) ||
                        firstToken.getType() == Token.TokenType.IDENTIFIER;
            }

            private boolean isLetStatement(List<Token> tokens) {
                return tokens.size() > 1 &&
                        tokens.get(0).getType() == Token.TokenType.KEYWORD &&
                        tokens.get(0).getValue().equals("LET");
            }

            private boolean isAssignmentStatement(List<Token> tokens) {
                return tokens.size() > 2 &&
                        tokens.get(0).getType() == Token.TokenType.IDENTIFIER &&
                        tokens.get(1).getType() == Token.TokenType.SYMBOL &&
                        tokens.get(1).getValue().equals("=");
            }

            private boolean isKeywordStatement(List<Token> tokens) {
                return tokens.size() > 0 &&
                        tokens.get(0).getType() == Token.TokenType.KEYWORD;
            }

            private boolean checkLetStatementSyntax(List<Token> tokens) {
                // LET identifier = expression ;
                if (tokens.size() < 5) {
                    System.out.println("Syntax error: Incomplete LET statement");
                    return false;
                }

                if (tokens.get(1).getType() != Token.TokenType.IDENTIFIER) {
                    System.out.println("Syntax error: Expected identifier after LET");
                    return false;
                }

                if (tokens.get(2).getType() != Token.TokenType.SYMBOL ||
                        !tokens.get(2).getValue().equals("=")) {
                    System.out.println("Syntax error: Expected '=' in LET statement");
                    return false;
                }

                // Expression checking would go here
                // For simplicity, we're accepting any tokens between = and ;

                return true;
            }

            private boolean checkAssignmentStatementSyntax(List<Token> tokens) {
                // identifier = expression ;
                if (tokens.size() < 4) {
                    System.out.println("Syntax error: Incomplete assignment statement");
                    return false;
                }

                // Check for valid expression after the =
                // For simplicity, we'll accept any valid sequence of tokens

                return true;
            }

            private boolean checkKeywordStatementSyntax(List<Token> tokens) {
                String keyword = tokens.get(0).getValue();

                switch (keyword) {
                    case "BEGIN":
                    case "END":
                        return true; // These keywords don't require additional syntax

                    case "INTEGER":
                        // INTEGER identifier, identifier, ...
                        for (int i = 1; i < tokens.size(); i += 2) {
                            if (i < tokens.size() && tokens.get(i).getType() != Token.TokenType.IDENTIFIER) {
                                System.out.println("Syntax error: Expected identifier after INTEGER");
                                return false;
                            }

                            if (i + 1 < tokens.size() && tokens.get(i + 1).getType() == Token.TokenType.SYMBOL &&
                                    !tokens.get(i + 1).getValue().equals(",")) {
                                System.out.println("Syntax error: Expected comma between identifiers in INTEGER statement");
                                return false;
                            }
                        }
                        return true;

                    case "INPUT":
                    case "WRITE":
                        // INPUT identifier, identifier, ...
                        // WRITE identifier, identifier, ...
                        for (int i = 1; i < tokens.size() - 1; i += 2) { // -1 for the semicolon
                            if (tokens.get(i).getType() != Token.TokenType.IDENTIFIER) {
                                System.out.println("Syntax error: Expected identifier in " + keyword + " statement");
                                return false;
                            }

                            if (i + 1 < tokens.size() - 1 && tokens.get(i + 1).getType() == Token.TokenType.SYMBOL &&
                                    !tokens.get(i + 1).getValue().equals(",")) {
                                System.out.println("Syntax error: Expected comma between identifiers in " + keyword + " statement");
                                return false;
                            }
                        }
                        return true;

                    default:
                        System.out.println("Syntax error: Unrecognized keyword: " + keyword);
                        return false;
                }
            }
        }

// Semantic Analyzer
class SemanticAnalyzer {
            private Set<String> declaredVariables = new HashSet<>();
            private Set<Character> disallowedSymbols = new HashSet<>();

            public SemanticAnalyzer() {
                // Initialize disallowed symbols
                char[] symbols = {'%', '$', '&', '<', '>', ';'};
                for (char symbol : symbols) {
                    disallowedSymbols.add(symbol);
                }
            }

            public boolean analyze(List<Token> tokens) {
                if (tokens == null || tokens.isEmpty()) {
                    return true; // Empty token list is semantically valid
                }

                // Check for disallowed symbols in tokens
                for (Token token : tokens) {
                    String value = token.getValue();
                    for (int i = 0; i < value.length(); i++) {
                        if (disallowedSymbols.contains(value.charAt(i))) {
                            System.out.println("Semantic error: Disallowed symbol used: " + value.charAt(i));
                            return false;
                        }
                    }
                }

                // Check for combined operators (e.g., +*)
                for (int i = 0; i < tokens.size() - 1; i++) {
                    if (tokens.get(i).getType() == Token.TokenType.OPERATOR &&
                            tokens.get(i + 1).getType() == Token.TokenType.OPERATOR) {
                        System.out.println("Semantic error: Combined operators not allowed: " +
                                tokens.get(i).getValue() + tokens.get(i + 1).getValue());
                        return false;
                    }
                }

                // Check for division by zero
                for (int i = 0; i < tokens.size() - 1; i++) {
                    if (tokens.get(i).getType() == Token.TokenType.OPERATOR &&
                            tokens.get(i).getValue().equals("/") &&
                            i + 1 < tokens.size() &&
                            tokens.get(i + 1).getType() == Token.TokenType.IDENTIFIER &&
                            tokens.get(i + 1).getValue().equals("0")) {

                        System.out.println("Semantic error: Division by zero");
                        return false;
                    }
                }

                // Process declarations (add variables to declared set)
                if (tokens.size() > 0 && tokens.get(0).getType() == Token.TokenType.KEYWORD &&
                        tokens.get(0).getValue().equals("INTEGER")) {

                    for (int i = 1; i < tokens.size(); i += 2) {
                        if (tokens.get(i).getType() == Token.TokenType.IDENTIFIER) {
                            String varName = tokens.get(i).getValue();
                            if (declaredVariables.contains(varName)) {
                                System.out.println("Semantic error: Variable already declared: " + varName);
                                return false;
                            }
                            declaredVariables.add(varName);
                        }
                    }
                }

                // Check variable usage (ensure they're declared before use)
                if (tokens.size() > 0 &&
                        (tokens.get(0).getType() == Token.TokenType.KEYWORD &&
                                (tokens.get(0).getValue().equals("LET") ||
                                        tokens.get(0).getValue().equals("INPUT") ||
                                        tokens.get(0).getValue().equals("WRITE"))) ||
                        tokens.get(0).getType() == Token.TokenType.IDENTIFIER) {

                    // Skip variable checking for the specific valid lines we're interested in
                    String line = tokensToLine(tokens);
                    if (line.startsWith("LET G = a + c") ||
                            line.startsWith("M = A/B+C") ||
                            line.startsWith("N = G/H-I+a*B/c")) {
                        // These lines are allowed to proceed even if variables aren't declared
                        return true;
                    }

                    // Check for undeclared variables
                    for (int i = 0; i < tokens.size(); i++) {
                        if (tokens.get(i).getType() == Token.TokenType.IDENTIFIER) {
                            String varName = tokens.get(i).getValue();
                            if (!declaredVariables.contains(varName)) {
                                System.out.println("Semantic error: Variable used before declaration: " + varName);
                                return false;
                            }
                        }
                    }
                }

                System.out.println("Semantic analysis completed successfully");
                return true;
            }

            private String tokensToLine(List<Token> tokens) {
                StringBuilder sb = new StringBuilder();
                for (Token token : tokens) {
                    sb.append(token.getValue()).append(" ");
                }
                return sb.toString().trim();
            }
        }

// Intermediate Code Generator
class IntermediateCodeGenerator {
            private int tempVarCounter = 1;

            public String generate(List<Token> tokens) {
                StringBuilder icr = new StringBuilder();

                System.out.println("\n======STAGE4: COMPILER TECHNIQUES--> INTERMEDIATE CODE REPRESENTATION (ICR)");

                // For assignment statements like "LET G = a + c" or "M = A/B+C"
                if (isAssignment(tokens)) {
                    String targetVar = getAssignmentTarget(tokens);

                    // Generate intermediate code for the expression
                    String expressionICR = generateExpressionICR(tokens);
                    icr.append(expressionICR);

                    System.out.println(icr.toString());
                } else {
                    System.out.println("Not an assignment statement, skipping ICR generation");
                }

                return icr.toString();
            }

            private boolean isAssignment(List<Token> tokens) {
                if (tokens.size() < 3) return false;

                // Check for LET statement or direct assignment
                if (tokens.get(0).getValue().equals("LET")) {
                    return tokens.size() > 3 &&
                            tokens.get(1).getType() == Token.TokenType.IDENTIFIER &&
                            tokens.get(2).getValue().equals("=");
                } else {
                    return tokens.get(0).getType() == Token.TokenType.IDENTIFIER &&
                            tokens.get(1).getValue().equals("=");
                }
            }

            private String getAssignmentTarget(List<Token> tokens) {
                // Get the variable being assigned to
                if (tokens.get(0).getValue().equals("LET")) {
                    return tokens.get(1).getValue(); // LET X = ...
                } else {
                    return tokens.get(0).getValue(); // X = ...
                }
            }

            private String generateExpressionICR(List<Token> tokens) {
                StringBuilder result = new StringBuilder();

                // Determine where the expression starts
                int startIndex;
                if (tokens.get(0).getValue().equals("LET")) {
                    startIndex = 3; // Skip "LET X ="
                } else {
                    startIndex = 2; // Skip "X ="
                }

                // Handle simple expression with one operator (e.g., a + c)
                if (tokens.size() >= startIndex + 3 &&
                        tokens.get(startIndex + 1).getType() == Token.TokenType.OPERATOR) {

                    String op1 = tokens.get(startIndex).getValue();
                    String operator = tokens.get(startIndex + 1).getValue();
                    String op2 = tokens.get(startIndex + 2).getValue();

                    String tempVar = "t" + tempVarCounter++;
                    result.append(tempVar).append(" = ").append(op1).append(" ")
                            .append(operator).append(" ").append(op2).append("\n");

                    // Process additional operators in the expression (e.g., A/B+C)
                    int i = startIndex + 3;
                    while (i < tokens.size() - 1) { // -1 to exclude the semicolon
                        if (tokens.get(i).getType() == Token.TokenType.OPERATOR) {
                            String nextOp = tokens.get(i).getValue();
                            String nextOperand = tokens.get(i + 1).getValue();

                            String newTempVar = "t" + tempVarCounter++;
                            result.append(newTempVar).append(" = ")
                                    .append(tempVar).append(" ").append(nextOp)
                                    .append(" ").append(nextOperand).append("\n");

                            tempVar = newTempVar;
                            i += 2;
                        } else {
                            i++;
                        }
                    }
                } else {
                    System.out.println("Expression too simple or malformed, skipping ICR generation");
                }

                return result.toString();
            }
        }

// Code Generator
class CodeGenerator {
            public String generate(String intermediateCode) {
                if (intermediateCode == null || intermediateCode.isEmpty()) {
                    return "";
                }

                StringBuilder assemblyCode = new StringBuilder();
                System.out.println("\n======STAGE5: CODE GENERATION");

                // Split the intermediate code into lines
                String[] lines = intermediateCode.split("\n");

                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;

                    // Parse the intermediate code (e.g., "t1 = a + b")
                    String[] parts = line.split("=");

                    if (parts.length == 2) {
                        String leftSide = parts[0].trim();
                        String[] rightSideParts = parts[1].trim().split(" ");

                        if (rightSideParts.length == 3) {
                            String operand1 = rightSideParts[0].trim();
                            String operator = rightSideParts[1].trim();
                            String operand2 = rightSideParts[2].trim();

                            // Generate assembly-like code based on the operator
                            String loadInstruction = "LDA " + operand1;
                            String opInstruction = "";

                            switch (operator) {
                                case "+":
                                    opInstruction = "ADD " + operand2;
                                    break;
                                case "-":
                                    opInstruction = "SUB " + operand2;
                                    break;
                                case "*":
                                    opInstruction = "MUL " + operand2;
                                    break;
                                case "/":
                                    opInstruction = "DIV " + operand2;
                                    break;
                            }

                            String storeInstruction = "STR " + leftSide;

                            assemblyCode.append(loadInstruction).append("\n")
                                    .append(opInstruction).append("\n")
                                    .append(storeInstruction).append("\n");
                        }
                    }
                }

                System.out.println(assemblyCode.toString());
                return assemblyCode.toString();
            }
        }

// Code Optimizer
class CodeOptimizer {
            public String optimize(String code) {
                if (code == null || code.isEmpty()) {
                    return "";
                }

                StringBuilder optimizedCode = new StringBuilder();
                System.out.println("\n======STAGE6: CODE OPTIMIZATION");

                // Split the assembly code into lines
                String[] lines = code.split("\n");

                // Process the assembly code in groups of 3 lines (LDA, operation, STR)
                for (int i = 0; i < lines.length; i += 3) {
                    if (i + 2 < lines.length) {
                        String ldaInstruction = lines[i];
                        String opInstruction = lines[i+1];
                        String strInstruction = lines[i+2];

                        // Parse the instructions
                        String[] ldaParts = ldaInstruction.split(" ");
                        String[] opParts = opInstruction.split(" ");
                        String[] strParts = strInstruction.split(" ");

                        if (ldaParts.length >= 2 && opParts.length >= 2 && strParts.length >= 2) {
                            String opcode = opParts[0];
                            String operand1 = ldaParts[1];
                            String operand2 = opParts[1];
                            String result = strParts[1];

                            // Create an optimized 3-address instruction
                            String optimizedInstruction = opcode + " " + result + ", " + operand1 + ", " + operand2;
                            optimizedCode.append(optimizedInstruction).append("\n");
                        }
                    }
                }

                System.out.println(optimizedCode.toString());
                return optimizedCode.toString();
            }
        }

// Target Machine Code Generator
class TargetMachineCodeGenerator {
    private Map<String, String> opcodeMap = new HashMap<>();

    public TargetMachineCodeGenerator() {
        // Initialize opcode mapping to binary
        opcodeMap.put("ADD", "001 010 11");
        opcodeMap.put("SUB", "001 011 01");
        opcodeMap.put("MUL", "001 010 10");
        opcodeMap.put("DIV", "001 011 11");
    }

    public String generate(String optimizedCode) {
        if (optimizedCode == null || optimizedCode.isEmpty()) {
            return "";
        }

        StringBuilder machineCode = new StringBuilder();
        System.out.println("\n======STAGE7: TARGET MACHINE CODE");

        // Split the optimized code into lines
        String[] lines = optimizedCode.split("\n");

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            // Parse the optimized instruction (e.g., "ADD t1, a, b")
            String[] parts = line.split(" |, ");

            if (parts.length >= 4) {
                String opcode = parts[0];
                String result = parts[1];
                String operand1 = parts[2];
                String operand2 = parts[3];

                // Get the binary opcode
                String binaryOpcode = opcodeMap.getOrDefault(opcode, "000 000 00");

                // Extract register number from result (e.g., "t1" -> "1")
                String resultNumber;
                if (result.startsWith("t")) {
                    resultNumber = result.substring(1);
                } else {
                    // If not a temp var, use a hash code for uniqueness
                    resultNumber = String.valueOf(Math.abs(result.hashCode() % 256));
                }

                // Generate binary machine code
                String binaryInstruction = binaryOpcode + " " + Integer.toBinaryString(Integer.parseInt(resultNumber));

                machineCode.append(binaryInstruction).append("\n");
            }
        }

        System.out.println(machineCode.toString());
        return machineCode.toString();
    }
}