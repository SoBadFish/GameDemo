package org.sobadfish.gamedemo;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.command.GameAdminCommand;
import org.sobadfish.gamedemo.command.GameCommand;
import org.sobadfish.gamedemo.command.GameSpeakCommand;
import org.sobadfish.gamedemo.manager.TotalManager;

/**

 * @author Sobadfish
 * 13:07
 */
public class GameDemoMain extends PluginBase {



    @Override
    public void onEnable() {


        TotalManager.initLanguage(this);
        //字符生成地址 http://www.network-science.de/ascii/
        //Font: small
        this.getLogger().info(TextFormat.colorize('&',"&e  ___                ___                "));
        this.getLogger().info(TextFormat.colorize('&',"&e / __|__ _ _ __  ___|   \\ ___ _ __  ___ "));
        this.getLogger().info(TextFormat.colorize('&',"&e| (_ / _` | '  \\/ -_) |) / -_) '  \\/ _ \\"));
        this.getLogger().info(TextFormat.colorize('&',"&e \\___\\__,_|_|_|_\\___|___/\\___|_|_|_\\___/"));
        this.getLogger().info(TextFormat.colorize('&',"&e"));
        this.getLogger().info(TextFormat.colorize('&',TotalManager.getLanguage().getLanguage("version","&e正在加载[1] 插件 本版本为&av[2]"
                ,TotalManager.GAME_NAME,this.getDescription().getVersion())));

        TotalManager.init(this);
        this.getServer().getCommandMap().register(TotalManager.GAME_NAME,new GameAdminCommand(TotalManager.COMMAND_ADMIN_NAME));
        this.getServer().getCommandMap().register(TotalManager.GAME_NAME,new GameCommand(TotalManager.COMMAND_NAME));
        this.getServer().getCommandMap().register(TotalManager.GAME_NAME,new GameSpeakCommand(TotalManager.COMMAND_MESSAGE_NAME));

        this.getLogger().info(TextFormat.colorize('&',TotalManager.getLanguage().getLanguage("success","&a插件加载完成，祝您使用愉快")));

    }

    @Override
    public void onDisable() {
       TotalManager.onDisable();
    }

}
