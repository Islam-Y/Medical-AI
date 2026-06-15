package com.medic.analysis.service;

import com.medic.analysis.dto.AnalysisResult;

public interface AnalysisClient {

    AnalysisResult analyze(AnalysisInput input);
}
