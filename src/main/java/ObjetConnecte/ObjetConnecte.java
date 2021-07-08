package ObjetConnecte;

import  GestionnaireDonnees.*;
import CalculOptimisation.*;
import Interface.GestionObjetControlleur;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class ObjetConnecte {
    protected int idObjet;
    protected String nom;
    protected String type;
    protected String piece;
    protected int etat;
    protected String droitAcces;
    protected float consoHoraire;
    protected static int nbObjets;
    public static FXMLLoader loaderAcceuil;

    //constructeur
    public ObjetConnecte(String nom, String type, String piece, float consoHoraire, String droitAccess) throws SQLException {
        Routeur r1 = Routeur.getInstance();
        this.idObjet = r1.getID();
        this.nom = nom;
        this.type = type;
        this.piece = piece;
        this.etat = 0;
        this.consoHoraire = consoHoraire;
        this.droitAcces = droitAccess;
    }


    public ObjetConnecte(ResultSet rs) throws SQLException {
        if (rs.getRow() > 0) {
            this.idObjet = rs.getInt("num_objet");
            this.nom = rs.getString("nom");
            this.consoHoraire = rs.getFloat("consommation_horaire");
            this.type = rs.getString("type_objet");
            this.piece = rs.getString("nom_chambre");
            this.etat = rs.getInt("etat_objet");
            this.droitAcces = rs.getString("droit_acces");
        }
    }



    // constructeur par copie

    public ObjetConnecte(ObjetConnecte obj) throws SQLException {
        Routeur r1 = Routeur.getInstance();
        this.idObjet = r1.getID();
        nom = obj.nom;
        type = obj.type;
        piece = obj.piece;
        etat = obj.etat;
        consoHoraire = obj.consoHoraire;
        droitAcces = obj.droitAcces;
    }

    /*
     * Getters et setters des attributs
     */
    public int getId() {
        return idObjet;
    }

    public String getNom() {
        return nom;
    }

    public String getType() {
        return type;
    }


    public String getPiece() {
        return piece;
    }

    public int getEtat() {
        return etat;
    }

    public String getDroitAcces() {
        return droitAcces;
    }


    public float getConsoHoraire() {
        return consoHoraire;
    }

    public int getNbObjets() {
        return nbObjets;
    }

    public void setNom(String nom) {
        this.nom = nom;
        String sql = "UPDATE ObjetConnecte " +
                " SET nom = \""+ nom +"\""+
                " where num_objet = " + idObjet + " and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changeNom");
        r1.envoyerRequete(req);
    }

    public void setPiece(String piece) {
        this.piece = piece;
        String sql = "UPDATE ObjetConnecte " +
                " SET nom_chambre = \""+ piece +"\""+
                " where num_objet = " + idObjet + " and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changePiece");
        r1.envoyerRequete(req);
    }

    public void setConsoHoraire(float consoHoraire) {
        this.consoHoraire = consoHoraire;
        String sql = "UPDATE ObjetConnecte " +
                "SET consommation_horaire = " + consoHoraire +
                " where num_objet = " + idObjet + " and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changeConsoHoraire");
        r1.envoyerRequete(req);
    }

    public void setDroitAcces(String droit) {
        if(droit.equals("OK") || droit.equals("Interdit") || droit.equals("Restreint")) {
            this.droitAcces = droit;

            String sql = "UPDATE ObjetConnecte " +
                    "SET droit_acces = \"" + droit + "\"" +
                    " where num_objet = " + idObjet + " and mail_utilisateur =";
            Routeur r1 = Routeur.getInstance();
            r1.envoyerRequete(sql);
            String req = constuireRequeteHistorique("changeDroitAcces");
            r1.envoyerRequete(req);
        }
    }

    /*
     * Changer l'etat de l'objet  : venir la consomation de notre objet dans optimisation pour sauvegarder
     */
        public void changeEtat() throws SQLException, InterruptedException, IOException {
                Routeur r1 = Routeur.getInstance();
                if (this.getEtat() == 0) {
                    etat = 1;
                } else {
                    etat = 0;
                }
                String sql = "UPDATE ObjetConnecte " +
                        "SET etat_objet = " + this.getEtat() +
                        " where num_objet = " + idObjet + " and mail_utilisateur =";
                r1.envoyerRequete(sql);
                String req = constuireRequeteHistorique("changeEtat");
                r1.envoyerRequete(req);
                if (this.getEtat() != 0) {
                    r1.envoieConso(this.consoHoraire);
                } else {
                    r1.envoieConso((-1)*this.consoHoraire);
                }

                GestionObjetControlleur ac = loaderAcceuil.getController();

                ac.Eteindre(Optimisation.getConsoHoraireGlobale());

            }

    /*
     * Envoyer une alerte vers le module calcul pour le depacement de seuil de consommation
     * */
    public void envoyerAlerte() throws SQLException, IOException, InterruptedException {
        Routeur r1 = Routeur.getInstance();
        System.out.println("L'objet a envoye une alerte de dépassement de seuil: " + this.nom);
        r1.envoyerAlerte(this.idObjet);
    }

    // requete d'insertion qui permet d'integrer l'objet dans notre base
    public String construireRequeteObjet() {
        String s = "insert into ObjetConnecte values(" + idObjet + ", ,\"" + piece + "\",\"" + nom + "\",\"" + type + "\"," + etat + "," + consoHoraire + ",\"" + droitAcces + "\");";
        return s;
    }

    public abstract void envoyerDonnee();

    /*
     *  requete d'insertion de ligne  d'historique dans la bdd
     * */
    public abstract String constuireRequeteHistorique(String type);

    // on l'utilise pour les classe filles
    public abstract String construireRequeteFille();

    public String toString() {
        return "ObjetConnecte{" +
                "idObjet=" + idObjet +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", piece='" + piece + '\'' +
                ", etat=" + etat +
                ", droitAcces='" + droitAcces + '\'' +
                ", consoHoraire=" + consoHoraire +
                '}';
    }

    public String convertirDate(LocalDateTime ldt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ldt.format(formatter);
    }

    public boolean seuilAtteint() throws SQLException {
        if(this.type.equals("Climatiseur") || this.type.equals("EnceinteConnecte")
             || this.type.equals("Lampe") ||this.type.equals("Television") ) {
            System.out.println("Objet a optimiser est : "+this.nom);
            BDDSingleton bd = BDDSingleton.getInstance();
            String mail = bd.getMail_utilisateur();
            LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateActuelle = date.format(formatter);
            String requete = "select date_action from HistoriqueObjet ob1 where ob1.num_objet = " + this.idObjet +
                    " and ob1.type_operation = \"changeEtat\" and mail_utilisateur = " + mail +
                    " and ob1.num_ligne = (SELECT max(ob2.num_ligne) from HistoriqueObjet ob2" +
                    " where ob2.date_action < \"" + dateActuelle + "\" and ob2.type_operation = \"changeEtat\" " +
                    "and ob2.num_objet = ob1.num_objet and ob2.mail_utilisateur ="+mail+");";
            ResultSet rs = bd.requeteSelectForce(requete);
            String d = null;
            int etat;
            while (rs.next()) {
                d = rs.getString("date_action");
                System.out.println(d);
            }
            if (d != null) {
                System.out.println(this.type);
                System.out.println(this.nom);
                String[] d1 = d.split(" ");
                Duration duration = Duration.between(LocalDateTime.parse(d1[0] + "T" + d1[1]), date);
                long nb_seconde = duration.toSeconds();
                System.out.println("L'objet est allume depuis : " + nb_seconde + "s");
                System.out.println("Le seuil de son allumage = : " + recupDuree() + "s");
                if (recupDuree() == 0 || (float) (nb_seconde) < recupDuree()) {
                    System.out.println("Seuil non atteint");
                    return false;
                } else {
                    System.out.println("Seuil Atteint !");
                    return true;
                }
            }
        }
        return false;
    }

    public abstract long recupDuree();
}