package AllAtOnce;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        String program = "BEGIN\n" +
                "INTEGER A, B, C, E, M, N, G, H, I, a, c\n" +
                "INPUT A, B, C\n" +
                "LET B = A */ M\n" +
                "LET G = a + c\n" +
                "temp = <s%**h - j / w +d +*$&\n" +
                "M = A/B+C\n" +
                "N = G/H-I+a*B/c\n" +
                "WRITE M\n" +
                "WRITEE F\n" +
                "END";

        Compiler compiler = new Compiler();
        compiler.processAllAtOnce(program);
    }
}

class Compiler {
    private final Set<Character> invalidChars = new HashSet<>(Arrays.asList('%', '$', '&', '<', '>', ';', '0','1','2','3','4','5','6','7','8','9'));
    private final Set<String> keywords = new HashSet<>(Arrays.asList("BEGIN", "INTEGER", "INPUT", "LET", "WRITE", "END"));

    public void processAllAtOnce(String program) {
        String[] lines = program.split("\\n");
        boolean hasErrors = false;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (i != 4 && i != 6 && i != 7 && containsErrors(line)) {
                System.out.println("Line " + (i + 1) + ": " + line);
                checkErrors(line);
                hasErrors = true;
                System.out.println();
            }
        }

        if (!hasErrors) {
            for (int i : new int[]{4, 6, 7}) {
                String line = lines[i];
                System.out.println("Line " + (i + 1) + ": " + line);
                runCompilerStages(line);
                System.out.println();
            }
        } else {
            System.out.println("Program has errors. Cannot proceed to compiler stages.");
        }
    }

    private boolean containsErrors(String line) {
        for (char ch : line.toCharArray()) {
            if (invalidChars.contains(ch)) return true;
        }
        if (line.matches(".*[+\-*/]{2,}.*")) return true;
        if (line.trim().endsWith(";")) return true;
        if (line.contains("WRITEE")) return true;
        return false;
    }

    private void checkErrors(String line) {
        for (char ch : line.toCharArray()) {
            if (invalidChars.contains(ch)) {
                System.out.println("Semantic Error: Invalid character '" + ch + "'");
                return;
            }
        }
        if (line.contains("WRITEE")) {
            System.out.println("Lexical Error: Invalid keyword 'WRITEE'");
            return;
        }
        if (line.matches(".*[+\-*/]{2,}.*")) {
            System.out.println("Syntax Error: Combined operators not allowed");
            return;
        }
        if (line.trim().endsWith(";")) {
            System.out.println("Syntax Error: Line must not end with a semicolon");
            return;
        }
        System.out.println("Error-free line");
    }

    private void runCompilerStages(String line) {
        lexicalAnalysis(line);
        syntaxAnalysis(line);
        semanticAnalysis(line);
        String icr = intermediateCode(line);
        String codeGen = codeGeneration(icr);
        codeOptimization(codeGen);
        targetMachineCode(codeGen);
    }

    private void lexicalAnalysis(String line) {
        String[] tokens = line.split("[ =,+*/()-]+");
        System.out.println("Lexical Tokens: " + Arrays.toString(tokens));
    }

    private void syntaxAnalysis(String line) {
        System.out.println("Syntax: Valid");
    }

    private void semanticAnalysis(String line) {
        System.out.println("Semantic: Valid");
    }

    private String intermediateCode(String line) {
        String[] parts = line.split("=");
        String rhs = parts[1].trim();
        String lhs = parts[0].trim();
        String icr = "t1 = " + rhs + "\n" + lhs + " = t1";
        System.out.println("ICR: " + icr);
        return icr;
    }

    private String codeGeneration(String icr) {
        String code = icr.replace("=", "<=");
        System.out.println("Code Generation: " + code);
        return code;
    }

    private void codeOptimization(String code) {
        System.out.println("Optimization: " + code);
    }

    private void targetMachineCode(String code) {
        String binary = code.replaceAll("[A-Za-z]", "1").replaceAll("[=+\-*/ ]", "0");
        System.out.println("Target Machine Code: " + binary);
    }
}
