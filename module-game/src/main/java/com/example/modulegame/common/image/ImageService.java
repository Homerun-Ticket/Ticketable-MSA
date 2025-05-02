package com.example.modulegame.common.image;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.modulegame.global.exception.ErrorCode;
import com.example.modulegame.global.exception.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucketName}")
    private String bucket;

    public String saveFile(MultipartFile multipartFile, String fileKey)  {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            amazonS3.putObject(bucket, fileKey, multipartFile.getInputStream(), metadata);
            return amazonS3.getUrl(bucket, fileKey).toString();
        } catch (IOException e) {
            throw new ServerException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public void deleteFile(String fileName) {
        try {
            String key = extractKeyFromUrl(fileName);
            DeleteObjectRequest deleteRequest = new DeleteObjectRequest(
                    bucket,
                    key
            );
            amazonS3.deleteObject(deleteRequest);
        } catch (ServerException e) {
            throw new ServerException(ErrorCode.IMAGE_DELETE_FAILED);
        }
    }

    private String extractKeyFromUrl(String url) {
        String delimiter = ".amazonaws.com/";
        int index = url.indexOf(delimiter);

        if (index == -1) {
            throw new ServerException(ErrorCode.IMAGE_DELETE_FAILED);
        }

        return url.substring(index + delimiter.length());
    }
}
