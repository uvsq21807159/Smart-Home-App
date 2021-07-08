package CalculOptimisation;

import GestionnaireDonnees.*;
import ObjetConnecte.*;

/**
 * Classe non utilisable
 */

public class Reglage {

    private int idObjet;
    private String ordre;

    public Reglage(int idObjet, String ordre){
        this.idObjet = idObjet;
        this.ordre = ordre;
    }

    public int getIdObjet(){
        return this.idObjet;
    }

    public String getOrdre(){
        return this.ordre;
    }

    public void setIdObjet(int IdObjet){
        this.idObjet = IdObjet;
    }

    public void setOrdre(String Ordre){
        this.ordre = ordre;
    }
}