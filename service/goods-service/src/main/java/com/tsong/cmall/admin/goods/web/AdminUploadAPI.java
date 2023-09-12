package com.tsong.cmall.admin.goods.web;

import com.tsong.cmall.admin.goods.service.IAdminUploadService;
import com.tsong.cmall.admin.goods.web.params.BatchUrlParam;
import com.tsong.cmall.common.util.Result;
import com.tsong.cmall.common.util.ResultGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @Author Tsong
 * @Date 2023/4/3 17:51
 */
@RestController
@Tag(name = "Admin Upload", description = "2-7.后台管理系统文件上传接口")
@RequestMapping("/admin/image")
public class AdminUploadAPI {
//    private static final Logger logger = LoggerFactory.getLogger(AdminUploadAPI.class);

    @Autowired
    private StandardServletMultipartResolver standardServletMultipartResolver;
    @Autowired
    private IAdminUploadService adminUploadService;

    /**
     * 图片上传
     */
    @PostMapping(value = "/upload/file")
    @Operation(summary = "单图上传", description = "file Name \"file\"")
    public Result upload(@RequestParam("file") MultipartFile file,
                         Long adminId) {
        String res = adminUploadService.uploadFile(file, adminId);
        if (res == null) {
            return ResultGenerator.genFailResult("文件上传失败");
        }
        Result resultSuccess = ResultGenerator.genSuccessResult();
        resultSuccess.setData(res);
        return resultSuccess;
    }

    /**
     * 图片上传
     */
    @PostMapping(value = "/upload/files")
    @Operation(summary = "多图上传", description = "图片上传")
    public Result uploadV2(HttpServletRequest httpServletRequest, Long adminId) {
        List<MultipartFile> multipartFiles = new ArrayList<>(8);
        if (standardServletMultipartResolver.isMultipart(httpServletRequest)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) httpServletRequest;
            Iterator<String> iter = multiRequest.getFileNames();
            int total = 0;
            while (iter.hasNext()) {
                if (total > 5) {
                    return ResultGenerator.genFailResult("最多上传5张图片");
                }
                total += 1;
                MultipartFile file = multiRequest.getFile(iter.next());
                multipartFiles.add(file);
            }
        }
        if (CollectionUtils.isEmpty(multipartFiles)) {
            return ResultGenerator.genFailResult("参数异常");
        }
        if (multipartFiles != null && multipartFiles.size() > 5) {
            return ResultGenerator.genFailResult("最多上传5张图片");
        }

        List<String> res = adminUploadService.uploadFiles(multipartFiles, adminId);
        if (res == null) {
            return ResultGenerator.genFailResult("文件上传失败");
        }
        return ResultGenerator.genSuccessResult(res);
    }

    /**
     * 图片删除
     */
    @PostMapping(value = "/delete/files")
    @Operation(summary = "多图删除", description = "图片删除")
    public Result deleteFiles(@RequestBody @Valid BatchUrlParam batchUrlParam, Long adminId) {
        if (batchUrlParam.getUrls().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        adminUploadService.deleteFiles(batchUrlParam.getUrls(), adminId);
        return ResultGenerator.genSuccessResult("删除成功");
    }

}
