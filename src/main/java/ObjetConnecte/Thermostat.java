package ObjetConnecte;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Thermostat extends ObjetConnecte {

    private float temperature ;
    public Thermostat(String nom, String type, String piece,float consoHoraire ,String
            droitAccess, float temperature) throws SQLException {
        super(nom,type,piece, consoHoraire,droitAccess);
        this.temperature = temperature;
        this.envoyerDonnee();


    }

    public Thermostat(Thermostat ther) throws SQLException {
        super(ther.nom,ther.type,ther.piece, ther.consoHoraire,ther.droitAcces);
        temperature = ther.temperature;
        this.envoyerDonnee();
    }
    public Thermostat(ResultSet rs, ResultSet rs2)throws SQLException {
        super(rs);
        if(rs2.getRow()>0){
            this.temperature =(float) rs2.getInt("temperature");
        }
    }

    public float getTemperature()
    {
        return temperature;

    }


    public void setTemperature(float temperature)
    {
        this.temperature = temperature;
        String sql = "UPDATE Thermostat " +
                "SET temperature = " + temperature +
                " where num_objet = "+ idObjet +" and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changeTemperature");
        r1.envoyerRequete(req);

        float temp_ideal = 19;
        float dif = 0;

        if(this.temperature != temp_ideal) {

            ArrayList<ObjetConnecte> list = r1.fournirReference("Chauffage");
            for (int i = 0; i < list.size(); i++) {
                Chauffage c = (Chauffage) list.get(i);
                if (this.temperature < temp_ideal) {
                    dif = temp_ideal - this.temperature;
                    c.setTemperature(temp_ideal + dif);
                }
                else{
                    dif = this.temperature - temp_ideal;
                    c.setTemperature(temp_ideal - dif);
                }
            }
        }
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
                "\"" + droitAcces + "\",\"" + convertirDate(LocalDateTime.now()) + "\",\"" + typeOp + "\",\"" + temperature + "\");";
        return s1;
    }

    @Override
    public String construireRequeteFille() {
        String s = "insert into Thermostat values(" + idObjet + ", ,"+ temperature + ");";
        return s;
    }

    @Override
    public String toString() {
        return "Thermostat{" +
                "temperature=" + temperature +
                "} " + super.toString();
    }

    @Override
    public long recupDuree() {
        return 0;
    }
}
