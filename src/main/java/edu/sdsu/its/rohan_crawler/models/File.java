package edu.sdsu.its.rohan_crawler.models;

/**
 * Models Files in the File System.
 *
 * @author Tom Paulus
 *         Created on 9/14/15.
 */
public class File {
    public String file_name;
    public String file_path;
    public String file_size;

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path.replace(System.getenv("HOME"), "~");
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }
}
