package CalculOptimisation;

import GestionnaireDonnees.*;
import java.sql.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Classe Calcul
 */

public class Calcul {

    /**
     * Attributs de la classe
     */
    private LocalDateTime dateDeb;
    private LocalDateTime dateFin;
    private ArrayList<Resultat> resultat;

    /**
     * Constructeur de la classe
     * @param dateDeb Date de début de la période saisie par l'utilisateur pour faire un calcul
     * @param dateFin Date de fin de la période saisie par l'utilisateur pour faire un calcul
     */
    public Calcul(LocalDateTime dateDeb,LocalDateTime dateFin){
        this.dateDeb = dateDeb;
        this.dateFin = dateFin;
        resultat = new ArrayList<Resultat>();
    }

    /**
     * methode "nombreJours()" elle permet de calculer le nombre de jours entre dateDeb et dateFin
     * @return le nombre de jours entre dateDeb et dateFin
     */
    public long nombreJours(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime temp = dateDeb;
        String string = dateDeb.format(formatter);
        temp = LocalDateTime.parse(string+"T00:00:00");
        return ChronoUnit.DAYS.between(dateDeb,dateFin) + 1;
    }


    /**
     * methode "convertirDateTime" qui convertit une date qui est en String to LocalDateTime
     * @param datetime la date récuperer depuis la base de données.
     * @return la date en LocalDateTime.
     */
    public LocalDateTime convertirDateTime(String datetime){
        String[] a=datetime.toString().split(" ");
        return LocalDateTime.parse(a[0]+"T"+a[1]);
    }

    /**
     * methode "calculConso" qui permet de calculer la consommation d'un objet  entre date1 et date2.
     * @param date1
     * @param date2
     * @return la consommation d'un objet entre les deux dates passées en parametre.
     */
    public float calculConso(LocalDateTime date1,LocalDateTime date2,float consoHoraire){
        Duration duree = Duration.between(date1,date2);
        float conso = (float) (consoHoraire * duree.toSeconds()) / (float) 3600;
        return conso;
    }

    /**
     * methode 'calculAllumageHP' permet de calculer la durée d'allumage de l'objet entre debHeureP et finHeureP
     * @param date1
     * @param date2
     * @param finHeureP
     * @param debHeureP
     * @return la durée d'allumage de l'objet entre debHeureP et finHeureP
     */
    public long calculAllumageHP(LocalDateTime date1,LocalDateTime date2,LocalDateTime debHeureP,LocalDateTime finHeureP){
        Duration duree;
        if(debHeureP.compareTo(date1) >= 0){
            if(date2.compareTo(finHeureP) >= 0){
                duree = Duration.between(debHeureP,finHeureP);
                return duree.toSeconds();
            }else{
                if(date2.compareTo(debHeureP) >= 0){
                    duree = Duration.between(debHeureP,date2);
                    return duree.toSeconds();
                }else{
                    return 0;
                }
            }
        }else{
            if(date1.compareTo(finHeureP) >= 0){
                return 0;
            }else{
                if(date2.compareTo(finHeureP) >= 0){
                    duree = Duration.between(date1,finHeureP);
                    return duree.toSeconds();
                }else{
                    duree = Duration.between(date1,date2);
                    return duree.toSeconds();
                }
            }

        }
    }

    /**
     * methode "calculConso" qui permet de calculer le prix equivalent à la consommation d'un objet  entre date1 et date2.
     * loqrsqu'on un seul créneau d'heure pleine
     * @return le prix d'un objet entre les deux dates passées en parametre.
     */
    public float calculPrix(LocalDateTime date1,LocalDateTime date2,float consoH,float prixHP,float prixHC,LocalDateTime debHeureP,LocalDateTime finHeureP){
        Duration duree = Duration.between(date1,date2);
        long dureeAllumage = calculAllumageHP(date1,date2,debHeureP,finHeureP);
        float prix = (float) (prixHP * dureeAllumage * consoH) / (float) 3600;
        prix = prix + (float) (prixHC * consoH * (duree.toSeconds() - dureeAllumage)) / (float) 3600;
        return prix;
    }

    /**
     * methode "calculConso" qui permet de calculer le prix equivalent à la consommation d'un objet  entre date1 et date2.
     * lorsqu'on a deux créneaux d'heure pleine
     * @return la consommation d'un objet entre les deux dates passées en parametre.
     */
    public float calculPrix(LocalDateTime date1,LocalDateTime date2,float consoH,float prixHP,float prixHC,LocalDateTime debHeureP1,LocalDateTime finHeureP1,LocalDateTime debHeureP2,LocalDateTime finHeureP2){
        Duration duree = Duration.between(date1,date2);
        long dureeAllumage1 = calculAllumageHP(date1,date2,debHeureP1,finHeureP1);
        long dureeAllumage2 = calculAllumageHP(date1,date2,debHeureP2,finHeureP2);
        float prix = (float) (prixHP * dureeAllumage1 * consoH) / (float) 3600;
        prix = prix + (float) (prixHP * dureeAllumage2 * consoH) / (float) 3600;
        prix = prix + (float) (prixHC * consoH * (duree.toSeconds() - dureeAllumage1 - dureeAllumage2)) / (float) 3600;
        return prix;
    }

