package org.example;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Slf4j
class FileCharsetTests {
    static final Charset GBK = Charset.forName("GBK");
    static final String DATA = "测试数据";
    static final String SUFFIX = ".txt";
    static final int BUFFER_SIZE = 8192;

    @Test
    void test() throws IOException {
        var tempFile = createTempFile(GBK);
        Files.writeString(tempFile.toPath(), DATA, GBK);

        doWrite(tempFile, StandardCharsets.UTF_8, true);
        doWrite(tempFile, StandardCharsets.UTF_16BE, false);
        doWrite(tempFile, StandardCharsets.UTF_16LE, false);
    }

    File createTempFile(Charset charsetToWrite) throws IOException {
        var f = File.createTempFile(charsetToWrite + "-", SUFFIX);
        log.info("createTempFile: {}", f);
        return f;
    }

    void doWrite(File tempFile, Charset charsetToWrite, boolean transferTo) throws IOException {
        try (var reader = getReaderByGBK(tempFile);
             var writer = new FileWriter(createTempFile(charsetToWrite), charsetToWrite)) {
            if (transferTo) {
                reader.transferTo(writer);
            } else {
                doCopy(reader, writer);
            }
        }
    }

    InputStreamReader getReaderByGBK(File source) throws FileNotFoundException {
        return new InputStreamReader(new FileInputStream(source), GBK);
    }

    /**
     * refer to {@link Reader#transferTo(Writer)}
     * but avoid unnecessary transferred counting
     *
     * @param reader reader
     * @param writer writer
     * @throws IOException IOException
     */
    void doCopy(InputStreamReader reader, OutputStreamWriter writer) throws IOException {
        int len;
        var buf = new char[BUFFER_SIZE];
        while ((len = reader.read(buf, 0, BUFFER_SIZE)) >= 0) {
            writer.write(buf, 0, len);
        }
    }

}