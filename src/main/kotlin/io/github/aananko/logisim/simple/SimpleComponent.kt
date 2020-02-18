package io.github.aananko.logisim.simple

import com.cburch.logisim.data.*
import com.cburch.logisim.instance.*

interface KPort {
    fun placeTo(x: Int, y: Int)

    /**
     * Inconveniently long names are somewhat intentional -
     * better use [PortHelper.get] and [PortHelper.set]]
     * from [SimpleComponent.simplePropagate] instead.
     */
    fun getValueFrom(state: InstanceState): Value
    fun setValueTo(state: InstanceState, value: Value, delay: Int = 1)
}

/** making this class inline may improve performance, probably */
class PortHelper(private val state: InstanceState) {
    fun KPort.get(): Value = getValueFrom(state)
    fun KPort.set(value: Value, delay: Int = 1) {
        setValueTo(state, value, delay)
    }
}

fun Iterable<KPort>.placeColumn(x: Int, top: Int, yStep: Int = 5) {
    for ((index, port) in this.withIndex()) {
        port.placeTo(x, top + index * yStep)
    }
}

fun Iterable<KPort>.placeRow(left: Int, y: Int, xStep: Int = 5) {
    for ((index, port) in this.withIndex()) {
        port.placeTo(left + index * xStep, y)
    }
}

abstract class SimpleComponent(name: String): InstanceFactory(name) {
    private val portListBuilder = object {
        private val ports = mutableListOf<KPortImpl>()

        private inner class KPortImpl(
            val direction: String,
            val widthAttr: Attribute<BitWidth>? = null,
            val widthFixed: BitWidth? = null
        ): KPort {
            private val index = ports.size
            private var final: Boolean = false
            init {
                if (final) throw IllegalStateException("All ports must be created before finalizeSetup() call.")
                if ((widthAttr == null) == (widthFixed == null))
                    throw IllegalStateException(
                        "Specify either widthAttr or widthFixed (seems like a bug in SimpleComponent)"
                    )
                ports.add(this)
            }

            private var position: Pair<Int, Int>? = null

            override fun placeTo(x: Int, y: Int) { position = Pair(x, y) }
            override fun getValueFrom(state: InstanceState): Value = state.getPort(index)
            override fun setValueTo(state: InstanceState, value: Value, delay: Int) {
                state.setPort(index, value, delay)
            }

            fun toLogisimPortOrNull(): Port? =
                position?.let { (x, y) ->
                    widthAttr?.let {
                        Port(x, y, direction, it)
                    } ?: Port(x, y, direction, widthFixed)
                }

            fun toLogisimPort(): Port = toLogisimPortOrNull()
                ?: throw IllegalStateException(
                    "Every port must have a defined position before this point. Use place* functions."
                )

            fun finalize() {
                final = true
            }
        }

        fun getPortList(): List<KPort> = ports

        fun toLogisimPortArray() : Array<Port> =
            ports.map { it.toLogisimPort() }.toTypedArray()
    }

    fun port(direction: String, attr: Attribute<BitWidth>): KPort =
        portListBuilder.KPortImpl(direction, widthAttr = attr)
    fun input(attr: Attribute<BitWidth>): KPort = port(Port.INPUT, attr)
    fun output(attr: Attribute<BitWidth>): KPort = port(Port.OUTPUT, attr)

    fun port(direction: String, width: Int): KPort =
        portListBuilder.KPortImpl(direction, widthFixed = BitWidth.create(width))
    fun input(width: Int): KPort = port(Port.INPUT, width)
    fun output(width: Int): KPort = port(Port.OUTPUT, width)

    /**
     * Finalizes the SimpleComponent ports' setup.
     *
     * Must be called after all ports are created (with [port], [input], [output]),
     * but before getting and setting signal values on any ports
     * (which usually happens inside [simplePropagate] only).
     */
    protected fun finalizeSetup() {
        setOffsetBounds(Bounds.create(-60,-15,60,30))
        setPorts(portListBuilder.toLogisimPortArray())
    }

    override fun paintInstance(painter: InstancePainter) {
        painter.drawRectangle(painter.bounds, "test_0003")
        painter.drawPorts()
    }

    final override fun propagate(state: InstanceState) {
        PortHelper(state).simplePropagate()
    }

    protected abstract fun PortHelper.simplePropagate()

    fun setAttributes(vararg attrToDefaultValue: Pair<Attribute<*>, Any>) {
        val (attributes, defaultValues) = attrToDefaultValue.unzip()
        setAttributes(attributes.toTypedArray(), defaultValues.toTypedArray())
    }
}
