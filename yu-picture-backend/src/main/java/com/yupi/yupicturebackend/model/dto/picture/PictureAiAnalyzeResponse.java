package com.yupi.yupicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * AI 分析图片响应
 */
@Data
public class PictureAiAnalyzeResponse implements Serializable {

    /**
     * AI 生成的标签列表
     */
    private List<String> tags;

    /**
     * AI 生成的图片描述
     */
    private String description;

    /**
     * AI 生成的图片分类
     */
    private String category;

    private static final long serialVersionUID = 1L;
}
