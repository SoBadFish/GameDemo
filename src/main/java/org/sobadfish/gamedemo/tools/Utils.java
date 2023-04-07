package org.sobadfish.gamedemo.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.BaseEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import cn.nukkit.utils.DyeColor;
import org.sobadfish.gamedemo.entity.ChairEntity;
import org.sobadfish.gamedemo.manager.TotalManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.SplittableRandom;


public class Utils {

    private static final SplittableRandom RANDOM = new SplittableRandom(System.currentTimeMillis());

    /**
     * 获取一定范围的整数随机数
     * @param min 最小值
     * @param max 最大值
     *
     * @return 取得的随机数
     * */
    public static int rand(int min, int max) {
        return min == max ? max : RANDOM.nextInt(max + 1 - min) + min;
    }

    /**
     * 获取一定范围的小数随机数
     * @param min 最小值
     * @param max 最大值
     *
     * @return 取得的随机数
     * */
    public static double rand(double min, double max) {
        return min == max ? max : min + Math.random() * (max - min);
    }

    /**
     * 获取一定范围的小数随机数
     * @param min 最小值
     * @param max 最大值
     *
     * @return 取得的随机数
     * */
    public static float rand(float min, float max) {
        return min == max ? max : min + (float) Math.random() * (max - min);
    }

    /**
     * 获取随机的true 或 false
     * @return 随机的bool
     * */
    public static boolean rand() {
        return RANDOM.nextBoolean();
    }

    /**
     * 获取中心坐标点周围 x格的所有玩家
     * @param player 中心坐标点
     * @param size 范围
     * @return 在范围内玩家
     * */
    public static ArrayList<Player> getAroundOfPlayers(Position player, int size) {
        ArrayList<Player> players = new ArrayList<>();
        for (Entity entity : getAroundPlayers(player, size, false)) {
            players.add((Player) entity);
        }
        return players;
    }

    /**
     * 获取中心坐标点周围 x格的所有生物
     * @param player 中心坐标点
     * @param size 范围
     * @param isEntity 是否获取生物
     * @return 在范围内生物
     * */
    public static LinkedList<Entity> getAroundPlayers(Position player, int size, boolean isEntity) {
        LinkedList<Entity> explodePlayer = new LinkedList<>();
        for (Entity player1 : player.level.getEntities()) {

            if (player1.x < player.x + size && player1.x > player.x - size && player1.z < player.z + size && player1.z > player.z - size && player1.y < player.y + size && player1.y > player.y - size) {
                if (!isEntity && player instanceof Player && ((Player) player).getGamemode() != 3) {
                    explodePlayer.add(player1);
                } else if (isEntity && !(player1 instanceof BaseEntity)) {
                    explodePlayer.add(player1);
                }

            }
        }
        return explodePlayer;
    }


