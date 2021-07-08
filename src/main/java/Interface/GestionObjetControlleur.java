package Interface;

import Thread.*;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import CalculOptimisation.Optimisation;
import GestionnaireDonnees.BDDSingleton;
import GestionnaireDonnees.Donnee;
import ObjetConnecte.*;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

public class GestionObjetControlleur implements Initializable {

    @FXML
    public StackPane StackPiece;
    @FXML
    public StackPane StackObjet;
    @FXML
    public StackPane StackReglage;
    @FXML
    public AnchorPane Acceuil;
    @FXML
    private Button droite;
    @FXML
    public Label consoH;

    @FXML
    public JFXToggleButton ToggleEconomique;


    private static ThreadOptiGlobale autoGlobale;
    private static ThreadOptiObjet autoObj;

    private double finalx;
    private double finaly;

    public static ArrayList<ObjetConnecte> recevoirObjets = new ArrayList<ObjetConnecte>() ;
    public static ArrayList<String> listePieces = new ArrayList<>();

    Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

    UnaryOperator<TextFormatter.Change> filter = c -> {
        String text = c.getControlNewText();
        if (validEditingState.matcher(text).matches()) {
            return c;
        } else {
            return null;
        }
    };

    StringConverter<Double> converter = new StringConverter<Double>() {

        @Override
        public Double fromString(String s) {
            if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                return 0.0; //null ;
            } else {
                return Double.valueOf(s);
            }
        }


