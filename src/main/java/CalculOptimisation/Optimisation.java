package CalculOptimisation;

import GestionnaireDonnees.*;
import Interface.GestionObjetControlleur;
import Interface.NotificationsControlleur;
import ObjetConnecte.*;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe Optimisation
 */

//

public class Optimisation {

    public static FXMLLoader loaderDeNotif;
    private  float seuilConsoGlobal ;
    public static float consoHoraireGlobale;
    private static Optimisation  instance = null;



    /**
     * Constructeur de la classe Optimisation
     * @throws SQLException
     */
    private Optimisation() throws  SQLException {
        BDDSingleton base = BDDSingleton.getInstance();
        consoHoraireGlobale = 0;
        for (int i = 0; i < GestionObjetControlleur.recevoirObjets.size(); i++)
        {
            if (GestionObjetControlleur.recevoirObjets.get(i).getEtat() == 1)
                consoHoraireGlobale = consoHoraireGlobale + GestionObjetControlleur.recevoirObjets.get(i).getConsoHoraire();

        }

        ResultSet result1 = base.requeteSelection
                ("SELECT seuil_maison FROM Configuration_SmartHome WHERE mail_utilisateur =");
        if(result1.next())
            this.seuilConsoGlobal = result1.getFloat("seuil_maison");

    }



    /**
     * methode 'optimiserConsommationUnique' qui permet de faire une optimisation sur un objet
     * @param idObjet
     * @param positionUtilisateur
     * @throws SQLException
     * @throws InterruptedException
     * @throws IOException
     */
    public void optimiserConsommationUnique(int idObjet,String positionUtilisateur) throws SQLException, InterruptedException, IOException {
        NotificationsControlleur c = loaderDeNotif.getController();

        BDDSingleton base = BDDSingleton.getInstance();
        ResultSet result = base.requeteSelection
                ("SELECT droit_acces FROM ObjetConnecte WHERE num_objet = "+idObjet+
                        " AND mail_utilisateur=");

        if(result.next()){
            Routeur ro = Routeur.getInstance();
            if(ro.fournirReference(idObjet).getEtat()==1) {

                if (result.getString("droit_acces").equals("OK")) {

                    if (positionUtilisateur.equals("Present")) {
                        c.AjouterNotification("   Est ce que vous voulez eteindre l'objet " + Routeur.getInstance().fournirReference(idObjet).getNom() + " qui est  dans la piéce occupee : " + Routeur.getInstance().fournirReference(idObjet).getPiece(), 1, idObjet);
                        //System.out.println("Voulez vous eteindre l'objet "+idObjet+" ?");
                    } else {
                        securiteConfort(idObjet);
                        c.AjouterNotification("   Optimisation : l'objet : " + Routeur.getInstance().fournirReference(idObjet).getNom() + " qui est  dans la piéce : " + Routeur.getInstance().fournirReference(idObjet).getPiece() + " a été éteint", 0, idObjet);
                    }
                } else {
                    if (result.getString("droit_acces").equals("Restreint")) {
                        c.AjouterNotification("   Est ce que vous voulez eteindre l'objet " + Routeur.getInstance().fournirReference(idObjet).getNom() + " qui est  dans la piéce : " + Routeur.getInstance().fournirReference(idObjet).getPiece() , 1, idObjet);
                    }/* else {
                        c.AjouterNotification("   Attention, la consommationde de : " + Routeur.getInstance().fournirReference(idObjet).getNom() + " qui est  dans la piéce : " + Routeur.getInstance().fournirReference(idObjet).getPiece(), 0, idObjet);
                    }*/
                }
            }

        }

    }

    /**
     * methode 'optimiserConsommationGlobale' qui permet de faire une optimisation sur la maison
     * @throws SQLException
     * @throws InterruptedException
     * @throws IOException
     */
    public void optimiserConsommationGlobale() throws SQLException, InterruptedException, IOException {
        Routeur routeur = Routeur.getInstance();
        BDDSingleton base = BDDSingleton.getInstance();
        ResultSet result = base.requeteSelection
                ("SELECT num_objet FROM ObjetConnecte WHERE  mail_utilisateur =");
        while(result.next()){
            if(routeur.fournirReference(result.getInt("num_objet")).getEtat() == 1) {
                optimiserConsommationUnique(result.getInt("num_objet"), routeur.getPositionUtilisateur(result.getInt("num_objet")));
            }
        }
    }

    /**
     * methode 'securiteConfort' qui permet d'appliquer l'optimisation sur un objet
     * @param idObjet
     * @throws SQLException
     * @throws InterruptedException
     * @throws IOException
     */
    public void securiteConfort(int idObjet) throws SQLException, InterruptedException, IOException {
        Routeur routeur = Routeur.getInstance();
        ObjetConnecte objet =  routeur.fournirReference(idObjet);
        objet.changeEtat();
    }

    /**
     * methode 'incrementer' qui permet de rajouter la consoHoraire d'un objet qu'on a allumé
     * à la consoHoraire globale de la maison
     * @param consoHoraireObjet
     * @throws SQLException
     * @throws InterruptedException
     * @throws IOException
     */
    public void incrementer(float consoHoraireObjet) throws SQLException, InterruptedException, IOException {

        consoHoraireGlobale = consoHoraireGlobale + consoHoraireObjet;
        if(consoHoraireGlobale >= this.seuilConsoGlobal ){
            optimiserConsommationGlobale();
        }
    }

    /**
     * methode 'decrementer' permet de soustraire la consoHoraire d'un objet qu'on a eteint
     * à la consoHoraire globale de la maison
     * @param consoHoraireObjet
     */
    public void decrementer(float consoHoraireObjet){
        consoHoraireGlobale = consoHoraireGlobale + consoHoraireObjet;
    }

    /**
     * methode 'setSeuilConsoGlobale' permet de modifier le seuil de la maison
     * @param seuilConsoGlobale
     */
    public void setSeuilConsoGlobale(float seuilConsoGlobale){
        this.seuilConsoGlobal = seuilConsoGlobale;
    }

    /**
     * methode 'getConsoHoraireGlobale' permet de recuperer le seuil de la  maison
     * @return
     */
    public float getSeuilConsoGlobale(){
        return this.seuilConsoGlobal;
    }

    /**
     * methode 'getConsoHoraireGlobale' permet de recuperer la consoHoraire globale de la maison
     * @return
     */
    public static float getConsoHoraireGlobale(){
        return  consoHoraireGlobale;
    }

    /**
     * methode 'getInstance' permet de retour
     * @return
     * @throws SQLException
     */
    public static synchronized Optimisation getInstance() throws SQLException{
        if(instance == null){
            instance = new Optimisation();
        }
        return instance;
    }

    public void setInstance(){
        instance=null;
    }
}