package mycom.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
public class OSSConfig {

    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.secretAccessKey}")
    private String secretAccessKey;

    @Value("${aliyun.oss.endPoint}")
    private String endPoint;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    public String uploadOneFile(MultipartFile file) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, secretAccessKey);
        //设置文件名
        String fileName = new DateTime().toString("yyyy/MM/dd")
                + UUID.randomUUID().toString().replace("-", "")
                + file.getOriginalFilename();

        try {
            // 创建PutObject请求。
            ossClient.putObject(bucketName, fileName, file.getInputStream());

            String url = "http://" + bucketName + "." + endPoint + "/" + fileName;
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    public String uploadDataUrl(String dataUrl) {
        byte[] fileContent = decodeDataURL(dataUrl);

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, secretAccessKey);
        // 设置文件名
        String fileName = new DateTime().toString("yyyy/MM/dd")
                + UUID.randomUUID().toString().replace("-", "")
                + ".jpg"; // 假设文件类型为 jpg

        try {
            // 创建PutObject请求。
            ossClient.putObject(bucketName, fileName, new ByteArrayInputStream(fileContent));

            String url = "http://" + bucketName + "." + endPoint + "/" + fileName;
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    public byte[] decodeDataURL(String dataURL) {
        String[] parts = dataURL.split(",");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid data URL");
        }

        String base64Data = parts[1];
        byte[] fileContent = Base64.getDecoder().decode(base64Data);

        return fileContent;
    }
    public List<String> uploadArrayFile(MultipartFile[] files) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, secretAccessKey);
        List<String> list = new ArrayList<>();

        try {
            //设置文件名
            for (MultipartFile file : files) {
                String fileName = new DateTime().toString("yyyy/MM/dd")
                        + UUID.randomUUID().toString().replace("-", "")
                        + file.getOriginalFilename();
                // 创建PutObject请求。
                ossClient.putObject(bucketName, fileName, file.getInputStream());

                String url = "http://" + bucketName + "." + endPoint + "/" + fileName;
                list.add(url);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return list;

    }

    public boolean deleteFile(String fileUrl) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, secretAccessKey);
        int begin = ("http://" + bucketName + "." + endPoint + "/").length(); //找到文件路径的开始下标
        String deleteUrl = fileUrl.substring(begin);

        try {
            // 删除文件请求
            ossClient.deleteObject(bucketName, deleteUrl);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

}

