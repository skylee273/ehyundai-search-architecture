package com.ehyundai.app;

import com.ehyundai.app.inspectedquery.model.AutoInspectionResult;

public interface QueryInspectUsecase {
    AutoInspectionResult inspect(String query, String[] options);
}
