package net.laurus.starmapper.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StarColour {

    public static final StarColour WHITE = StarColour
            .builder()
            .blue(240)
            .red(240)
            .green(240)
            .build();

    @JsonProperty("r")
    private double red;

    @JsonProperty("g")
    private double green;

    @JsonProperty("b")
    private double blue;

}
