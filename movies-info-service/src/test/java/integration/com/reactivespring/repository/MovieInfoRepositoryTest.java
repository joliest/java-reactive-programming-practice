package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest // scans application and look for repository claases, make those classes avail in your tc. no need to instantiate the app
@ActiveProfiles("test") // IMPORTANT! if you dont provide this, it will try to create a connecrion using yml details.
// because theres ann embedded mongodb autoconfig class that will interact to embedded mongodb
class MovieInfoRepositoryTest {

    @BeforeEach
    void setup() {
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        // saves the Movie Info data above
        movieInfoRepository.saveAll(movieinfos)
                .blockLast(); // should gets completed before TC will call findAll() or else, method wont find these newly added
                // disables async for tc only
                // block last is only available in test cases
    }

    @AfterEach
    void cleanup() {
        movieInfoRepository.deleteAll().block();
    }

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Test
    void findAll() {
        var moviesInfoFlux = movieInfoRepository.findAll().log();
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }
}