package org.sobadfish.gamedemo.room;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import de.theamychan.scoreboard.network.Scoreboard;
import org.sobadfish.gamedemo.dlc.IGameEndJudge;
import org.sobadfish.gamedemo.dlc.IGameRoomDlc;
import org.sobadfish.gamedemo.event.*;
import org.sobadfish.gamedemo.item.button.FollowItem;
import org.sobadfish.gamedemo.item.button.RoomQuitItem;
import org.sobadfish.gamedemo.item.button.TeamChoseItem;
import org.sobadfish.gamedemo.manager.*;
import org.sobadfish.gamedemo.player.PlayerData;
import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.player.team.TeamInfo;
import org.sobadfish.gamedemo.player.team.config.TeamInfoConfig;
import org.sobadfish.gamedemo.room.config.GameRoomConfig;
import org.sobadfish.gamedemo.room.config.ItemConfig;
import org.sobadfish.gamedemo.room.event.EventControl;
import org.sobadfish.gamedemo.room.floattext.FloatTextInfo;
import org.sobadfish.gamedemo.room.floattext.FloatTextInfoConfig;
import org.sobadfish.gamedemo.room.world.WorldInfo;
import org.sobadfish.gamedemo.tools.Utils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 小游戏房间信息
 * @author Sobadfish
 *  2022/9/9
 */
public class GameRoom {

    public GameRoomConfig roomConfig;


    private boolean isInit = true;

    private boolean isMax;

    private boolean teamAll;


    private final ArrayList<FloatTextInfo> floatTextInfos = new ArrayList<>();

    //房间内的玩家
    private final CopyOnWriteArrayList<PlayerInfo> playerInfos = new CopyOnWriteArrayList<>();

    private final LinkedHashMap<PlayerInfo, Scoreboard> scoreboards = new LinkedHashMap<>();

    private boolean hasStart;

    public int loadTime = -1;

    private GameType type;

    private final ArrayList<TeamInfo> teamInfos = new ArrayList<>();

    private List<IGameRoomDlc> gameRoomDlc = new ArrayList<>();

    /**
     * 地图配置
     * */
    public WorldInfo worldInfo;

    public boolean close;

    /**
     * 事件控制器
     * */
    private final EventControl eventControl;

    private GameRoom(GameRoomConfig roomConfig){
        this.roomConfig = roomConfig;
        this.worldInfo = new WorldInfo(this,roomConfig.worldInfo);

        type = GameType.WAIT;
        for(TeamInfoConfig config: getRoomConfig().getTeamConfigs()){
            teamInfos.add(new TeamInfo(this,config));
        }

        //启动事件
        eventControl = new EventControl(this,roomConfig.eventConfig);
        eventControl.initAll(this);
    }

    public List<IGameRoomDlc> getGameRoomDlc() {
        return gameRoomDlc;
    }

    public ArrayList<FloatTextInfo> getFloatTextInfos() {
        return floatTextInfos;
    }

    public LinkedHashMap<PlayerInfo, Scoreboard> getScoreboards() {
        return scoreboards;
    }

    public CopyOnWriteArrayList<PlayerInfo> getPlayerInfos() {
        return playerInfos;
    }

    public GameRoomConfig getRoomConfig() {
        return roomConfig;
    }

    public ItemConfig getRandomItemConfig(Block block){
        if(roomConfig.items.containsKey(block.getId()+"")){
            return roomConfig.items.get(block.getId()+"");
        }
        return null;
    }

    /**
     * 获取事件控制器
     *
     * @return 事件的控制器
     * */
    public EventControl getEventControl() {
        return eventControl;
    }


    public GameType getType() {
        return type;
    }

    public boolean isStart() {
        return getType() == GameType.START;
    }

    public enum GameType{
        /**
         * WAIT: 等待 START: 开始 END: 结束 CLOSE: 关闭
         * */
        WAIT,START,END,CLOSE
    }

    public PlayerInfo getPlayerInfo(EntityHuman player){
        if(playerInfos.contains(new PlayerInfo(player))){
            return playerInfos.get(playerInfos.indexOf(new PlayerInfo(player)));
        }
        return null;
    }

    public void sendMessageOnWatch(String msg) {
        ArrayList<PlayerInfo> watchPlayer = new ArrayList<>();
        for(PlayerInfo info: playerInfos){
            if(info.isWatch()){
                watchPlayer.add(info);
            }
        }
        watchPlayer.forEach(dp -> dp.sendMessage(msg));
    }

