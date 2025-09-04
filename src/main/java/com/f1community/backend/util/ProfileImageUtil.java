package com.f1community.backend.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public final class ProfileImageUtil {

    private ProfileImageUtil() {
    }

    /**
     * 웹 경로(/uploads/... 또는 http(s)://...)를 받아
     * - 외부 URL이면 null(파일 읽지 않음)
     * - 내부 저장 파일이면 uploadRootDir/username/파일명 또는 기본 이미지 경로에서 파일을 읽어 Base64로 반환
     *
     * @param webPath       DB에 저장된 프로필 이미지 경로 (예: /uploads/profile-images/{username}/{file}.png or /uploads/profile-images/default.png or http://cdn/...)
     * @param username      사용자명 (폴더 매핑용)
     * @param uploadRootDir 물리 루트 경로 (예: /home/mindung/f1community/uploads/profile-images)
     * @param withDataUri   true면 data:image/*;base64, 접두어까지 포함해서 반환
     * @return base64 문자열(또는 data URI), 없으면 null
     */
    public static String toBase64FromWebPath(String webPath,
                                             String username,
                                             String uploadRootDir,
                                             boolean withDataUri) {
        try {
            if (webPath == null || webPath.isBlank()) return null;

            // 외부 URL은 파일을 읽지 않음
            String lower = webPath.toLowerCase();
            if (lower.startsWith("http://") || lower.startsWith("https://")) {
                return null;
            }

            // 파일명 추출
            String fileName = Paths.get(webPath).getFileName().toString();

            // 기본 이미지인지 판별
            Path physicalPath;
            if ("default.png".equalsIgnoreCase(fileName)) {
                // 기본 이미지는 username 폴더 없이 바로
                physicalPath = Paths.get(uploadRootDir, fileName);
            } else {
                // 일반 유저 이미지는 username 폴더 안
                physicalPath = Paths.get(uploadRootDir, username, fileName);
            }

            if (!Files.exists(physicalPath)) return null;

            byte[] bytes = Files.readAllBytes(physicalPath);
            String base64 = Base64.getEncoder().encodeToString(bytes);

            if (!withDataUri) return base64;

            String mime = guessMimeTypeByName(fileName);
            return "data:" + mime + ";base64," + base64;

        } catch (Exception e) {
            return null;
        }
    }

    private static String guessMimeTypeByName(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        return "application/octet-stream";
    }
}