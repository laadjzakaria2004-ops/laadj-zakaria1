/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mini_compilator;

/**
 *
 * @author HP
 */
public enum TokenType {
    KEYWORD,      // Mots-clés (if, else, while, etc.)
    IDENTIFIER,   // Identificateurs normaux (maVariable, calcul, etc.)
    VARIABLE,     // Variables PHP ($age, $nom, etc.)
    NUMBER,       // Nombres (25, 19.99, etc.)
    STRING,       // Chaînes de caractères ("hello", 'world')
    OPERATOR,     // Opérateurs (+, -, ==, &&, etc.)
    SEPARATOR,    // Séparateurs (; , ( ) { } [ ] :)
    ERROR,        // Erreurs lexicales
    EOF           // Fin de fichier
}