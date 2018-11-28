package com.bjpowernode.p2p;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:QRCode
 * Package:com.bjpowernode.p2p
 * Description:
 * Date:2018/3/22 11:02
 * Author:13651027050
 */
public class QRCode {

    @Test
    public void generateQRCode() throws WriterException, IOException {

        //创建json对象
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("address","北京大兴");
        jsonObject.put("companyName","动力节点");
        jsonObject.put("phone","82889999");

        //把json对象转换为string字符串
        String content = jsonObject.toString();

        //二维码尺寸
        int width = 200;
        int hight = 200;

        //二维码编码格式
        Map<EncodeHintType,Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");

        //生成路径对象
        String filePath = "D://";
        String fileName = "QRCode.jpg";

        //创建一个路径对象
        Path path = FileSystems.getDefault().getPath(filePath,fileName);

        //创建一个矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,BarcodeFormat.QR_CODE,width,hight,hints);

        MatrixToImageWriter.writeToPath(bitMatrix,"jpg",path);

        System.out.println("二维码图片: ");

    }

    @Test
    public void generateQrcode() throws WriterException, IOException {

        //创建json对象
        JSONObject jsonObject = new JSONObject();
        //给json对象赋值
        jsonObject.put("hh","HelloQRC");

        //json对象转换为json格式的字符串
        String jsonString = jsonObject.toString();

        //条码宽度,高度
        int width = 200;
        int hight = 200;
        //创建一个map集合
        Map<EncodeHintType,Object> hits = new HashMap<>();
        hits.put(EncodeHintType.CHARACTER_SET,"UTF-8");

        //创建一个矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode(jsonString,BarcodeFormat.QR_CODE,width,hight,hits);

        //创建一个路径对象
        //生成路径
        String filePath = "D://";
        String fileName = "QRCTest.jpg";
        Path path = FileSystems.getDefault().getPath(filePath,fileName);
        //将矩阵对象生成二维码
        MatrixToImageWriter.writeToPath(bitMatrix,"jpg",path);


    }

}




























