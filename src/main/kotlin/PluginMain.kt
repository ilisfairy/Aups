package moe.kusuri.mirai.plugin.AutiRefresh

import com.google.gson.Gson
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.PermissionDeniedException
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource
import net.mamoe.mirai.message.data.MessageSource.Key.recall

import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "moe.kusuri.AutiRefresh",
        name = "AutiRefresh",
        version = "0.0.1"
        )
) {
    data class Msg(
        val hash: Int,
        var num: Int = 0,
        var source: ArrayList<Any> = ArrayList(),
    )
    private val STCache = mutableMapOf<Long, Msg>()
    private val LTCache = arrayListOf<Msg>()
    private fun getTime(hour: Int, min: Int, sec: Int) = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, min)
        set(Calendar.SECOND, sec)
    }.time
    override fun onEnable() {
        val file = File("${configFolder.absolutePath}\\config.json")
        logger.info ("配置文件目录${configFolder.absolutePath}\\config.json")
        val config = if(file.isFile && file.exists())
            Gson().fromJson(file.readText(), Config:: class.java )
        else {
            logger.warning("找不到配置文件, 使用默认文件")
            Config(null, null, null, null, null)
        }
        val n = config.max?: 4
        val h = config.hour ?: 20
        val m = config.min ?: 0
        val s = config.sec ?: 0
        val period:Long = 86400000
        Timer().schedule(object:TimerTask(){
            override fun run() {
                logger.warning("清空长缓存")
                LTCache.clear()
            }
        }, getTime(h, m, s), period)
        logger.info ("清空缓存任务启动于${getTime(h, m, s)}，每隔${period}ms执行一次")
        logger.info ("撤回阈值为$n")

        fun recall(group: Group) {
            with(STCache[  group.id  ]!!) {
                for (i in 1 until num - 1) {
                    try {
                        source[i].recall()
                    } catch (e: PermissionDeniedException) {
                        logger.error("权限不足")
                    } catch(e: IllegalStateException ) {
                        logger.error("该消息已被撤回")
                    }
                }
            }
        }

        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            val msg = this.message.serializeToMiraiCode().hashCode()
            for(l in LTCache){
                if(l.hash == msg){
                    try {
                        this.message.recall()
                    } catch (e: PermissionDeniedException) {
                        logger.error("权限不足")
                    } catch(e: IllegalStateException ) {
                        logger.error("该消息已被撤回")
                    }
                    l.num += 1
                }
            }
            val num = 5

            if(!STCache.containsKey( this.group.id )){
                STCache[ this.group.id ] = Msg(msg, 1).apply { source.add(this@subscribeAlways.message[MessageSource]!!) }
                return@subscribeAlways
            }else{
                val temp = STCache[ this.group.id ]!!
                when (temp.hash) {
                    msg -> {
                        if (temp.num == num - 1) {
                            if(config.notification == true)
                                this.group .owner.sendMessage("[QQ群${this.group.id}]开始搞事情[${this.message.serializeToMiraiCode()}]")
                            recall()
                            LTCache.add(Msg(msg,num))
                            return@subscribeAlways
                        }else{
                            temp.num += 1
                            temp.source.add(this.message[MessageSource]!!)
                            return@subscribeAlways
                        }
                    }
                    else -> {
                        STCache[ this.group.id ] = Msg(msg,1).apply { source.add(this@subscribeAlways.message[MessageSource]!!) }
                        return@subscribeAlways
                    }
                }
            }
        }
    }
}

private fun Any.recall() {
    TODO("Not yet implemented")
}


