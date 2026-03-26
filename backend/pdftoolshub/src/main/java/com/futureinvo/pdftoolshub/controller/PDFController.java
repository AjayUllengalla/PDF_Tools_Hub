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
	@PostMapping("/word-to-pdf")
	public ResponseEntity<byte[]> wordToPdf(
	        @RequestParam("file") MultipartFile file) throws Exception {

	    if (file == null || file.isEmpty()) {
	        throw new RuntimeException("File is missing or empty");
	    }

	    byte[] result = pdfService.wordToPdf(file);

	    String originalName = file.getOriginalFilename();

	    if (originalName == null) {
	        originalName = "converted";
	    }

	    String name = fileUtil.baseName(originalName) + ".pdf";

	    return fileUtil.downloadResponse(
	            result,
	            name,
	            "application/pdf"
	    );
	}
}
