package net.laurus.starmapper.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class StarColour {

    @JsonProperty("r")
    private double red;

    @JsonProperty("g")
    private double green;

    @JsonProperty("b")
    private double blue;

}
