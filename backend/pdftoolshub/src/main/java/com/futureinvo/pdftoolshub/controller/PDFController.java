package com.futureinvo.pdftoolshub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.futureinvo.pdftoolshub.exception.CustomException;
import com.futureinvo.pdftoolshub.service.PDFService;
import com.futureinvo.pdftoolshub.util.FileUtil;



@RestController
@RequestMapping("/pdf/convert")
public class PDFController {

	@Autowired
	private PDFService pdfService;
	
	@Autowired
	private FileUtil fileUtil;
	
	@PostMapping("/pdf-to-word")
	public ResponseEntity<byte[]> pdfToWord(@RequestParam("file") MultipartFile file ) throws RuntimeException, Exception{
		
		if(file.isEmpty()) {
			throw new CustomException("Empty File is uploade");
		}
		
		byte[] result = pdfService.pdfToWord(file);
		String name = fileUtil.baseName(file.getOriginalFilename()) + ".docx";
		return  fileUtil.downloadResponse(
				result,
				name,
				"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
				
	}
}
