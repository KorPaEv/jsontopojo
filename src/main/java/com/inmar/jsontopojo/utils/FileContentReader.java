package com.inmar.jsontopojo.utils;

import com.sun.codemodel.JCodeModel;
import lombok.SneakyThrows;
import org.jsonschema2pojo.*;
import org.jsonschema2pojo.rules.RuleFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Scanner;

@Component

public class FileContentReader implements CommandLineRunner {

    @Override
    @SneakyThrows
    public void run(String... args) {
        System.out.println("Enter the input path to JSON file:");
        String inputPathToFile = readPath();
        System.out.println("Input path to file: " + inputPathToFile);
        System.out.println("Enter the output path to JAVA CLASS file:");
        File outputPathDirectory = new File(readPath());
        System.out.println("Input path to file: " + outputPathDirectory);
        String jsonData;
        File file = new File(inputPathToFile);
        if (file.isFile()) {
            jsonData = new String(Files.readAllBytes(file.toPath()));
            System.out.println("File data:\n" + jsonData);
            URL url = file.toURI().toURL();
            convertJsonToJavaClass(url, outputPathDirectory, "com.inmar", "Test");
        }
    }

    private String readPath() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public void convertJsonToJavaClass(
            URL inputJsonUrl,
            File outputJavaClassDirectory,
            String packageName,
            String javaClassName
    ) throws IOException {
        JCodeModel jcodeModel = new JCodeModel();

        GenerationConfig config = new DefaultGenerationConfig() {
            @Override
            public boolean isGenerateBuilders() {
                return true;
            }

            @Override
            public SourceType getSourceType() {
                return SourceType.JSON;
            }
        };

        SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()), new SchemaGenerator());
        mapper.generate(jcodeModel, javaClassName, packageName, inputJsonUrl);

        jcodeModel.build(outputJavaClassDirectory);
    }
}
