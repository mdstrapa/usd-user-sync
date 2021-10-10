package br.com.sicredi.usdusersync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class UsdUserSyncApplication {

	static App app = new App();

	public static void main(String[] args) {

		SpringApplication.run(UsdUserSyncApplication.class, args);

		log.info("Program start ===================");

		app.processInputFile();

		log.info("Program end =====================");
	}

}
