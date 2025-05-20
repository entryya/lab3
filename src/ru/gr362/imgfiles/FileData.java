package ru.gr362.imgfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import ru.gr362.converting.Converter;
import ru.gr362.painting.FractalPainter;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FileData {

    public static void saveFractal(FractalPainter fp, File file) {
        var filename = file.getName();
        var nameParts = filename.split("\\.");
        var ext = nameParts[nameParts.length - 1];

        if (ext.equals("frac")) {
            saveAsFractal(ext, fp.getConv(), file);
        }
        else {
            try {
                saveAsImage(ext, fp.getImg(), fp.getConv(), file);
            } catch (NonImageFormatException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static void saveAsFractal(String ext, Converter conv, File file) {
        var mapper = new ObjectMapper();
        try {
            /*var s = mapper.writeValueAsString(conv);
            System.out.println(s);*/
            mapper.writeValue(file, conv);
        } catch (JsonProcessingException e) {
            System.out.println("json error");
        } catch (IOException e) {
            System.out.println("json error");
        }
    }

    public static Converter openFractal(File file) {
        return load(file, Converter.class);
    }

    public static <T> T load(File file, Class<T> type) {
        var mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        try {
            return mapper.readValue(file, type);
        } catch (IOException e) {
            System.out.println("load error");
            return null;
        }
    }

    public static void saveAsImage(String ext, BufferedImage img, Converter conv, File file) throws NonImageFormatException, IOException {

        var writers = ImageIO.getImageWritersByFormatName(ext);
        if (!writers.hasNext()){
            throw new NonImageFormatException("Указанный формат не может быть использован для сохранения изображения");
        }

        var b = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        var bG = b.getGraphics();
        bG.drawImage(img, 0, 0 , null);

        String label = String.format(
                "x: [%.5f, %.5f], y: [%.5f, %.5f]",
                conv.getxMin(), conv.getxMax(), conv.getyMin(), conv.getyMax()
        );

        bG.setColor(new Color(255, 255, 255, 180));
        bG.fillRect(10, img.getHeight() - 30, bG.getFontMetrics().stringWidth(label) + 10, 20);
        bG.setColor(Color.BLACK);
        bG.drawString(label, 15, img.getHeight() - 15);

        var writer = writers.next();
        var param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.98f);
        try (var os = ImageIO.createImageOutputStream(file)) {
            writer.setOutput(os);
            writer.write(
                    null,
                    new IIOImage(b, null, null),
                    param
            );
            writer.dispose();
        }
    }
}