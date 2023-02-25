package org.sobadfish.gamedemo.variable;

import cn.nukkit.Player;
import org.sobadfish.gamedemo.manager.TotalManager;
import org.sobadfish.gamedemo.player.PlayerData;
import tip.utils.Api;
import tip.utils.variables.BaseVariable;

/**
 * 对接Tips变量
 * @author Sobadfish
 *   2023/2/20
 */
public class GameTipVariable extends BaseVariable {

    public GameTipVariable(Player player) {
        super(player);
    }

    public static void init() {
        Api.registerVariables(TotalManager.GAME_NAME,GameTipVariable.class);
    }

    @Override
    public void strReplace() {
        //等级
        PlayerData data = TotalManager.getDataManager().getData(player.getName());
        addStrReplaceString("%"+TotalManager.COMMAND_NAME+"-level%",data.getLevelString());
        addStrReplaceString("%"+TotalManager.COMMAND_NAME+"-exp%",data.getExpString(data.getExp())+"");
        addStrReplaceString("%"+TotalManager.COMMAND_NAME+"-nextExp%",data.getExpString(data.getNextLevelExp())+"");
        addStrReplaceString("%"+TotalManager.COMMAND_NAME+"-line%",data.getExpLine(10)+"");
        addStrReplaceString("%"+TotalManager.COMMAND_NAME+"-per%",String.format("%.2f",data.getExpPercent() * 100)+"");
        for(PlayerData.DataType dataType: PlayerData.DataType.values()){
            addStrReplaceString("%"+TotalManager.COMMAND_NAME+"-"+dataType.getName()+"%",data.getFinalData(dataType.getName())+"");
        }
    }
}
