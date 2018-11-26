package com.yj.service.impl;

import com.google.common.collect.Lists;
import com.yj.service.IFileService;
import com.yj.util.FTPUtill;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by 63254 on 2018/8/28.
 */
@Transactional(readOnly = false)
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path, String removePath){
        String fileName = file.getOriginalFilename();
        //扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件，上传文件的文件名:{}，上传的路径:{}，新文件名:{}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);
        logger.info(targetFile.toString());
        try {
            //上传文件
            file.transferTo(targetFile);
            //压缩图片
            Thumbnails.of(targetFile)
            .scale(1f)//指定图片大小    0-1f  1f是原图
            .outputQuality(0.23f)//图片质量  0-1f  1f是原图
            .toFile(targetFile);

            //todo 将文件传到ftp服务器上
            FTPUtill.uploadFile(Lists.newArrayList(targetFile),removePath);

            //todo 上传完之后，删除upload下面的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            System.out.println(e);
            return null;
        }
        return targetFile.getName();
    }
}
