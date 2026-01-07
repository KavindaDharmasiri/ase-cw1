package lk.ase.kavinda.islandlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IslandlinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(IslandlinkApplication.class, args);
	}

}
