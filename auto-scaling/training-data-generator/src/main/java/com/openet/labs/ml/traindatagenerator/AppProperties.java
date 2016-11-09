package com.openet.labs.ml.traindatagenerator;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppProperties {

    protected final String resourceName;
    protected Properties props;
    
    public AppProperties(String resourceName) throws IOException {
        this.resourceName = resourceName;        
        loadResource();
    }

    public AppProperties() throws IOException {
        this("application.properties");
    }

    private void loadResource() throws IOException {
        //System.out.println(currentPath());
        File file = new File(currentPath().concat(File.separator).concat(resourceName));
        props = new Properties();
        
        if (file.exists()) {
            Logger.getLogger(AppProperties.class.getName()).log(Level.INFO, resourceName.concat(" resource found in the current path."));            
            props.load(new FileReader(file));
        } else {
            Logger.getLogger(AppProperties.class.getName()).log(Level.INFO, resourceName.concat(" resource Not found in the current path.  Loading the build-in resource"));                        
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName));
        }
    }

    private String currentPath() {
        try {
            String jar = new File(AppProperties.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
            
            if (jar.endsWith("jar")) {
                return jar.substring(0, jar.lastIndexOf("/"));
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(AppProperties.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        return ".";
    }
    
    public String getProperty(String key) {
        return props.getProperty(key);
    }
    
}
