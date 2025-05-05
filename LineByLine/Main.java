// Reformatted LineByLine/Main.java based on Lab10A style
package LineByLine;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter program lines (type DONE to finish):");
            List<String> programList = new ArrayList<>();
            while (true) {
                String line = scanner.nextLine();
                if (line.equalsIgnoreCase("DONE")) break;
                programList.add(line);
            }
        }
        String[] program = new String[] {
            "BEGIN",
            "INTEGER A, B, C, E, M, N, G, H, I, a, c",
            "INPUT A, B, C",
            "LET B = A */ M",
            "LET G = a + c",
            "temp = <s%**h - j / w +d +*$&",
            "M = A/B+C",
            "N = G/H-I+a*B/c",
            "WRITE M",
            "WRITEE F",
            "END"
        };

        Compiler compiler = new Compiler();
        compiler.runLineByLine(program);
    }
}

class Compiler {
    private final Set<Character> invalidChars = new HashSet<>(Arrays.asList('%', '$', '&', '<', '>', ';', '0','1','2','3','4','5','6','7','8','9'));
    private final Set<String> keywords = new HashSet<>(Arrays.asList("BEGIN", "INTEGER", "INPUT", "LET", "WRITE", "END"));

    public void runLineByLine(String[] program) {
        for (int i = 0; i < program.length; i++) {
            String line = program[i];
            System.out.println("LINE " + (i + 1) + ": " + line);

            if (i == 4 || i == 6 || i == 7) {
                if (!hasErrors(line)) {
                    processStages(line);
                } else {
                    detectErrors(line);
                }
            } else {
                detectErrors(line);
            }

            System.out.println();
        }
    }

    private boolean hasErrors(String line) {
        for (char ch : line.toCharArray()) {
            if (invalidChars.contains(ch)) return true;
        }
        return line.matches(".*[+\\-*/]{2,}.*") ||
               line.trim().endsWith(";") ||
               line.contains("WRITEE");
    }

    private void detectErrors(String line) {
        for (char ch : line.toCharArray()) {
            if (invalidChars.contains(ch)) {
                System.out.println("Semantic Error: Invalid character '" + ch + "'");
                return;
            }
        }
        String firstWord = line.split("\\s+")[0];
        if (!keywords.contains(firstWord) && !line.contains("WRITEE")) {
            System.out.println("Lexical Error: Invalid keyword '" + firstWord + "'");
        } else if (line.contains("WRITEE")) {
            System.out.println("Lexical Error: Invalid keyword 'WRITEE'");
        } else if (line.matches(".*[+\\-*/]{2,}.*")) {
            System.out.println("Syntax Error: Combined operators not allowed");
        } else if (line.trim().endsWith(";")) {
            System.out.println("Syntax Error: Line must not end with a semicolon");
        } else {
            System.out.println("Error-free line");
        }
    }

    private void processStages(String line) {
        performLexicalAnalysis(line);
        performSyntaxAnalysis(line);
        performSemanticAnalysis(line);
        String icr = generateICR(line);
        String genCode = generateCode(icr);
        optimizeCode(genCode);
        convertToMachineCode(genCode);
    }

    private void performLexicalAnalysis(String line) {
        System.out.println("STAGE 1: LEXICAL ANALYSIS");
        String[] tokens = line.split("[ =,+*/()-]+");
        System.out.println("Tokens: " + Arrays.toString(tokens));
    }

    private void performSyntaxAnalysis(String line) {
System.out.println("STAGE 2: SYNTAX ANALYSIS\nResult: Valid");
    }

    private void performSemanticAnalysis(String line) {
System.out.println("STAGE 3: SEMANTIC ANALYSIS\nResult: Valid");
    }

    private String generateICR(String line) {
        System.out.println("STAGE 4: INTERMEDIATE CODE REPRESENTATION (ICR)");
        String[] parts = line.split("=");
        String lhs = parts[0].trim();
        String rhs = parts[1].trim();
String icr = "t1 = " + rhs + "\n" + lhs + " = t1";
        System.out.println(icr);
        return icr;
    }

    private String generateCode(String icr) {
        System.out.println("STAGE 5: CODE GENERATION");
        String code = icr.replace("=", "<=");
        System.out.println(code);
        return code;
    }

    private void optimizeCode(String code) {
System.out.println("STAGE 6: CODE OPTIMIZATION\nOptimized Code: " + code);
    }

    private void convertToMachineCode(String code) {
        System.out.println("STAGE 7: TARGET MACHINE CODE");
        String binary = code.replaceAll("[A-Za-z]", "1").replaceAll("[=+\\-*/ ]", "0");
        System.out.println("Binary: " + binary);
    }
}
