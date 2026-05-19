package Models;

/**
 * Координаты объекта.
 */
public class Coordinates {

    /** Координата X. */
    private double x;

    /** Координата Y (не {@code null}). */
    private Double y;

    /**
     * Создаёт координаты.
     *
     * @param x координата X
     * @param y координата Y
     */
    public Coordinates(double x, Double y) {
        setX(x);
        setY(y);
    }

    /** @return координата X */
    public double getX() {
        return x;
    }

    /** @return координата Y */
    public Double getY() {
        return y;
    }

    /** @param x координата X */
    public void setX(double x) {
        if (Double.isNaN(x)) {
            throw new IllegalArgumentException("Coordinate x cannot be NaN");
        }
        this.x = x;
    }

    /** @param y координата Y */
    public void setY(Double y) {
        if (y == null) {
            throw new NullPointerException("Coordinate y cannot be NaN");
        }
        this.y = y;
    }

    /** @return строковое представление координат */
    @Override
    public String toString() {
        return "Coordinates [x=" + x + ", y=" + y + "]";
    }
}