package org.sobadfish.gamedemo.player.data;

/**
 * 玩家数据是整数的参数
 * @author Sobadfish
 * @date 2023/4/22
 */
public class IntegerDataValue extends IDataValue<Integer>{

    public IntegerDataValue(Integer value) {
        super(value);
    }

    @Override
    public void setValue(IDataValue<?> value) {
        if(value instanceof IntegerDataValue || value.getValue() instanceof Integer){
            this.value = (Integer) value.getValue();
        }
    }

    @Override
    public void addValue(IDataValue<?> value) {
        if(asAppend()) {
            if (value instanceof IntegerDataValue || value.getValue() instanceof Integer) {
                this.value += (Integer) value.getValue();
            }
        }
    }

    @Override
    public void removeValue(IDataValue<?> value) {
        if(value instanceof IntegerDataValue || value.getValue() instanceof Integer){
            this.value -= (Integer) value.getValue();
        }
    }


}
