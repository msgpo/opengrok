/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2005 Sun Microsystems, Inc.  All rights reserved.
 * Use is subject to license terms.
 */

package org.opensolaris.opengrok.index;

import java.awt.GraphicsEnvironment;
import java.io.*;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.*;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.oro.io.GlobFilenameFilter;
import org.opensolaris.opengrok.analysis.*;
import org.opensolaris.opengrok.configuration.Project;
import org.opensolaris.opengrok.history.ExternalRepository;
import org.opensolaris.opengrok.history.HistoryGuru;
import org.opensolaris.opengrok.configuration.RuntimeEnvironment;
import org.opensolaris.opengrok.search.scope.MainFrame;
import org.opensolaris.opengrok.util.Getopt;

/**
 * Creates and updates an inverted source index
 * as well as generates Xref, file stats etc., if specified
 * in the options
 */
public class Indexer {
    private static String usage = "Usage: " +
            "opengrok.jar [-qe] [-c ctagsToUse] [-H] [-R filename] [-W filename] [-U hostname:port] [-P] [-p project-path] [-w webapproot] [-i ignore_name [ -i ..]] [-n] [-s SRC_ROOT] DATA_ROOT [subtree .. ]\n" +
            "       opengrok.jar [-O | -l | -t] DATA_ROOT\n" +
            "\t-q run quietly\n" +
            "\t-e economical - consumes less disk space\n" +
            "\t-c path to ctags\n" +
            "\t-R Read configuration from file\n" +
            "\t-W Write the current running configuration\n" +
            "\t-U Send configuration to hostname:port\n" +
            "\t-P Generate a project for each toplevel directory\n" +
            "\t-p Use the project specified by the project path as the default project\n" +
            "\t-Q +/- Turn on / off quick context scan. By default only the first 32k\n" +
            "\t       of a file is scanned and a '[..all..]' link is inserted if the\n" +
            "\t       is bigger. Activating this option may slow down the server.\n" +
            "\t-n Do not generate indexes\n" +
            "\t-H Start a threadpool to read history history\n" +
            "\t-w root URL of the webapp, default is /source\n" +
            "\t-i ignore named files or directories\n" +
            "\t-m Maximum words in a file to index\n" +
            "\t-S Search and add \"External\" repositories (Mercurial etc)\n" +
            "\t-s SRC_ROOT is root directory of source tree\n" +
            "\t   default: last used SRC_ROOT\n" +
            "\tDATA_ROOT - is where output of indexer is stored\n" +
            "\tsubtree - only specified files or directories under SRC_ROOT are processed\n" +
            "\t   if not specified all files under SRC_ROOT are processed\n" +
            "\n\t-O optimize the index \n" +
            "\t-l list all files in the index \n" +
            "\t-t lists tokens occuring more than 5 times. Useful for building a unix dictionary\n" +
            "\n Eg. java -jar opengrok.jar -s /usr/include /var/tmp/opengrok_data rpc";

    private static String options = "qec:Q:R:W:U:Pp:nHw:i:Ss:O:l:t:vD:m:";

