package org.example.Utils;

import java.sql.SQLException;

/**
 * Traduit les erreurs SQL techniques en messages compréhensibles pour l'utilisateur.
 * Ne change pas l'architecture : uniquement une aide côté UI.
 */
public final class DbErrorMapper {

    private DbErrorMapper() {}

    public static String toUserMessage(Throwable t) {
        if (t == null) return "Une erreur inattendue est survenue.";

        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) root = root.getCause();

        String msg = root.getMessage() != null ? root.getMessage() : "";
        String m = msg.toLowerCase();

        // Contrainte FK (ex: coach inexistant)
        if (m.contains("foreign key constraint fails")) {
            if (m.contains("session") && m.contains("id_coach") && m.contains("users")) {
                return "Le coach sélectionné n'existe pas dans la base de données. "
                        + "Vérifiez que le coach est bien présent dans la table 'users' (id valide) puis réessayez.";
            }
            return "Impossible d'enregistrer ces données car une référence liée n'existe pas (contrainte de relation).";
        }

        // Doublons (unique index)
        if (m.contains("duplicate entry")) {
            return "Cet enregistrement existe déjà (doublon). Vérifiez les champs et réessayez.";
        }

        // Valeurs invalides / enum trop long
        if (m.contains("data truncated for column")) {
            return "Valeur invalide pour un champ (format non supporté). Vérifiez vos sélections et réessayez.";
        }

        // Champ obligatoire manquant
        if (m.contains("cannot be null")) {
            return "Un champ obligatoire est manquant. Veuillez compléter le formulaire puis réessayer.";
        }

        // Valeur hors limites
        if (m.contains("out of range")) {
            return "Une valeur est hors limites autorisées. Vérifiez les champs numériques puis réessayez.";
        }

        // Table / colonne manquante (souvent erreur de DB/schema)
        if (m.contains("unknown column") || m.contains("doesn't exist") || m.contains("does not exist")) {
            return "Erreur de configuration de la base de données. Vérifiez la structure (tables/colonnes) puis relancez l'application.";
        }

        // Connexion / accès
        if (m.contains("communications link failure") || m.contains("connection refused") || m.contains("access denied")) {
            return "Impossible de se connecter à la base de données. Vérifiez que MySQL est démarré et que les identifiants sont corrects.";
        }

        if (root instanceof SQLException) {
            // On évite d'afficher le message SQL brut par défaut
            return "Erreur base de données. Veuillez réessayer ou contacter l'administrateur.";
        }
        return "Une erreur inattendue est survenue.";
    }
}

