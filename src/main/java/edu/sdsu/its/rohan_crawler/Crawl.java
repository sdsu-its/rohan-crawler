package edu.sdsu.its.rohan_crawler;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Directly interact with the File System.
 *
 * @author Tom Paulus
 *         Created on 9/14/15.
 */

public class Crawl {
    public static void main(final String[] args) {
        Crawl crawl = new Crawl();
        List<File> files = crawl.listf(System.getenv("HOME") + "/Desktop/test/");

        for (File f : files) {
            System.out.printf("Found file with name %s at %s\n", f.getName(), f.getAbsoluteFile());
            System.out.printf("\tFile has size %s\n", crawl.sizef(f.getAbsolutePath()));
        }
    }

    /**
     * List all files and folders in a given directory.
     *
     * @param directoryName {@link String} Directory to list files and folders for.
     * @return {@link List} Files in the Directory.
     */
    public List<File> listf(final String directoryName) {
        final File directory = new File(directoryName);
        final List<File> resultList = new ArrayList<File>();

        final File[] fList = directory.listFiles();
        if (fList != null) {
            resultList.addAll(Arrays.asList(fList));

            for (File file : fList) {
                if (file.isDirectory()) {
                    resultList.remove(file);
                    resultList.addAll(listf(file.getAbsolutePath()));
                }
            }

        }
        return resultList;
    }

    /**
     * Get the file size for a file.
     * Uses the largest unit that fits well.
     *
     * @param absoluteFilePath {@link String} Path for file to get the size for.
     * @return {@link String} The size of the file as a formatted String, includes the units.
     */
    public String sizef(final String absoluteFilePath) {
        final File file = new File(absoluteFilePath);
        String file_size;

        if (file.exists()) {

            final double bytes = file.length();
            final double kilobytes = (bytes / 1024.0);
            final double megabytes = (kilobytes / 1024.0);
            final double gigabytes = (megabytes / 1024.0);

            if (gigabytes >= 1) {
                file_size = String.format("%.2f GB", gigabytes);
            } else if (megabytes >= 1) {
                file_size = String.format("%.2f MB", megabytes);
            } else if (kilobytes >= 1) {
                file_size = String.format("%.2f KB", kilobytes);
            } else if (bytes >= 1) {
                file_size = String.format("%.0f B", bytes);
            } else {
                file_size = "";
            }

        } else {
            file_size = "";
            Logger.getLogger(getClass()).warn(String.format("Could not get file size for %s. File not found.", absoluteFilePath));
        }

        return file_size;
    }
}

