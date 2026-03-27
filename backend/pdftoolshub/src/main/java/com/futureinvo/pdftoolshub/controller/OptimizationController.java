package com.futureinvo.pdftoolshub.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.futureinvo.pdftoolshub.service.OptimizationService;
import com.futureinvo.pdftoolshub.util.FileUtil;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pdf/optimize")
@RequiredArgsConstructor
public class OptimizationController {

	@Autowired
	private OptimizationService optimizationService;
	 private static final Logger log = LoggerFactory.getLogger(OptimizationController.class);
	 @Autowired
	private FileUtil fileUtil;
	
	@PostMapping("/compress")
	public ResponseEntity<byte[]> cpmpressPdf(
			@RequestParam("file") MultipartFile file, 
			@RequestParam(value = "imageQuality", defaultValue = "0.6") float imageQuality) throws Exception {
		 log.info("POST /optimize/compress — {} | quality={}", file.getOriginalFilename(), imageQuality);
				
		byte[] result = optimizationService.compressPdf(file, imageQuality);
		String name = fileUtil.baseName(file.getOriginalFilename()) + "." + "compressed.pdf";
		return fileUtil.downloadResponse(result, name, "application/pdf");
	}
}
