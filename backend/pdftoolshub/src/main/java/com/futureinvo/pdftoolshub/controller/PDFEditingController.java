package com.futureinvo.pdftoolshub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.futureinvo.pdftoolshub.service.PDFEditingService;
import com.futureinvo.pdftoolshub.util.FileUtil;

@RestController
@RequestMapping("/pdf/edit")
@CrossOrigin(origins  = "*")
public class PDFEditingController {

	@Autowired
	private  PDFEditingService editingService;
	@Autowired
    private FileUtil fileUtil;

    @PostMapping("/merge")
    public ResponseEntity<byte[]> merge(
            @RequestParam("files") List<MultipartFile> files) throws Exception {
        byte[] result = editingService.mergePdfs(files);
        return fileUtil.downloadResponse(result, "merged.pdf", "application/pdf");
    }

    @PostMapping("/split")
    public ResponseEntity<byte[]> splitPdf(
    		@RequestParam("file") MultipartFile file, 
    		@RequestParam(value = "startPage", defaultValue = "1") int startPage,
    		@RequestParam(value = "endPage", defaultValue = "-1") int endPage) throws Exception {
    	byte[] result = editingService.splitPdf(file, startPage, endPage);
    	String name = fileUtil.baseName(file.getOriginalFilename()) +"_split.pdf";
    	return fileUtil.downloadResponse(result, name, "application/pdf");
    }
    
    @PostMapping("/rotate")
    public ResponseEntity<byte[]> rotatePdf(
    		@RequestParam("file") MultipartFile file,
    		@RequestParam int angle,
    		@RequestParam String pageNums) throws Exception {
    	byte[] result = editingService.rotatePdf(file, angle, pageNums);
    	String name = fileUtil.baseName(file.getOriginalFilename())+ "_rotated.pdf";
    	return fileUtil.downloadResponse(result, name, "application/pdf");
    }
}