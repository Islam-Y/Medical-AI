package com.medic.analysis.service;

import java.util.UUID;

public class AnalysisJobNotFoundException extends RuntimeException {

    public AnalysisJobNotFoundException(UUID jobId) {
        super("Analysis job not found: " + jobId);
    }
}
