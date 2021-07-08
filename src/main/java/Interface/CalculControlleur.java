package Interface;
import CalculOptimisation.*;

import CalculOptimisation.Resultat;
import GestionnaireDonnees.BDDSingleton;
import GestionnaireDonnees.Donnee;
import ObjetConnecte.Routeur;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.controlsfx.control.CheckTreeView;

public class CalculControlleur implements Initializable{
	
	@FXML ChoiceBox<String> ChoiceObjets;
	@FXML DatePicker DateDebPicker;
	@FXML DatePicker DateFinPicker;
	
	@FXML TableView<Resultats> TableauCalcul ;
		@FXML TableColumn<Resultats,String> objetCollone ;
		@FXML TableColumn<Resultats,LocalDate> jour ;
		@FXML TableColumn<Resultats,Float> prixParJour ;
		@FXML TableColumn<Resultats,Float> consoParJour ;
		
	@FXML LineChart<String,Number> Graphe;
	@FXML TextField TotalePrix;
	@FXML TextField TotaleConso;
	@FXML Label LabelErreur;

	@FXML
	AnchorPane AnchorTree;

	private CheckTreeView<String> checkTreeView;
	CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>("Génerale");

	ArrayList<Integer> listesIds = new ArrayList<Integer>();

	private ArrayList<CheckItems> liste= new ArrayList<>();

