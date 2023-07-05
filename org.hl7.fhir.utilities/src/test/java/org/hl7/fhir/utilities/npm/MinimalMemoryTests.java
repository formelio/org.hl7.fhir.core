package org.hl7.fhir.utilities.npm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.hl7.fhir.utilities.Utilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MinimalMemoryTests {

  @Test
  public void testFetch() throws IOException {
    File folder = new File(Utilities.path("[tmp]", ".fhir-mm"));
    if (folder.exists()) {
      Utilities.clearDirectory(folder.getAbsolutePath());
    } else {
      Utilities.createDirectory(folder.getAbsolutePath());
    }
    FilesystemPackageCacheManager pcm = new FilesystemPackageCacheManager(folder.getAbsolutePath());
    pcm.setMinimalMemory(true);
    NpmPackage npm = pcm.loadPackage("hl7.fhir.us.core", "5.0.0");
    Assertions.assertTrue(npm.isMinimalMemory());
  }

}
