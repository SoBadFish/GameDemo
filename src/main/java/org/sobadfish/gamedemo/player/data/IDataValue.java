package org.sobadfish.gamedemo.player.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;

/**
 * 排行榜或其他可以统计出数据的值
 * 自定义类型数据需要继承这个接口 同时要在onLoad中执行registerDataValue注册
 * @author Sobadfish
 * @date 2023/4/22
 */
public abstract class IDataValue<T> implements Serializable {

    private static final LinkedHashMap<String,Class<? extends IDataValue<?>>> DATA_VALUE = new LinkedHashMap<>();

    public static void registerDataValue(Class<? extends IDataValue<?>> value) {
        DATA_VALUE.put(value.getSimpleName(), value);
    }

    public static void init(){
        registerDataValue(IntegerDataValue.class);
        registerDataValue(StringDataValue.class);
    }


    //千万别动
    public String type = getClass().getSimpleName();

    /**
     * 玩家参数 支持基本数据类型
     * */
    public T value;

    public IDataValue(T value) {
        this.value = value;
    }

    public T getValue(){
        return value;
    }


    /**
     * 是否在原来的基础上增加这个值
     * */
    public boolean asAppend(){
        return true;
    }

    /**
     * 设置值
     * */
    public abstract void setValue(IDataValue<?> value);

    /**
     * 添加值
     * */
    public abstract void addValue(IDataValue<?> value);

    /**
     * 移除值
     * */
    public abstract void removeValue(IDataValue<?> value);


    public static IDataValue<?> jsonObjectAsDataValue(JsonObject jsonObject){
        if( IDataValue.DATA_VALUE.containsKey(jsonObject.get("type").getAsString())){
            Class<IDataValue<?>> dataValueClass = (Class<IDataValue<?>>) IDataValue.DATA_VALUE.get(jsonObject.get("type").getAsString());
            try {
                Type type = ((ParameterizedType)dataValueClass.getGenericSuperclass()).getActualTypeArguments()[0];

                Class<?> valueClass = Class.forName(type.getTypeName());
                Constructor<?> constructor = dataValueClass.getConstructor(valueClass);
                return (IDataValue<?>) constructor.newInstance(new Gson().fromJson(jsonObject.get("value"),valueClass));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new IntegerDataValue(0);
    }


    @Override
    public String toString() {
        return "IDataValue{" +
                "type='" + type + '\'' +
                ", value=" + value +
                '}';
    }
}
