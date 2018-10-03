package com.sgr.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.sgr.domain.User;
import com.sgr.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Service("imageService")
public class ImageServiceImpl implements ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);
    @Value("${cloudinary.cloud_name}")
    private String cloud_name;

    @Value("${cloudinary.api_key}")
    private String api_key;

    @Value("${cloudinary.api_secret}")
    private String api_secret;

    private Cloudinary cloudinary = null;

    @Override
    public User storeImage(User user) {
        MultipartFile file = user.getPicture();
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty() || filename.isEmpty()) {
                //throw new StorageException("Failed to store empty file " + filename);
                logger.error("Failed to store empty file::: " + file.getSize());
                logger.error("file::: " + file);
                logger.error("file name::: " + filename);
                return user;
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }

            //Map params = ObjectUtils.asMap("transformation", getProfileThumbTransformation());

            Map options = ObjectUtils.asMap(
                    "eager", Arrays.asList(getProfileThumbTransformation()));

            cloudinary = getInstanceOfCloudinary();
            Map uploadResult = null;
            uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            user.setProfile_thumb_url((String) uploadResult.get("secure_url"));
            user.setPic_public_id((String) uploadResult.get("public_id"));
            user.setProfile_pic_link((String) uploadResult.get("url"));
            logger.info("Upload Result ::" + uploadResult);
            return user;

        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    private Cloudinary getInstanceOfCloudinary() {
        if (null != cloudinary)
            return cloudinary;
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloud_name,
                "api_key", api_key,
                "api_secret", api_secret));
        return cloudinary;
    }

    private Transformation getProfileThumbTransformation() {
        return new Transformation()
                .width(90)
                .height(98)
                .crop("fill")
                .gravity("face")
                .radius("max")
                .effect("sepia");
    }
}
