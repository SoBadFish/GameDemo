package org.sobadfish.gamedemo.player;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemColorArmor;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.TextFormat;
import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;
import org.sobadfish.gamedemo.event.PlayerGameDeathEvent;
import org.sobadfish.gamedemo.item.button.OpenShopItem;
import org.sobadfish.gamedemo.manager.ButtonItemManager;
import org.sobadfish.gamedemo.manager.FunctionManager;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.player.message.ScoreBoardMessage;
import org.sobadfish.gamedemo.player.team.TeamInfo;
import org.sobadfish.gamedemo.player.team.config.TeamInfoConfig;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.room.event.IGameRoomEvent;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 玩家对象的信息
 * @author Sobadfish
 * @date 2022/9/9
 */
public class PlayerInfo {

    public EntityHuman player;

    private PlayerType playerType;

    private GameRoom gameRoom;

    private TeamInfo teamInfo;

    public boolean cancel;

    public boolean disable;

    public boolean isLeave;

    //记录信息
    public LinkedHashMap<PlayerData.DataType,Integer> statistics = new LinkedHashMap<>();

    //小游戏强制等待的时间
    public int waitTime = 0;

    //玩家复活次数
    public int reSpawnCount = 0;

    //攻击间隔
    public int damageTime = 0;

    //缓存攻击的玩家对象
    private PlayerInfo damageByInfo = null;

    public Map<Integer,Item> inventory;

    public  Map<Integer,Item> eInventory;

    //缓存玩家速度
    public float speed;

    //玩家生命
    public int health = 0;

    //内置经济系统
    public double money;

    //助攻
    public LinkedHashMap<PlayerInfo,Long> assistsPlayers = new LinkedHashMap<>();

    public LinkedHashMap<Integer,Item> armor = new LinkedHashMap<>();

    /**
     * 背包初始物品
     * */
    public LinkedHashMap<Integer,Item> inventoryItem = new LinkedHashMap<>();


    /**
     * 实例化
     * */
    public PlayerInfo(EntityHuman player){
        this.player = player;
        this.speed = player.getMovementSpeed();
    }

    public int getData(PlayerData.DataType type){
        if(statistics.containsKey(type)){
            return statistics.get(type);
        }
        return 0;
    }

    public void addData(PlayerData.DataType type,int value){
        if(statistics.containsKey(type)){
            statistics.put(type,statistics.get(type) + value);
        }else{
            statistics.put(type,value);
        }
    }

    public void addData(PlayerData.DataType type){
        if(statistics.containsKey(type)){
            statistics.put(type,statistics.get(type) + 1);
        }else{
            statistics.put(type,1);
        }
    }


    public LinkedHashMap<PlayerInfo, Long> getAssistsPlayers() {
        return assistsPlayers;
    }


    public EntityHuman getPlayer() {
        return player;
    }

    public void setPlayer(EntityHuman player){
        this.player = player;
    }

    public Level getLevel(){
        return player.getLevel();
    }


    public Location getLocation(){
        return player.getLocation();
    }

    public BlockFace getHorizontalFacing(){
        return player.getHorizontalFacing();
    }

    public String getName(){
        return player.getName();
    }

    public Position getPosition(){
        return player.getPosition();
    }

    public void setDamageByInfo(PlayerInfo damageByInfo) {
        if(damageByInfo != null) {

            this.damageByInfo = damageByInfo;
            damageTime = 5;
            assistsPlayers.put(damageByInfo,System.currentTimeMillis());
            //现身
            getPlayer().removeEffect(14);
        }
    }




    public void setTeamInfo(TeamInfo teamInfo) {
        this.teamInfo = teamInfo;
        if(teamInfo != null) {
            this.armor = teamInfo.getTeamConfig().getTeamConfig().getInventoryArmor();
            this.inventoryItem = teamInfo.getTeamConfig().getTeamConfig().getInventoryItem();
            ////////////////////////////// 设置生命
            this.health = 0;//重置
            this.health += teamInfo.getTeamConfig().getTeamConfig().getTeamSpawnCount();
        }
    }

