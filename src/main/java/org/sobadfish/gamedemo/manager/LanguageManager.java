package org.sobadfish.gamedemo.manager;

import cn.nukkit.plugin.PluginBase;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;

/**
 * 语言文件
 * @author Sobadfish
 * 2023/1/10
 */
public class LanguageManager {

    public HashMap<String, String> ini;


    public LanguageManager(PluginBase plugin){
        ini = new HashMap<>();
        File iniFile = new File(plugin.getDataFolder()+"/language.ini");
        if(!iniFile.exists()){
            plugin.saveResource("language.ini",false);
        }
        BufferedReader br;
        try {
            InputStream in = new FileInputStream(iniFile);
            br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            Properties props = new Properties();
            props.load(br);
            for(Object s: props.keySet()){
                ini.put(s.toString(), props.getProperty(s.toString()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getLanguage(String key,IniValueData... values) {
        String value = "";
        return getLanguage(key,value, values);
    }

    public String getLanguage(String key,String defaultValue,String... values) {
        IniValueData[] iniValueData = new IniValueData[values.length];
        int size = 0;
        if(values.length > 0) {
            for(String s: values){
                iniValueData[size++] = new IniValueData(size, s);
            }
        }

        return getLanguage(key,defaultValue, iniValueData);
    }

    public String getLanguage(String key,String defaultValue,IniValueData[] values) {
        String value = defaultValue;
        if(ini != null && ini.containsKey(key)){
            value = ini.get(key);
        }
        if(value == null || value.length() == 0){
            value = defaultValue;
        }
        if(values.length > 0){
            for(IniValueData iv : values){
                value = value.replace("["+iv.key+"]",iv.value);
            }
        }
        value = value.replace("[n]","\n");
        return value;
    }

    public static class IniValueData{
        public int key;

        public String value;

        public IniValueData(int key, String value){
            this.key = key;
            this.value = value;
        }
    }

}
