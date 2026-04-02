package com.yupi.yupicturebackend.manager;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import com.yupi.yupicturebackend.api.claudeai.ClaudeAiApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

/**
 * Claude AI 能力管理器
 */
@Slf4j
@Component
public class ClaudeAiManager {

    @Resource
    private CosManager cosManager;

    @Resource
    private ClaudeAiApi claudeAiApi;

    /**
     * AI 分析图片，返回标签、描述、分类
     *
     * @param imageKey  MinIO 中的图片 key
     * @param mediaType 图片 MIME 类型，如 image/jpeg
     * @return 包含 tags(List<String>), description(String), category(String) 的 JSON 对象
     */
    public JSONObject analyzePicture(String imageKey, String mediaType) {
        String base64Data = downloadAndEncode(imageKey);
        if (mediaType == null || mediaType.isEmpty()) {
            mediaType = "image/jpeg";
        }
        return claudeAiApi.analyzeImage(base64Data, mediaType);
    }

    /**
     * AI 审核图片内容是否安全
     *
     * @param imageKey  MinIO 中的图片 key
     * @param mediaType 图片 MIME 类型
     * @return true 表示安全，false 表示不安全
     */
    public boolean moderatePicture(String imageKey, String mediaType) {
        String base64Data = downloadAndEncode(imageKey);
        if (mediaType == null || mediaType.isEmpty()) {
            mediaType = "image/jpeg";
        }
        JSONObject result = claudeAiApi.moderateImage(base64Data, mediaType);
        return result.getBool("safe", true);
    }

    /**
     * 从 MinIO 下载图片并转为 base64
     */
    private String downloadAndEncode(String imageKey) {
        try (InputStream inputStream = cosManager.getObject(imageKey)) {
            byte[] bytes = IoUtil.readBytes(inputStream);
            return Base64.encode(bytes);
        } catch (Exception e) {
            log.error("下载图片失败，imageKey = {}", imageKey, e);
            throw new RuntimeException("Failed to download image", e);
        }
    }
}
