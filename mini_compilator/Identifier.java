/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mini_compilator;

/**
 *
 * @author HP
 */


public class Identifier {

    // Matrice de transition pour valider les identificateurs
    // États: 0 = initial, 1 = accepté
    // Colonnes: 0 = lettre, 1 = chiffre, 2 = underscore, 3 = autre
    private static final int[][] MAT = {
        { 1, -1, 1, -1},  // État 0: doit commencer par lettre ou _
        { 1,  1, 1, -1}   // État 1: peut continuer avec lettre, chiffre ou _
    };
    

    // Retourne l'index de colonne selon le caractère
    private static int colonne(char c) {
        if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')) {
            return 0; // lettre
        } else if ('0' <= c && c <= '9') {
            return 1; // chiffre
        } else if (c == '_') {
            return 2; // underscore
        } else {
            return 3; // autre (invalide)
        }
    }

    // Vérifie une chaîne avec la matrice de transition
    private static boolean validateWithMatrix(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        int etatCourant = 0;
        int i = 0;
        int longueur = str.length();//le problème a riglet c'est ici

        while (i < longueur && etatCourant != -1) { //on utilisant # pour iniquer la fin de mot 
            etatCourant = MAT[etatCourant][colonne(str.charAt(i))];
            i++;
        }

        return (etatCourant == 0 || etatCourant == 1);
    }

    // Vérifie si le mot est une variable PHP ($xxx)
    public static boolean isVariable(String word) {
        if (word == null || !word.startsWith("$")) {
            return false;
        }
        if (word.length() < 2) {
            return false;
        }
        String afterDollar = word.substring(1);
        return validateWithMatrix(afterDollar);
    }

    // Vérifie si le mot est un identificateur normal
    public static boolean isIdentifier(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        if (word.startsWith("$")) {
            return false;
        }
        return validateWithMatrix(word);
    }

    // Classification complète d'un mot
    public static Token classify(String word, int line) {
        if (word.startsWith("$")) {
            if (isVariable(word)) {
                return new Token(TokenType.VARIABLE, word, line);
            } else {
                return new Token(TokenType.ERROR, "Variable invalide: " + word, line);
            }
        }
        if (Keywords.KEYWORDS.contains(word)) {
            return new Token(TokenType.KEYWORD, word, line);
        }
        if (isIdentifier(word)) {
            return new Token(TokenType.IDENTIFIER, word, line);
        }
        return new Token(TokenType.ERROR, "Identificateur invalide: " + word, line);
    }
}