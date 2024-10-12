package com.ehyundai.app.chatgpt;

import com.ehyundai.app.CustomObjectMapper;
import com.ehyundai.app.SearchPort;
import com.ehyundai.app.inspectedquery.model.AutoInspectionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchGptAdapter implements SearchPort {

    private final ChatGptClient chatGptClient;

    private final CustomObjectMapper objectMapper = new CustomObjectMapper();

    @Override
    public AutoInspectionResult search(String query, String[] options) {

        String contentString = buildContentString(query, options);
        log.info(contentString);
        ChatGptClient.ChatPolicy chatPolicy = new ChatGptClient.ChatPolicy(
                AutoInspectionPolicy.INSPECTION_INSTRUCTION,
                AutoInspectionPolicy.EXAMPLE_CONTENT,
                AutoInspectionPolicy.EXAMPLE_INSPECTION_RESULT
        );
        try {
            String resultString = chatGptClient.getResultForContentWithPolicy(
                    contentString,
                    chatPolicy
            );
            return objectMapper.readValue(resultString, AutoInspectionResult.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildContentString(String query, String[] options) {
        return String.format(
                "Questions: [%s] Answers: [%s]",
                query,
                String.join(", ", Arrays.stream(options).map(o -> "'" + o + "'").toArray(String[]::new))
        );
    }



    static class AutoInspectionPolicy {
        private static final String INSPECTION_INSTRUCTION =
                "You are a customer service representative at a department store information desk with 20 years of experience.\n" +
                "The task is to return the result item in JSON format. The information I will provide will be in the following format:\n" +
                        "\n" +
                        "yaml\n" +
                        "코드 복사\n" +
                        "Questions: [Question] Answers: [List of answers]\n" +
                        "To complete this task, please follow these steps:\n" +
                        "\n" +
                        "Correct any typos in both the question and answer lists if needed.\n" +
                        "Evaluate each answer's relevance to the question and assign a score between 0 and 100 based on relevance.\n" +
                        "Generate an explanation for the assigned score.\n" +
                        "Extract the most suitable answer for each question and populate the result field with it.\n" +
                        "Finally, if the question includes any brand information, extract only the brand name and populate it in the result field.";
        private static final String EXAMPLE_CONTENT =
                "Questions: ['Do they sell interior decor items?']" +
                        "Answers: ['Closed days', 'Operating hours', 'Start time', 'Chanel', 'The Hyundai Seoul', 'Trade Center', 'Branch information']";
        private static final String EXAMPLE_INSPECTION_RESULT =
                "{\n" +
                        "  \"result\": \"영업시간\",\n" +
                        "  \"score\": 100,\n" +
                        "  \"explanation\": \"질문 '백화점 언제 닫아?'는 백화점의 영업 종료 시간을 묻고 있습니다. 이에 가장 관련성이 높은 답변인 '영업시간'은 고객이 백화점의 개장 및 폐점 시간을 알 수 있도록 제공하는 정보를 포함하고 있습니다. 반면 '휴점일'은 특정 일자가 문을 닫는 날을 의미하지만, 질문에서는 특정 날을 물어보지 않고 일반적인 시간을 물었으므로, '영업시간'이 가장 적합한 답변입니다.\"\n" +
                        "}";

    }
}
