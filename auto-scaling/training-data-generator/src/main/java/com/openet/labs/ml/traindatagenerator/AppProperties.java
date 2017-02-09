/**************************************************************************
 *
 * Copyright © Openet Telecom, Ltd. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **************************************************************************/

package com.openet.labs.ml.traindatagenerator;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class AppProperties {

	private static Logger logger = Logger.getLogger(AppProperties.class);
	
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
        File file = new File(currentPath().concat(File.separator).concat(resourceName));
        props = new Properties();
        
        if (file.exists()) {
            logger.info(resourceName.concat(" resource found in the current path."));            
            props.load(new FileReader(file));
        } else {
            logger.info(resourceName.concat(" resource Not found in the current path.  Loading the build-in resource"));                        
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
            logger.error(ex);            
        }
        
        return ".";
    }
    
    public String getProperty(String key) {
        return props.getProperty(key);
    }
    
}
