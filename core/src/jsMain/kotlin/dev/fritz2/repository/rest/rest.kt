package dev.fritz2.repository.rest

import dev.fritz2.remote.Request
import dev.fritz2.remote.Response
import dev.fritz2.remote.http
import dev.fritz2.repository.EntityRepository
import dev.fritz2.repository.QueryRepository
import dev.fritz2.repository.Resource
import dev.fritz2.repository.ResourceNotFoundException


/**
 * provides CRUD-functions for REST-API to a defined [Resource]
 *
 * @param resource definition of the [Resource]
 * @param url base-url of the REST-API
 * @param initialId id to compare a given resource for differentiation of adding or updating
 * @param contentType to be used by the REST-API
 */
fun <T, I> restEntityOf(
    resource: Resource<T, I>,
    url: String,
    initialId: I,
    contentType: String = "application/json; charset=utf-8"
): EntityRepository<T, I> =
    RestEntity(resource, initialId, contentType, http(url))

/**
 * provides crud-functions for REST-API to a defined [Resource]
 *
 * @param resource definition of the [Resource]
 * @param remote base [Request] to be used by all subsequent requests. Use it to configure authentication, etc.
 * @param initialId id to compare a given resource for differentiation of adding or updating
 * @param contentType to be used by the REST-API
 */
fun <T, I> restEntityOf(
    resource: Resource<T, I>,
    remote: Request,
    initialId: I,
    contentType: String = "application/json; charset=utf-8"
): EntityRepository<T, I> =
    RestEntity(resource, initialId, contentType, remote)

/**
 * provides crud-functions for REST-API to a defined [Resource]
 *
 * @param resource definition of the [Resource]
 * @param initialId id to compare a given resource for differentiation of adding or updating
 * @param contentType to be used by the REST-API
 * @param remote base [Request] to be used by all subsequent requests. Use it to configure authentication, etc.
 */
class RestEntity<T, I>(
    private val resource: Resource<T, I>,
    val initialId: I,
    val contentType: String,
    private val remote: Request
) : EntityRepository<T, I> {

    /**
     * loads an entity by a get request to [resource]/{id}
     *
     * @param id of the entity to load
     * @return the entity (identified by [id]) loaded
     * @throws ResourceNotFoundException when resource not found
     */
    override suspend fun load(id: I): T =
        try {
            resource.deserialize(
                remote.accept(contentType).get(resource.serializeId(id))
                    .body()
            )
        } catch (throwable: Throwable) {
            throw ResourceNotFoundException(resource.serializeId(id), throwable)
        }

    /**
     * sends a post-(for add) or a put-(for update) request to [remote]/{id}
     * with the serialized entity in it's body.
     * The [initialId] is used to determine if it should add or updated.
     *
     * @param entity entity to save
     * @return the added or saved entity
     */
    override suspend fun addOrUpdate(entity: T): T =
        remote.contentType(contentType)
            .body(resource.serialize(entity)).run {
                if (resource.idProvider(entity) == initialId) {
                    resource.deserialize(
                        accept(contentType).post().body()
                    )
                } else {
                    put(resource.serializeId(resource.idProvider(entity)))
                    entity
                }
            }

    /**
     * deletes an entity by a delete-request to [resource].url/{id}
     *
     * @param entity entity to delete
     * @return the emptyEntity defined at [resource]
     */
    override suspend fun delete(entity: T) {
        remote.delete(resource.serializeId(resource.idProvider(entity)))
    }
}


/**
 * provides services to deal with queries for REST-API to a defined [Resource]
 *
 * @param resource definition of the [Resource]
 * @param url base-url of the REST-API
 * @param initialId id to compare a given resource for differentiation of adding or updating
 * @param contentType to be used by the REST-API
 * @param buildQuery function to build a [Request] for a given object defining the query
 */
fun <T, I, Q> restQueryOf(
    resource: Resource<T, I>,
    url: String,
    initialId: I,
    contentType: String = "application/json; charset=utf-8",
    buildQuery: suspend Request.(Q) -> Response = { accept(contentType).get() }
): QueryRepository<T, I, Q> = RestQuery(resource, initialId, contentType, http(url), buildQuery)

