package io.github.sinri.carina.helper;

import io.github.sinri.carina.facade.Carina;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @since 2.6
 */
public class CarinaFileHelper {
    private static final CarinaFileHelper instance = new CarinaFileHelper();

    private CarinaFileHelper() {

    }

    static CarinaFileHelper getInstance() {
        return instance;
    }

    public byte[] readFileAsByteArray(String filePath, boolean seekInsideJarWhenNotFound) throws IOException {
        try {
            return Files.readAllBytes(new File(filePath).toPath());
        } catch (IOException e) {
            if (seekInsideJarWhenNotFound) {
                Buffer buffer = Buffer.buffer();
                try (InputStream resourceAsStream = CarinaFileHelper.class.getClassLoader().getResourceAsStream(filePath)) {
                    while (true) {
                        int c = 0;
                        if (resourceAsStream != null) {
                            c = resourceAsStream.read();
                        } else {
                            throw new IOException("PATH NOT READABLE");
                        }
                        if (c >= 0) {
                            buffer.appendByte((byte) c);
                        } else {
                            break;
                        }
                    }
                    return buffer.getBytes();
                }
            } else {
                throw e;
            }
        }
    }

    /**
     * @param filePath path string of the target file, or directory
     * @return the URL of target file; if not there, null return.
     */
    public URL getUrlOfFileInJar(String filePath) {
        return CarinaFileHelper.class.getClassLoader().getResource(filePath);
    }

    /**
     * Seek in JAR, under the root (exclusive)
     *
     * @param root ends with '/'
     * @return list of JarEntry
     */
    public List<JarEntry> traversalInJar(String root) {
        List<JarEntry> jarEntryList = new ArrayList<>();
        try {
            // should root ends with '/'?
            URL url = CarinaFileHelper.class.getClassLoader().getResource(root);
            if (url == null) {
                throw new RuntimeException("Resource is not found");
            }
            if (!url.toString().contains("!/")) {
                throw new RuntimeException("Resource is not in JAR");
            }
            String jarPath = url.toString().substring(0, url.toString().indexOf("!/") + 2);

            URL jarURL = new URL(jarPath);
            JarURLConnection jarCon = (JarURLConnection) jarURL.openConnection();
            JarFile jarFile = jarCon.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            JarEntry baseJarEntry = jarFile.getJarEntry(root);
            Path pathOfBaseJarEntry = new File(baseJarEntry.getName()).toPath(); // Path.of(baseJarEntry.getName());

            while (jarEntries.hasMoreElements()) {
                JarEntry entry = jarEntries.nextElement();

                Path entryPath = new File(entry.getName()).toPath();
                if (entryPath.getParent() == null) {
                    continue;
                }
                if (entryPath.getParent().compareTo(pathOfBaseJarEntry) == 0) {
                    jarEntryList.add(entry);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jarEntryList;
    }

    /**
     * @return absolute created Temp File path
     * @since 3.0.0
     */
    public Future<String> crateTempFile(String prefix, String suffix) {
        return Carina.getVertx().fileSystem().createTempFile(prefix, suffix);
    }
}
