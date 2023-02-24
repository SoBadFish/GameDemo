package org.sobadfish.gamedemo.manager;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockTNT;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityNameable;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.level.WeatherChangeEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemColorArmor;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.dlc.IGameRoomDlc;
import org.sobadfish.gamedemo.entity.DamageFloatTextEntity;
import org.sobadfish.gamedemo.entity.EntityTnt;
import org.sobadfish.gamedemo.event.GameRoomCreateEvent;
import org.sobadfish.gamedemo.item.ICustomItem;
import org.sobadfish.gamedemo.panel.ChestInventoryPanel;
import org.sobadfish.gamedemo.panel.DisPlayWindowsFrom;
import org.sobadfish.gamedemo.panel.from.GameFrom;
import org.sobadfish.gamedemo.panel.from.button.BaseIButton;
import org.sobadfish.gamedemo.panel.items.BasePlayPanelItemInstance;
import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.player.team.TeamInfo;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.room.GameRoom.GameType;
import org.sobadfish.gamedemo.room.config.GameRoomConfig;
import org.sobadfish.gamedemo.room.config.ItemConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 游戏房间管理类
 * 这里综合了获取房间
 * 房间事件监听
 *
 * @author Sobadfish
 * @date 2022/9/9
 */
public class RoomManager implements Listener {

    public Map<String, GameRoomConfig> roomConfig;

    public static LanguageManager language = TotalManager.getLanguage();

    public static List<GameRoomConfig> LOCK_GAME = new ArrayList<>();

    public LinkedHashMap<String,String> playerJoin = new LinkedHashMap<>();

    public Map<String, GameRoom> getRooms() {
        return rooms;
    }

    private Map<String, GameRoom> rooms = new LinkedHashMap<>();

    public boolean hasRoom(String room){
        return roomConfig.containsKey(room);
    }

    public boolean hasGameRoom(String room){
        return rooms.containsKey(room);
    }

    private RoomManager(Map<String, GameRoomConfig> roomConfig){
        this.roomConfig = roomConfig;
    }

    /**
     * 根据地图获取房间
     * @param level 地图
     * @return 游戏房间
     * */
    private GameRoom getGameRoomByLevel(Level level){
        for(GameRoom room : new ArrayList<>(rooms.values())){
            if(room.getRoomConfig().worldInfo.getGameWorld() == null){
                continue;
            }
            if(room.getRoomConfig().worldInfo.getGameWorld().getFolderName().equalsIgnoreCase(level.getFolderName())){
                return room;
            }
        }
        return null;
    }


    /**
     * 玩家根据名称加入房间
     * @param player 玩家对象 {@link PlayerInfo}
     * @param roomName 房间名称
     * @return 是否成功加入房间
     * */
    public boolean joinRoom(PlayerInfo player, String roomName){
        PlayerInfo info = getPlayerInfo(player.getPlayer());
        if(info != null){
            player = info;
        }

        if (hasRoom(roomName)) {
            if (!hasGameRoom(roomName)) {
                if(!enableRoom(getRoomConfig(roomName))){
                    player.sendForceMessage(language.getLanguage("room-enable-error","&c[1] 还没准备好 code",roomName));

                    return false;
                }
            }else{
                GameRoom room =getRoom(roomName);
                if(room != null){
                    if(RoomManager.LOCK_GAME.contains(room.getRoomConfig()) && room.getType() == GameType.END || room.getType() == GameType.CLOSE){
                        player.sendForceMessage(language.getLanguage("room-enable-error","&c[1] 还没准备好",roomName));
                        return false;
                    }
                    if(room.getWorldInfo().getConfig().getGameWorld() == null){
                        return false;
                    }
                    if(room.getType() == GameType.END ||room.getType() == GameType.CLOSE){
                        player.sendForceMessage(language.getLanguage("room-status-ending","&c[1] 结算中",roomName));
                        return false;
                    }
                }
            }

            GameRoom room = getRoom(roomName);
            if(room == null){
                return false;
            }
            switch (room.joinPlayerInfo(player,true)){
                case CAN_WATCH:
                    if(!room.getRoomConfig().hasWatch){
                        player.sendForceMessage(language.getLanguage("room-ban-watch","&c该房间开始后不允许旁观"));
                    }else{

                        if(player.getGameRoom() != null && !player.isWatch()){
                            player.sendForceMessage(language.getLanguage("room-ban-watch-join","&c你无法进入此房间"));
                            return false;
                        }else{
                            room.joinWatch(player);
                            return true;
                        }
                    }
                    break;
                case NO_LEVEL:
                    player.sendForceMessage(language.getLanguage("room-level-resting","&c这个房间正在准备中，稍等一会吧"));
                    break;
                case NO_ONLINE:
                    break;
                case NO_JOIN:
                    player.sendForceMessage(language.getLanguage("room-ban-join","&c该房间不允许加入"));
                    break;
                default:
                    //可以加入
                    return true;
            }
        } else {
            player.sendForceMessage(language.getLanguage("room-absence","&c不存在 &r[1] &c房间",roomName));

        }
        return false;
    }


