package moe.nyanna.mirai.plugin.AutireFresh

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object PluginData : AutoSavePluginData("PluginData") {
    var mutableMap: MutableMap<Long, Int> by value()
}