package org.sobadfish.gamedemo.item;

/**
 * @author Sobadfish
 * 2023/2/7
 */
public interface IItemObjectValue {

    /**
     * 设置数值
     * @param value 额外的值
     * */
    void setValue(Object value);

    /**
     * 获取设置的值
     * @return 设置的值
     * */
    Object getValue();
}
