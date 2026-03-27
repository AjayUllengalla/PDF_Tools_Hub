package com.futureinvo.pdftoolshub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.futureinvo.pdftoolshub.service.PDFSecurityService;
import com.futureinvo.pdftoolshub.util.FileUtil;

@RestController
@RequestMapping("/pdf/security")
public class PDFSecurityController {

	@Autowired
	private FileUtil fileUtil;
	@Autowired
	private PDFSecurityService securityService;
	
	@PostMapping("/lock")
	public ResponseEntity<byte[]> lockPdf(
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "userPassword") String userPassword,
			@RequestParam(value = "ownerPassword" , defaultValue = "") String ownerPassword,
			@RequestParam(value = "allowPrinting" , defaultValue = "true") boolean allowPrinting,
			@RequestParam(value = "allowCopying", defaultValue = "false") boolean allowCopying
			) throws Exception {
		
		byte[] result = securityService.lockPdf(file, userPassword, ownerPassword, allowPrinting, allowCopying);
		String name = fileUtil.baseName(file.getOriginalFilename()) + "_locked.pdf";
		return fileUtil.downloadResponse(result, name, "application/pdf");
	}
	
	@PostMapping("/unlock")
	public ResponseEntity<byte[]> unlockPdf(
			@RequestParam("file")MultipartFile file,
			@RequestParam(value = "userPassword") String userPassword) throws Exception{
		byte[] result = securityService.unlockPdf(file, userPassword);
		String name = fileUtil.baseName(file.getOriginalFilename()) + "_Unlocked.pdf";
		return fileUtil.downloadResponse(result, name, "application/pdf");
		
	}
	
}
