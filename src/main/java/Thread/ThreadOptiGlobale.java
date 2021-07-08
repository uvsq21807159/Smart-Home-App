package Thread;

import CalculOptimisation.*;

import java.io.IOException;
import java.sql.SQLException;

public class ThreadOptiGlobale extends Thread {
    static int tour = 0;
    /**
     * Méthode permettant de faire une optimisation automatique globale au bout de 5 minutes écoulées.
     * @throws  SQLException
     */
    static synchronized void optimisationAutomatique() throws SQLException {
        Optimisation optimisation = Optimisation.getInstance();
        try {
            //sleep(120000);
            tour++;
            System.out.println(currentThread().getName() + " effectue une optimisation globale " + tour);
            optimisation.optimiserConsommationGlobale();
            sleep(35000);
        } catch (SQLException | InterruptedException | IOException e){e.printStackTrace();}
    }

    /**
     * Méthode activée lors du lancement du thread. Elle effectue une optimisation globale toutes les 5 minutes
     * tant que l'application est ouverte.
     * @Override
     */
    @Override
    public void run() {
        while(true){
            try {
                optimisationAutomatique();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}