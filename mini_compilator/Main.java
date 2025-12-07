/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mini_compilator;

/**
 *
 * @author HP
 */

import java.util.List;
import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║         MINI-COMPILATEUR PHP - Par: Zak                  ║");
        System.out.println("║         Structure de controle: try/catch                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");
        
        // ==================== LECTURE DU FICHIER ====================
        
        String filePath;
        String code;
        
        // Option 1: Chemin passé en argument
        if (args.length > 0) {
            filePath = args[0];
        } 
        // Option 2: Demander à l'utilisateur
        else {
            System.out.print("📁 Entrez le chemin du fichier source: ");
            filePath = scanner.nextLine().trim();
        }
        
        // Vérifier si le fichier existe
        if (!FileReader.fileExists(filePath)) {
            System.out.println("\n❌ Fichier non trouve: " + filePath);
            System.out.println("   Vérifiez le chemin et réessayez.");
            return;
        }
        
        // Lire le contenu du fichier
        System.out.println("\n📖 Lecture du fichier: " + filePath);
        code = FileReader.readFile(filePath);
        
        if (code == null || code.isEmpty()) {
            System.out.println("❌ Le fichier est vide ou illisible.");
            return;
        }
        
        // ==================== AFFICHER LE CODE SOURCE ====================
        
        System.out.println("\n══════════════ CODE SOURCE ══════════════\n");
        
        // Afficher avec numéros de ligne
        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            System.out.printf("%3d | %s%n", i + 1, lines[i]);
        }
        
        // ==================== ANALYSE LEXICALE ====================
        
        System.out.println("\n══════════════ ANALYSE LEXICALE ══════════════\n");
        
        SimpleLexer lexer = new SimpleLexer(code);
        List<Token> tokens = lexer.tokenize();
        
        // Afficher les tokens
        for (Token t : tokens) {
            String emoji = switch (t.type) {
                case KEYWORD -> "ee";
                case VARIABLE -> "💲";
                case IDENTIFIER -> "📝";
                case NUMBER -> "🔢";
                case STRING -> "📜";
                case OPERATOR -> "⚙️";
                case SEPARATOR -> "📌";
                case ERROR -> "❌";
                case EOF -> "🏁";
            };
            System.out.println(emoji + " " + t);
        }
        
        // Compter les erreurs lexicales
        long lexErrors = tokens.stream()
            .filter(t -> t.type == TokenType.ERROR)
            .count();
        
        System.out.println("\n📊 Tokens generes: " + tokens.size());
        System.out.println("❌ Erreurs lexicales: " + lexErrors);
        
        // ==================== ANALYSE SYNTAXIQUE ====================
        
       System.out.println("\n══════════════ ANALYSE SYNTAXIQUE ══════════════\n");
        
        Parser parser = new Parser(tokens);
        parser.analyser();
        
        // ==================== RÉSUMÉ FINAL ====================
        
        System.out.println("\n══════════════ RESUME FINAL ══════════════\n");
        System.out.println("📁 Fichier: " + filePath);
        System.out.println("📏 Lignes de code: " + lines.length);
        System.out.println("🔢 Tokens: " + tokens.size());
        System.out.println("❌ Erreurs lexicales: " + lexErrors);
        
        scanner.close();
    }
}


/*
<Programme>    ---- > <Instruction>*

<Instruction>   - - ->   <TryCatch>
                      | <Declaration>
                      | <Affectation>
                      | <StructureIgnorée>

<TryCatch>   - ---> 'try' <Block> <Catch>+ <Finally>?

<Catch>      ---> 'catch' '(' IDENTIFIER VARIABLE ')' <Block>

<Finally>      --> 'finally' <Block>

<Declaration>    ---> 'var' VARIABLE '=' <Expression> ';'

<Affectation>    ---> VARIABLE '=' <Expression> ';'
                      | VARIABLE '++' ';'
                      | VARIABLE '--' ';'

<Block>          ----> '{' <Instruction>* '}'

<Expression>      ---> <LogicOr>

<LogicOr>       ----> <LogicAnd> ( '||' <LogicAnd> )*

<LogicAnd>       ---> <Comparaison> ( '&&' <Comparaison> )*

<Comparaison>    ----> <Addition> ( <OpComp> <Addition> )*

<OpComp>     -----> '==' | '!=' | '<' | '>' | '<=' | '>='

<Addition>   -----> <Multiplication> ( ('+' | '-') <Multiplication> )*

<Multiplication>    ---> <Unaire> ( ('*' | '/' | '%') <Unaire> )*

<Unaire>      -----> ('!' | '-') <Unaire>
                      | <Primaire>

<Primaire>     ----> NUMBER
                      | STRING
                      | VARIABLE ( '++' | '--' )?
                      | IDENTIFIER
                      | 'true'
                      | 'false'
                      | '(' <Expression> ')'

<StructureIgnorée>  ---->  'if' '(' ... ')' '{' ... '}'
                      | 'while' '(' ... ')' '{' ... '}'
                      | 'for' '(' ... ')' '{' ... '}'|
                        'else'('...')' '{' ... '}' 

*/