	public void CalculerClicked(ActionEvent event) throws SQLException {

		int choix = 0;


		if (!checkTreeView.getCheckModel().getCheckedItems().contains(rootItem)) {

					if (checkTreeView.getCheckModel().getCheckedItems().size() == 1)
						choix = 1;
					if (checkTreeView.getCheckModel().getCheckedItems().size() > 1)
						choix = 2;
					if (checkTreeView.getCheckModel().getCheckedItems().size() == 0)
						choix = -1;

					if (choix != -1) {
						for (int j = 0; j < checkTreeView.getCheckModel().getCheckedItems().size(); j++) {
							for (int k = 0; k < liste.size(); k++) {
								if (liste.get(k).getItem() == checkTreeView.getCheckModel().getCheckedItems().get(j)) {
									listesIds.add(liste.get(k).getId());
								}
							}
						}
					}
				}


		if (DateDebPicker.getValue()!=null && DateFinPicker.getValue()!=null) {

			if (DateDebPicker.getValue().isAfter(DateFinPicker.getValue())) {

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

				String datedeb = DateDebPicker.getValue().toString();
				String datefin = DateFinPicker.getValue().toString();

				LocalDateTime ldtdeb = convertirDateDeb(datedeb);
				LocalDateTime ldtfin = convertirDateFin(datefin);

			int[] IDS = new int[listesIds.size()];
			if (choix != -1) {
					for (int i = 0; i < listesIds.size(); i++) {
						IDS[i] = listesIds.get(i);
					}
				}

				Calcul calcul = new Calcul(ldtdeb, ldtfin);

					switch (choix) {
				case 0:
					calcul.consommationMaison();
					break;
				case 1:
					calcul.consommationObjet(listesIds.get(0), true, 0);
					break;
				case 2:
					calcul.consommationPlusieursObjets(IDS);
					break;
				case -1:
					String s1 = "Information";
					String s2 = "";
					String s3 = "Veuillez choisir un ou plusieurs objets !";

					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle(s1);
					alert.setHeaderText(s3);
					alert.setContentText(s2);

					alert.showAndWait();
					alert.close();
					break;

			}

			if (choix != -1) {
				ArrayList<Resultat> resCalcul = calcul.getResultat();
				////System.out.println(Resultat.getConsoGlobale());

				objetCollone.setCellValueFactory(new PropertyValueFactory<>("nomObjet"));
				jour.setCellValueFactory(new PropertyValueFactory<>("Jour"));
				prixParJour.setCellValueFactory(new PropertyValueFactory<>("PrixJour"));
				consoParJour.setCellValueFactory(new PropertyValueFactory<>("consoJour"));

				TableauCalcul.setItems(getResultats(resCalcul, calcul, choix));

				XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();

				DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				for (int i = 0; i < resCalcul.size(); i++) {
					series.getData().add(new XYChart.Data<String, Number>(calcul.getDateDeb().plusDays(i).format(formatter1), resCalcul.get(i).getConsoGlobaleJour()));
				}
				Graphe.getData().add(series);
				calcul.reinitialiserResultat();
				listesIds.clear();
			}

		}
		else
		{
			String s1 = "Information";
			String s2 = "";
			String s3 = "Veuillez saisir une 'DATE DEBUT' et une 'DATE FIN' pour le calcul !";

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle(s1);
			alert.setHeaderText(s3);
			alert.setContentText(s2);

			alert.showAndWait();
			alert.close();

		}


	}

	//get all of the 'Resultats'
	public ObservableList<Resultats> getResultats(ArrayList<Resultat> ar, Calcul c,int choix) throws SQLException {
		//System.out.println("deb ");

		ObservableList<Resultats> resultats = FXCollections.observableArrayList();

		Routeur r = Routeur.getInstance();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		int [] tab = ar.get(0).getIdObjets();
		//System.out.println(ar.get(0).getConsoGlobaleJour());
		for (int i = 0; i < ar.size(); i++) {
			if (choix == 0) // il a choisi une maison
			{
				resultats.add(new Resultats("Maison",c.getDateDeb().plusDays(i).format(formatter),
						ar.get(i).getPrixTotalJour(),ar.get(i).getConsoGlobaleJour()));
			}
			else if (choix == 1) { // il a choisi un objet
				resultats.add(new Resultats(r.fournirReference(tab[0]).getNom(),c.getDateDeb().plusDays(i).format(formatter),
						ar.get(i).getPrixTotalJour(), ar.get(i).getConsoGlobaleJour()));
			}
			else if (choix == 2) {// il a choisi plusieurs objets
				resultats.add(new Resultats("ensemble",c.getDateDeb().plusDays(i).format(formatter),
						ar.get(i).getPrixTotalJour(),ar.get(i).getConsoGlobaleJour()));
			}
		}

		if (choix == 0) // il a choisi une maison
		{
			Donnee data = new Donnee("Calcul","L'utilisateur a demande un calcul de consommation sur la maison");

		}
		else if (choix == 1) { // il a choisi un objet
			Donnee data = new Donnee("Calcul","L'utilisateur a demande un calcul de consommation sur un objet");

		}
		else if (choix == 2) {// il a choisi plusieurs objets
			Donnee data = new Donnee("Calcul","L'utilisateur a demande un calcul de consommation sur plusieurs objets");

		}


			TotaleConso.setText(Resultat.getConsoGlobale()+"");
			TotalePrix.setText(Resultat.getPrixTotal()+"");


		//System.out.println("fin ");


		return resultats;
	}

	public LocalDateTime convertirDateDeb(String Date)
	{
		//String[] words=Date.split("/");
		LocalDateTime date = LocalDateTime.parse(Date + "T00:00:00");
		return date;
	}

	public LocalDateTime convertirDateFin(String Date)
	{
		//String[] words=Date.split("/");
		LocalDateTime date = LocalDateTime.parse(Date + "T23:59:59");
		return date;
	}

	public void updateTree()
	{
		//System.out.println(GestionObjetControlleur.listePieces);
		rootItem.getChildren().clear();
		AnchorTree.getChildren().clear();

		CheckBoxTreeItem<String> piece;
		for (int i = 0; i < GestionObjetControlleur.listePieces.size(); i++) {
			piece = new CheckBoxTreeItem<>(GestionObjetControlleur.listePieces.get(i));
			for (int j = 0; j < GestionObjetControlleur.recevoirObjets.size(); j++) {
				if (GestionObjetControlleur.recevoirObjets.get(j).getPiece().equals(GestionObjetControlleur.listePieces.get(i))){
					CheckBoxTreeItem cbti = new CheckBoxTreeItem<>(GestionObjetControlleur.recevoirObjets.get(j).getNom() );
					piece.getChildren().add(cbti);
					liste.add(new CheckItems(GestionObjetControlleur.recevoirObjets.get(j).getId(),cbti));
				}
			}
			if (piece.getChildren().size() != 0)
				rootItem.getChildren().add(piece);
		}
		rootItem.setExpanded(true);

		checkTreeView = new CheckTreeView<String>(rootItem);
		//CheckTreeView<String> checkTreeView = new CheckTreeView<>(rootItem);

		checkTreeView.setPrefSize(676,395);

		//checkTreeView.getCheckModel().getCheckedItems().addListener(new );

		AnchorTree.getChildren().add(checkTreeView);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		if (GestionObjetControlleur.recevoirObjets!=null && !GestionObjetControlleur.recevoirObjets.isEmpty()) {
			updateTree();

			BDDSingleton bdd = BDDSingleton.getInstance();
			try {
				ResultSet rs = bdd.requeteSelection
						("SELECT date_action FROM HistoriqueObjet WHERE  " +
								"type_operation = \"changeEtat\" AND mail_utilisateur=");

				String[] str = null;
				if(rs.next()) {
					str = rs.getString("date_action").split(" ");
				}
				else{
					str = new String[1];
					str[0] = LocalDate.now().minusDays(0).toString();
				}
				DatePicker maxDate = new DatePicker();
				maxDate.setValue(LocalDate.now().minusDays(1));

				DatePicker minDate = new DatePicker();
				minDate.setValue(LocalDate.parse(str[0]));

				final Callback<DatePicker, DateCell> dayCellFactory;

				dayCellFactory = (final DatePicker datePicker) -> new DateCell() {
					@Override
					public void updateItem(LocalDate item, boolean empty) {
						super.updateItem(item, empty);
						if (item.isBefore(minDate.getValue()) || item.isAfter(maxDate.getValue())) {
							setDisable(true);
							setStyle("-fx-background-color: #ffc0cb;");
						}
					}
				};
				DateFinPicker.setDayCellFactory(dayCellFactory);
				DateDebPicker.setDayCellFactory(dayCellFactory);
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		}


		Graphe.setAnimated(false);

//
////		DatePicker myDatePicker = new DatePicker(); // This DatePicker is shown to user
//		DatePicker maxDate = new DatePicker(); // DatePicker, used to define max date available, you can also create another for minimum date
//		maxDate.setValue(LocalDate.now().minusDays(1) ); // Max date available will be 2015-01-01
//		final Callback<DatePicker, DateCell> dayCellFactory;
//
//		dayCellFactory = (final DatePicker datePicker) -> new DateCell() {
//			@Override
//			public void updateItem(LocalDate item, boolean empty) {
//				super.updateItem(item, empty);
//				if (item.isAfter(maxDate.getValue())) { //Disable all dates after required date
//					setDisable(true);
//					setStyle("-fx-background-color: #ffc0cb;"); //To set background on different color
//				}
//			}
//		};
////Finally, we just need to update our DatePicker cell factory as follow:
//		DateFinPicker.setDayCellFactory(dayCellFactory);





	}
}
