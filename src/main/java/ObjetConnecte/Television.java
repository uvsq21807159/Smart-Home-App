package ObjetConnecte;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Television extends ObjetConnecte {

    private float seuilTV;
    private  float luminosite;

    public Television (String nom, String type, String piece,float consoHoraire ,String
            droitAccess, float seuilTV, float luminosite) throws SQLException {
        super(nom,type,piece, consoHoraire,droitAccess);
        this.luminosite = luminosite;
        this.seuilTV = seuilTV;
        this.envoyerDonnee();
    }

    public Television(Television tel) throws SQLException {
        super(tel.nom,tel.type,tel.piece, tel.consoHoraire,tel.droitAcces);
        seuilTV = tel.seuilTV ;
        luminosite = tel.luminosite;
        this.envoyerDonnee();
    }
    public Television(ResultSet rs, ResultSet rs2) throws SQLException {
        super(rs);
        if(rs2.getRow()>0){
            this.seuilTV = rs2.getFloat("seuil");
            this.luminosite = rs2.getFloat("luminosite");
        }
    }

    public float getLuminosite() {
        return luminosite;
    }


    public float getSeuilTV() {
        return seuilTV;
    }

    public void setLuminosite(float luminosite) {

        this.luminosite = luminosite;
        String sql = "UPDATE Television " +
                "SET luminosite = " + luminosite +
                " where num_objet = "+idObjet+" and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req =constuireRequeteHistorique( "changeLuminosite");
        r1.envoyerRequete(req);
    }


    public void setSeuilTV(float seuilTV) {

        this.seuilTV = seuilTV;

        String sql = "UPDATE Television " +
                "SET seuil = " + seuilTV +
                " where num_objet = "+idObjet+" and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req =constuireRequeteHistorique( "changeSeuil");
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
        String s1 = "insert into HistoriqueObjet values( ," + idObjet + ", ,\""+ nom +"\","+ "\"" + type + "\"," + etat + "," + consoHoraire + "," +
                "\"" + droitAcces + "\",\"" + convertirDate(LocalDateTime.now()) + "\",\"" + typeOp + "\",\"" +seuilTV +" "+ luminosite +"\");";
        return s1;
    }

    @Override
    public String construireRequeteFille() {
        String s = "insert into Television values(" + idObjet + ", ," + seuilTV + "," + luminosite + ");";
        return s;
    }

    @Override
    public String toString() {
        return "Television{" +
                "seuilTV=" + seuilTV +
                ", luminosite=" + luminosite +
                "} " + super.toString();
    }

    @Override
    public long recupDuree() {
        float duree = seuilTV/this.consoHoraire;
        long res = (long) (60 * duree); //test pour 1 minutes
        return res;
    }
}
