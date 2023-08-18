package com.navapbc.fciv.login.mock.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class FileSystemStorageService implements StorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemStorageService.class);
  private final Path rootLocation;

  @Autowired
  public FileSystemStorageService(StorageProperties properties) {
    LOGGER.debug("File system service root location: {}", properties.getLocation());
    this.rootLocation = Paths.get("/", "tmp", properties.getLocation());
    init();
  }

  @Override
  public void init() {
    try {
      if (!Files.exists(rootLocation)) {
        LOGGER.debug("Create root location");
        Files.createDirectories(rootLocation);
      }else {
        LOGGER.debug("Use existing root location");
      }
    } catch (IOException e) {
      throw new StorageException("Could not initialize storage", e);
    }
  }

  @Override
  public void store(String fileName, byte[] content) {
    try {
      if (content == null || content.length == 0) {
        throw new StorageException("Failed to store empty file.");
      }
      Path destinationFile = this.rootLocation.resolve(
              Paths.get(fileName))
          .normalize().toAbsolutePath();
      if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
        // This is a security check
        throw new StorageException(
            "Cannot store file outside current directory.");
      }
      Files.write(destinationFile, content, StandardOpenOption.TRUNCATE_EXISTING);

    } catch (IOException e) {
      throw new StorageException("Failed to store file.", e);
    }
  }

  @Override
  public Path load(String fileName) {
    return rootLocation.resolve(fileName);
  }

  @Override
  public Resource loadAsResource(String fileName) {
    try {
      Path file = load(fileName);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new StorageException(
            "Could not read file: " + fileName);

      }
    } catch (MalformedURLException e) {
      throw new StorageException("Could not read file: " + fileName, e);
    }
  }
}
