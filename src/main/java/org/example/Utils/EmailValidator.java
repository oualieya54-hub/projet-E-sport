package org.example.Utils;

/**
 * Règles simples pour l’inscription : exactement un « @ », et au moins un point
 * dans la partie domaine (après le @), ex. user@gmail.com
 */
public final class EmailValidator {

    private EmailValidator() {}

    public static boolean isValid(String email) {
        if (email == null) return false;
        String e = email.trim();
        if (e.isEmpty()) return false;

        long atCount = e.chars().filter(ch -> ch == '@').count();
        if (atCount != 1) return false;

        int at = e.indexOf('@');
        String local = e.substring(0, at).trim();
        String domain = e.substring(at + 1).trim();
        if (local.isEmpty() || domain.isEmpty()) return false;
        if (!domain.contains(".")) return false;

        return true;
    }

    public static String reasonIfInvalid(String email) {
        if (isValid(email)) return null;
        if (email == null || email.trim().isEmpty()) {
            return "L’adresse e-mail est obligatoire.";
        }
        String e = email.trim();
        long atCount = e.chars().filter(ch -> ch == '@').count();
        if (atCount == 0) {
            return "L’adresse doit contenir exactement un symbole « @ ».";
        }
        if (atCount > 1) {
            return "L’adresse ne doit pas contenir plus d’un symbole « @ ».";
        }
        int at = e.indexOf('@');
        String domain = e.substring(at + 1).trim();
        if (!domain.contains(".")) {
            return "L’adresse doit contenir au moins un point « . » (ex. .com, .fr).";
        }
        return "Adresse e-mail invalide.";
    }
}
