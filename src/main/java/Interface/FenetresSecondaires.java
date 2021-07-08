package Interface;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

public class FenetresSecondaires {

    double finalx=0;
    double finaly =0;

    Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");
    UnaryOperator<TextFormatter.Change> filter = c -> {
        String text = c.getControlNewText();
        if (validEditingState.matcher(text).matches()) {
            return c ;
        } else {
            return null ;
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


    /**
     * Afficher la fenetre d'ajout d'objet
     * @return
     * @throws IOException
     */
    public ArrayList<String> afficherAjoutPiece() throws IOException {
        Stage window1 = new Stage();

        window1.setTitle("Ajouter une piece");
        window1.setMinWidth(600); window1.setMaxWidth(600);
        window1.setMinHeight(420); window1.setMaxHeight(420);
        window1.setResizable(false);
        window1.initModality(Modality.APPLICATION_MODAL);
        window1.initStyle(StageStyle.UNDECORATED);
        AnchorPane root = FXMLLoader.load(getClass().getResource("FXMLFiles/ajouterPiece.fxml"));

        root.setOnMousePressed(event -> {
            finalx= event.getSceneX();
            finaly= event.getSceneY();
        });


        root.setOnMouseDragged(event -> {
            window1.setX(event.getScreenX()-finalx);
            window1.setY(event.getScreenY()-finaly);
        });

        ImageView exit = (ImageView) root.getChildren().get(8);
        exit.setOnMouseClicked(e -> window1.close());

        ArrayList<String> res = new ArrayList<String>();
        Button Confirmer = (Button) root.getChildren().get(4);
        TextField NomField = (TextField) root.getChildren().get(7);
        @SuppressWarnings("unchecked")
        ChoiceBox<String> ChoiceType = (ChoiceBox<String>) root.getChildren().get(6);
        Label LabelErreur = (Label) root.getChildren().get(3);
        Button Annuler = (Button) root.getChildren().get(5);

        LabelErreur.setVisible(false);
        ChoiceType.getItems().addAll("Chambre","Cuisine","Salle_de_bain","WC","Garage","Salon","Couloir");

        Annuler.setOnAction(e -> window1.close());

        Confirmer.setOnAction(
                e -> {
                    if(NomField.getText().length()== 0 || ChoiceType.getSelectionModel().getSelectedItem()== null)
                    {
                        LabelErreur.setText("Erreur : les deux informations sont obligatoires"); LabelErreur.setVisible(true);
                    }
                    else {
                        res.add(ChoiceType.getSelectionModel().getSelectedItem());
                        res.add(NomField.getText());
                        window1.close();
                    }
                });

        //par defaut
        ChoiceType.setValue("Chambre");

        Scene Sc = new Scene(root);
        window1.setScene(Sc);
        window1.showAndWait();

        return res;
    }

    /**
     * Afficher la fenetre d'ajout d'objet
     * @return
     * @throws IOException
     */
    public ArrayList<Object> afficherAjoutObjet() throws IOException
    {
        Stage window1 = new Stage();
        window1.initModality(Modality.APPLICATION_MODAL);
        window1.initStyle(StageStyle.UNDECORATED);
        window1.setTitle("Ajouter un objet connecte");
        window1.setResizable(false);

        AnchorPane root = FXMLLoader.load(getClass().getResource("FXMLFiles/AjouterObjet.fxml"));

        root.setOnMousePressed(event -> {
            finalx= event.getSceneX();
            finaly= event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            window1.setX(event.getScreenX()-finalx);
            window1.setY(event.getScreenY()-finaly);
        });

        ImageView exit = (ImageView) root.getChildren().get(17);
        exit.setOnMouseClicked(e -> window1.close());

        ArrayList<Object> res = new ArrayList<Object>();

        Button Annuler = (Button) root.getChildren().get(3);
        Annuler.setOnAction(e -> window1.close());

        ChoiceBox<String> finalChoiceBoxType = (ChoiceBox<String>) root.getChildren().get(4);
        ChoiceBox<String> finalChoiceBoxDroitA = (ChoiceBox<String>) root.getChildren().get(8);

        TextField finalTexteFieldNom = (TextField) root.getChildren().get(5);

        TextField finalTexteFieldConso = (TextField) root.getChildren().get(6);
        TextFormatter<Double> textFormatter = new TextFormatter<>(converter,0.0, filter);
        finalTexteFieldConso.setTextFormatter(textFormatter);

        TextField finalTextFieldCommun = (TextField) root.getChildren().get(14);
        TextFormatter<Double> textFormatter2 = new TextFormatter<>(converter,0.0, filter);
        finalTextFieldCommun.setTextFormatter(textFormatter2);

        Slider finalSliderCommun2 = (Slider) root.getChildren().get(15);


        Label finalLabelErreur = (Label) root.getChildren().get(1);
        finalLabelErreur.setVisible(false);
        Label finalLabelCommun = (Label) root.getChildren().get(16);

        Label finalLabelCommun2 = (Label) root.getChildren().get(18);
        finalLabelCommun2.setText("Type de Lavage");
        finalLabelCommun2.setVisible(false);
        //pour la machine a laver
        ChoiceBox finalChoiceBoxCommun2 = (ChoiceBox) root.getChildren().get(19);
        finalChoiceBoxCommun2.setVisible(false);
        //pour le capteur presence
        ChoiceBox finalChoiceBoxCommun = (ChoiceBox) root.getChildren().get(20);
        finalChoiceBoxCommun.setVisible(false);


        finalLabelErreur.setVisible(false);
        finalChoiceBoxType.getItems().addAll("Lampe","Thermostat","Refrigerateur","Television","Enceinte", "Climatiseur","Chauffage","Machine_a_laver","CapteurPresence");
        finalChoiceBoxDroitA.getItems().addAll("OK","Interdit","Restreint");

        //par defaut
        finalLabelCommun2.setVisible(true); finalLabelCommun2.setText("Luminosité : ");

        finalChoiceBoxType.setValue("Lampe");
        finalLabelCommun.setText("Seuil de consommation : ");
        finalLabelCommun.setVisible(true);

        finalChoiceBoxDroitA.setValue("OK");

        finalSliderCommun2.setVisible(true);
        finalTextFieldCommun.setVisible(true);

        TextField finalTextFieldCommun3 = (TextField) root.getChildren().get(22);
        finalTextFieldCommun3.setVisible(false);

        Label finalLabelCommun3 = (Label) root.getChildren().get(21);
        finalLabelCommun3.setVisible(false);
        ////////////////////////////////////////

        finalChoiceBoxType.setOnAction(event -> {
            if(finalChoiceBoxType.getSelectionModel().getSelectedItem().equals("Lampe")) //Slider
            {
                finalLabelCommun.setVisible(false);
                finalLabelCommun2.setVisible(false);
                finalLabelCommun3.setVisible(false);
                finalTextFieldCommun.setVisible(true);
                finalChoiceBoxCommun2.setVisible(false);
                finalChoiceBoxCommun.setVisible(false);
                finalSliderCommun2.setVisible(false);
                finalTextFieldCommun3.setVisible(false);

                finalLabelCommun.setText("Seuil de consommation : ");
                finalLabelCommun.setVisible(true);
                finalTextFieldCommun.setVisible(true);

                finalLabelCommun2.setVisible(true); finalLabelCommun2.setText("Luminosité :");
                finalSliderCommun2.setVisible(true);

            }
            else if (finalChoiceBoxType.getSelectionModel().getSelectedItem().equals("Chauffage")) //TextField
            {
                finalLabelCommun.setVisible(false);
                finalLabelCommun2.setVisible(false);
                finalLabelCommun3.setVisible(false);
                finalTextFieldCommun.setVisible(true);
                finalChoiceBoxCommun2.setVisible(false);
                finalChoiceBoxCommun.setVisible(false);
                finalSliderCommun2.setVisible(false);
                finalTextFieldCommun3.setVisible(false);

                finalLabelCommun.setText("Temperature :");
                finalLabelCommun.setVisible(true);

                finalTextFieldCommun.setVisible(true);
            }
            else if (finalChoiceBoxType.getSelectionModel().getSelectedItem().equals("Thermostat")) //TextField
            {
                finalLabelCommun.setVisible(false);
                finalLabelCommun2.setVisible(false);
                finalLabelCommun3.setVisible(false);
                finalTextFieldCommun.setVisible(true);
                finalChoiceBoxCommun2.setVisible(false);
                finalChoiceBoxCommun.setVisible(false);
                finalSliderCommun2.setVisible(false);
                finalTextFieldCommun3.setVisible(false);

                finalLabelCommun.setText("Temperature :");
                finalLabelCommun.setVisible(true);

                finalTextFieldCommun.setVisible(true);
            }
            else if (finalChoiceBoxType.getSelectionModel().getSelectedItem().equals("Television")) //TextField
            {
                finalLabelCommun.setVisible(false);
                finalLabelCommun2.setVisible(false);
                finalLabelCommun3.setVisible(false);
                finalTextFieldCommun.setVisible(true);
                finalChoiceBoxCommun2.setVisible(false);
                finalChoiceBoxCommun.setVisible(false);
                finalSliderCommun2.setVisible(false);
                finalTextFieldCommun3.setVisible(false);

                finalLabelCommun.setText("Seuil Television");
                finalLabelCommun.setVisible(true);
                finalTextFieldCommun.setVisible(true);

                finalSliderCommun2.setVisible(true);
                finalLabelCommun2.setText("Luminosite");
                finalLabelCommun2.setVisible(true);

            }
            else if (finalChoiceBoxType.getSelectionModel().getSelectedItem().equals("Enceinte")) //Slider
            {
                finalLabelCommun.setVisible(false);
                finalLabelCommun2.setVisible(false);
                finalLabelCommun3.setVisible(false);
                finalTextFieldCommun.setVisible(true);
                finalChoiceBoxCommun2.setVisible(false);
                finalChoiceBoxCommun.setVisible(false);
                finalSliderCommun2.setVisible(false);
                finalTextFieldCommun3.setVisible(false);

                finalLabelCommun.setText("Seuil ");
                finalLabelCommun.setVisible(true);

                finalTextFieldCommun.setVisible(true);

                finalSliderCommun2.setVisible(true);
                finalLabelCommun2.setVisible(true);
                finalLabelCommun2.setText("Volume");

                finalLabelCommun3.setVisible(true);
                finalLabelCommun3.setText("Duree");
                finalTextFieldCommun3.setVisible(true);
            }
            else if (finalChoiceBoxType.getSelectionModel().getSelectedItem().equals("Climatiseur")) //TextField
            {
                finalLabelCommun.setVisible(false);
                finalLabelCommun2.setVisible(false);
                finalLabelCommun3.setVisible(false);
                finalTextFieldCommun.setVisible(true);
                finalChoiceBoxCommun2.setVisible(false);
                finalChoiceBoxCommun.setVisible(false);
                finalSliderCommun2.setVisible(false);
                finalTextFieldCommun3.setVisible(false);

                finalLabelCommun.setText("Seuil Climatiseur");
                finalLabelCommun.setVisible(true);
                finalTextFieldCommun.setVisible(true);

                finalSliderCommun2.setVisible(true);
                finalLabelCommun2.setVisible(true);
                finalLabelCommun2.setText("Temperature");
            }
            else if (finalChoiceBoxType.getSelectionModel().getSelectedItem().equals("Refrigerateur")) //TextField
            {
                finalLabelCommun.setVisible(false);
                finalLabelCommun2.setVisible(false);
                finalLabelCommun3.setVisible(false);
                finalTextFieldCommun.setVisible(false);
                finalChoiceBoxCommun2.setVisible(false);
                finalChoiceBoxCommun.setVisible(false);
                finalSliderCommun2.setVisible(false);
                finalTextFieldCommun3.setVisible(false);

                finalLabelCommun.setText("Niveau Fraicheur");
                finalLabelCommun.setVisible(true);
                finalChoiceBoxCommun.setVisible(true); finalChoiceBoxCommun.getItems().addAll("1","2","3","4","5");
            }
            else if (finalChoiceBoxType.getSelectionModel().getSelectedItem().equals("Machine_a_laver")) //TextField
            {
                finalLabelCommun.setVisible(false);
                finalLabelCommun2.setVisible(false);
                finalLabelCommun3.setVisible(false);
                finalTextFieldCommun.setVisible(true);
                finalChoiceBoxCommun2.setVisible(false);
                finalChoiceBoxCommun.setVisible(false);
                finalSliderCommun2.setVisible(false);
                finalTextFieldCommun3.setVisible(false);

                finalLabelCommun.setText("Duree de Lavage");
                finalLabelCommun.setVisible(true);
                finalTextFieldCommun.setVisible(true);

                finalChoiceBoxCommun2.setVisible(true);
                finalLabelCommun2.setVisible(true);
                finalLabelCommun2.setText("Type lavage");
                finalChoiceBoxCommun2.getItems().addAll("40 degre","60 degre","120 degre"); // A COMPLETER

            }
            else if (finalChoiceBoxType.getSelectionModel().getSelectedItem().equals("CapteurPresence")) //TextField
            {
                finalLabelCommun.setVisible(false);
                finalLabelCommun2.setVisible(false);
                finalLabelCommun3.setVisible(false);
                finalTextFieldCommun.setVisible(false);
                finalChoiceBoxCommun2.setVisible(false);
                finalChoiceBoxCommun.setVisible(false);
                finalSliderCommun2.setVisible(false);
                finalTextFieldCommun3.setVisible(false);

                finalLabelCommun.setText("Placement Utilisateur");
                finalLabelCommun.setVisible(true);

                finalChoiceBoxCommun.setVisible(true);
                finalChoiceBoxCommun.getItems().addAll("Abscent","Present");
            }
        });

        Button Confirmer = (Button) root.getChildren().get(2);
        Confirmer.setOnAction(e -> {
            boolean bool = true;
            res.clear();

            if(finalTexteFieldNom.getText().length()==0
                    ||finalTexteFieldConso.getText().length()==0
                    || finalChoiceBoxDroitA.getSelectionModel().getSelectedItem().isEmpty()
                    || finalChoiceBoxType.getSelectionModel().getSelectedItem().isEmpty()
                    || verifNom(finalTexteFieldNom.getText())
            )
            {
                finalLabelErreur.setText(" 1 Veuillez fournir correctement tous les champs indiqué SVP");
                finalLabelErreur.setVisible(true);
            }
            else
            {
                if (finalTexteFieldConso.getText().length()==0)
                {
                    finalLabelErreur.setText(" 2 Veuillez fournir correctement tous les champs indiqué SVP");
                    finalLabelErreur.setVisible(true);
                    //res.clear();
                    bool=false;
                }
                else {
                    res.add(finalChoiceBoxType.getSelectionModel().getSelectedItem()); // 0
                    res.add(finalTexteFieldNom.getText()); // 1
                    if (Float.valueOf(finalTexteFieldConso.getText()) <= 0) {
                        finalLabelErreur.setText("Veuillez fournir une consommation horaire positive SVP");
                        finalLabelErreur.setVisible(true);
                        //res.clear();
                        bool = false;
                    } else
                        res.add(finalTexteFieldConso.getText()); // 2

                    if (bool)
                    {
                        res.add(finalChoiceBoxDroitA.getSelectionModel().getSelectedItem()); // 3

                        for (Node v : root.getChildren()) {
                        if (v.isVisible()) {
                            if ((v instanceof ChoiceBox) && (v.getId().equals("ChoiceBoxCommun"))) {
                                if (finalChoiceBoxCommun.getSelectionModel().getSelectedItem() == null) {
                                    finalLabelErreur.setText("Veuillez fournir correctement tous les champs indiqué SVP");
                                    finalLabelErreur.setVisible(true);
                                    //res.clear();
                                    bool = false;
                                    break;
                                } else {
                                    //System.out.println("J'ai trouvé ChoiceBoxCommun");
                                    res.add(finalChoiceBoxCommun.getSelectionModel().getSelectedItem()); // 4-1
                                }
                            }
                            if ((v instanceof ChoiceBox) && v.getId().equals("ChoiceBoxCommun2")) {
                                if (finalChoiceBoxCommun2.getSelectionModel().getSelectedItem() == null) {
                                    finalLabelErreur.setText("Veuillez fournir correctement tous les champs indiqué SVP");
                                    finalLabelErreur.setVisible(true);
                                    //res.clear();
                                    bool = false;
                                    break;
                                } else {
                                    //System.out.println("J'ai trouvé ChoiceBoxCommun2");
                                    res.add(finalChoiceBoxCommun2.getSelectionModel().getSelectedItem()); // 5-1
                                }
                            }
                            if ((v instanceof Slider)) {
                                //System.out.println("J'ai trouvé le SLIDER");
                                res.add((float) finalSliderCommun2.getValue()); // 5-2
                            }
                            if (v instanceof TextField && v.getId().equals("TextFieldCommun"))
                            {
                                if (finalTextFieldCommun.getText().length() == 0)
                                {
                                    finalLabelErreur.setText("Veuillez fournir correctement tous les champs indiqué SVP");
                                    finalLabelErreur.setVisible(true);
                                    //res.clear();
                                    bool = false;
                                    break;
                                }
                                else {
                                    if ((finalChoiceBoxType.getSelectionModel().getSelectedItem().equals("Chauffage") && (Float.valueOf(finalTextFieldCommun.getText()) > 40 || Float.valueOf(finalTextFieldCommun.getText()) < 5))
                                            || (finalChoiceBoxType.getSelectionModel().getSelectedItem().equals("Thermostat") && (Float.valueOf(finalTextFieldCommun.getText()) > 40 || Float.valueOf(finalTextFieldCommun.getText()) < -15))
                                            )
                                    {
                                        if (finalChoiceBoxType.getSelectionModel().getSelectedItem().equals("Chauffage"))
                                            finalLabelErreur.setText("Veuillez fournir une temperature entre 5 et 40 SVP");
                                        else if (finalChoiceBoxType.getSelectionModel().getSelectedItem().equals("Thermostat"))
                                            finalLabelErreur.setText("Veuillez fournir une temperature entre -15 et 40 SVP");
                                        else if(Float.valueOf(finalTextFieldCommun.getText()) <0)
                                            finalLabelErreur.setText("Veuillez fournir un seuil positive SVP");

                                        finalLabelErreur.setVisible(true);
                                        //res.clear();
                                        bool = false;
                                        break;
                                    } else {
                                        //System.out.println("J'ai trouvé TextFieldCommun");
                                        if (!finalChoiceBoxType.getSelectionModel().getSelectedItem().equals("Enceinte")
                                                && !finalChoiceBoxType.getSelectionModel().getSelectedItem().equals("Machine_a_laver")
                                        )
                                            res.add(Float.valueOf(finalTextFieldCommun.getText())); // 4-2
                                        else {
                                            System.out.println("Je veux ajouter = "+finalTextFieldCommun.getText() );
                                            res.add(Float.valueOf(finalTextFieldCommun.getText())); // 4-2
                                        }
                                    }
                                }
                            }
                            if (v instanceof TextField && v.getId().equals("TexteFieldDernier")) {
                                if (finalTextFieldCommun3.getText().isEmpty()) {
                                    finalLabelErreur.setText("Veuillez fournir correctement tous les champs indiqué SVP");
                                    finalLabelErreur.setVisible(true);
                                    //res.clear();
                                    bool = false;
                                    break;
                                } else {
                                    //System.out.println("J'ai trouvé TexteFieldCommun3");
                                    res.add(Integer.valueOf(finalTextFieldCommun3.getText()));
                                }
                            }
                        }
                    }

                    }
                    if (bool)
                        window1.close();
                }
            }

        });

        Scene Sc = new Scene(root);
        window1.setScene(Sc);
        window1.showAndWait();

        return res;
    }

    public boolean verifNom(String nom)
    {
        boolean B = false;

        int co = 0;
       for (int i = 0; i < nom.length() ; i++)
       {
            if (nom.charAt(i) == ' ')
            {
                co++;
            }
            if (nom.charAt(i) == '\"' || nom.charAt(i) == '*' || nom.charAt(i) == '$'  || nom.charAt(i) == '!' || nom.charAt(i) == '?' || nom.charAt(i) == ';' || nom.charAt(i) == '\'' )
           {
               B = true;
               break;
           }
       }
       if (co == nom.length() || B == true )
           return true;

       return false;
    }
    /**
     * méthode de vérification de tous les champs de la configuration
     * @param anchorConfig
     * @param erreurLabel
     * @return
     */
    public boolean VerificationConfig (AnchorPane anchorConfig,Label erreurLabel)
    {
        erreurLabel.setText("");

        int i = 0;

        if (((TextField) anchorConfig.getChildren().get(1)).getLength() == 0 || (!((TextField) anchorConfig.getChildren().get(1)).getText().matches("[+]?[0-9]*\\.?[0-9]+")))
        {
            erreurLabel.setVisible(true);
            erreurLabel.setText("ERREUR : Veuillez fournir un Prix HP valide");
            ((TextField) anchorConfig.getChildren().get(1)).clear();
            i++;
        }

        if (((TextField) anchorConfig.getChildren().get(2)).getLength() == 0 || (!((TextField) anchorConfig.getChildren().get(2)).getText().matches("[+]?[0-9]*\\.?[0-9]+")))
        {
            erreurLabel.setVisible(true);
            erreurLabel.setText("ERREUR : Veuillez fournir un Prix HC valide");
            ((TextField) anchorConfig.getChildren().get(2)).clear();
            i++;
        }


        if (((TextField) anchorConfig.getChildren().get(7)).getLength() == 0 || (!((TextField) anchorConfig.getChildren().get(7)).getText().matches("[+]?[0-9]*\\.?[0-9]+")))
        {
            erreurLabel.setText("ERREUR : Veuillez fournir un seuil de consommation");
            erreurLabel.setVisible(true);
            ((TextField) anchorConfig.getChildren().get(7)).clear();
            i++;
        }

        if (((TextField) anchorConfig.getChildren().get(3)).getLength() == 0 ||(!((TextField) anchorConfig.getChildren().get(3)).getText().matches("(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)")) )
        {
            erreurLabel.setText("ERREUR : Veuillez fournir une heure début HP1 valide");
            erreurLabel.setVisible(true);
            ((TextField) anchorConfig.getChildren().get(3)).clear();
            i++;
        }

        if (((TextField) anchorConfig.getChildren().get(4)).getLength() == 0 ||(!((TextField) anchorConfig.getChildren().get(4)).getText().matches("(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)")) )
        {
            erreurLabel.setText("ERREUR : Veuillez fournir une heure de fin HP1 valide");
            erreurLabel.setVisible(true);
            ((TextField) anchorConfig.getChildren().get(4)).clear();
            i++;
        }

        if (((TextField) anchorConfig.getChildren().get(5)).getLength() == 0 ||(!((TextField) anchorConfig.getChildren().get(5)).getText().matches("(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)")) )
        {
            erreurLabel.setText("ERREUR : Veuillez fournir une heure début HP2 valide");
            erreurLabel.setVisible(true);
            ((TextField) anchorConfig.getChildren().get(5)).clear();
            i++;
        }

        if (((TextField) anchorConfig.getChildren().get(6)).getLength() == 0 ||(!((TextField) anchorConfig.getChildren().get(6)).getText().matches("(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)")) )
        {
            erreurLabel.setText("ERREUR : Veuillez fournir une heure de fin HP2 valide");
            erreurLabel.setVisible(true);
            ((TextField) anchorConfig.getChildren().get(6)).clear();
            i++;
        }

        if (i==0) {
            erreurLabel.setVisible(false);
            return true;
        }

        return false;
    }


    /**
     * Afficher la fenetre de creation d'utilisateur
     * @return
     * @throws IOException
     */
    public ArrayList<Object> afficherCreerUtilisateur() throws IOException {

        Stage window1 = new Stage();

        window1.setTitle("Ajouter une piece");
        window1.setMinWidth(600); window1.setMaxWidth(600);
        window1.setMinHeight(420); window1.setMaxHeight(420);
        window1.initStyle(StageStyle.UNDECORATED);
        window1.setResizable(false);
        window1.initModality(Modality.APPLICATION_MODAL);
        AnchorPane root = FXMLLoader.load(getClass().getResource("FXMLFiles/CreerUtilisateur.fxml"));

        root.setOnMousePressed(event -> {
            finalx= event.getSceneX();
            finaly= event.getSceneY();
        });


        root.setOnMouseDragged(event -> {
            window1.setX(event.getScreenX()-finalx);
            window1.setY(event.getScreenY()-finaly);
        });

        ImageView exit = (ImageView) root.getChildren().get(21);
        exit.setOnMouseClicked(e -> window1.close());

        ArrayList<Object> res = new ArrayList<>();

        Button v = (Button) root.getChildren().get(19);

        v.setOnAction(e -> {
            if (VerificationConfig(root,((Label) root.getChildren().get(17))))
            {
                res.add(Float.valueOf(((TextField) root.getChildren().get(1)).getText()));
                res.add(Float.valueOf(((TextField) root.getChildren().get(2)).getText()));
                res.add(Float.valueOf(((TextField) root.getChildren().get(7)).getText()));
                res.add(((TextField) root.getChildren().get(3)).getText());
                res.add(((TextField) root.getChildren().get(4)).getText());
                res.add(((TextField) root.getChildren().get(5)).getText());
                res.add(((TextField) root.getChildren().get(6)).getText());
                window1.close();
            }
        });

        Button annuler = (Button) root.getChildren().get(18);

        annuler.setOnAction(e -> {
            window1.close();
        });

        Scene Sc = new Scene(root);
        window1.setScene(Sc);
        window1.showAndWait();

        return res;

    }

}