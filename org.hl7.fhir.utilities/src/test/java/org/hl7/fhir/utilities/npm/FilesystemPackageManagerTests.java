package org.hl7.fhir.utilities.npm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.hl7.fhir.utilities.IniFile;
import org.hl7.fhir.utilities.filesystem.ManagedFileAccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class FilesystemPackageManagerTests {

  private static final String DUMMY_URL_1 = "https://dummy1.org";
  private static final String DUMMY_URL_2 = "https://dummy2.org";

  private static final String DUMMY_URL_3 = "https://dummy3.org";

  private static final String DUMMY_URL_4 = "https://dummy4.org";
  private final List<PackageServer> dummyPrivateServers = List.of(
     new PackageServer(DUMMY_URL_1),
     new PackageServer(DUMMY_URL_2)
  );

  private final List<PackageServer> dummyDefaultServers = List.of(
    new PackageServer(DUMMY_URL_3),
    new PackageServer(DUMMY_URL_4)
  );



  @Test
  public void testDefaultServers() throws IOException {
    FilesystemPackageCacheManager filesystemPackageCacheManager = getFilesystemPackageCacheManager(false);

    assertEquals(4, filesystemPackageCacheManager.myPackageServers.size());
    assertEquals(DUMMY_URL_1, filesystemPackageCacheManager.myPackageServers.get(0).getUrl());
    assertEquals(DUMMY_URL_2, filesystemPackageCacheManager.myPackageServers.get(1).getUrl());
    assertEquals(DUMMY_URL_3, filesystemPackageCacheManager.myPackageServers.get(2).getUrl());
    assertEquals(DUMMY_URL_4, filesystemPackageCacheManager.myPackageServers.get(3).getUrl());
  }

  @Test
  public void testIgnoreDefaultServers() throws IOException {
    FilesystemPackageCacheManager filesystemPackageCacheManager = getFilesystemPackageCacheManager(true);

    assertEquals(2, filesystemPackageCacheManager.myPackageServers.size());
    assertEquals(DUMMY_URL_1, filesystemPackageCacheManager.myPackageServers.get(0).getUrl());
    assertEquals(DUMMY_URL_2, filesystemPackageCacheManager.myPackageServers.get(1).getUrl());
  }

  @Nonnull
  private FilesystemPackageCacheManager getFilesystemPackageCacheManager(final boolean ignoreDefaultPackageServers) throws IOException {

    FilesystemPackageCacheManager.Builder builder = new FilesystemPackageCacheManager.Builder() {
      protected boolean isIgnoreDefaultPackageServers() {
        return ignoreDefaultPackageServers;
      }

      @Nonnull
      protected List<PackageServer> getDefaultServers() {
        return dummyDefaultServers;
      }

      protected List<PackageServer> getConfiguredServers() {
        return dummyPrivateServers;
      }
    };

    return builder.build();

  }

  @Test
  public void testUserCacheDirectory() throws IOException {
    FilesystemPackageCacheManager filesystemPackageCacheManager = new FilesystemPackageCacheManager.Builder().build();
    assertEquals(System.getProperty("user.home") + File.separator + ".fhir" + File.separator + "packages", filesystemPackageCacheManager.getFolder());
  }

  /*
    Targeted folder will only be valid on -nix style systems.
   */
  @Test
  @DisabledOnOs(OS.WINDOWS)
  public void testSystemCacheDirectory() throws IOException {
    File folder = new FilesystemPackageCacheManager.Builder().withSystemCacheFolder().getCacheFolder();
    assertEquals( "/var/lib/.fhir/packages", folder.getAbsolutePath());
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  public void testSystemCacheDirectoryWin() throws IOException {
    File folder = new FilesystemPackageCacheManager.Builder().withSystemCacheFolder().getCacheFolder();
    assertEquals( System.getenv("ProgramData") + "\\.fhir\\packages", folder.getAbsolutePath());
  }

  /**
    We repeat the same tests multiple times here, in order to catch very rare edge cases.
   */
  public static Stream<Arguments> packageCacheMultiThreadTestParams() {
    List<Arguments> params = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      params.add(Arguments.of(100, 1));
      params.add(Arguments.of(10,10));
      params.add(Arguments.of(100, 10));
    }
    return params.stream();
  }

  private void createDummyTemp(File cacheDirectory, String lowerCase) throws IOException {
    createDummyPackage(cacheDirectory, lowerCase);
  }

  private void createDummyPackage(File cacheDirectory, String packageName, String packageVersion) throws IOException {
    String directoryName = packageName + "#" + packageVersion;
    createDummyPackage(cacheDirectory, directoryName);
  }

  private static void createDummyPackage(File cacheDirectory, String directoryName) throws IOException {
    File packageDirectory = ManagedFileAccess.file(cacheDirectory.getAbsolutePath(), directoryName);
    packageDirectory.mkdirs();

    File dummyContentFile = ManagedFileAccess.file(packageDirectory.getAbsolutePath(), "dummy.txt");
    FileWriter wr = new FileWriter(dummyContentFile);
    wr.write("Ain't nobody here but us chickens");
    wr.flush();
    wr.close();
  }

  private void assertThatDummyTempExists(File cacheDirectory, String dummyTempPackage) throws IOException {
    File dummyTempDirectory = ManagedFileAccess.file(cacheDirectory.getAbsolutePath(), dummyTempPackage);
    assertThat(dummyTempDirectory).exists();

    File dummyContentFile = ManagedFileAccess.file(dummyTempDirectory.getAbsolutePath(), "dummy.txt");
    assertThat(dummyContentFile).exists();
  }

  @Test
  public void testCreatesIniIfDoesntExistAndCacheStaysIntact() throws IOException {
    File cacheDirectory = ManagedFileAccess.fromPath(Files.createTempDirectory("fpcm-multithreadingTest"));
    File cacheIni = ManagedFileAccess.file(cacheDirectory.getAbsolutePath(), "packages.ini");

    createDummyPackage(cacheDirectory, "example.fhir.uv.myig", "1.2.3");

    String dummyTempPackage = UUID.randomUUID().toString().toLowerCase();
    createDummyTemp(cacheDirectory, dummyTempPackage);
    assertThatDummyTempExists(cacheDirectory, dummyTempPackage);

    assertThat(cacheIni).doesNotExist();
    FilesystemPackageCacheManager filesystemPackageCacheManager = new FilesystemPackageCacheManager.Builder().withCacheFolder(cacheDirectory.getAbsolutePath()).build();
    assertInitializedTestCacheIsValid(cacheDirectory, true);
  }



  @Test
  public void testClearsCacheIfVersionIsWrong() throws IOException {
    File cacheDirectory = ManagedFileAccess.fromPath(Files.createTempDirectory("fpcm-multithreadingTest"));
    File cacheIni = ManagedFileAccess.file(cacheDirectory.getAbsolutePath(), "packages.ini");

    createDummyPackage(cacheDirectory, "example.fhir.uv.myig", "1.2.3");
    String dummyTempPackage = UUID.randomUUID().toString().toLowerCase();
    createDummyTemp(cacheDirectory, dummyTempPackage);
    assertThatDummyTempExists(cacheDirectory, dummyTempPackage);


    IniFile ini = new IniFile(cacheIni.getAbsolutePath());
    ini.setStringProperty("cache", "version", "2", null);
    ini.save();

    assertThat(cacheIni).exists();
    FilesystemPackageCacheManager filesystemPackageCacheManager = new FilesystemPackageCacheManager.Builder().withCacheFolder(cacheDirectory.getAbsolutePath()).build();
    assertInitializedTestCacheIsValid(cacheDirectory, false);
  }

  @Test
  public void testCacheStaysIntactIfVersionIsTheSame() throws IOException {
    File cacheDirectory = ManagedFileAccess.fromPath(Files.createTempDirectory("fpcm-multithreadingTest"));
    File cacheIni = ManagedFileAccess.file(cacheDirectory.getAbsolutePath(), "packages.ini");

    createDummyPackage(cacheDirectory, "example.fhir.uv.myig", "1.2.3");
    String dummyTempPackage = UUID.randomUUID().toString().toLowerCase();
    createDummyTemp(cacheDirectory, dummyTempPackage);
    assertThatDummyTempExists(cacheDirectory, dummyTempPackage);


    IniFile ini = new IniFile(cacheIni.getAbsolutePath());
    ini.setStringProperty("cache", "version", "3", null);
    ini.save();

    assertThat(cacheIni).exists();
    FilesystemPackageCacheManager filesystemPackageCacheManager = new FilesystemPackageCacheManager.Builder().withCacheFolder(cacheDirectory.getAbsolutePath()).build();
    assertInitializedTestCacheIsValid(cacheDirectory, true);
  }

  private void assertInitializedTestCacheIsValid(File cacheDirectory, boolean dummyPackageShouldExist) throws IOException {
    assertThat(cacheDirectory).exists();
    File iniFile = ManagedFileAccess.file(cacheDirectory.getAbsolutePath(), "packages.ini");
    assertThat(ManagedFileAccess.file(cacheDirectory.getAbsolutePath(), "packages.ini")).exists();
    IniFile ini = new IniFile(iniFile.getAbsolutePath());
    String version = ini.getStringProperty("cache", "version");
    assertThat(version).isEqualTo("3");

    File[] files = cacheDirectory.listFiles();
    if (dummyPackageShouldExist) {
      // Check that only packages.ini and our dummy package are in the cache. Our previous temp should be deleted.
      assertThat(files).hasSize(2); // packages.ini and example.fhir.uv.myig#1.2.3 (directory)

      File dummyPackage = ManagedFileAccess.file(cacheDirectory.getAbsolutePath(), "example.fhir.uv.myig#1.2.3");
      assertThat(dummyPackage).exists();

      File dummyContentFile = ManagedFileAccess.file(dummyPackage.getAbsolutePath(), "dummy.txt");
      assertThat(dummyContentFile).exists();
    } else {
      // Check that only packages.ini is in the cache.
      assertThat(files).hasSize(1);
    }


  }

  @MethodSource("packageCacheMultiThreadTestParams")
  @ParameterizedTest
  public void packageCacheMultiThreadTest(final int threadTotal, final int packageCacheManagerTotal) throws IOException {

    String pcmPath = ManagedFileAccess.fromPath(Files.createTempDirectory("fpcm-multithreadingTest")).getAbsolutePath();
    FilesystemPackageCacheManager[] packageCacheManagers = new FilesystemPackageCacheManager[packageCacheManagerTotal];
    Random rand = new Random();

    final AtomicInteger totalSuccessful = new AtomicInteger();
    final ConcurrentHashMap successfulThreads = new ConcurrentHashMap();
    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < threadTotal; i++) {
      final int index = i;
      Thread t = new Thread(() -> {
        try {
          System.out.println("Thread #" + index + ": " + Thread.currentThread().getId() + " started");
          final int randomPCM = rand.nextInt(packageCacheManagerTotal);
          final int randomOperation = rand.nextInt(4);
          if (packageCacheManagers[randomPCM] == null) {
            packageCacheManagers[randomPCM] = new FilesystemPackageCacheManager.Builder().withCacheFolder(pcmPath).build();
          }
          FilesystemPackageCacheManager pcm = packageCacheManagers[randomPCM];
          if (randomOperation == 0) {
            pcm.addPackageToCache("example.fhir.uv.myig", "1.2.3", this.getClass().getResourceAsStream("/npm/dummy-package.tgz"), "https://packages.fhir.org/example.fhir.uv.myig/1.2.3");
          } else if (randomOperation == 1) {
            pcm.clear();
          } else if (randomOperation == 2) {
            pcm.loadPackageFromCacheOnly("example.fhir.uv.myig", "1.2.3");
          } else {
            pcm.removePackage("example.fhir.uv.myig", "1.2.3");
          }
          totalSuccessful.incrementAndGet();
          successfulThreads.put(Thread.currentThread().getId(), index);
          System.out.println("Thread #" + index + ": " + Thread.currentThread().getId() + " completed");
        } catch (Exception e) {
          e.printStackTrace();
          System.err.println("Thread #" + index + ": " + Thread.currentThread().getId() + " failed");
        }
      });
      t.start();
      threads.add(t);
    }
    threads.forEach(t -> {
      try {
        t.join();
      } catch (InterruptedException e) {

      }
    });

    printUnsuccessfulThreads(successfulThreads, threads);
    assertEquals(threadTotal, totalSuccessful.get(), "Not all threads were successful.");

  }

  private void printUnsuccessfulThreads(final ConcurrentHashMap successfulThreads, List<Thread> threads) {
    for (Thread t : threads) {
      if (!successfulThreads.containsKey(t.getId())) {
        System.out.println("Thread #" + t.getId() + " failed");
      }
    }
  }
}
