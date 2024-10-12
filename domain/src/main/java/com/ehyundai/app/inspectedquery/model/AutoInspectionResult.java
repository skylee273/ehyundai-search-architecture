package com.ehyundai.app.inspectedquery.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AutoInspectionResult {
    private String result;
    private int score;
    private String explanation;
}