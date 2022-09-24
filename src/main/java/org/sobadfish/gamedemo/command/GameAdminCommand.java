package org.sobadfish.gamedemo.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.manager.ThreadManager;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.player.PlayerData;
import org.sobadfish.gamedemo.player.PlayerInfo;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.room.GameRoomCreater;
import org.sobadfish.gamedemo.room.config.GameRoomConfig;
import org.sobadfish.gamedemo.room.config.WorldInfoConfig;
import org.sobadfish.gamedemo.room.floattext.FloatTextInfoConfig;
import org.sobadfish.gamedemo.top.TopItem;

import java.util.LinkedHashMap;

/**
 *
 * 游戏管理主命令
 * 这个命令仅限管理员执行
 * 可以根据游戏的具体需求修改
 * 创建游戏房间的类参考{@link GameRoomCreater}
 *
 * @author Sobadfish
 * @date 2022/9/12
 */
public class GameAdminCommand extends Command {
    public GameAdminCommand(String name) {
        super(name);
        this.usageMessage = "/"+TotalManager.COMMAND_ADMIN_NAME+" help 查看指令帮助";
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
        GameRoomCreater creater;
        if (create.containsKey(commandSender.getName())) {
            creater = create.get(commandSender.getName());
        } else {
            creater = new GameRoomCreater(new PlayerInfo((Player) commandSender));
            create.put(commandSender.getName(), creater);
        }
        creater.onCreatePreset(value);
        return true;
    }