    public void joinWatch(PlayerInfo info,boolean isTeleport){
        //TODO 欢迎加入观察者大家庭
        if(!playerInfos.contains(info)){


            info.init();
            info.setGameRoom(this);
            if(info.getPlayer() instanceof Player) {
                TotalManager.getRoomManager().playerJoin.put(info.getPlayer().getName(),getRoomConfig().name);
            }
            playerInfos.add(info);

        }
        if(info.getPlayer() instanceof Player) {
            ((Player)info.getPlayer()).setGamemode(3);
        }

        info.setMoveSpeed(1.5f);
        info.setPlayerType(PlayerInfo.PlayerType.WATCH);
        info.getPlayer().getInventory().setItem(8,ButtonItemManager.getItem(RoomQuitItem.class));
        info.getPlayer().getInventory().setItem(5, ButtonItemManager.getItem(FollowItem.class));
        info.getPlayer().getInventory().setHeldItemSlot(0);
        sendMessage(TotalManager.getLanguage().getLanguage("watcher-join-room-message",
                "&7[1]&7 成为了旁观者 （[2]）",
                info.toString(),getWatchPlayers().size()+""));
        info.sendMessage(TotalManager.getLanguage().getLanguage("display-to-watcher-join-room","&e你可以等待游戏结束 也可以手动退出游戏房间"));
        if(isTeleport) {
            Position position = getTeamInfos().get(0).getSpawnLocation();
            position.add(0, 64, 0);
            position.level = getWorldInfo().getConfig().getGameWorld();
            info.getPlayer().teleport(position);
        }
    }

    public void joinWatch(PlayerInfo info) {
       joinWatch(info,true);

    }

    public static GameRoom enableRoom(GameRoomConfig roomConfig){

        if(roomConfig.getWorldInfo().getGameWorld() == null){
            return null;
        }
        if(WorldResetManager.RESET_QUEUE.containsKey(roomConfig.name)){
            return null;
        }

        GameRoom room = new GameRoom(roomConfig);
        GameRoomCreateEvent createEvent = new GameRoomCreateEvent(room,TotalManager.getPlugin());
        TotalManager.getPlugin().getServer().getPluginManager().callEvent(createEvent);
        return room;
    }

    public JoinType joinPlayerInfo(PlayerInfo info,boolean sendMessage){
        if(WorldResetManager.RESET_QUEUE.containsKey(roomConfig.name)){
            return JoinType.NO_JOIN;
        }
        boolean cutIn = false;
        if(info.getGameRoom() == null){
            if(info.getPlayer() instanceof Player) {
                if(!((Player) info.getPlayer()).isOnline()){
                    return JoinType.NO_ONLINE;
                }
            }

            if(getType() != GameType.WAIT){
                if(getType() == GameType.END || getType() == GameType.CLOSE){
                    return JoinType.NO_JOIN;
                }
                if(roomConfig.playerCutIn && !getPlayerInfos().contains(info)){
                    if(getRoomConfig().getMaxPlayerSize() > getInRoomPlayers().size()){
                        cutIn = true;
                    }else{
                        return JoinType.CAN_WATCH;
                    }
                }
                if(!cutIn) {
                    return JoinType.CAN_WATCH;
                }
            }
            if(getWorldInfo().getConfig().getGameWorld() == null || getWorldInfo().getConfig().getGameWorld().getSafeSpawn() == null){
                return JoinType.NO_LEVEL;
            }

            PlayerJoinRoomEvent event = new PlayerJoinRoomEvent(info,this,TotalManager.getPlugin());
            event.setSend(sendMessage);
            Server.getInstance().getPluginManager().callEvent(event);
            if(event.isCancelled()){
                return JoinType.NO_JOIN;
            }
            if(!eventToJoinPlayer(event)){
                return JoinType.NO_JOIN;
            }

            if(info.getPlayer() instanceof Player) {
                ((Player) info.getPlayer()).setFoodEnabled(false);
                ((Player) info.getPlayer()).setGamemode(2);
            }else{
                CompoundTag tag = info.getPlayer().namedTag;
                tag.putString("room",getRoomConfig().getName());

            }
            info.sendForceTitle("",1);
            info.sendForceSubTitle("");

            info.init();
            info.setGameRoom(this);
            if(info.getPlayer() instanceof Player) {
                TotalManager.getRoomManager().playerJoin.put(info.getPlayer().getName(),getRoomConfig().name);
            }

            if(!cutIn) {
                if (roomConfig.teamConfigs.size() > 1 && roomConfig.playerChoseTeam) {
                    info.getPlayer().getInventory().setItem(6, ButtonItemManager.getItem(TeamChoseItem.class));

                }
                info.getPlayer().getInventory().setItem(8, ButtonItemManager.getItem(RoomQuitItem.class));
                info.setPlayerType(PlayerInfo.PlayerType.WAIT);
                playerInfos.add(info);
                info.getPlayer().teleport(getWorldInfo().getConfig().getWaitPosition());
                if(info.getPlayer() instanceof Player) {
                    ((Player)info.getPlayer()).setGamemode(2);
                }
                sendMessage(TotalManager.getLanguage().getLanguage("player-join-room",
                        "[1]&e加入了游戏 &7([2]/[3])"
                        ,info.toString()
                        ,(playerInfos.size())+""
                        ,(getRoomConfig().getMaxPlayerSize())+""));
            }else{
                if (teamInfos.size() > 1) {
                    //TODO 找到玩家最少的队伍
                    int lowPlayer = 0;
                    TeamInfo low = teamInfos.get(0);
                    for(TeamInfo teamInfo: teamInfos){
                        int size = teamInfo.getInRoomPlayer().size();
                        if(lowPlayer == 0){
                            lowPlayer = size;
                        }
                        if(size > 0 && lowPlayer >= size){
                            low = teamInfo;
                            lowPlayer = size;
                        }
                    }
                    low.mjoin(info);
                }else{
                    TeamInfo teamInfo = teamInfos.get(0);
                    teamInfo.mjoin(info);

                }
                sendMessage(TotalManager.getLanguage().getLanguage("player-cut-join-room",
                        "[1]&e中途加入了游戏 &7([2]/[3])"
                        ,info.toString()
                        ,(playerInfos.size()+1)+""
                        ,(getRoomConfig().getMaxPlayerSize())+""));
                playerInfos.add(info);
                info.spawn();

            }



            if(isInit){
                isInit = false;
            }

        }else {

            if(info.getGameRoom().getType() != GameType.END && info.getGameRoom() == this){
                return JoinType.NO_JOIN;
            }else{
                info.getGameRoom().quitPlayerInfo(info,true);
                return JoinType.CAN_WATCH;
            }
        }
        return JoinType.CAN_JOIN;

    }

