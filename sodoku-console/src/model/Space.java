package src.model;

public class Space {

    private Integer actual;      // valor ingresado
    private final int expected;  // valor correcto
    private final boolean fixed; // casilla fija

    public Space(final int expected, final boolean fixed) {
        this.expected = expected;
        this.fixed = fixed;
        if (fixed){
            actual = expected; // Si es fija, el valor actual ya es el correcto
        }
    }

    public Integer getActual() {
        return actual;
    }

    public void setActual(final Integer actual) {
        if (fixed) return;
        this.actual = actual;
    }

    public void clearSpace(){
        setActual(null);
    }

    public int getExpected() {
        return expected;
    }

    public boolean isFixed() {
        return fixed;
    }
}