        @Override
        public String toString(Double d) {
            return d.toString();
        }
    };



    public static void setStopAutoGlobale() {
        autoGlobale.stop();
    }


    public static void setStopAutoObj() {
        autoObj.stop();
    }


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        try {
            //if(recevoirObjets!=null && !recevoirObjets.isEmpty())
                construire();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }


    public void Eteindre(float val)
    {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                consoH.setText(val+" watts/h");
            }
        });

    }

    //Final
    public void creerPieceLayout(String nom) {

        //HBOX qui va contenir la piece
        Image img = null;
        HBox HBoxPiece = new HBox();
        switch (nom.charAt(0))
        {
            case 'S' :
                if (nom.charAt(3)=='l')
                    img = new Image("Interface/images/douche.png");
                else
                    img = new Image("Interface/images/sofa.png");
                break;
            case 'C' : {
                if (nom.charAt(1) == 'h')
                    img = new Image("Interface/images/chambregeneral.png");
                else if (nom.charAt(1) == 'u')
                    img = new Image("Interface/images/cuisine.png");
                else
                    img = new Image("Interface/images/corridor.png");
                break;
            }
            case 'W':
                    img = new Image("Interface/images/wc.png");
                    break;
            case 'G' :
                    img = new Image("Interface/images/garage.png");
                    break;
        }

        //image du type de la piece
        ImageView imgV = new ImageView();
        imgV.setImage(img);
        HBox.setMargin(imgV, new Insets(6, 20, 0, 4));
        imgV.setFitWidth(48);
        imgV.setFitHeight(48);
        imgV.setPreserveRatio(true);

        //Nom de la piece
        Label labelTmp = new Label(nom);
        labelTmp.setPrefWidth(100);
        labelTmp.setPrefHeight(60);
        labelTmp.setWrapText(true);
        labelTmp.setTextFill(Color.web("White"));
        labelTmp.setStyle("-fx-font-weight: bold");

        //Supprimer
        Image ii = new Image("Interface/images/icons8-delete-26.png");
        ImageView view = new ImageView(ii);
        Button SupprimerButton = new Button();
        SupprimerButton.setStyle("-fx-background-color: transparent");
        view.setPreserveRatio(true);
        SupprimerButton.setGraphic(view);
        HBox.setMargin(SupprimerButton, new Insets(20, 0, 0, 4));

        SupprimerButton.setOnMouseEntered(event -> {
            Acceuil.getScene().setCursor(Cursor.HAND); //Change cursor to hand
            SupprimerButton.setStyle("-fx-background-color: RED");
        });

        SupprimerButton.setOnMouseExited(event -> {
            Acceuil.getScene().setCursor(Cursor.DEFAULT); //Change cursor to crosshair
            SupprimerButton.setStyle("-fx-background-color: transparent");
        });

        SupprimerButton.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(" Confirmation de Suppression de piece");
            alert.setContentText(" Etes-vous sûr de vouloir supprimer la piece '"+nom+"' cela engendrera" +
                    " la suppression de tous les objets qu'elle contient ?");
            alert.setResizable(true);
            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == ButtonType.OK) {
                //supprimer graphiquement
                ScrollPane tmp = (ScrollPane) StackObjet.getChildren().get(StackObjet.getChildren().size() - 1);
                VBox vbtmp = (VBox) tmp.getContent();
                for (int i = 0; i < vbtmp.getChildren().size(); i++) {
                    for (int j = 0; j < StackReglage.getChildren().size(); j++) {
                        if (StackReglage.getChildren().get(j).getId().equals(vbtmp.getChildren().get(i).getId())) {
                            StackReglage.getChildren().remove(j);
                        }
                    }
                }
                StackObjet.getChildren().remove(StackObjet.getChildren().size() - 1);
                StackPiece.getChildren().remove(StackPiece.getChildren().size() - 1);


                //supprimer dans la BD
                String delete_requete = "delete from Chambre where nom_chambre = \"" + nom + "\" AND mail_utilisateur = ";
                try {
                    Donnee data = new Donnee("Suppression d'une piece", "L'utilisateur a supprime la piece " + nom + "");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                BDDSingleton bdd = BDDSingleton.getInstance();
                try {
                    bdd.decisionRequete(delete_requete);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                Routeur r = Routeur.getInstance();
                ArrayList<Integer> j = new ArrayList<Integer>();

                for (int i = 0; i < recevoirObjets.size(); i++) {
                    if (recevoirObjets.get(i).getPiece().equals(nom)) {
                        try {
                            r.supprimerRef(recevoirObjets.get(i).getId());
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        j.add(i);
                    }
                }

                for (Integer v : j) {
                    recevoirObjets.remove(v);
                }

                listePieces.remove(nom);
            }
        });

        HBoxPiece.getChildren().addAll(imgV, labelTmp, SupprimerButton);
        HBoxPiece.setStyle("-fx-background-color:  #6495ED");

        StackPiece.getChildren().addAll(HBoxPiece);
        ScrollPane sp = new ScrollPane();
        sp.setStyle("-fx-background-color:  #6495ED");

        //creer la VBox des objets
        VBox vboxNew = new VBox();
        vboxNew.setStyle("-fx-background-color: #6495ED");
        vboxNew.setSpacing(3);
        sp.setContent(vboxNew);
        StackObjet.getChildren().add(sp);

        //Mettre en avant un reglage vide
        ObservableList<Node> childsReglage = this.StackReglage.getChildren();
        for (int i = 0; i < childsReglage.size(); i++) {
            StackReglage.getChildren().get(i).setVisible(false);
        }
    }

    //SemiFinal
    public void creerObjetLayout(ObjetConnecte objetC, String nomPiece) throws IOException{

        //HBOX qui va contenir l'objet (Cliquable)
        HBox hbButton = new HBox();
        hbButton.setPrefSize(300, 100);

        //VBOX qui va contenir les reglages de l'objet
        VBox vbReglage = null;

        //Image du type de l'objet
        ImageView imgV = new ImageView();
        imgV.setFitWidth(60);
        imgV.setFitHeight(60);
        imgV.setPreserveRatio(true);
        HBox.setMargin(imgV, new Insets(25, 20, 20, 20));
        hbButton.setStyle("-fx-border-color : white ");

        //Nom de l'objet
        Label labelTmp = new Label(objetC.getNom());
        labelTmp.setTextFill(Color.web("White"));
        labelTmp.setStyle("-fx-font-weight: bold");
        labelTmp.setPrefWidth(160);
        HBox.setMargin(labelTmp, new Insets(45, 0, 0, 0));

        hbButton.getChildren().addAll(imgV, labelTmp);

        if (objetC.getType().equals("Lampe")) {
            vbReglage = FXMLLoader.load(getClass().getResource("FXMLFiles/LampeReglage.fxml"));

            Image img = new Image("Interface/images/icons8-light-bulb-64.png");
            imgV.setImage(img);

            VBox finalVbReglage = vbReglage;

            ((Button)((HBox)vbReglage.getChildren().get(4)).getChildren().get(2)).setOnAction(event -> {
                ((Lampe)objetC).setSeuilConso( Float.valueOf(( (TextField) ((HBox) finalVbReglage.getChildren().get(4)).getChildren().get(1)).getText()));
            }); // SEUIL CONSO

            ((Button)((HBox)vbReglage.getChildren().get(5)).getChildren().get(2)).setOnAction(event -> {
                ((Lampe)objetC).setLuminosite( Float.valueOf((float) ( (Slider) ((HBox) finalVbReglage.getChildren().get(5)).getChildren().get(1)).getValue()));
            }); // LUMINOSITE

            ((TextField)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setText(((Lampe)objetC).getSeuilConso()+"");

            ((Slider) ((HBox) finalVbReglage.getChildren().get(5)).getChildren().get(1)).setValue(((Lampe)objetC).getLuminosite());

        } else if (objetC.getType().equals("Chauffage")) {
            vbReglage = FXMLLoader.load(getClass().getResource("FXMLFiles/ChauffageReglage.fxml"));

            Image img = new Image("Interface/images/icons8-heating-radiator-48.png");
            imgV.setImage(img);

            VBox finalVbReglage = vbReglage;
            ((Button)((HBox)vbReglage.getChildren().get(4)).getChildren().get(2)).setOnAction(event -> {
                ((Chauffage)objetC).setTemperature( Float.valueOf((float) ( (Slider) ((HBox) finalVbReglage.getChildren().get(4)).getChildren().get(1)).getValue()));
            });
            ((Slider)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setValue(((Chauffage)objetC).getTemperature());

            ((Slider)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setMax(((Chauffage)objetC).getTempMax());
            ((Slider)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setMin(((Chauffage)objetC).getTempMin());

            ((Slider)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setShowTickLabels(true);
            ((Slider)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setShowTickMarks(true);
            ((Slider)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setMajorTickUnit(5);


        } else if (objetC.getType().equals("Thermostat")) {
            vbReglage = FXMLLoader.load(getClass().getResource("FXMLFiles/ThermostatReglage.fxml"));

            Image img = new Image("Interface/images/clima.png");

            imgV.setImage(img);

            VBox finalVbReglage = vbReglage;
            ((Button)((HBox)vbReglage.getChildren().get(4)).getChildren().get(2)).setOnAction(event -> {
                ((Thermostat)objetC).setTemperature( Float.valueOf((float) ( (Slider) ((HBox) finalVbReglage.getChildren().get(4)).getChildren().get(1)).getValue()));
            });
            ((Slider)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setValue(((Thermostat)objetC).getTemperature());
            ((Slider)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setMin(-15);
            ((Slider)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setMax(40);

            ((Slider)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setShowTickLabels(true);
            ((Slider)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setShowTickMarks(true);
            ((Slider)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setMajorTickUnit(5);

        } else if (objetC.getType().equals("Television")) {
            vbReglage = FXMLLoader.load(getClass().getResource("FXMLFiles/TelevisionReglage.fxml"));

            Image img = new Image("Interface/images/tv.png");
            imgV.setImage(img);

            VBox finalVbReglage1 = vbReglage;
            ((Button)((HBox)vbReglage.getChildren().get(4)).getChildren().get(2)).setOnAction(event -> {
                ((Television)objetC).setSeuilTV( Float.valueOf( ((TextField) ((HBox) finalVbReglage1.getChildren().get(4)).getChildren().get(1)).getText()) );
            });
            ((Button)((HBox)vbReglage.getChildren().get(5)).getChildren().get(2)).setOnAction(event -> {
                ((Television)objetC).setLuminosite( (float) ((Slider) ((HBox) finalVbReglage1.getChildren().get(5)).getChildren().get(1)).getValue() );
            });

            ((TextField)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setText(((Television)objetC).getSeuilTV()+"");
            ((Slider)((HBox)vbReglage.getChildren().get(5)).getChildren().get(1)).setValue(((Television)objetC).getSeuilTV());

        } else if (objetC.getType().equals("Enceinte")) {
            vbReglage = FXMLLoader.load(getClass().getResource("FXMLFiles/EnceinteConnecteReglage.fxml"));

            Image img = new Image("Interface/images/icons8-portable-speaker-64.png");
            imgV.setImage(img);

            VBox finalVbReglage = vbReglage;
            ((Button)((HBox)vbReglage.getChildren().get(4)).getChildren().get(2)).setOnAction(event -> {
                ((EnceinteConnecte)objetC).setVolumeEnregistrement((int) ( (Slider) ((HBox) finalVbReglage.getChildren().get(4)).getChildren().get(1)).getValue());
            });

            ((Button)((HBox)vbReglage.getChildren().get(5)).getChildren().get(2)).setOnAction(event -> {
                ((EnceinteConnecte)objetC).setSeuil(Float.valueOf ( ((TextField) ((HBox) finalVbReglage.getChildren().get(5)).getChildren().get(1)).getText() ));
            });

            ((Button)((HBox)vbReglage.getChildren().get(6)).getChildren().get(2)).setOnAction(event -> {
                ((EnceinteConnecte)objetC).setDureeEnregistrement(Integer.valueOf ( ((TextField) ((HBox) finalVbReglage.getChildren().get(6)).getChildren().get(1)).getText() ));
            });
            ((Slider)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setValue(((EnceinteConnecte)objetC).getVolumeEnregistrement());
            ((TextField)((HBox)vbReglage.getChildren().get(5)).getChildren().get(1)).setText(((EnceinteConnecte)objetC).getSeuil()+"");
            ((TextField)((HBox)vbReglage.getChildren().get(6)).getChildren().get(1)).setText(((EnceinteConnecte)objetC).getDureeEnregistrement()+"");
        } else if (objetC.getType().equals("Climatiseur")) {
            vbReglage = FXMLLoader.load(getClass().getResource("FXMLFiles/ClimatiseurReglage.fxml"));

            Image img = new Image("Interface/images/icons8-air-conditioner-100.png");
            imgV.setImage(img);

            VBox finalVbReglage = vbReglage;
            ((Button)((HBox)vbReglage.getChildren().get(4)).getChildren().get(2)).setOnAction(event -> {
                ((Climatiseur)objetC).setSeuilClim(Float.valueOf ( ((TextField) ((HBox) finalVbReglage.getChildren().get(4)).getChildren().get(1)).getText() ));
            });

            ((Button)((HBox)vbReglage.getChildren().get(5)).getChildren().get(2)).setOnAction(event -> {
                ((Climatiseur)objetC).setTemperature(Float.valueOf ((float) ((Slider) ((HBox) finalVbReglage.getChildren().get(5)).getChildren().get(1)).getValue() ));
            });
            ((TextField)((HBox)vbReglage.getChildren().get(4)).getChildren().get(1)).setText(((Climatiseur)objetC).getSeuilClim()+"");
            ((Slider)((HBox)vbReglage.getChildren().get(5)).getChildren().get(1)).setValue(((Climatiseur)objetC).getTemperature());

        } else if (objetC.getType().equals("Refrigerateur")) {
            vbReglage = FXMLLoader.load(getClass().getResource("FXMLFiles/RefrigirateurReglage.fxml"));

            Image img = new Image("Interface/images/icons8-fridge-64.png");
            imgV.setImage(img);

            VBox finalVbReglage = vbReglage;

            ((Button)((HBox)vbReglage.getChildren().get(4)).getChildren().get(2)).setOnAction(event -> {
                ((Refrigerateur)objetC).setNiveauFraicheur(Float.valueOf ((String) ((ChoiceBox) ((HBox) finalVbReglage.getChildren().get(4)).getChildren().get(1)).getSelectionModel().getSelectedItem() ));
            });

            ((ChoiceBox) ((HBox) finalVbReglage.getChildren().get(4)).getChildren().get(1)).getItems().addAll("1.0","2.0","3.0","4.0","5.0");
            ((ChoiceBox) ((HBox) finalVbReglage.getChildren().get(4)).getChildren().get(1)).setValue(((Refrigerateur)objetC).getNiveauFraicheur());
        } else if (objetC.getType().equals("Machine_a_laver")) {
            vbReglage = FXMLLoader.load(getClass().getResource("FXMLFiles/MachineALaverReglage.fxml"));

            Image img = new Image("Interface/images/machine_a_laver.png");
            imgV.setImage(img);

            VBox finalVbReglage = vbReglage;

            ((Button)((HBox)vbReglage.getChildren().get(4)).getChildren().get(2)).setOnAction(event -> {
                ((MachineALaver)objetC).setDureeLavage(Integer.valueOf ( ((TextField) ((HBox) finalVbReglage.getChildren().get(4)).getChildren().get(1)).getText() ));
            });
            ((ChoiceBox) ((HBox) finalVbReglage.getChildren().get(5)).getChildren().get(1)).getItems().addAll("40 degre","60 degre","120 degre");

            ((Button)((HBox)vbReglage.getChildren().get(5)).getChildren().get(2)).setOnAction(event -> {
                ((MachineALaver)objetC).setTypeLavage( (String) ((ChoiceBox) ((HBox) finalVbReglage.getChildren().get(5)).getChildren().get(1)).getSelectionModel().getSelectedItem() );
            });
            ((TextField) ((HBox) finalVbReglage.getChildren().get(4)).getChildren().get(1)).setText(((MachineALaver)objetC).getDureeLavage()+"");
            ((ChoiceBox) ((HBox) finalVbReglage.getChildren().get(5)).getChildren().get(1)).setValue(((MachineALaver)objetC).getDureeLavage());

        } else if (objetC.getType().equals("CapteurPresence")) {
            vbReglage = FXMLLoader.load(getClass().getResource("FXMLFiles/CapteurPresenceReglage.fxml"));

            Image img = new Image("Interface/images/capteur.png");
            imgV.setImage(img);

            VBox finalVbReglage = vbReglage;
            ((ChoiceBox) ((HBox) finalVbReglage.getChildren().get(4)).getChildren().get(1)).getItems().addAll("Abscent","Present");

            ((Button)((HBox)vbReglage.getChildren().get(4)).getChildren().get(2)).setOnAction(event -> {
                ((CapteurPresence)objetC).setPlacementUtilisateur( (String) ((ChoiceBox) ((HBox) finalVbReglage.getChildren().get(4)).getChildren().get(1)).getSelectionModel().getSelectedItem() );
            });
            ((ChoiceBox) ((HBox) finalVbReglage.getChildren().get(4)).getChildren().get(1)).setValue(((CapteurPresence)objetC).getPlacementUtilisateur());
        }
        else
        {
            System.out.println("Erreur : type non reconnu");
        }

        VBox finalVbReglage2 = vbReglage;

        ((Button)((HBox)vbReglage.getChildren().get(0)).getChildren().get(2)).setOnAction(event -> {
            (objetC).setNom( ((TextField) ((HBox) finalVbReglage2.getChildren().get(0)).getChildren().get(1)).getText() );
            labelTmp.setText(objetC.getNom());
        }); // NOM

        ((Button)((HBox)vbReglage.getChildren().get(1)).getChildren().get(2)).setOnAction(event -> {
            (objetC).setConsoHoraire( Float.valueOf( ((TextField) ((HBox) finalVbReglage2.getChildren().get(1)).getChildren().get(1)).getText()) );
        }); // CONSO HORAIRE

        ((Button)((HBox)vbReglage.getChildren().get(3)).getChildren().get(2)).setOnAction(event -> {
            (objetC).setDroitAcces((String) ((ChoiceBox) ((HBox) finalVbReglage2.getChildren().get(3)).getChildren().get(1)).getSelectionModel().getSelectedItem() );
        }); // DROIT ACCES

        ((JFXToggleButton)((HBox)vbReglage.getChildren().get(0)).getChildren().get(0)).setOnAction(event ->
        {
            if (((JFXToggleButton)((HBox)finalVbReglage2.getChildren().get(0)).getChildren().get(0)).isSelected()) // LE CAS DU ON
            {
                try {
                    if (isHeurePleine() && objetC.getType().equals("Machine_a_laver")) // CAS MACHINE A LAVER
                    {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setHeaderText(" Confirmation Allumage en heure pleine");
                        alert.setContentText(" Etes-vous sur de vouloir allumer la machine à laver dans une heure pleine ? ");
                        alert.setResizable(true);
                        Optional<ButtonType> option = alert.showAndWait();

                        if (option.get() == ButtonType.OK)
                        {
                            consoH.setText(Float.valueOf(consoH.getText().substring(0,consoH.getText().length()-7)) + objetC.getConsoHoraire() + " watts/h");
                            objetC.changeEtat();
                        } else if (option.get() == ButtonType.CANCEL) {
                            ((JFXToggleButton)((HBox)finalVbReglage2.getChildren().get(0)).getChildren().get(0)).setSelected(false);
                        }
                    }
                    else // LES AUTRES CAS POSSIBLES
                    {
                        consoH.setText(Float.valueOf(consoH.getText().substring(0,consoH.getText().length()-7)) + objetC.getConsoHoraire() + "watts/h");
                        objetC.changeEtat();
                    }


                } catch (SQLException | InterruptedException | IOException throwables) {
                    throwables.printStackTrace();
                }
            }
            else // LE CAS DU OFF
                {
                try {
                    objetC.changeEtat();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                consoH.setText(Float.valueOf(consoH.getText().substring(0,consoH.getText().length()-7)) - objetC.getConsoHoraire() + " watts/h");
            }

        }); // ETAT


        //Action si on clique sur l'objet
        hbButton.setPrefSize(300, 100);
        hbButton.setOnMouseClicked(e ->
        {
            ObservableList<Node> childsReglage = this.StackReglage.getChildren();
            for (int i = 0; i < childsReglage.size(); i++)
            {
                if (hbButton.getId().equals(StackReglage.getChildren().get(i).getId())) {
                    StackReglage.getChildren().get(i).setVisible(true);

                    if(objetC.getType().equals("Lampe"))
                    {
                        ((TextField)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(4)).getChildren().get(1)).setText(((Lampe)objetC).getSeuilConso()+"");

                        ((Slider) ((HBox) ((VBox)StackReglage.getChildren().get(i)).getChildren().get(5)).getChildren().get(1)).setValue(((Lampe)objetC).getLuminosite());
                    }
                    else if (objetC.getType().equals("Chauffage"))
                    {
                        ((Slider)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(4)).getChildren().get(1)).setValue(((Chauffage)objetC).getTemperature());
                    }
                    else if (objetC.getType().equals("Thermostat"))
                    {
                        ((Slider)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(4)).getChildren().get(1)).setValue(((Thermostat)objetC).getTemperature());
                    }
                    else if (objetC.getType().equals("Television"))
                    {
                        ((TextField)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(4)).getChildren().get(1)).setText(((Television)objetC).getSeuilTV()+"");
                        ((Slider)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(5)).getChildren().get(1)).setValue(((Television)objetC).getLuminosite());
                    }
                    else if (objetC.getType().equals("Enceinte"))
                    {
                        ((Slider)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(4)).getChildren().get(1)).setValue(((EnceinteConnecte)objetC).getVolumeEnregistrement());
                        ((TextField)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(5)).getChildren().get(1)).setText(((EnceinteConnecte)objetC).getSeuil()+"");
                        ((TextField)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(6)).getChildren().get(1)).setText(((EnceinteConnecte)objetC).getDureeEnregistrement()+"");
                    }
                    else if (objetC.getType().equals("Climatiseur"))
                    {
                        ((TextField)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(4)).getChildren().get(1)).setText(((Climatiseur)objetC).getSeuilClim()+"");
                        ((Slider)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(5)).getChildren().get(1)).setValue(((Climatiseur)objetC).getTemperature());
                    }
                    else if (objetC.getType().equals("Refrigerateur"))
                    {
                        ((ChoiceBox)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(4)).getChildren().get(1)).setValue(((Refrigerateur)objetC).getNiveauFraicheur());
                    }
                    else if (objetC.getType().equals("Machine_a_laver"))
                    {
                        ((TextField)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(4)).getChildren().get(1)).setText(((MachineALaver)objetC).getDureeLavage()+"");
                        ((ChoiceBox)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(5)).getChildren().get(1)).setValue(((MachineALaver)objetC).getTypeLavage());
                    }
                    else if (objetC.getType().equals("CapteurPresence"))
                    {
                        ((ChoiceBox)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(4)).getChildren().get(1)).setValue(((CapteurPresence)objetC).getPlacementUtilisateur());
                    }


                    ((TextField)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(0)).getChildren().get(1)).setText((objetC).getNom()+"");
                    ((TextField)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(1)).getChildren().get(1)).setText((objetC).getConsoHoraire()+"");

                    ((ChoiceBox)((HBox)((VBox)StackReglage.getChildren().get(i)).getChildren().get(3)).getChildren().get(1)).setValue(objetC.getDroitAcces());
                    ((JFXToggleButton) ( (HBox) ((VBox)StackReglage.getChildren().get(i)).getChildren().get(0) ).getChildren().get(0)).setSelected(objetC.getEtat()==1);


                    StackReglage.getChildren().get(i).toFront();
                    break;
                }
            }
        });

        //Ajout de Hbox de l'objet
        int i;
        for ( i = 0; i < StackPiece.getChildren().size(); i++) {
            if (((Label) ((HBox)StackPiece.getChildren().get(i)).getChildren().get(1)).getText().equals(objetC.getPiece()))
            {
                break;
            }
        }
        ScrollPane tmpSP = (ScrollPane) StackObjet.getChildren().get(i);

        VBox vb = (VBox) tmpSP.getContent();
        vb.getChildren().add(hbButton);

        //Bouton supprimer Objet
        Image im = new Image("Interface/images/icons8-delete-26.png");
        ImageView view = new ImageView(im);
        Button SupprimerButton = new Button();
        SupprimerButton.setStyle("-fx-background-color: transparent");
        view.setPreserveRatio(true);
        SupprimerButton.setGraphic(view);
        HBox.setMargin(SupprimerButton, new Insets(40, 0, 0, 0));
        hbButton.getChildren().add(SupprimerButton);
        VBox finalVboxTmp = vbReglage;

        SupprimerButton.setOnMouseEntered(event -> {
            Acceuil.getScene().setCursor(Cursor.HAND); //Change cursor to hand
            SupprimerButton.setStyle("-fx-background-color: RED");
        });

        SupprimerButton.setOnMouseExited(event -> {
            Acceuil.getScene().setCursor(Cursor.DEFAULT); //Change cursor to crosshair
            SupprimerButton.setStyle("-fx-background-color: transparent");
        });

        SupprimerButton.setOnMouseClicked(e -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(" Confirmation de Suppression d'objet");
            alert.setContentText(" Etes-vous sûr de vouloir supprimer l'objet '"+objetC.getNom()+"' ?");
            alert.setResizable(true);
            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == ButtonType.OK)
            {
                Routeur r = Routeur.getInstance();
                try {
                    r.supprimerRef(objetC.getId());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                vb.getChildren().remove(hbButton);
                StackReglage.getChildren().remove(finalVboxTmp);
                for (ObjetConnecte v :recevoirObjets) {
                    if (v.getId()==objetC.getId())
                    {
                        recevoirObjets.remove(v);
                        break;
                    }
                }
            }

        });

        //NomField
        HBox HB = (HBox) vbReglage.getChildren().get(0);
        TextField Field = (TextField) HB.getChildren().get(1);
        Field.setText(objetC.getNom());

        //ConsoField
        HB = (HBox) vbReglage.getChildren().get(1);
        Field = (TextField) HB.getChildren().get(1);

        //Field.setOn
        TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 0.0, filter);
        Field.setTextFormatter(textFormatter);
        Field.setText("" + objetC.getConsoHoraire());

        //DroitAccesField
        HB = (HBox) vbReglage.getChildren().get(3);
        ChoiceBox<String> choiceDA = (ChoiceBox<String>) HB.getChildren().get(1);
        choiceDA.getItems().addAll("OK", "Interdit", "Restreint");
        choiceDA.setValue(objetC.getDroitAcces());

        if (objetC.getEtat()==1)
        {
            ((JFXToggleButton)((HBox)vbReglage.getChildren().get(0)).getChildren().get(0)).setSelected(true);
        }
        else {
            ((JFXToggleButton)((HBox)vbReglage.getChildren().get(0)).getChildren().get(0)).setSelected(false);
        }

        Button ButtonHistorique;
        if(!objetC.getType().equals("Enceinte"))  // GERER LE CAS D'ENCEINTE CONNECTE pour le boutton historique
            ButtonHistorique = (Button) vbReglage.getChildren().get(6);
        else
            ButtonHistorique = (Button) vbReglage.getChildren().get(7);

        ButtonHistorique.setOnAction(e -> {
            Stage window1 = new Stage();

            window1.setTitle("Calculer Historique");
            window1.setResizable(false);
            window1.initModality(Modality.APPLICATION_MODAL);
            window1.initStyle(StageStyle.UNDECORATED);
            AnchorPane root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("FXMLFiles/CalculerHistorique.fxml"));
                TextField tf = (TextField) root.getChildren().get(7);
                ((Label) root.getChildren().get(8)).setText(objetC.getNom());
                tf.setText("" + objetC.getId());

                TableView tv = (TableView) root.getChildren().get(0);
                TableColumn tc = (TableColumn) tv.getColumns().get(5);

                if (objetC.getType().equals("Lampe"))
                {
                    tc.setText("Seuil Luminosite");
                }
                else if(objetC.getType().equals("CapteurPresence"))
                {
                    tc.setText("Placement de l'utilisateur");
                }
                else if(objetC.getType().equals("Chauffage"))
                {
                    tc.setText("Temperature|Temp Max|Temp Min");

                }
                else if(objetC.getType().equals("Climatiseur"))
                {
                    tc.setText("Temperature |Seuil");
                }
                else if(objetC.getType().equals("Enceinte"))
                {
                    tc.setText("Seuil|Durée enregistrement|Volume");
                }
                else if(objetC.getType().equals("Machine_a_laver"))
                {
                    tc.setText("Type |Durée");
                }
                else if(objetC.getType().equals("Refrigerateur"))
                {
                    tc.setText("Niveau de fraicheur");
                }
                else if(objetC.getType().equals("Television"))
                {
                    tc.setText("Seuil|Luminosité");
                }
                else if(objetC.getType().equals("Thermostat"))
                {
                    tc.setText("Temperature");
                }

                // HistoriqueObjetControlleur.num_objet = objetC.getId();
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

        });

        //ID de l'objet et de ses reglages
        hbButton.setId(String.valueOf(objetC.getId()));
        vbReglage.setId(String.valueOf(objetC.getId()));

        //Ajout des reglages de l'objet
        StackReglage.getChildren().add(vbReglage);
    }

    //Final
    public void construire() throws IOException, SQLException {

        String nomPiece;

        String requete = "select * from Chambre where mail_utilisateur = ";

        BDDSingleton bdd = BDDSingleton.getInstance();

        ResultSet rs = bdd.decisionRequete(requete);

        consoH.setText(Optimisation.getConsoHoraireGlobale() + " watts/h");
        if (recevoirObjets==null || listePieces==null)
        {
            return;
        }
        float tmpH = 0;
        for (int i = 0; i < recevoirObjets.size(); i++) {
            if (recevoirObjets.get(i).getEtat() == 1 ) {
                tmpH = tmpH + recevoirObjets.get(i).getConsoHoraire();
            }
        }
        consoH.setText(tmpH + " watts/h");


        while (rs.next()) {
            creerPieceLayout(rs.getString("nom_chambre"));
            listePieces.add(rs.getString("nom_chambre"));
        }

        for (int i = 0; i < recevoirObjets.size(); i++) {

            nomPiece = recevoirObjets.get(i).getPiece();

            for (int j = 0; j < listePieces.size(); j++) {

                if (listePieces.get(j).equals(nomPiece)) {
                    creerObjetLayout(recevoirObjets.get(i), nomPiece);
                }
            }
        }

        ////////////////////////AREFAIIRE::::::::::::::::::::::::::::::::::
        ObservableList<Node> childsReglage1 = this.StackReglage.getChildren();
        for (int i = 0; i < childsReglage1.size(); i++) {

            ScrollPane sp = (ScrollPane) StackObjet.getChildren().get(0);
            VBox vb = (VBox) sp.getContent();
            HBox hb = (HBox) vb.getChildren().get(0);

            if (hb.getId() == childsReglage1.get(i).getId()) {

                StackReglage.getChildren().get(i).setVisible(true);

            } else {
                StackReglage.getChildren().get(i).setVisible(false);
            }

        }


        if (!listePieces.isEmpty())
        {
            //Cas special une piece vide ne pas doit afficher de reglages
            if (((VBox) ((ScrollPane) StackObjet.getChildren().get(StackObjet.getChildren().size() - 1)).getContent()).getChildren().isEmpty()) {

                //Mettre en avant un reglage vide
                ObservableList<Node> childsReglage = this.StackReglage.getChildren();
                for (int i = 0; i < childsReglage.size(); i++) {
                    StackReglage.getChildren().get(i).setVisible(false);
                }
            }
        }


        }

    public boolean isHeurePleine() throws SQLException
    {
        BDDSingleton base = BDDSingleton.getInstance();

        ResultSet resultset2 = base.requeteSelection
                ("SELECT * FROM Configuration_SmartHome WHERE mail_utilisateur=");

        String [] heureP=null;
        float prixHP=0;
        float prixHC=0;
        if(resultset2.next()) {
            prixHC = resultset2.getFloat("PrixHC");
            prixHP= resultset2.getFloat("PrixHP");


            if (resultset2.getString("Debut_heure_hp2") != null) {
                heureP = new String[4];
                heureP[0] = resultset2.getString("Debut_heure_hp1");
                heureP[1] = resultset2.getString("Fin_heure_hp1");
                heureP[2] = resultset2.getString("Debut_heure_hp2");
                heureP[3] = resultset2.getString("Fin_heure_hp2");
            } else {
                heureP = new String[2];
                heureP[0] = resultset2.getString("Debut_heure_hp1");
                heureP[1] = resultset2.getString("Fin_heure_hp1");
            }
        }

        LocalTime time1 = LocalTime.now();
        // time1 = LocalTime.parse("21:29:00");
        if(heureP.length == 2)
        {
            if (time1.isAfter(LocalTime.parse(heureP[0])) && time1.isBefore(LocalTime.parse(heureP[1])))
            {
                return true;
            }
        }
        else if (heureP.length == 4)
        {
            if (  (time1.isAfter(LocalTime.parse(heureP[0])) && time1.isBefore(LocalTime.parse(heureP[1])) )
                    || ( time1.isAfter(LocalTime.parse(heureP[2])) && time1.isBefore(LocalTime.parse(heureP[3])) ) )
            {
                return true;
            }
        }

        return false;
    }

    public void economiqueClicked() throws SQLException {

        if (ToggleEconomique.isSelected())
        {
            //System.out.println("ACTIVER");
            if (isHeurePleine())
            {
                float NewSeuil = Optimisation.getInstance().getSeuilConsoGlobale() / 2;
                Optimisation.getInstance().setSeuilConsoGlobale(NewSeuil);
            }

            Donnee d = new Donnee("modeEconomique","L'utilisateur a active le mode economique");

            autoGlobale= new ThreadOptiGlobale();
            autoObj = new ThreadOptiObjet();

            autoGlobale.start();
            autoObj.start();

        }
        else
        {
            //System.out.println("DES  ACTIVER");
            float oldSeuil = Optimisation.getInstance().getSeuilConsoGlobale()*2;
            Optimisation.getInstance().setSeuilConsoGlobale(oldSeuil);

            Donnee d = new Donnee("modeEconomique","L'utilisateur a desactiver le mode economique");

            setStopAutoObj();
            setStopAutoGlobale();

        }
    }


    @FXML
    public void LancerOptimisation() throws SQLException, IOException, InterruptedException
    {   if(recevoirObjets != null)
    {
        Optimisation.getInstance().optimiserConsommationGlobale();
        Donnee d = new Donnee("Optimisation", "L'utilisateur a lance une optimisation manuelle");
    }

    }


    /**
     * Fonction qui permet de rajouter une piece
     * @param event
     * @throws IOException
     * @throws SQLException
     */
    public void ajouterPieceClicked(ActionEvent event) throws IOException, SQLException {

        FenetresSecondaires fenetre = new FenetresSecondaires();
        ArrayList<String> res = fenetre.afficherAjoutPiece();

        if (res.isEmpty()) {
            System.out.println("no data found");
        } else {
            String insererPiece = "insert into Chambre values (\""+ res.get(0) +" "+res.get(1) + "\", ,"+ 25 + ");";

            listePieces.add(res.get(0)+" "+res.get(1));
            BDDSingleton base = BDDSingleton.getInstance();
            base.requeteInsertion(insererPiece);
            creerPieceLayout(res.get(0)+" "+res.get(1));
        }
    }


    /**
     * Fonction qui permet de rajouter un objet
     * @param event
     * @throws IOException
     * @throws SQLException
     */
    public void ajouterObjetClicked(ActionEvent event) throws IOException, SQLException{

        FenetresSecondaires fenetre = new FenetresSecondaires();
        ArrayList<Object> res = fenetre.afficherAjoutObjet();
        if ((!res.isEmpty()) && (!StackPiece.getChildren().isEmpty()))
        {
            ObjetConnecte theObject= null;

            if (res.get(0).equals("Lampe")) {
                theObject = new Lampe((String)res.get(1)
                        ,(String) res.get(0),
                        ((Label) ((HBox)StackPiece.getChildren().get(StackPiece.getChildren().size()-1)).getChildren().get(1) ).getText(),
                        Float.valueOf((String)res.get(2)),
                        (String) res.get(3),
                        (Float) res.get(4),
                        (Float)res.get(5)
                );

                Donnee D = new Donnee("creation d'objet","utilisateur a ajouté une Lampe");
                recevoirObjets.add(theObject);
                Routeur r = Routeur.getInstance();
                r.ajouterRefObjet(theObject);
            } else if (res.get(0).equals("Chauffage")) {
                theObject = new Chauffage((String)res.get(1)
                        ,(String) res.get(0),
                        ((Label) ((HBox)StackPiece.getChildren().get(StackPiece.getChildren().size()-1)).getChildren().get(1) ).getText(),
                        Float.valueOf((String)res.get(2)),(String) res.get(3),(Float) res.get(4));

                Donnee D = new Donnee("creation d'objet","utilisateur a ajouté un Chauffage");
                recevoirObjets.add(theObject);
                Routeur r = Routeur.getInstance();
                r.ajouterRefObjet(theObject);
            } else if (res.get(0).equals("Thermostat")) {
                theObject = new Thermostat((String)res.get(1)
                        ,(String) res.get(0),
                        ((Label) ((HBox)StackPiece.getChildren().get(StackPiece.getChildren().size()-1)).getChildren().get(1) ).getText(),
                        Float.valueOf((String)res.get(2)),(String) res.get(3),
                        (Float) res.get(4));

                Donnee D = new Donnee("creation d'objet","utilisateur a ajouté un Thermostat");
                recevoirObjets.add(theObject);
                Routeur r = Routeur.getInstance();
                r.ajouterRefObjet(theObject);

            } else if (res.get(0).equals("Television")) {
                theObject = new Television((String)res.get(1)
                        ,(String) res.get(0),
                        ((Label) ((HBox)StackPiece.getChildren().get(StackPiece.getChildren().size()-1)).getChildren().get(1) ).getText(),
                        Float.valueOf((String)res.get(2)),(String) res.get(3),(Float) res.get(4),(Float) res.get(5));

                Donnee D = new Donnee("creation d'objet","utilisateur a ajouté une Television");
                recevoirObjets.add(theObject);
                Routeur r = Routeur.getInstance();
                r.ajouterRefObjet(theObject);

            } else if (res.get(0).equals("Enceinte"))
            {

                float volume = Float.valueOf(res.get(5).toString());
                int volumeI = (int)volume;
                float duree = Float.valueOf(res.get(6).toString());
                int dureeI = (int)duree;

                theObject = new EnceinteConnecte(
                        (String)res.get(1)
                        ,(String) res.get(0)
                        ,((Label) ((HBox)StackPiece.getChildren().get(StackPiece.getChildren().size()-1)).getChildren().get(1) ).getText()
                        ,Float.valueOf((String)res.get(2))
                        ,(String) res.get(3)
                        ,volumeI
                        ,Float.valueOf(res.get(4).toString())
                        ,dureeI);

                Donnee D = new Donnee("creation d'objet","utilisateur a ajouté une enceinte connecte");
                recevoirObjets.add(theObject);
                Routeur r = Routeur.getInstance();
                r.ajouterRefObjet(theObject);

            } else if (res.get(0).equals("Climatiseur")) {
                theObject = new Climatiseur((String)res.get(1)
                        ,(String) res.get(0),
                        ((Label) ((HBox)StackPiece.getChildren().get(StackPiece.getChildren().size()-1)).getChildren().get(1) ).getText(),
                        Float.valueOf((String)res.get(2)),(String) res.get(3),(Float) res.get(4),(Float) res.get(5));

                Donnee D = new Donnee("creation d'objet","utilisateur a ajouté un Climatiseur");
                recevoirObjets.add(theObject);
                Routeur r = Routeur.getInstance();
                r.ajouterRefObjet(theObject);

            } else if (res.get(0).equals("Refrigerateur")) {;
                theObject = new Refrigerateur((String)res.get(1)
                        ,(String) res.get(0),
                        ((Label) ((HBox)StackPiece.getChildren().get(StackPiece.getChildren().size()-1)).getChildren().get(1) ).getText(),
                        Float.valueOf((String)res.get(2)),
                        (String) res.get(3),
                        Float.valueOf((String) res.get(4)));

                Donnee D = new Donnee("creation d'objet","L'utilisateur a ajouté un refrigerateur");
                recevoirObjets.add(theObject);
                Routeur r = Routeur.getInstance();
                r.ajouterRefObjet(theObject);
            }
            else if (res.get(0).equals("Machine_a_laver")) {
                float x = (Float.valueOf((String.valueOf(res.get(4)))));
                int temp = (int)x;
                theObject = new MachineALaver((String)res.get(1)
                        ,(String) res.get(0),
                        ((Label) ((HBox)StackPiece.getChildren().get(StackPiece.getChildren().size()-1)).getChildren().get(1) ).getText(),
                        Float.valueOf((String)res.get(2)),
                        (String) res.get(3),
                        temp,
                        //44,
                        (String) res.get(5));

                Donnee D = new Donnee("creation d'objet","L'utilisateur a ajouté une machine a laver");
                recevoirObjets.add(theObject);
                Routeur r = Routeur.getInstance();
                r.ajouterRefObjet(theObject);;

            } else if (res.get(0).equals("CapteurPresence")) {

                theObject = new CapteurPresence((String)res.get(1)
                        ,(String) res.get(0),
                        ((Label) ((HBox)StackPiece.getChildren().get(StackPiece.getChildren().size()-1)).getChildren().get(1) ).getText(),
                        Float.valueOf((String)res.get(2)),
                        (String) res.get(3),
                        (String) res.get(4));

                Donnee D = new Donnee("creation d'objet","utilisateur a ajouté un capteur Presence");
                recevoirObjets.add(theObject);
                Routeur r = Routeur.getInstance();
                r.ajouterRefObjet(theObject);
            }
            else {
                //System.out.println("Erreur selection d'objet");
            }

            creerObjetLayout(theObject,theObject.getPiece());
        }

    }

    /**
     *Fonction qui permet de changer les pieces
     * @param event
     */
    public void changerPiece(ActionEvent event) {

        ObservableList<Node> childsPiece = this.StackPiece.getChildren();
        ObservableList<Node> childsObjet = this.StackObjet.getChildren();
        ObservableList<Node> childsReglage = this.StackReglage.getChildren();

        if (childsPiece.size() > 1) {
            if (event.getSource() == droite) {
                Node topNodePiece = childsPiece.get(childsPiece.size() - 1);
                topNodePiece.toBack();
                Node topNodeObjet = childsObjet.get(childsObjet.size() - 1);
                topNodeObjet.toBack();
            } else {
                Node topNodePiece = childsPiece.get(0);
                topNodePiece.toFront();
                Node topNodeObjet = childsObjet.get(0);
                topNodeObjet.toFront();
            }

            ScrollPane tmp = (ScrollPane) childsObjet.get(childsObjet.size() - 1);
            VBox vbtmp = (VBox) tmp.getContent();

            if (!vbtmp.getChildren().isEmpty()) {
                for (int i = 0; i < childsReglage.size(); i++) {
                    if (StackReglage.getChildren().get(i).getId().equals(vbtmp.getChildren().get(0).getId())) {
                        StackReglage.getChildren().get(i).setVisible(true);
                        StackReglage.getChildren().get(i).toFront();
                    }
                }
            } else {
                for (int i = 0; i < childsReglage.size(); i++) {
                    StackReglage.getChildren().get(i).setVisible(false);
                }
            }
        }
    }
}
