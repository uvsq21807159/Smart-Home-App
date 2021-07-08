package ObjetConnecte;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Lampe extends ObjetConnecte {


    private float seuilConso;
    private float luminosite;

    public Lampe(String nom, String type, String piece, float consoHoraire, String
            droitAccess, float seuilConso, float luminosite) throws SQLException {
        super(nom, type, piece, consoHoraire, droitAccess);
        this.seuilConso = seuilConso;
        this.luminosite = luminosite;
        this.envoyerDonnee();

    }

    public Lampe(Lampe lampe) throws SQLException {
        super(lampe.nom, lampe.type, lampe.piece, lampe.consoHoraire, lampe.droitAcces);
        seuilConso = lampe.seuilConso;
        luminosite = lampe.luminosite;
        this.envoyerDonnee();
    }

    public Lampe(ResultSet rs1, ResultSet rs2) throws SQLException {
        super(rs1);
        if(rs2.getRow()>0) {
            this.seuilConso = rs2.getFloat("seuil");
            this.luminosite = rs2.getFloat("luminosite");
        }

    }

    public float getSeuilConso() {


        return seuilConso;
    }

    public void setSeuilConso(float seuilConso) {

        this.seuilConso = seuilConso;
        String sql = "UPDATE Lampe " +
                "SET seuil = " + seuilConso +
                " where num_objet = " + idObjet + " and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changeSeuil");
        r1.envoyerRequete(req);
    }


    public float getLuminosite() {

        return luminosite;
    }

    public void setLuminosite(float luminosite) {

        this.luminosite = luminosite;
        String sql = "UPDATE Lampe " +
                "SET luminosite = " + luminosite +
                " where num_objet = " + idObjet + " and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changeLuminosite");
        r1.envoyerRequete(req);
    }

    @Override
    public void envoyerDonnee() {

        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(this.construireRequeteObjet());
        r1.envoyerRequete(this.construireRequeteFille());
        String S1 = constuireRequeteHistorique("Creation");
        // r1.envoyerRequete(S1);
    }

    @Override
    public String constuireRequeteHistorique(String typeOp) {
        String s1 = "insert into HistoriqueObjet values(," + idObjet + ", ,\""+nom+"\"," + "\"" + type + "\"," + etat + "," + consoHoraire + "," +
                "\"" + droitAcces + "\",\""+ convertirDate(LocalDateTime.now()) +"\",\""+typeOp+"\",\"" + luminosite + " " +seuilConso + "\");";

        return s1;
    }

    @Override
    public String construireRequeteFille() {
        String s = "insert into Lampe values("+idObjet+ ", ," +seuilConso+","+luminosite+");";
        return s;
    }

    @Override
    public String toString() {
        return "Lampe{" +
                "seuilConso=" + seuilConso +
                ", luminosite=" + luminosite +
                "} " + super.toString();
    }

    @Override
    public long recupDuree() {
        float duree = seuilConso/this.consoHoraire;

        long res = (long) (60 * duree); //test pour 1 minutes
        return res;
    }
}
