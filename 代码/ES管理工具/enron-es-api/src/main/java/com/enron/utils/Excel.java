package com.enron.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.springframework.web.multipart.MultipartFile;


public class Excel {
    //文件处理
    public static File multipartFileToFile(MultipartFile file) throws Exception {
        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }
    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *   	读取所有行
     * @param file
     * @throws Exception
     */
    public List<List<String>>  readAllRows(File file)throws Exception{
        List<List<String>> all = new ArrayList<>();
        Workbook readwb = null;
        InputStream io = new FileInputStream(file.getAbsoluteFile());

        readwb = Workbook.getWorkbook(io);
        Sheet readsheet = readwb.getSheet(0);
        int rsColumns = readsheet.getColumns();  //获取表格列数
        int rsRows = readsheet.getRows();  //获取表格行数
        for(int i=0; i<rsRows; i++){
            List<String> columnList = new ArrayList<String>();
            for(int j=0; j<rsColumns; j++){

                Cell cell=readsheet.getCell(j, i);
                columnList.add(cell.getContents());
            }

            all.add(columnList);
        }

        return  all;
    }


}
