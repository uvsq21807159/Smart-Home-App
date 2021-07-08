package Interface;

import GestionnaireDonnees.BDDSingleton;
import GestionnaireDonnees.Donnee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import jdk.jfr.Frequency;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class HistoriqueObjetControlleur implements Initializable {

    // static int num_objet;
    @FXML  public DatePicker DateDeb;
    @FXML  public DatePicker DateFin;
    @FXML  public Button Consulter;
    @FXML  public TableView TableHistorique;
    @FXML TableColumn<HistoriqueObjet,String> etat_objet ;
    @FXML TableColumn<HistoriqueObjet, String> nomObjet ;
    @FXML TableColumn<HistoriqueObjet,String> droitAcces ;
    @FXML TableColumn<HistoriqueObjet,String> dateAction ;
    @FXML TableColumn<HistoriqueObjet,String> typeOperation ;
    @FXML TableColumn<HistoriqueObjet, String> special ;
    @FXML TextField IDObjet;

    @FXML
    AnchorPane pane;

    private ArrayList<String> liste_a_generer = new ArrayList<>();

    @FXML
    public void GenererHistorique() throws FileNotFoundException, SQLException {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog((new Stage()));
        //File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            //System.out.println(file.getPath());
            if (liste_a_generer != null)
            {
                //System.out.println("entree dans .");

                BDDSingleton bdd = BDDSingleton.getInstance();
                ////System.out.println(liste_a_generer.get(0).toString());
                bdd.genererFichier(file,liste_a_generer);
            }

            Donnee data = new Donnee("Generation de fichiers","L'utilisateur a genere le fichier d'historique d'un objet");
        }
        else
            System.out.println("  no enter ..");


    }


    public void consulterClicked(javafx.event.ActionEvent actionEvent) throws SQLException {

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (liste_a_generer != null)
        {
            liste_a_generer.clear();
        }

        if (DateDeb.getValue()!=null && DateFin.getValue()!=null) {

            if (DateDeb.getValue().isAfter(DateFin.getValue())) {

                String s1 = "Information";
                String s2 = "";
                String s3 = "Vous avez saisi une 'DATE FIN' qui est avant la 'DATE DEBUT' !";

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(s1);
                alert.setHeaderText(s3);
                alert.setContentText(s2);

                alert.showAndWait();
                alert.close();

                return;

            }


            String datedeb = DateDeb.getValue().toString();
            String datefin = DateFin.getValue().toString();

            //System.out.println(datedeb);

            datedeb = "\"" + datedeb + "\"";
            datefin = "\"" + datefin + "\"";


            String numObjet1 = IDObjet.getText();


            BDDSingleton bdd = BDDSingleton.getInstance();
            String requete = "select * from HistoriqueObjet where date_action between " + datedeb + " and " + datefin + "" +
                    " and num_objet=" + numObjet1 +
                    " and mail_utilisateur =  ";
            ////System.out.println(requete);

            ResultSet rs = bdd.decisionRequete(requete);

            etat_objet.setCellValueFactory(new PropertyValueFactory<>("etat_objet"));
            nomObjet.setCellValueFactory(new PropertyValueFactory<>("nom_objet"));
            droitAcces.setCellValueFactory(new PropertyValueFactory<>("droit_acces"));
            typeOperation.setCellValueFactory(new PropertyValueFactory<>("date_action"));
            dateAction.setCellValueFactory(new PropertyValueFactory<>("type_operation"));
            special.setCellValueFactory(new PropertyValueFactory<>("special"));


            TableHistorique.setItems(getHistorique(rs));


        }
        else {
            String s1 = "Information";
            String s2 = "";
            String s3 = "Veuillez saisir une 'DATE DEBUT' et une 'DATE FIN'  !";

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(s1);
            alert.setHeaderText(s3);
            alert.setContentText(s2);

            alert.showAndWait();
            alert.close();
        }
    }


    public ObservableList<HistoriqueObjet> getHistorique(ResultSet rs) throws SQLException {
        ObservableList<HistoriqueObjet> historique = FXCollections.observableArrayList();


        String etatObj ;
        String nomObjet = "";
        String droitAcces ;
        String dateAction ;
        String typeOperation ;
        String special ;
        int num ;
        int i = 0 ;
        while(rs.next()){
            etatObj = String.valueOf(rs.getInt("etat_objet"));
            nomObjet = rs.getString("nom");
            droitAcces = rs.getString("droit_acces");
            dateAction = rs.getString("date_action");
            typeOperation=rs.getString("type_operation");
            special = rs.getString("special");

            if (etatObj.equals("1")){
                etatObj = "Allume";
            }
            else etatObj = "Etteint";

            historique.add(new HistoriqueObjet(etatObj,nomObjet,droitAcces,typeOperation,dateAction,special));
            String ligne = historique.get(i).toString();
            liste_a_generer.add(ligne);
            i++;
        }
        Donnee d = new Donnee("Consultation d'historique","L'utilisateur a consulte l'historique de "+ nomObjet );


        return  historique;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}