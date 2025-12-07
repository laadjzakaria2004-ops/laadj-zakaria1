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



public class SimpleLexer {
    
    private String source;
    private int i;
    private int ligne;
    
    public SimpleLexer(String source) {
        this.source = source + '\0';  // Sentinelle de fin
        this.i = 0;
        this.ligne = 1;
    }
    
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        
        while (source.charAt(i) != '\0') {
            char c = source.charAt(i);
            
            // ==================== ESPACES ET SAUTS DE LIGNE ====================
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                if (c == '\n') {
                    ligne++;
                }
                i++;
                continue;
            }
            
            // ==================== COMMENTAIRES ====================
            
            // Commentaire ligne (//)
            if (c == '/' && source.charAt(i + 1) == '/') {
                i += 2;
                while (source.charAt(i) != '\n' && source.charAt(i) != '\0') {
                    i++;
                }
                continue;
            }
            
            // Commentaire multi-lignes (/* ... */)
            if (c == '/' && source.charAt(i + 1) == '*') {
                i += 2;
                boolean commentaireFerme = false;
                
                while (source.charAt(i) != '\0') {
                    if (source.charAt(i) == '*' && source.charAt(i + 1) == '/') {
                        i += 2;
                        commentaireFerme = true;
                        break;
                    }
                    if (source.charAt(i) == '\n') {
                        ligne++;
                    }
                    i++;
                }
                
                if (!commentaireFerme) {
                    tokens.add(new Token(TokenType.ERROR, "Commentaire non terminé", ligne));
                }
                continue;
            }
            
            // ==================== CHAÎNES DE CARACTÈRES ====================
            
            // Chaîne avec guillemets doubles "..."
            if (c == '"') {
                String chaine = "";
                int ligneDebut = ligne;
                i++; // Passer le guillemet ouvrant
                
                while (source.charAt(i) != '"' && source.charAt(i) != '\0') {
                    if (source.charAt(i) == '\\') {
                        // Gestion des caractères d'échappement
                        i++;
                        if (source.charAt(i) != '\0') {
                            chaine += source.charAt(i);
                        }
                    } else {
                        if (source.charAt(i) == '\n') {
                            ligne++;
                        }
                        chaine += source.charAt(i);
                    }
                    i++;
                }
                
                if (source.charAt(i) == '"') {
                    i++; // Passer le guillemet fermant
                    tokens.add(new Token(TokenType.STRING, chaine, ligneDebut));
                } else {
                    tokens.add(new Token(TokenType.ERROR, "Chaîne non terminée: \"" + chaine, ligneDebut));
                }
                continue;
            }
            
            // Chaîne avec guillemets simples '...'
            if (c == '\'') {
                String chaine = "";
                int ligneDebut = ligne;
                i++; // Passer le guillemet ouvrant
                
                while (source.charAt(i) != '\'' && source.charAt(i) != '\0') {
                    if (source.charAt(i) == '\\') {
                        // Gestion des caractères d'échappement
                        i++;
                        if (source.charAt(i) != '\0') {
                            chaine += source.charAt(i);
                        }
                    } else {
                        if (source.charAt(i) == '\n') {
                            ligne++;
                        }
                        chaine += source.charAt(i);
                    }
                    i++;
                }
                
                if (source.charAt(i) == '\'') {
                    i++; // Passer le guillemet fermant
                    tokens.add(new Token(TokenType.STRING, chaine, ligneDebut));
                } else {
                    tokens.add(new Token(TokenType.ERROR, "Chaîne non terminée: '" + chaine, ligneDebut));
                }
                continue;
            }
            
