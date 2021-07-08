package Interface;

import CalculOptimisation.Optimisation;
import GestionnaireDonnees.BDDSingleton;
import GestionnaireDonnees.Donnee;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class UtilisateursControlleur implements Initializable {
    @FXML
    private Label erreurLabel;
    @FXML
    private AnchorPane dataP;


    private double finalx;
    private double finaly;

    public static ArrayList<Object> donneesStockes = new ArrayList<>(); // structure qui nous permettras de stocker des le lancement de l'application les donnees de l'utilisateur

    public void SetConfigurationValues(float PrixHP, float PrixHC, float seuil, String d_h1,String d_h2,
                                       String f_h1, String f_h2) throws SQLException {
        Donnee data = new Donnee("Modification", "L'utilisateur a modifie ses configurations");
        String requete = "update Configuration_SmartHome set" +
                " PrixHC=" + PrixHC + " " +
                ",PrixHP =" + PrixHP + " " +
                ",seuil_maison = " + seuil + " " +
                ",Debut_heure_hp1=\"" + d_h1 + "\"" +
                ",Fin_heure_hp1= \"" + f_h1 + "\"" +
                ",Debut_heure_hp2=\"" + d_h2 + "\"" +
                ",Fin_heure_hp2 =\"" + f_h2 + "\"" +
                " where mail_utilisateur =";

        BDDSingleton bdd = BDDSingleton.getInstance();
        bdd.decisionRequete(requete);
        bdd.setPrixHC_PrixHP();
        Optimisation.getInstance().setSeuilConsoGlobale(seuil);
    }

    // méthode de vérification de tous les champs de l'utilisateur en cas de modification de ces derniers
    public void Verification() {
        erreurLabel.setText("");

        boolean B = true;

        if (((TextField) dataP.getChildren().get(1)).getLength() == 0 ) {
            erreurLabel.setVisible(true);
            erreurLabel.setText("ERREUR : Veuillez fournir un Prix HP valide");

            B = false;
        }

        if (((TextField) dataP.getChildren().get(2)).getLength() == 0 ) {
            erreurLabel.setVisible(true);
            erreurLabel.setText("ERREUR : Veuillez fournir un Prix HC valide");
            B = false;
        }


        if (((TextField) dataP.getChildren().get(3)).getLength() == 0 || (!((TextField) dataP.getChildren().get(3)).getText().matches("[+]?[0-9]*\\.?[0-9]+"))) {
            erreurLabel.setText("ERREUR : Veuillez fournir un seuil de consommation");
            erreurLabel.setVisible(true);
            B = false;
        }

        if (((TextField) dataP.getChildren().get(4)).getLength() == 0 || (!((TextField) dataP.getChildren().get(4)).getText().matches("(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)"))) {
            erreurLabel.setText("ERREUR : Veuillez fournir une heure début HP1 valide");
            erreurLabel.setVisible(true);
            B = false;
        }

        if (((TextField) dataP.getChildren().get(5)).getLength() == 0 || (!((TextField) dataP.getChildren().get(5)).getText().matches("(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)"))) {
            erreurLabel.setText("ERREUR : Veuillez fournir une heure de fin HP1 valide");
            erreurLabel.setVisible(true);
            B = false;
        }

        if (((TextField) dataP.getChildren().get(6)).getLength() == 0 || (!((TextField) dataP.getChildren().get(6)).getText().matches("(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)"))) {
            erreurLabel.setText("ERREUR : Veuillez fournir une heure début HP2 valide");
            erreurLabel.setVisible(true);
            B = false;

        }

        if (((TextField) dataP.getChildren().get(7)).getLength() == 0 || (!((TextField) dataP.getChildren().get(7)).getText().matches("(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)"))) {
            erreurLabel.setText("ERREUR : Veuillez fournir une heure de fin HP2 valide");
            erreurLabel.setVisible(true);
            B = false;
        }

        if (((TextField) dataP.getChildren().get(22)).getLength() == 0) {
            erreurLabel.setText("ERREUR : Veuillez fournir un nom valide");
            erreurLabel.setVisible(true);
            B = false;
        }

        if (((TextField) dataP.getChildren().get(20)).getLength() == 0) {
            erreurLabel.setText("ERREUR : Veuillez fournir un prenom valide");
            erreurLabel.setVisible(true);
            B = false;
        }

        if (B) {
            erreurLabel.setVisible(false);
            String h_d1 = ((TextField) dataP.getChildren().get(4)).getText(),
                    h_d2 = ((TextField) dataP.getChildren().get(5)).getText(), h_f1 = ((TextField) dataP.getChildren().get(6)).getText(), h_f2 = ((TextField) dataP.getChildren().get(7)).getText();
            float pHp = Float.valueOf(((TextField) dataP.getChildren().get(1)).getText()),
                    pHc = Float.valueOf(((TextField) dataP.getChildren().get(2)).getText()),
                    seuil = Float.valueOf(((TextField) dataP.getChildren().get(3)).getText());

            String nomU = ((TextField) dataP.getChildren().get(22)).getText();
            String prenomU = ((TextField) dataP.getChildren().get(20)).getText();

            try {
                SetConfigurationValues(pHp, pHc, seuil, h_d1, h_f1, h_d2, h_f2);
                Optimisation op = Optimisation.getInstance();
                op.setSeuilConsoGlobale(seuil);
                SetUserValues(nomU, prenomU);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    private void SetUserValues(String nom, String prenom) throws SQLException {
        String requete = "update Utilisateur set nom = \"" + nom + "\" , prenom =\"" + prenom + "\"" +
                " where mail_utilisateur = ";

        BDDSingleton bdd = BDDSingleton.getInstance();
        bdd.decisionRequete(requete);

    }

    public void HistoriqueUtilisateur() throws IOException {

        Stage window1 = new Stage();

        window1.setTitle("Historique Utilisateur");
        window1.setResizable(false);
        window1.initModality(Modality.APPLICATION_MODAL);
        window1.initStyle(StageStyle.UNDECORATED);
        AnchorPane root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("FXMLFiles/HistoriqueUtilisateur.fxml"));
            Label l = (Label) root.getChildren().get(7);
            l.setText("Historique de : " + ((TextField) dataP.getChildren().get(22)).getText() + " " + ((TextField) dataP.getChildren().get(20)).getText());

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        root.setOnMousePressed(event1 -> {
            finalx = event1.getSceneX();
            finaly = event1.getSceneY();
        });

        root.setOnMouseDragged(event1 -> {
            window1.setX(event1.getScreenX() - finalx);
            window1.setY(event1.getScreenY() - finaly);
        });

        ImageView exit = (ImageView) root.getChildren().get(6);
        exit.setOnMouseClicked(event1 -> window1.close());

        Scene Sc = new Scene(root);
        window1.setScene(Sc);
        window1.show();
    }

    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        int i = 0;
        erreurLabel.setVisible(false);

        if (!donneesStockes.isEmpty()) {
            for (Node value : dataP.getChildren()) {
                if (value instanceof TextField) {
                    ((TextField) value).setText(donneesStockes.get(i).toString());
                    i++;
                }
            }
        }
    }





}


