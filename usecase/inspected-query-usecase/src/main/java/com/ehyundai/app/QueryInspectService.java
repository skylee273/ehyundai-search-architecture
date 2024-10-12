package com.ehyundai.app;

import com.ehyundai.app.inspectedquery.model.AutoInspectionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class QueryInspectService implements QueryInspectUsecase{

    private final SearchPort searchPort;

    @Override
    public AutoInspectionResult inspect(String query, String[] options) {
        return searchPort.search(query, options);
    }
}
