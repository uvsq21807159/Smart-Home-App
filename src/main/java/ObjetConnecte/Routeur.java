package ObjetConnecte;

import CalculOptimisation.*;
import GestionnaireDonnees.BDDSingleton;
import GestionnaireDonnees.BDDSingleton;
import GestionnaireDonnees.Donnee;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import  java.util.*;


public class Routeur {

    private static Routeur instance = new Routeur();
    // la reference est l'objet connecté
    private Map<Integer,ObjetConnecte> lesObjets;


    private Routeur() {
        lesObjets = new HashMap<>();
    }

    /** Point d'accès pour l'instance unique du singleton */
    public static Routeur getInstance()
    {
        return instance;
    }


    public Map<Integer,ObjetConnecte> getLesObjets(){
        return  lesObjets;
    }


    public void ajouterRefObjet (ObjetConnecte obj) {

        if(!lesObjets.containsKey(obj.getId()))
            this.lesObjets.put(obj.getId(), obj);
       // else
       //     System.out.println("L'objet est deja inclus dans la map");
    }

    public void supprimerRef(int idObjet) throws SQLException {
        Donnee d = new Donnee("Suppression d'objet", "L'utilisateur a supprime l'objet "+lesObjets.get(idObjet).getNom());
        String sql = "DELETE from ObjetConnecte WHERE num_objet ="+ idObjet +" AND mail_utilisateur =";
        BDDSingleton bdd = BDDSingleton.getInstance();
        bdd.decisionRequete(sql);
        this.lesObjets.remove(idObjet);
    }

    public String getPositionUtilisateur(int idObjet) throws SQLException {
        BDDSingleton base = BDDSingleton.getInstance();
        ResultSet result=base.requeteSelection
                ("SELECT placementUtilisateur,nom_chambre FROM CapteurPresence c,ObjetConnecte obj" +
                        " WHERE obj.num_objet=c.num_objet AND c.mail_utilisateur=");
        ResultSet result1 = base.requeteSelection
                ("SELECT nom_chambre FROM ObjetConnecte" +
                        " WHERE num_objet=" +  idObjet + " AND mail_utilisateur=");

        if (result1.next()) {
            while (result.next()) {
                if (result1.getString("nom_chambre").equals(result.getString("nom_chambre"))) {
                    return result.getString("placementUtilisateur");
                }
            }
        }

       return "Present";
    }

    public void envoyerAlerte(int idObjet) throws SQLException, IOException, InterruptedException {
        Optimisation op1 = Optimisation.getInstance();
        op1.optimiserConsommationUnique(idObjet,getPositionUtilisateur(idObjet));
    }

    public void envoieConso(float consoH) throws SQLException, IOException, InterruptedException {
        Optimisation opt = Optimisation.getInstance();
        if (consoH < 0) {
            opt.decrementer(consoH);
        }
        else{
            opt.incrementer(consoH);
        }
    }

    public void envoyerRequete(String requete) {
        BDDSingleton bd = BDDSingleton.getInstance();
        try {
            bd.decisionRequete(requete);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //elle retourne la reference d'un objet
    public ObjetConnecte fournirReference(int idObj) {
        return  lesObjets.get(idObj);
    }


    public void libererRouteur() {
        this.lesObjets.clear();
    }


    /**
     * Fonction qui retourne les références sur tout les objets du meme type
     * @param type_objet
     * @return
     */
    public ArrayList<ObjetConnecte> fournirReference(String type_objet)
    {

        ArrayList<ObjetConnecte> listObjet = new ArrayList<>();

        for (Map.Entry mapentry : lesObjets.entrySet()) {
            ObjetConnecte ob = (ObjetConnecte) mapentry.getValue();
            if(ob.type.equals(type_objet)) {
                Chauffage c = (Chauffage) ob;
                listObjet.add(c);
            }
        }

        return  listObjet;
    }



    public ArrayList<ObjetConnecte> fournirReference(int[] idObj)
    {

        ArrayList<ObjetConnecte> listObjet = new ArrayList<>();

        for(int i = 0; i < idObj.length;i++)
        {
            // on ajoute toutes les references des objets dans une liste (listObjet)
            listObjet.add(fournirReference(idObj[i]));
        }

        return  listObjet;

    }

    //fonction qui renvoie ResultSet getID qui va etre affecté au nouvel objet ajouté par l'utilisateur
    public int getID() throws SQLException {
        BDDSingleton base = BDDSingleton.getInstance();
        ResultSet result = base.requeteSelectForce("SELECT num_objet FROM ObjetConnecte WHERE mail_utilisateur="+base.getMail_utilisateur()+" ORDER BY num_objet;");
        int i = 1;
        while (result.next()) {
            if (result.getInt( "num_objet") != i) {
                return i;
            }
            i++;
        }
        return i;
    }
}
