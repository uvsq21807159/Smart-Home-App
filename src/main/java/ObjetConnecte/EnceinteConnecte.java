package ObjetConnecte;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EnceinteConnecte extends ObjetConnecte{

    private  int volumeEnregistrement;
    private float Seuil;
    private int dureeEnregistrement;


    public EnceinteConnecte (String nom, String type, String piece,float consoHoraire ,String
            droitAccess, int volume,float Seuil,int dureeEnregistrement) throws SQLException {
        super(nom,type,piece, consoHoraire,droitAccess);
        this.volumeEnregistrement = volume;
        this.dureeEnregistrement = dureeEnregistrement;
        this.Seuil = Seuil;
        this.envoyerDonnee();
    }

    public EnceinteConnecte(EnceinteConnecte enceinte) throws SQLException {
        super(enceinte.nom,enceinte.type,enceinte.piece, enceinte.consoHoraire,enceinte.droitAcces);
        volumeEnregistrement = enceinte.volumeEnregistrement;
        dureeEnregistrement = enceinte.dureeEnregistrement;
        Seuil = enceinte.Seuil;
        this.envoyerDonnee();
    }

    public EnceinteConnecte(ResultSet rs, ResultSet rs2)throws SQLException {
        super(rs);
        if (rs2.getRow()>0){
            this.volumeEnregistrement = rs2.getInt("volume_enregistrement");
            this.dureeEnregistrement =rs2.getInt("duree_enregistrement");
            this.Seuil =rs2.getFloat("seuil");
        }

    }


    public float getSeuil() {
        return Seuil;
    }

    public int getDureeEnregistrement() {
        return dureeEnregistrement;
    }

    public int getVolumeEnregistrement() {
        return volumeEnregistrement;
    }

    public void setDureeEnregistrement(int dureeEnregistrement) {

        this.dureeEnregistrement = dureeEnregistrement;
        String sql = "UPDATE Enceinte" +
                " SET duree_enregistrement = " +dureeEnregistrement+
                " where num_objet = " +idObjet+ " and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changeDuree");
        r1.envoyerRequete(req);

    }

    public void setSeuil(float Seuil) {

        this.Seuil = Seuil;
        String sql = "UPDATE Enceinte" +
                " SET seuil = " +Seuil+
                " where num_objet = " +idObjet+ " and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changeSeuil");
        r1.envoyerRequete(req);

    }

    public void setVolumeEnregistrement(int volumeEnregistrement) {
        this.volumeEnregistrement = volumeEnregistrement;
        String sql = "UPDATE Enceinte" +
                " SET volume_enregistrement = " +volumeEnregistrement+
                " where num_objet = "+idObjet+ " and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changeVolume");
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
        String s1 = "insert into HistoriqueObjet values( ," +idObjet+ ", ,\""+ nom+ "\","+"\""+type+"\","+ etat +","+consoHoraire+","+
                "\""+droitAcces+ "\",\"" + convertirDate(LocalDateTime.now()) + "\",\""+typeOp+"\",\""+ Seuil + " " + dureeEnregistrement + " "+volumeEnregistrement +"\");";

        return s1;
    }

    @Override
    public String construireRequeteFille() {
        String s = "insert into Enceinte values("+idObjet+", ,"+Seuil+"," +dureeEnregistrement+ "," +volumeEnregistrement+ ");";
        return s;
    }

    @Override
    public String toString() {
        return "EnceinteConnecte{" +
                "volumeEnregistrement=" + volumeEnregistrement +
                ", Seuil=" + Seuil +
                ", dureeEnregistrement=" + dureeEnregistrement +
                "} " + super.toString();
    }

    @Override
    public long recupDuree() {
        float duree = Seuil/this.consoHoraire;

        long res = (long) (60 * duree); //test pour 1 minutes
        return res;
    }
}
