package com.pharos.walker.utils;

import android.os.Environment;

import com.pharos.walker.beans.Battery;
import com.pharos.walker.beans.EvaluateEntity;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.beans.UserTrainRecordEntity;
import com.pharos.walker.constants.Global;
import com.pharos.walker.database.EvaluateManager;
import com.pharos.walker.database.UserTrainRecordManager;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by zhanglun on 2021/1/19
 * Describe:
 */
public class SqlToExcleUtil {
    public void onUserTrainRecord(String rootPath,String userPath,String fileName) {
        int rowNum = 0;//行号
        int cellNum = 0;//单元格下标
        Cell cell;
        Row row;
        Workbook workbook = new HSSFWorkbook();// 创建工作簿
        Sheet sheet = workbook.createSheet();// 创建工作表
        row = sheet.createRow(rowNum);
        row.setHeightInPoints(28f);// 设置行高
        String[] titles = {"用户ID", "记录时间","姓名", "手术时间", "体重(kg)","诊断结果","目标负重(kg)","达标次数(次)","警告次数(次)","训练时间(分钟)","疼痛程度","不良反应","得分(0~5)","评估值(kg)"};
        // 创建单元格对象，设置内容与样式
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        for (String title : titles) {
            cell = row.createCell(cellNum++);
            cell.setCellValue(title);
            cell.setCellStyle(title(style,font));
        }
        UserBean userBean = SPHelper.getUser();
        List<UserTrainRecordEntity> recordEntities = UserTrainRecordManager.getInstance().loadAll(userBean.getUserId());
        List<EvaluateEntity> evaluateEntities = EvaluateManager.getInstance().loadAll(userBean.getUserId());
        for (UserTrainRecordEntity userTrainRecordEntity : recordEntities) {
            row = sheet.createRow(++rowNum);
            row.setHeightInPoints(24);
            for (int i = 0; i < titles.length; i++){
                cell = row.createCell(i);
                switch (i){
                    case 0:
                        cell.setCellValue(userTrainRecordEntity.getUserId()+"");
                        break;
                    case 1:
                        cell.setCellValue(DateFormatUtil.getDate2String(userTrainRecordEntity.getCreateDate(),null));
                        break;
                    case 2:
                        cell.setCellValue(userBean.getName());
                        break;
                    case 3:
                        cell.setCellValue(userBean.getDate());
                        break;
                    case 4:
                        cell.setCellValue(userBean.getWeight());
                        break;
                    case 5:
                        cell.setCellValue(userBean.getDiagnosis());
                        break;
                    case 6:
                        cell.setCellValue(userTrainRecordEntity.getTargetLoad());
                        break;
                    case 7:
                        cell.setCellValue(userTrainRecordEntity.getSuccessTime());
                        break;
                    case 8:
                        cell.setCellValue(userTrainRecordEntity.getWarningTime());
                        break;
                    case 9:
                        cell.setCellValue(userTrainRecordEntity.getTrainTime());
                        break;
                    case 10:
                        cell.setCellValue(userTrainRecordEntity.getPainLevel());
                        break;
                    case 11:
                        cell.setCellValue(userTrainRecordEntity.getAdverseReactions());
                        break;
                    case 12:
                        cell.setCellValue(userTrainRecordEntity.getScore());
                        break;
                    case 13:
                        if (evaluateEntities.size() > 0){
                            cell.setCellValue(evaluateEntities.get(evaluateEntities.size()-1).getEvaluateResult());
                        }
                        break;
                }
                cell.setCellStyle(text(style, font));
            }
        }
        try {
            File file = new File(rootPath);
            if (!file.exists()) {
                file.mkdir();
            }
            File file1 = new File(rootPath + userPath);
            if (!file1.exists()) {
                file1.mkdir();
            }
            workbook.write(new FileOutputStream(new File(file1.getAbsolutePath() + File.separator + fileName)));
            workbook.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
    /**
     * 小标题的样式
     * @param
     * @return
     */
    public CellStyle title(CellStyle style, Font font){
        font.setFontName("黑体");
        font.setFontHeightInPoints((short)12);
        style.setFont(font);
        style.setAlignment(CellStyle.ALIGN_CENTER);					//横向居中
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);		//纵向居中

//        style.setBorderTop(CellStyle.BORDER_THIN);					//上细线
//        style.setBorderBottom(CellStyle.BORDER_THIN);				//下细线
//        style.setBorderLeft(CellStyle.BORDER_THIN);					//左细线
//        style.setBorderRight(CellStyle.BORDER_THIN);				//右细线

        return style;
    }

    /**
     * 文字样式
     * @param
     * @return
     */
    public CellStyle text(CellStyle style,Font font){
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short)10);
        style.setFont(font);
        style.setAlignment(CellStyle.ALIGN_LEFT);					//横向居左
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);		//纵向居中

//        style.setBorderTop(CellStyle.BORDER_THIN);					//上细线
//        style.setBorderBottom(CellStyle.BORDER_THIN);				//下细线
//        style.setBorderLeft(CellStyle.BORDER_THIN);					//左细线
//        style.setBorderRight(CellStyle.BORDER_THIN);				//右细线

        return style;
    }

    public void initBatteryVolumeExcle(){
        String rootPath = Environment.getExternalStorageDirectory() + File.separator + "battery_volume.xls";
        File file = new File(rootPath);
        if (file.exists()){
            return;
        }
        int rowNum = 0;//行号
        int cellNum = 0;//单元格下标
        Cell cell;
        Row row;
        Workbook workbook = new HSSFWorkbook();// 创建工作簿
        Sheet sheet = workbook.createSheet();// 创建工作表
        row = sheet.createRow(rowNum);
        row.setHeightInPoints(28f);// 设置行高
        String[] titles = {"序号", "记录时间","电量"};
        // 创建单元格对象，设置内容与样式
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        for (String title : titles) {
            cell = row.createCell(cellNum++);
            cell.setCellValue(title);
            cell.setCellStyle(title(style,font));
        }
        try {
            workbook.write(new FileOutputStream(file));
            workbook.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    static int count = 0;
    public void writeExcel(Battery battery){
        try {
            FileInputStream in = new FileInputStream(Environment.getExternalStorageDirectory() + File.separator + "battery_volume.xls");
            Workbook workbook = new HSSFWorkbook(in);
            Sheet sheet = workbook.getSheetAt(0);
            Row row;
            FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "battery_volume.xls");
            row = sheet.createRow((short)(sheet.getLastRowNum()+1)); //在现有行号后追加数据
            row.createCell(0).setCellValue(count++); //设置第一个（从0开始）单元格的数据
            row.createCell(1).setCellValue(DateFormatUtil.getNowDate()); //设置第二个（从0开始）单元格的数据
            row.createCell(2).setCellValue(battery.getBatteryVolume() + "%"); //设置第二个（从0开始）单元格的数据
            out.flush();
            workbook.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (e.getMessage().contains("6553")){
                Global.enable = false;
            }
        }

    }
}