    /**
     * 创建房间方法
     * 具体的创建流程参考{@link GameRoomCreater}
     * 当onCreateNext 为false的时候就代表创建流程结束，执行文件的写入
     * @param commandSender 执行指令的用户
     * @return 是否创建成功
     *
     * */
    private boolean createRoom(CommandSender commandSender){
        GameRoomCreater creater;
        if(create.containsKey(commandSender.getName())){
            creater = create.get(commandSender.getName());
        }else{
            creater = new GameRoomCreater(new PlayerInfo((Player) commandSender));
            create.put(commandSender.getName(),creater);
        }
        if(!creater.onCreateNext()){
            if(!creater.createRoom()){
                commandSender.sendMessage("房间创建失败");
            }
        }
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!commandSender.isOp()){
            TotalManager.sendMessageToObject("&c你没有使用此指令的权限",commandSender);
            return true;
        }
        if (strings.length > 0 && "help".equalsIgnoreCase(strings[0])) {
            commandSender.sendMessage("只需要输入/"+TotalManager.COMMAND_ADMIN_NAME+" 就可以了");
            commandSender.sendMessage("其他指令介绍:");
            commandSender.sendMessage("/"+TotalManager.COMMAND_ADMIN_NAME+" reload 重新载入配置");
            commandSender.sendMessage("/"+TotalManager.COMMAND_ADMIN_NAME+" set [名称] 创建一个自定义房间模板");
            commandSender.sendMessage("/"+TotalManager.COMMAND_ADMIN_NAME+" tsl 读取模板的队伍数据");
            commandSender.sendMessage("/"+TotalManager.COMMAND_ADMIN_NAME+" see 查看所有加载的房间");
            commandSender.sendMessage("/"+TotalManager.COMMAND_ADMIN_NAME+" close [名称] 关闭房间");
            commandSender.sendMessage("/"+TotalManager.COMMAND_ADMIN_NAME+" exp [玩家] [数量] <由来> 增加玩家经验");
            commandSender.sendMessage("/"+TotalManager.COMMAND_ADMIN_NAME+" status 查看线程状态");
            commandSender.sendMessage("/"+TotalManager.COMMAND_ADMIN_NAME+" end 停止模板预设");
            commandSender.sendMessage("/"+TotalManager.COMMAND_ADMIN_NAME+" float add/remove [房间名称] [名称] [文本] 在脚下设置浮空字/删除浮空字");
            commandSender.sendMessage("/"+TotalManager.COMMAND_ADMIN_NAME+" cancel 终止房间创建");
            commandSender.sendMessage("/"+TotalManager.COMMAND_ADMIN_NAME+" top add/remove [名称] [类型] [房间(可不填)] 创建/删除排行榜");
            StringBuilder v = new StringBuilder("类型: ");
            for(PlayerData.DataType type: PlayerData.DataType.values()){
                v.append(type.getName()).append(" , ");
            }
            commandSender.sendMessage(v.toString());
            return true;
        }
        if (strings.length == 0) {
            if(!commandSender.isPlayer()){
                commandSender.sendMessage("请不要在控制台执行");
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
                        commandSender.sendMessage("请不要在控制台执行");
                        return false;
                    }
                }else{
                    commandSender.sendMessage(TextFormat.colorize('&',"/"+TotalManager.COMMAND_ADMIN_NAME+" set [内容] &e首次创建为房间名称"));
                    return false;
                }
            case "end":
                if(commandSender instanceof Player) {
                    if (create.containsKey(commandSender.getName())) {
                        create.get(commandSender.getName()).stopInit();
                    }
                }else{
                    commandSender.sendMessage("请不要在控制台执行");
                    return false;
                }
                break;
            case "exp":
                if(strings.length < 3){
                    commandSender.sendMessage("指令参数错误 执行/"+TotalManager.COMMAND_ADMIN_NAME+" help 查看帮助");
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
                String cause = "指令给予";
                if(strings.length > 3){
                    cause = strings[3];
                }
                if(exp > 0){
                    PlayerData playerData = TotalManager.getDataManager().getData(playerName);
                    playerData.addExp(exp,cause);
                    commandSender.sendMessage("成功给予玩家 "+playerName+" "+exp+" 点经验");
                }else{
                    commandSender.sendMessage("经验必须大于0");
                    return false;
                }
                break;
            case "top":
                if(commandSender instanceof Player) {
                    if (strings.length < 3) {
                        commandSender.sendMessage("指令参数错误 执行/"+TotalManager.COMMAND_ADMIN_NAME+" help 查看帮助");
                        return false;
                    }
                    String name = strings[2];


                    if ("add".equalsIgnoreCase(strings[1])) {
                        if(strings.length < 4){
                            commandSender.sendMessage("指令参数错误 执行/"+TotalManager.COMMAND_ADMIN_NAME+" help 查看帮助");
                            return false;
                        }
                        PlayerData.DataType type = PlayerData.DataType.byName(strings[3]);
                        if (type == null) {
                            commandSender.sendMessage("未知类型");
                            return true;
                        }
                        String room = null;
                        if (strings.length > 4) {
                            room = strings[4];
                        }
                        TopItem item = new TopItem(name,type,((Player) commandSender).getPosition(),"");
                        item.room = room;
                        if(TotalManager.getTopManager().hasTop(name)){
                            commandSender.sendMessage("存在名称为 "+name+" 的排行榜了");
                            return true;
                        }
                        item.setTitle(TextFormat.colorize('&',TotalManager.getTitle()+" &a"+type.getName()+" &r排行榜"));
                        TotalManager.getTopManager().addTopItem(item);
                        commandSender.sendMessage("排行榜创建成功");
                    } else {
                        if(!TotalManager.getTopManager().hasTop(name)){
                            commandSender.sendMessage("不存在名称为 "+name+" 的排行榜");
                            return true;
                        }
                        TopItem topItem = TotalManager.getTopManager().getTop(name);
                        if(topItem == null){
                            commandSender.sendMessage("不存在名称为 "+name+" 的排行榜");
                            return true;
                        }
                        TotalManager.getTopManager().removeTopItem(topItem);
                        commandSender.sendMessage("排行榜删除成功");

                    }
                }else{
                    commandSender.sendMessage("请不要在控制台执行");
                    return false;
                }
                break;
            case "float":
                if(strings.length < 4){

                    commandSender.sendMessage("指令参数错误 执行/"+TotalManager.COMMAND_ADMIN_NAME+" help 查看帮助");
                    return false;
                }
                if(commandSender instanceof Player) {
                    GameRoomConfig roomConfig = TotalManager.getRoomManager().getRoomConfig(strings[2]);
                    if(roomConfig == null){
                        commandSender.sendMessage("房间 "+strings[2]+" 不存在");
                        return false;
                    }
                    if("remove".equalsIgnoreCase(strings[1])){
                        if(roomConfig.notHasFloatText(strings[3])){
                            commandSender.sendMessage("浮空字 "+strings[3]+" 不存在");
                            return false;
                        }
                        roomConfig.removeFloatText(strings[3]);
                        commandSender.sendMessage("浮空字删除成功");

                    }else{
                        if(strings.length < 5){
                            commandSender.sendMessage("指令参数错误 执行/"+TotalManager.COMMAND_ADMIN_NAME+" help 查看帮助");
                            return false;
                        }
                        if(roomConfig.notHasFloatText(strings[3])){
                            roomConfig.floatTextInfoConfigs.add(new FloatTextInfoConfig(strings[3], WorldInfoConfig.positionToString(((Player) commandSender).getPosition()),strings[4]));
                            commandSender.sendMessage("成功添加浮空字");
                        }else{
                            commandSender.sendMessage("房间存在 "+strings[3]+"的浮空字");
                        }
                    }

                }else{
                    commandSender.sendMessage("请不要在控制台执行");
                    return false;
                }

                break;
            case "status":
                TotalManager.sendMessageToObject("&6定时任务: &a"+ ThreadManager.getScheduledSize(),commandSender);
                TotalManager.sendMessageToObject("&6正在执行的定时任务: &a"+ ThreadManager.getScheduledActiveCount(),commandSender);
                TotalManager.sendMessageToObject("&6线程详情: &r\n"+ThreadManager.info(),commandSender);
                TotalManager.sendMessageToObject("&6房间状态: &a",commandSender);
                for(GameRoomConfig config: TotalManager.getRoomManager().getRoomConfigs()){
                    GameRoom room = TotalManager.getRoomManager().getRoom(config.name);
                    if(room != null){

                        TotalManager.sendMessageToObject("&a"+config.getName()+" (已启动) "+room.getType()+" : &2"+room.getPlayerInfos().size(),commandSender);
                    }else{
                        TotalManager.sendMessageToObject("&c"+config.getName()+" (未启动)",commandSender);
                    }
                }
                break;

            case "tsl":
                teamLoad(commandSender);
                break;

            case "see":
                TotalManager.sendMessageToObject(TotalManager.getRoomManager().getRooms().keySet().toString(),commandSender);
                break;
            case "reload":
                TotalManager.sendMessageToObject("正在读取配置文件中",commandSender);
                TotalManager.loadConfig();
                TotalManager.sendMessageToObject("配置文件读取完成",commandSender);
                break;
            case "close":
                if(strings.length > 1) {
                    String name = strings[1];
                    if(TotalManager.getRoomManager().hasGameRoom(name)){
                        TotalManager.getRoomManager().disEnableRoom(name);
                        commandSender.sendMessage("成功关闭房间: "+name);
                    }else{
                        commandSender.sendMessage("游戏房间未开启");
                    }
                }else{
                    commandSender.sendMessage("请输入房间名");
                }
                break;
            case "cancel":
                create.remove(commandSender.getName());
                TotalManager.sendMessageToObject("成功终止房间的创建，残留文件将在重启服务器后自动删除", commandSender);
                // commandSender.sendMessage(TextFormat.colorize('&', "&d"));

                break;

            default:break;

        }
        return true;

    }

    private void teamLoad(CommandSender commandSender) {
        if(!create.containsKey(commandSender.getName())){
            commandSender.sendMessage("请先创建房间模板");
            return;
        }
       GameRoomCreater creater = create.get(commandSender.getName());
        GameRoomConfig roomConfig = creater.getRoomConfig();
        if(roomConfig != null) {
            GameRoomConfig.loadTeamConfig(roomConfig);
            commandSender.sendMessage("成功重新读取模板信息");
        }else{
            commandSender.sendMessage("无模板信息");
        }

    }


    private final LinkedHashMap<String, GameRoomCreater> create = new LinkedHashMap<>();
}
