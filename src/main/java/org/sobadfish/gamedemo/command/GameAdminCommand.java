package org.sobadfish.gamedemo.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.entity.RobotEntity;
import org.sobadfish.gamedemo.item.tag.TagItem;
import org.sobadfish.gamedemo.manager.LanguageManager;
import org.sobadfish.gamedemo.manager.ThreadManager;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.panel.lib.AbstractFakeInventory;
import org.sobadfish.gamedemo.player.PlayerData;
import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.room.GameRoomCreator;
import org.sobadfish.gamedemo.room.config.GameRoomConfig;
import org.sobadfish.gamedemo.room.config.WorldInfoConfig;
import org.sobadfish.gamedemo.room.floattext.FloatTextInfoConfig;
import org.sobadfish.gamedemo.top.TopItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

/**
 *
 * 游戏管理主命令
 * 这个命令仅限管理员执行
 * 可以根据游戏的具体需求修改
 * 创建游戏房间的类参考{@link GameRoomCreator}
 *
 * @author Sobadfish
 * @date 2022/9/12
 */
public class GameAdminCommand extends Command {


    private LanguageManager language = TotalManager.getLanguage();

    public GameAdminCommand(String name) {
        super(name);
        this.usageMessage = language.getLanguage("command-admin-usage","/[1] help 查看指令帮助",TotalManager.COMMAND_ADMIN_NAME);
        this.setPermission("op");
    }

    /**
     * 创建游戏房间模板
     * 注意这个创建的是模板，不是房间
     * 模板就是可以预设接下来创建房间的名称，人数
     * 这里最好不要更改
     *
     * @param commandSender 执行指令的用户
     * @param value 创建时传入的参数
     * */
    private boolean createSetRoom(CommandSender commandSender, String value){
        GameRoomCreator creator;
        if (create.containsKey(commandSender.getName())) {
            creator = create.get(commandSender.getName());
        } else {
            creator = new GameRoomCreator(new PlayerInfo((Player) commandSender));
            create.put(commandSender.getName(), creator);
        }
        creator.onCreatePreset(value);
        return true;
    }

