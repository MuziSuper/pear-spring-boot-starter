package cn.muzisheng.pear;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.muzisheng.pear.mapper")
public class SpringBootStarterPearApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootStarterPearApplication.class, args);
	}

}
