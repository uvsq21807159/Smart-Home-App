package ObjetConnecte;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CapteurPresence extends ObjetConnecte{

    private String placementUtilisateur ;

    public CapteurPresence(String nom,String type, String piece,float consoHoraire,
                           String droitAccess,String placementUtilisateur) throws SQLException {
        super(nom,type,piece,consoHoraire,droitAccess);
        this.placementUtilisateur  = placementUtilisateur ;
        this.envoyerDonnee();
    }

    public CapteurPresence( CapteurPresence capteur) throws SQLException {
        super(capteur.nom,capteur.type,capteur.piece, capteur.consoHoraire,capteur.droitAcces);
        placementUtilisateur  = capteur.placementUtilisateur;
        this.envoyerDonnee();
    }


    public CapteurPresence(ResultSet rs, ResultSet rs2)throws SQLException {
        super(rs);
        if(rs2.getRow()>0){
            this.placementUtilisateur = rs2.getString("placementUtilisateur");

        }

    }



    public String getPlacementUtilisateur()
    {
        return placementUtilisateur;
    }

    public void setPlacementUtilisateur(String placementUtilisateur) {
        this.placementUtilisateur = placementUtilisateur;

        String sql = "UPDATE CapteurPresence " +
                " SET placementUtilisateur = \""  +placementUtilisateur +"\""+
                " where num_objet = " +idObjet+ " and mail_utilisateur =";

        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changeAutre");
        r1.envoyerRequete(req);
    }

    @Override
    public void envoyerDonnee() {
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(this.construireRequeteObjet());
        r1.envoyerRequete(this.construireRequeteFille());
        String S1 = constuireRequeteHistorique("changeAutre");
        r1.envoyerRequete(S1);


    }

    @Override
    public String constuireRequeteHistorique(String typeOp) {

        String s1 = "insert into HistoriqueObjet values( ,"+idObjet+", ,"+"\""+nom+"\","+"\""+type+"\","+etat+","+consoHoraire+","+
                "\""+droitAcces+"\",\""+convertirDate(LocalDateTime.now())+"\",\""+typeOp+"\","+"\""+placementUtilisateur+"\");";

        return s1;
    }

    @Override
    public String construireRequeteFille() {

        String s = "insert into CapteurPresence values(" +idObjet + ", ,\""+ placementUtilisateur +"\");";
        return s;
    }

    @Override
    public long recupDuree() {
        return 0;
    }
}
