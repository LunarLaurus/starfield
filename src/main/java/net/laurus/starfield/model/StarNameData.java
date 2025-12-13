package net.laurus.starfield.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StarNameData {

    @JsonProperty("i")
    private int id;

    @JsonProperty("n")
    private List<String> names;

}
