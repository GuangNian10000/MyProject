// 定义常量
val SERVER_TYPE_TEST = "test"
val SERVER_TYPE_PRODUCT = "product"
val SERVER_TYPE_PRODUCT_TC = "productTC"

// 获取当前 Task 名称
// Kotlin 中列表可能为空，需要安全获取
val taskNames = project.gradle.startParameter.taskNames
val taskName = taskNames.firstOrNull() ?: ""

println("GradleLog TaskNameOutput $taskName")

// 默认 Server Type
var serverType = SERVER_TYPE_PRODUCT

// 根据 Task 名称判断环境
if (taskName.endsWith("Debug")) {
    serverType = SERVER_TYPE_TEST
} else if (taskName.endsWith("Release")) {
    serverType = SERVER_TYPE_PRODUCT
} else if (taskName.endsWith("ReleaseTC")) {
    serverType = SERVER_TYPE_PRODUCT_TC
}

// 从 Gradle 命令读取参数配置 (-P ServerType="test")
if (project.hasProperty("ServerType")) {
    serverType = project.property("ServerType").toString()
}

println("GradleLog ServerTypeOutput $serverType")

// 定义配置变量
var logEnable = false
var hostUrl = ""
var logChannel = "" // 默认为空字符串，防止未定义报错

// 根据 serverType 设置具体的值 (对应原 switch 语句)
when (serverType) {
    SERVER_TYPE_TEST -> {
        logEnable = true
        hostUrl = "https://api.synthetic.audio"
        logChannel = ""
    }
    SERVER_TYPE_PRODUCT -> {
        logEnable = false
        hostUrl = "https://api.synthetic.audio"
    }
    SERVER_TYPE_PRODUCT_TC -> {
        logEnable = false
        hostUrl = "https://api.synthetic.audio" // "https://audio-jp.dlxk.com"
    }
    else -> {
        // 默认兜底逻辑 (可选)
        logEnable = false
        hostUrl = "https://api.synthetic.audio"
    }
}

// 关键步骤：将变量存入 extra 属性中，以便在 build.gradle.kts 中使用
extra["LOG_ENABLE"] = logEnable
extra["HOST_URL"] = hostUrl
extra["LOG_CHANNEL"] = logChannel