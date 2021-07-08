package Thread;

import ObjetConnecte.*;
import CalculOptimisation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class ThreadOptiObjet extends Thread {

    private static Routeur r = Routeur.getInstance();
    static int tour = 0;
    /**
     * Méthode permettant de faire une optimisation automatique unique si le seuil d'un objet à été atteint
     * ou a été dépassé et d'attendre 2 minutes.
     * @throws  SQLException
     */
    static synchronized void optimisationAutomatique() throws SQLException {
        Optimisation optimisation = Optimisation.getInstance();
        try {
            tour++;
            System.out.println(currentThread().getName() + " effectue une optimisation unique " + tour);
            Map<Integer,ObjetConnecte> listeObj = r.getLesObjets();
            //System.out.println("Le Thread test seuil unique lance son traitement Num "+tour );
            for(Map.Entry m : listeObj.entrySet()) {
                ObjetConnecte ob = (ObjetConnecte) m.getValue();
                if(ob.getEtat() == 1 && !ob.getDroitAcces().equals("Interdit") && ob.seuilAtteint()) {
                    System.out.println("Le Thread test seuil unique a remarque une possibilite d'optimisation sur l'objet "+ob.getNom()+ "("+ob.getId()+")");
                    ob.envoyerAlerte();
                    System.out.println("Consultez vos notifications ! ");
                }
            }
            sleep(25000);

            //sleep(300000);
        } catch (SQLException | InterruptedException | IOException e){e.printStackTrace();}
    }

    /**
     * Méthode activée lors du lancement du thread. Elle effectue une optimisation du seuil des objets toutes les 2 minutes
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