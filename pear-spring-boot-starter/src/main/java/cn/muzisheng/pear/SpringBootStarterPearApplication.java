package cn.muzisheng.pear;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
@MapperScan("cn.muzisheng.pear.mapper")
@ComponentScan(basePackages = {"cn.muzisheng.pear.service","cn.muzisheng.pear.configure","cn.muzisheng.pear.properties","cn.muzisheng.pear.dao","cn.muzisheng.pear.utils"})
public class SpringBootStarterPearApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootStarterPearApplication.class, args);
	}

}
