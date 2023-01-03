package com.eddie.reggie.controller;

import com.eddie.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * //TODO 文件上传下载的Controller
 @author EddieZhang
 @create 2023-01-02 22:26
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.uploadFilePath}")
    private String filePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @RequestMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info(file.toString());
        //获取file的originName
        String originalFilename = file.getOriginalFilename();
        //获取file的originName的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        log.info(suffix);
        //使用UUID并且根据file的originName的后缀组成新的文件name
        String fileName = UUID.randomUUID() + suffix;//xxx.jpg
        //将file转存至指定的路径 并且判断是否存在路径中的file 若不存在则创建
        File dir = new File(filePath);
        log.info(filePath);
        if (!dir.exists()) {//判断dir是否存在 若不存在则进行make
            log.info("mkdir");
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(filePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param response
     * @param name
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response, String name) {
        try {
            //输入流 读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(filePath + name));
            //输出流 写出文件内容
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");//设置响应的类型
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            //释放资源
            if (outputStream != null) {
                outputStream.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
