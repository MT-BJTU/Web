package com.service;

import com.utils.JdbcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.OutputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Component
@EnableScheduling
public class Schedule {

    @Autowired
    private JdbcUtils jdbcUtils;

    @Value("${server.resource}")
    private String resourcePath;

    @Value("root")
    private String dbServerUsername;

    @Value("8.130.76.6")
    private String dbServerHostname;

    @Scheduled(cron = "0 0 18 * * ?")
    public void backUpDataBase() {
        System.out.println("======执行定时器:定时备份数据库=======");
        String backUpPath = resourcePath + "/sql/" + Date.valueOf(LocalDate.now());
        File backUpFile = new File(backUpPath);
        if (!backUpFile.exists()) {
            backUpFile.mkdirs();
        }
        File dataFile = new File(backUpPath + "/campusportal" + System.currentTimeMillis() + ".sql");
        //拼接备份命令
        StringBuilder sb = new StringBuilder();
        Map<String, String> dbInfo = jdbcUtils.getDBInfo();
        sb.append("sshpass -p '").append("GC20011228sh!").append("' ssh ")
                .append(dbServerUsername)
                .append("@")
                .append(dbServerHostname)
                .append(" mysqldump")
                .append(" -u").append(dbInfo.get("userName"))
                .append(" -p").append(dbInfo.get("passWord"))
                .append(" ").append(dbInfo.get("dbName")).append(" > ")
                .append(dataFile.getAbsolutePath());
        System.out.println("======数据库备份命令为：" + sb.toString() + "=======");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", sb.toString());
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("======数据库备份成功，路径为：" + dataFile + "=======");
            }
        } catch (Exception e) {
            System.out.println("======数据库备份失败，异常为：" + e.getMessage() + "=======");
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteBackUpDataBase() {
        System.out.println("======执行定时器:定时删除备份数据库文件=======");
        String backUpPath = resourcePath + "/sql";
        File backUpFile = new File(backUpPath);
        if (backUpFile.exists()) {
            File[] files = backUpFile.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    LocalDate date1 = LocalDate.parse(file.getName());
                    LocalDate date2 = LocalDate.now();
                    long daysBetween = ChronoUnit.DAYS.between(date1, date2);
                    if (daysBetween > 7) {
                        File[] subFiles = file.listFiles();
                        for (File subFile : subFiles) {
                            subFile.delete();
                        }
                        file.delete();
                    }
                }
            }
        }
    }
}
