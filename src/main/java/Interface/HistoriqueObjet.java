package Interface;

public class HistoriqueObjet {

    private String etat_objet;
    private String nom_objet;
    private String droit_acces;
    private String date_action;
    private String type_operation;
    private String special;

    public HistoriqueObjet(String etat_objet,String nom_objet,String droit_acces,
                            String date_action, String type_operation, String special){
        this.etat_objet = etat_objet;
        this.nom_objet = nom_objet;
        this.droit_acces= droit_acces;
        this.date_action = date_action;
        this.type_operation= type_operation;
        this.special = special;
    }

    public String getEtat_objet() {
        return etat_objet;
    }

    public void setEtat_objet(String etat_objet) {
        this.etat_objet = etat_objet;
    }

    public String getNom_objet() {
        return nom_objet;
    }

    public void setNom_objet(String nom_objet) {
        this.nom_objet = nom_objet;
    }

    public String getDroit_acces() {
        return droit_acces;
    }

    public void setDroit_acces(String droit_acces) {
        this.droit_acces = droit_acces;
    }

    public String getDate_action() {
        return date_action;
    }

    public void setDate_action(String date_action) {
        this.date_action = date_action;
    }

    public String getType_operation() {
        return type_operation;
    }

    public void setType_operation(String type_operation) {
        this.type_operation = type_operation;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    @Override
    public String toString() {
        return "HistoriqueObjet{" +
                "etat_objet='" + etat_objet + '\'' +
                ", nom_objet='" + nom_objet + '\'' +
                ", droit_acces='" + droit_acces + '\'' +
                ", date_action='" + date_action + '\'' +
                ", type_operation='" + type_operation + '\'' +
                ", special='" + special + '\'' +
                '}';
    }
}
