package org.sobadfish.gamedemo.manager;


import org.checkerframework.checker.nullness.qual.NonNull;
import org.sobadfish.gamedemo.room.GameRoom;
import org.sobadfish.gamedemo.thread.PluginMasterRunnable;
import org.sobadfish.gamedemo.thread.RandomJoinRunnable;
import org.sobadfish.gamedemo.thread.RoomLoadRunnable;
import org.sobadfish.gamedemo.thread.TopRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程管理器
 * 不建议更改
 * @author SoBadFish
 * 2022/1/2
 */
public class ThreadManager {


    public static final List<AbstractGameRunnable> RUNNABLE = new CopyOnWriteArrayList<>();

    /**
     * 线程池数量
     * */
    private final static Integer CORE_POOL_SIZE = 5;



    /**
     * 后台任务
     * */
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 1000, TimeUnit.MILLISECONDS
            , new ArrayBlockingQueue<>(5), new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            if(r instanceof AbstractThreadRunnable){
                return new Thread(r,"后台任务: "+((AbstractThreadRunnable) r).getThreadName());
            }
            return new Thread(r,"后台任务: "+r.hashCode());
        }
    });


    private static final ScheduledThreadPoolExecutor SCHEDULED = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE,new ThreadPoolExecutor.AbortPolicy());


    public static void cancel(AbstractGameRunnable r) {
        RUNNABLE.remove(r);
        SCHEDULED.remove(r);
    }

    private static void schedule(AbstractGameRunnable r, int delay) {
        RUNNABLE.add(r);
        SCHEDULED.scheduleAtFixedRate(r,delay,1,TimeUnit.SECONDS);
    }

    private static void schedule(AbstractGameRunnable r) {
        schedule(r,0);
    }


    /**
     * 获取当前线程池线程数量
     * @return 线程的数量
     */
    public static int getScheduledSize() {
        return SCHEDULED.getPoolSize();
    }

    /**
     * 获取当前活动的线程数量
     * @return 正在进行的线程数量
     */
    public static int getScheduledActiveCount() {
        return SCHEDULED.getActiveCount();
    }




    public static String info() {
        StringBuilder builder = new StringBuilder();
        Map<String, List<AbstractGameRunnable>> map = getRunnables();
        for(Map.Entry<String, List<AbstractGameRunnable>> me : map.entrySet()){
            builder.append("&r").append(me.getKey()).append("\n").append(listToString(me.getValue()));
        }
        String s = builder.toString();
        if("".equalsIgnoreCase(s)){
            return "null";
        }
        return s;
    }


    public static void runRunnable(Runnable runnable){
        THREAD_POOL_EXECUTOR.execute(runnable);
    }

    private static String listToString(List<AbstractGameRunnable> runnables){
        StringBuilder s = new StringBuilder();
        for(AbstractGameRunnable runnable: runnables){
            s.append("  &r- ").append(runnable.getThreadName()).append("\n");
        }
        return s.toString();
    }

    private static Map<String,List<AbstractGameRunnable>> getRunnables(){
        LinkedHashMap<String, List<AbstractGameRunnable>> threadList = new LinkedHashMap<>();

        for(AbstractGameRunnable workerValue: RUNNABLE) {
            GameRoom room = workerValue.getRoom();
            if (room != null) {
                if (!threadList.containsKey(room.getRoomConfig().name)) {
                    threadList.put(room.getRoomConfig().name, new ArrayList<>());
                }
                List<AbstractGameRunnable> runnables = threadList.get(room.getRoomConfig().name);
                runnables.add(workerValue);
                threadList.put(room.getRoomConfig().name, runnables);
            } else {
                String name = "Unknown";
                if (!threadList.containsKey(name)) {
                    threadList.put(name, new ArrayList<>());
                }
                List<AbstractGameRunnable> runnables = threadList.get(name);
                runnables.add(workerValue);
                threadList.put(name, runnables);
            }
        }
        return threadList;
    }

    public static void init() {
        ThreadManager.schedule(new RunnableCheck());
        ThreadManager.schedule(new PluginMasterRunnable(),1);
        ThreadManager.schedule(new RoomLoadRunnable());
        ThreadManager.schedule(new TopRunnable());
        ThreadManager.schedule(new RandomJoinRunnable());

    }

    public abstract static class AbstractThreadRunnable implements Runnable{

        public boolean isClose;

        /**
         * 获取线程名称
         * @return 线程名称
         * */
        abstract public String getThreadName();

        public boolean isClose() {
            return isClose;
        }
    }

    public abstract static class AbstractGameRunnable implements Runnable{

        public boolean isClose;

        /**
         * 游戏房间
         * @return 房间
         * */
        abstract public GameRoom getRoom();

        /**
         * 获取线程名称
         * @return 线程名称
         * */
        abstract public String getThreadName();

        public boolean isClose() {
            return isClose;
        }
    }

    public static class RunnableCheck extends AbstractGameRunnable {
        @Override
        public GameRoom getRoom() {
            return null;
        }

        @Override
        public String getThreadName() {
            String color = "&a";
            if(isClose){
                color = "&7";
            }
            return color+TotalManager.getLanguage().getLanguage("thread-chunk","线程检测");
        }

        @Override
        public void run() {
            if(isClose){
                return;
            }
            if(TotalManager.isDisabled()){
                isClose = true;
                return;
            }
            for (AbstractGameRunnable runnable : RUNNABLE) {
                if (runnable.isClose) {
                    cancel(runnable);
                }
            }
        }
    }
}
