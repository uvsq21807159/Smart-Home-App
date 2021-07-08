package ObjetConnecte;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class MachineALaver extends ObjetConnecte{
    private int dureeLavage;
    private String typeLavage;

    public  MachineALaver(String nom, String type, String piece,float consoHoraire ,String
            droitAccess, int dureeLavage, String typeLavage) throws SQLException {
        super(nom,type,piece, consoHoraire,droitAccess);
        this.dureeLavage = dureeLavage;
        this.typeLavage = typeLavage;
        this.envoyerDonnee();
    }

    public MachineALaver(MachineALaver machine) throws SQLException {
        super(machine.nom,machine.type,machine.piece, machine.consoHoraire,machine.droitAcces);
        dureeLavage = machine.dureeLavage;
        typeLavage = machine.typeLavage;
        this.envoyerDonnee();
    }
    public MachineALaver(ResultSet rs, ResultSet rs2) throws SQLException {
        super(rs);
        if(rs2.getRow()>0){
            this.dureeLavage = rs2.getInt("duree_lavage");
            this.typeLavage = rs2.getString("type_lavage");
        }

    }


    public int getDureeLavage() {
        return dureeLavage;
    }

    public String getTypeLavage() {
        return typeLavage;
    }

    public void setDureeLavage(int dureeLavage) {
        this.dureeLavage = dureeLavage;
        String sql = "UPDATE Machine_a_laver " +
                "SET duree_lavage = " + dureeLavage +
                " where num_objet = " + idObjet + " and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changeDureeLavage");
        r1.envoyerRequete(req);

    }

    public void setTypeLavage(String typeLavage) {
        this.typeLavage = typeLavage;
        String sql = "UPDATE Machine_a_laver " +
                "SET type_lavage = \"" + typeLavage + "\"" +
                " where num_objet = " + idObjet +" and mail_utilisateur =";
        Routeur r1 = Routeur.getInstance();
        r1.envoyerRequete(sql);
        String req = constuireRequeteHistorique("changeTypeLavage");
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
                "\"" + droitAcces + "\",\"" + convertirDate(LocalDateTime.now()) + "\",\"" + typeOp + "\",\""+ dureeLavage +" "+typeLavage+ "\");";

        return s1;
    }

    @Override
    public String construireRequeteFille() {
        String s = "insert into Machine_a_laver values(" + idObjet +", ," + dureeLavage  +","+"\"" + typeLavage + "\");";
        return s;
    }

    @Override
    public String toString() {
        return "MachineALaver{" +
                "dureeLavage=" + dureeLavage +
                ", typeLavage='" + typeLavage + '\'' +
                "} " + super.toString();
    }

    @Override
    public long recupDuree() {
        return 0;
    }
}
