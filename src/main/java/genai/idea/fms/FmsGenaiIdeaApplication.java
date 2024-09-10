package genai.idea.fms;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@SpringBootApplication
public class FmsGenaiIdeaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FmsGenaiIdeaApplication.class, args);

        // 종료 훅 추가
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                // OS에 따라 다른 명령어 사용
                String os = System.getProperty("os.name").toLowerCase();
                ProcessBuilder processBuilder;
                if (os.contains("win")) {
                    processBuilder = new ProcessBuilder("cmd", "/c", "gradlew.bat", "composeDown");
                } else {
                    processBuilder = new ProcessBuilder("./gradlew", "composeDown");
                }
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                // 로그를 리다이렉트하여 터미널에 출력
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }

                process.waitFor();
            } catch (Exception e) {
                System.err.println("Gradle task composeDown 실행 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }));
    }
}
