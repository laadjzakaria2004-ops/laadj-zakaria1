/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mini_compilator;

/**
 *
 * @author HP
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {
    
    /**
     * Lit tout le contenu d'un fichier et le retourne comme String
     * @param filePath Chemin du fichier
     * @return Contenu du fichier
     */
    public static String readFile(String filePath) {
        try {
            // Méthode simple avec Java NIO
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.out.println("❌ Erreur de lecture du fichier: " + filePath);
            System.out.println("   " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lit un fichier ligne par ligne (alternative)
     * @param filePath Chemin du fichier
     * @return Contenu du fichier
     */
    public static String readFileLineByLine(String filePath) {
        StringBuilder content = new StringBuilder();
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("❌ Erreur de lecture du fichier: " + filePath);
            System.out.println("   " + e.getMessage());
            return null;
        }
        
        return content.toString();
    }
    
    /**
     * Vérifie si un fichier existe
     * @param filePath Chemin du fichier
     * @return true si le fichier existe
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }
    
    /**
     * Obtient l'extension d'un fichier
     * @param filePath Chemin du fichier
     * @return Extension du fichier (ex: "php", "txt")
     */
    public static String getExtension(String filePath) {
        int lastDot = filePath.lastIndexOf('.');
        if (lastDot > 0) {
            return filePath.substring(lastDot + 1);
        }
        return "";
    }
}
