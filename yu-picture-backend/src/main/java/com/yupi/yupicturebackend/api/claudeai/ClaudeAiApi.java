package com.yupi.yupicturebackend.api.claudeai;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClaudeAiApi {

    @Value("${claude.apiKey:}")
    private String apiKey;

    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";

    private static final String MODEL = "claude-sonnet-4-20250514";

    private static final String ANALYZE_PROMPT = "Please analyze this image and return a JSON object with the following fields:\n" +
            "1. \"tags\": an array of 3-5 descriptive tags in English\n" +
            "2. \"description\": a brief description of the image content in English, under 200 characters\n" +
            "3. \"category\": a single category for this image in English (e.g. Nature, People, Animals, Architecture, Food, Technology, Art, Other)\n\n" +
            "Return ONLY valid JSON, no other text. Example:\n" +
            "{\"tags\": [\"nature\", \"landscape\", \"mountain\"], \"description\": \"A beautiful snow-capped mountain landscape\", \"category\": \"Nature\"}";

    private static final String MODERATE_PROMPT = "Please check if this image contains any inappropriate, explicit, violent, " +
            "or otherwise unsafe content. Return ONLY a JSON object with:\n" +
            "1. \"safe\": boolean, true if the image is safe, false if inappropriate\n" +
            "2. \"reason\": string, empty if safe, otherwise a brief reason\n\n" +
            "Return ONLY valid JSON, no other text. Example:\n" +
            "{\"safe\": true, \"reason\": \"\"}";

    /**
     * 使用 Claude AI 分析图片（base64 方式）
     *
     * @param base64Data 图片的 base64 编码数据
     * @param mediaType  图片的 MIME 类型，如 image/jpeg, image/png
     * @return 包含 tags, description, category 的 JSON 对象
     */
    public JSONObject analyzeImage(String base64Data, String mediaType) {
        String responseBody = callClaudeApi(base64Data, mediaType, ANALYZE_PROMPT);
        return JSONUtil.parseObj(responseBody);
    }

    /**
     * 使用 Claude AI 审核图片内容（base64 方式）
     *
     * @param base64Data 图片的 base64 编码数据
     * @param mediaType  图片的 MIME 类型
     * @return 包含 safe, reason 的 JSON 对象
     */
    public JSONObject moderateImage(String base64Data, String mediaType) {
        String responseBody = callClaudeApi(base64Data, mediaType, MODERATE_PROMPT);
        return JSONUtil.parseObj(responseBody);
    }

    /**
     * 调用 Claude API
     */
    private String callClaudeApi(String base64Data, String mediaType, String prompt) {
        // 构造请求体
        JSONObject imageSource = JSONUtil.createObj()
                .set("type", "base64")
                .set("media_type", mediaType)
                .set("data", base64Data);

        JSONObject imageContent = JSONUtil.createObj()
                .set("type", "image")
                .set("source", imageSource);

        JSONObject textContent = JSONUtil.createObj()
                .set("type", "text")
                .set("text", prompt);

        JSONObject message = JSONUtil.createObj()
                .set("role", "user")
                .set("content", JSONUtil.createArray().set(imageContent).set(textContent));

        JSONObject requestBody = JSONUtil.createObj()
                .set("model", MODEL)
                .set("max_tokens", 1024)
                .set("messages", JSONUtil.createArray().set(message));

        // 发送请求
        try (HttpResponse httpResponse = HttpRequest.post(CLAUDE_API_URL)
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(requestBody))
                .timeout(60000)
                .execute()) {
            if (!httpResponse.isOk()) {
                log.error("Claude API 请求异常：{}", httpResponse.body());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "Claude AI API call failed");
            }
            // 解析响应，提取文本内容
            JSONObject responseJson = JSONUtil.parseObj(httpResponse.body());
            String textResult = responseJson.getJSONArray("content")
                    .getJSONObject(0)
                    .getStr("text");
            return textResult;
        }
    }
}
