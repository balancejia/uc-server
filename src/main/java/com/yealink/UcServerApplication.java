package com.yealink;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.yealink.*")
@ServletComponentScan
@MapperScan(basePackages="com.yealink.uc.dao")
public class UcServerApplication {
	 
	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(UcServerApplication.class);
		springApplication.addListeners(new ApplicationPidFileWriter("uc-server.pid"));
		springApplication.run(args);
	}
}