    /**
     * Program entry point
     * @param argv argument vector
     */
    public static void main(String argv[]) {
        RuntimeEnvironment env = RuntimeEnvironment.getInstance();
        boolean runIndex = true;

        if(argv.length == 0) {
            if (GraphicsEnvironment.isHeadless()) {
                System.err.println("No display available for the Graphical User Interface");
                System.err.println(usage);
                System.exit(1);
            } else {
                MainFrame.main(argv);
            }
            //Run Scope GUI here I am running Indexing GUI for testing
            //new IndexerWizard(null).setVisible(true);
        } else {
            boolean searchRepositories = false;
            ArrayList<String> subFiles = new ArrayList<String>();
            String configFilename = null;
            String configHost = null;
            boolean addProjects = false;
            boolean refreshHistory = false;
            String defaultProject = null;

            // Parse command line options:
            Getopt getopt = new Getopt(argv, options);
            try {
                getopt.parse();
            } catch (ParseException ex) {
                System.err.println("OpenGrok: " + ex.getMessage());
                System.err.println(usage);
                System.exit(1);
            }

            try{
                int cmd;
                
                // We need to read the configuration file first, since we
                // will try to overwrite options..
                while ((cmd = getopt.getOpt()) != -1) {
                    if (cmd == 'R') {
                        env.readConfiguration(new File(getopt.getOptarg()));
                        break;
                    }
                }
                
                // Now we can handle all the other options..
                getopt.reset();                
                while ((cmd = getopt.getOpt()) != -1) {
                    switch (cmd) {
                    case 'D' : 
                        Index.dumpU(getopt.getOptarg()); 
                        System.exit(1);
                        break;
                    case 'O':
                        Index.doOptimize(new File(getopt.getOptarg()));
                        System.exit(0);
                        break;
                    case 'l':
                        Index.doList(new File(getopt.getOptarg()));
                        System.exit(0);
                        break;
                    case 't':
                        Index.doDict(new File(getopt.getOptarg()));
                        System.exit(0);
                        break;
                    case 'g':
                        IgnoredNames.glob = new GlobFilenameFilter(getopt.getOptarg());
                        break;

                    case 'q': env.setVerbose(false); break;
                    case 'e': env.setGenerateHtml(false); break;
                    case 'P': addProjects = true; break;
                    case 'p': defaultProject = getopt.getOptarg(); break;
                    case 'c': env.setCtags(getopt.getOptarg()); break;
                    case 'w': {
                        String webapp = getopt.getOptarg();
                        if (webapp.startsWith("/") || webapp.startsWith("http")) {
                            ;
                        } else {
                            webapp = "/" + webapp;
                        }
                        if (webapp.endsWith("/")) {
                            env.setUrlPrefix(webapp + "s?");
                        } else {
                            env.setUrlPrefix(webapp + "/s?");
                        }
                    }
                    break;
                    case 'W': configFilename = getopt.getOptarg(); break;
                    case 'U': configHost = getopt.getOptarg(); break;
                    case 'R': 
                        // already handled
                        break;
                    case 'n': runIndex = false; break;
                    case 'H': refreshHistory = true; break;
                    case 'v': env.setVerbose(true); break;

                    case 's': {
                        File file = new File(getopt.getOptarg());
                        if (!file.isDirectory()) {
                            System.err.println("ERROR: No such directory: " + file.toString());
                            System.exit(1);
                        }

                        env.setSourceRoot(file);
                        break;
                    }

                    case 'i':  IgnoredNames.add(getopt.getOptarg()); break;
                    case 'S' : searchRepositories = true; break;
                    case 'Q' : 
                        switch (getopt.getOptarg().charAt(0)) {
                        case '+' :
                            env.setQuickContextScan(true);
                            break;
                        case '-' :
                            env.setQuickContextScan(false);
                            break;
                        default:
                            System.err.println("ERROR: You should pass either '+' or '-' as argument to -Q");
                            System.exit(1);
                        }
                        
                        break;
                    case 'm' : {
                        try {
                            env.setIndexWordLimit(Integer.parseInt(getopt.getOptarg()));
                        } catch (NumberFormatException exp) {
                            System.err.println("ERROR: Failed to parse argument to \"-m\": " + exp.getMessage());
                            System.exit(1);
                        }
                        break;
                    }
                    default: 
                        System.err.println("Unknown option: " + (char)cmd);
                        System.exit(1);
                    }
                }

                int optind = getopt.getOptind();
                if (optind != -1) {
                    if (optind < argv.length) {
                        env.setDataRoot(argv[optind]);
                        ++optind;
                    }

                    while (optind < argv.length) {
                        subFiles.add(argv[optind]);
                        ++optind;
                    }
                }
                
                if (env.getDataRootPath()  == null) {
                    System.err.println("ERROR: Please specify a DATA ROOT path");
                    System.err.println(usage);
                    System.exit(1);
                }

                if (env.getSourceRootFile() == null) {
                    File srcConfig = new File(env.getDataRootPath(), "SRC_ROOT");
                    String line = null;
                    if(srcConfig.exists()) {
                        try {
                            BufferedReader sr = new BufferedReader(new FileReader(srcConfig));
                            line = sr.readLine();
                            sr.close();
                        } catch (IOException e) {
                        }
                    }
                    if(line == null) {
                        System.err.println("ERROR: please specify a SRC_ROOT with option -s !");
                        System.err.println(usage);
                        System.exit(1);
                    }
                    env.setSourceRoot(line);

                    if (!env.getSourceRootFile().isDirectory()) {
                        System.err.println("ERROR: No such directory:" + line);
                        System.err.println(usage);
                        System.exit(1);
                    }
                }

                if (!env.validateExuberantCtags()) {
                    System.exit(1);
                }

                if (searchRepositories) {
                    if (env.isVerbose()) {
                        System.out.println("Scanning for repositories...");
                    }
                    env.getRepositories().clear();
                    long start = System.currentTimeMillis();
                    HistoryGuru.getInstance().addExternalRepositories(env.getSourceRootPath());
                    long time = (System.currentTimeMillis() - start) / 1000;
                    if (env.isVerbose()) {
                        System.out.println("Done searching for repositories (" + time + "s)");
                    }
                }

                if (addProjects) {
                    File files[] = env.getSourceRootFile().listFiles();
                    List<Project> projects = env.getProjects();
                    projects.clear();
                    for (File file : files) {
                        if (!file.getName().startsWith(".") && file.isDirectory()) {
                            projects.add(new Project(file.getName(), "/" + file.getName()));
                        }
                    }

                    // The projects should be sorted...
                    Collections.sort(projects, new Comparator<Project>() {
                        public int compare(Project p1, Project p2){
                            String s1 = p1.getDescription();
                            String s2 = p2.getDescription();

                            int ret;
                            if (s1 == null) {
                                ret = (s2 == null) ? 0 : 1;
                            } else {
                                ret = s1.compareTo(s2);
                            }
                            return ret;
                        }
                    });
                }

                if (defaultProject != null) {
                    for (Project p : env.getProjects()) {
                        if (p.getPath().equals(defaultProject)) {
                            env.setDefaultProject(p);
                            break;
                        }
                    }
                }
                
                if (configFilename != null) {
                    if (env.isVerbose()) {
                        System.out.println("Writing configuration to " + configFilename);
                        System.out.flush();
                    }
                    env.writeConfiguration(new File(configFilename));
                    if (env.isVerbose()) {
                        System.out.println("Done...");
                        System.out.flush();
                    }
                }

                if (refreshHistory) {
                    for (Map.Entry<String, ExternalRepository> entry : RuntimeEnvironment.getInstance().getRepositories().entrySet()) {
                        try {
                            entry.getValue().createCache();
                        } catch (Exception e) {
                            System.err.println("Failed to generate history cache.");
                            e.printStackTrace();
                        }
                    }
                }

                if (runIndex) {
                    Index idx = new Index(env.isVerbose() ? new StandardPrinter(System.out) : new NullPrinter(), new StandardPrinter(System.err));
                    idx.runIndexer(env.getDataRootFile(), env.getSourceRootFile(), subFiles, !env.isGenerateHtml());
                }

                if (configHost != null) {
                    String[] cfg = configHost.split(":");
                    if (env.isVerbose()) {
                        System.out.println("Send configuration to: " + configHost);
                    }

                    if (cfg.length == 2) {
                        try {
                            InetAddress host = InetAddress.getByName(cfg[0]);
                            RuntimeEnvironment.getInstance().writeConfiguration(host, Integer.parseInt(cfg[1]));
                        } catch (Exception ex) {
                            System.err.println("Failed to send configuration to " + configHost);
                            ex.printStackTrace();
                        }
                    } else {
                        System.err.println("Syntax error: ");
                        for (String s : cfg) {
                            System.err.print("[" + s + "]");
                        }
                        System.err.println();
                    }
                    if (env.isVerbose()) {
                        System.out.println("Configuration successfully updated");
                    }
                }
            } catch (Exception e) {
                System.err.println("Error: [ main ] " + e);
                if (env.isVerbose()) e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
