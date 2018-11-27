package com.yww.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

    /**
     * excel导入
     * @param keys		字段名称数组，如  ["id", "name", ... ]
     * @param filePath	文件物理地址
     * @return
     */
    public static List<Map<String, Object>> imp(String filePath, String[] keys) throws Exception {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();

        if(null == keys) {
            throw new Exception("keys can not be null!");
        }

        if (!filePath.endsWith(".xls") && !filePath.endsWith(".xlsx")) {
            throw new Exception("The file is not excel document!");
        }

        // 读取文件
        FileInputStream fis = null;
        Workbook wookbook = null;
        try {

            fis = new FileInputStream(filePath);
            if(filePath.endsWith(".xls")) {
                wookbook = new HSSFWorkbook(fis);
            } else if(filePath.endsWith(".xlsx")) {
                wookbook = new XSSFWorkbook(fis);
            }

            // 获取第一个工作表信息
            Sheet sheet = wookbook.getSheetAt(0);

            //获得数据的总行数
            int totalRowNum = sheet.getLastRowNum();

            // 获得表头
            Row rowHead = sheet.getRow(0);
            // 获得表头总列数
            int cols = rowHead.getPhysicalNumberOfCells();

            // 传入的key数组长度与表头长度不一致
            if(keys.length != cols) {
                throw new Exception("keys length does not match head row's cols!");
            }

            Row row = null;
            Cell cell = null;
            Object value = null;
            // 遍历所有行
            for (int i = 1; i <= totalRowNum; i++) {
                // 清空数据，避免遍历时读取上一次遍历数据
                row = null;
                cell = null;
                value = null;
                map = new HashMap<String, Object>();

                row = sheet.getRow(i);
                if(null == row) continue;	// 若该行第一列为空，则默认认为该行就是空行

                // 遍历该行所有列
                for (short j = 0; j < cols; j++) {
                    cell = row.getCell(j);
                    if(null == cell) continue;	// 为空时，下一列

                    // 根据poi返回的类型，做相应的get处理
                    if(Cell.CELL_TYPE_STRING == cell.getCellType()) {
                        value = cell.getStringCellValue();
                    } else if(Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                        value = cell.getNumericCellValue();

                        // 由于日期类型格式也被认为是数值型，此处判断是否是日期的格式，若时，则读取为日期类型
                        if(cell.getCellStyle().getDataFormat() > 0)  {
                            value = cell.getDateCellValue();
                        }
                    } else if(Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
                        value = cell.getBooleanCellValue();
                    } else if(Cell.CELL_TYPE_BLANK == cell.getCellType()) {
                        value = cell.getDateCellValue();
                    } else {
                        throw new Exception("At row: %s, col: %s, can not discriminate type!");
                    }

                    map.put(keys[j], value);
                }

                list.add(map);
            }
        } catch (Exception e) {
            throw new Exception("analysis excel exception!", e);
        } finally {
            if(null != fis) {
                fis.close();
            }
        }

        return list;
    }

    /**
     * excel导出
     * @param fileNamePath	导出的文件名称
     * @param sheetName		导出的sheet名称
     * @param list			数据集合
     * @param titles		第一行表头
     * @param fieldNames	字段名称数组
     * @return
     * @throws Exception
     */
    public static <T> File export(String fileNamePath, String sheetName, List<T> list,
                                  String[] titles, String[] fieldNames) throws Exception {

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = null;

        // 对每个表生成一个新的sheet,并以表名命名
        if(sheetName == null){
            sheetName = "sheet1";
        }
        sheet = wb.createSheet(sheetName);

        // 设置表头的说明
        HSSFRow topRow = sheet.createRow(0);
        for(int i = 0; i < titles.length; i++){
            setCellGBKValue(topRow.createCell(i), titles[i]);
        }

        String methodName = "";
        Method method = null;
        T t = null;
        Object ret = null;
        // 遍历生成数据行，通过反射获取字段的get方法
        for (int i = 0; i < list.size(); i++) {
            t = list.get(i);
            HSSFRow row = sheet.createRow(i+1);
            Class<? extends Object> clazz = t.getClass();
            for(int j = 0; j < fieldNames.length; j++){
                methodName = "get" + capitalize(fieldNames[j]);
                try {
                    method = clazz.getDeclaredMethod(methodName);
                } catch (java.lang.NoSuchMethodException e) {	//	不存在该方法，查看父类是否存在。此处只支持一级父类，若想支持更多，建议使用while循环
                    if(null != clazz.getSuperclass()) {
                        method = clazz.getSuperclass().getDeclaredMethod(methodName);
                    }
                }
                if(null == method) {
                    throw new Exception(clazz.getName() + " don't have menthod --> " + methodName);
                }
                ret = method.invoke(t);
                setCellGBKValue(row.createCell(j), ret + "");
            }
        }

        File file = null;
        OutputStream os = null;
        file = new File(fileNamePath);
        try {
            os = new FileOutputStream(file);
            wb.write(os);
        } catch (Exception e) {
            throw new Exception("write excel file error!", e);
        } finally {
            if(null != os) {
                os.flush();
                os.close();
            }
        }


        return file;
    }

    private static void setCellGBKValue(HSSFCell cell, String value) {
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue(value);
    }

    private static String capitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        final char firstChar = str.charAt(0);
        final char newChar = Character.toTitleCase(firstChar);
        if (firstChar == newChar) {
            // already capitalized
            return str;
        }

        char[] newChars = new char[strLen];
        newChars[0] = newChar;
        str.getChars(1,strLen, newChars, 1);
        return String.valueOf(newChars);
    }

}
