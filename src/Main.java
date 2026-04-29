import models.Membership;
import models.Team;
import services.ServiceMembership;
import services.ServiceTeam;

import java.util.Date;

public class Main {
    public static void main(String[] args) {

        ServiceTeam st = new ServiceTeam();
        ServiceMembership sm = new ServiceMembership();

        // --- TEAM ---
        Team team = new Team(0, "Les Lions", "lions.png", new Date(), 1);
        st.add(team);
        System.out.println("Toutes les équipes : " + st.getAll());

        // --- MEMBERSHIP ---
        sm.join(1, 2, "Joueur");
        System.out.println("Tous les membres : " + sm.getAll());

        // Changer le rôle
        Membership m = sm.getAll().get(0);
        sm.changeRole(m, "Capitaine");
        System.out.println("Après changement de rôle : " + sm.getAll());
        // Recherche Team par ID
        System.out.println("▶ TEST 9 : Recherche team par ID");
        Team found = st.getById(1);
        if (found != null)
            System.out.println("   ✅ Trouvée : " + found);
        else
            System.out.println("   ❌ Team introuvable");
        System.out.println();

        //chercher par le nom

        System.out.println("▶ TEST 10 : Recherche team par nom");
        Team foundByName = st.getByName("Les Lions");
        if (foundByName != null)
            System.out.println("   ✅ Trouvée : " + foundByName);
        else
            System.out.println("   ❌ Team introuvable");
        System.out.println();

    // Recherche Membership par ID
        System.out.println("▶ TEST 11 : Recherche membership par ID");
        Membership foundM = sm.getById(1);
        if (foundM != null)
            System.out.println("   ✅ Trouvé : " + foundM);
        else
            System.out.println("   ❌ Membership introuvable");
        System.out.println();
        // Recherche Membership par rôle
        System.out.println("▶ TEST 12 : Recherche membership par rôle");
        sm.getByRole("Joueur").forEach(mb ->
                System.out.println("   • ID=" + mb.getId_user() + " | " + mb.getRole_dans_equipe())
        );
        System.out.println();

// Tous les membres d'une team
        System.out.println("▶ TEST 13 : Tous les membres de la team ID=1");
        st.getMembershipsOfTeam(1).forEach(mt ->
                System.out.println("   • UserID=" + mt.getId_user() + " | Rôle=" + mt.getRole_dans_equipe())
        );
        System.out.println();
    }

}

