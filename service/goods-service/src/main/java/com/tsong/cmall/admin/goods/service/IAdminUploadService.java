package com.tsong.cmall.admin.goods.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/9/12 23:07
 */
public interface IAdminUploadService {
    String uploadFile(MultipartFile file, Long adminId);

    List<String> uploadFiles(List<MultipartFile> multipartFiles, Long adminId);

    void deleteFiles(String[] urls, Long adminId);
}
