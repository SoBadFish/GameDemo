# GameDemo

小游戏的模板框架
修改模板来快捷的开发小游戏

## **开发文档**

---

#### **房间管理**

1.**简介**

本插件的房间管理通过 **RoomManager** 获取房间对象 (**GameRoom**) 以及房间的配置文件 (**GameRoomConfig**）来操作房间的启动。

2.**房间功能配置**

房间的配置在 GameRoomConfig.java 文件设置，对应的配置文件为 **room.yml** 。本模板已预设好如下参数

| 参数变量             | 类型                           | 介绍                                                                                                                                                                                                    |
| -------------------- | ------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| name                 | String                         | 游戏房间的名称 同时对应着插件文件夹中生成的文件                                                                                                                                                         |
| worldInfo            | WorldInfoConfig                | 游戏房间的地图配置,内部封装游戏地图，等待坐标等变量<br />开发者可以根据游戏的地图需求增加其他的                                                                                                         |
| time                 | int                            | 游戏总时长 超过这个时间游戏结束                                                                                                                                                                         |
| waitTime             | int                            | 当玩家数量满足最低人数限制的时候，就开始读取等待时长<br />达到等待时长后，游戏进入开始状态                                                                                                              |
| reSpawnTime          | int                            | 玩家的复活时长，如果这个时间为 0 则玩家死亡后<br />迅速回到队伍的出生点                                                                                                                                 |
| maxWaitTime          | int                            | 当房间人数到达上限后，等待时长就会相应的修改为maxWaitTime                                                                                                                                               |
| minPlayerSize        | int                            | 游戏房间开始的最低玩家数量                                                                                                                                                                              |
| maxPlayerSize        | int                            | 游戏房间的人数上限                                                                                                                                                                                      |
| isAutomaticNextRound | boolean                        | 当游戏结束或队伍失败后，当这个选项开启，则自动匹配其他游戏房间                                                                                                                                          |
| teamCfg              | Map `<String,TeamConfig>`    | 存放队伍的名称与队伍的数据信息 队伍的配置信息默认为<br /> 名称,队伍颜色字符，队伍方块颜色，队伍皮革套的颜色。<br />开发者可以根据游戏的需求对这个参数进行调整<br />**对应的配置文件为** team.yml |
| teamConfigs          | List `<TeamInfoConfig>`      | 存放队伍的配置，包含队伍的数据信息(TeamConfig),队伍的出生点<br />如果队伍中存在物品刷新点，可以在这个配置文件添加                                                                                       |
| hasWatch             | boolean                        | 游戏房间是否允许玩家进入观战                                                                                                                                                                            |
| callbackY            | int                            | 防止玩家在游戏的等待大厅跳下虚空 当玩家低于等待大厅的y轴<br />一定的值后将玩家传送回等待大厅的出生点                                                                                                   |
| floatTextInfoConfigs | List `<FloatTextInfoConfig>` | 浮空字的配置文件 包含名称，显示的坐标，显示的文本                                                                                                                                                       |
| banCommand           | List `<String>`              | 在游戏内禁用的指令                                                                                                                                                                                      |
| quitCommand          | List `<String>`              | 玩家退出游戏房间执行的指令                                                                                                                                                                              |
| victoryCommand       | List `<String>`              | 玩家在游戏中获得胜利执行的指令                                                                                                                                                                          |
| defeatCommand        | List `<String>`              | 玩家在游戏中失败执行的指令                                                                                                                                                                              |
| gameStartMessage     | List `<String>`              | 游戏开始时，给玩家发送的一些话                                                                                                                                                                          |

如果你的游戏房间有其他的参数需求，例如空岛战争需要资源箱，就可以在地图的配置中 **WorldInfoConfig** 写入资源箱的位置并在WorldInfoConfig中的**getInstance()** 中写入读取坐标点的逻辑 WorldInfoConfig 封装了 positionToString 方法和 getPositionByString 方法，可以将坐标点转换为字符串。

如果是房间内需要增加一些参数需要在 **getGameRoomConfigByFile** 中写入读取文件中配置的逻辑，加载到GameRoomConfig 中,并在**save()**中写入保存

2.**游戏房间逻辑**

只有房间被启动后 GameRoom才会被实例化 比如玩家加入房间，此时，GameRoomConfig 被激活，RoomManager就可以获取到相应的**GameRoom**房间对象

启动后房间的初始状态为 **WAIT 大部分都是写好的逻辑 构造方法可以不需要修改** 

可以在房间中增加一些变量来实现游戏的功能玩法

房间的主要逻辑为: 

#### onUpdate() 房间更新

当房间被实例化后 这个方法**每秒**就会触发一次 触发类在**RoomLoadRunnable** 线程类中。

##### onWait: 房间状态为等待玩家加入

##### onStart: 房间状态为正在进行

##### onEnd: 房间状态为结束

##### onClose: 房间状态为正在进行回收关闭，短时间无法启动


---

### 持续更新中....
