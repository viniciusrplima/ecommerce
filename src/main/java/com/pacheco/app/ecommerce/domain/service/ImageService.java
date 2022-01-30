package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.domain.model.Image;
import com.pacheco.app.ecommerce.domain.repository.ImageRepository;
import com.pacheco.app.ecommerce.infrastructure.awsS3.AwsS3ImageBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class ImageService {

    @Autowired
    private AwsS3ImageBucket imageBucket;

    @Autowired
    private ImageRepository repository;

    public Image saveImage(InputStream stream, String contentType) {
        AwsS3ImageBucket.BucketImageObject imageObject = imageBucket.saveImage(stream, contentType);

        Image image = new Image();
        image.setKey(imageObject.getKey());
        image.setBucketName(imageObject.getBucketName());
        image.setPublicUrl(imageObject.getUrl());

        return repository.save(image);
    }

    public void deleteImage(Image image) {
        imageBucket.removeImage(image.getBucketName(), image.getKey());
    }
}
