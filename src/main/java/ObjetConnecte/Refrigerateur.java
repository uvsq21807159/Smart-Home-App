package ObjetConnecte;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Refrigerateur extends ObjetConnecte {

    private float NiveauFraicheur ;

    public Refrigerateur (String nom, String type, String piece,float consoHoraire ,String
            droitAccess, float NiveauFraicheur) throws SQLException {
        super(nom,type,piece, consoHoraire,droitAccess);
        this.NiveauFraicheur = NiveauFraicheur;
        this.envoyerDonnee();
    }

    public Refrigerateur (Refrigerateur ref) throws SQLException {
        super(ref.nom,ref.type,ref.piece, ref.consoHoraire,ref.droitAcces);
        NiveauFraicheur = ref.NiveauFraicheur;
        this.envoyerDonnee();
    }
    public Refrigerateur(ResultSet rs, ResultSet rs2) throws SQLException {
        super(rs);
        if(rs2.getRow()>0){
            this.NiveauFraicheur = rs2.getInt("niveau_fraicheur");

        }
    }

    public float getNiveauFraicheur() {
        return NiveauFraicheur;
    }

    public void setNiveauFraicheur(float niveauFraicheur) {
        this.NiveauFraicheur = niveauFraicheur;
        String sql = "UPDATE Refrigerateur " +
                "SET niveau_fraicheur = " + niveauFraicheur +
                " where num_objet = " + idObjet + " and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changeNiveauFraicheur");
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
                "\"" + droitAcces + "\",\"" + convertirDate(LocalDateTime.now()) + "\",\"" + typeOp + "\",\"" + NiveauFraicheur + "\");";
        return s1;

    }

    @Override
    public String construireRequeteFille() {
        String s = "insert into Refrigerateur values(" + idObjet + ", ," + NiveauFraicheur + ");";
        return s;

    }

    @Override
    public String toString() {
        return "Refrigerateur{" +
                "NiveauFraicheur=" + NiveauFraicheur +
                "} " + super.toString();
    }

    @Override
    public long recupDuree() {
        return 0;
    }
}
