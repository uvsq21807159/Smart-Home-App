package Interface;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import CalculOptimisation.Optimisation;
import GestionnaireDonnees.BDDSingleton;
import GestionnaireDonnees.Donnee;
import ObjetConnecte.ObjetConnecte;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXML;

public class IdentificationControlleur implements Initializable {

	@FXML
	AnchorPane parent;
	@FXML
	ImageView exit;

	@FXML
	TextField mailConnexion;
	@FXML
	PasswordField mdpConnexion;
	@FXML
	TextField Nom;
	@FXML
	TextField Prenom;
	@FXML
	TextField mailCreation;
	@FXML
	PasswordField mdpCreation;

	@FXML
	Button SeConnecter;
	@FXML
	Button CreerCompte;

	@FXML
	Label LabelErreur;
	@FXML
	Label labelErreurCreer;





	public void connexion(String mail, String mdp) throws IOException, SQLException {

		String mailNew = "\"" + mail + "\"";
		String mdpNew = "\"" + mdp + "\"";
		BDDSingleton bdd = BDDSingleton.getInstance();

		if (bdd.verifierIdentifiants(mailNew, mdpNew)) {
			String sql = "select * from Configuration_SmartHome where mail_utilisateur =";
			String sql2 = "select nom , prenom from Utilisateur where mail_utilisateur =";

			ResultSet rs = bdd.decisionRequete(sql);

			while (rs.next()) {
				UtilisateursControlleur.donneesStockes.add(rs.getFloat("PrixHP"));
				UtilisateursControlleur.donneesStockes.add(rs.getFloat("PrixHC"));
				UtilisateursControlleur.donneesStockes.add(rs.getFloat("seuil_maison"));
				UtilisateursControlleur.donneesStockes.add(rs.getString("Debut_heure_hp1"));
				UtilisateursControlleur.donneesStockes.add(rs.getString("Fin_heure_hp1"));
				UtilisateursControlleur.donneesStockes.add(rs.getString("Debut_heure_hp2"));
				UtilisateursControlleur.donneesStockes.add(rs.getString("Fin_heure_hp2"));
			}

			ResultSet rs2 = bdd.decisionRequete(sql2);

			while (rs2.next()){
				UtilisateursControlleur.donneesStockes.add(rs2.getString("prenom"));
				UtilisateursControlleur.donneesStockes.add(rs2.getString("nom"));
			}


			ArrayList<ObjetConnecte> listeObjs = bdd.genererObjetsSmartHome();
			GestionObjetControlleur.recevoirObjets = listeObjs;
			AnchorPane pane = FXMLLoader.load(getClass().getResource("FXMLFiles/GestionObjet.fxml"));
			parent.getChildren().setAll(pane);


		} else {
			LabelErreur.setText("identifiants incorrects");
			LabelErreur.setVisible(true);
		}
	}

	public void IdentificationClicked(ActionEvent event) throws IOException, SQLException {
		connexion(mailConnexion.getText(), mdpConnexion.getText());
	}

	public void CreationClicked(ActionEvent event) throws IOException, SQLException {
		if (Nom.getText().equals("")
				||Prenom.getText().equals("")
				||Prenom.getText().equals("")
				||mailCreation.getText().equals("")
				||mdpCreation.getText().equals("")
		)
		{
			labelErreurCreer.setText("Veuillez saisir tous les champs");
			return;
		}

		if (!mailCreation.getText().matches("^(.+)@(\\S+)$"))
		{
			labelErreurCreer.setText("Veuillez saisir une adresse mail valide");
			return;
		}

		FenetresSecondaires fenetre = new FenetresSecondaires();
		ArrayList<Object> res = fenetre.afficherCreerUtilisateur();
		//System.out.println(res);

		if (res.isEmpty())
			return;

		String nom = "\"" + Nom.getText() + "\"";
		String prenom = "\"" + Prenom.getText() + "\"";
		String mail = "\"" + mailCreation.getText() + "\"";
		String mdp = "\"" + mdpCreation.getText() + "\"";

		float d1 = (float) res.get(0);
		float d2 = (float) res.get(1);
		float d3 = (float) res.get(2);
		String res3 = "\"" + res.get(3) + "\"";
		String res4 = "\"" + res.get(4) + "\"";
		String res5 = "\"" + res.get(5) + "\"";
		String res6 = "\"" + res.get(6) + "\"";

		UtilisateursControlleur.donneesStockes.add(res.get(0));
		UtilisateursControlleur.donneesStockes.add(res.get(1));
		UtilisateursControlleur.donneesStockes.add(res.get(2));
		UtilisateursControlleur.donneesStockes.add(res.get(3));
		UtilisateursControlleur.donneesStockes.add(res.get(4));
		UtilisateursControlleur.donneesStockes.add(res.get(5));
		UtilisateursControlleur.donneesStockes.add(res.get(6));
		UtilisateursControlleur.donneesStockes.add(Prenom.getText());
		UtilisateursControlleur.donneesStockes.add(Nom.getText());

		String user = "insert into Utilisateur values(" + mail + "," + nom + "," + prenom + "," + mdp + ",\"paris\");";

		String config1 = "insert into Configuration_SmartHome values( " + "," + d1 + "," + d2 + "," + d3 + "," + res3 + "," + res4 + "," + res5 + "," + res6 + ");";

		BDDSingleton base = BDDSingleton.getInstance();

		boolean verifExistence = base.verifierIdentifiants(mail, mdp);

		if (verifExistence) {
			//System.out.println("utilisateur deja existant");
			labelErreurCreer.setText("Utilisateur deja  existant");
			labelErreurCreer.setVisible(true);
		} else {
			base.requeteInsertion(user);
			base.verifierIdentifiants(mail, mdp);
			base.requeteInsertion(config1);
			base.setPrixHC_PrixHP();

			AnchorPane pane = FXMLLoader.load(getClass().getResource("FXMLFiles/GestionObjet.fxml"));
			parent.getChildren().setAll(pane);

			Donnee donne = new Donnee("Creation de Compte","L'utilisateur a cree son compte");

		}
}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		exit.setOnMouseClicked(event -> {
			System.exit(0);
		});

		mailConnexion.setText("leila.kloul@uvsq.fr");
		mdpConnexion.setText("123k");

		mailConnexion.setFocusTraversable(false);
		mdpConnexion.setFocusTraversable(false);
		Nom.setFocusTraversable(false);
		Prenom.setFocusTraversable(false);
		mailCreation.setFocusTraversable(false);
		mdpCreation.setFocusTraversable(false);
		SeConnecter.setFocusTraversable(false);
		CreerCompte.setFocusTraversable(false);

	}
	
}



