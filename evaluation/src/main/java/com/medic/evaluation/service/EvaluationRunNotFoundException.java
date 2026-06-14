package com.medic.evaluation.service;

import java.util.UUID;

public class EvaluationRunNotFoundException extends RuntimeException {

    public EvaluationRunNotFoundException(UUID id) {
        super("Evaluation run not found: " + id);
    }
}