    /**
     * 玩家加入房间的一些监听
     * */
    private boolean eventToJoinPlayer(PlayerJoinRoomEvent event) {
        PlayerInfo info = event.getPlayerInfo();
        GameRoom gameRoom = event.getRoom();
        if (TotalManager.getRoomManager().playerJoin.containsKey(info.getPlayer().getName())) {
            String roomName = TotalManager.getRoomManager().playerJoin.get(info.getPlayer().getName());
            if (roomName.equalsIgnoreCase(event.getRoom().getRoomConfig().name) && gameRoom.getPlayerInfos().contains(info)) {
                if(event.isSend()) {
                    info.sendForceMessage(TotalManager.language.getLanguage("player-join-in-room","&c你已经在游戏房间内了"));
                }
                return false;
            }
            if (TotalManager.getRoomManager().hasGameRoom(roomName)) {
                GameRoom room = TotalManager.getRoomManager().getRoom(roomName);
                if (room.getType() != GameRoom.GameType.END && room.getPlayerInfos().contains(info)) {
                    if (room.getPlayerInfo(info.getPlayer()).getPlayerType() != PlayerInfo.PlayerType.WATCH ||
                            room.getPlayerInfo(info.getPlayer()).getPlayerType() != PlayerInfo.PlayerType.LEAVE) {
                        if(event.isSend()) {
                            info.sendForceMessage(TotalManager.language.getLanguage("player-join-in-room","&c你已经在游戏房间内了"));
                        }
                        return false;

                    }
                }
            }
        }
        if(gameRoom.getType() != GameRoom.GameType.WAIT){

            if(GameType.END != gameRoom.getType()){
//                //TODO 中途加入
                if(!gameRoom.roomConfig.playerCutIn){
                    event.setCancelled();
                    return false;
                }else{
                    return true;
                }
//                if(!gameRoom.getRoomConfig().hasWatch){
//                    event.setCancelled();
//                    return false;
//                }
//
            }
            if(event.isSend()) {
                info.sendForceMessage(TotalManager.language.getLanguage("player-join-in-room-started","&c游戏已经开始了"));
            }
            return false;
        }
        if(gameRoom.getPlayerInfos().size() == gameRoom.getRoomConfig().getMaxPlayerSize()){
            if(event.isSend()) {
                info.sendForceMessage(TotalManager.language.getLanguage("player-join-in-room-max","&c房间满了"));
            }
            return false;
        }
        return true;

    }

    public WorldInfo getWorldInfo() {
        return worldInfo;
    }

    public ArrayList<TeamInfo> getTeamInfos() {
        return teamInfos;
    }

    public void setType(GameType type) {
        this.type = type;
    }

    /**
     * 根据名称
     * */
    private TeamInfo getTeamInfo(String name){
        for(PlayerInfo info : playerInfos){
            if(info.getTeamInfo() != null &&
                    info.getTeamInfo().getTeamConfig().getName().equalsIgnoreCase(name)){
                return info.getTeamInfo();
            }
        }
        return null;
    }

    public enum JoinType{
        //加入类型
        NO_ONLINE,NO_JOIN,NO_LEVEL,CAN_WATCH,CAN_JOIN
    }

