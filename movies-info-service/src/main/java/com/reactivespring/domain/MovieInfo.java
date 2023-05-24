package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor // generates all appropriate constructor
@AllArgsConstructor
@Document // representation of Entity in MongoDB
public class MovieInfo {
    @Id
    private String movieInfoId;
    @NotBlank(message =  "movieInfo.name must be present")
    private String name;
    @NotNull
    @Positive(message = "movieInfo.year must be a positive value")
    private Integer year;
    /**
     * @NotBlank will not work on the List<String>
     */
    private List<@NotBlank(message = "movieInfo.cast must be present") String> cast;
    private LocalDate release_date;

}
