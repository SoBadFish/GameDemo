package org.sobadfish.gamedemo.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.player.PlayerInfo;


/**
 * 玩家在游戏里的全体消息
 *
 * @author SoBadFish
 * 2022/1/15
 */
public class GameDemoSpeakCommand extends Command {

    public GameDemoSpeakCommand(String name) {
        super(name);
    }


    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            PlayerInfo info = TotalManager.getRoomManager().getPlayerInfo((Player) commandSender);
            if(info == null){
                new PlayerInfo((Player)commandSender).sendForceMessage("&c你不在游戏房间内!");
                return false;
            }else{
                if(strings.length > 0){
                    info.getGameRoom().sendFaceMessage("&l&7(全体消息)&r "+info+"&r >> "+strings[0]);

                }else{
                    info.sendForceMessage("&c指令:/bws <你要说的内容> 全体消息");
                }
            }

        }
        return true;
    }
}
