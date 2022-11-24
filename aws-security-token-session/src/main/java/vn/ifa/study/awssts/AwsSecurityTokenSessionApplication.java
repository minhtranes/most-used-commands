package vn.ifa.study.awssts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class AwsSecurityTokenSessionApplication implements CommandLineRunner {

    public static void main(final String[] args) {

        SpringApplication.run(AwsSecurityTokenSessionApplication.class, args);
    }

    @Value("${credential.read-default-filename}")
    private String defaultFilename;

    @Value("${credential.write-default-profile}")
    private String defaultProfile;

    private final ObjectMapper mapper = new ObjectMapper();

    @Bean
    @ConfigurationProperties(prefix = "mappings")
    Map<String, String> mappings() {

        return new HashMap<>();
    }

    @Override
    public void run(final String... args) throws Exception {

        final String filename = args.length > 0 ? args[0] : defaultFilename;
        final String profile = args.length > 1 ? args[1] : defaultProfile;

        final File credentialFile = Path.of(filename)
                .toFile();

        final String homeDir = System.getProperty("user.home");
        final File awsCredentialFile = Path.of(homeDir, ".aws", "credentials")
                .toFile();

        if (!credentialFile.exists()) {
            log.warn("Credential file does not exist: {}", credentialFile.getAbsolutePath());
            return;
        }

        if (!awsCredentialFile.exists()) {
            log.warn("AWS credential file does not exist: {}", awsCredentialFile.getAbsolutePath());
            return;
        }

        final JsonNode credentialNode = mapper.readTree(credentialFile);
        final JsonNode credValueNode = credentialNode.findValue("Credentials");
        final Map<String, String> credentialMap = mapper.convertValue(credValueNode,
                                                                      new TypeReference<Map<String, String>>() {
                                                                      });

        credentialMap.forEach((k, v) -> log.info("{}:{}", k, v));

        writeCredential(credentialMap, profile, awsCredentialFile);
    }

    private void writeCredential(
        final Map<String, String> credentialMap,
        final String profile,
        final File awsCredentialFile) throws IOException {

        final Map<String, Map<String, String>> content = new HashMap<>();
        @SuppressWarnings("unchecked")
        final List<String> lines = FileUtils.readLines(awsCredentialFile);

        if (lines != null) {
            log.warn("Build current content");

            Map<String, String> profileBody = null;

            for (final String line : lines) {

                if (line.startsWith("[")) {
                    profileBody = new HashMap<>();
                    content.put(line, profileBody);
                } else {
                    final String[] strings = line.split(" = ");

                    if (strings.length > 1) {

                        if (profileBody == null) {
                            profileBody = new HashMap<>();
                            content.put("NO_PROFILE", profileBody);
                        }

                        profileBody.put(strings[0], strings[1]);
                    }

                }

            }

        }

        final String profileKey = String.format("[%s]", profile);
        final Map<String, String> map = Optional.ofNullable(content.get(profileKey))
                .orElseGet(() -> {
                    final Map<String, String> m = new HashMap<>();
                    content.put(profileKey, m);
                    return m;
                });

        mappings().forEach((k, v) -> {
            map.put(v, credentialMap.get(k));
        });

        final List<String> liness = content.entrySet()
                .stream()
                .map(e -> {
                    final List<String> entries = new ArrayList<>();

                    if (!"NO_PROFILE".equals(e.getKey())) {
                        entries.add(e.getKey());
                    }

                    e.getValue()
                            .entrySet()
                            .stream()
                            .map(i -> String.format("%s = %s", i.getKey(), i.getValue()))
                            .forEach(entries::add);
                    return entries;
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
        writeProfile(awsCredentialFile, liness);
    }

    private void writeProfile(final File awsCredentialFile, final List<String> entr) {

        try {
            FileUtils.writeLines(awsCredentialFile, entr);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }

    }

}
