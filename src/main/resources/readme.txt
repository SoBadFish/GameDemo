事件系统是作用在游戏中的事件是 event.yml 文件 这个决定着游戏的进程
事件的配置格式如下:

 - type: "事件类型"
   eventTime: 需要多长时间触发事件（单位： 秒）
   display: "显示的名称"
   value: "执行的内容"



案例
 - type: "effect"
   eventTime: 30
   display: "一个药水效果"
   value: "药水id:等级:时间"

目前插件内置的事件类型有

custom: 自定义
effect: 药水效果
command: 执行指令


custom 自定义事件

----------------------------
其中custom 事件的value参数格式为

"类型:事件ID"

类型分为: while random foreach

事件ID: 为 roomEventList.yml 文件中的事件 其中 0 是第一条 后面的以此类推

while: 循环执行x事件

示例 "while:0": 循环执行 roomEventList.yml 文件中的 第一个事件

random: 随机执行事件

示例 "random:0-5": 从事件ID 0-5中随机执行一个事件 使用","分隔
示例2 "random:0,2,3,5" 随机执行 0 2 3 5事件

foreach: 顺序执行事件

示例 "foreach:0-5" 从0开始执行事件 一直到 5结束
----------------------------
effect 药水事件

其中effect 事件的value参数格式为

单个效果:
value: "药水id:等级:时间"
value: "药水id:时间"
value: "药水id"
其中时间和等级可不填 默认1级1秒 单位是秒


多个效果:

value: [""药水id:时间]

----------------------------
command 指令事件

其中 command 事件的value参数格式为

value: "give @p 264 1"

给予玩家一颗钻石
