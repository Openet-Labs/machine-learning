/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openet.labs.ml.autoscale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.kohsuke.args4j.CmdLineException;

/**
 *
 * @author openet
 */
public class AutoScalingOptionsTest {   

    @Test
    public void testSomeMethod() throws CmdLineException {
        
        
        String[] argsCorrect = { "--usecase-properties", "application.properties"};
        AutoScalingOptions arguments = new AutoScalingOptions(argsCorrect);
        assertEquals(arguments.getUseCaseConfFilePath(), "application.properties");
    }
    

    
}
