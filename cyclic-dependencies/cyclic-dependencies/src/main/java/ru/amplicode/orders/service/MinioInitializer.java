package ru.amplicode.orders.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.SetBucketPolicyRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioInitializer {
    private final AmazonS3 amazonS3;

    @Value("${storage.bucket}")
    private String bucket;

    @SneakyThrows
    @EventListener
    public void handleApplicationReadyEvent(ApplicationReadyEvent event) {
        // create bucket if needed
        boolean exists = amazonS3.doesBucketExistV2(
            bucket
        );


        if (!exists) {
            amazonS3.createBucket(
                bucket
            );
        }

        // Allow to read for everybody

        String policyJson = """
            {
              "Version": "2012-10-17",
              "Statement": [{
                "Effect": "Allow",
                "Principal": "*",
                "Action": ["s3:GetObject"],
                "Resource": "arn:aws:s3:::%s/*"
              }]
            }
            """.formatted(bucket);


        amazonS3.setBucketPolicy(new SetBucketPolicyRequest(bucket, policyJson));
    }
}
