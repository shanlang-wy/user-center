package com.han.usercenter.utils;

import com.han.usercenter.common.ErrorCode;
import com.han.usercenter.exception.BusinessException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件工具类
 *
 */
public class FileUtils {

    /**
     * 允许上传的图片类型
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");

    /**
     * 允许上传的图片后缀
     */
    private static final List<String> ALLOWED_IMAGE_SUFFIXES = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    /**
     * 最大头像文件大小 2MB
     */
    private static final long MAX_AVATAR_SIZE = 2 * 1024 * 1024;

    /**
     * 保存上传的文件
     *
     * @param file     上传的文件
     * @param savePath 保存目录
     * @param accessPrefix 访问路径前缀
     * @return 可访问的 URL
     */
    public static String saveFile(MultipartFile file, String savePath, String accessPrefix) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件为空");
        }
        // 校验文件大小
        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 2MB");
        }
        // 校验文件类型
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仅支持 jpg、png、gif、webp 格式的图片");
        }
        // 校验后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = getFileSuffix(originalFilename);
        if (!ALLOWED_IMAGE_SUFFIXES.contains(suffix.toLowerCase())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仅支持 jpg、png、gif、webp 格式的图片");
        }
        // 生成唯一文件名
        String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + suffix;
        // 创建目录
        File saveDir = new File(savePath).getAbsoluteFile();
        if (saveDir.exists() && !saveDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传路径不是目录");
        }
        if (!saveDir.exists() && !saveDir.mkdirs()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建上传目录失败");
        }
        // 保存文件
        File destFile = new File(saveDir, newFileName).getAbsoluteFile();
        try {
            file.transferTo(destFile);
        } catch (IOException | IllegalStateException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件保存失败");
        }
        return normalizeAccessPrefix(accessPrefix) + newFileName;
    }

    /**
     * 获取文件后缀
     *
     * @param filename
     * @return
     */
    private static String getFileSuffix(String filename) {
        if (!StringUtils.hasText(filename)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件名非法");
        }
        int index = filename.lastIndexOf(".");
        if (index == -1 || index == filename.length() - 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件名缺少后缀");
        }
        return filename.substring(index + 1);
    }

    private static String normalizeAccessPrefix(String accessPrefix) {
        String prefix = StringUtils.hasText(accessPrefix) ? accessPrefix : "/";
        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        return prefix;
    }
}

