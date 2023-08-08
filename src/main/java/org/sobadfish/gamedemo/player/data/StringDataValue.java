package org.sobadfish.gamedemo.player.data;

/**
 * 玩家数据是字符串
 * @author Sobadfish
 * @date 2023/4/22
 */
public class StringDataValue extends IDataValue<String>{
    public StringDataValue(String value) {
        super(value);
    }

    @Override
    public void setValue(IDataValue<?> value) {
        this.value = value.getValue().toString();
    }

    @Override
    public void addValue(IDataValue<?> value) {
        if(asAppend()) {
            this.value += value.getValue().toString();
        }
    }

    @Override
    public void removeValue(IDataValue<?> value) {
        this.value = "";
    }


}
