package CalculOptimisation;

import GestionnaireDonnees.*;
import ObjetConnecte.*;

/**
 * Classe Resultat
 * @author  Salah & Tina
 */
public class Resultat {

    private int[] idObjets;
    private int nbObjets;
    private float[] prixObjetJour;
    private float[] consoObjetJour;
    private float prixTotalJour;
    private float consoGlobaleJour;
    private static float prixTotal = 0;
    private static float consoGlobale = 0;

    /**
     * Constructeur de la classe
     * @param idObjets
     * @param nbObjets
     */
    public Resultat(int[ ] idObjets,int nbObjets){
        this.nbObjets = nbObjets;
        prixObjetJour = new float[nbObjets];
        consoObjetJour = new  float[nbObjets];
        this.idObjets = new  int [nbObjets];
        for (int i=0;i<nbObjets;i++){
            this.prixObjetJour[i] = 0;
            this.consoObjetJour[i] = 0;
            this.idObjets[i] = idObjets[i];
        }
        prixTotalJour = 0;
        consoGlobaleJour = 0;
    }

    /**
     * methode 'getConsoObjetJour'
     * @return le tableau consoObjetJour
     */
    public float[ ] getConsoObjetJour(){

        return this.consoObjetJour;
    }

    /**
     * methode 'getPrixObjetJour'
     * @return le tableau prixObjetJour.
     */
    public float[ ] getPrixObjetJour(){
        return this.prixObjetJour;
    }

    /**
     * methode 'getIdObjets'
     * @return le tableau idObjets
     */
    public int[ ] getIdObjets(){
        return this.idObjets;
    }

    /**
     * methode 'getNbObjets'
     * @return l'attribut nbObjets
     */
    public int getNbObjets(){
        return this.nbObjets;
    }

    /**
     * methode 'getPrixTotalJour'
     * @return l'attribut prixTotalJour
     */
    public float getPrixTotalJour(){
        return  this.prixTotalJour;
    }

    /**
     * methode 'getConsoGlobaleJour'
     * @return l'attribut getConsoGlobaleJour
     */
    public float getConsoGlobaleJour(){
        return this.consoGlobaleJour;
    }

    /**
     * methode 'getConsoGlobale'
     * @return l'attribut consoGlobale
     */
    public static float getConsoGlobale(){
        return Resultat.consoGlobale;
    }

    /**
     * methode 'getPrixTotal'
     * @return l'attribut prixTotal
     */
    public static float getPrixTotal(){
        return Resultat.prixTotal;
    }

    /**
     * methode 'setConsoObjetJour'
     * elle permet de modifier les attributs qui ont une relation avec la consommation.
     * @param conso
     * @param indiceID
     */
    public void setConsoObjetJour(float conso,int indiceID){
        this.consoObjetJour[indiceID] = conso;
        this.consoGlobaleJour = this.consoGlobaleJour + conso;
        Resultat.consoGlobale = Resultat.consoGlobale + conso;
    }

    /**
     * methode 'setPrixObjetJour'
     * elle permet de modifier les attributs qui ont une relation avec le prix
     * @param prix
     * @param indiceID
     */
    public void setPrixObjetJour(float prix,int indiceID){
        this.prixObjetJour[indiceID] = prix;
        this.prixTotalJour = this.prixTotalJour + prix;
        Resultat.prixTotal = Resultat.prixTotal + prix;
    }

    /**
     * methode 'reinitialiserAttributStatic'
     * elle permet de mettre les attributs statiques à 00.
     */
    public static void reinitialiserAttributStatic(){
        Resultat.prixTotal = 0;
        Resultat.consoGlobale = 0;
    }

}
