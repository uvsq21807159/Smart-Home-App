package Interface;

public class HistoriqueUtilisateur {

    public String getTypeOperation() {
        return TypeOperation;
    }

    public void setTypeOperation(String typeOperation) {
        TypeOperation = typeOperation;
    }

    public String getDateHeure() {
        return DateHeure;
    }

    public void setDateHeure(String dateHeure) {
        DateHeure = dateHeure;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    private String TypeOperation;
    private String DateHeure;
    private String Description;

    @Override
    public String toString() {
        return "HistoriqueUtilisateur{" +
                "TypeOperation='" + TypeOperation + '\'' +
                ", DateHeure='" + DateHeure + '\'' +
                ", Description='" + Description + '\'' +
                '}';
    }

    public HistoriqueUtilisateur(String typeOperation, String dateHeure, String description) {
        TypeOperation = typeOperation;
        DateHeure = dateHeure;
        Description = description;
    }


}