/**
 * provides services to deal with queries for REST-API to a defined [Resource]
 *
 * @param resource definition of the [Resource]
 * @param remote base [Request] to be used by all subsequent requests. Use it to configure authentication, etc.
 * @param initialId id to compare a given resource for differentiation of adding or updating
 * @param contentType to be used by the REST-API
 * @param buildQuery function to build a [Request] for a given object defining the query
 */
fun <T, I, Q> restQueryOf(
    resource: Resource<T, I>,
    remote: Request,
    initialId: I,
    contentType: String = "application/json; charset=utf-8",
    buildQuery: suspend Request.(Q) -> Response = { accept(contentType).get() }
): QueryRepository<T, I, Q> = RestQuery(resource, initialId, contentType, remote, buildQuery)

/**
 * provides services to deal with queries for REST-API to a defined [Resource]
 *
 * @param resource definition of the [Resource]
 * @param initialId id to compare a given resource for differentiation of adding or updating
 * @param contentType to be used by the REST-API
 * @param remote base [Request] to be used by all subsequent requests. Use it to configure authentication, etc.
 * @param buildQuery function to build a [Request] for a given object defining the query
 */
class RestQuery<T, I, Q>(
    private val resource: Resource<T, I>,
    val initialId: I,
    val contentType: String,
    private val remote: Request,
    private inline val buildQuery: suspend Request.(Q) -> Response
) : QueryRepository<T, I, Q> {

    /**
     * queries the resource by sending the request which is build by [buildQuery] using the [query]
     *
     * @param query object defining the query
     * @return result of the query
     */
    override suspend fun query(query: Q): List<T> =
        resource.deserializeList(remote.buildQuery(query).body())

    /**
     * updates given entities in the [entities] list
     * (sending a put-request to the base-url of the [resource] for every updated entity)
     *
     * @param entities current list
     * @param entitiesToUpdate entities which getting updated
     * @return list after update
     */
    override suspend fun updateMany(entities: List<T>, entitiesToUpdate: List<T>): List<T> {
        val request = remote.contentType(contentType)
        val updated: Map<I, T> = (entities + entitiesToUpdate).groupBy { resource.idProvider(it) }
            .filterValues { it.size > 1 }.mapValues { (id, entities) ->
                val entity = entities.last()
                request.body(resource.serialize(entity))
                    .put(resource.serializeId(id))
                entity
            }
        return entities.map { updated[resource.idProvider(it)] ?: it }
    }

    /**
     * sends a post-(for add) or a put-(for update) request to [remote]/{id}
     * with the serialized entity in it's body. The [initialId] is used to
     * determine if it should saved or updated.
     *
     * @param entities entity list
     * @param entity entity to add or update
     * @return list after add or update
     */
    override suspend fun addOrUpdate(entities: List<T>, entity: T): List<T> =
        remote.contentType(contentType)
            .body(resource.serialize(entity)).run {
                if (resource.idProvider(entity) == initialId) {
                    entities + resource.deserialize(
                        accept(contentType).post().body()
                    )
                } else {
                    put(resource.serializeId(resource.idProvider(entity)))
                    entities.map { if(resource.idProvider(it) == resource.idProvider(entity)) entity else it }
                }
            }

    private suspend fun deleteById(id: I) {
        remote.delete(resource.serializeId(id))
    }

    /**
     * deletes one entity from the list
     *
     * @param entities list before deletion
     * @param id identifies the entity to delete
     * @return list after deletion
     */
    override suspend fun delete(entities: List<T>, id: I): List<T> {
        deleteById(id)
        return entities.filterNot { resource.idProvider(it) == id }
    }

    /**
     * deletes multiple entities from the list
     *
     * @param entities list before deletion
     * @param ids identifies the entities to delete
     * @return list after deletion
     */
    override suspend fun delete(entities: List<T>, ids: List<I>): List<T> {
        ids.forEach { deleteById(it) }
        return entities.filterNot { ids.contains(resource.idProvider(it)) }
    }
}
