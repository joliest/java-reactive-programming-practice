package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

// ReactiveMongoRepository - this will enable the reactive nature of mongo db
public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {
}
