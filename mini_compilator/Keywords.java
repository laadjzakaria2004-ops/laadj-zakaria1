/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mini_compilator;

/**
 *
 * @author HP
 */
import java.util.HashSet;
import java.util.Set;

public class Keywords {
    public static final Set<String> KEYWORDS = new HashSet<>();

    static {
        // Déclaration
        KEYWORDS.add("var");
        
        // Structures de contrôle
        KEYWORDS.add("if");
        KEYWORDS.add("else");
        KEYWORDS.add("while");
        KEYWORDS.add("do");
        KEYWORDS.add("for");
        KEYWORDS.add("foreach");
        KEYWORDS.add("switch");
        KEYWORDS.add("case");
        KEYWORDS.add("break");
        KEYWORDS.add("default");
        KEYWORDS.add("return");
        
        // Try/Catch (STRUCTURE PRINCIPALE)
        KEYWORDS.add("try");
        KEYWORDS.add("catch");
        KEYWORDS.add("finally");
        KEYWORDS.add("throw");
        
        // Booléens
        KEYWORDS.add("true");
        KEYWORDS.add("false");
        
        // les srtucture d'affichage
            KEYWORDS.add("echo");
                KEYWORDS.add("print");
                
        
        // Nom personnalisé (obligatoire dans le TP)
        KEYWORDS.add("Zakaria");
    }
}