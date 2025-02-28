package mako3.found.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

// based on https://spring.pleiades.io/guides/gs/uploading-files
@Component
public class FileSystemStorageService {

    private static Log logger = LogFactory.getLog(FileSystemStorageService.class);

    private Path rootLocation = Paths.get("upload-dir");

    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    public Path load(String fileName) {
        return rootLocation.resolve(fileName);
    }

    public void store(MultipartFile file, String filename) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(filename)).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new RuntimeException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
                logger.info(String.format("succeeded to store file %s", filename));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

}
