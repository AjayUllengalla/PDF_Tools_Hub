package com.futureinvo.pdftoolshub.util;

import org.springframework.http.MediaType;

//import java.io.File;
import java.io.IOException;
import java.io.InputStream;
//import java.lang.System.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.futureinvo.pdftoolshub.exception.CustomException;
//import com.futureinvo.pdftoolshub.exception.GlobalException;


import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileUtil {

	@Value("${app.temp-dir}")
	private String tempDir;
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(FileUtil.class);


	// creates directory path if not exists tempPath
		public Path getTempPath() {
		
		Path dir = Paths.get(tempDir);
		try {
			Files.createDirectories(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dir;
		
	}
		//save the uploaded file in temp Directory
		public Path saveToTemp(MultipartFile file, String extension) throws RuntimeException, Exception {
			String fileName = UUID.randomUUID() + "." + extension;
			Path target = getTempPath().resolve(fileName);
			file.transferTo(target.toFile());
			log.debug("Saved to temp file: {} ", target);
			return target;
		}
		
		//read and delete file
		public byte[] readAndDelete(Path path) throws Exception {
			byte[] bytes = Files.readAllBytes(path);
			Files.delete(path);
			return bytes;
		}
		
		// Delete tempfile file
		public void deleteFile(Path path) {
			try {
				if(path != null) {
					Files.deleteIfExists(path);
				}
			} catch (Exception e) {
				
			}
		}
		
		// download response
		public ResponseEntity<byte[]> downloadResponse(byte[] data, String fileName, String contentType){
			return ResponseEntity.ok()
								.header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
										"attachment; filename=\""+ fileName + "\"")
										.contentType(MediaType.parseMediaType(contentType))
						                .contentLength(data.length)
						                .body(data);
		}
		
		//validate pdf
		public void validatePdf(MultipartFile file) throws IOException{
			byte[] header = new byte[4];
			
			try(InputStream is = file.getInputStream()) {
				 int bytesRead = is.read(header); 
				if( bytesRead  < 4 || !isPdfMagic(header)) {
					throw  new CustomException("Upload file not valid pdf type");
				}
			}
		}
		
		private boolean isPdfMagic(byte[] header) {
			return header[0] == 0x25 && // % 
				   header[1] == 0x50 && // P
				   header[2] == 0x44 && // D
				   header[3] == 0x46;   // F
			}
		
		//Get filename without extension
		 public String baseName(String filename) {
		        int dot = filename.lastIndexOf('.');
		        return dot > 0 ? filename.substring(0, dot) : filename;
		    }
}
