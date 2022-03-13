package wiki.justreddy.ga.reddyutils.dependency;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Plays the role of writing JAR files at the given directory.
 * @author Yizhong Ding
 */
public class FileWriter implements AutoCloseable {

    // OutputStream specifies the output file.
    private OutputStream outputStream;
    private static final int CHUNK_SIZE = 1024;

    /**
     * Write the InputStream as a file.
     * @param inputStream The Jar file to be written on the disk.
     * @param length The length of the file.
     * @return return total bytes that has been written.
     * @throws ArtifactResolveException throws an exception if write fails.
     */
    public long write(InputStream inputStream, double length) throws ArtifactResolveException {
        if(outputStream == null) throw new ArtifactResolveException("Error: Please specify output stream!");
        try (BufferedInputStream input = new BufferedInputStream(inputStream)) {
            byte[] dataBuffer = new byte[CHUNK_SIZE];
            int readBytes;
            long totalBytes = 0;
            while ((readBytes = input.read(dataBuffer)) != -1) {
                totalBytes += readBytes;
                outputStream.write(dataBuffer, 0, readBytes);
            }
            return totalBytes;
        } catch (IOException e) {
            throw new ArtifactResolveException("Error: Failed to write files!");
        }
    }

    /**
     * Getter for setOutputStream. Indicates the output Jar file.
     * @param outputStream input outputStream
     */
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Getter for setOutputStream.
     * @return the outputStream instance
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Automatically close the outputStream after saving the JAR file.
     */
    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}