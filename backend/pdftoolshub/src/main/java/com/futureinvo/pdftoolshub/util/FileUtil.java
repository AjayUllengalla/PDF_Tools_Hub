package com.futureinvo.pdftoolshub.util;

import org.springframework.http.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.futureinvo.pdftoolshub.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileUtil {

    // If not set in properties, fallback to system temp
    @Value("${app.temp-dir:}")
    private String tempDir;

    //  Get temp directory safely
    public Path getTempPath() {

        try {
            // 🔥 Fallback if property is missing
            if (tempDir == null || tempDir.isEmpty()) {
                tempDir = System.getProperty("java.io.tmpdir") + "/pdftoolshub";
            }

            Path dir = Paths.get(tempDir);

            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            return dir;

        } catch (Exception e) {
            throw new CustomException("Failed to initialize temp directory");
        }
    }

    //  Save file to temp directory
    public Path saveToTemp(MultipartFile file, String extension) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new CustomException("File is null or empty");
        }

        String fileName = UUID.randomUUID() + "." + extension;

        Path tempDirPath = getTempPath();

        Path target = tempDirPath.resolve(fileName);

        file.transferTo(target.toFile());

//        log.debug("Saved to temp file: {}", target);

        return target;
    }

    //  Read & delete file
    public byte[] readAndDelete(Path path) throws Exception {
        byte[] bytes = Files.readAllBytes(path);
        Files.deleteIfExists(path);
        return bytes;
    }

    //  Delete temp file safely
    public void deleteFile(Path path) {
        try {
            if (path != null) {
                Files.deleteIfExists(path);
            }
        } catch (Exception e) {
//            log.warn("Failed to delete temp file: {}", path);
        }
    }

    // ✅ Download response
    public ResponseEntity<byte[]> downloadResponse(byte[] data, String fileName, String contentType) {
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(data.length)
                .body(data);
    }

    // ✅ Validate PDF file
    public void validatePdf(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new CustomException("File is empty");
        }

        byte[] header = new byte[4];

        try (InputStream is = file.getInputStream()) {
            int bytesRead = is.read(header);

            if (bytesRead < 4 || !isPdfMagic(header)) {
                throw new CustomException("Uploaded file is not a valid type");
            }
        }
    }

    private boolean isPdfMagic(byte[] header) {
        return header[0] == 0x25 && // %
               header[1] == 0x50 && // P
               header[2] == 0x44 && // D
               header[3] == 0x46;   // F
    }

    // ✅ Get filename without extension
    public String baseName(String filename) {

        if (filename == null || filename.isEmpty()) {
            return "file";
        }

        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(0, dot) : filename;
    }
}