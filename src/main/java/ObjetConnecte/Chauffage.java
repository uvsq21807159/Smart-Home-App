package ObjetConnecte;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Chauffage extends ObjetConnecte {

    private float temperature;
    private final float tempMin = (float) 5.0;
    private final float tempMax = (float) 40.0;


    public Chauffage(String nom, String type, String piece, float consoHoraire, String
            droitAccess, float temperature) throws SQLException {
        super(nom, type, piece, consoHoraire, droitAccess);
        this.temperature = temperature;

        this.envoyerDonnee();
    }

    public Chauffage(Chauffage chauff) throws SQLException {
        super(chauff.nom, chauff.type, chauff.piece, chauff.consoHoraire, chauff.droitAcces);
        temperature = chauff.temperature;

        this.envoyerDonnee();

    }

    public Chauffage(ResultSet rs, ResultSet rs2) throws SQLException {
        super(rs);
        if(rs2.getRow()>0){
            this.temperature = rs2.getFloat("temperature");
        }

    }


    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        if (temperature >= this.tempMax)
        {
            this.temperature = tempMax;
        }
        else if (temperature <= tempMin)
        {
            this.temperature = tempMin;
        }
        else
            this.temperature = temperature;

        String sql = "UPDATE Chauffage " +
                    "SET temperature = " + this.temperature +
                    " where num_objet = " + idObjet + " and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changeTemperature");
        r1.envoyerRequete(req);
    }

    public float getTempMin() {
        return tempMin;
    }

    public float getTempMax() {
        return tempMax;
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

        String s1 = "insert into HistoriqueObjet values( ," + idObjet + ", ,\""+ nom +"\","+ "\"" + type + "\"," + etat + "," + consoHoraire + "," +
                "\"" + droitAcces + "\",\"" + convertirDate(LocalDateTime.now()) + "\",\"" + typeOp + "\",\"" + temperature + " " + tempMax + " " + tempMin + "\");";

        return s1;
    }

    @Override
    public String construireRequeteFille() {
        String s = "insert into Chauffage values(" + idObjet +  ", ," + temperature + "," + tempMax + "," + tempMin + ");";
        return s;
    }

    @Override
    public String toString() {
        return "Chauffage{" +
                "temperature=" + temperature +
                ", tempMin=" + tempMin +
                ", tempMax=" + tempMax +
                "} " + super.toString();
    }

    @Override
    public long recupDuree() {
        return 0;
    }
}