    /**
     * 分配玩家
     * */
    private boolean allotOfAverage(){

        int t =  (int) Math.ceil(playerInfos.size() / (double)getRoomConfig().getTeamConfigs().size());
        PlayerInfo listener;
        LinkedList<PlayerInfo> noTeam = getNoTeamPlayers();
        //打乱，，这样就可以随机分配了
        Collections.shuffle(noTeam);
        // TODO 检测是否一个队伍里有太多的人 拆掉多余的人
        for (TeamInfo manager: teamInfos){
            if(manager.getTeamConfig().getTeamConfig().maxPlayer > 0){
                if(manager.getTeamPlayers().size() > manager.getTeamConfig().getTeamConfig().maxPlayer){
                    int size = manager.getTeamPlayers().size() - manager.getTeamConfig().getTeamConfig().maxPlayer;
                    for(int i = 0;i < size;i++){
                        PlayerInfo info = manager.getTeamPlayers().remove(manager.getTeamPlayers().size()-1);
                        noTeam.add(info);
                    }
                }
            }
            if(manager.getTeamPlayers().size() > t){
                int size = manager.getTeamPlayers().size() - t;
                for(int i = 0;i < size;i++){
                    PlayerInfo info = manager.getTeamPlayers().remove(manager.getTeamPlayers().size()-1);
                    noTeam.add(info);
                }
            }
        }
        int w = 0;
        while(noTeam.size() > 0){
          if(w > playerInfos.size()){
              //TODO 防止服主不会设置导致的死循环
              break;
          }
            for (TeamInfo manager: teamInfos){
                int s = manager.getTeamPlayers().size();
                if(s == 0
                        || (s < t )){
                    if(manager.getTeamConfig().getTeamConfig().maxPlayer > 0) {
                        if(t > manager.getTeamConfig().getTeamConfig().maxPlayer) {
                            t += (int)Math.ceil(((t - manager.getTeamConfig().getTeamConfig().maxPlayer) /
                                    (double)getRoomConfig().getTeamConfigs().size()));
                        }
                        if(manager.getTeamConfig().getTeamConfig().maxPlayer > s) {
                            if (noTeam.size() > 0) {
                                listener = noTeam.poll();
                                manager.mjoin(listener);
                            }
                        }
                    }else{
                        if (noTeam.size() > 0) {
                            listener = noTeam.poll();
                            manager.mjoin(listener);
                        }
                    }
                }else{
                    if(manager.getTeamPlayers().size() > t){
                        int size =  manager.getTeamPlayers().size();
                        LinkedList<PlayerInfo> playerInfos = new LinkedList<>(manager.getTeamPlayers());
                        for(int i = 0;i <size - t;i++) {
                            noTeam.add(playerInfos.pollLast());
                        }
                    }
                }
            }
            w++;
        }
        if(noTeam.size() > 0){
            for(PlayerInfo info: noTeam){
                quitPlayerInfo(info,true);
            }
        }
        return true;
    }


    public LinkedList<PlayerInfo> getNoTeamPlayers(){
        LinkedList<PlayerInfo> noTeam = new LinkedList<>();
        for(PlayerInfo playerInfo: playerInfos){
            if(playerInfo.getTeamInfo() == null){
                noTeam.add(playerInfo);
            }
        }
        return noTeam;
    }

    /**
     * 还在游戏内的玩家
     * @return 在游戏内的玩家列表
     * */
    public ArrayList<PlayerInfo> getInRoomPlayers(){
        ArrayList<PlayerInfo> t = new ArrayList<>();
        for(PlayerInfo playerInfo: playerInfos){
            if(playerInfo.isInRoom()){
                t.add(playerInfo);
            }
        }
        return t;
    }

    /**
     * 获取等待救起的玩家
     * @return 等待救起的玩家列表
     * */
    public ArrayList<PlayerInfo> getWaitHelperPlayers(){
        ArrayList<PlayerInfo> t = new ArrayList<>();
        for(PlayerInfo playerInfo: playerInfos){
            if(playerInfo.isWaitHelper()){
                t.add(playerInfo);
            }
        }
        return t;
    }


    public ArrayList<TeamInfo> getLiveTeam(){
        ArrayList<TeamInfo> t = new ArrayList<>();
        for(TeamInfo teamInfo: teamInfos){
            if(teamInfo.isLoading()){
                t.add(teamInfo);
            }
        }
        return t;
    }


    public ArrayList<PlayerInfo> getIPlayerInfos() {
        ArrayList<PlayerInfo> p = new ArrayList<>();
        for(PlayerInfo info: playerInfos){
            if(info.getPlayer() instanceof Player){
                if(!info.isLeave()) {
                    p.add(info);
                }
            }
        }
        return p;
    }
    /**
     * 旁观者们
     * @return 旁观的玩家列表
     * */
    public ArrayList<PlayerInfo> getWatchPlayers(){
        ArrayList<PlayerInfo> t = new ArrayList<>();
        for(PlayerInfo playerInfo: playerInfos){
            if(playerInfo.isWatch()){
                t.add(playerInfo);
            }
        }
        return t;
    }