    /**
     * methode 'convertirTimeToDate' elle permet de convertir Time en LocalDateTime
     * HH:mm:ss -----> yyyy:MM:ddTHH:mm:ss
     * @param time
     * @param date
     * @return
     */
    public LocalDateTime convertirTimeToDate(String time,LocalDateTime date){
        String [] words = date.toString().split("T");
        return LocalDateTime.parse(words[0]+"T"+time);
    }


    /**
     *
     * @param date1
     * @param date2
     * @param prixHP
     * @param prixHC
     * @param heureP
     * @return
     */
    public float decisionCalculPrix(LocalDateTime date1,LocalDateTime date2,float consoH,float prixHP,float prixHC,String [] heureP){
        LocalDateTime [] heuresP = new LocalDateTime[heureP.length];
        for(int i = 0; i<heureP.length; i++){
            heuresP[i] = convertirTimeToDate(heureP[i],date1);
        }
        if(heureP.length == 2){
            return calculPrix(date1,date2,consoH,prixHP,prixHC,heuresP[0],heuresP[1]);
        }else{
            return calculPrix(date1,date2,consoH,prixHP,prixHC,heuresP[0],heuresP[1],heuresP[2],heuresP[3]);
        }
    }




    /**
     * methode "ResultSetEstVide()" qui permet de traiter le cas où
     * l'historique de la période demandée est vide.
     * @param consoHoraire represente la consommation horaire de l'objet.
     */
    public void ResultSetEstVide(int idObjet, float consoHoraire,float prixHP,float prixHC,String [] heureP,int indice) throws SQLException{
        BDDSingleton base = BDDSingleton.getInstance();
        String requete = "select etat_objet from HistoriqueObjet ob1 where ob1.num_objet = " +idObjet+
                " and ob1.type_operation = \"changeEtat\"  and mail_utilisateur = "+base.getMail_utilisateur() +
                " and ob1.num_ligne = (SELECT max(ob2.num_ligne) from HistoriqueObjet ob2" +
                " where ob2.date_action < \""+ dateDeb.toString()  +"\" and ob2.type_operation = \"changeEtat\" " +
                "and ob2.num_objet = ob1.num_objet and ob2.mail_utilisateur="+base.getMail_utilisateur()+" );";
        ResultSet result = base.requeteSelectForce(requete);
        LocalDateTime dateDebTmp = dateDeb;
        if(result.next()){
            if(result.getInt("etat_objet") == 1){
                for(int i = 0; i < resultat.size(); i++){
                    resultat.get(i).setConsoObjetJour((float) (24 * consoHoraire), indice);
                    resultat.get(i).setPrixObjetJour(decisionCalculPrix(dateDebTmp,dateDebTmp.plusDays(1),consoHoraire,prixHP,prixHC,heureP),indice);
                    dateDebTmp = dateDebTmp.plusDays(1);
                }
            }
        }
    }

