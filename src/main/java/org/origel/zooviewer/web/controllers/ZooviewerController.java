package org.origel.zooviewer.web.controllers;

import java.util.Arrays;
import java.util.List;

import org.I0Itec.zkclient.DataUpdater;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.origel.zooviewer.util.ConfigUtil;


import net.paoding.rose.web.Invocation;
import net.paoding.rose.web.annotation.Param;
import net.paoding.rose.web.annotation.Path;
import net.paoding.rose.web.annotation.rest.Get;
import net.paoding.rose.web.annotation.rest.Post;

@Path("")
public class ZooviewerController {
    static Log logger = LogFactory.getLog(ZooviewerController.class);

    public static final int SESSION_TIMEOUT = 30000;
    public static final int CONNECTION_TIMEOUT = 30000;
    
    public final static String ROOT = "/";

    @Get("")
    public String index(Invocation inv){
        inv.addModel("envMap", ConfigUtil.envMap);
        return "layout/index";
    }
    
    @Get("{env:[0-9A-Z_]+}/data")
    public String node(@Param("env") String env, @Param("path") String path, Invocation inv){
        inv.addModel("envMap", ConfigUtil.envMap);
        inv.addModel("env", env);

        ZkClient zkCLient = getZkClient(env);
        try{
            path = StringUtils.defaultIfEmpty(path, ROOT);
            inv.addModel("path", path);
            
            List<String> levelList= Arrays.asList(StringUtils.split(path, "/"));
            inv.addModel("levelList", levelList);
            
            if(!zkCLient.exists(path)){
                inv.addModel("error", "node_not_exist");
            }
    
            List<String> childList = zkCLient.getChildren(path);
            
            Stat stat = new Stat();
            byte[] data = zkCLient.readData(path, stat);
            inv.addModel("stat", stat);
            
            if(data==null||data.length==0){
                inv.addModel("data", StringUtils.EMPTY);
            }else{
                inv.addModel("data", new String(data));
            }
            
            inv.addModel("childList", childList);
        }catch(Exception e){
            logger.error("", e);
        }finally{
            zkCLient.close();
        }

        return "layout/node";
    }

    private ZkClient getZkClient(String env){
        String connStr = ConfigUtil.envMap.get(env);
        ZkClient zkClient = new ZkClient(connStr, SESSION_TIMEOUT, CONNECTION_TIMEOUT, new BytesPushThroughSerializer());
        
        return zkClient;
    }

    @Get("createNode")
    public String createNode(@Param("env") String env, @Param("path") String path, Invocation inv){
        inv.addModel("envMap", ConfigUtil.envMap);
        
        path = StringUtils.defaultIfEmpty(path, ROOT);
        inv.addModel("path", path);
        inv.addModel("env", env);
        return "layout/create";
    }

    @Post("create")
    public String create(@Param("env") String env, @Param("parent") String parent, @Param("name") String name, @Param("data") String data, Invocation inv){
        ZkClient zkClient = getZkClient(env);

        try{
            String path = ROOT.equals(parent)? ROOT + name: parent + ROOT + name;
            zkClient.create(path, data.getBytes("utf-8"), CreateMode.PERSISTENT);
        }catch(Exception e){
            logger.error("", e);
        }finally{
            zkClient.close();
        }

        return r(env + "/data?path=" + parent, inv);
    }
    
    @Post("update")
    public String update(@Param("env") String env, @Param("path") String path, @Param("data") final String data, Invocation inv){
        ZkClient zkClient = getZkClient(env);

        try{
            zkClient.updateDataSerialized(path, new DataUpdater<byte[]>(){

				public byte[] update(byte[] oldData) {
					try {
						return data.getBytes("utf-8");
					} catch (Exception e) {
						e.printStackTrace();
						return new byte[0];
					}
				}
            	
            });
        }catch(Exception e){
            logger.error("", e);
        }finally{
            zkClient.close();
        }

        return r(env + "/data?path=" + path, inv);
    }

    @Post("delete")
    public String delete(@Param("env") String env, @Param("path") String path, Invocation inv){
        ZkClient zkClient = getZkClient(env);

        if(ROOT.equals(StringUtils.trim(path))){
            return r(env + "/data?path=" + ROOT, inv); 
        }

        try{
            zkClient.delete(path);
        }catch(Exception e){
            logger.error("", e);
        }finally{
            zkClient.close();
        }

        String parent = StringUtils.substring(path, 0, path.lastIndexOf('/'));
        return r(env + "/data?path=" + parent, inv);
    }

    @Get("help")
    public String help(Invocation inv){
        return "layout/help";
    }
    
    @Get("logout")
    public String logout(Invocation inv){
        return r("/", inv);
    }
    
    public String r(String url, Invocation inv){
        inv.addModel("redirectUrl", url);
        return "r:" + url;
    }
}
