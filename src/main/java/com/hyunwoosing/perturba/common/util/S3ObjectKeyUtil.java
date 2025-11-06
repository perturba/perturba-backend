package com.hyunwoosing.perturba.common.util;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class S3ObjectKeyUtil {

    // INPUT 업로드용: users/{owner}/{uuid}-{filename}
    public static String inputKey(String filename, @Nullable Long userId) {
        String owner = (userId == null) ? "guest" : userId.toString();
        return "users/" + owner + "/" + UUID.randomUUID() + "-" + trimSlash(filename);
    }

    // 결과물 업로드용: users/{owner}/jobs/{publicId}/{type}-{uuid}.jpg
    public static String jobResultJpegKey(String owner, String publicId, String type) {
        return "users/" + owner + "/jobs/" + publicId + "/" + type + "-" + UUID.randomUUID() + ".jpg";
    }

    private static String trimSlash(String s) {
        return (s == null) ? "" : s.replaceFirst("^/+", "");
    }
}