    /**
     * 玩家数据初始化
     * */
    public void init(){
        if(TotalManager.getConfig().getBoolean("save-playerInventory",true)){
            inventory = getPlayer().getInventory().getContents();
            eInventory = getPlayer().getEnderChestInventory().getContents();
        }
        getPlayer().setHealth(getPlayer().getMaxHealth());
        if(getPlayer() instanceof Player) {
            ((Player)getPlayer()).getFoodData().reset();
        }
        getPlayer().getInventory().clearAll();
        getPlayer().getEnderChestInventory().clearAll();
        if(getPlayer() instanceof Player){
            ((Player) getPlayer()).removeAllWindows();
            ((Player) getPlayer()).setExperience(0,0);
        }
        //TODO 给玩家初始化物品
        getPlayer().getInventory().setHeldItemSlot(0);
    }

    private void addKill(PlayerInfo info){

        info.addData(PlayerData.DataType.KILL);
        //助攻累计
        for(PlayerInfo playerInfo: assistsPlayers.keySet()){
            if(playerInfo.equals(info)){
                continue;
            }
            info.addData(PlayerData.DataType.ASSISTS);
        }
    }


    /**
     * 发送无前缀信息
     * */
    public void sendTipMessage(String msg){
        if(getGameRoom() != null) {
            if (cancel || isLeave  || getGameRoom().getType() == GameRoom.GameType.END) {
                return;
            }
            TotalManager.sendTipMessageToObject(msg, getPlayer());
        }
    }
    /**
     * 发送信息
     * */
    public void sendMessage(String msg){
        if(getGameRoom() != null) {
            if (cancel || isLeave  || getGameRoom().getType() == GameRoom.GameType.END) {
                return;
            }
            TotalManager.sendMessageToObject(msg, getPlayer());
        }
    }
    /**
     * 发送音效
     * */
    public void addSound(Sound sound){
        if(cancel || isLeave){
            return;
        }
        getPlayer().getLevel().addSound(getPlayer(),sound);
    }

    /**
     * 增加效果
     * */
    public void addEffect(Effect effect) {
        if(cancel || isLeave){
            return;
        }
        getPlayer().addEffect(effect);
    }

    /**
     * 发送信息
     * */
    public void sendTitle(String msg,int time){
        if(cancel || isLeave){
            return;
        }
        if(getPlayer() instanceof Player) {
            TotalManager.sendTitle(msg, time * 20, (Player) getPlayer());
        }
    }
    /**
     * 发送信息
     * */
    public void sendTitle(String msg){
        sendTitle(msg,1);
    }
    /**
     * 发送信息
     * */
    public void sendSubTitle(String msg){
        if(cancel || isLeave ){
            return;
        }
        if(getPlayer() instanceof Player) {
            TotalManager.sendSubTitle(msg, ((Player) getPlayer()));
        }
    }

    public void removeScoreBoard(){
        if(gameRoom != null) {
            if (gameRoom.getScoreboards().containsKey(this)) {
                if (getPlayer() instanceof Player) {
                    ScoreboardAPI.removeScorebaord((Player) getPlayer(),
                            gameRoom.getScoreboards().get(this));
                    gameRoom.getScoreboards().remove(this);
                }

            }
        }
    }
    public boolean  isDeath(){
        return  playerType == PlayerType.DEATH;
    }

    public boolean isWatch(){
        return  playerType == PlayerType.WATCH;
    }

    public boolean isInRoom(){
        return !cancel && !isLeave;
    }

    public boolean isLive(){
        return !cancel && !isLeave && playerType != PlayerType.WATCH;
    }