    /**
     * methode "remplirListeResultat" qui permet de remplir la liste resultat pour un seul objet.
     * @param r1 ResultSet, l'historique de l'objet entre dateDeb et dateFin
     * @param consoHoraire Consommation horaire de l'objet
     * @throws SQLException
     */
    public void remplirListeResultat(int idObjet,ResultSet r1,float consoHoraire,float prixHP,float prixHC,String [] heureP,int indice) throws SQLException {

        LocalDateTime date = dateDeb;
        LocalDateTime dateDebutAllumage = dateDeb;
        LocalDateTime dateFinJournee = dateDeb;
        LocalDateTime dateAction = dateDeb;
        LocalDateTime dateAllumage24h;


        boolean bool = r1.next();

        boolean resterAllumer = false;
        if (bool) {
            //System.out.println("Le bool n'est pas nulle");
            dateAction = convertirDateTime(r1.getString("date_action"));
        }
        boolean etatObjet = false;
        int indiceJournee = 0;
        if (!bool) {

            //System.out.println("Le bool est nulle");
            ResultSetEstVide(idObjet,consoHoraire,prixHP,prixHC,heureP,indice);
        } else {
            for (int i = 0; i < resultat.size(); i++) {
                while ((bool) && (dateAction.compareTo(date) >= 0) && (date.plusDays(1).compareTo(dateAction) >= 0)) {
                    if (r1.getInt("etat_objet") == 1) {
                        indiceJournee = i;
                        dateFinJournee = date.plusDays(1);
                        dateDebutAllumage = dateAction;
                        etatObjet = true;
                        resterAllumer = true;
                    } else {
                        resterAllumer = false;
                        if (etatObjet) {
                            if (indiceJournee == i) {
                                resultat.get(i).setConsoObjetJour(calculConso(dateDebutAllumage, dateAction,consoHoraire) , indice);
                                resultat.get(i).setPrixObjetJour(decisionCalculPrix(dateDebutAllumage,dateAction,consoHoraire,prixHP,prixHC,heureP),indice);
                            } else {
                                int journeetmp = indiceJournee + 1;
                                resultat.get(indiceJournee).setConsoObjetJour(calculConso(dateDebutAllumage, dateFinJournee,consoHoraire), indice);
                                resultat.get(indiceJournee).setPrixObjetJour(decisionCalculPrix(dateDebutAllumage,dateFinJournee,consoHoraire,prixHP,prixHC,heureP),indice);
                                dateAllumage24h = dateFinJournee;
                                while (journeetmp < i) {
                                    resultat.get(journeetmp).setConsoObjetJour((float) (24 * consoHoraire), indice);
                                    resultat.get(journeetmp).setPrixObjetJour(decisionCalculPrix(dateAllumage24h,dateAllumage24h.plusDays(1),consoHoraire,prixHP,prixHC,heureP),indice);
                                    dateAllumage24h = dateAllumage24h.plusDays(1);
                                    journeetmp++;
                                }
                                resultat.get(i).setConsoObjetJour(calculConso(date, dateAction,consoHoraire) , indice);
                                resultat.get(i).setPrixObjetJour(decisionCalculPrix(date,dateAction,consoHoraire,prixHP,prixHC,heureP),indice);
                            }
                        } else {
                            int j = 0;
                            dateAllumage24h = dateDeb;
                            while (j < i) {
                                resultat.get(j).setConsoObjetJour(24 * consoHoraire, indice);
                                resultat.get(j).setPrixObjetJour(decisionCalculPrix(dateAllumage24h,dateAllumage24h.plusDays(1),consoHoraire,prixHP,prixHC,heureP),indice);
                                dateAllumage24h = dateAllumage24h.plusDays(1);
                                j++;
                            }
                            resultat.get(i).setConsoObjetJour(calculConso(dateDebutAllumage, dateAction,consoHoraire), indice);
                            resultat.get(i).setPrixObjetJour(decisionCalculPrix(dateDebutAllumage,dateAction,consoHoraire,prixHP,prixHC,heureP),indice);
                         }
                    }
                    bool = r1.next();
                    if (bool) {
                        dateAction = convertirDateTime(r1.getString("date_action"));
                    }
                }
                // Le cas où la derniere ligne est allumé donc on calcul
                // la durée d'allumege de l'objet de 'dateAction' jusqu'à 'dateFin'
                if((resterAllumer) && (!bool)){
                    resultat.get(i).setConsoObjetJour(calculConso(dateAction, date.plusDays(1),consoHoraire), indice);
                    resultat.get(i).setPrixObjetJour(decisionCalculPrix(dateAction,date.plusDays(1),consoHoraire,prixHP,prixHC,heureP),indice);
                    dateAction = date.plusDays(1);
                }
                date = date.plusDays(1);
            }
        }

    }

