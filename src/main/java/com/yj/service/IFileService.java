package com.yj.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by 63254 on 2018/8/28.
 */
public interface IFileService {

    String upload(MultipartFile file, String path, String removePath);


    String upload_uncompressed(MultipartFile file, String path, String removePath);
}
