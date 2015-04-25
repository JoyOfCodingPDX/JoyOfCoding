package edu.pdx.cs410J;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

@Mojo( name = "submit", defaultPhase = LifecyclePhase.NONE )
public class SubmitProjectToGraderMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     */
    @Parameter( defaultValue = "${project.build.directory}", property = "outputDir", required = true )
    private File outputDirectory;

    @Parameter( defaultValue = "${project.build.sourceDirectory}", property = "sourceDirectory", required = false)
    private File sourceDirectory;

    @Parameter( property = "projectName", required = true )
    private String projectName;

    @Parameter( property = "userName", required = true )
    private String userName;

    @Parameter( property = "userEmail", required = true )
    private String userEmail;

    @Parameter( property = "comment", required = false )
    private String comment;

    @Parameter( property = "debug", defaultValue = "false", required = false )
    private boolean debug;

    @Parameter( property = "saveJar", defaultValue = "false", required = false )
    private boolean saveJar;

    @Parameter( property = "sendEmail", defaultValue = "true", required = false )
    private boolean sendEmail;

    @Override
    public void execute()
        throws MojoExecutionException
    {
        getLog().info("Hello " + new Date());
        getLog().info("Source Directory is " + sourceDirectory);

        File f = outputDirectory;

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        File touch = new File( f, "touch.txt" );

        FileWriter w = null;
        try
        {
            w = new FileWriter( touch );

            w.write( "touch.txt" );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error creating file " + touch, e );
        }
        finally
        {
            if ( w != null )
            {
                try
                {
                    w.close();
                }
                catch ( IOException e )
                {
                    // ignore
                }
            }
        }
    }
}
