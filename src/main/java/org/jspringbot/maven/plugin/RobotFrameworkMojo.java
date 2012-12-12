package org.jspringbot.maven.plugin;

/*
 * Copyright 2011 Michael Mallete, Dietrich Schulten
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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Runs the Robot tests. Behaves like invoking the "jybot" command.
 * <p/>
 * Robot Framework tests cases are created in files and directories, and they are executed by configuring the path to
 * the file or directory in question to the testCasesDirectory configuration. The given file or directory creates the
 * top-level tests suites, which gets its name, unless overridden with the "name" option, from the file or directory
 * name.
 *
 */
@Mojo(name = "run", threadSafe = true, defaultPhase = LifecyclePhase.INTEGRATION_TEST, requiresDependencyResolution = ResolutionScope.TEST)
public class RobotFrameworkMojo extends AcceptanceTestMojo {

    protected void evaluateReturnCode(int robotRunReturnValue) throws MojoFailureException, MojoExecutionException {
        // ======== ==========================================
        // RC Explanation
        // ======== ==========================================
        // 0 All critical tests passed.
        // 1-249 Returned number of critical tests failed.
        // 250 250 or more critical failures.
        // 251 Help or version information printed.
        // 252 Invalid test data or command line options.
        // 253 Test execution stopped by user.
        // 255 Unexpected internal error.
        // ======== ==========================================
        switch (robotRunReturnValue) {
            case 0:
                // success
                break;
            case 1:
                throw new MojoFailureException("1 critical test case failed. Check the logs for details.");
            case 250:
                throw new MojoFailureException("250 or more critical test cases failed. Check the logs for details.");
            case 251:
                getLog().info("Help or version information printed. No tests were executed.");
                break;
            case 252:
                throw new MojoExecutionException("Invalid test data or command line options.");
            case 255:
                throw new MojoExecutionException("Unexpected internal error.");
            case 253:
                getLog().info("Test execution stopped by user.");
                break;
            default:
                throw new MojoFailureException(robotRunReturnValue
                        + " critical test cases failed. Check the logs for details.");
        }
    }
}
