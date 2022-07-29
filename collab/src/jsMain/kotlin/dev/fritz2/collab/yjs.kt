package dev.fritz2.collab

@JsModule("yjs")
@JsNonModule
external object Y {

    class Doc {

        fun getArray(key: String): Array
            = definedExternally

        fun getMap(key: String): Map
            = definedExternally

        fun transact(transaction: () -> Unit, origin: dynamic = definedExternally): Unit
            = definedExternally

        fun on(eventType: String, handler: (dynamic, dynamic) -> Unit): Unit
            = definedExternally

        val clientID: String
    }

    abstract class AbstractType

    class Array : AbstractType {

        fun delete(index: Int): dynamic
            = definedExternally

        fun get(index: Int): dynamic
            = definedExternally

        fun insert(index: Int, items: dynamic): dynamic
            = definedExternally

        fun push(items: dynamic): dynamic
            = definedExternally

        val length: Int
    }

    class Map : AbstractType {

        fun get(key: String): dynamic
            = definedExternally

        fun set(key: String, value: dynamic): dynamic
            = definedExternally

        val size: Int
    }

}

typealias SharedType = Y.AbstractType
typealias SharedMap = Y.Map
typealias SharedArray = Y.Array
typealias SharedDocument = Y.Doc

inline fun SharedDocument.onUpdate(noinline handler: (update: ByteArray, origin: dynamic) -> Unit): Unit
    = on("update", handler)

inline fun SharedDocument.transact(noinline transaction: () -> Unit): Unit
    = transact(transaction, clientID)

inline fun <S: SharedType> SharedArray.getAsShared(index: Int): S
    = get(index).unsafeCast<S>()

inline fun <S: SharedType> SharedMap.getAsShared(key: String): S
    = get(key).unsafeCast<S>()