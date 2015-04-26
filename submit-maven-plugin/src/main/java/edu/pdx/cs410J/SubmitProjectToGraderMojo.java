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

import edu.pdx.cs410J.grader.Submit;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

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

    @Parameter( property = "projectName", defaultValue = "${project.description}", required = true )
    private String projectName;

    @Parameter( property = "loginId", defaultValue = "${project.developers[0].id}", required = true )
    private String loginId;

    @Parameter( property = "userName", defaultValue = "${project.developers[0].name}", required = true )
    private String userName;

    @Parameter( property = "userEmail", defaultValue = "${project.developers[0].email}",required = true )
    private String userEmail;

    @Parameter( property = "comment", required = false )
    private String comment;

    @Parameter( property = "debug", defaultValue = "false", required = false )
    private boolean debug;

    @Parameter( property = "saveJar", defaultValue = "false", required = false )
    private boolean saveJar;

    @Parameter( property = "sendEmail", defaultValue = "true", required = false )
    private boolean sendEmail;

    @Parameter( property = "jarFileDirectory", required = false)
    private File jarFileDirectory;

    @Parameter( property = "verifySubmissionWithUser", defaultValue = "true", required = false)
    private boolean verifySubmissionWithUser;


    @Override
    public void execute()
        throws MojoExecutionException
    {
        Submit submit = new Submit(adaptMojoLogger());
        submit.setProjectName(projectName);
        submit.setUserId(loginId);
        submit.setUserName(userName);
        submit.setUserEmail(userEmail);
        submit.setComment(comment);
        submit.setDebug(debug);
        submit.setSaveJar(saveJar);
        submit.setSendEmail(sendEmail);

        try {
            submit.setJarFileDirectory(getJarFileDirectory());
            findJavaFiles(sourceDirectory).forEach(f -> submit.addFile(f.getAbsolutePath()));
            submit.submit(verifySubmissionWithUser);

        } catch (IOException | MessagingException ex) {
            throw new MojoExecutionException("While submitting to grader", ex);
        }

    }

    private Submit.Logger adaptMojoLogger() {
        return new Submit.Logger() {

            @Override
            public void debug(String message) {
                getLog().debug(message);
            }

            @Override
            public void info(String message) {
                getLog().info(message);
            }

            @Override
            public void warn(String message) {
                getLog().warn(message);
            }

            @Override
            public void error(String message) {
                getLog().error(message);
            }
        };
    }

    private File getJarFileDirectory() throws IOException {
        if (jarFileDirectory == null) {
            return null;
        }

        if (!jarFileDirectory.exists()){
            if (!jarFileDirectory.mkdirs()) {
                throw new IOException("Could not create jar file directory: " + jarFileDirectory);
            }
        }
        return jarFileDirectory;
    }

    private Stream<File> findJavaFiles(File directory) throws IOException {
        return Files.walk(directory.toPath())
          .map(Path::toFile)
          .filter(this::fileNameEndsWithJava);
    }

    private boolean fileNameEndsWithJava(File f) {
        return f.getName().endsWith(".java");
    }
}
