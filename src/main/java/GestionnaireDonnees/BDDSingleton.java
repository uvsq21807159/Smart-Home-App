package GestionnaireDonnees;
import ObjetConnecte.*;

import javax.security.auth.Refreshable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BDDSingleton {

    private static BDDSingleton instance = new BDDSingleton(); // attribut de type BDDSingleton,
    // son utilisation préconise, une instance unique de la classe
    private  Connexion connection=null; // attribut de type connection (servira à etablir la connexion avec le driver SQLite)
    private float prixHC; //attribut de type float, il contiendra le prixHC correspondant à la configuration utilisateur
    private float prixHP; //attribut de type float, il contiendra le prixHC correspondant à la configuration utilisateur
    private String mail_utilisateur; //attribut de type String, il contiendra le mail de l'utilisateur (afin de mieux compléter nos requêtes)

    /**
     * Constructeur privé de la classe BDDSingleton
     */
    private BDDSingleton(){
        connection= new Connexion();
        prixHC = 0;
        prixHP = 0;
        mail_utilisateur = "NO_USER";
    }

    /**
     * Methode permettant de retourner une référence vers l'instance unique de notre BDDSingleton
     * @return
     */
    public static BDDSingleton getInstance(){
        return  instance;
    }

    /**
     * Cette méthode, assure une meilleur prise en main pour les autres modules du logiciel,
     * en effet son usage est purement pratique, elle prend la requête(pré requete) écrite en paramètres
     * et déclanche ensuite la fonction correspondant à la requete
     * @param requete
     * @return
     * @throws SQLException
     */
    public ResultSet decisionRequete(String requete) throws SQLException {

        ResultSet rs = null;

        if(requete.contains("select") ||requete.contains("SELECT"))
            rs = requeteSelection(requete);
        else if( requete.contains("update") || requete.contains("UPDATE") )
            requeteModification(requete);
        else if(requete.contains("insert") || requete.contains("INSERT") )
            requeteInsertion(requete);
        else if(requete.contains("delete") || requete.contains("DELETE"))
            requeteSuppression(requete);

        return  rs;
    }

    /**
     * Fonction représentant l'algorithme d'insertion, elle prend une requete "pré faite" en paramétre
     * suivant un format déjà établie et parse ensuite la requête afin d'ajouter les champs requis
     * Exemple => dans le cas d'une insertion dans l'historique utilisateur la requete vaudra :
     *  "insert into HistoriqueUtilisateur values ( , ,"typeOperation ","dateDeclachement" +"descriptionOperation");
     *  La fonction se permettra alors d'ajouter les 2 champs manquants afin de construire une requete executable
     * @param requete
     * @throws SQLException
     */
    public void requeteInsertion(String requete) throws SQLException {
        ResultSet rs =null;

        if(requete.contains(" Utilisateur")){//l'espace derriere me permet de savoir si c une table
            //utilisateur ou HistoriqueUtilisateur
            rs = connection.query(requete); // dans ce cas on execute directement la requete
            return; // on sort de la méthode;
        }

        String requeteMax="";

        int max = 0;

        if(requete.contains("HistoriqueObjet")){
            //je dois d'abbord recupéré le numéro de l'objet à intégrer à la requete
            // on a une requete de type " insert into HistoriqueObjet values( ,8, , complet....) "
            String strid = "";
            for (int i=0 ; i< requete.length(); i++){
                if( requete.charAt(i) == ',' &&  requete.charAt(i+1) != ' '){
                    int j = i+1;
                    while (requete.charAt(j) != ',' ){
                        strid += requete.charAt(j);
                        j++;
                    }
                    break;
                }
            }

            requeteMax= "select MAX(HistoriqueObjet.num_ligne) as MAX from HistoriqueObjet" +
                    " where HistoriqueObjet.num_objet ="+ strid + " and mail_utilisateur="+ mail_utilisateur+";";

            rs = connection.query(requeteMax); // on recupere le max de l'une des requetes
            try {
                max = rs.getInt("MAX");
                max++;
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            String numLigne = String.valueOf(max);


            String requeteFinal ="";
            int suite_normale=0;
            int nbVirgules = 0;
            for(int i=0 ; i<requete.length();i++) {
                //je veux recuperer l'indice auquel la parenthése commence pour completer
                if (requete.charAt(i) == '(') {
                    requeteFinal = requete.substring(0, i + 1); //on met la prem partie de la requete
                    // dans requeteFinal (i+1) car l'indice i est exclusif
                    requeteFinal +=(numLigne);
                    requeteFinal += ',';
                    requeteFinal +=strid;
                    requeteFinal += ',';
                    requeteFinal += mail_utilisateur;
                }
                if (requete.charAt(i) == ','){
                    nbVirgules++;
                    if (nbVirgules == 3){
                        suite_normale= i;
                        break;
                    }
                }
            }

            requeteFinal +=requete.substring(suite_normale);

            rs = connection.query(requeteFinal);
            return;

        }
        else if(requete.contains("Configuration_SmartHome")){
            requeteMax= "select MAX(Configuration_SmartHome.id_configuration) as MAX from" +
                    " Configuration_SmartHome;";
        }
        else if(requete.contains("HistoriqueUtilisateur")){
            requeteMax= "select MAX(HistoriqueUtilisateur.ligne_historique) as MAX from HistoriqueUtilisateur"+
                    " where mail_utilisateur="+mail_utilisateur+";";
        }

        //sinon notre requete ressemble à requete.contains("Chambre") || requete.contains("ObjetConnecte")
        //ou meme des sous classes
        else{
            int suite_normale = 0;
            String requete_finale = "";

            for(int i=0; i < requete.length();i++){
                if(requete.charAt(i)==',' && requete.charAt(i+1) ==' ') {
                    requete_finale = requete.substring(0, i + 1); //on récup requete jusqu'a (chambre,
                    //ou bien (num_obj,
                }
                // on recup la suite de la requete normale
                // ,surface) cas de chambre
                // ,nomchambre,....) cas objet
                if(requete.charAt(i)==',' && requete.charAt(i+1) != ' '){
                    suite_normale = i;
                    break;
                }
            }
            requete_finale +=mail_utilisateur;
            requete_finale +=requete.substring(suite_normale,requete.length());

            rs= connection.query(requete_finale);
            return; // on sort le méthode
        }
        //fin du programme sous classe / objet / chambre

        // ici on revient au cas d'avant (celui de user, histuser, configsh ,histobj)
        rs = connection.query(requeteMax); // on recupere le max de l'une des requetes
        try {
            max = rs.getInt("MAX");
            max++;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        String requeteFinal ="";
        int suite_normale=0;
        for(int i=0 ; i<requete.length();i++){
            //je veux recuperer l'indice auquel la parenthése commence pour completer
            if (requete.charAt(i)=='('){
                requeteFinal = requete.substring(0,i+1); //on met la prem partie de la requete
                // dans requeteFinal (i+1) car l'indice i est exclusif

            }
            //on doit ensuite savoir a quel moment la suite de la requete n'est pas à touché
            //c'est a dire => on doit juste completer le numéro de ligne et le mail de l'utilisateur
            if(requete.charAt(i) == ',' && requete.charAt(i+1) != ' '){
                suite_normale=i; //c'est ici que commence notre suite de manière sure
                break;
            }
        }
        String tmp = String.valueOf(max);
        requeteFinal +=tmp;//la valeur de max est +=éné a la requete (max
        requeteFinal += ','; // on sépare (max,
        requeteFinal += mail_utilisateur; //(max,mail
        // enfin on ajoute la suite normale de la forme :   " ,   );"
        requeteFinal += requete.substring(suite_normale);

        rs = connection.query(requeteFinal);
    }

    /**
     * Fonction permettant d'executer une requete de Selection
     * @param requete
     * @return son type de retour est un ResultSet
     * @throws SQLException
     */
    public ResultSet requeteSelection(String requete) throws SQLException {
        ResultSet r1 = null;
        //la requete doit etre organisé de sorte que seul le mail de l'utilisateur manque
        //exp select * from ObjetConnecte where mail_utilisateur = ...
        //    select from HistoriqueObjet where dateHeureDeclanchement between " 16mars " "12avril"
        //  and mail_utilisateur =....

        requete += mail_utilisateur;
        requete += ';';
        r1 = connection.query(requete);
        return r1;
    }

    /**
     * Fonction permettant la modification de tables dans la BD
     * @param requete Elle n'a aucun type de retour
     * @throws SQLException
     */
    public void requeteModification(String requete) throws SQLException {
        // meme resonnement la seule information manquante doit etre le mail de l'utilisateur

        requete +=(mail_utilisateur);
        requete +=';';
        ResultSet r1 = connection.query(requete);
    }

    /**
     * Fonction permettant la suppression de tables dans la BD
     * @param requete
     * @throws SQLException
     */
    public void requeteSuppression(String requete) throws SQLException {
        requete +=(mail_utilisateur);
        requete +=';';
        ResultSet r1 = connection.query(requete);
        //System.out.println("Suppression reussi");
    }

    /**
     * Fonction permettant de mettre à jour les attributs PrixHC et le PrixHP de l'utilisateur
     * Elle se déclanche automatiquement une fois que le système vérifie les identifiants de l'utilisateur
     * (fonction verifierIdentifiants de la meme classe)
     * @throws SQLException
     */
    public void setPrixHC_PrixHP() throws SQLException {
        String requete1 = "select prixHC from Utilisateur u1, Configuration_SmartHome c1 where " +
                "c1.mail_utilisateur = u1.mail_utilisateur and u1.mail_utilisateur =" + mail_utilisateur;
        String requete2 = "select prixHP from Utilisateur u1, Configuration_SmartHome c1 where" +
                " c1.mail_utilisateur = u1.mail_utilisateur and u1.mail_utilisateur =" + mail_utilisateur;

        ResultSet rs = connection.query(requete1);
        while (rs.next())
            prixHC = rs.getFloat("PrixHC");

        rs = connection.query(requete2);
        while (rs.next())
            prixHP = rs.getFloat("PrixHP");
    }

    /**
     * Cette fonction permet l'execution direct d'une requete ( elle permet de fournir la possibilité aux autre modules
     * d'executer des requetes complexes ne nécessitant pas une insertion "Propre aux cas de select complexes")
     * @param requete
     * @return ResultSet
     * @throws SQLException
     */
    public ResultSet requeteSelectForce(String requete) throws SQLException {
        ResultSet rs = connection.query(requete);
        return  rs;
    }

    /**
     * Fonction qui permet à l'utilisateur de declancher lui même une sauvegarde
     *  Bien quele systeme prend déja le cas de sauvegardes automatiques
     *  ici je déclanche de manière forcé un ajout de ligne d'historique dans tout les objets
     */
    public void sauvegarderTout() {

        Routeur r1 = Routeur.getInstance();

        Map<Integer,ObjetConnecte> lesObjets = r1.getLesObjets();

        for (Map.Entry mapentry : lesObjets.entrySet()) {
            ObjetConnecte ob = (ObjetConnecte) mapentry.getValue();
            // on declanche un changement qui engendrera une requete (ici je recupere
            // simplement le nom de l'objet et je le set pour forcer le declanchement d'une requete
            // d'historique (permettre à l'utilisateur de sauvegarder lui même)
            String nom = ob.getNom();
            ob.setNom(nom);
        }

    }

    /**
     * Fonction permettant de verifier si l'utilisateur a déja un compte, elle met à jour dans ce cas les attributs
     * PrixHP,PrixHC, mail_utilisateur, dans le cas contraire la fonction retourne FAUX
     * @param mailUtilisateur
     * @param mdp
     * @return booleen
     * @throws SQLException
     */
    public boolean verifierIdentifiants(String mailUtilisateur, String mdp) throws SQLException {
        String requete = "select * from Utilisateur where " +
                "Utilisateur.mail_utilisateur =" +mailUtilisateur+"" +
                " and Utilisateur.mdp ="+mdp+";";
        ResultSet rs = connection.query(requete);

        boolean test = false;

        while(rs.next()){
            test = true;
        }

        if(test == true){
            this.mail_utilisateur = mailUtilisateur;
            setPrixHC_PrixHP();
        }
        return test;
    }

    /**
     * Fonction permettant la génération des Objets de la Smart Home au lancement de l'application
     * @return ArrayList<ObjetConnecte> à manipuler sur l'interface, et ajoute en même temps les objets
     * au Routeur
     * @throws SQLException
     */
    public ArrayList<ObjetConnecte> genererObjetsSmartHome() throws SQLException {
        ArrayList <ObjetConnecte> collec_objets = new ArrayList<>();

        String requete_select = "select * from ObjetConnecte Where mail_utilisateur = "+mail_utilisateur+";";
        ResultSet rs = connection.query(requete_select);
        String type_objet;
        String requete_fille;

        while(rs.next()){
            type_objet = rs.getString("type_objet");
            ResultSet rs2 = null;
            int id = rs.getInt("num_objet");

            if(type_objet.equals("Lampe")) {
                requete_fille = "select * from Lampe where num_objet =" + id + " and " +
                        "mail_utilisateur =" + mail_utilisateur + ";";
                rs2 = connection.query(requete_fille);
                rs2.next();
                if (rs2.getRow() > 0) {
                    Lampe c = new Lampe(rs, rs2);
                    Routeur r1 = Routeur.getInstance();
                    collec_objets.add(c);
                    r1.ajouterRefObjet(c);
                }

            }
            else if(type_objet.equals("Chauffage")) {
                requete_fille = "select * from Chauffage where num_objet =" + id + " and " +
                        "mail_utilisateur =" + mail_utilisateur + ";";
                rs2 = connection.query(requete_fille);
                rs2.next();
                if (rs2.getRow() > 0) {
                    Chauffage c = new Chauffage(rs, rs2);
                    Routeur r1 = Routeur.getInstance();
                    collec_objets.add(c);
                    r1.ajouterRefObjet(c);
                }

            }
            else if(type_objet.equals("Climatiseur")) {
                requete_fille = "select * from Climatiseur where num_objet =" + id + " and " +
                        "mail_utilisateur =" + mail_utilisateur + ";";
                rs2 = connection.query(requete_fille);
                rs2.next();
                if (rs2.getRow() > 0) {
                    Climatiseur c = new Climatiseur(rs, rs2);
                    Routeur r1 = Routeur.getInstance();
                    collec_objets.add(c);
                    r1.ajouterRefObjet(c);
                }

            }
            else if(type_objet.equals("CapteurPresence")) {
                requete_fille = "select * from CapteurPresence where num_objet =" + id + " and " +
                        "mail_utilisateur =" + mail_utilisateur + ";";
                rs2 = connection.query(requete_fille);
                rs2.next();
                if (rs2.getRow() > 0) {
                    CapteurPresence c = new CapteurPresence(rs, rs2);
                    Routeur r1 = Routeur.getInstance();
                    collec_objets.add(c);
                    r1.ajouterRefObjet(c);
                }
                //break;
            }
            else if(type_objet.equals("Enceinte")) {
                requete_fille = "select *from Enceinte where num_objet =" + id + " and " +
                        "mail_utilisateur =" + mail_utilisateur + ";";
                rs2 = connection.query(requete_fille);
                rs2.next();
                if (rs2.getRow() > 0) {
                    EnceinteConnecte c = new EnceinteConnecte(rs, rs2);
                    Routeur r1 = Routeur.getInstance();
                    collec_objets.add(c);
                    r1.ajouterRefObjet(c);
                }

            }
            else if(type_objet.equals("Machine_a_laver")) {
                requete_fille = "select * from Machine_a_laver where num_objet =" + id + " and " +
                        "mail_utilisateur =" + mail_utilisateur + ";";
                rs2 = connection.query(requete_fille);
                rs2.next();
                if (rs2.getRow() > 0) {
                    MachineALaver c = new MachineALaver(rs, rs2);
                    Routeur r1 = Routeur.getInstance();
                    collec_objets.add(c);
                    r1.ajouterRefObjet(c);
                }

            }
            else if (type_objet.equals("Refrigerateur")) {
                requete_fille = "select * from Refrigerateur where num_objet =" + id + " and " +
                        "mail_utilisateur =" + mail_utilisateur + ";";
                rs2 = connection.query(requete_fille);
                rs2.next();
                if (rs2.getRow() > 0) {
                    Refrigerateur c = new Refrigerateur(rs, rs2);
                    Routeur r1 = Routeur.getInstance();
                    collec_objets.add(c);
                    r1.ajouterRefObjet(c);
                }

            }
            else if(type_objet.equals("Television")) {
                requete_fille = "select * from Television where num_objet =" + id + " and " +
                        "mail_utilisateur =" + mail_utilisateur + ";";
                rs2 = connection.query(requete_fille);
                rs2.next();
                if (rs2.getRow() > 0) {
                    Television c = new Television(rs, rs2);
                    Routeur r1 = Routeur.getInstance();
                    collec_objets.add(c);
                    r1.ajouterRefObjet(c);
                }

            }
            else if(type_objet.equals("Thermostat")) {
                requete_fille = "select * from Thermostat where num_objet =" + id + " and " +
                        "mail_utilisateur =" + mail_utilisateur + ";";
                rs2 = connection.query(requete_fille);
                rs2.next();
                if (rs2.getRow() > 0) {
                    Thermostat c = new Thermostat(rs, rs2);
                    Routeur r1 = Routeur.getInstance();
                    collec_objets.add(c);
                    r1.ajouterRefObjet(c);
                }

            }


        }
        return collec_objets;
    }

    /**
     * Fonction permettant la génération de fichier d'historique (option telecharger fichier de l'interface)
     * @param f le nom du fichier à générer
     * @param liste_a_generer une liste de String
     */
    public void genererFichier(File f, ArrayList<String> liste_a_generer){
        //String chemin = path_fichier;
        try {
            // Recevoir le fichier
            //File f = new File(chemin);
            // Créer un nouveau fichier
            // Vérifier s'il n'existe pas
            if (f.createNewFile())
                System.out.println("File created");
            else
                System.out.println("File already exists");
        }
        catch (Exception e) {
            System.err.println(e);
        }
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(f.getPath(), "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for(int i = 0 ; i < liste_a_generer.size() ; i ++){
            writer.println(liste_a_generer.get(i));
        }
        /*writer.println("La ssss ligne");
        writer.println("La deuxième ligne");*/
        writer.close();

    }

    /**
     * Fonction permettant l'importation de fichier SQL à partir de l'application
     * Elle prend en parametres le nom du fichier à importer
     * et génére le fichier dans le chemin "files/nom.sql"
     * @param nom_fichier
     * @throws SQLException
     * @throws IOException
     */
    public void importerFichierSql(String nom_fichier) throws SQLException, IOException {
        String extension = nom_fichier.substring(nom_fichier.length()-3);

        if(!extension.equals("sql")){
            System.out.println("Le fichier à importer n'est pas un .sql 'FORMAT NON SUPPORTE' ");
            return;
        }

        String requete_a_lancer = "";
        String ligne = "";
        String str = new String(Files.readAllBytes(Paths.get(nom_fichier)));

        for (int i = 0 ; i<str.length(); i++){
            requete_a_lancer += str.charAt(i);
            if(str.charAt(i) == ';'){
                this.connection.query(requete_a_lancer);
                requete_a_lancer = "";
            }
        }

    }

    /**
     * Fonction permettant la fermeture de connexion (à declancher lors de la fermeture de l'application)
     */
    public void fermerConnexion(){
        connection.close();
    }

    public float getPrixHC() {
        return prixHC;
    }

    public float getPrixHP(){
        return  prixHP;
    }

    public String getMail_utilisateur() {
        return  mail_utilisateur;
    }
}
