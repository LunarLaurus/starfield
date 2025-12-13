package net.laurus.starfield.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Star {

    @JsonProperty("i")
    private int id;

    @JsonProperty("n")
    private String name;

    @JsonProperty("x")
    private double x;

    @JsonProperty("y")
    private double y;

    @JsonProperty("z")
    private double z;

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

}