    /**
     * 启动房间
     * @param config 房间配置 {@link GameRoomConfig}
     *
     * @return 是否成功启动房间
     * */
    public boolean enableRoom(GameRoomConfig config){
        if(config.getWorldInfo().getGameWorld() == null){
            TotalManager.getPlugin().getLogger().error("启动房间时出错: "+config.getName()+"游戏地图为空!");
            return false;
        }
        if(!RoomManager.LOCK_GAME.contains(config)){
            RoomManager.LOCK_GAME.add(config);

            GameRoom room = GameRoom.enableRoom(config);
            if(room == null){
                RoomManager.LOCK_GAME.remove(config);
                TotalManager.getPlugin().getLogger().error("启动房间时出错: "+config.getName()+"游戏地图在初始化队列或游戏地图为空");
                return false;
            }
            rooms.put(config.getName(),room);
            return true;
        }else{
            TotalManager.getPlugin().getLogger().error("启动房间时出错: "+config.getName()+"游戏房间被锁定!");
            return false;
        }

    }

    public GameRoomConfig getRoomConfig(String name){
        return roomConfig.getOrDefault(name,null);
    }

    public List<GameRoomConfig> getRoomConfigs(){
        return new ArrayList<>(roomConfig.values());
    }

    /**
     * 根据名称获取已经实例化的房间
     * @param name 房间名称
     * @return 游戏房间
     * */
    public GameRoom getRoom(String name){
        GameRoom room = rooms.getOrDefault(name,null);
        if(room == null || room.worldInfo == null){
            return null;
        }

        if(room.getWorldInfo().getConfig().getGameWorld() == null){
            return null;
        }
        return room;
    }

    /**
     * 关闭游戏房间
     * @param name 房间名称
     * */
    public void disEnableRoom(String name){
        if(rooms.containsKey(name)){
            rooms.get(name).onDisable();

        }
    }





    /**
     * 获取在游戏房间内的玩家
     * @param player 玩家 {@link Player}
     * @return 玩家对象 {@link PlayerInfo}
     * */
    public PlayerInfo getPlayerInfo(EntityHuman player){
        //TODO 获取游戏中的玩家
        if(playerJoin.containsKey(player.getName())) {
            String roomName = playerJoin.get(player.getName());
            if (!"".equalsIgnoreCase(roomName)) {
                if (rooms.containsKey(roomName)) {
                    return rooms.get(roomName).getPlayerInfo(player);
                }
            }
        }else{
            if(player.namedTag.contains("room")){
                String roomName = player.namedTag.getString("room");
                if (!"".equalsIgnoreCase(roomName)) {
                    if (rooms.containsKey(roomName)) {
                        return rooms.get(roomName).getPlayerInfo(player);
                    }
                }
            }

        }
        return null;
    }



