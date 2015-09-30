package edu.sdsu.its.rohan_crawler;

import edu.sdsu.its.rohan_crawler.models.File;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Index files on the local file system.
 *
 * @author Tom Paulus
 *         Created on 9/14/15.
 */
public class Index {
    /**
     * Add files from the local file system to the DB.
     *
     * @param files {@link List} List of all files on the file system.
     */


    public static void add_to_db(final List<File> files) {
        for (File f : files) {
            DB.getInstance().addFile(f);
        }
    }

    public static void main(final String[] args) {
        add_to_db(new Index().list_files("/nas/streaming/faculty/ondemand/user/"));
        DB.getInstance().clean();
    }

    /**
     * List all files (including those in sub-folders) in the given directory.
     *
     * @param directory {@link String} The directory to start indexing.
     * @return {@link String} List of all files in and under the supplied directory.
     */
    public List<File> list_files(final String directory) {
        final List<File> list = new ArrayList<File>();
        for (java.io.File sys_file : new Crawl().listf(directory)) {
            if (!sys_file.getAbsolutePath().contains("/.") && sys_file.isFile()) {
                boolean whitelisted_file = false;

                String extension = "";
                int i = sys_file.getName().lastIndexOf('.');
                int p = Math.max(sys_file.getName().lastIndexOf('/'), sys_file.getName().lastIndexOf('\\'));

                if (i > p) {
                    extension = sys_file.getName().substring(i + 1);
                }

                Logger.getLogger(getClass()).debug(String.format("File %s has extension %s", sys_file.getName(), extension));

                for (String wl_extension : new Config().getFile_whitelist()) {
                    if (extension.contains(wl_extension)) {
                        whitelisted_file = true;
                        break;
                    }
                }


                if (whitelisted_file) {
                    File file = new File();
                    file.setFile_name(sys_file.getName());
                    file.setFile_path(sys_file.getAbsolutePath());
                    file.setFile_size(new Crawl().sizef(sys_file.getAbsolutePath()));
                    list.add(file);
                } else {
                    Logger.getLogger(getClass()).info("Rejecting File - Non-Whitelisted Extension");
                }
            }
        }
        Logger.getLogger(getClass()).info(String.format("Found %d files in the specified directory", list.size()));

        return list;
    }
}