    public void sendScore(ScoreBoardMessage message){
        if(getPlayer() instanceof Player) {
            if (message == null || getPlayer() == null || !((Player) getPlayer()).isOnline()) {
                removeScoreBoard();
                return;
            }
            if (((Player) getPlayer()).isOnline()) {
                try {
                    Scoreboard scoreboard = ScoreboardAPI.createScoreboard();
                    String title = message.getTitle();
                    ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay(DisplaySlot.SIDEBAR,
                            "dumy", TextFormat.colorize('&', title));

                    ArrayList<String> list = message.getLore();
                    for (int line = 0; line < list.size(); line++) {
                        String s = list.get(line);

                        scoreboardDisplay.addLine(TextFormat.colorize('&', s), line);
                    }
                    try {
                        gameRoom.getScoreboards().get(this).hideFor((Player) player);
                    } catch (Exception ignored) {
                    }
                    scoreboard.showFor((Player) player);
                    gameRoom.getScoreboards().put(this, scoreboard);
                } catch (Exception ignored) {
                }
            }
        }

    }

    public GameRoom getGameRoom() {
        return gameRoom;
    }


    public boolean isLeave() {
        return isLeave;
    }

    public void setGameRoom(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }

    public void setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    /**
     * 发送信息
     * */
    public void sendActionBar(String msg){
        if(cancel || isLeave){
            return;
        }
        if(getPlayer() instanceof Player) {
            ((Player) getPlayer()).sendActionBar(TextFormat.colorize('&',msg));
        }
    }

    /**
     * 发送信息
     * */
    public void sendTip(String msg){
        if(cancel || isLeave){
            return;
        }
        if(getPlayer() instanceof Player) {
            TotalManager.sendTip(msg, (Player) getPlayer());
        }
    }

    /**
     * 发送强制信息
     * */
    public void sendForceMessage(String msg){
        TotalManager.sendMessageToObject(msg, getPlayer());
    }

    /**
     * 发送强制信息
     * */
    public void sendForceTitle(String msg){
        if(player instanceof Player){
            ((Player) player).sendTitle(TextFormat.colorize('&',msg));
        }
    }

    /**
     * 发送强制信息
     * */
    public void sendForceTitle(String msg,int time){
        if(player instanceof Player){
            ((Player) player).sendTitle(TextFormat.colorize('&',msg),null,0,time,0);
        }
    }

    /**
     * 发送强制信息
     * */
    public void sendForceSubTitle(String msg){
        if(player instanceof Player){
            ((Player) player).setSubtitle(TextFormat.colorize('&',msg));
        }
    }

    public void spawn() {
        //TODO 玩家复活进入游戏
        //游戏刚开始也会进入这个方法
        if(isSendkey){
            isSendkey = false;
        }
        if (player instanceof Player) {
            if(gameRoom.getRoomConfig().enableFood){
                ((Player) player).setFoodEnabled(true);
                ((Player) player).getFoodData().reset();
            }
            if (!((Player) player).isOnline()) {
                playerType = PlayerType.LEAVE;
                return;
            }
        }

        player.getInventory().clearAll();


        boolean teleport;
        try {
            teleport = player.teleport(teamInfo.getSpawnLocation());
        }catch (Exception e){
            teleport = false;
        }
        if(!teleport){
            throw new NullPointerException(TotalManager.getLanguage().getLanguage("player-teleport-team-spawn-error","无法将玩家传送到队伍出生点"));
        }
        if (getPlayer() instanceof Player) {
            ((Player) getPlayer()).setGamemode(0);

            ((Player) getPlayer()).setExperience(0,0);


        }
        //TODO 初始装备
        for (Map.Entry<Integer, Item> entry : armor.entrySet()) {
            Item item;
            if(entry.getValue() instanceof ItemColorArmor){
                ItemColorArmor colorArmor = (ItemColorArmor) entry.getValue();
                BlockColor rgb = getTeamInfo().getTeamConfig().getRgb();
                if(rgb != null) {
                    colorArmor.setColor(rgb);
                }
                item = colorArmor;
            }else{
                item = entry.getValue();
            }

            player.getInventory().setArmorItem(entry.getKey(), item);
        }
        for (Map.Entry<Integer, Item> entry : inventoryItem.entrySet()) {
            Item item = entry.getValue();
            player.getInventory().setItem(entry.getKey(), item);
        }
        if(gameRoom != null && gameRoom.roomConfig.enableShop){
            player.getInventory().addItem(ButtonItemManager.getItem(OpenShopItem.class));
        }
        playerType = PlayerType.START;

    }


