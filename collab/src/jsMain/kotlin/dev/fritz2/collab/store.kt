package dev.fritz2.collab

import dev.fritz2.core.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class Update<D>(
    val apply: suspend (D) -> D,
    var documentUpdated: Boolean = false,
)

class DocumentReference {
    // todo
}

interface SharedStore<D> : WithJob {

    fun <A> handle(
        execute: suspend (D, A) -> D
    ) = SimpleHandler<A> { flow, job ->
        flow.onEach { enqueue(Update({ d -> execute(d, it) })) }
            .catch { d -> errorHandler(d) }
            .launchIn(MainScope() + job)
    }

    fun handle(
        execute: suspend (D) -> D
    ) = SimpleHandler<Unit> { flow, job ->
        flow.onEach { enqueue(Update({ d -> execute(d) })) }
            .catch { d -> errorHandler(d) }
            .launchIn(MainScope() + job)
    }

    fun <A, E> handleAndEmit(
        execute: suspend FlowCollector<E>.(D, A) -> D
    ) = EmittingHandler<A, E>({ inFlow, outFlow, job ->
        inFlow.onEach { enqueue(Update({ d -> outFlow.execute(d, it) })) }
            .catch { d -> errorHandler(d) }
            .launchIn(MainScope() + job)
    })

    fun <E> handleAndEmit(
        execute: suspend FlowCollector<E>.(D) -> D
    ) = EmittingHandler<Unit, E>({ inFlow, outFlow, job ->
        inFlow.onEach { enqueue(Update({ d -> outFlow.execute(d) })) }
            .catch { d -> errorHandler(d) }
            .launchIn(MainScope() + job)
    })

    suspend fun enqueue(update: Update<D>)

    val id: String

    val path: String

    val data: Flow<D>

    val current: D

    val update: Handler<D>

    fun <X> sub(lens: Lens<D, X>): SubStore<D, X> = TODO("unimplemented")

    val codec: Codec<D, *>

    val docRef: DocumentReference
}

abstract class _SharedStore<D, out SharedType>(
    initialData: D
) {
    val store: Store<D> = storeOf(initialData)
    val codec: Codec<D, SharedMap> =
}

open class SharedRootStore<D>(
    initialData: D,
    override val id: String = Id.next()
) : SharedStore<D> {

    private val state: MutableStateFlow<D> = MutableStateFlow(initialData)
    private val mutex = Mutex()

    override suspend fun enqueue(update: Update<D>) {
        mutex.withLock {
            if (!update.documentUpdated) {
                // todo: Update document
            }
            state.value = update.apply(state.value)
        }
    }
}