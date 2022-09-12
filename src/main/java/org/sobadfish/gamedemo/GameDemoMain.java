package org.sobadfish.gamedemo;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.gamedemo.manager.MenuRoomManager;
import org.sobadfish.gamedemo.manager.RoomManager;
import org.sobadfish.gamedemo.manager.ThreadManager;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.panel.lib.AbstractFakeInventory;


import java.io.File;

/**

 * @author Sobadfish
 * 13:07
 */
public class GameDemoMain extends PluginBase {



    @Override
    public void onEnable() {


        this.getLogger().info(TextFormat.colorize('&',"&e  ___                ___                "));
        this.getLogger().info(TextFormat.colorize('&',"&e / __|__ _ _ __  ___|   \\ ___ _ __  ___ "));
        this.getLogger().info(TextFormat.colorize('&',"&e| (_ / _` | '  \\/ -_) |) / -_) '  \\/ _ \\"));
        this.getLogger().info(TextFormat.colorize('&',"&e \\___\\__,_|_|_|_\\___|___/\\___|_|_|_\\___/"));
        this.getLogger().info(TextFormat.colorize('&',"&e"));
        this.getLogger().info(TextFormat.colorize('&',"&e正在加载GameDemo 插件 本版本为&av"+this.getDescription().getVersion()));
        this.getLogger().info(TextFormat.colorize('&',"&a插件加载完成，祝您使用愉快"));


        this.getLogger().info(TextFormat.colorize('&',"&a插件加载完成，祝您使用愉快"));
        TotalManager.init(this);
    }


//    public enum UiType{
//        /**
//         * auto: 自动
//         *
//         * packet: GUI界面
//         *
//         * ui: 箱子界面
//         * */
//        AUTO,PACKET,UI
//    }
}