    /**
     * methode 'consommationObjet' qui permet de calculer la consommation d'un seul objet
     * @param idObjet
     * @param decision
     * @throws SQLException
     */
    public void consommationObjet(int idObjet,boolean decision,int indice) throws SQLException {
        if(decision) {
            int[] nbObjet = new int[1];
            nbObjet[0] = idObjet;
            long nb = nombreJours();
            while (nb > 0) {
                resultat.add(new Resultat(nbObjet, 1));
                nb--;
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String deb = dateDeb.format(formatter);
        String fin = dateFin.format(formatter);
        BDDSingleton base = BDDSingleton.getInstance();
        ResultSet resultset = base.requeteSelection
                ("SELECT etat_objet,date_action FROM HistoriqueObjet WHERE  num_objet = \""+String.valueOf(idObjet)+"\"" +
                        " AND date_action BETWEEN \""+deb+ "\" AND \""+fin+"\" " +
                        "AND type_operation = \"changeEtat\" AND mail_utilisateur=");
        ResultSet resultset1 = base.requeteSelection
                ("SELECT consommation_horaire FROM ObjetConnecte WHERE num_objet = \""+String.valueOf(idObjet)+
                        "\" AND mail_utilisateur=");
        ResultSet resultset2 = base.requeteSelection
                ("SELECT * FROM Configuration_SmartHome WHERE mail_utilisateur=");
        float consoH=0;
        if(resultset1.next())
             consoH= resultset1.getFloat("consommation_horaire");

        String [] heureP=null;
        float prixHP=0;
        float prixHC=0;
        if(resultset2.next()) {
             prixHC = resultset2.getFloat("PrixHC");
             prixHP= resultset2.getFloat("PrixHP");


            if (resultset2.getString("Debut_heure_hp2") != null) {
                heureP = new String[4];
                heureP[0] = resultset2.getString("Debut_heure_hp1");
                heureP[1] = resultset2.getString("Fin_heure_hp1");
                heureP[2] = resultset2.getString("Debut_heure_hp2");
                heureP[3] = resultset2.getString("Fin_heure_hp2");
            } else {
                heureP = new String[2];
                heureP[0] = resultset2.getString("Debut_heure_hp1");
                heureP[1] = resultset2.getString("Fin_heure_hp1");
            }
        }
        remplirListeResultat(idObjet, resultset, consoH, prixHP, prixHC, heureP, indice);

    }


    /**
     * methode 'resultCount' qui permet de calculer le nombre de ligne d'un result set
     * @param resultSet
     * @return le nombre de ligne
     * @throws SQLException
     */
    private int resultSetCount(ResultSet resultSet) throws SQLException{
        int i = 0;
        while (resultSet.next()) {
            i++;
        }
        return i;
    }

    /**
     * methode 'consommationPlusieursObjets' qui permet de calculer la consommation de plusieurs objets
     * @param idObjets tableau d'identifiant
     * @throws SQLException
     */
    public void consommationPlusieursObjets(int [] idObjets) throws SQLException {
        long nb = nombreJours();
        while (nb > 0) {
            resultat.add(new Resultat(idObjets, idObjets.length));
            nb--;
        }
        for(int i=0; i<idObjets.length;i++){
            consommationObjet(idObjets[i],false,i);
        }
    }

    /**
     * methode 'consommationPiece' qui permet de calculer la consommation d'une piece
     * @param idPiece represente le nom de la piece
     * @throws SQLException
     */
    public void consommationPiece (String idPiece) throws SQLException {
        BDDSingleton base = BDDSingleton.getInstance();
        ResultSet result = base.requeteSelection
                ("SELECT num_objet FROM ObjetConnecte " +
                        "WHERE  nom_chambre = \""+idPiece+"\" AND mail_utilisateur=");
        ResultSet result1 = base.requeteSelection
                ("SELECT num_objet FROM ObjetConnecte " +
                        "WHERE  nom_chambre = \""+idPiece+"\" AND mail_utilisateur=");
        int [] id0bjets = new int[resultSetCount(result1)];


        int i = 0 ;
        while (result.next()){
            id0bjets[i] = result.getInt("num_objet");
            i++;
        }

        consommationPlusieursObjets(id0bjets);
    }


    /**
     * methode 'consommationMaison ()' qui permet de caluler la consommation de la maison
     * @throws SQLException
     */
    public void consommationMaison () throws SQLException {
        BDDSingleton base = BDDSingleton.getInstance();
        ResultSet result = base.requeteSelection
                ("SELECT num_objet FROM ObjetConnecte WHERE  mail_utilisateur =");
        ResultSet result1 = base.requeteSelection
                ("SELECT num_objet FROM ObjetConnecte WHERE  mail_utilisateur =");
        int [] id0bjets = new int[resultSetCount(result1)];

        int i = 0 ;
        while (result.next()){
            id0bjets[i] = result.getInt("num_objet");
            i++;
        }

        consommationPlusieursObjets(id0bjets);
    }

    /**
     * methode 'getResultat()'
     * @return l'attribut 'resultat'
     */
    public ArrayList<Resultat> getResultat(){
        return this.resultat;
    }

    /**
     * methode 'getDateDeb()'
     * @return l'attribut 'dateDeb'
     */
    public LocalDateTime getDateDeb(){
        return this.dateDeb;
    }

    /**
     * methode 'getDateFin()'
     * @return l'attribut 'dateFin'
     */
    public LocalDateTime getDateFin(){
        return this.dateFin;
    }

    /**
     * methode 'setDateDeb'
     * @param dateDeb
     */
    public void setDateDeb(LocalDateTime dateDeb) {
        this.dateDeb = dateDeb;
    }

    /**
     * methode 'setDateFin'
     * @param dateFin
     */
    public void setDateFin (LocalDateTime dateFin){
        this.dateFin = dateFin;
    }

    /**
     * methode 'reinitialiserResultat()' qui permet de réinitialiser un objet Calcul
     */
    public void reinitialiserResultat(){
        Resultat.reinitialiserAttributStatic();
        this.resultat.clear();
    }
}