    /**
     * 创建房间方法
     * 具体的创建流程参考{@link GameRoomCreator}
     * 当onCreateNext 为false的时候就代表创建流程结束，执行文件的写入
     * @param commandSender 执行指令的用户
     * @return 是否创建成功
     *
     * */
    private boolean createRoom(CommandSender commandSender){
        GameRoomCreator creator;
        if(create.containsKey(commandSender.getName())){
            creator = create.get(commandSender.getName());
        }else{
            creator = new GameRoomCreator(new PlayerInfo((Player) commandSender));
            create.put(commandSender.getName(),creator);
        }
        if(!creator.onCreateNext()){
            if(!creator.createRoom()){
                commandSender.sendMessage(language.getLanguage("create-room-error","房间创建失败"));
            }
            //使用完就释放掉
            create.remove(commandSender.getName());
        }
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!commandSender.isOp()){
            TotalManager.sendMessageToObject(language.getLanguage("command-admin-no-permission","&c你没有使用此指令的权限"),commandSender);
            return true;
        }
        String valueData = TotalManager.COMMAND_ADMIN_NAME;
        if (strings.length > 0 && "help".equalsIgnoreCase(strings[0])) {
            commandSender.sendMessage(language.getLanguage("command-admin-help1","只需要输入/[1] 就可以了",
                    valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-help2","其他指令介绍:"));

            commandSender.sendMessage(language.getLanguage("command-admin-reload","/[1] reload 重新载入配置",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-back","/[1] back 重新回到上一步设置",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-set","/[1] set [名称] 创建一个自定义房间模板",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-tsl","/[1] tsl 读取模板的队伍数据",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-see","/[1] see 查看所有加载的房间",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-save-item","/[1] si [名称] 将手持的物品保存到配置文件中",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-close","/[1] close [名称] 关闭房间",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-exp","/[1] exp [玩家] [数量] <由来> 增加玩家经验",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-status","/[1] status 查看线程状态",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-end","/[1] end 停止模板预设",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-float","/[1] float add/remove [房间名称] [名称] [文本] 在脚下设置浮空字/删除浮空字",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-cancel","/[1] cancel 终止房间创建",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-robot","/[1] robot [房间名称] [数量] 向游戏房间内增加测试机器人",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-top","/[1] top add/remove [名称] [类型] [房间(可不填)] 创建/删除排行榜",valueData));
            StringBuilder v = new StringBuilder(language.getLanguage("top-type","类型: "));
            for(PlayerData.DataType type: PlayerData.DataType.values()){
                v.append(type.getName()).append(" , ");
            }
            commandSender.sendMessage(v.toString());
            return true;
        }
        if (strings.length == 0) {
            if(!commandSender.isPlayer()){
                commandSender.sendMessage(language.getLanguage("do-not-console","请不要在控制台执行"));
                return false;
            }
            return createRoom(commandSender);
        }
        switch (strings[0]){
            case "set":
                if(strings.length > 1) {
                    if (commandSender instanceof Player) {
                        return createSetRoom(commandSender, strings[1]);
                    } else {
                        commandSender.sendMessage(language.getLanguage("do-not-console","请不要在控制台执行"));
                        return false;
                    }
                }else{
                    commandSender.sendMessage(language.getLanguage("set","/[1] set [名称] 创建一个自定义房间模板",valueData));
                    return false;
                }
            case "end":
                if(commandSender instanceof Player) {
                    if (create.containsKey(commandSender.getName())) {
                        create.get(commandSender.getName()).stopInit();
                    }
                }else{
                    commandSender.sendMessage(language.getLanguage("do-not-console","请不要在控制台执行"));
                    return false;
                }
                break;
            case "back":
                if(commandSender instanceof Player) {
                    if (create.containsKey(commandSender.getName())) {
                        GameRoomCreator creator = create.get(commandSender.getName());
                        if(creator.spawnSizeFlag > 0){
                            creator.spawnSizeFlag--;
                            TotalManager.sendMessageToObject(language.getLanguage("create-goBack-spawnPoint","&a成功回退出生点的设置"),commandSender);
                        }else if(creator.spawnFlag > 0){
                            creator.spawnFlag--;
                            TotalManager.sendMessageToObject(language.getLanguage("create-goBack-teamPoint","&a成功回退队伍出生点的设置"),commandSender);
                        }else if(creator.setFlag > 1){
                            creator.setFlag--;
                            TotalManager.sendMessageToObject(language.getLanguage("create-goBack-setting","&a成功回退预设的设置"),commandSender);
                        }

                    }
                }else{
                    commandSender.sendMessage(language.getLanguage("do-not-console","请不要在控制台执行"));
                    return false;
                }

                break;
            case "robot":

                if(strings.length < 3){
                    commandSender.sendMessage(language.getLanguage("command-admin-usage","/[1] help 查看指令帮助",TotalManager.COMMAND_ADMIN_NAME));
                    return false;
                }
                String roomName = strings[1];
                GameRoomConfig roomConfig = TotalManager.getRoomManager().getRoomConfig(roomName);
                if(roomConfig == null){
                    commandSender.sendMessage(language.getLanguage("room-no-exists","房间 [1] 不存在",strings[2]));
                    return false;
                }
                int count = Integer.parseInt(strings[2]);
                for(int i = 0; i < count; i++){
                    Position pos = roomConfig.getWorldInfo().getWaitPosition();
                    CompoundTag tag = EntityHuman.getDefaultNBT(pos);
                    Skin skin;
                    if(AbstractFakeInventory.IS_PM1E) {
                        Skin.initDefaultSkin();
                        skin = Skin.NO_PERSONA_SKIN;
                    }else{
                        List<Player> pls = new ArrayList<>(Server.getInstance().getOnlinePlayers().values());
                        skin = new Skin();
                        if(pls.size() > 0) {
                            Player pskin = pls.get(new Random().nextInt(pls.size()));
                            skin = pskin.getSkin();

                        }else{
                            skin.setSkinData(new byte[Skin.SINGLE_SKIN_SIZE]);
                        }

                    }

                    tag.putCompound("Skin",new CompoundTag()
                            .putByteArray("Data", skin.getSkinData().data)
                            .putString("ModelId",skin.getSkinId())
                    );
                    int finalI = i;
                    RobotEntity entityHuman = new RobotEntity(pos.getChunk(), tag){
                        @Override
                        public String getName() {
                            return "robot No."+ finalI;
                        }
                    };
                    entityHuman.setNameTag("robot No."+ i);

                    entityHuman.setNameTagAlwaysVisible(true);
                    entityHuman.setNameTagVisible(true);
                    entityHuman.setSkin(skin);
                    entityHuman.spawnToAll();
                    TotalManager.getRoomManager().joinRoom(new PlayerInfo(entityHuman), roomName);




                }

                break;
            case "exp":
                if(strings.length < 3){
                    commandSender.sendMessage(language.getLanguage("command-admin-usage","/[1] help 查看指令帮助",TotalManager.COMMAND_ADMIN_NAME));
                    return false;
                }
                String playerName = strings[1];
                Player player = Server.getInstance().getPlayer(playerName);
                if(player != null){
                    playerName = player.getName();
                }
                String expString = strings[2];
                int exp = 0;
                try {
                    exp = Integer.parseInt(expString);
                }catch (Exception ignore){}
                String cause = language.getLanguage("exp-command-give-cause","指令给予");
                if(strings.length > 3){
                    cause = strings[3];
                }
                if(exp > 0){
                    PlayerData playerData = TotalManager.getDataManager().getData(playerName);
                    playerData.addExp(exp,cause);
                    commandSender.sendMessage(language.getLanguage("exp-command-give","成功给予玩家 [1] [2] 点经验",
                            playerName,exp+""));
                }else{
                    commandSender.sendMessage(language.getLanguage("exp-give-error","经验必须大于0"));
                    return false;
                }
                break;
            case "top":
                if(commandSender instanceof Player) {
                    if (strings.length < 3) {
                        commandSender.sendMessage(language.getLanguage("command-admin-usage","/[1] help 查看指令帮助",TotalManager.COMMAND_ADMIN_NAME));
                        return false;
                    }
                    String name = strings[2];


                    if ("add".equalsIgnoreCase(strings[1])) {
                        if(strings.length < 4){
                            commandSender.sendMessage(language.getLanguage("command-admin-usage","/[1] help 查看指令帮助",TotalManager.COMMAND_ADMIN_NAME));
                            return false;
                        }
                        PlayerData.DataType type = PlayerData.DataType.byName(strings[3]);
                        if (type == null) {
                            commandSender.sendMessage(language.getLanguage("type-unknown","未知类型"));
                            return true;
                        }
                        String room = null;
                        if (strings.length > 4) {
                            room = strings[4];
                        }
                        TopItem item = new TopItem(name,type,((Player) commandSender).getPosition(),"");
                        item.room = room;
                        if(TotalManager.getTopManager().hasTop(name)){
                            commandSender.sendMessage(language.getLanguage("top-exists","存在名称为 [1] 的排行榜了", name));
                            return true;
                        }
                        item.setTitle(TextFormat.colorize('&',language.getLanguage("top-title","[1] &a[2] &r排行榜",
                                        TotalManager.getTitle(),type.getName())));
                        TotalManager.getTopManager().addTopItem(item);
                        commandSender.sendMessage(language.getLanguage("top-create-success","排行榜创建成功"));
                    } else {
                        if(!TotalManager.getTopManager().hasTop(name)){
                            commandSender.sendMessage(language.getLanguage("top-no-exists","不存在名称为 [1] 的排行榜",name));
                            return true;
                        }
                        TopItem topItem = TotalManager.getTopManager().getTop(name);
                        if(topItem == null){
                            commandSender.sendMessage(language.getLanguage("top-no-exists","不存在名称为 [1] 的排行榜",name));
                            return true;
                        }
                        TotalManager.getTopManager().removeTopItem(topItem);
                        commandSender.sendMessage(language.getLanguage("top-delete-success","排行榜删除成功"));

                    }
                }else{
                    commandSender.sendMessage(language.getLanguage("do-not-console","请不要在控制台执行"));
                    return false;
                }
                break;
            case "float":
                if(strings.length < 4){
                    commandSender.sendMessage(language.getLanguage("command-admin-usage","/[1] help 查看指令帮助",TotalManager.COMMAND_ADMIN_NAME));
                    return false;
                }
                if(commandSender instanceof Player) {
                    roomConfig = TotalManager.getRoomManager().getRoomConfig(strings[2]);
                    if(roomConfig == null){
                        commandSender.sendMessage(language.getLanguage("room-no-exists","房间 [1] 不存在",strings[2]));
                        return false;
                    }
                    if("remove".equalsIgnoreCase(strings[1])){
                        if(roomConfig.notHasFloatText(strings[3])){
                            commandSender.sendMessage(language.getLanguage("float-no-exists","浮空字 [1] 不存在",strings[3]));
                            return false;
                        }
                        roomConfig.removeFloatText(strings[3]);
                        commandSender.sendMessage(language.getLanguage("float-delete-success","浮空字删除成功"));

                    }else{
                        if(strings.length < 5){
                            commandSender.sendMessage(language.getLanguage("command-admin-usage","/[1] help 查看指令帮助",TotalManager.COMMAND_ADMIN_NAME));
                            return false;
                        }
                        if(roomConfig.notHasFloatText(strings[3])){
                            roomConfig.floatTextInfoConfigs.add(new FloatTextInfoConfig(strings[3], WorldInfoConfig.positionToString(((Player) commandSender).getPosition()),strings[4]));
                            commandSender.sendMessage(language.getLanguage("float-crate-success","成功添加浮空字"));
                        }else{
                            commandSender.sendMessage(language.getLanguage("float-room-exists","房间存在 [1] 浮空字",strings[3]));
                        }
                    }

                }else{
                    commandSender.sendMessage(language.getLanguage("do-not-console","请不要在控制台执行"));
                    return false;
                }

                break;
            case "status":
                TotalManager.sendMessageToObject(language.getLanguage("status-timing","&6定时任务: &a")+ ThreadManager.getScheduledSize(),commandSender);
                TotalManager.sendMessageToObject(language.getLanguage("status-running","&6正在执行的定时任务: &a")+ ThreadManager.getScheduledActiveCount(),commandSender);
                TotalManager.sendMessageToObject(language.getLanguage("status-thread","&6线程详情: &r")+"\n"+ThreadManager.info(),commandSender);
                TotalManager.sendMessageToObject(language.getLanguage("status-room","&6房间状态: &a"),commandSender);
                for(GameRoomConfig config: TotalManager.getRoomManager().getRoomConfigs()){
                    GameRoom room = TotalManager.getRoomManager().getRoom(config.name);
                    if(room != null){

                        TotalManager.sendMessageToObject("&a"+config.getName()+language.getLanguage("status-started"," (已启动) ")+room.getType()+" : &2"+room.getPlayerInfos().size(),commandSender);
                    }else{
                        TotalManager.sendMessageToObject("&c"+config.getName()+language.getLanguage("status-unstarted"," (未启动) "),commandSender);
                    }
                }
                break;

            case "tsl":
                teamLoad(commandSender);
                break;

            case "see":
                TotalManager.sendMessageToObject(TotalManager.getRoomManager().getRooms().keySet().toString(),commandSender);
                break;
            case "si":
                if(commandSender instanceof Player) {
                    if (strings.length > 1) {
                        String name = strings[1];
                        if("".equalsIgnoreCase(name)) {
                            commandSender.sendMessage(language.getLanguage("command-admin-usage", "/[1] help 查看指令帮助", TotalManager.COMMAND_ADMIN_NAME));
                            return false;
                        }
                        if ( TotalManager.getTagItemDataManager().hasItem(name)) {
                            commandSender.sendMessage(language.getLanguage("save-item-exists-name", "&c存在名称为 &a[1] &c的物品了", name));
                            return true;
                        }
                        Item item = ((Player) commandSender).getInventory().getItemInHand();
                        if(item.getId() == 0){
                            commandSender.sendMessage(language.getLanguage("save-item-air", "&c你不能保存空气！"));
                            return true;
                        }
                        TotalManager.getTagItemDataManager().dataList.add(new TagItem(name,item));
                        TotalManager.getTagItemDataManager().save();
                        commandSender.sendMessage(language.getLanguage("save-item-exists-success", "&a成功保存名称为 &e[1] &a的物品",name));
                    } else {
                        commandSender.sendMessage(language.getLanguage("command-admin-usage", "/[1] help 查看指令帮助", TotalManager.COMMAND_ADMIN_NAME));
                        return false;
                    }
                }else{
                    commandSender.sendMessage(language.getLanguage("do-not-console","请不要在控制台执行"));
                    return false;
                }
                break;
            case "reload":
                TotalManager.sendMessageToObject(language.getLanguage("reload-config-loading","正在读取配置文件"),commandSender);
                TotalManager.loadConfig();
                TotalManager.sendMessageToObject(language.getLanguage("reload-config-success","配置文件读取完成"),commandSender);
                break;
            case "close":
                if(strings.length > 1) {
                    String name = strings[1];
                    if(TotalManager.getRoomManager().hasGameRoom(name)){
                        TotalManager.getRoomManager().disEnableRoom(name);
                        commandSender.sendMessage(language.getLanguage("close-room-success","成功关闭房间: [1]",name));
                    }else{
                        commandSender.sendMessage(language.getLanguage("close-room-error-unenable","游戏房间未开启"));
                    }
                }else{
                    commandSender.sendMessage(language.getLanguage("close-room-error-unknown-name","请输入房间名"));
                }
                break;
            case "cancel":
                create.remove(commandSender.getName());
                TotalManager.sendMessageToObject(language.getLanguage("cancel-room-create","成功终止房间的创建，残留文件将在重启服务器后自动删除"), commandSender);
                // commandSender.sendMessage(TextFormat.colorize('&', "&d"));

                break;

            default:break;

        }
        return true;

    }

    private void teamLoad(CommandSender commandSender) {
        if(!create.containsKey(commandSender.getName())){
            commandSender.sendMessage(language.getLanguage("template-reload-error","请先创建房间模板"));
            return;
        }
       GameRoomCreator creator = create.get(commandSender.getName());
        GameRoomConfig roomConfig = creator.getRoomConfig();
        if(roomConfig != null) {
            GameRoomConfig.loadTeamConfig(roomConfig);
            commandSender.sendMessage(language.getLanguage("template-reload-success","成功重新读取模板信息"));
        }else{
            commandSender.sendMessage(language.getLanguage("template-unknown","无模板信息"));
        }

    }

    public static GameRoomCreator getCreatorByPlayer(String playerName){
        if(create.containsKey(playerName)){
            return create.get(playerName);
        }
        return null;
    }





    private static final LinkedHashMap<String, GameRoomCreator> create = new LinkedHashMap<>();
}
