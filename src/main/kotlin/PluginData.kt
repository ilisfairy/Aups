package moe.kusuri.mirai.plugin.AutiRefresh

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object PluginData : AutoSavePluginData("PluginData") {
    var mutableMap: MutableMap<Long, Int> by value()
}