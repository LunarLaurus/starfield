package net.laurus.starfield.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Star {

    private static final double PARSEC_TO_LY = 3.26; // 1 parsec = 3.26156 light-years

    @JsonProperty("i")
    private int id;

    @JsonProperty("n")
    private String name;

    // Internal light-year values
    private int x;

    private int y;

    private int z;

    // Original parsec values
    private double xParsec;

    private double yParsec;

    private double zParsec;

    @JsonProperty("p")
    private double distanceInParsecs;

    @JsonProperty("N")
    private double magnitude;

    @JsonProperty("K")
    private StarColour colour;

    @Data
    public static final class StarColour {

        @JsonProperty("r")
        private double red;

        @JsonProperty("g")
        private double green;

        @JsonProperty("b")
        private double blue;

    }

    // Map JSON parsec fields to internal LY fields
    @JsonProperty("x")
    public void setXFromParsec(double xParsec) {
        this.xParsec = xParsec;
        this.x = (int) (xParsec * PARSEC_TO_LY);
    }

    @JsonProperty("y")
    public void setYFromParsec(double yParsec) {
        this.yParsec = yParsec;
        this.y = (int) (yParsec * PARSEC_TO_LY);
    }

    @JsonProperty("z")
    public void setZFromParsec(double zParsec) {
        this.zParsec = zParsec;
        this.z = (int) (zParsec * PARSEC_TO_LY);
    }

    /**
     * Returns the Euclidean distance to another star in light-years. If 'other' is
     * null, distance is calculated from Sol at (0,0,0). Handles null coordinates
     * safely.
     */
    public int distanceTo(Star other) {
        // Reference coordinates (Sol if other is null)
        int refX = (int) ((other != null && !Double.isNaN(other.getX())) ? other.getX() : 0.0);
        int refY = (int) ((other != null && !Double.isNaN(other.getY())) ? other.getY() : 0.0);
        int refZ = (int) ((other != null && !Double.isNaN(other.getZ())) ? other.getZ() : 0.0);

        // Current star coordinates
        int thisX = (int) (!Double.isNaN(this.getX()) ? this.getX() : 0.0);
        int thisY = (int) (!Double.isNaN(this.getY()) ? this.getY() : 0.0);
        int thisZ = (int) (!Double.isNaN(this.getZ()) ? this.getZ() : 0.0);

        int dx = thisX - refX;
        int dy = thisY - refY;
        int dz = thisZ - refZ;

        return (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

}
