package com.ehyundai.app;

import com.ehyundai.app.inspectedquery.model.AutoInspectionResult;

public interface SearchPort {

    AutoInspectionResult search(String query, String[] options);
}
