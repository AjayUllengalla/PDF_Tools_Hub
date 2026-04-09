package com.futureinvo.pdftoolshub.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.Iterator;

import javax.imageio.*;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.*;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.futureinvo.pdftoolshub.util.FileUtil;

@Service
public class OptimizationService {

    @Autowired
    private FileUtil fileUtil;

    private static final Logger log = LoggerFactory.getLogger(OptimizationService.class);

    // Balanced compression settings (MEDIUM)
    private static final int MAX_IMAGE_DIMENSION = 800;
    private static final float RASTER_DPI = 60f;

    // ─────────────────────────────────────────────────────────────

    public byte[] compressPdf(MultipartFile file) throws Exception {

        fileUtil.validatePdf(file);

        Path tempPath = null;

        try {
            tempPath = fileUtil.saveToTemp(file, "pdf");

            byte[] originalBytes = Files.readAllBytes(tempPath);
            long originalSize = originalBytes.length;

            log.info("Original Size: {} KB", originalSize / 1024);

            try (PDDocument pdf = PDDocument.load(tempPath.toFile())) {

                // 🔥 Remove metadata
                if (pdf.getDocumentInformation() != null) {
                    pdf.getDocumentInformation().getCOSObject().clear();
                }

                int imageCount = countImages(pdf);
                int pageCount = pdf.getNumberOfPages();
                double ratio = (double) imageCount / Math.max(1, pageCount);

                log.info("Pages: {} | Images: {} | Ratio: {}",
                        pageCount, imageCount, String.format("%.2f", ratio));

                byte[] result;

                //  MEDIUM COMPRESSION LOGIC
                if (ratio > 0.3) {
                    log.info("Mode: Image-heavy → recompression");
                    result = recompressImages(pdf, 0.3f);
                } else {
                    log.info("Mode: Text/Mixed → light rasterization");
                    result = rasterizePdf(pdf, 0.3f);
                }

                long newSize = result.length;
                double reduction = 100.0 - (newSize * 100.0 / originalSize);

                log.info("Compressed Size: {} KB", newSize / 1024);
                log.info("Reduction: {}%", String.format("%.2f", reduction));

                if (newSize >= originalSize) {
                    log.warn("Compression ineffective → returning original");
                    return originalBytes;
                }

                return result;
            }

        } finally {
            fileUtil.deleteFile(tempPath);
        }
    }

    // ── Rasterization (for text PDFs) ─────────────────────────────

    private byte[] rasterizePdf(PDDocument sourcePdf, float quality) throws Exception {

        try (PDDocument outDoc = new PDDocument()) {

            PDFRenderer renderer = new PDFRenderer(sourcePdf);

            for (int i = 0; i < sourcePdf.getNumberOfPages(); i++) {

                BufferedImage pageImage =
                        renderer.renderImageWithDPI(i, RASTER_DPI, ImageType.RGB);

                byte[] jpegBytes = encodeJpeg(pageImage, quality);
                if (jpegBytes == null) continue;

                PDPage page = new PDPage(sourcePdf.getPage(i).getMediaBox());
                outDoc.addPage(page);

                PDImageXObject img =
                        JPEGFactory.createFromByteArray(outDoc, jpegBytes);

                try (PDPageContentStream cs =
                             new PDPageContentStream(outDoc, page)) {

                    float w = page.getMediaBox().getWidth();
                    float h = page.getMediaBox().getHeight();

                    cs.drawImage(img, 0, 0, w, h);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            outDoc.save(baos);
            return baos.toByteArray();
        }
    }

    // ── Image recompression ──────────────────────────────────────

    private byte[] recompressImages(PDDocument pdf, float quality) throws Exception {

        for (PDPage page : pdf.getPages()) {
            recompressPageImages(pdf, page, quality);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pdf.save(baos);
        return baos.toByteArray();
    }

    private void recompressPageImages(PDDocument pdf, PDPage page, float quality) {

        try {
            PDResources resources = page.getResources();
            if (resources == null) return;

            for (COSName name : resources.getXObjectNames()) {

                try {
                    PDXObject obj = resources.getXObject(name);

                    if (!(obj instanceof PDImageXObject image)) continue;

                    BufferedImage rgb = toRgb(image.getImage());

                    BufferedImage scaled =
                            scaleIfNeeded(rgb, MAX_IMAGE_DIMENSION, MAX_IMAGE_DIMENSION);

                    byte[] jpegBytes = encodeJpeg(scaled, quality);
                    if (jpegBytes == null) continue;

                    resources.put(name,
                            JPEGFactory.createFromByteArray(pdf, jpegBytes));

                } catch (Exception e) {
                    log.debug("Skip image: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            log.debug("Skip page: {}", e.getMessage());
        }
    }

    // ── Helpers ──────────────────────────────────────────────────

    private int countImages(PDDocument pdf) {

        int count = 0;

        for (PDPage page : pdf.getPages()) {
            try {
                PDResources res = page.getResources();
                if (res == null) continue;

                for (COSName name : res.getXObjectNames()) {
                    try {
                        if (res.getXObject(name) instanceof PDImageXObject) {
                            count++;
                        }
                    } catch (Exception ignored) {}
                }

            } catch (Exception ignored) {}
        }

        return count;
    }

    private byte[] encodeJpeg(BufferedImage img, float quality) {

        try {
            Iterator<ImageWriter> writers =
                    ImageIO.getImageWritersByFormatName("jpeg");

            if (!writers.hasNext()) return null;

            ImageWriter writer = writers.next();

            JPEGImageWriteParam param = new JPEGImageWriteParam(null);
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try (MemoryCacheImageOutputStream out =
                         new MemoryCacheImageOutputStream(baos)) {

                writer.setOutput(out);
                writer.write(null, new IIOImage(img, null, null), param);
            } finally {
                writer.dispose();
            }

            return baos.toByteArray();

        } catch (Exception e) {
            return null;
        }
    }

    private BufferedImage toRgb(BufferedImage src) {

        BufferedImage rgb = new BufferedImage(
                src.getWidth(),
                src.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = rgb.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
        g.drawImage(src, 0, 0, null);
        g.dispose();

        return rgb;
    }

    private BufferedImage scaleIfNeeded(BufferedImage src, int maxW, int maxH) {

        int w = src.getWidth();
        int h = src.getHeight();

        float scale = Math.min((float) maxW / w, (float) maxH / h);

        int nw = Math.max(1, Math.round(w * scale));
        int nh = Math.max(1, Math.round(h * scale));

        BufferedImage out = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        g.drawImage(src, 0, 0, nw, nh, null);
        g.dispose();

        return out;
    }
}