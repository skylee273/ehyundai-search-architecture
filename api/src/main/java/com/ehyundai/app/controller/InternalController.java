package com.ehyundai.app.controller;

import com.ehyundai.app.QueryInspectUsecase;
import com.ehyundai.app.inspectedquery.model.AutoInspectionResult;
import com.ehyundai.app.inspectedquery.model.QuerySampleData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/internal")
public class InternalController {

    private final QueryInspectUsecase queryInspectUsecase;

    @GetMapping
    AutoInspectionResult text(
            @RequestParam("query") String query
    ) {
        QuerySampleData querySampleData = new QuerySampleData();
        String[] keyword = querySampleData.getKeyword();
        AutoInspectionResult inspect = queryInspectUsecase.inspect(query, keyword);

        log.info("inspect ={}", inspect);

        if(inspect.getScore() < 89) {
            inspect.setResult(query);
            inspect.setExplanation("의미가 분명하지 않아 기존 질문 그대로 반환 합니다.");
        }
        return inspect;
    }


}
