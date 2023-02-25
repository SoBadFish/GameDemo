package org.sobadfish.gamedemo.variable;

import cn.nukkit.Player;
import com.smallaswater.npc.data.RsNpcConfig;
import com.smallaswater.npc.variable.BaseVariableV2;
import com.smallaswater.npc.variable.VariableManage;
import org.sobadfish.gamedemo.manager.LanguageManager;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.room.config.GameRoomConfig;

/**
 * 对接RSNPC
 * @author Sobadfish
 *  2023/2/20
 */
public class GameNpcVariable extends BaseVariableV2 {


    public static void init() {
        VariableManage.addVariableV2("game", GameNpcVariable.class);
    }

    @Override
    public void onUpdate(Player player, RsNpcConfig rsNpcConfig) {
        initVariable();
    }


    private void initVariable(){
        LanguageManager language = TotalManager.getLanguage();
        for(GameRoomConfig roomConfig: TotalManager.getRoomManager().getRoomConfigs()){
            GameRoom room = TotalManager.getRoomManager().getRoom(roomConfig.name);
            int p = 0;
            int mp = roomConfig.getMaxPlayerSize();
            String status = language.getLanguage("variable-room-wait","&a等待中");
            if(room != null){
                p = room.getPlayerInfos().size();
                switch (room.getType()){
                    case START:
                        status = language.getLanguage("variable-room-start","&c游戏中");
                        break;
                    case END:
                    case CLOSE:
                        status =  language.getLanguage("variable-room-end","&e结算中");
                        break;
                    default:break;
                }
            }
            addVariable("%"+roomConfig.getName()+"-player%",p+"");
            addVariable("%"+roomConfig.getName()+"-maxplayer%",mp+"");
            addVariable("%"+roomConfig.getName()+"-status%",status);
        }
    }
}