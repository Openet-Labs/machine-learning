package com.openet.labs.ml.autoscale;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.ParentNotDirectoryException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.log4j.Logger;

public class FileSystem {

    private static final Logger LOGGER = Logger.getLogger(FileSystem.class);
    private org.apache.hadoop.fs.FileSystem fs;
    private static final String LINK_MAGIC = "com.openet.enigma.link.to:";
    

    public FileSystem() throws IOException, URISyntaxException {
        YarnClient yarnClient = YarnClient.createYarnClient();
        final YarnConfiguration yarnConfiguration = new YarnConfiguration();
        yarnClient.init(yarnConfiguration);
        yarnClient.start();
        String fsUri = yarnConfiguration.get(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, CommonConfigurationKeysPublic.FS_DEFAULT_NAME_DEFAULT);
        fs = org.apache.hadoop.fs.FileSystem.get(new URI(fsUri), new Configuration());
    }
    
    public FileSystem(String uri) throws IOException, URISyntaxException {
        fs = org.apache.hadoop.fs.FileSystem.get(new URI(uri), new Configuration());
    }
    
    public int move(String src, String dst) throws IllegalArgumentException, IOException {
        int fileCount = 0;
        FileStatus[] fileStat = fs.globStatus(new Path(src));
        if(fileStat == null || fileStat.length == 0) {
            
        } else if(fileStat.length == 1 && !fs.isDirectory(new Path(dst))) {
            Path srcPath = fileStat[0].getPath();
            Path dstPath = new Path(dst);
            LOGGER.debug("Moving file " + srcPath + " to " + dstPath);
            fs.rename(srcPath, dstPath);
            ++fileCount;
        } else {
            for(FileStatus fileStatus : fileStat) {
                Path srcPath = fileStatus.getPath();
                Path dstPath = new Path(dst, srcPath.getName());
                LOGGER.debug("Moving file " + srcPath + " to " + dstPath);
                fs.rename(srcPath, dstPath);
                ++fileCount;
            }
        }
        return fileCount;
    }

    public void mkdirs(String path) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, IllegalArgumentException, IOException {
        fs.mkdirs(new Path(path));
    }

    
    
    public void createLink(String target, String linkName) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, IllegalArgumentException, IOException {
        //fs.createSymlink(new Path(target), new Path(linkName), true);
        // XXX symlinks are disabled in current HDFS implementation
        // We need to create out onw implementation of it
        FSDataOutputStream link = fs.create(new Path(linkName));
        link.writeUTF(LINK_MAGIC + target);
        link.flush();
        link.close();
    }
    
    public String resolvePath(String path) throws IllegalArgumentException, IOException {
        // return fs.resolvePath(new Path(path));
        if(!fs.exists(new Path(path)) || isDirectory(path))
            return path;
        FSDataInputStream link = null;
        String ret;
        try {
            link = fs.open(new Path(path));
            String content = link.readUTF(); 
            ret = content.startsWith(LINK_MAGIC) ? content.substring(LINK_MAGIC.length()) : path;
        } catch(EOFException e) {
            ret = path;
        } finally {
            if(link != null)
                link.close();
        }
        return ret;
    }

    public boolean deleteFile(String path) throws IllegalArgumentException, IOException {
        return fs.delete(new Path(path), true);
    }

    public boolean isDirectory(String path) throws IllegalArgumentException, IOException {
        return fs.isDirectory(new Path(path));
    }

    public boolean isFile(String path) throws IllegalArgumentException, IOException {
        return fs.isFile(new Path(path));
    }

    public boolean isLink(String path) throws IllegalArgumentException, IOException {
//        return fs.exists(new Path(path)) && !isFile(path) && !isDirectory(path);
        return !path.equals(resolvePath(path));
    }

    public List<File> listDir(String path) throws FileNotFoundException, IllegalArgumentException, IOException {
        FileStatus[] fileStatuses = fs.listStatus(new Path(path));
        ArrayList<File> ret = new ArrayList<>();
        for(FileStatus fileStatus : fileStatuses) {
            ret.add(new File(path, fileStatus.getPath().getName()));
        }
        return ret;
    }

    public boolean testFiles(String path) {
        try {
            return fs.globStatus(new Path(path)).length > 0;
        } catch (IllegalArgumentException | IOException e) {
            return false;
        }
    }

    public List<String> glob(String path) throws IllegalArgumentException, IOException {
        FileStatus[] files = fs.globStatus(new Path(path));
        List<String> list = new ArrayList<>();
        if(files != null) {
            for(FileStatus f : files) {
                list.add(Path.getPathWithoutSchemeAndAuthority(f.getPath()).toString());
            }
        }
        return list;
    }
    
    public OutputStream openFileForWriting(String path) throws IllegalArgumentException, IOException {
        return fs.create(new Path(path));
    }
    public InputStream openFileForReading(String path) throws IllegalArgumentException, IOException {
        return fs.open(new Path(path));
    }
    
    /**
     * Method to save a @see Serializable object to HDFS
     * @param object A Serializable object to be saved to HDFS
     * @param path The path where the object should be saved to
     * @return True if the object could be saved. False if the path doesn't exist or there was an error saving the object
     */
    public boolean save(Object object, String path) {
        OutputStream out;
        try {
            out = openFileForWriting(path);
            ObjectOutputStream objWriter = new ObjectOutputStream(out);
            objWriter.writeObject(object);
        } catch (IllegalArgumentException | IOException e) {
            LOGGER.error("Could not save the object", e);
            return false;
        }

        return true;
    }
    /**
     * Load a @see Serializable object from disk
     * @param path The path where the object was previously saved 
     * @return The object, or null if it couldn't be read from disk or doesn't exist
     */
    public Object load(String path) {
        try {
            InputStream in = openFileForReading(path);
            ObjectInputStream objReader = new ObjectInputStream(in);
            return objReader.readObject();
        } catch (Exception ex) {
            //expected when the object hasn't been created yet.
        }
        
        return null;
    }
    
    public void copyFromLocalFile(String src, String dst) throws IllegalArgumentException, IOException {
        fs.copyFromLocalFile(false, true, new Path(src), new Path(dst));
    }
    
    public FileChecksum getFileChecksum(String path) throws IOException {
        return fs.getFileChecksum(new Path(path));
    }
    
    public long getFileLen(String path) throws IOException {
        return fs.getFileStatus(new Path(path)).getLen();
    }
    
    public long getModificationTime(String path) throws IOException {
        return fs.getFileStatus(new Path(path)).getModificationTime();
    }
    
    public boolean isFileExist(String path) throws IOException {
        return fs.exists(new Path(path));
    }
}
