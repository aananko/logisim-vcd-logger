package io.github.aananko.logisim.vcdlogger

import com.cburch.logisim.data.Attribute
import com.cburch.logisim.data.Attributes
import com.cburch.logisim.data.BitWidth
import com.cburch.logisim.instance.Instance
import com.cburch.logisim.instance.Port
import io.github.aananko.logisim.simple.*

class TestComponent : SimpleComponent("Test Component") {

    private val i = input(4)
    private val o = output(4)

    private val inputsCount: Attribute<Int> = "inputsCount".let {
        Attributes.forInteger(it) { Strings.get(it) }
    }

    init {
        setAttributes(
            inputsCount to 1
        )
        listOf(i, o).placeRow(-60, 0, 60)
//        i.placeTo(-30,0)
//        o.placeTo(0,0)
        finalizeSetup()
    }

    override fun PortHelper.simplePropagate() {
        o.set(i.get())
    }

    override fun configureNewInstance(instance: Instance) {
        instance.addAttributeListener()
    }

    override fun instanceAttributeChanged(instance: Instance, attr: Attribute<*>) {
        if(attr === inputsCount) {
            setPorts(
                (0 until instance.getAttributeValue(attr))
                    .map { Port(-30, 0 + 10*it, Port.INPUT, BitWidth.create(4)) }
                    .toTypedArray()
            )
            //instance.fireInvalidated()
            //Instance.getComponentFor(instance).
            attr.displayName
        }
    }
}