package org.hl7.fhir.validation.cli.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.util.Locale;

import org.hl7.fhir.utilities.filesystem.ManagedFileAccess;
import org.hl7.fhir.validation.cli.model.CliContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParamsTests {
  @Test
  void testLocale() throws Exception {
    CliContext cliContext = Params.loadCliContext(new String[]{"-locale", "de"});
    assertEquals(Locale.GERMAN, cliContext.getLocale());
  }

  @Test
  void testFhirSettingsFile() throws Exception {
    File tempFile = ManagedFileAccess.fromPath(Files.createTempFile("fhir-settings", "json"));
    CliContext cliContext = Params.loadCliContext(new String[]{"-fhir-settings", tempFile.getAbsolutePath()});
    assertEquals(tempFile.getAbsolutePath(), cliContext.getFhirSettingsFile());
  }

  @Test
  void testFhirSettingsFileDoesntExist() {

    java.lang.Error error = Assertions.assertThrows(java.lang.Error.class, () -> {
      CliContext cliContext = Params.loadCliContext(new String[]{"-fhir-settings", "this-does-not-exist.json"});
    });
    assertThat(error.getMessage()).contains("this-does-not-exist.json");
  }
}