package com.futureinvo.pdftoolshub.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.futureinvo.pdftoolshub.util.FileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimizationService {

	@Autowired
	private FileUtil fileUtil;
	private Logger log;
	
	private static final float TARGET_IMAGE_DPI = 96f;
	
	public byte[] compressPdf(MultipartFile file ,float imageQuality) throws Exception {
		fileUtil.validatePdf(file);
		float quality = clamp(imageQuality, 0.1f, 0.1f);
		long originalSize = file.getSize();
		Path tempPath = null;
		try {
			tempPath = fileUtil.saveToTemp(file, "pdf");
			
			try (PDDocument pdf = PDDocument.load(tempPath.toFile());
					ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				
				for(PDPage page : pdf.getPages()) {
					compressPageImage(pdf, page, quality);
				}
				pdf.save(out);
				
				long compressedSize = out.size();
                float reduction = 100f - (compressedSize * 100f / originalSize);
                
                return out.toByteArray();
				
			}
		} finally {
			fileUtil.deleteFile(tempPath);
		}
		
	}

	private void compressPageImage(PDDocument pdf, PDPage page, float quality) {
		
		 try {
	            PDResources resources = page.getResources();
	            if (resources == null) return;

	            for (COSName name : resources.getXObjectNames()) {
	                try {
	                    var xobject = resources.getXObject(name);

	                    if (!(xobject instanceof PDImageXObject image)) continue;

	                    int w = image.getWidth();
	                    int h = image.getHeight();

	                    if (w < 10 || h < 10) continue;

	                    BufferedImage original  = image.getImage();
	                    BufferedImage converted = toRgb(original);

	                    BufferedImage scaled = scaleIfNeeded(converted, 1200, 1600);

	                    PDImageXObject compressed =
	                            JPEGFactory.createFromImage(pdf, scaled, quality);

	                    resources.put(name, compressed);

	                } catch (Exception ex) {
	                    log.debug("Skipped image {}: {}", name.getName(), ex.getMessage());
	                }
	            }
	        } catch (Exception ex) {
	            log.debug("Skipped page image compression: {}", ex.getMessage());
	        }
	}

	private BufferedImage toRgb(BufferedImage src) {
		
		if(src.getType() == BufferedImage.TYPE_INT_RGB) return src;
		BufferedImage rgb = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = rgb.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
		g.drawImage(src , 0, 0, null);
		g.dispose();
		return rgb;
	}

	private BufferedImage scaleIfNeeded(BufferedImage src, int maxW, int maxH) {
		 int w = src.getWidth(), h = src.getHeight();
	        if (w <= maxW && h <= maxH) return src;

	        float scale = Math.min((float) maxW / w, (float) maxH / h);
	        int nw = Math.max(1, Math.round(w * scale));
	        int nh = Math.max(1, Math.round(h * scale));

	        BufferedImage out = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
	        Graphics2D g = out.createGraphics();
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                           RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	        g.drawImage(src, 0, 0, nw, nh, null);
	        g.dispose();
	        return out;
		
	}

	private float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}
}
