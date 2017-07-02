package com.utility;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.utility.service.ScanFileService;

@Component
public class FileScanner implements CommandLineRunner {

    @Autowired
    private ScanFileService scanFileService;
    
    @Override
    public void run(String... arg0) throws Exception {
        String[] args = Arrays.copyOfRange(arg0, 1, arg0.length);
        CommandLine commandLine;
        Options options = new Options();
        options.addOption("s", true, "server").addOption("t", true, "thread number").addOption("p", true, "scan path")
                .addOption("i", true, "interval second");
        CommandLineParser parser = new DefaultParser();
        try {
            commandLine = parser.parse(options, args);
            String serverName = commandLine.getOptionValue("s");
            int threadNum = 4;
            if (commandLine.hasOption("t")) {
                threadNum = Integer.parseInt(commandLine.getOptionValue("t"));
            }
            String filePath = commandLine.getOptionValue("p");
            int intervalSecond = 5;
            if (commandLine.hasOption("i")) {
                intervalSecond = Integer.parseInt(commandLine.getOptionValue("i"));
            }
            
            scanFileService.scanFolder(filePath, serverName, threadNum, intervalSecond);

        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

}
