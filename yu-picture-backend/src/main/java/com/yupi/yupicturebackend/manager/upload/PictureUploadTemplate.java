package com.yupi.yupicturebackend.manager.upload;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.yupi.yupicturebackend.config.CosClientConfig;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.manager.CosManager;
import com.yupi.yupicturebackend.model.dto.file.UploadPictureResult;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

/**
 * 图片上传模板
 */
@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    /**
     * 上传图片
     */
    public UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        // 1. 校验图片
        validPicture(inputSource);
        // 2. 图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = getOriginFilename(inputSource);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);
        File file = null;
        try {
            // 3. 创建临时文件
            file = File.createTempFile(uploadPath, null);
            processFile(inputSource, file);

            // 4. 用 Java ImageIO 读取图片信息
            BufferedImage bufferedImage = ImageIO.read(file);
            int picWidth = 0;
            int picHeight = 0;
            String picFormat = FileUtil.getSuffix(originalFilename);
            String picColor = null;

            if (bufferedImage != null) {
                picWidth = bufferedImage.getWidth();
                picHeight = bufferedImage.getHeight();
                // 获取主色调
                picColor = getMainColor(bufferedImage);
            }

            // 5. 上传到 MinIO
            cosManager.putPictureObject(uploadPath, file);

            // 6. 封装返回结果
            double picScale = picHeight > 0 ? NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue() : 0;
            UploadPictureResult result = new UploadPictureResult();
            result.setUrl(cosClientConfig.getHost() + uploadPath);
            result.setThumbnailUrl(cosClientConfig.getHost() + uploadPath); // 缩略图 = 原图
            result.setPicName(FileUtil.mainName(originalFilename));
            result.setPicSize(FileUtil.size(file));
            result.setPicWidth(picWidth);
            result.setPicHeight(picHeight);
            result.setPicScale(picScale);
            result.setPicFormat(picFormat);
            result.setPicColor(picColor);
            return result;
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to upload image to object storage");
        } finally {
            this.deleteTempFile(file);
        }
    }

    /**
     * 获取图片主色调
     */
    private String getMainColor(BufferedImage image) {
        try {
            // 缩小图片采样以提高性能
            int sampleWidth = Math.min(image.getWidth(), 50);
            int sampleHeight = Math.min(image.getHeight(), 50);
            BufferedImage sample = new BufferedImage(sampleWidth, sampleHeight, BufferedImage.TYPE_INT_RGB);
            sample.getGraphics().drawImage(image, 0, 0, sampleWidth, sampleHeight, null);

            long totalR = 0, totalG = 0, totalB = 0;
            int count = sampleWidth * sampleHeight;
            for (int x = 0; x < sampleWidth; x++) {
                for (int y = 0; y < sampleHeight; y++) {
                    Color color = new Color(sample.getRGB(x, y));
                    totalR += color.getRed();
                    totalG += color.getGreen();
                    totalB += color.getBlue();
                }
            }
            int avgR = (int) (totalR / count);
            int avgG = (int) (totalG / count);
            int avgB = (int) (totalB / count);
            return String.format("0x%02x%02x%02x", avgR, avgG, avgB);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 校验输入源
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 获取输入源的原始文件名
     */
    protected abstract String getOriginFilename(Object inputSource);

    /**
     * 处理输入源并生成本地临时文件
     */
    protected abstract void processFile(Object inputSource, File file) throws Exception;

    /**
     * 清理临时文件
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }
}