    /**
     * 还在游戏内的存活玩家
     * @return 在游戏中存活的玩家列表
     * */
    public ArrayList<PlayerInfo> getLivePlayers(){
        ArrayList<PlayerInfo> t = new ArrayList<>();
        for(PlayerInfo playerInfo: playerInfos){
            if(playerInfo.isLive()){
                t.add(playerInfo);
            }
        }
        return t;
    }



    /**
     * 将消息发送给阵亡的玩家
     * @param msg 消息
     * */
    public void sendMessageOnDeath(String msg){
        ArrayList<PlayerInfo> deathPlayer = new ArrayList<>();
        for(PlayerInfo info: playerInfos){
            if(info.isDeath()){
                deathPlayer.add(info);
            }
        }
        deathPlayer.forEach(dp -> dp.sendMessage(msg));
    }

    /**
     * 向游戏房间内全体玩家发送信息
     * 这个信息不带前缀
     * @param msg 文本信息
     * */
    public void sendTipMessage(String msg){
        for(PlayerInfo info: getPlayerInfos()){
            info.sendTipMessage(msg);
        }
    }

    /**
     * 向游戏房间内的玩家发送信息，不包含退出房间的玩家
     * 这个信息带前缀
     * @param msg 文本信息
     * */
    public void sendMessage(String msg){
        for(PlayerInfo info: getPlayerInfos()){
            info.sendMessage(msg);
        }
    }

    /**
     * 向游戏房间内全体玩家发送信息包含退出房间的玩家
     * 这个信息带前缀
     * @param msg 文本信息
     * */
    public void sendFaceMessage(String msg){
        for(PlayerInfo info: getPlayerInfos()){
            info.sendForceMessage(msg);
        }
    }

    /**
     * 向游戏房间内全体玩家发送标题
     * @param msg 标题信息
     * */
    public void sendTitle(String msg){
        for(PlayerInfo info: getPlayerInfos()){
            info.sendTitle(msg);
        }
    }

    /**
     * 向游戏房间内全体玩家发送小标题
     * @param msg 小标题信息
     * */
    public void sendSubTitle(String msg){
        for(PlayerInfo info: getPlayerInfos()){
            info.sendSubTitle(msg);
        }
    }

    /**
     * 向游戏房间内全体玩家发送底部信息提示
     * @param msg 提示信息
     * */
    public void sendTip(String msg){
        for(PlayerInfo info: getPlayerInfos()){
            info.sendTip(msg);
        }
    }

    /**
     * 向游戏房间内全体玩家发送底部信息提示
     * @param msg 提示信息
     * */
    public void sendActionBar(String msg){
        for(PlayerInfo info: getPlayerInfos()){
            info.sendActionBar(msg);
        }
    }

    /**
     * 向游戏房间内全体玩家发送声音
     * @param sound 声音
     * */
    public void addSound(Sound sound){
        for(PlayerInfo info: getPlayerInfos()){
            info.addSound(sound);
        }
    }

    /**
     * 给予游戏房间内的全部玩家药水效果
     * @param effect 药水效果
     * */
    public void addEffect(Effect effect){
        for(PlayerInfo info: getLivePlayers()){
            info.addEffect(effect);
        }
    }

    /**
     * 玩家离开游戏
     * @param info 玩家
     * @param teleport 是否传送回主出生点
     * @return 是否成功离开
     * */
    public boolean quitPlayerInfo(PlayerInfo info,boolean teleport){
        if(info != null) {
            info.isLeave = true;
            if (info.getPlayer() instanceof Player) {
                if (playerInfos.contains(info)) {
                    PlayerQuitRoomEvent event = new PlayerQuitRoomEvent(info, this,TotalManager.getPlugin());
                    Server.getInstance().getPluginManager().callEvent(event);
                    executeQuitCommand(event);
                    if(((Player) info.getPlayer()).isOnline()) {
                        if (teleport) {
                            info.getPlayer().teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
                        }
                        info.getPlayer().removeAllEffects();
                        ((Player) info.getPlayer()).setExperience(0, 0);
                    }
                    info.cancel();
                    TotalManager.getRoomManager().playerJoin.remove(info.getPlayer().getName());
                } else {
                    TotalManager.getRoomManager().playerJoin.remove(info.getPlayer().getName());

                }
            } else {
                info.getPlayer().close();
                playerInfos.remove(info);

            }
        }
        if (getIPlayerInfos().size() == 0) {
            onDisable();
        }
        return true;
    }

