/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mini_compilator;

/**
 *
 * @author HP
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Analyseur syntaxique par descente récursive
 * Structure principale: try/catch
 * Style: comme compilateur.java
 * @author HP
 */
public class Parser {
    
    private List<Token> tokens;
    private static int i;
    private List<String> erreurs = new ArrayList<>();
    
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        i = 0;
    }
    
    // ==================== UTILITAIRES ====================
    
    private Token current() {
        if (i < tokens.size()) {
            return tokens.get(i);
        }
        return new Token(TokenType.EOF, "", -1);
    }
    
    private void avancer() {
        if (i < tokens.size()) {
            i++;
        }
    }
    
    private boolean estType(TokenType type) {
        return current().type == type;
    }
    
    private boolean estMotCle(String keyword) {
        return current().type == TokenType.KEYWORD && current().value.equals(keyword);
    }
    
    private boolean estSeparateur(String sep) {
        return current().type == TokenType.SEPARATOR && current().value.equals(sep);
    }
    
    private boolean estOperateur(String op) {
        return current().type == TokenType.OPERATOR && current().value.equals(op);
    }
    
    private void erreurSyntaxe(String message) {
        String erreurMsg = "UNE ERREUR SYNTAXIQUE ligne " + current().line + ": " + message + 
                          "\n   Token trouvé: [" + current().type + " : " + current().value + "]";
        erreurs.add(erreurMsg);
        System.out.println(erreurMsg);
    }
    
    //pour la Récupération d'erreur avance jusqu'au prochain point de synchronisation
    private void synchroniser() {
        avancer();
        
        while (current().type != TokenType.EOF) {
            // Points de synchronisation: fin d'instruction ou début de nouvelle structure
            if (estSeparateur(";")) {
                avancer();
                return;
            }
            
            if (estSeparateur("}")) {
                return;
            }
            
            if (estMotCle("try") || estMotCle("catch") || estMotCle("finally") ||
                estMotCle("if") || estMotCle("else") || estMotCle("while") || 
                estMotCle("for") || estMotCle("var")) {
                return;
            }
            
            avancer();
        }
    }
    
    // ==================== POINT D'ENTRÉE ====================
    
    public void analyser() {
        System.out.println(" Début de l'analyse syntaxique\n" );
        
        Programme();
        
        System.out.println("\n" + "=".repeat(60));
        if (erreurs.isEmpty()) {
            System.out.println("✅ Le programme est syntaxiquement correct!");
        } else {
            System.out.println("❌ Le programme contient " + erreurs.size() + " erreur(s) syntaxique(s):");
            System.out.println("=".repeat(60));
            for (int j = 0; j < erreurs.size(); j++) {
                System.out.println("\nErreur #" + (j + 1) + ":");
                System.out.println(erreurs.get(j));
            }
        }
        
    }
    
    // ==================== GRAMMAIRE ====================
    
    private void Programme() {
        while (current().type != TokenType.EOF) {
            try {
                Instruction();
            } catch (Exception e) {
                // En cas d'erreur inattendue, synchroniser et continuer
                synchroniser();
            }
        }
    }
    
    private void Instruction() {
        if (estType(TokenType.ERROR)) {
            System.out.println("   [!] Erreur lexicale ignorée: " + current().value);
            avancer();
            return;
        }
        
        // STRUCTURE PRINCIPALE: try/catch (analysée en détail)
        if (estMotCle("try")) {
            TryCatch();
        } 
        // AUTRES STRUCTURES: Ignorées (reconnues mais pas vérifiées)
        else if (estMotCle("if")) {
            System.out.println("[IF] ligne " + current().line + " - IGNORÉ (non analysé)");
            IgnorerStructure();
        }
        else if (estMotCle("else")) {
            System.out.println("[ELSE] ligne " + current().line + " - IGNORÉ (non analysé)");
            avancer();
            
            if (estMotCle("if")) {
                System.out.println("[ELSE IF] ligne " + current().line + " - IGNORÉ (non analysé)");
                IgnorerStructure();
            } 
            else if (estSeparateur("{")) {
                int accolades = 1;
                avancer();
                while (accolades > 0 && current().type != TokenType.EOF) {
                    if (estSeparateur("{")) accolades++;
                    if (estSeparateur("}")) accolades--;
                    avancer();
                }
            }
        }
        else if (estMotCle("while")) {
            System.out.println("[WHILE] ligne " + current().line + " - IGNORÉ (non analysé)");
            IgnorerStructure();
        } 
        else if (estMotCle("for")) {
            System.out.println("[FOR] ligne " + current().line + " - IGNORÉ (non analysé)");
            IgnorerStructure();
        } 
        // Déclarations et affectations (analysées)
        else if (estMotCle("var")) {
            Declaration();
        } 
        else if (estType(TokenType.VARIABLE)) {
            Affectation();
        } 
        else {
            erreurSyntaxe("Instruction attendue");
            synchroniser();
        }
    }
    
    private void IgnorerStructure() {
        avancer();
        
        if (estSeparateur("(")) {
            int parentheses = 1;
            avancer();
            while (parentheses > 0 && current().type != TokenType.EOF) {
                if (estSeparateur("(")) parentheses++;
                if (estSeparateur(")")) parentheses--;
                avancer();
            }
        }
        
        if (estSeparateur("{")) {
            int accolades = 1;
            avancer();
            while (accolades > 0 && current().type != TokenType.EOF) {
                if (estSeparateur("{")) accolades++;
                if (estSeparateur("}")) accolades--;
                avancer();
            }
        }
    }
    
   
    // <TryCatch>   - ---> 'try' <Block> <Catch>+ <Finally>?
    private void TryCatch() {
        System.out.println("[TRY/CATCH] ligne " + current().line);
        
        if (estMotCle("try")) {
            avancer();
        } else {
            erreurSyntaxe("'try' attendu");
            synchroniser();
            return;
        }
        
        Block();
        
        if (!estMotCle("catch")) {
            erreurSyntaxe("'catch' attendu après le bloc try");
            synchroniser();
            return;
        }
        
        int nbCatch = 0;
        while (estMotCle("catch")) {
            Catch();
            nbCatch++;
        }
        System.out.println("   -> " + nbCatch + " clause(s) catch");
        
        if (estMotCle("finally")) {
            Finally();
        }
    }
    
    // <Catch>    ---> 'catch' '(' IDENTIFIER VARIABLE ')' <Block> 
    private void Catch() {
        System.out.println("   [CATCH] ligne " + current().line);
        avancer();
        
        if (estSeparateur("(")) {
            avancer();
        } else {
            erreurSyntaxe("'(' attendu après 'catch'");
            synchroniser();
            return;
        }
        
        if (estType(TokenType.IDENTIFIER)) {  
            System.out.println("      Type: " + current().value);
            avancer();
        } else {
            erreurSyntaxe("Type d'exception attendu");
            synchroniser();
            return;
        }
        
        if (estType(TokenType.VARIABLE)) {
            System.out.println("      Variable: " + current().value);
            avancer();
        } else {
            erreurSyntaxe("Variable attendue");
            synchroniser();
            return;
        }
        
        if (estSeparateur(")")) {
            avancer();
        } else {
            erreurSyntaxe("')' attendu");
            synchroniser();
            return;
        }
        
        Block();
    }
    
    // <Finally>      --> 'finally' <Block>
    private void Finally() {
        System.out.println("   [FINALLY] ligne " + current().line);
        avancer();
        Block();
    }
    
    // ==================== DECLARATION/AFFECTATION ====================
    
    private void Declaration() {
        System.out.println("[DECLARATION] ligne " + current().line);
        avancer();
        
        if (estType(TokenType.VARIABLE)) {
            System.out.println("   Variable: " + current().value);
            avancer();
        } else {
            erreurSyntaxe("Variable attendue après 'var'");
            synchroniser();
            return;
        }
        
        if (estOperateur("=")) {
            avancer();
        } else {
            erreurSyntaxe("'=' attendu");
            synchroniser();
            return;
        }
        
        Expression();
        
        if (estSeparateur(";")) {
            avancer();
        } else {
            erreurSyntaxe("';' attendu");
            synchroniser();
        }
    }
    
    private void Affectation() {
        System.out.println("[AFFECTATION] ligne " + current().line);
        String varName = current().value;
        avancer();
        
        if (estOperateur("++") || estOperateur("--")) {
            System.out.println("   " + varName + current().value);
            avancer();
        } else if (estOperateur("=")) {
            System.out.println("   " + varName + " = ...");
            avancer();
            Expression();
        } else {
            erreurSyntaxe("'=', '++' ou '--' attendu");
            synchroniser();
            return;
        }
        
        if (estSeparateur(";")) {
            avancer();
        } else {
            erreurSyntaxe("';' attendu");
            synchroniser();
        }
    }
    
    
    // ==================== BLOCK ====================
    
    private void Block() {
        if (estSeparateur("{")) {
            avancer();
        } else {
            erreurSyntaxe("'{' attendu");
            synchroniser();
            return;
        }
        
        while (!estSeparateur("}") && current().type != TokenType.EOF) {
            Instruction();
        }
        
        if (estSeparateur("}")) {
            avancer();
        } else {
            erreurSyntaxe("'}' attendu avant la fin du fichier");
        }
    }
    
    // ==================== EXPRESSIONS ====================
    
    private void Expression() {
        LogicOr();
    }
    
    private void LogicOr() {
        LogicAnd();
        while (estOperateur("||")) {
            avancer();
            LogicAnd();
        }
    }
    
    private void LogicAnd() {
        Comparaison();
        while (estOperateur("&&")) {
            avancer();
            Comparaison();
        }
    }
    
    private void Comparaison() {
        Addition();
        while (estOperateur("==") || estOperateur("!=") || estOperateur("<") || 
               estOperateur(">") || estOperateur("<=") || estOperateur(">=")) {
            avancer();
            Addition();
        }
    }
    
    private void Addition() {
        Multiplication();
        while (estOperateur("+") || estOperateur("-")) {
            avancer();
            Multiplication();
        }
    }
    
    private void Multiplication() {
        Unaire();
        while (estOperateur("*") || estOperateur("/") || estOperateur("%")) {
            avancer();
            Unaire();
        }
    }
    //<Unaire>      -----> ('!' | '-') <Unaire> | <Primaire>
    /*  exemple : !!$x deux ! unaires imbriqués
                  -(-3) deux - unaires imbriqués
    */                  
    
    private void Unaire() {
        if (estOperateur("!") || estOperateur("-")) {
            avancer();
            Unaire();
        } else {
            Primaire();
        }
    }
    
    
    //<Primaire>     ----> NUMBER | STRING | VARIABLE ( '++' | '--' )? | IDENTIFIER | 'true' | 'false' | '(' <Expression> ')'
    
                   
                      
    private void Primaire() {
        if (estType(TokenType.NUMBER)) {
            avancer();
        } else if (estType(TokenType.STRING)) {
            avancer();
        } else if (estType(TokenType.VARIABLE)) {
            avancer();
            if (estOperateur("++") || estOperateur("--")) {
                avancer();
            }
        } else if (estType(TokenType.IDENTIFIER)) {
            avancer();
        } else if (estMotCle("true") || estMotCle("false")) {
            avancer();
        } else if (estSeparateur("(")) {
            avancer();
            Expression();
            if (estSeparateur(")")) {
                avancer();
            } else {
                erreurSyntaxe("')' attendu");
                synchroniser();
            }
        } else {
            erreurSyntaxe("Expression attendue");
            synchroniser();
        }
    }
}