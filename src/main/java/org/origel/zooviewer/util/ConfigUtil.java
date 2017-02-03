package org.origel.zooviewer.util;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;


public class ConfigUtil {

    public static Map<String, String> envMap = new HashMap<String, String>();
    public static List<String> envNames = new ArrayList<String>();

    static {
        loadEnvs();
    }

    private static void loadEnvs() {
        List<String> envStrings = null;

        try {
            envStrings = readFromRepository();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Pattern p = Pattern.compile("(\\w+)=(.+)");
        for (String env : envStrings) {
            Matcher m = p.matcher(env);
            if (m.matches()) {
                envMap.put(m.group(1).toUpperCase(), m.group(2));
                envNames.add(m.group(1).toUpperCase());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> readFromRepository() throws IOException {
        try {
            return IOUtils.readLines(ConfigUtil.class.getResourceAsStream("/zookeeper_cluster.properties"));
        	//List<String> lineList = new ArrayList<String>();
        	//lineList.add("localhost.zookeeper.servers=localhost:2181");
        	
        	//return lineList;
        } finally {
        }
    }
    
    public static void main(String[] args) {
    	ConfigUtil.loadEnvs();
    	
    	System.out.println(ConfigUtil.envMap);
	}
}
