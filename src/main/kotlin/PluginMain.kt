@file:Suppress("PackageName")

package moe.naynna.mirai.plugin.AutiRefresh

import com.google.gson.Gson
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.PermissionDeniedException
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import java.io.File
import java.util.*
object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "com.naynna.AutiRefresh",
        name = "AutiRefresh",
        version = "0.0.1"
        )
) {
    data class Msg(
        val hash: Int,
        var num: Int,
        val source: ArrayList<MessageSource> = ArrayList()
    )
    private val STCache:MutableMap<Long, Msg> = mutableMapOf()
    val LTCache = ArrayList<Msg>()
    fun getTime(hour:Int, min:Int, sec:Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, min)
        calendar.set(Calendar.SECOND, sec)
        return calendar.time
    }
    override fun onEnable() {
        val file = File("${configFolder.absolutePath}\\config.json")
        logger.info("配置文件目录${configFolder.absolutePath}\\config.json")
        val config = if(file.isFile && file.exists())
            Gson().fromJson(file.readText(), Config::class.java)
        else {
            logger.warning("找不到配置文件, 使用默认文件")
            Config(null, null, null, null, null)
        }
        val n = config.max ?: 4
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
        logger.info("清空缓存任务启动于${getTime(h, m, s)}，每隔${period}ms执行一次")
        logger.info("撤回阈值为$n")
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            val msg = this.message.serializeToMiraiCode().hashCode()
            for(l in LTCache){
                if(l.hash == msg){
                    try{
                        this.message.recall()
                    }catch (e: PermissionDeniedException){
                        logger.error("权限不足")
                    }catch(e: IllegalStateException ){
                        logger.error("该消息已被撤回")
                    }
                    l.num += 1
                    return@subscribeAlways
                }
            }
            if(!STCache.containsKey(this.group.id)){
                STCache[this.group.id] = Msg(msg, 1)
                STCache[this.group.id]!!.source.add(this.message[MessageSource]!!)
                return@subscribeAlways
            }else{
                val temp = STCache[this.group.id]!!
                if(temp.hash == msg) {
                    if (temp.num == n - 1) {
                        if(config.notification == true)
                            this.group.owner.sendMessage("[QQ群${this.group.id}]开始刷屏[${this.message.serializeToMiraiCode()}]")
                        try {
                            for(i in 1 until n - 1){
                                try {
                                    temp.source[i].recall()
                                } catch (e: PermissionDeniedException) {
                                    logger.error("权限不足")
                                } catch (e: IllegalStateException) {
                                    logger.error("该消息已被撤回")
                                }
                            }
                            this.message.recall()
                        } catch (e: PermissionDeniedException) {
                            logger.error("权限不足")
                        } catch (e: IllegalStateException) {
                            logger.error("该消息已被撤回")
                        }
                        LTCache.add(Msg(msg,n))
                        return@subscribeAlways
                    }else{
                        temp.num += 1
                        temp.source.add(this.message[MessageSource]!!)
                        return@subscribeAlways
                    }
                }else{
                    STCache[this.group.id] = Msg(msg,1)
                    STCache[this.group.id]!!.source.add(this.message[MessageSource]!!)
                    return@subscribeAlways
                }
            }
        }
    }
}
