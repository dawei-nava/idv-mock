package com.navapbc.fciv.login.mock.services;

import org.springframework.core.io.Resource;

import java.nio.file.Path;

public interface StorageService {
  void init();
  void store(String fileName, byte [] content);

  Path load(String fileName);

  Resource loadAsResource(String fileName);
}
