package org.sobadfish.gamedemo.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import org.sobadfish.gamedemo.manager.LanguageManager;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.player.PlayerInfo;


/**
 * 玩家在游戏里的全体消息
 *
 * @author SoBadFish
 * 2022/1/15
 */
public class GameSpeakCommand extends Command {

    public LanguageManager language = TotalManager.getLanguage();

    public GameSpeakCommand(String name) {
        super(name);
    }


    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            PlayerInfo info = TotalManager.getRoomManager().getPlayerInfo((Player) commandSender);
            if(info == null){
                new PlayerInfo((Player)commandSender).sendForceMessage(language.getLanguage("command-player-not-in-room","&c你不在游戏房间内!"));
                return false;
            }else{
                if(strings.length > 0){
//                    "&l&7(全体消息)&r "+info+"&r >> "+strings[0]
                    info.getGameRoom().sendFaceMessage(language.getLanguage("player-speak-in-room-message-all","&l&7(全体消息)&r [1]&r >> [2]",
                            info.toString(),strings[0]));

                }else{
                    //&c指令:/"+TotalManager.COMMAND_MESSAGE_NAME+" <你要说的内容> 全体消息
                    info.sendForceMessage(language.getLanguage("command-speak-help","&c指令:/[1] <你要说的内容> 全体消息",
                            TotalManager.COMMAND_MESSAGE_NAME ));
                }
            }

        }
        return true;
    }
}
