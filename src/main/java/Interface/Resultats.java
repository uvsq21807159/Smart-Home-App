package Interface;

import java.time.LocalDate;

public class Resultats {

    private String nomObjet;
    private String Jour;
    private float PrixJour;
    private float consoJour;

    public Resultats (String nomObjet,String Jour,float PrixJour,float consoJour)
    {
        this.setNomObjet(nomObjet);
        this.setJour(Jour);
        this.setPrixJour(PrixJour);
        this.setConsoJour(consoJour);
    }


    public String getNomObjet() {
        return nomObjet;
    }

    public void setNomObjet(String nomObjet) {
        this.nomObjet = nomObjet;
    }

    public String getJour() {
        return Jour;
    }

    public void setJour(String jour) {
        Jour = jour;
    }

    public float getPrixJour() {
        return PrixJour;
    }

    public void setPrixJour(float prixJour) {
        PrixJour = prixJour;
    }

    public float getConsoJour() {
        return consoJour;
    }

    public void setConsoJour(float consoJour) {
        this.consoJour = consoJour;
    }

}
