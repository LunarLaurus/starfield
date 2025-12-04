package net.laurus.starmapper.model;

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

}
