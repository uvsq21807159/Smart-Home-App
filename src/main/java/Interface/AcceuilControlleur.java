package Interface;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import CalculOptimisation.Optimisation;
import GestionnaireDonnees.BDDSingleton;
import GestionnaireDonnees.Donnee;
import ObjetConnecte.*;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;


public class AcceuilControlleur implements Initializable{

	@FXML
	ImageView exit;

	@FXML
	AnchorPane rootMenuPane;
	
	@FXML
	Pane parent;
	
	@FXML
	StackPane rootStack;

	@FXML
	ImageView notifDispo;

	AnchorPane PaneAcceuil = null;
	AnchorPane PaneCalcul = null;
	AnchorPane PaneNotifications = null;
	AnchorPane PaneUtilisateur = null;


	private Label label;


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(
					"FXMLFiles/Notifications.fxml"));
			PaneNotifications = loader.load();
			Optimisation.loaderDeNotif = loader;
			//PaneNotifications = FXMLLoader.load(getClass().getResource("FXMLFiles/Notifications.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		rootStack.getChildren().add(PaneNotifications);

		FXMLLoader loader = new FXMLLoader(getClass().getResource(
				"FXMLFiles/Acceuil.fxml"));
		try {
			PaneAcceuil = loader.load();
			ObjetConnecte.loaderAcceuil =loader;

			if (GestionObjetControlleur.recevoirObjets!=null && !GestionObjetControlleur.recevoirObjets.isEmpty()) {
				Optimisation op = Optimisation.getInstance();
				op.optimiserConsommationGlobale();
			}

		} catch (IOException | SQLException | InterruptedException e) {
			e.printStackTrace();
		}
		rootStack.getChildren().add(PaneAcceuil);

		try {
			PaneCalcul = FXMLLoader.load(getClass().getResource("FXMLFiles/Calcul.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		rootStack.getChildren().add(PaneCalcul);


		try {
			PaneUtilisateur = FXMLLoader.load(getClass().getResource("FXMLFiles/Utilisateur.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		rootStack.getChildren().add(PaneUtilisateur);


		PaneCalcul.setVisible(false);
		PaneNotifications.setVisible(false);
		PaneUtilisateur.setVisible(false);


		exit.setOnMouseClicked(event -> {
			BDDSingleton.getInstance().fermerConnexion();
			System.exit(0);
		});

	}

	/**
	 * Appuyer sur le boutton Accueil afin de selectionner la scene d'acceuil
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void Acceuil(ActionEvent event) throws IOException {
		PaneAcceuil.setVisible(true);

		PaneCalcul.setVisible(false);
		PaneNotifications.setVisible(false);
		PaneUtilisateur.setVisible(false);

		if (NotificationsControlleur.alertNotif == 1)
		{
			notifDispo.setVisible(true);
		}

	}


	/**
	 * Appuyer sur le boutton Calcul afin de selectionner la scene de Calculs
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void Calcul(ActionEvent event) throws IOException {

		PaneCalcul.setVisible(true);


		TabPane tp=  (TabPane) PaneCalcul.getChildren().get(0);
		Tab t = (Tab) tp.getTabs().get(1);
		TableView tv = (TableView) t.getContent();

		tv.getItems().clear();

		Tab t2 = (Tab) tp.getTabs().get(2);

		LineChart lc = (LineChart) t2.getContent();

		for (int i = 0; i < lc.getData().size(); i++) {
			((XYChart.Series) lc.getData().get(i)).getData().clear();
		}
		lc.getData().clear();




		PaneAcceuil.setVisible(false);
		PaneNotifications.setVisible(false);
		PaneUtilisateur.setVisible(false);

		if (NotificationsControlleur.alertNotif == 1)
		{
			notifDispo.setVisible(true);
		}
	}


	/**
	 * Appuyer sur le boutton Notifications afin de selectionner la scene de notifications
	 * @param event
	 */
	@FXML
	private void Notifications(ActionEvent event)
	{
		PaneNotifications.setVisible(true);

		PaneAcceuil.setVisible(false);
		PaneCalcul.setVisible(false);
		PaneUtilisateur.setVisible(false);


		if (NotificationsControlleur.alertNotif == 1)
		{
			notifDispo.setVisible(false);
			NotificationsControlleur.alertNotif=0;
		}

	}


	/**
	 * Appuyer sur le boutton utilisateur afin de selectionner la scene d'utilisateurs
	 * @param event
	 */
	@FXML
	private void Utilisateur(ActionEvent event) {
		PaneUtilisateur.setVisible(true);

		PaneAcceuil.setVisible(false);
		PaneCalcul.setVisible(false);
		PaneNotifications.setVisible(false);

		if (NotificationsControlleur.alertNotif == 1)
		{
			notifDispo.setVisible(true);
		}
	}

	/**
	 * Bouton importer un fichier
	 * @throws SQLException
	 * @throws IOException
	 */
	@FXML
	private void Importer() throws SQLException, IOException {

		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.sql)", "*.sql");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(null);
		if (file != null) {
			this.label = new Label();
			showConfirmation(file.getPath());
		}
		if (NotificationsControlleur.alertNotif == 1)
		{
			notifDispo.setVisible(true);
		}
	}

	private void showConfirmation(String path) throws IOException, SQLException {

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Importer configuration");
		alert.setHeaderText("Etes vous sur d'importer cette configuration" +
				" cela engendrera une deconnexion de votre environnement Smart Home ?");
		alert.setContentText(path);

		// option != null.
		Optional<ButtonType> option = alert.showAndWait();

		if (option.get() == null) {
			this.label.setText("Aucune selection !");
		} else if (option.get() == ButtonType.OK) {
			this.label.setText("Fichier importe !");
			Donnee data = new Donnee("Importation de fichier","L'utilisateur" +
					" a importe une nouvelle configuration");
			BDDSingleton bdd = BDDSingleton.getInstance();
			bdd.importerFichierSql(path);
			if ( GestionObjetControlleur.recevoirObjets != null) {

				GestionObjetControlleur.recevoirObjets.clear();
				GestionObjetControlleur.listePieces.clear();
			}
			UtilisateursControlleur.donneesStockes.clear();
			AnchorPane pane = FXMLLoader.load(getClass().getResource("FXMLFiles/Identification.fxml"));
			parent.getChildren().setAll(pane);
		} else if (option.get() == ButtonType.CANCEL) {
			this.label.setText("Action annulee !");
		} else {
			this.label.setText("-");
		}
	}

	/**
	 * Appuyer sur le boutton Deconnexion afin d'afficher la scene d'identification
	 * @param event
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@FXML
	private void Deconnexion(ActionEvent event) throws IOException, InterruptedException, SQLException {


		if (((JFXToggleButton) PaneAcceuil.getChildren().get(8)).isSelected())
		{
			GestionObjetControlleur.setStopAutoObj();
			GestionObjetControlleur.setStopAutoGlobale();
		}

		if ( GestionObjetControlleur.recevoirObjets != null) {
			GestionObjetControlleur.recevoirObjets.clear();
			GestionObjetControlleur.listePieces.clear();
			Routeur r = Routeur.getInstance();
			r.libererRouteur();
		}


		Optimisation op = Optimisation.getInstance();
		op.setInstance();

		UtilisateursControlleur.donneesStockes.clear();
		AnchorPane pane = FXMLLoader.load(getClass().getResource("FXMLFiles/Identification.fxml"));
		parent.getChildren().setAll(pane);

	}


	/**
	 * Bouton Sauvegarder
	 * @throws SQLException
	 */
	@FXML
	private void Sauvegarder() throws SQLException {


		String s1 = "Sauvegarde";
		String s2 = "Resultat";
		String s3 = "Sauvegarde manuelle reussie";

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(s1);
		alert.setHeaderText(s2);
		alert.setContentText(s3);

		alert.showAndWait();

		BDDSingleton bdd =	BDDSingleton.getInstance();
		bdd.sauvegarderTout();
		alert.close();

		Donnee DATA = new Donnee("Sauvegarde","l'utilisateur a applique une sauvegarde manuelle");
	}
}