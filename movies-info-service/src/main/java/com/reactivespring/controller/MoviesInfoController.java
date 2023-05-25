package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/v1")
public class MoviesInfoController {

    private MovieInfoService movieInfoService;

    // you can use autowiring, Dilip recommends this
    public MoviesInfoController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@Valid @RequestBody MovieInfo movieInfo) {
        return movieInfoService.addMovieInfo(movieInfo).log();
    }

    @GetMapping("/movieinfos")
    public Flux<MovieInfo> getAllMovieInfos(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "name", required = false) String name
            ) {
        if (year != null) {
            log.info("Year is: {} ", year);
            return movieInfoService.getMovieInfoByYear(year);
        }
        if (name != null) {
            log.info("Name is: {} ", name);
            return movieInfoService.getMovieInfoByName(name);
        }

        return movieInfoService.getAllMovieInfos().log();
    }

    @GetMapping("/movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable String id) {
        return movieInfoService.getMovieInfoById(id)
                .map(movieInfo -> ResponseEntity.ok().body(movieInfo))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }
    @PutMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    /**
     * Convert Mono<MovieInfo> to Mono<ResponseEntity<MovieInfo>> and allow us to manipulate the response status
     */
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@RequestBody MovieInfo updateMovieInfo, @PathVariable String id) {
        return movieInfoService.updateMovieInfo(updateMovieInfo, id)
                .map(movieInfo -> ResponseEntity.ok().body(movieInfo))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build())) // if movieInfo == null or empty
                .log();
    }
    @DeleteMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String id) {
        return movieInfoService.deleteMovieInfo(id).log();
    }
}
