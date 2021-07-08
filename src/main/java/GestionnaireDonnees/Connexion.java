package GestionnaireDonnees;

import java.sql.*;

public class Connexion {
    private final String DBPath = "BD_SGE.db"; // le nom de la BD utilisee dans le projet
    private Connection connection; // Attribut de type Connection qui permet de faire la connection avec
                                        //le driver SQLite
    private Statement statement; // Attribut de type statement son role est d'executer les requêtes

    public Connexion() {
        connect();
    }

    /**
     * Fonction de connection, elle est déclanché une fois que l'utilisateur s'identifie
     * @param
     */
    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DBPath);
            statement = connection.createStatement();
            query("PRAGMA foreign_keys = ON ;");
            System.out.println("Connexion a " + DBPath + " avec succes");
        } catch (ClassNotFoundException notFoundException) {
            notFoundException.printStackTrace();
            System.out.println("Erreur de connexion");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("Erreur de connexion");
        }
    }

    /**
     * Fonction permettant l'execution de requêtes sql
     * @param requete
     * @return Sa valeur de retour est un ResultSet (un tableau de données contenant les resultats de la requête)
     * @throws SQLException
     */
    public ResultSet query(String requete) throws SQLException {
        ResultSet resultat = null;
        boolean s = true;
        Statement s2 = connection.createStatement();

        try {
            if(requete.contains("select") || requete.contains("SELECT"))
                 resultat = s2.executeQuery(requete);
            else {
                s = s2.execute(requete);
            }
                //s2.executeQuery(requete);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur dans la requete : " + requete);
        }

        return resultat;

    }


    /**
     * Fonction permettant de fermer la connection avec la base de donnees, elle est appellee lorsque
     * l'utilisateur ferme son application
     */
    public void close() {
        try {
            connection.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Base de donnée fermee");
    }
}
