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
    }
}