    private void executeQuitCommand(PlayerQuitRoomEvent event) {
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
            room.sendMessage(TotalManager.language.getLanguage("player-quit-room-echo-message","&c玩家 [1] 离开了游戏",event.getPlayerInfo().getPlayer().getName()));
        }
    }

    /**
     * 房间状态更新方法
     * */
    public void onUpdate(){
        if(close){
            return;
        }
        //TODO 当房间启动后
        if(getIPlayerInfos().size() == 0 && !isInit){
            onDisable();
            return;
        }
        switch (type){
            case WAIT:
                onWait();
                break;
            case START:
                eventControl.enable = true;
                worldInfo.isStart = true;
                try {
                    onStart();
                }catch (Exception e){
                    e.printStackTrace();
                    for(PlayerInfo playerInfo: new ArrayList<>(playerInfos)){
                        playerInfo.sendForceMessage(TotalManager.getLanguage().getLanguage("room-error","房间出现异常 请联系服主/管理员修复"));
                    }
                    onDisable();
                    return;
                }

                break;
            case END:
                //TODO 房间结束
                onEnd();
                break;
            case CLOSE:
                onDisable();
                break;
            default:break;
        }

        //移除编外人员
        for(PlayerInfo info: getInRoomPlayers()){
            if(!TotalManager.getRoomManager().playerJoin.containsKey(info.getPlayer().getName())){
                if(info.getPlayer() instanceof Player){
                    playerInfos.remove(info);
                }
            }
        }

    }

    private void onEnd() {
        if(loadTime == -1){
            loadTime = 10;
        }

        for(PlayerInfo playerInfo:getLivePlayers()){
            Utils.spawnFirework(playerInfo.getPosition());
        }

        if(loadTime == 0){
            type = GameType.CLOSE;

        }

    }

    /**
     * 执行这个可以将游戏直接结束
     * 传入胜利的队伍
     * @param teamInfo 队伍信息
     * @param more 在单队伍模式中填写 false 在多队伍模式中填写 true
     * */
    public void gameEnd(TeamInfo teamInfo,boolean more){
        if(!more){
            teamInfo.echoDefeat();
        }
        teamInfo.echoVictory();
        teamInfo.givePlayerAward();
        end();


    }

    /**
     * 执行次方法后，游戏结束
     * */
    public void end(){
        type = GameType.END;
        worldInfo.setClose(true);
        loadTime = 5;
    }

    private void onStart() {

        eventControl.run();
        if(loadTime == -1 && teamAll){
            //TODO 房间首次重置
            for(FloatTextInfoConfig config: roomConfig.floatTextInfoConfigs){
                FloatTextInfo info = new FloatTextInfo(config).init(this);
                if(info != null){
                    floatTextInfos.add(info);
                }
            }
            //TODO 当房间开始

            for(PlayerInfo i : getPlayerInfos()){
                if(roomConfig.gameInWait > 0){
                    i.waitTime = roomConfig.gameInWait;
                }
                try {
                    i.spawn();
                }catch (Exception e){
                    e.printStackTrace();
                    i.sendForceMessage(TotalManager.getLanguage().getLanguage("room-start-game-teleport-error","&c出现未知原因影响导致无法正常传送 正在重新将你移动中"));
                    try {
                        i.spawn();
                    }catch (Exception e1){
                        i.sendForceMessage(TotalManager.getLanguage().getLanguage("room-start-game-teleport-error-back","&c移动失败 请尝试重新进入游戏"));
                        quitPlayerInfo(i,true);
                    }
                }
            }
            sendTitle(TotalManager.getLanguage().getLanguage("room-start-game","&c游戏开始"));

            loadTime = getRoomConfig().time;
            worldInfo = new WorldInfo(this,getRoomConfig().worldInfo);
            if(!hasStart) {
                GameRoomStartEvent event = new GameRoomStartEvent(this, TotalManager.getPlugin());
                Server.getInstance().getPluginManager().callEvent(event);
                if(!event.isCancelled()){
                    displayGameStartMsg();
                    hasStart = true;
                }else{
                    onDisable();
                }

            }


        }

        for (TeamInfo teamInfo : teamInfos) {
            teamInfo.onUpdate();
            for(IGameRoomDlc dlc: gameRoomDlc){
                if(teamInfo.isLoading()){
                    dlc.onTeamUpdate(teamInfo);
                }
            }
        }
        //TODO 可以在这里实现胜利的条件
        ////////////////////////// 示例算法 ///////////////////////////
        IGameEndJudge endJudge = null;

        for(IGameRoomDlc dlc: gameRoomDlc){
            if(dlc instanceof IGameEndJudge){
                endJudge = (IGameEndJudge) dlc;
            }
            dlc.onGameUpdate(this);
        }
        if(endJudge == null) {
            demoGameEnd();
        }else{
            if(endJudge.judgeGameEnd(this)){
                end();
                return;
            }
            demoGameEnd();
        }

        ////////////////////////// 示例算法 ///////////////////////////
    }

    /**
     * 游戏开始时提示的文本信息
     * */
    private void displayGameStartMsg() {
        String line = "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■";
        for(String s: this.getRoomConfig().gameStartMessage){
            this.sendTipMessage(FunctionManager.getCentontString(s,line.length()));
        }
    }

    /**
     * 游戏结束的条件示例代码
     * 可参考实现自己的逻辑
     * 这个方法的主要实现逻辑为 PVP，存活到最后为胜利条件
     * */
    private void demoGameEnd(){

        if(loadTime > 0) {
            //TODO 在房间倒计时内
            if(!roomConfig.infiniteTime) {
                if (getRoomConfig().teamConfigs.size() > 1) {
                    if (getLiveTeam().size() <= 1) {
                        //当有多个队伍的时候 只剩余一个队伍时将这个队伍中所有的玩家都扔进 胜利的玩家列表。
                        TeamInfo teamInfo = getLiveTeam().get(0);
                        teamInfo.getVictoryPlayers().addAll(teamInfo.getTeamPlayers());
                        gameEnd(teamInfo, true);
                    }
                } else {
                    //当仅有一个队伍时，把最终存活的玩家放到胜利列表中
                    TeamInfo teamInfo = getTeamInfos().get(0);
                    ArrayList<PlayerInfo> pl = teamInfo.getLivePlayer();
                    //判断是否为唯一幸存者
                    if (pl.size() <= 1) {
                        teamInfo.getVictoryPlayers().add(pl.get(0));
                        gameEnd(teamInfo, false);
                    }
                }
            }else{
                if(getInRoomPlayers().size() == 0){
                    end();
                }
            }
        } else{
            //TODO 在房间倒计时结束
            TeamInfo successInfo;
            if(getRoomConfig().teamConfigs.size() > 1) {
                //在这个判断条件下为多队伍状态

                //TODO 倒计时结束后 找到血量最高的队伍判胜
                ArrayList<TeamInfo> teamInfos = getLiveTeam();
                if (teamInfos.size() > 0) {

                    int pl = 0;
                    double dh = 0;

                    int vw = 0;
                    successInfo = teamInfos.get(0);
                    for (TeamInfo info : teamInfos) {
                        //TODO 先找到权重高的
                        if (info.getTeamConfig().getTeamConfig().victoryWeight > 0) {
                            if(info.getTeamConfig().getTeamConfig().victoryWeight > vw){
                                vw = info.getTeamConfig().getTeamConfig().victoryWeight;
                            }
                            successInfo = info;
                        }
                    }
                    if(vw == 0){
                        for (TeamInfo info : teamInfos) {
                            ArrayList<PlayerInfo> successInfos = info.getLivePlayer();
                            if (successInfos.size() > pl) {
                                pl = successInfos.size();
                                successInfo = info;
                                dh = info.getAllHealth();

                            } else if (successInfos.size() == pl && pl > 0) {
                                double dh2 = info.getAllHealth();
                                if (dh2 > dh) {
                                    successInfo = info;
                                    dh = dh2;
                                }
                            }
                        }
                    }
                    successInfo.getVictoryPlayers().addAll(successInfo.getTeamPlayers());
                    gameEnd(successInfo,true);
                }
            }else{
                //在这个判断条件下为单队伍状态
                //TODO 倒计时结束后 找到血量最高的玩家判胜，其余玩家均失败
                double h = 0;
                PlayerInfo successPlayerInfo = null;
                TeamInfo teamInfo = getTeamInfos().get(0);
                for(PlayerInfo info: teamInfo.getLivePlayer()){
                    if(info.player.getHealth() > h){
                        successPlayerInfo = info;
                        h = info.player.getHealth();
                    }
                }
                if(successPlayerInfo == null){
                    successPlayerInfo = teamInfo.getLivePlayer().get(0);
                }
                teamInfo.getVictoryPlayers().add(successPlayerInfo);
                for(PlayerInfo info: teamInfo.getLivePlayer()){
                    if(!info.equals(successPlayerInfo)){
                        teamInfo.getDefeatPlayers().add(info);
                    }
                }
                //游戏结束
                gameEnd(teamInfo,false);
            }


        }
    }


    private void onWait() {
        if(getPlayerInfos().size() >= getRoomConfig().minPlayerSize){
            if(loadTime == -1){
                loadTime = getRoomConfig().waitTime;
                sendMessage(TotalManager.getLanguage().getLanguage("room-wait-player-min","&2到达最低人数限制&e [1] &2秒后开始游戏",
                        loadTime+""));

            }
        }else {
            loadTime = -1;
        }
        if(getPlayerInfos().size() == getRoomConfig().getMaxPlayerSize()){
            if(!isMax){
                isMax = true;
                loadTime = getRoomConfig().getMaxWaitTime();
            }
        }
        if(loadTime >= 1) {
            sendTip(TotalManager.getLanguage().getLanguage("room-wait-start","&e距离开始还剩 &a [1] &e秒",
                    loadTime+""));
            if(loadTime <= 5){
                switch (loadTime){
                    case 5: sendTitle(TotalManager.getLanguage().getLanguage("room-wait-time-5","&a5"));break;
                    case 4: sendTitle(TotalManager.getLanguage().getLanguage("room-wait-time-4","&e4"));break;
                    case 3: sendTitle(TotalManager.getLanguage().getLanguage("room-wait-time-3","&63"));break;
                    case 2: sendTitle(TotalManager.getLanguage().getLanguage("room-wait-time-2","&42"));break;
                    case 1: sendTitle(TotalManager.getLanguage().getLanguage("room-wait-time-1","&41"));break;
                    default:
                        sendTitle("");break;

                }
                //音效
                addSound(Sound.RANDOM_CLICK);

            }
            if(loadTime == 1){
                type = GameType.START;
                loadTime = -1;
                //分配玩家算法
                if(allotOfAverage()){
                    teamAll = true;
                }


            }
        }else{
            sendTip(TotalManager.getLanguage().getLanguage("room-waiting","&a等待中"));
        }
    }


    /**
     * 房间是否为无团队模式
     * 也就是房间中的队伍是多队伍还是单一队伍
     * @return 是否为无团队
     * */
    public boolean isOnlyTeam(){
        return getRoomConfig().teamConfigs.size() == 1;
    }

    /**
     * 关闭房间
     * 已设计好算法，不建议修改
     * */
    public void onDisable(){
        if(close){
            return;
        }
        close = true;
        type = GameType.CLOSE;
        for(IGameRoomDlc dlc: gameRoomDlc){
            dlc.onDisable(this);
        }

        if(hasStart) {
//            roomConfig.save();
            GameCloseEvent event = new GameCloseEvent(this, TotalManager.getPlugin());
            Server.getInstance().getPluginManager().callEvent(event);
            worldInfo.setClose(true);
            //房间结束后的执行逻辑
            if(getRoomConfig().isAutomaticNextRound){
                sendMessage(TotalManager.getLanguage().getLanguage("player-auto-join-next-room","&7即将自动进行下一局"));
                for(PlayerInfo playerInfo: getInRoomPlayers()){
                    RandomJoinManager.joinManager.nextJoin(playerInfo);
                }
            }
            //TODO 房间被关闭 释放一些资源
            for (PlayerInfo info : playerInfos) {
                info.clear();
                if (info.getPlayer() instanceof Player) {
                    quitPlayerInfo(info, true);
                }
            }

            //浮空字释放
            for(FloatTextInfo floatTextInfo: floatTextInfos){
                floatTextInfo.gameFloatText.toClose();
            }

            String level = worldInfo.getConfig().getLevel();
            Level level1 = getWorldInfo().getConfig().getGameWorld();
            for(Entity entity: new CopyOnWriteArrayList<>(level1.getEntities())){
                if(entity instanceof Player){
                    //这里出现的玩家就是没有清出地图的玩家
                    entity.teleport(Server.getInstance().getDefaultLevel().getSpawnLocation());
                    TotalManager.getRoomManager().playerJoin.remove(entity.getName());
                    ((Player) entity).setGamemode(0);
                    entity.removeAllEffects();
                    ((Player) entity).getInventory().clearAll();
                    ((Player) entity).getEnderChestInventory().clearAll();
                    ((Player) entity).getFoodData().reset();
                    continue;
                }
                if(entity != null && !entity.isClosed()){
                    entity.close();
                }

            }
            //卸载区块就炸...
//            level1.unloadChunks();
            worldInfo = null;
            WorldResetManager.RESET_QUEUE.put(getRoomConfig().name, level);
        }else{
            worldInfo.setClose(true);
            worldInfo = null;
            TotalManager.getRoomManager().getRooms().remove(getRoomConfig().name);
            RoomManager.LOCK_GAME.remove(getRoomConfig());
        }

    }

    /**
     * 设置资源箱的物品
     * @param size 箱子的格子数量
     * @param block 可以存放物品的容器
     * @return 箱子内物品
     * */
    public LinkedHashMap<Integer, Item> getRandomItem(int size, Block block){
        LinkedHashMap<Integer,Item> itemLinkedHashMap = new LinkedHashMap<>();
        if(worldInfo == null){
            return itemLinkedHashMap;
        }
        if(!worldInfo.clickChest.containsKey(block)){
            List<Item> list = getRoundItems(block);
            if(list.size() > 0) {
                for (int i = 0; i < size; i++) {
                    if (Utils.rand(0, 100) <= getRoomConfig().getRound()) {
                        itemLinkedHashMap.put(i, list.get(new Random().nextInt(list.size())));
                    }
                }
                worldInfo.clickChest(block);
            }
        }
        return itemLinkedHashMap;

    }

    /**
     * 根据容器方块获取对应的物品列表
     * @param block 容器方块
     * @return 物品列表
     * */
    public List<Item> getRoundItems(Block block){
        if(roomConfig.items.containsKey(block.getId()+"")){
            return roomConfig.items.get(block.getId()+"").items;
        }
        return new ArrayList<>();
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof GameRoom){
            if(((GameRoom) obj).roomConfig == null || this.roomConfig == null){
                return false;
            }
            return ((GameRoom) obj).roomConfig.equals(this.roomConfig);
        }
        return false;
    }
}