    /**
     * 删除的文件
     * @param file 文件
     * */
    public static void toDelete(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File file1 : files) {
                if (file1.isDirectory()) {
                    toDelete(file1);
                } else {
                    file1.delete();
                }
            }
        }

        file.delete();

    }

    /**
     * 放烟花
     * @param position 放烟花的位置
     */
    public static void spawnFirework(Position position) {

        Level level = position.getLevel();
        ItemFirework item = new ItemFirework();
        CompoundTag tag = new CompoundTag();
        Random random = new Random();
        CompoundTag ex = new CompoundTag();
        ex.putByteArray("FireworkColor", new byte[]{
                (byte) DyeColor.values()[random.nextInt(ItemFirework.FireworkExplosion.ExplosionType.values().length)].getDyeData()
        });
        ex.putByteArray("FireworkFade", new byte[0]);
        ex.putBoolean("FireworkFlicker", random.nextBoolean());
        ex.putBoolean("FireworkTrail", random.nextBoolean());
        ex.putByte("FireworkType", ItemFirework.FireworkExplosion.ExplosionType.values()
                [random.nextInt(ItemFirework.FireworkExplosion.ExplosionType.values().length)].ordinal());
        tag.putCompound("Fireworks", (new CompoundTag("Fireworks")).putList(new ListTag<CompoundTag>("Explosions").add(ex)).putByte("Flight", 1));
        item.setNamedTag(tag);
        CompoundTag nbt = new CompoundTag();
        nbt.putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("", position.x + 0.5D))
                .add(new DoubleTag("", position.y + 0.5D))
                .add(new DoubleTag("", position.z + 0.5D))
        );
        nbt.putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("", 0.0D))
                .add(new DoubleTag("", 0.0D))
                .add(new DoubleTag("", 0.0D))
        );
        nbt.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("", 0.0F))
                .add(new FloatTag("", 0.0F))

        );
        nbt.putCompound("FireworkItem", NBTIO.putItemHelper(item));
        EntityFirework entity = new EntityFirework(level.getChunk((int) position.x >> 4, (int) position.z >> 4), nbt);
        entity.spawnToAll();
    }



    /**
     * 画一条自定义长度的线
     * @param size 线的长度
     * @param line 用于填充线条的字符
     *
     * @return 画出来的线
     * */
    public static String writeLine(int size,String line){
        StringBuilder s = new StringBuilder();
        for(int i = 0;i< size;i++){
            s.append(line);
        }
        return s.toString();
    }






    /**
     * 复制文件，将File文件复制到另一个文件路径上
     * 不会覆盖文件
     * @param old 复制的文件
     * @param target 复制到的位置
     * @return 是否复制成功
     * */
    public static boolean copyFiles(File old,File target){

        int load = 1;
        File[] files = old.listFiles();
        if(files != null){
            for (File value : files) {
                TotalManager.sendMessageToConsole("Coping File ... "+((load / (float)files.length) * 100) +"%");
                load++;
                if (value.isFile()) {
                    // 复制文件
                    try {
                        File file1 = new File(target +File.separator + value.getName());
                        if(!file1.exists()){
                            try{
                                file1.createNewFile();
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                        copyFile(value, file1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (value.isDirectory()) {
                    // 复制目录
                    String sourceDir = old + File.separator + value.getName();
                    String targetDir = target+ File.separator + value.getName();
                    try {
                        copyDirectiory(sourceDir, targetDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return true;
    }


    private static void copyFile(File source,File target)
            throws IOException{

        RandomAccessFile sourceFile = new RandomAccessFile(source, "r");
        FileChannel sourceChannel = sourceFile.getChannel();

        if (!target.isFile()) {
            if (!target.createNewFile()) {
                sourceChannel.close();
                sourceFile.close();
                return;
            }
        }
        RandomAccessFile destFile = new RandomAccessFile(target, "rw");
        FileChannel destChannel = destFile.getChannel();
        long leftSize = sourceChannel.size();
        long position = 0;
        while (leftSize > 0) {
            long writeSize = sourceChannel.transferTo(position, leftSize, destChannel);
            position += writeSize;
            leftSize -= writeSize;
        }
        sourceChannel.close();
        sourceFile.close();
        destChannel.close();
        destFile.close();
    }

    /**
     * 复制文件夹
     * @param sourceDir 复制的文件夹
     * @param targetDir 目标文件夹
     * */
    private static void copyDirectiory(String sourceDir, String targetDir)
            throws IOException {
        // 新建目标目录
        File file = new File(targetDir);
        if(!file.exists()) {
            if (!file.mkdirs()) {
                Server.getInstance().getLogger().error("create" + targetDir + "error");
            }
        }
        // 获取源文件夹当前下的文件或目录
        File[] files = (new File(sourceDir)).listFiles();
        if(files != null){
            for (File value : files) {
                if (value.isFile()) {
                    // 源文件
                    // 目标文件
                    File targetFile = new
                            File(new File(targetDir).getAbsolutePath()
                            + File.separator + value.getName());
                    copyFile(value, targetFile);

                }
                if (value.isDirectory()) {
                    // 准备复制的源文件夹
                    String dir1 = sourceDir + File.separator + value.getName();
                    // 准备复制的目标文件夹
                    String dir2 = targetDir + File.separator + value.getName();
                    copyDirectiory(dir1, dir2);
                }
            }
        }

    }
    /**
     * 生物坐下方法
     * @author Champrin
     * /
     * Nukkit-ScientificGames
     * */
    public static void sitDown(Entity player, Block block) {

        double x = block.getX();
        double y = block.getY();
        double z = block.getZ();

        CompoundTag nbt = Entity.getDefaultNBT(new Vector3(x + 0.5, y, z + 0.4), new Vector3(0, 0, 0), 0, 0);

        ChairEntity entity = new ChairEntity(player.chunk, nbt);
        entity.spawnToAll();

        SetEntityLinkPacket pk = new SetEntityLinkPacket();
        pk.vehicleUniqueId = entity.getId();
        pk.riderUniqueId = player.getId();
        pk.type = 2;

        SetEntityLinkPacket finalPk = pk;
        Server.getInstance().getOnlinePlayers().forEach((uuid, p) -> {
            p.dataPacket(finalPk);
        });

        pk = new SetEntityLinkPacket();
        pk.vehicleUniqueId = entity.getId();
        pk.riderUniqueId = 0;
        pk.type = 2;
        SetEntityLinkPacket finalPk1 = pk;
        Server.getInstance().getOnlinePlayers().forEach((uuid, p) -> {
            p.dataPacket(finalPk1);
        });

        removeSitEntity(player);
        player.namedTag.putLong("Chair", entity.getId());

    }

    public static void removeSitEntity(Entity player) {
        if (player.namedTag.getLong("Chair") != 0) {
            Level level = player.getLevel();

            Entity chairEntity = level.getEntity(player.namedTag.getLong("Chair"));

            if (chairEntity != null && "Chair".equals(chairEntity.getName())) {
                chairEntity.kill();
                chairEntity.close();
            }
            player.namedTag.remove("Chair");
        }
        if (player.namedTag.getBoolean("Click")) {
            player.namedTag.remove("Click");
        }
        player.teleport(player.getPosition().add(1,1,1));
    }


}
