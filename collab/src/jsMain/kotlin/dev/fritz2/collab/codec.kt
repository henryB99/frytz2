package dev.fritz2.collab

interface Codec<T, S: SharedType> {
    fun createShared(value: T): S
    fun updateShared(shared: S, value: T)
    fun decodeShared(shared: S): T
}

internal const val VALUE_KEY = ""

sealed class PrimitiveCodec<P> : Codec<P, SharedMap> {
    override fun createShared(value: P): SharedMap {
        val shared = SharedMap()
        shared.set(VALUE_KEY, value)
        return shared
    }

    override fun updateShared(shared: SharedMap, value: P) {
        shared.set(VALUE_KEY, value)
    }

    override fun decodeShared(shared: SharedMap): P {
        return shared.get(VALUE_KEY).unsafeCast<P>()
    }
}

object IntCodec : PrimitiveCodec<Int>()
object DoubleCodec : PrimitiveCodec<Double>()
object BooleanCodec : PrimitiveCodec<Boolean>()
object StringCodec : PrimitiveCodec<String>()

abstract class EnumCodec<E: Enum<E>>(
    private val values: Array<E>
) : Codec<E, SharedMap> {
    final override fun createShared(value: E): SharedMap {
        val shared = SharedMap()
        shared.set(VALUE_KEY, value.ordinal)
        return shared
    }

    final override fun updateShared(shared: SharedMap, value: E) {
        shared.set(VALUE_KEY, value.ordinal)
    }

    final override fun decodeShared(shared: SharedMap): E {
        return values[shared.get(VALUE_KEY).unsafeCast<Int>()]
    }
}

abstract class ListCodec<I, S: SharedType>(
    private val itemCodec: Codec<I, S>
): Codec<List<I>, SharedArray> {
    final override fun createShared(value: List<I>): SharedArray {
        val shared = SharedArray()
        for (item in value) {
            shared.push(item)
        }
        return shared
    }

    final override fun updateShared(shared: SharedArray, value: List<I>) {
        val newItems = js("new Array()")
        val maxSize = maxOf(value.size, shared.length)

        for (index in 0 until maxSize) {
            val item = value.getOrNull(index)
            val sharedItem = shared.get(index) as? S

            if (item != null && sharedItem != null) {
                itemCodec.updateShared(sharedItem, item)
            }
            else if (item != null) {
                newItems.push(itemCodec.createShared(item))
            }
            else if (sharedItem != null) {
                shared.delete(index)
            }
        }

        if (newItems.length > 0) {
            shared.push(newItems)
        }
    }

    final override fun decodeShared(shared: SharedArray): List<I> = List(shared.length) { index ->
        itemCodec.decodeShared(shared.getAsShared(index))
    }
}