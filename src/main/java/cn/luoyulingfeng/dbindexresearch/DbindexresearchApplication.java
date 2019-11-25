package cn.luoyulingfeng.dbindexresearch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@MapperScan(basePackages = "cn.luoyulingfeng.dbindexresearch.mapper")
public class DbindexresearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbindexresearchApplication.class, args);
    }

}
