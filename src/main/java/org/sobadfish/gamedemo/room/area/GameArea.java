package org.sobadfish.gamedemo.room.area;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 这个类只是提供了一直区域划分的算法
 * 通过这个算法可检测到区域 可以对这个区域进行操作
 * @author Sobadfish
 * 2023/1/12
 */
public class GameArea {

    public String level;

    public int minX;

    public int maxX;

    public int minY;

    public int maxY;

    public int minZ;

    public int maxZ;

    public GameArea(Vector3 startPosition, Vector3 endPosition, String level) {
        this.level = level;
        this.minX = startPosition.getFloorX();
        this.minY = startPosition.getFloorY();
        this.minZ = startPosition.getFloorZ();

        this.maxX = endPosition.getFloorX();
        this.maxY = endPosition.getFloorY();
        this.maxZ = endPosition.getFloorZ();
        sort();
    }

    private void sort(){
        int minX = Math.min(this.minX,this.maxX);
        int maxX = Math.max(this.minX,this.maxX);
        int minY = Math.min(this.minY,this.maxY);
        int maxY = Math.max(this.minY,this.maxY);
        int minZ = Math.min(this.minZ,this.maxZ);
        int maxZ = Math.max(this.minZ,this.maxZ);
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    /**
     * 获取这个区域中的所有的非空气方块
     * */
    public ArrayList<Block> asValueBlocks(){
        ArrayList<Block> blocks = new ArrayList<>();
        //首先检测地图有没有加载
        Level world = Server.getInstance().getLevelByName(level);
        if(world != null){
            for(int x1 = minX; x1 <= maxX; x1++){
                for(int y1 = minY; y1 <= maxY; y1++){
                    for(int z1 = minZ; z1 <= maxZ; z1++){
                        Block block = world.getBlock(x1, y1, z1);
                        if(block != null && block.getId() != 0){
                            blocks.add(block);
                        }
                    }
                }
            }
        }

        return blocks;

    }

    /**
     * 检测坐标是否在区域内
     * @param position 被检测坐标
     * @param ignoreY 忽略Y轴
     * @return 是否在区域内
     * */
    public boolean chunkPosition(Position position,boolean ignoreY){
        if(position.x >= this.minX && position.x <= this.maxX && (ignoreY || position.y >= this.minY)
                && (ignoreY || position.y <= this.maxY) && position.z >= this.minZ
                && position.z <= this.maxZ){
            return this.level != null && position.level.getFolderName().equalsIgnoreCase(this.level);
        }
        return false;
    }






    public Map<String,Object> asConfigMap(){
        Map<String,Object> configMap = new HashMap<>();
        configMap.put("minX",this.minX);
        configMap.put("minY",this.minY);
        configMap.put("maxX",this.maxX);
        configMap.put("maxY",this.maxY);
        configMap.put("minZ",this.minZ);
        configMap.put("maxZ",this.maxZ);
        configMap.put("level",this.level);
        return configMap;
    }
}
