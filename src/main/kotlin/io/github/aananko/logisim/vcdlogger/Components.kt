package io.github.aananko.logisim.vcdlogger

import com.cburch.logisim.tools.AddTool
import com.cburch.logisim.tools.Library
import com.cburch.logisim.tools.Tool

class Components: Library() {
    override fun getDisplayName(): String = "Test_00500 tools"

    private val tools: MutableList<Tool> = mutableListOf(
        AddTool(TestComponent())
    )

    override fun getTools(): MutableList<Tool> = tools
}

