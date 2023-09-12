package com.tsong.cmall.admin.goods.service.impl;

import com.tsong.cmall.admin.goods.service.IAdminUploadService;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @Author Tsong
 * @Date 2023/9/12 23:07
 */
@Service
public class AdminUploadService implements IAdminUploadService {
    public final static String FILE_UPLOAD_DIC; // 上传文件的默认url前缀，根据部署设置自行修改

    static {
        try {
            FILE_UPLOAD_DIC = ResourceUtils.getURL("classpath:").getPath() + "static/upload/";
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file, Long adminId) {
        String newFileName = getNewFileName(file, adminId);
        if (newFileName == null) return null;
        return "/image/" + adminId + "/" + newFileName;
    }

    @Override
    public List<String> uploadFiles(List<MultipartFile> multipartFiles, Long adminId) {
        List<String> fileNames = new ArrayList(multipartFiles.size());
        for (MultipartFile file : multipartFiles) {
            String newFileName = getNewFileName(file, adminId);
            if (newFileName == null) return null;
            fileNames.add("/image/" + adminId + "/" + newFileName);
        }
        return fileNames;
    }

    private static String getNewFileName(MultipartFile file, Long adminId) {
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //生成文件名称通用方法
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        StringBuilder tempName = new StringBuilder();
        tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
        String newFileName = tempName.toString();
        String dirPath = FILE_UPLOAD_DIC + adminId + "/";
        File fileDirectory = new File(dirPath);
        //创建文件
        File destFile = new File(dirPath + newFileName);
        try {
            if (!fileDirectory.exists()) {
                if (!fileDirectory.mkdir()) {
                    throw new IOException("文件夹创建失败,路径为：" + fileDirectory);
                }
            }
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return newFileName;
    }

    @Override
    public void deleteFiles(String[] urls, Long adminId) {
        String dirPath = FILE_UPLOAD_DIC + adminId + "/";
        for (String url: urls){
            FileSystemUtils.deleteRecursively(new File(dirPath + url.substring(url.lastIndexOf('/') + 1)));
        }
    }
}
