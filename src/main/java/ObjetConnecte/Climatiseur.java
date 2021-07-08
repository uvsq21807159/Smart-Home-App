package ObjetConnecte;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Climatiseur  extends ObjetConnecte{

    private float seuilClim ;
    private float temperature;

    public Climatiseur (String nom, String type, String piece,float consoHoraire ,String
            droitAccess, float seuilClim, float temperature) throws SQLException {
        super(nom,type,piece, consoHoraire,droitAccess);
        this.temperature = temperature;
        this.seuilClim = seuilClim;
        this.envoyerDonnee();
    }

    public Climatiseur(Climatiseur clim) throws SQLException {
        super(clim.nom,clim.type,clim.piece, clim.consoHoraire,clim.droitAcces);
        seuilClim = clim.seuilClim;
        temperature = clim.temperature;
        this.envoyerDonnee();
    }

    public Climatiseur(ResultSet rs, ResultSet rs2) throws SQLException {
        super(rs);

        if(rs2.getRow()>0){
            this.seuilClim = rs2.getFloat("seuil");
            this.temperature = rs2.getInt("temperature");

        }
    }


    public float getTemperature() {
        return temperature;
    }

    public float getSeuilClim() {
        return seuilClim;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
        String sql = "UPDATE Climatiseur " +
                "SET temperature = " + temperature +
                " where num_objet = " + idObjet + " and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changeTemperature");
        r1.envoyerRequete(req);

    }

    public void setSeuilClim(float seuilClim) {

        this.seuilClim = seuilClim;
        String sql = "UPDATE Climatiseur " +
                "SET seuil = " + seuilClim +
                " where num_objet = " + idObjet + " and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changeSeuil");
        r1.envoyerRequete(req);

    }

    @Override
    public void envoyerDonnee() {
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(this.construireRequeteObjet());
        r1.envoyerRequete(this.construireRequeteFille());
        String S1 = constuireRequeteHistorique("Creation");
        r1.envoyerRequete(S1);
    }

    @Override
    public String constuireRequeteHistorique(String typeOp) {

        String s1 = "insert into HistoriqueObjet values( ," +idObjet +", ,\""+ nom +"\","+"\""+type+"\","+etat+","+consoHoraire+","+
                "\""+ droitAcces +"\",\"" + convertirDate(LocalDateTime.now()) + "\",\""+ typeOp +"\",\""+ temperature+ " " +seuilClim+"\");";

        return s1;
    }

    @Override
    public String construireRequeteFille() {
        String s = "insert into Climatiseur values(" + idObjet +", ," + seuilClim+ "," +temperature+ ");";
        return s;
    }

    @Override
    public String toString() {
        return "Climatiseur{" +
                "seuilClim=" + seuilClim +
                ", temperature=" + temperature +
                "} " + super.toString();
    }

    @Override
    public long recupDuree() {
        float duree = seuilClim/this.consoHoraire;

        long res = (long) (60 * duree); //test pour 1 minutes

        return res;
    }


}