            // ==================== IDENTIFICATEURS / VARIABLES / MOTS-CLÉS ====================
            
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '$' || c == '_') {
                String mot = "";
                int ligneDebut = ligne;
                
                // Regrouper tous les caractères valides
                while ((source.charAt(i) >= 'a' && source.charAt(i) <= 'z') || 
                       (source.charAt(i) >= 'A' && source.charAt(i) <= 'Z') || 
                       (source.charAt(i) >= '0' && source.charAt(i) <= '9') || 
                       source.charAt(i) == '$' || source.charAt(i) == '_') {
                    mot += source.charAt(i);
                    i++;
                }
                
                // DÉLÉGUER à Identifier.classify()
                // Cette méthode utilise la matrice de transition pour valider (elle se trouve dans identifier )
                Token token = Identifier.classify(mot, ligneDebut);
                tokens.add(token);
                continue;
            }
            
            // ==================== NOMBRES ====================
            
            if (c >= '0' && c <= '9' ) {
                String nombre = "";
                int ligneDebut = ligne;
                
                // Lire les chiffres
                while (source.charAt(i) >= '0' && source.charAt(i) <= '9' ) {
                    nombre += source.charAt(i);
                    i++;
                }
                
                // Partie décimale
                if (source.charAt(i) == '.') {
                    nombre += source.charAt(i);
                    i++;
                    
                    // Doit être suivi de chiffres
                    if (source.charAt(i) >= '0' && source.charAt(i) <= '9') {
                        while (source.charAt(i) >= '0' && source.charAt(i) <= '9') {
                            nombre += source.charAt(i);
                            i++;
                        }
                    } else {
                        // Point non suivi de chiffre = erreur
                        tokens.add(new Token(TokenType.ERROR, "Nombre invalide: " + nombre, ligneDebut));
                        continue;
                    }
                }
                
                // VÉRIFICATION CRITIQUE: Nombre suivi directement d'une lettre/$/_ = ERREUR
                // Exemples: 2var, 123abc, 42$test
                if ((source.charAt(i) >= 'a' && source.charAt(i) <= 'z') ||
                    (source.charAt(i) >= 'A' && source.charAt(i) <= 'Z') ||
                    source.charAt(i) == '$' || source.charAt(i) == '_') {
                    
                    // Continuer à lire pour le message d'erreur complet
                    while ((source.charAt(i) >= 'a' && source.charAt(i) <= 'z') ||
                           (source.charAt(i) >= 'A' && source.charAt(i) <= 'Z') ||
                           (source.charAt(i) >= '0' && source.charAt(i) <= '9') ||
                           source.charAt(i) == '$' || source.charAt(i) == '_') {
                        nombre += source.charAt(i);
                        i++;
                    }
                    tokens.add(new Token(TokenType.ERROR, "Identificateur invalide: " + nombre, ligneDebut));
                } else {
                    tokens.add(new Token(TokenType.NUMBER, nombre, ligneDebut));
                }
                continue;
            }
            
            // ==================== OPÉRATEURS ====================
            
            // Opérateur = et ==
            if (c == '=') {
                if (i + 1 < source.length() && source.charAt(i + 1) == '=') {
                    tokens.add(new Token(TokenType.OPERATOR, "==", ligne));
                    i += 2;
                } else {
                    tokens.add(new Token(TokenType.OPERATOR, "=", ligne));
                    i++;
                }
                continue;
            }
            
            // Opérateur ! et !=
            if (c == '!') {
                if (i + 1 < source.length() && source.charAt(i + 1) == '=') {
                    tokens.add(new Token(TokenType.OPERATOR, "!=", ligne));
                    i += 2;
                } else {
                    tokens.add(new Token(TokenType.OPERATOR, "!", ligne));
                    i++;
                }
                continue;
            }
            
            // Opérateur && et &
            if (c == '&') {
                if (i + 1 < source.length() && source.charAt(i + 1) == '&') {
                    tokens.add(new Token(TokenType.OPERATOR, "&&", ligne));
                    i += 2;
                } else {
                    tokens.add(new Token(TokenType.OPERATOR, "&", ligne));
                    i++;
                }
                continue;
            }
            
            // Opérateur || et |
            if (c == '|') {
                if (i + 1 < source.length() && source.charAt(i + 1) == '|') {
                    tokens.add(new Token(TokenType.OPERATOR, "||", ligne));
                    i += 2;
                } else {
                    tokens.add(new Token(TokenType.OPERATOR, "|", ligne));
                    i++;
                }
                continue;
            }
            
            // Opérateur < et <=
            if (c == '<') {
                if (i + 1 < source.length() && source.charAt(i + 1) == '=') {
                    tokens.add(new Token(TokenType.OPERATOR, "<=", ligne));
                    i += 2;
                } else {
                    tokens.add(new Token(TokenType.OPERATOR, "<", ligne));
                    i++;
                }
                continue;
            }
            
            // Opérateur > et >=
            if (c == '>') {
                if (i + 1 < source.length() && source.charAt(i + 1) == '=') {
                    tokens.add(new Token(TokenType.OPERATOR, ">=", ligne));
                    i += 2;
                } else {
                    tokens.add(new Token(TokenType.OPERATOR, ">", ligne));
                    i++;
                }
                continue;
            }
            
            // Opérateur ++ et +
            if (c == '+') {
                if (i + 1 < source.length() && source.charAt(i + 1) == '+') {
                    tokens.add(new Token(TokenType.OPERATOR, "++", ligne));
                    i += 2;
                } else {
                    tokens.add(new Token(TokenType.OPERATOR, "+", ligne));
                    i++;
                }
                continue;
            }
            
            // Opérateur -- et -
            if (c == '-') {
                if (i + 1 < source.length() && source.charAt(i + 1) == '-') {
                    tokens.add(new Token(TokenType.OPERATOR, "--", ligne));
                    i += 2;
                } else {
                    tokens.add(new Token(TokenType.OPERATOR, "-", ligne));
                    i++;
                }
                continue;
            }
            
            // Autres opérateurs: *, /, %
            if (c == '*' || c == '/' || c == '%') {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(c), ligne));
                i++;
                continue;
            }
            
            // ==================== SÉPARATEURS ====================
            
            if (Symbols.SEPARATORS.contains(c)) {
                tokens.add(new Token(TokenType.SEPARATOR, String.valueOf(c), ligne));
                i++;
                continue;
            }
            
            // ==================== ERREUR ====================
            
            tokens.add(new Token(TokenType.ERROR, "Symbole inconnu: '" + c + "'", ligne));
            i++;
        }
        
        // Ajouter le token de fin de fichier
        tokens.add(new Token(TokenType.EOF, "", ligne));
        
        return tokens;
    }
}