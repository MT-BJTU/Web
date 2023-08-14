package mycom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan("mycom.mapper")
public class CurriculumDesignApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurriculumDesignApplication.class, args);
    }

}