    /**
     * 初始化房间管理器
     * @param file 配置文件
     * @return 房间管理类
     * */
    public static RoomManager initGameRoomConfig(File file){
        Map<String, GameRoomConfig> map = new LinkedHashMap<>();
        if(file.isDirectory()){
            File[] dirNameList = file.listFiles();
            if(dirNameList != null && dirNameList.length > 0) {
                for (File nameFile : dirNameList) {
                    if(nameFile.isDirectory()){
                        String roomName = nameFile.getName();
                        GameRoomConfig roomConfig = GameRoomConfig.getGameRoomConfigByFile(roomName,nameFile);
                        if(roomConfig != null){
                            TotalManager.sendMessageToConsole(language.getLanguage("room-loading-success","&a加载房间 [1] 完成",roomName));
                            map.put(roomName,roomConfig);

                        }else{
                            TotalManager.sendMessageToConsole(language.getLanguage("room-loading-error","&c加载房间 [1] 失败",roomName));

                        }
                    }
                }
            }
        }
        return new RoomManager(map);
    }

    /*
     * ***********************************************
     *
     * 模板事件 可不更改
     *
     * ***********************************************
     * */

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        //TODO 断线重连 上线
        Player player = event.getPlayer();
        if(playerJoin.containsKey(player.getName())){
            player.setFoodEnabled(false);
            player.setGamemode(2);
            String room = playerJoin.get(player.getName());
            if(hasGameRoom(room)) {
                GameRoom room1 = getRoom(room);
                if (room1 == null) {
                    reset(player);
                    playerJoin.remove(player.getName());
                    player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
                    return;
                }
                if (room1.getType() != GameRoom.GameType.END && !room1.close) {
                    PlayerInfo info = room1.getPlayerInfo(player);
                    if (info != null) {
                        info.setPlayer(player);
                        info.setLeave(false);
                        if (room1.getType() == GameRoom.GameType.WAIT) {
                            room1.quitPlayerInfo(info,true);
                            return;
                        } else {
                            if (info.isWatch() || info.getTeamInfo() == null) {
                                room1.joinWatch(info);
                            } else {
                                info.death(null);
                            }
                        }
                    }
                }
            }
            reset(player);
        }else {
            for(GameRoomConfig gameRoomConfig: getRoomConfigs()){
                if(gameRoomConfig.getWorldInfo().getGameWorld() ==  player.level){
                    reset(player);
                }
            }
            if(player.getGamemode() == 3) {
                player.setGamemode(0);
            }
        }

    }


    /**
     * 游戏地图的爆炸保护
     * */

    @EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent event){
        Level level = event.getPosition().getLevel();
        GameRoom room = getGameRoomByLevel(level);
        if(room != null) {
            ArrayList<Block> blocks = new ArrayList<>(event.getBlockList());
            if(room.roomConfig.canBreak.size() > 0){
                for(String block: room.roomConfig.canBreak){
                    int bid = 0;
                    try{
                        bid = Integer.parseInt(block);
                    }catch (Exception ignore){}
                    if(bid > 0) {
                        blocks.add(Block.get(bid));
                    }
                }
            }
            for (Block block : event.getBlockList()) {
                if (!room.worldInfo.getPlaceBlock().contains(block)) {
                    blocks.remove(block);

                }else{
                    room.worldInfo.getPlaceBlock().remove(block);
                }
            }
            event.setBlockList(blocks);
        }
    }



    private void reset(Player player){
        PlayerInfo info = getPlayerInfo(player);
        player.setNameTag(player.getName());
        playerJoin.remove(player.getName());
        player.setHealth(player.getMaxHealth());
        if(info != null){
            player.getInventory().setContents(info.inventory);
            player.getEnderChestInventory().setContents(info.eInventory);
        }
        player.removeAllEffects();
        player.setGamemode(0);
        player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
    }




    /**
     * 进入地图就传送到游戏房间
     * */
    @EventHandler
    public void onLevelTransfer(EntityLevelChangeEvent event){
        Entity entity = event.getEntity();
        Level level = event.getTarget();
        GameRoom room = getGameRoomByLevel(level);
        if(entity instanceof Player) {
            PlayerInfo info = getPlayerInfo((Player) entity);
            if(info == null){
                info = new PlayerInfo((Player) entity);
            }
            if (room != null) {
                //不能阻止正常进入游戏
                if(info.getPlayerType() == PlayerInfo.PlayerType.WAIT){
                    if(room.equals(info.getGameRoom())){
                        return;
                    }
                }else if(room.equals(info.getGameRoom())){
                    //断线重连
                    return;
                }
                if(info.getGameRoom() != null){
                    info.getGameRoom().quitPlayerInfo(info,false);
                }
                switch (room.joinPlayerInfo(info,true)){
                    case CAN_WATCH:
                        room.joinWatch(info);
                        break;
                    case NO_LEVEL:
                    case NO_JOIN:
                        event.setCancelled();
                        TotalManager.sendMessageToObject(language.getLanguage("world-ban-join","&c你无法进入该地图"),entity);
                        if(Server.getInstance().getDefaultLevel() != null) {
                            info.getPlayer().teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
                        }else{
                            info.getPlayer().teleport(info.getPlayer().getLevel().getSafeSpawn());
                        }
                        break;
                    default:break;
                }

            }else{
                if(info.getGameRoom() != null){
                    if(info.isLeave()){
                        return;
                    }

                    if(!info.getGameRoom().getWorldInfo().getConfig().getWaitPosition().getLevel().getFolderName().equalsIgnoreCase(level.getFolderName())) {
                        info.getGameRoom().quitPlayerInfo(info, false);
                    }
                }
            }
        }

    }
    @EventHandler(ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent event){
        for(GameRoomConfig gameRoomConfig: TotalManager.getRoomManager().roomConfig.values()){
            if(gameRoomConfig.getWorldInfo().getGameWorld() != null){
                if(gameRoomConfig.worldInfo.getGameWorld().
                        getFolderName().equalsIgnoreCase(event.getLevel().getFolderName())){
                    event.setCancelled();
                    return;
                }
            }

        }
    }



    @EventHandler
    public void onGameRoomCreateEvent(GameRoomCreateEvent event){
        GameRoomConfig roomConfig = event.getRoom().roomConfig;
        //TODO 从Manager中加载
        for(String dlcName: roomConfig.roomDlc){
            IGameRoomDlc dlc = GameRoomDlcManager.loadDlc(dlcName);
            if(dlc != null){
                event.addDlc(dlc);
                dlc.onEnable(event.getRoom());
            }
        }
    }



    /**
     * TODO 玩家攻击事件
     * */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof EntityHuman){
            PlayerInfo playerInfo = getPlayerInfo((EntityHuman) event.getEntity());
            if(playerInfo != null) {

                if (playerInfo.isWatch()) {
                    playerInfo.sendForceMessage(language.getLanguage("player-gamemode-3","&c你处于观察者模式"));
                    event.setCancelled();
                    return;
                }
                GameRoom room = playerInfo.getGameRoom();
                if (room.getType() == GameRoom.GameType.WAIT) {
                    event.setCancelled();
                    return;
                }

                /////////////
                //会重复
                if (playerInfo.getPlayerType() == PlayerInfo.PlayerType.WAIT) {
                    event.setCancelled();
                    return;
                }

                //TODO 弓箭击中玩家
                if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                    if (event instanceof EntityDamageByEntityEvent) {
                        Entity damagers = (((EntityDamageByEntityEvent) event).getDamager());
                        if (damagers instanceof Player) {
                            PlayerInfo playerInfo1 = TotalManager.getRoomManager().getPlayerInfo((Player) damagers);
                            if (playerInfo1 != null) {
                                playerInfo1.addSound(Sound.RANDOM_ORB);
                                double h = event.getEntity().getHealth() - event.getFinalDamage();
                                if (h < 0) {
                                    h = 0;
                                }
                                playerInfo1.sendTip(language.getLanguage("player-attack-by-arrow","&e目标: &c❤[1]",String.format("%.1f", h)));
                            }

                        }


                    }
                }
                if (event instanceof EntityDamageByEntityEvent) {
                    //TODO 免受TNT爆炸伤害
                    Entity entity = ((EntityDamageByEntityEvent) event).getDamager();
                    if (entity instanceof EntityPrimedTNT) {
                        event.setDamage(room.getRoomConfig().tntDamage);
                    }
                    if(entity instanceof EntityTnt){
                        PlayerInfo target = ((EntityTnt) entity).getTarget();
                        if(target != null){
                            if(!target.equals(playerInfo) && (target.getTeamInfo() != null && !target.getTeamInfo().equals(playerInfo.getTeamInfo()))){
                                playerInfo.setDamageByInfo(target);
                            }else{
                                event.setCancelled();
                                return;
                            }
                        }
                    }
                    //击退..
                    if(room.roomConfig.knockConfig.enable){
                        event.getEntity().setMotion(FunctionManager.knockBack(event.getEntity(),entity,
                                room.roomConfig.knockConfig.speed,
                                room.roomConfig.knockConfig.force,
                                room.roomConfig.knockConfig.motionY));
                        ((EntityDamageByEntityEvent) event).setKnockBack(0f);
                    }

                    if (entity instanceof EntityHuman) {
                        PlayerInfo damageInfo = room.getPlayerInfo((EntityHuman) entity);
                        if (damageInfo != null) {
                            if (damageInfo.isWatch()) {
                                event.setCancelled();
                                return;
                            }
                            ///////////////// 阻止队伍PVP///////////////
                            //TODO 阻止队伍PVP
                            TeamInfo t1 = playerInfo.getTeamInfo();
                            TeamInfo t2 = damageInfo.getTeamInfo();
                            if (t1 != null && t2 != null) {
                                if (t1.getTeamConfig().getName().equalsIgnoreCase(t2.getTeamConfig().getName())) {
                                    if(!t1.getTeamConfig().getTeamConfig().isCanPvp()) {
                                        event.setCancelled();
                                        return;
                                    }
                                }
                            }
                            ///////////////// 阻止队伍PVP///////////////
                            playerInfo.setDamageByInfo(damageInfo);
                        } else {
                            event.setCancelled();
                        }
                    }

                }
                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    event.setCancelled();
                    playerInfo.death(event);
                }
                if (event.getFinalDamage() + 1 > playerInfo.getPlayer().getHealth()) {
                    event.setCancelled();
                    playerInfo.death(event);
                    for (EntityDamageEvent.DamageModifier modifier : EntityDamageEvent.DamageModifier.values()) {
                        event.setDamage(0, modifier);
                    }
                }
                if(!event.isCancelled()){
                    if(TotalManager.getConfig().getBoolean("display-damage",true)){
                        DamageFloatTextEntity floatTextEntity = new DamageFloatTextEntity(
                                TextFormat.colorize('&',"&c-"+String.format("%.1f",event.getDamage())),
                                event.getEntity().chunk,Entity.getDefaultNBT(
                                event.getEntity().getPosition().add(0,0.8,0)
                        ));
                        floatTextEntity.spawnToAll();

                        floatTextEntity.addMotion(1,0.8,90 / 180f);
                    }
                }
            }
        }
    }




    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        //TODO 断线重连 - 离线状态下
        Player player = event.getPlayer();
        if(playerJoin.containsKey(player.getName())){
            String roomName = playerJoin.get(player.getName());
            GameRoom room = getRoom(roomName);
            if(room != null){
                if(room.getType() != GameRoom.GameType.START ){
                    PlayerInfo info = room.getPlayerInfo(player);
                    if(info != null){
                        room.quitPlayerInfo(info,true);
                    }

                }else{
                    PlayerInfo info = room.getPlayerInfo(player);
                    if(info != null){
                        if(info.isWatch()){
                            room.quitPlayerInfo(info,true);
                            return;
                        }
                        player.getInventory().clearAll();
                        info.setLeave(true);
                    }
                }
            }
        }
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            Item item = event.getItem();

            if (playerJoin.containsKey(player.getName())) {
                String roomName = playerJoin.get(player.getName());
                GameRoom room = getRoom(roomName);
                if (room != null) {
                    PlayerInfo info = room.getPlayerInfo(player);
                    if(info != null) {
                        if(item.hasCompoundTag() && item.getNamedTag().contains(TotalManager.GAME_NAME)){
                            event.setCancelled();
                            String itemName = item.getNamedTag().getString(TotalManager.GAME_NAME);
                            ICustomItem iButtonItem = ButtonItemManager.getButtonItem(itemName);
                            if(iButtonItem != null){
                                iButtonItem.onClick(info);
                                if(iButtonItem.canBeUse()){
                                    Item ic = item.clone();
                                    ic.setCount(1);
                                    player.getInventory().removeItem(ic);
                                }
                            }
                        }
                        //TODO 投掷TNT
                        if(item.getId() == new BlockTNT().getId()){
                            event.setCancelled();
                            Item ic = item.clone();
                            ic.setCount(1);
                            player.getInventory().removeItem(ic);
//                            Block[] blocks = player.getLineOfSight(2);  // 这里的参数100表示最远搜索距离
//                            Vector3 lastBlockPos = blocks[blocks.length - 1].getLocation();
//                            Vector3 precisePos = lastBlockPos.add(player.getDirectionVector().multiply(0.5f));
//                            Vector3 v3 = FunctionManager.k(precisePos,player,0.6f,2.0f);
                            CompoundTag nbt = new CompoundTag()
                                    .putList(new ListTag<DoubleTag>("Pos")
                                            .add(new DoubleTag("", player.x))
                                            .add(new DoubleTag("", player.y + player.getEyeHeight()))
                                            .add(new DoubleTag("", player.z)))
                                    .putList(new ListTag<DoubleTag>("Motion")
                                            .add(new DoubleTag("", -Math.sin(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI) * 1.2))
                                            .add(new DoubleTag("", -Math.sin(player.pitch / 180 * Math.PI) * 1.2))
                                            .add(new DoubleTag("", Math.cos(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI) * 1.2)))
                                    .putList(new ListTag<FloatTag>("Rotation")
                                            .add(new FloatTag("", (player.yaw > 180 ? 360 : 0) - (float) player.yaw))
                                            .add(new FloatTag("", (float) -player.pitch)));

                            EntityTnt entityTnt = new EntityTnt(player.chunk,nbt,info,80);
                            entityTnt.spawnToAll();


                        }

                    }

                    Block block = event.getBlock();

                    if(room.roomConfig.items.size() > 0 && room.roomConfig.roundChest) {
                        if (room.getType() == GameType.START) {
                            ItemConfig config = room.getRandomItemConfig(block);
                            if (config != null) {
                                BlockEntity entityChest = block.level.getBlockEntity(block);
                                if (entityChest instanceof InventoryHolder && entityChest instanceof BlockEntityNameable) {
                                    LinkedHashMap<Integer, Item> items = room.getRandomItem(((InventoryHolder) entityChest)
                                            .getInventory().getSize(), block);
                                    if (items.size() > 0) {
                                        ((InventoryHolder) entityChest).getInventory().setContents(items);
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

    }






    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        if(playerJoin.containsKey(player.getName())) {
            String roomName = playerJoin.get(player.getName());
            GameRoom room = getRoom(roomName);
            if (room != null) {
                if(room.getType() == GameType.WAIT){
                    event.setCancelled();
                    return;
                }
                Item item = event.getItem();
                if (item.hasCompoundTag() && item.getNamedTag().contains(TotalManager.GAME_NAME)) {
                    event.setCancelled();
                }
            }
        }
    }




    @EventHandler
    public void onFrom(PlayerFormRespondedEvent event){
        if(event.wasClosed()){
            return;
        }
        Player player = event.getPlayer();
        if(DisPlayWindowsFrom.FROM.containsKey(player.getName())){
            GameFrom simple = DisPlayWindowsFrom.FROM.get(player.getName());
            if (onGameFrom(event, player, simple)) {
                return;
            }

        }
        int fromId = 102;
        if(event.getFormID() == fromId && event.getResponse() instanceof FormResponseSimple){
            PlayerInfo info = TotalManager.getRoomManager().getPlayerInfo(player);
            if(info != null){
                if(info.getGameRoom() == null || info.getGameRoom().getType() == GameType.START){
                    return;
                }
                TeamInfo teamInfo = info.getGameRoom().getTeamInfos().get(((FormResponseSimple) event.getResponse())
                        .getClickedButtonId());
                if(!teamInfo.join(info)){
                    info.sendMessage(language.getLanguage("player-joined-team","&c你已经加入了 [1]",teamInfo.toString()));
                }else{
                    info.sendMessage(language.getLanguage("player-join-team-success","&a加入&r[1] &a成功",teamInfo.toString()));
                    player.getInventory().setItem(0,teamInfo.getTeamConfig().getTeamConfig().getBlockWoolColor());
                    for (Map.Entry<Integer, Item> entry : info.armor.entrySet()) {
                        Item item;
                        if(entry.getValue() instanceof ItemColorArmor){
                            ItemColorArmor colorArmor = (ItemColorArmor) entry.getValue();
                            colorArmor.setColor(teamInfo.getTeamConfig().getRgb());
                            item = colorArmor;
                        }else{
                            item = entry.getValue();
                        }
                        player.getInventory().setArmorItem(entry.getKey(), item);
                    }
                }
            }

        }

    }

    private boolean onGameFrom(PlayerFormRespondedEvent event, Player player, GameFrom simple) {
        if(simple.getId() == event.getFormID()) {
            if (event.getResponse() instanceof FormResponseSimple) {
                BaseIButton button = simple.getBaseIButtons().get(((FormResponseSimple) event.getResponse())
                        .getClickedButtonId());
                button.onClick(player);
            }
            return true;

        }else{
            DisPlayWindowsFrom.FROM.remove(player.getName());
        }
        return false;
    }

    @EventHandler
    public void onItemChange(InventoryTransactionEvent event) {
        InventoryTransaction transaction = event.getTransaction();
        for (InventoryAction action : transaction.getActions()) {
            for (Inventory inventory : transaction.getInventories()) {
                if (inventory instanceof ChestInventoryPanel) {
                    Player player = ((ChestInventoryPanel) inventory).getPlayer();
                    event.setCancelled();
                    Item i = action.getSourceItem();
                    if(i.hasCompoundTag() && i.getNamedTag().contains("index")){
                        int index = i.getNamedTag().getInt("index");
                        BasePlayPanelItemInstance item = ((ChestInventoryPanel) inventory).getPanel().getOrDefault(index,null);

                        if(item != null){
                            ((ChestInventoryPanel) inventory).clickSolt = index;
                            item.onClick((ChestInventoryPanel) inventory,player);
                            ((ChestInventoryPanel) inventory).update();
                        }
                    }

                }
                if(inventory instanceof PlayerInventory){
                    EntityHuman player =((PlayerInventory) inventory).getHolder();
                    PlayerInfo playerInfo = getPlayerInfo(player);
                    if(playerInfo != null){
                        GameRoom gameRoom = playerInfo.getGameRoom();
                        if(gameRoom != null){
                            if(gameRoom.getType() == GameType.WAIT){
                                event.setCancelled();
                            }
                        }
                    }
                }
            }
        }
    }



    @EventHandler
    public void onExecuteCommand(PlayerCommandPreprocessEvent event){
        PlayerInfo info = getPlayerInfo(event.getPlayer());
        if(info != null){
            GameRoom room = info.getGameRoom();
            if(room != null) {
                for(String cmd: room.getRoomConfig().banCommand){
                    if(event.getMessage().contains(cmd)){
                        event.setCancelled();
                    }
                }
            }
        }

    }




    @EventHandler
    public void onCraft(CraftItemEvent event){
        Player player = event.getPlayer();
        GameRoom room = getGameRoomByLevel(player.getLevel());
        if(room != null && room.roomConfig.banCraft) {
            PlayerInfo info = room.getPlayerInfo(player);
            if (info != null) {
                event.setCancelled();
            }
        }
    }

    /**
     * 限制玩家放置方块事件

     * */
    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event){
        Level level = event.getBlock().level;

        Block block = event.getBlock();
        Item item = event.getItem();
        if(item.hasCompoundTag() && (item.getNamedTag().contains(TotalManager.GAME_NAME)
        )){
            event.setCancelled();
            return;
        }
        GameRoom room = getGameRoomByLevel(level);
        if(room != null){
            PlayerInfo info = room.getPlayerInfo(event.getPlayer());
            if(info != null) {
                if (info.isWatch()) {
                    info.sendMessage(language.getLanguage("event-place-block-cancel-watch","&c观察状态下不能放置方块"));
                    event.setCancelled();

                }else{
                    room.worldInfo.onChangeBlock(block,true);
                }

            }

//            //TODO 放置TNT
//            if (block instanceof BlockTNT) {
//                ((BlockTNT) block).prime();
//                event.setCancelled();
//                if(info != null) {
//                    EntityTnt entityTnt = new EntityTnt(event.getBlock().getChunk(), Entity.getDefaultNBT(event.getBlock()));
//                    entityTnt.setTick(60);
//                    entityTnt.setTarget(info);
//                    entityTnt.spawnToAll();
//                    Item i2 = item.clone();
//                    i2.setCount(1);
//                    event.getPlayer().getInventory().removeItem(i2);
//                }
//            }
        }
    }


    /**
     * 限制玩家放置方块事件

     * */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Level level = event.getBlock().level;

        Item item = event.getItem();
        if(item.hasCompoundTag() && (item.getNamedTag().contains(TotalManager.GAME_NAME)
        )){
            event.setCancelled();
            return;
        }
        Block block = event.getBlock();
        GameRoom room = getGameRoomByLevel(level);
        if(room != null){
            PlayerInfo info = room.getPlayerInfo(event.getPlayer());
            if(info != null) {
                if(info.isWatch()) {
                    info.sendMessage(language.getLanguage("event-break-block-cancel","&c无法破坏地图方块"));
                    event.setCancelled();
                }
                if(room.roomConfig.banBreak.size() > 0){
                    if(room.roomConfig.banBreak.contains(block.getId()+"")){
                        if(!room.roomConfig.canBreak.contains(block.getId()+"")){
                            event.setCancelled();
                        }else{
                            room.addSound(Sound.BLOCK_END_PORTAL_FRAME_FILL);
                        }
                    }
                    room.worldInfo.onChangeBlock(block, false);

                }else {

                    if (room.worldInfo.getPlaceBlock().contains(block) || room.roomConfig.canBreak.contains(block.getId()+"")) {
                        room.worldInfo.onChangeBlock(block, false);
                    } else {
                        if(!room.roomConfig.canBreak.contains(block.getId()+"")){
                            info.sendMessage(language.getLanguage("event-break-block-cancel","&c无法破坏地图方块"));
                            event.setCancelled();
                        }else{
                            room.addSound(Sound.BLOCK_END_PORTAL_FRAME_FILL);
                        }
                    }
                }

            }
        }
    }
    /**
     * 修改玩家聊天信息事件

     * */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(PlayerChatEvent event){
        PlayerInfo info = getPlayerInfo(event.getPlayer());
        if(info != null){
            GameRoom room = info.getGameRoom();
            if(room != null){
                if(info.isWatch()){
                    room.sendMessageOnWatch(info+" &r>> "+event.getMessage());
                }else{
                    String msg = event.getMessage();
                    if(msg.startsWith("@") || msg.startsWith("!")){
//
                        info.getGameRoom().sendFaceMessage( language.getLanguage("player-speak-in-room-message-all","&l&7(全体消息)&r [1]&r >> [2]",
                            info.toString(),msg.substring(1)));
                    }else{
                        TeamInfo teamInfo = info.getTeamInfo();
                        if(teamInfo != null){
                            if(info.isDeath()){
                                room.sendMessageOnDeath(language.getLanguage("player-speak-in-room-message-death",
                                        "[1]&7(死亡) &r>> [2]",
                                        info.toString(),msg));
                            }else {
                                teamInfo.sendMessage(language.getLanguage("player-speak-in-room-message-team",  "[1]&7[2] &f>>&r [3]",
                                        teamInfo.getTeamConfig().getNameColor()+teamInfo.getTeamConfig().getName(),
                                        info.getPlayer().getName(),
                                        msg));
                            }
                        }else{
                            room.sendMessage(language.getLanguage("player-speak-in-room-message","[1] &f>>&r [2]",
                                    info.toString(),
                                    msg));
                        }
                    }
                }
                event.setCancelled();
            }
        }
    }



}
