package Interface;

import ObjetConnecte.Routeur;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

public class NotificationsControlleur implements Initializable {

    @FXML VBox NotifVBOX;

    static int idNotif=0;

    public static HashMap<Integer,String> lesNotifs;

    public static int alertNotif = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void AjouterNotification(String Notif,int type, int id)
    {
        alertNotif = 1;

        if (type == 1) {

            HBox temp = new HBox();

            temp.setPrefHeight(100);
            temp.setSpacing(10);
            temp.setId(""+idNotif);
            idNotif++;

            Label l = new Label(Notif);
            l.setPrefWidth(500);

            l.setPrefHeight(60);
            l.setWrapText(true);

            Button Oui = new Button("Oui");
            Oui.setStyle("-fx-background-color: #7fffd4");
            Oui.setPrefSize(60,60);

            Button Non = new Button("Non");
            Non.setStyle("-fx-background-color: #cd5c5c");
            Non.setPrefSize(60,60);

            Oui.setOnAction(e -> {

                Routeur routeur = Routeur.getInstance();
                try {
                    if (routeur.fournirReference(id).getEtat()==1) {
                        routeur.fournirReference(id).changeEtat();
                    }
                    HBox hb = (HBox) Non.getParent();
                    NotifVBOX.getChildren().remove(hb);
                    //System.out.println(routeur.fournirReference(id).getEtat());
                } catch (SQLException | InterruptedException | IOException throwables) {
                    throwables.printStackTrace();
                }
            });

            Non.setOnAction(e -> {
                    NotifVBOX.getChildren().remove((HBox) Non.getParent());
            });


            temp.setMargin(l, new Insets(40, 0, 0, 0));
            temp.setMargin(Oui, new Insets(30, 0, 0, 0));
            temp.setMargin(Non, new Insets(30, 0, 0, 0));

            temp.getChildren().addAll(l, Oui, Non);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    NotifVBOX.getChildren().addAll(temp);
                }
            });

            temp.setStyle("-fx-background-color: #808080");
        }
        else {
            HBox temp = new HBox();

            temp.setPrefHeight(100);
            temp.setSpacing(10);
            temp.setId(""+idNotif);
            idNotif++;

            Label l = new Label(Notif);
            l.setPrefWidth(500);
//            l.setPrefHeight(60);
//            l.setWrapText(true);




            Button OK = new Button("OK");
            OK.setStyle("-fx-background-color: #7fffd4");
            OK.setPrefSize(60,60);

            temp.setMargin(l, new Insets(50, 0, 0, 0));
            temp.setMargin(OK, new Insets(30, 0, 0, 0));

            OK.setOnAction(e -> {
                NotifVBOX.getChildren().remove((HBox) OK.getParent());
            });

            temp.getChildren().addAll(l,OK);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    NotifVBOX.getChildren().addAll(temp);
                }
            });
            temp.setStyle("-fx-background-color: #dcdcdc");


        }
    }
}


