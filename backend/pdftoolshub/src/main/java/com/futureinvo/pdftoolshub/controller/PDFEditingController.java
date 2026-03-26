package com.futureinvo.pdftoolshub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.futureinvo.pdftoolshub.service.PDFEditingService;
import com.futureinvo.pdftoolshub.util.FileUtil;

@RestController
@RequestMapping("/pdf/edit")
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

}
