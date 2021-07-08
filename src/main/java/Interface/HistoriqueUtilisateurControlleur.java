package Interface;

import GestionnaireDonnees.BDDSingleton;
import GestionnaireDonnees.Donnee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HistoriqueUtilisateurControlleur {
    @FXML public TableView TableHistorique;
    @FXML public TableColumn<HistoriqueUtilisateur,String> TypeOperation;
    @FXML public TableColumn<HistoriqueUtilisateur,String> Description;
    @FXML public TableColumn<HistoriqueUtilisateur,String> DateHeure;
    @FXML public DatePicker DateDeb;
    @FXML public DatePicker DateFin;





    private ArrayList<String> liste_a_generer2 = new ArrayList<>();

    @FXML
    public void GenererHistorique1() throws FileNotFoundException, SQLException {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog((new Stage()));

        if (file != null) {
            //System.out.println(file.getPath());
            if (liste_a_generer2 != null)
            {
                //System.out.println("entree dans .");

                BDDSingleton bdd = BDDSingleton.getInstance();
                ////System.out.println(liste_a_generer.get(0).toString());
                bdd.genererFichier(file,liste_a_generer2);

            }
            Donnee data = new Donnee("Generation de fichiers","L'utilisateur a genere le fichier d'historique");
        }
        else
            System.out.println( "  no enter ..");


    }



    public void consulterClicked(javafx.event.ActionEvent actionEvent) throws SQLException {

        if (liste_a_generer2 != null)
        {
            liste_a_generer2.clear();
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

            BDDSingleton bdd = BDDSingleton.getInstance();
            String requete = "select * from HistoriqueUtilisateur where heureDeclanchement between " + datedeb + " and " + datefin + "" +
                    " and mail_utilisateur =  ";

            ResultSet rs = bdd.decisionRequete(requete);

            TypeOperation.setCellValueFactory(new PropertyValueFactory<>("TypeOperation"));
            DateHeure.setCellValueFactory(new PropertyValueFactory<>("DateHeure"));
            Description.setCellValueFactory(new PropertyValueFactory<>("Description"));


            TableHistorique.setItems(getHistorique(rs));
        }
        else {
            String s1 = "Information";
            String s2 = "";
            String s3 = "Veuillez saisir une 'DATE DEBUT' et une 'DATE FIN' !";

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(s1);
            alert.setHeaderText(s3);
            alert.setContentText(s2);

            alert.showAndWait();
            alert.close();
        }

    }

    private ObservableList getHistorique(ResultSet rs) throws SQLException {

        ObservableList<HistoriqueUtilisateur> historique = FXCollections.observableArrayList();


        String TypeOperation ;
        String DateHeure ;
        String Description ;

        int num ;
        int i = 0 ;
        while(rs.next()){

            TypeOperation = rs.getString("type_operation");
            DateHeure = rs.getString("heureDeclanchement");
            Description=rs.getString("descriptionOperation");

            historique.add(new HistoriqueUtilisateur(TypeOperation,DateHeure,Description));
            String ligne = historique.get(i).toString();
            liste_a_generer2.add(ligne);
            i++;

        }
        Donnee d = new Donnee("Consultation d'historique","L'utilisateur a consulte son historique");



        return  historique;
    }






}
