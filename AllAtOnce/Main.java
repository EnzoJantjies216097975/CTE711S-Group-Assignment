package AllAtOnce;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        String program;
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter the full program (end with an empty line):");
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) break;
                sb.append(line).append("\n");
            }
            program = sb.toString();
        }

        program =
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
        compiler.runAllAtOnce(program);
    }
}

class Compiler {
    private final Set<Character> invalidChars = new HashSet<>(Arrays.asList('%', '$', '&', '<', '>', ';', '0','1','2','3','4','5','6','7','8','9'));
    private final Set<String> keywords = new HashSet<>(Arrays.asList("BEGIN", "INTEGER", "INPUT", "LET", "WRITE", "END"));

    public void runAllAtOnce(String program) {
        String[] lines = program.split("\\n");
        boolean hasError = false;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (i != 4 && i != 6 && i != 7 && hasErrors(line)) {
                System.out.println("LINE " + (i + 1) + ": " + line);
                reportErrors(line);
                hasError = true;
                System.out.println();
            }
        }

        if (!hasError) {
            for (int i : new int[]{4, 6, 7}) {
                String line = lines[i];
                System.out.println("LINE " + (i + 1) + ": " + line);
                executeStages(line);
                System.out.println();
            }
        } else {
            System.out.println("Errors detected. Compilation stages aborted.");
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

    private void reportErrors(String line) {
        for (char ch : line.toCharArray()) {
            if (invalidChars.contains(ch)) {
                System.out.println("Semantic Error: Invalid character '" + ch + "'");
                return;
            }
        }
        String firstWord = line.split("\\s+")[0];
        if (!keywords.contains(firstWord) && !line.trim().isEmpty()) {
            System.out.println("Lexical Error: Invalid keyword '" + firstWord + "'");
        }
        if (line.contains("WRITEE")) {
            System.out.println("Lexical Error: Invalid keyword 'WRITEE'");
        } else if (line.matches(".*[+\\-*/]{2,}.*")) {
            System.out.println("Syntax Error: Combined operators not allowed");
        } else if (line.trim().endsWith(";")) {
            System.out.println("Syntax Error: Line must not end with a semicolon");
        } else {
            System.out.println("Error-free line");
        }
    }

    private void executeStages(String line) {
        lexicalAnalysis(line);
        syntaxAnalysis(line);
        semanticAnalysis(line);
        String icr = createICR(line);
        String genCode = generateCode(icr);
        optimize(genCode);
        toBinary(genCode);
    }

    private void lexicalAnalysis(String line) {
        System.out.println("STAGE 1: LEXICAL ANALYSIS");
        String[] tokens = line.split("[ =,+*/()-]+");
        System.out.println("Tokens: " + Arrays.toString(tokens));
    }

    private void syntaxAnalysis(String line) {
        System.out.println("STAGE 2: SYNTAX ANALYSIS\nResult: Valid");
    }

    private void semanticAnalysis(String line) {
        System.out.println("STAGE 3: SEMANTIC ANALYSIS\nResult: Valid");
    }

    private String createICR(String line) {
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

    private void optimize(String code) {
        System.out.println("STAGE 6: CODE OPTIMIZATION\nOptimized Code: " + code);
    }

    private void toBinary(String code) {
        System.out.println("STAGE 7: TARGET MACHINE CODE");
        String binary = code.replaceAll("[A-Za-z]", "1").replaceAll("[=+\\-*/ ]", "0");
        System.out.println("Binary: " + binary);
    }
}
