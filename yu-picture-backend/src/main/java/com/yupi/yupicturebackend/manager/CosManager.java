package com.yupi.yupicturebackend.manager;

import com.yupi.yupicturebackend.config.CosClientConfig;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private MinioClient minioClient;

    /**
     * 确保 bucket 存在
     */
    public void ensureBucketExists() {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(cosClientConfig.getBucket()).build());
            if (!found) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(cosClientConfig.getBucket()).build());
                // 设置 bucket 为公开读
                String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + cosClientConfig.getBucket() + "/*\"]}]}";
                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(cosClientConfig.getBucket())
                                .config(policy)
                                .build());
            }
        } catch (Exception e) {
            log.error("Failed to ensure bucket exists", e);
        }
    }

    /**
     * 上传对象
     */
    public void putObject(String key, File file) {
        try {
            ensureBucketExists();
            // 去掉开头的 /
            String objectName = key.startsWith("/") ? key.substring(1) : key;
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(cosClientConfig.getBucket())
                            .object(objectName)
                            .filename(file.getAbsolutePath())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    /**
     * 上传图片（MinIO 没有图片处理，直接上传）
     */
    public void putPictureObject(String key, File file) {
        putObject(key, file);
    }

    /**
     * 下载对象
     */
    public InputStream getObject(String key) {
        try {
            String objectName = key.startsWith("/") ? key.substring(1) : key;
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(cosClientConfig.getBucket())
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from MinIO", e);
        }
    }

    /**
     * 删除对象
     */
    public void deleteObject(String key) {
        try {
            String objectName = key.startsWith("/") ? key.substring(1) : key;
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(cosClientConfig.getBucket())
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            log.error("删除 MinIO 对象失败, key = {}", key, e);
        }
    }
}
