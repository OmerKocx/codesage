package com.omerkoc.main.controller;

import com.omerkoc.main.model.Analysis;
import org.springframework.http.ResponseEntity;

public interface IAnalysisController {

    ResponseEntity<Analysis> getById(Long id);

}
