package moe.nyadoki.mirai.plugin.AutiRefresh

data class Config(
    val max:Int?,
    val hour:Int?,
    val min:Int?,
    val sec:Int?,
    val notification:Boolean?
)
