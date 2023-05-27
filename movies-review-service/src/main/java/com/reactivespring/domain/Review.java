package com.reactivespring.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Review {

    @Id
    private String reviewId;
    private Long movieInfoId;
    private String comment;
    //@Min(value = 0L, message = "rating.negative : please pass a non-negative value")
    private Double rating;
}