    /**
     * 取消
     * */
    public void cancel(){
        leave();

        cancel = true;
        disable = true;
        if(getGameRoom() != null) {
            getGameRoom().getPlayerInfos().remove(this);
        }
        setGameRoom(null);
    }

    private void leave(){
        clear();
        if(getTeamInfo() != null){
            getTeamInfo().quit(this);
        }
        sendScore(null);
        setLeave(true);
        playerType = PlayerType.LEAVE;
    }

    public void setLeave(boolean leave) {
        isLeave = leave;
        if(leave){
            playerType = PlayerType.LEAVE;

        }else{
            if(getGameRoom().getType() == GameRoom.GameType.WAIT){
                playerType = PlayerType.WAIT;
            }
            if(getGameRoom().getType() == GameRoom.GameType.START){
                playerType = PlayerType.START;
            }
        }
    }

    public void setMoveSpeed(float speed){
        player.setMovementSpeed(speed);
    }

    public void clear(){
        if(player instanceof Player){
            if(((Player) player).isOnline()){
                player.setNameTag(player.getName());
                player.getInventory().clearAll();
                player.setMovementSpeed(speed);
                player.getEnderChestInventory().clearAll();
                ((Player) player).getFoodData().reset();
                player.setHealth(player.getMaxHealth());
                ((Player) player).setExperience(0,0);
                if(inventory != null && eInventory != null){
                    player.getInventory().setContents(inventory);
                    player.getEnderChestInventory().setContents(eInventory);
                }
                if(getPlayer() instanceof Player) {
                    ((Player) getPlayer()).setGamemode(0);
                }
            }
        }
    }



    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PlayerInfo){
            return ((PlayerInfo) obj).getPlayer().getName().equalsIgnoreCase(getPlayer().getName());
        }
        return false;
    }



    @Override
    public String toString(){
        PlayerData data = TotalManager.getDataManager().getData(getName());
        String teamName = "&r";
        String playerName = "&7"+player.getName();
        if(teamInfo != null && !isWatch()){
            teamName = "&7[&r"+teamInfo.getTeamConfig().getNameColor()+teamInfo.getTeamConfig().getName()+"&7]&r";
            playerName = teamInfo.getTeamConfig().getNameColor()+" &7"+player.getName();
        }else if(isWatch()){
            teamName = TotalManager.getLanguage().getLanguage("player-watch-title","&7[旁观] ");
        }

        return TotalManager.getLanguage().getLanguage("player-info-title","&7[[1]&7]&r [2][3]",
                data.getLevelString(),teamName,playerName);
    }

    public TeamInfo getTeamInfo() {
        return teamInfo;
    }

    private int spawnTime = 0;

    /**
     * 表现形式不好看
     * */
    @Deprecated
    public static String formatTime(int s){
        int min = s / 60;
        int ss = s % 60;

        if(min > 0){
            return min+" 分 "+ss+" 秒";
        }else{
            return ss+" 秒";
        }

    }



    /**
     * 此方法移植到{@link FunctionManager}
     * */
    @Deprecated
    public static String formatTime1(int s){
        return FunctionManager.formatTime(s);
    }

    private ArrayList<String> getLore(boolean isWait){
        ArrayList<String> lore = new ArrayList<>();
        String levelName = TotalManager.getMenuRoomManager().getNameByRoom(gameRoom.getRoomConfig());
        if(levelName == null){
            levelName = " -- ";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        lore.add("&7"+format.format(new Date()));
        lore.add(TotalManager.getLanguage().getLanguage("scoreboard-line-game-world","游戏模式: &a[1]",levelName));

        lore.add(" ");
        if(isWait){
//            "玩家数: &a"+gameRoom.getPlayerInfos().size()+" &r/&a "+gameRoom.getRoomConfig().getMaxPlayerSize()
            lore.add(TotalManager.getLanguage().getLanguage("scoreboard-line-wait-player","玩家数: &a[1] &r/&a [2]",
                    gameRoom.getPlayerInfos().size()+"",gameRoom.getRoomConfig().getMaxPlayerSize()+""
            ));
            lore.add(TotalManager.getLanguage().getLanguage("scoreboard-line-waiting","等待中...."));
            lore.add("   ");

        }else{
            IGameRoomEvent event = getGameRoom().getEventControl().getNextEvent();
            if(event != null){
                lore.add(event.display()+" &a"+formatTime1(event.getEventTime() - getGameRoom().getEventControl().loadTime));
                lore.add("    ");
            }else{

                lore.add(TotalManager.getLanguage().getLanguage("scoreboard-line-game-end","游戏结束: &a[1]",
                        formatTime1(getGameRoom().loadTime)));
            }
            if(gameRoom.roomConfig.teamConfigs.size() > 1){
                for(TeamInfo teamInfo: gameRoom.getTeamInfos()){
                    String me = "";
                    if(getTeamInfo() != null && getTeamInfo().equals(teamInfo)){
                        me = TotalManager.getLanguage().getLanguage("scoreboard-line-myself","&7(我)");
                    }
//                    ""◎ "+ teamInfo +": &r  &c"+teamInfo.getLivePlayer().size()+" "+me
                    lore.add(TotalManager.getLanguage().getLanguage("scoreboard-line-team-info","◎ [1]: &r  &c[2] [3]",
                           teamInfo.toString(),teamInfo.getLivePlayer().size()+"",me));
                }
            }else{
                TeamInfo teamInfo = gameRoom.getTeamInfos().get(0);
                lore.add("   ");
                if(teamInfo.getTeamPlayers().size() <= 2){
                    lore.add(TotalManager.getLanguage().getLanguage("scoreboard-line-damage-one","目标:"));
                    PlayerInfo target = null;
                    for(PlayerInfo info: teamInfo.getLivePlayer()){
                        if(!info.equals(this)){
                            target = info;
                            break;
                        }
                    }
                    if(target != null){
                        lore.add(TotalManager.getLanguage().getLanguage("scoreboard-line-damage-one-target"," [1] [2] &c♥",
                                target.getName(),(int)target.getPlayer().getHealth()+""));
                    }
                }else {
//                " 存活人数: &a "+teamInfo.getLivePlayer().size() +" &7/&a "+teamInfo.getTeamPlayers().size()
                    lore.add(TotalManager.getLanguage().getLanguage("scoreboard-line-no-team-live-player", " 存活人数: &a [1] &7/&a [2]",
                            teamInfo.getLivePlayer().size() + "",
                            teamInfo.getTeamPlayers().size() + ""));
                }
            }

            lore.add("      ");
            lore.add(TotalManager.getLanguage().getLanguage("scoreboard-line-kill","击杀数: &a[1]",
                    getData(PlayerData.DataType.KILL)+""));
            lore.add(TotalManager.getLanguage().getLanguage("scoreboard-line-assists",
                    "助攻数: &a[1]",
                    getData(PlayerData.DataType.ASSISTS)+""));

            lore.add("        ");
        }
        Object obj = TotalManager.getConfig().get("game-logo");
        if(obj instanceof List){
            for(Object s : (List<?>)obj){
                lore.add(s.toString());
            }
        }else{
            lore.add(TotalManager.getConfig().getString("game-logo","&l&cT&6o&eC&ar&ba&9f&dt"));
        }
        return lore;
    }

    private int loadTime = 0;

    private boolean isSendkey = false;

    /**
     * 定时任务
     * */
    public void onUpdate(){
        //TODO 玩家进入房间后每秒就会调用这个方法
        if(waitTime > 0){
            player.setImmobile(true);
            sendTip(TotalManager.getLanguage().getLanguage("player-wait","&e开始倒计时 &r[1] &a[2] s",
                    FunctionManager.drawLine(waitTime / (float)gameRoom.getRoomConfig().gameInWait,
                            10,"&c■","&7■"),waitTime+""));
            waitTime--;
        }else if(waitTime == 0 && playerType == PlayerType.START){
            waitTime = -1;
            player.setImmobile(false);
            sendTitle(TotalManager.getLanguage().getLanguage("player-wait-success","&a游戏开始!"));
        }

        //助攻间隔
        LinkedHashMap<PlayerInfo,Long> ass = new LinkedHashMap<>(assistsPlayers);
        for(Map.Entry<PlayerInfo,Long> entry: ass.entrySet()){
            if(System.currentTimeMillis() - entry.getValue() > 3000){
                assistsPlayers.remove(entry.getKey());
            }
        }
        if(damageTime > 0){
            damageTime--;
        }else{
            damageByInfo = null;
        }
        if(damageByInfo != null){

            sendTip(TotalManager.getLanguage().getLanguage("player-attack-player-msg","[1]  &a[2] / [3]",
                    damageByInfo.toString(),damageByInfo.getPlayer().getHealth()+"",
                    damageByInfo.getPlayer().getMaxHealth()+""));
        }

        //死亡倒计时
        if(playerType == PlayerType.DEATH){
            if(gameRoom != null){
                if(gameRoom.roomConfig.reSpawnTime >= 0){
                    if(spawnTime >= gameRoom.roomConfig.reSpawnTime){
                        sendTitle(TotalManager.getLanguage().getLanguage("player-respawn-info","&a你复活了"),1);
                        sendSubTitle("");
                        spawn();
                        spawnTime = 0;
                    }else{
                        if(spawnTime == 0 && !isSendkey){
                            isSendkey = true;
                            sendTitle(TotalManager.getLanguage().getLanguage("player-death-info","&c你死了"), gameRoom.roomConfig.reSpawnTime);
                        }
                        if(gameRoom != null) {
                            sendSubTitle(TotalManager.getLanguage().getLanguage("player-death-respawn-info-title","[1] 秒后复活",
                                    (gameRoom.roomConfig.reSpawnTime - spawnTime)+""));
                        }
                        spawnTime++;
                    }
                }else{
                    playerType = PlayerType.START;
                }

            }
        }

        //TODO 玩家更新线程
        if(playerType == PlayerType.START){
            //TODO 游戏开始后 可以弄一些buff
            player.setNameTag(TextFormat.colorize('&',TotalManager.getLanguage().getLanguage("player-nametag-info","[1] [n]&c❤&7[2]",teamInfo.getTeamConfig().getNameColor()+player.getName(),
                    String.format("%.1f",player.getHealth()))));


        }else if(playerType == PlayerType.WAIT){
            if(getGameRoom().getRoomConfig().getWorldInfo().getWaitPosition().getY() - player.getY() > getGameRoom().getRoomConfig().callbackY){
                if(getGameRoom().getRoomConfig().getWorldInfo().getWaitPosition() == null){
                    if(getGameRoom() != null){
                        getGameRoom().quitPlayerInfo(this,true);
                        sendMessage(TotalManager.getLanguage().getLanguage("player-teleport-room-error","&c房间出现了错误 （未识别到等待大厅）已将你送回出生点"));
                    }
                    return;
                }
                player.teleport(getGameRoom().getRoomConfig().getWorldInfo().getWaitPosition());
            }
        }

        try{
            Class.forName("de.theamychan.scoreboard.api.ScoreboardAPI");
        }catch (Exception e){
            return;
        }
        if(playerType != PlayerType.LEAVE){
            //TODO 计分板的一些内容

            ScoreBoardMessage boardMessage = new ScoreBoardMessage(TotalManager.getScoreBoardTitle());
            boardMessage.setLore(getLore(playerType == PlayerType.WAIT));
            sendScore(boardMessage);
        }else{
            sendScore(null);
        }

    }

    public enum PlayerType{
        /**
         * WAIT: 等待 START: 开始 DEATH: 死亡(等待复活)  LEAVE: 离开 WATCH 观察(真正的死亡)
         * */
        WAIT,START,DEATH,LEAVE,WATCH
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }

    /**
     * 玩家死亡后的一些操作
     * */
    public void death(EntityDamageEvent event){
        boolean finalDeath = false;
        //TODO 玩家死亡后可以做一些逻辑处理
        player.setHealth(player.getMaxHealth());
        if(player instanceof Player){
            ((Player) player).removeAllWindows();
            ((Player) player).getUIInventory().clearAll();
        }

        PlayerGameDeathEvent event1 = new PlayerGameDeathEvent(this,getGameRoom(),TotalManager.getPlugin());
        Server.getInstance().getPluginManager().callEvent(event1);
        player.removeAllEffects();
        if(getGameRoom().getWorldInfo().getConfig().getGameWorld() == null){
            cancel();
            return;
        }
        if(health > 0){
            health--;
            sendMessage(TotalManager.getLanguage().getLanguage("player-respawn-health-count","&a你剩余 &e[1] &a条生命",
                     health+""));
            deathCanRespawn();
        }else {
            if (gameRoom != null && gameRoom.roomConfig.reSpawnTime >= 0) {
                int roomReSpawnCount = gameRoom.getRoomConfig().reSpawnCount;
                if (roomReSpawnCount > 0) {
                    if (reSpawnCount >= 0 && reSpawnCount < roomReSpawnCount) {
                        reSpawnCount++;
                        sendMessage(TotalManager.getLanguage().getLanguage("player-respawn-count", "&e你还能复活 &a[1] &e次",
                                (roomReSpawnCount - reSpawnCount) + ""));
                        deathCanRespawn();
                    } else {
                        finalDeath = true;

                    }
                } else {
                    if(roomReSpawnCount == -1) {
                        deathCanRespawn();
                    }else{
                        finalDeath = true;
                    }
                }
            } else {
                finalDeath = true;
            }
        }
        if(getGameRoom().getWorldInfo().getConfig().getGameWorld() == null){
            cancel();
            return;
        }
        player.teleport(teamInfo.getSpawnLocation());
        //防止共归于尽
        if(finalDeath && gameRoom.getLivePlayers().size() == 1){
            return;
        }
        addData(PlayerData.DataType.DEATH);

        //死亡后是否掉落物品
        if(gameRoom != null){
            if(gameRoom.getRoomConfig().isDeathDrop()){
                for(Item item: player.getInventory().getContents().values()){
                    player.level.dropItem(player,item,new Vector3(0,0.5,0));
                }
            }
        }
        //玩家死亡后的信息
        echoPlayerDeathInfo(event);
        //被击杀后给予击杀者钱..
        if (damageByInfo != null && damageByInfo.teamInfo != null) {
            if(gameRoom.roomConfig.enableMoney){
                gameRoom.roomConfig.moneyConfig.add(this,damageByInfo.teamInfo
                        .getTeamConfig().getTeamConfig().deathMoney);
            }
        }
        if(finalDeath) {
            if (damageByInfo != null && damageByInfo.teamInfo != null) {
                TeamInfoConfig targetTeam = damageByInfo.teamInfo.getTeamConfig();
                if(damageByInfo.teamInfo.equals(getTeamInfo())
                        || gameRoom.getRoomConfig().teamConfigs.size() == 1){
                    deathFinal();
                }else{
                    if (targetTeam.getTeamConfig().isCanInfection()) {
                        //TODO 被感染了
                        damageByInfo.teamInfo.mjoin(this);
                        gameRoom.addSound(Sound.MOB_ZOMBIE_SAY);
                    }else{
                        deathFinal();
                    }
                }
            }else{
                deathFinal();
            }
        }
        damageByInfo = null;
        player.getInventory().clearAll();
        player.getOffhandInventory().clearAll();
        if(playerType == PlayerType.WATCH){
            getGameRoom().joinWatch(this,false);
        }
    }

    /**
     * 设置玩家生命次数
     * */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * 获取玩家生命次数
     * */
    public int getHealth() {
        return health;
    }

    /**
     * 增加玩家生命次数
     * */
    public void addHealth(int health) {
        this.health += health;
    }

    /**
     * 增加玩家生命次数
     * */
    public void reduceHealth(int health) {
        if(this.health >= health){
            this.health -= health;
        }else{
            this.health = 0;
        }

    }

    /**
     * 允许玩家复活
     * */
    public void deathCanRespawn(){
        if (getPlayer() instanceof Player) {
            ((Player) getPlayer()).setGamemode(3);
        }
        player.teleport(getGameRoom().worldInfo.getConfig().getGameWorld().getSafeSpawn());
        Position position = teamInfo.getSpawnLocation();
        player.teleport(new Position(player.x, position.y + 64, player.z, getLevel()));
        sendTitle(TotalManager.getLanguage().getLanguage("player-death-info-title","&c你死了"),2);
        playerType = PlayerType.DEATH;
    }

    /**
     * 死亡后进入旁观模式
     * */
    public void deathFinal(){
        if (getPlayer() instanceof Player) {
            ((Player) getPlayer()).setGamemode(3);
        }
        playerType = PlayerType.WATCH;
        //当队伍仅有一个的时候，玩家死亡后就列入失败列表

        if(gameRoom.getRoomConfig().teamConfigs.size() == 1) {
            teamInfo.getDefeatPlayers().add(this);
        }
    }

    public void echoPlayerDeathInfo(EntityDamageEvent event){
        if(event != null) {

            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                if(damageByInfo != null){
                    gameRoom.sendMessage(TotalManager.getLanguage().getLanguage("player-death-by-player-void","[1] &e被 &r[2] 推入虚空。",
                            this.toString(),damageByInfo.toString()));
                    addKill(damageByInfo);
                }
                gameRoom.sendMessage(TotalManager.getLanguage().getLanguage("player-death-by-void","[1]&e掉入虚空",this.toString()));

            } else if (event instanceof EntityDamageByEntityEvent) {
                Entity entity = ((EntityDamageByEntityEvent) event).getDamager();
                if (entity instanceof Player) {
                    PlayerInfo info = TotalManager.getRoomManager().getPlayerInfo((Player) entity);
                    String killInfo = TotalManager.getLanguage().getLanguage("death-by-damage","击杀");
                    if(event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE){
                        killInfo = TotalManager.getLanguage().getLanguage("death-by-arrow","射杀");
                    }
                    if (info != null) {
                        addKill(info);
                        gameRoom.sendMessage(TotalManager.getLanguage().getLanguage("player-kill-player-info",
                                "[1] &e被 &r[2] [3]了。",
                                this.toString(),info.toString(),killInfo));
                    }
                } else {
                    gameRoom.sendMessage(TotalManager.getLanguage().getLanguage("player-death-by-player-kill","[1] &e被 &r[2] 击败了",
                            this.toString(),entity.getName()));
                }
            } else {
                if(damageByInfo != null){
                    addKill(damageByInfo);
                    gameRoom.sendMessage(TotalManager.getLanguage().getLanguage("player-death-by-player-kill","[1] &e被 &r[2] 击败了",
                            this.toString(),damageByInfo.toString()));
                }else {
                    String deathInfo = TotalManager.getLanguage().getLanguage("player-death-info-unknown","&e死了");
                    switch (event.getCause()){
                        case LAVA:
                            deathInfo = TotalManager.getLanguage().getLanguage("player-death-info-lava","&e被岩浆烧死了");
                            break;
                        case FALL:
                            deathInfo = TotalManager.getLanguage().getLanguage("player-death-info-fall","&e摔死了");
                            break;
                        case FIRE:
                            deathInfo = TotalManager.getLanguage().getLanguage("player-death-info-fire","&e被烧了");
                            break;
                        case HUNGER:
                            deathInfo = TotalManager.getLanguage().getLanguage("player-death-info-hunger","&e饿死了");
                            break;
                        default:break;
                    }
                    gameRoom.sendMessage(this +deathInfo);
                }
            }
        }
    }

}
