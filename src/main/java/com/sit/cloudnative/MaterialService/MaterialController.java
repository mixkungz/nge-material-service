package com.sit.cloudnative.MaterialService;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@RestController
public class MaterialController {
    @Autowired
    private MaterialService materialService;


    @RequestMapping(
            method = RequestMethod.POST,
            value = "/subjects/{subjectId}/materials"
    )
    public ResponseEntity<Material> addMaterial(@PathVariable("subjectId")int subjectId,@RequestParam("file") MultipartFile file,@RequestParam("isActive")boolean isActive) throws Exception {

        String fileType = file.getContentType();
        String fileName = file.getOriginalFilename();
        String timestampWithFileName = generateTimestampWithFileName(fileName);
        String encryptTimestampWithFileName = encrypt(timestampWithFileName);

        if(checkValidFileType(fileType)){
            Material material = new Material();
            material.setId(encryptTimestampWithFileName);
            material.setSubjectId(subjectId);
            material.setFileName(timestampWithFileName);
            material.setPath("wait for path");
            material.setActive(isActive);
            Material material_object = materialService.addMaterial(material);
            return new ResponseEntity<Material>(material_object,HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    public boolean checkValidFileType(String fileType){
        String allowType[] = {"application/pdf","application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation"};
        return Arrays.asList(allowType).contains(fileType);
    }

    public String generateTimestampWithFileName(String originalFileName){
        Date now = new Date();
        SimpleDateFormat yearMonthDay = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat hhmmss = new SimpleDateFormat("hhmmss");
        return yearMonthDay.format(now)+"-"+hhmmss.format(now)+"-"+originalFileName;
    }

    public String encrypt(String timestampWithFileName) {
        String key = "Bar12345Bar12345";
        String initVector = "WoWoWoWoWoWoWoWo";

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(timestampWithFileName.getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
