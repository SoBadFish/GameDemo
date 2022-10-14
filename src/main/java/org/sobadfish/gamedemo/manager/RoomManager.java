package org.sobadfish.gamedemo.manager;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBed;
import cn.nukkit.block.BlockCraftingTable;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityNameable;
import cn.nukkit.command.ConsoleCommandSender;
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
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.level.WeatherChangeEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemColorArmor;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.event.*;
import org.sobadfish.gamedemo.item.ItemIDSunName;
import org.sobadfish.gamedemo.item.button.RoomQuitItem;
import org.sobadfish.gamedemo.item.button.TeamChoseItem;
import org.sobadfish.gamedemo.panel.ChestInventoryPanel;
import org.sobadfish.gamedemo.panel.DisPlayWindowsFrom;
import org.sobadfish.gamedemo.panel.from.GameFrom;
import org.sobadfish.gamedemo.panel.from.button.BaseIButton;
import org.sobadfish.gamedemo.panel.items.BasePlayPanelItemInstance;
import org.sobadfish.gamedemo.panel.items.PlayerItem;
import org.sobadfish.gamedemo.player.PlayerData;
import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.player.team.TeamInfo;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.room.GameRoom.GameType;
import org.sobadfish.gamedemo.room.config.GameRoomConfig;
import org.sobadfish.gamedemo.room.config.ItemConfig;
import org.sobadfish.gamedemo.tools.Utils;

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
        PlayerInfo info = TotalManager.getRoomManager().getPlayerInfo(player.getPlayer());
        if(info != null){
            player = info;
        }

        if (TotalManager.getRoomManager().hasRoom(roomName)) {
            if (!TotalManager.getRoomManager().hasGameRoom(roomName)) {
                if(!TotalManager.getRoomManager().enableRoom(TotalManager.getRoomManager().getRoomConfig(roomName))){
                    player.sendForceMessage("&c" + roomName + " 还没准备好");
                    return false;
                }
            }else{
                GameRoom room = TotalManager.getRoomManager().getRoom(roomName);
                if(room != null){
                    if(RoomManager.LOCK_GAME.contains(room.getRoomConfig()) && room.getType() == GameType.END || room.getType() == GameType.CLOSE){
                        player.sendForceMessage("&c" + roomName + " 还没准备好");
                        return false;
                    }
                    if(room.getWorldInfo().getConfig().getGameWorld() == null){
                        return false;
                    }
                    if(room.getType() == GameType.END ||room.getType() == GameType.CLOSE){
                        player.sendForceMessage("&c" + roomName + " 结算中");
                        return false;
                    }
                }
            }

            GameRoom room = TotalManager.getRoomManager().getRoom(roomName);
            if(room == null){
                return false;
            }
            switch (room.joinPlayerInfo(player,true)){
                case CAN_WATCH:
                    if(!room.getRoomConfig().hasWatch){
                        player.sendForceMessage("&c该房间开始后不允许旁观");
                    }else{

                        if(player.getGameRoom() != null && !player.isWatch()){
                            player.sendForceMessage("&c你无法进入此房间");
                            return false;
                        }else{
                            room.joinWatch(player);
                            return true;
                        }
                    }
                    break;
                case NO_LEVEL:
                    player.sendForceMessage("&c这个房间正在准备中，稍等一会吧");
                    break;
                case NO_ONLINE:
                    break;
                case NO_JOIN:
                    player.sendForceMessage("&c该房间不允许加入");
                    break;
                default:
                    //可以加入
                    return true;
            }
        } else {
            player.sendForceMessage("&c不存在 &r" + roomName + " &c房间");

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
            return false;
        }
        if(!RoomManager.LOCK_GAME.contains(config)){
            RoomManager.LOCK_GAME.add(config);

            GameRoom room = GameRoom.enableRoom(config);
            if(room == null){
                RoomManager.LOCK_GAME.remove(config);
                return false;
            }
            rooms.put(config.getName(),room);
            return true;
        }else{

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
                            TotalManager.sendMessageToConsole("&a加载房间 "+roomName+" 完成");
                            map.put(roomName,roomConfig);

                        }else{
                            TotalManager.sendMessageToConsole("&c加载房间 "+roomName+" 失败");

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
            if(hasGameRoom(room)){
                GameRoom room1 = getRoom(room);
                if(room1 == null){
                    playerJoin.remove(player.getName());
                    player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
                    return;
                }
                if(room1.getType() != GameRoom.GameType.END && !room1.close ){
                    PlayerInfo info = room1.getPlayerInfo(player);
                    if(info != null){
                        info.setPlayer(player);
                        info.setLeave(false);
                        if(room1.getType() == GameRoom.GameType.WAIT){
                            if(room1.worldInfo.getConfig().getGameWorld() != null){
                                player.teleport(room1.worldInfo.getConfig().getGameWorld().getSafeSpawn());
                                player.teleport(room1.getWorldInfo().getConfig().getWaitPosition());
                            }

                        }else{
                            if(info.isWatch() || info.getTeamInfo() == null){
                                room1.joinWatch(info);
                            }else{
                                info.death(null);
                            }

                        }
                    }else{
                        reset(player);
                    }

                }else{
                    reset(player);
                }
            }else{
                //TODO 无房间回到出生点
                reset(player);
            }
        }else if(player.getGamemode() == 3){
            player.setGamemode(0);
        }

    }


    private void reset(Player player){
        player.setNameTag(player.getName());
        playerJoin.remove(player.getName());
        player.setHealth(player.getMaxHealth());
        player.getInventory().clearAll();
        player.removeAllEffects();
        player.setGamemode(0);
        player.getEnderChestInventory().clearAll();
        player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
    }


    @EventHandler
    public void onGameStartEvent(GameRoomStartEvent event){
        GameRoom room = event.getRoom();
        String line = "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■";
        for(String s: room.getRoomConfig().gameStartMessage){
            room.sendTipMessage(FunctionManager.getCentontString(s,line.length()));
        }
    }

    /**
     * 进入地图就传送到游戏房间
     * */
    @EventHandler
    public void onLevelTransfer(EntityLevelChangeEvent event){
        Entity entity = event.getEntity();
        Level level = event.getTarget();
        GameRoom room = getGameRoomByLevel(level);
        if(entity instanceof EntityHuman) {
            PlayerInfo info = getPlayerInfo((EntityHuman) entity);
            if(info == null){
                info = new PlayerInfo((EntityHuman) entity);
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
                        TotalManager.sendMessageToObject("&c你无法进入该地图",entity);
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



    /**
     * TODO 玩家攻击事件
     * */
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event){

        if(event.getEntity() instanceof Player){
            PlayerInfo playerInfo = getPlayerInfo((EntityHuman) event.getEntity());
            if(playerInfo != null) {
                if (playerInfo.isWatch()) {
                    playerInfo.sendForceMessage("&c你处于观察者模式");
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
                                playerInfo1.sendTip("&e目标: &c❤" + String.format("%.1f", h));
                            }

                        }


                    }
                }
                if (event instanceof EntityDamageByEntityEvent) {
                    //TODO 免受TNT爆炸伤害
                    Entity entity = ((EntityDamageByEntityEvent) event).getDamager();
                    if (entity instanceof EntityPrimedTNT) {
                        event.setDamage(2);
                    }

                    if (entity instanceof Player) {
                        PlayerInfo damageInfo = room.getPlayerInfo((Player) entity);
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
                                    event.setCancelled();
                                    return;
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
                    if(item.hasCompoundTag() && item.getNamedTag().getBoolean("quitItem")){
                        event.setCancelled();
                        quitRoomItem(player, roomName, room);
                        return;
                    }
                    if(item.hasCompoundTag() && item.getNamedTag().getBoolean("follow")){
                        followPlayer(room.getPlayerInfo(player),room);
                        event.setCancelled();
                        return;
                    }

                    if(item.hasCompoundTag() && item.getNamedTag().getBoolean("choseTeam")){
                        event.setCancelled();
                        choseteamItem(player, room);

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

    private void choseteamItem(Player player, GameRoom room) {
        if(!TeamChoseItem.clickAgain.contains(player)){
            TeamChoseItem.clickAgain.add(player);
            player.sendTip("请再点击一次");
            return;
        }
        FormWindowSimple simple = new FormWindowSimple("请选择队伍","");
        for(TeamInfo teamInfoConfig: room.getTeamInfos()){
            Item wool = teamInfoConfig.getTeamConfig().getTeamConfig().getBlockWoolColor();
            simple.addButton(new ElementButton(TextFormat.colorize('&', teamInfoConfig +" &r"+teamInfoConfig.getTeamPlayers().size()+" / "+(room.getRoomConfig().getMaxPlayerSize() / room.getTeamInfos().size())),
                    new ElementButtonImageData("path",
                            ItemIDSunName.getIDByPath(wool.getId(),wool.getDamage()))));
        }
        player.showFormWindow(simple,102);
        TeamChoseItem.clickAgain.remove(player);
    }

    private void followPlayer(PlayerInfo info,GameRoom room){
        info.sendMessage("选择要传送的玩家");
        if (room == null){
            return;
        }
        disPlayUI(info, room);

    }

    private void disPlayProtect(PlayerInfo info,GameRoom room){
        List<BaseIButton> list = new ArrayList<>();
        //手机玩家
        for(PlayerInfo i: room.getLivePlayers()){
            list.add(new BaseIButton(new PlayerItem(i).getGUIButton(info)) {
                @Override
                public void onClick(Player player) {
                    player.teleport(i.getPlayer().getLocation());
                }
            });
        }
        DisPlayWindowsFrom.disPlayerCustomMenu((Player) info.getPlayer(),"传送玩家",list);

    }


    private void disPlayUI(PlayerInfo info, GameRoom room){
        //WIN10 玩家 故障，，，，
//        DisPlayerPanel playerPanel = new DisPlayerPanel();
//        playerPanel.displayPlayer(info,DisPlayerPanel.displayPlayers(room),"传送玩家");

        disPlayProtect(info, room);
    }

    private boolean quitRoomItem(Player player, String roomName, GameRoom room) {
        if(!RoomQuitItem.clickAgain.contains(player)){
            RoomQuitItem.clickAgain.add(player);
            player.sendTip("请再点击一次");
            return true;
        }
        RoomQuitItem.clickAgain.remove(player);
        if(room.quitPlayerInfo(room.getPlayerInfo(player),true)){
            player.sendMessage("你成功离开房间 "+roomName);
        }
        return false;
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


    //事件响应
    @EventHandler
    public void onQuitRoom(PlayerQuitRoomEvent event){
        if(event.performCommand){
            PlayerInfo info = event.getPlayerInfo();
            PlayerData data = TotalManager.getDataManager().getData(info.getName());
            data.setInfo(info);

            GameRoom room = event.getRoom();
            info.clear();

            if(info.getPlayer() instanceof Player && ((Player) info.getPlayer()).isOnline()){
                ((Player)info.getPlayer()).setFoodEnabled(false);
                room.getRoomConfig().quitRoomCommand.forEach(cmd-> Server.getInstance().dispatchCommand(((Player)info.getPlayer()),cmd));
            }
            if(info.isWatch()){
                return;
            }
            room.sendMessage("&c玩家 "+event.getPlayerInfo().getPlayer().getName()+" 离开了游戏");
        }
    }


    @EventHandler
    public void onGetExp(PlayerGetExpEvent event){
        String playerName = event.getPlayerName();
        Player player = Server.getInstance().getPlayer(playerName);
        if(player != null){
            player.sendMessage(TextFormat.colorize('&',"&b +"+event.getExp()+" 经验("+event.getCause()+")"));
            PlayerInfo info = TotalManager.getRoomManager().getPlayerInfo(player);
            PlayerData data = TotalManager.getDataManager().getData(playerName);

            if(info == null || info.getGameRoom() == null){

                TotalManager.sendTipMessageToObject("&l&m"+Utils.writeLine(5,"&a▁▁▁"),player);
                TotalManager.sendTipMessageToObject("&l"+Utils.writeLine(9,"&a﹉﹉"),player);
                String line = String.format("%20s","");
                player.sendMessage(line);
                String inputTitle = "&b&l小游戏经验\n";
                TotalManager.sendTipMessageToObject(FunctionManager.getCentontString(inputTitle,30),player);
                TotalManager.sendTipMessageToObject(FunctionManager.getCentontString("&b等级 "+data.getLevel()+String.format("%"+inputTitle.length()+"s","")+" 等级 "+(data.getLevel() + 1)+"\n",30),player);

                TotalManager.sendTipMessageToObject("&7["+data.getExpLine(20)+"&7]\n",player);

                String d = String.format("%.1f",data.getExpPercent() * 100.0);
                TotalManager.sendTipMessageToObject(FunctionManager.getCentontString("&b"+data.getExpString(data.getExp())+" &7/ &a"+data.getExpString(data.getNextLevelExp())+" &7("+d+"％)",40)+"\n",player);
                TotalManager.sendTipMessageToObject("&l&m"+Utils.writeLine(5,"&a▁▁▁"),player);
                TotalManager.sendTipMessageToObject("&l"+Utils.writeLine(9,"&a﹉﹉"),player);

            }
        }

    }

    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        if(playerJoin.containsKey(player.getName())){
            String roomName = playerJoin.get(player.getName());
            GameRoom room = getRoom(roomName);
            if(room != null){
                Item item = event.getItem();
                if(item.hasCompoundTag() && "quitItem".equalsIgnoreCase(item.getNamedTag().getString(TotalManager.GAME_NAME))){
                    player.getInventory().setHeldItemSlot(0);
                    if (quitRoomItem(player, roomName, room)) {
                        return;
                    }
                }
                if(item.hasCompoundTag() && "choseTeam".equalsIgnoreCase(item.getNamedTag().getString(TotalManager.GAME_NAME))){
                    player.getInventory().setHeldItemSlot(0);
                    choseteamItem(player, room);


                }
                if(item.hasCompoundTag() && "follow".equalsIgnoreCase(item.getNamedTag().getString(TotalManager.GAME_NAME))){
                    followPlayer(room.getPlayerInfo(player),room);
                    player.getInventory().setHeldItemSlot(0);
                }
            }
        }
    }



    @EventHandler
    public void onFrom(PlayerFormRespondedEvent event){
        if(event.wasClosed()){
            DisPlayWindowsFrom.FROM.remove(event.getPlayer().getName());
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
                    info.sendMessage("&c你已经加入了 "+ teamInfo);
                }else{
                    info.sendMessage("&a加入了&r"+ teamInfo +" &a成功");
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
    public void onTeamDefeat(TeamDefeatEvent event){

        final GameRoom room = event.getRoom();
        for (PlayerInfo info:event.getTeamInfo().getDefeatPlayers()) {

            room.getRoomConfig().defeatCommand.forEach(cmd->Server.getInstance().dispatchCommand(new ConsoleCommandSender(),cmd.replace("@p",info.getName())));
            if(event.getRoom().getRoomConfig().isAutomaticNextRound){
                info.sendMessage("&7即将自动进行下一局");
                RandomJoinManager.joinManager.nextJoin(info);
                //                ThreadManager.addThread(new AutoJoinGameRoomRunnable(5,info,event.getRoom(),null));

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
    public void onTeamVictory(TeamVictoryEvent event){
        event.getTeamInfo().sendTitle("&e&l胜利!",5);
        String line = "■■■■■■■■■■■■■■■■■■■■■■■■■■";
        event.getRoom().sendTipMessage("&a"+line);
        event.getRoom().sendTipMessage(FunctionManager.getCentontString("&b游戏结束",line.length()));
        event.getRoom().sendTipMessage("");
        for(PlayerInfo playerInfo: event.getTeamInfo().getVictoryPlayers()){
            event.getRoom().sendTipMessage(FunctionManager.getCentontString("&7   "+playerInfo.getPlayer().getName()+" 击杀："+(playerInfo.getKillCount())+" 助攻: "+playerInfo.getAssists(),line.length()));
            event.getRoom().getRoomConfig().victoryCommand.forEach(cmd->Server.getInstance().dispatchCommand(new ConsoleCommandSender(),cmd.replace("@p",playerInfo.getName())));
        }
        event.getRoom().sendTipMessage("&a"+line);


        event.getRoom().sendMessage("&a恭喜 "+event.getTeamInfo().getTeamConfig().getNameColor()+event.getTeamInfo().getTeamConfig().getName()+" &a 获得了胜利!");

    }


    @EventHandler
    public void onCraft(CraftItemEvent event){
        Player player = event.getPlayer();
        GameRoom room = getGameRoomByLevel(player.getLevel());
        if(room != null) {
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
                    info.sendMessage("&c观察状态下不能放置方块");
                    event.setCancelled();

                }else{
                    room.worldInfo.onChangeBlock(block,true);
                }

            }
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
                    info.sendMessage("&c无法破坏地图方块");
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
                            info.sendMessage("&c无法破坏地图方块");
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
                        info.getGameRoom().sendFaceMessage("&l&7(全体消息)&r "+info+"&r >> "+msg.substring(1));
                    }else{
                        TeamInfo teamInfo = info.getTeamInfo();
                        if(teamInfo != null){
                            if(info.isDeath()){
                                room.sendMessageOnDeath(info+"&7(死亡) &r>> "+msg);
                            }else {
                                teamInfo.sendMessage(teamInfo.getTeamConfig().getNameColor() + "[队伍]&7 " + info.getPlayer().getName() + " &f>>&r " + msg);
                            }
                        }else{
                            room.sendMessage(info+" &f>>&r "+msg);
                        }
                    }
                }
                event.setCancelled();
            }
        }
    }
    @EventHandler
    public void onPlayerJoinRoom(PlayerJoinRoomEvent event){
        PlayerInfo info = event.getPlayerInfo();
        GameRoom gameRoom = event.getRoom();
        if (TotalManager.getRoomManager().playerJoin.containsKey(info.getPlayer().getName())) {
            String roomName = TotalManager.getRoomManager().playerJoin.get(info.getPlayer().getName());
            if (roomName.equalsIgnoreCase(event.getRoom().getRoomConfig().name) && gameRoom.getPlayerInfos().contains(info)) {
                if(event.isSend()) {
                    info.sendForceMessage("&c你已经在这个房间内了");
                }
                event.setCancelled();
                return;
            }
            if (TotalManager.getRoomManager().hasGameRoom(roomName)) {
                GameRoom room = TotalManager.getRoomManager().getRoom(roomName);
                if (room.getType() != GameRoom.GameType.END && room.getPlayerInfos().contains(info)) {
                    if (room.getPlayerInfo(info.getPlayer()).getPlayerType() != PlayerInfo.PlayerType.WATCH ||
                            room.getPlayerInfo(info.getPlayer()).getPlayerType() != PlayerInfo.PlayerType.LEAVE) {
                        if(event.isSend()) {
                            info.sendForceMessage("&c你已经在游戏房间内了");
                        }
                        event.setCancelled();

                    }
                }
            }
        }
        if(gameRoom.getType() != GameRoom.GameType.WAIT){
            if(GameType.END != gameRoom.getType()){
                //TODO 或许还能旁观
                if(gameRoom.getRoomConfig().hasWatch){
                    event.setCancelled();
                    return;
                }

            }
            if(event.isSend()) {
                info.sendForceMessage("&c游戏已经开始了");
            }
            event.setCancelled();
            return;
        }
        if(gameRoom.getPlayerInfos().size() == gameRoom.getRoomConfig().getMaxPlayerSize()){
            if(event.isSend()) {
                info.sendForceMessage("&c房间满了");
            }
            event.setCancelled();
        }
        if(info.getPlayer() instanceof Player) {
            ((Player) info.getPlayer()).setFoodEnabled(false);
            ((Player) info.getPlayer()).setGamemode(2);
        }

    }


}
