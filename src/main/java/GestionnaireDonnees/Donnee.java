package GestionnaireDonnees;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * La classe donnee, comme présenté dans le cahier des spécification nous servira a génerer un historique pour
 * l'utilisateur, en effet à chaque création de donnée (appel au constructeur) une ligne d'historique utilisateur
 * est généré
 */
public class Donnee{
    private String typeOperation; // attributs de type String contenant le type d'operation declanché par l'utilisateur
    private LocalDateTime dateHeureDeclanchement; // attribut de type LocalDateTime, il nous servira a recupéré
                                                            //la date exacte ou l'utilisateur a déclanché son opération
    private String descriptionOperation; // un bref descriptif de l'opération demandee par l'utilisateur

    public Donnee() {
        typeOperation = "\0";
        dateHeureDeclanchement = null;
        descriptionOperation = "\0";
    }

    /**
     * Constructeur de la classe donnée, l'attibut dateHeureDeclanchement est directement initialisé a la date
     * du systeme
     * @param type_operation
     * @param description
     * @throws SQLException
     */
    public Donnee(String type_operation, String description) throws SQLException {

        this.typeOperation = type_operation;
        this.dateHeureDeclanchement = LocalDateTime.now(); // on récupére l'heure de la création de l'objet
        this.descriptionOperation = description; // calcul/ histo => savegardé dans le fichier tel ou tel

        /*
        On recupere l'instance de notre BDDSingleton et on envoie la requete de generation d'historique
         */
        BDDSingleton bdd = BDDSingleton.getInstance();
        String requete = construireRequete();
        bdd.decisionRequete(requete);
    }

    public void setTypeOperation(String type) {
        this.typeOperation = type;
    }

    public void setDateHeureDeclanchement(LocalDateTime dateHeure) {
        this.dateHeureDeclanchement = dateHeure;
    }

    public void setDescriptionOperation(String description) {
        this.descriptionOperation = description;
    }


    public String getTypeOperation() {
        return typeOperation;
    }

    public LocalDateTime getDateHeureDeclanchement() {
        return dateHeureDeclanchement;
    }

    public String getDescriptionOperation() {
        return descriptionOperation;
    }

    /**
     * Cette méthode permet la construction de la requete permettant l'ajout d'une ligne dans l'historique utilisateur,
     * elle est appelé directement dans le constructeur de la classe
     * @return la requete à lancer
     */
    public String construireRequete() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateDeclachement = dateHeureDeclanchement.format(formatter);

        String requete =
                "insert into HistoriqueUtilisateur values" +
                        " ( , ," +
                        "\"" + typeOperation + "\"," +
                        "\"" + dateDeclachement + "\"," +
                        "\"" + descriptionOperation + "\");";

        return requete;
    }

    @Override
    public String toString() {
        return "Donnee :" +
                "|\ttype d'operation = " + typeOperation + "\n" +
                "|\t, heure de declanchement = " + dateHeureDeclanchement + "\n" +
                "|\t, description de l'operation = " + descriptionOperation + "\n" +
                "|_";
    }
}