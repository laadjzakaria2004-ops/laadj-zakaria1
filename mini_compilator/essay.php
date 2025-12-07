tr
{
    
    
    // erreur : division par zéro volontaire
    $x = 10 / 0;

} catch Exception $e {   // ❌ mauvaise syntaxe (parenthèses manquantes)
    echo "Erreur : " . $e->getMessage() ;

} catch {                // ❌ type d’exception manquant
    echo "Autre erreur" ;

finally {                // ❌ "finally" doit venir avant le catch ou mal placé
    echo "Bloc finally exécuté" ;
}