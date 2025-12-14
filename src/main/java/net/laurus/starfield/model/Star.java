package net.laurus.starfield.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Star {

    private static final double PARSEC_TO_LY = 3.26156;

    @JsonProperty("i")
    private Integer id;

    @JsonProperty("n")
    private String name;

    // Internal coordinates (light-years)
    private Double x;

    private Double y;

    private Double z;

    // Original parsec values
    private Double xParsec;

    private Double yParsec;

    private Double zParsec;

    @JsonProperty("p")
    private Double distanceInParsecs;

    @JsonProperty("N")
    private Double magnitude;

    @JsonProperty("K")
    private StarColour colour;

    @Data
    public static final class StarColour {

        @JsonProperty("r")
        private Double red;

        @JsonProperty("g")
        private Double green;

        @JsonProperty("b")
        private Double blue;

    }

    /*
     * ---------------------------- JSON → internal conversion
     * ----------------------------
     */

    @JsonProperty("x")
    public void setXFromParsec(Double xParsec) {
        this.xParsec = xParsec;
        this.x = convertParsecToLy(xParsec);
    }

    @JsonProperty("y")
    public void setYFromParsec(Double yParsec) {
        this.yParsec = yParsec;
        this.y = convertParsecToLy(yParsec);
    }

    @JsonProperty("z")
    public void setZFromParsec(Double zParsec) {
        this.zParsec = zParsec;
        this.z = convertParsecToLy(zParsec);
    }

    private static Double convertParsecToLy(Double parsec) {
        return (parsec == null) ? null : parsec * PARSEC_TO_LY;
    }

    /*
     * ---------------------------- Validation helpers ----------------------------
     */

    @JsonIgnore
    public boolean hasValidPosition() {
        return x != null && y != null && z != null;
    }

    /*
     * ---------------------------- Distance calculation
     * ----------------------------
     */

    /**
     * Returns the Euclidean distance to another star in light-years. If
     * {@code other} is null, distance is calculated from Sol (0,0,0). Returns
     * {@link Double#NaN} if this star has no valid position.
     */
    public double distanceTo(Star other) {

        if (!hasValidPosition()) {
            return Double.NaN;
        }

        double refX = 0.0;
        double refY = 0.0;
        double refZ = 0.0;

        if (other != null && other.hasValidPosition()) {
            refX = other.x;
            refY = other.y;
            refZ = other.z;
        }

        double dx = x - refX;
        double dy = y - refY;
        double dz = z - refZ;

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

}
