package src.model;

public enum GameStatus {

    NON_STARTED("no iniciado"),
    INCOMPLETE("incompleto"),
    COMPLETE("completo");

    private String label;

    GameStatus(final String label){
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}