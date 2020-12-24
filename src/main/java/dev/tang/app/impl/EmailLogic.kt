package dev.tang.app.impl

import dev.tang.utils.FileUtils
import dev.tang.utils.PathUtils
import dev.tang.utils.TimeUtils
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeUtility
import kotlin.collections.HashSet

/**
 * 邮件发送者
 *
 * @author TangYing
 */
object EmailLogic {

    private val props = Properties()

    /**
     * 需要归档的账号以及对应的路径
     * 路径对应关系
     */
    val accountMap = mutableMapOf<String, String>()

    /**
     * 归档成功
     */
    val successSet = HashSet<String>()

    fun read() {
        try {
            info("程序开始运行…")
            accountMap.clear()
            successSet.clear()

            // 初始化配置
            val lines = readConf()
            val host = lines[0].split("->")[0]
            val user = lines[0].split("->")[1]
            val password = lines[0].split("->")[2]
            val startTime = TimeUtils.dateToTime(lines[1].split("->")[0])
            val endTime = TimeUtils.dateToTime(lines[1].split("->")[1])
            val startIndex = lines[2].split("->")[0].toInt()
            val endIndex = lines[2].split("->")[1].toInt()
            info("$host $user $password")

            // 1、创建session
            val session = Session.getDefaultInstance(props, null)

            // 2、通过session得到Store对象
            val store = session.getStore("pop3")

            // 3、连上邮件服务器
            store.connect(host, user, password)

            val folder = store.getFolder("inbox")
            folder.open(Folder.READ_ONLY)

            // 初始化对应关系
            lines.subList(3, lines.size).forEach {
                val r = it.split("->")
                accountMap[r[0]] = r[1]
            }

            // 获得邮件夹Folder内的所有邮件Message对象
            folder.getMessages(startIndex, kotlin.math.min(folder.messageCount, endIndex)).forEach { message ->
                if (message.sentDate.time in startTime..endTime) {
                    when (val content = message.content) {
                        is Multipart -> handleMultipart(message, content)
                        is Part -> handlePart(message, content)
                        else -> {
                            // Do nothing
                        }
                    }
                }
            }

            // 5、关闭
            folder.close(false)
            store.close()

            // 输出归档失败的账号
            accountMap.forEach {
                val email = it.key
                if (!successSet.contains(email)) {
                    failure(email)
                }
            }

            info("程序运行结束…")
        } catch (e: Exception) {
            e.message?.let {
                error(it)
            }
        }
    }

    private fun handleMultipart(message: Message, mPart: Multipart) {
        for (i in 0 until mPart.count) {
            val part = mPart.getBodyPart(i)
            if (part is Multipart) {
                handleMultipart(message, part)
            } else {
                handlePart(message, part)
            }
        }
    }

    private fun handlePart(message: Message, part: Part) {
        if (part.disposition == Part.ATTACHMENT) {
            val subject = message.subject
            val date = TimeUtils.timeToDate(message.sentDate.time)
            val from = MimeUtility.decodeText(message.from[0].toString())
            val emailAddress = from.subSequence(from.indexOf("<")  + 1, from.indexOf(">")).toString()
            accountMap[emailAddress]?.let { path ->
                val fileName = MimeUtility.decodeText(part.fileName)
                FileUtils.mkdir(path)
                FileUtils.writeFile(path + File.separator + fileName, part.inputStream)

                // 归档成功
                successSet.add(emailAddress)
                success("Date $date, From $emailAddress, Subject $subject, File $fileName")
            }
        }
    }

    /**
     * 读取关系对应配置
     */
    private fun readConf(): List<String> {
        val path = Paths.get(PathUtils.rootPath, "归档配置.txt")
        return Files.readAllLines(path)
    }

    /**
     * 信息输出
     */
    fun info(c: String) {
        val loggerPath = PathUtils.loggerEmailPath
        FileUtils.writeFile(loggerPath, "[Info] $c" , true)
    }

    /**
     * 错误的结果输出
     */
    fun error(c: String) {
        val loggerPath = PathUtils.loggerEmailPath
        FileUtils.writeFile(loggerPath, "[Error] $c" , true)
    }

    /**
     * 归档成功的结果输出
     */
    fun success(c: String) {
        val loggerPath = PathUtils.loggerEmailPath
        FileUtils.writeFile(loggerPath, "[成功] $c" , true)
    }

    /**
     * 归档失败的结果输出
     */
    fun failure(c: String) {
        val loggerPath = PathUtils.loggerEmailPath
        FileUtils.writeFile(loggerPath, "[失败] $c" , true)
    }

    /**
     * 邮件发送
     *
     * @param receiver
     * @param title
     * @param content
     */
    fun send(receiver: String?, title: String, content: String) {
        if (receiver == null || receiver == "") {
            return
        }

        //创建缺省的session对象
        val session = Session.getDefaultInstance(props, null)

        //创建message对象
        val msg = MimeMessage(session)

        //设置发件人和收件人
        try {
            // 设置发件人
            val addressFrom = InternetAddress("xxx@126.com")
            addressFrom.personal = "SAO2游戏"
            msg.setFrom(addressFrom)

            // 设置收件人
            val addressTo = InternetAddress(receiver)
            msg.setRecipient(Message.RecipientType.TO, addressTo)

            //设置邮件标题
            msg.subject = title
            msg.setText(content)

            // 设置传输协议
            val transport = session.getTransport("smtp")
            transport.connect("smtp.126.com", "xxx@126.com", "")
            transport.sendMessage(msg, msg.allRecipients)
            transport.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
