package com.pacheco.app.ecommerce.infrastructure.awsS3;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("application.aws.s3")
@Getter
@Setter
@NoArgsConstructor
public class AwsS3Properties {

    private String imageBucketName;

}
