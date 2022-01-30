package com.pacheco.app.ecommerce.infrastructure.awsS3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class AwsS3ImageBucket {

    private static final int HASH_SIZE = 40;

    @Autowired
    private AwsS3Properties awsS3Properties;

    @Autowired
    private AmazonS3 s3;

    public String saveImage(InputStream file, String contentType) throws AmazonServiceException {
        String hashName = RandomStringUtils.randomAlphanumeric(HASH_SIZE);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);

        s3.putObject(awsS3Properties.getImageBucketName(), hashName, file, metadata);

        AccessControlList acl = s3.getObjectAcl(awsS3Properties.getImageBucketName(), hashName);
        acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        s3.setObjectAcl(awsS3Properties.getImageBucketName(), hashName, acl);

        return String.format("https://%s.s3.sa-east-1.amazonaws.com/%s", awsS3Properties.getImageBucketName(), hashName);
    }

    public void removeImage(String bucket, String imageKey) throws AmazonServiceException {
        s3.deleteObject(bucket, imageKey);
    }

}
