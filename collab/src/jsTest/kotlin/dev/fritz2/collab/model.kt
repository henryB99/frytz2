package dev.fritz2.collab

enum class Genre {
    ACTION,
    DRAMA,
    HORROR,
    UNKNOWN
}

data class Person(
    val firstName: String = "",
    val lastName: String = "",
)

data class Movie(
    val title: String = "",
    val director: Person = Person(),
    val actors: List<Person> = emptyList(),
    val rating: Double = -1.0,
    val released: Int = -1,
    val genre: Genre = Genre.UNKNOWN,
    val watched: Boolean = false,
)

object GenreCodec : EnumCodec<Genre>(enumValues())

object PersonCodec : Codec<Person, SharedMap> {
    override fun createShared(value: Person): SharedMap {
        val shared = SharedMap()
        shared.set("firstName", StringCodec.createShared(value.firstName))
        shared.set("lastName", StringCodec.createShared(value.lastName))
        return shared
    }

    override fun updateShared(shared: SharedMap, value: Person) {
        StringCodec.updateShared(shared.getAsShared("firstName"), value.firstName)
        StringCodec.updateShared(shared.getAsShared("lastName"), value.lastName)
    }

    override fun decodeShared(shared: SharedMap) = Person(
        firstName = shared.getAsShared("firstName"),
        lastName = shared.getAsShared("lastName"),
    )
}

object PersonListCodec : ListCodec<Person, SharedMap>(itemCodec = PersonCodec)

object MovieCodec : Codec<Movie, SharedMap> {
    override fun createShared(value: Movie): SharedMap {
        val shared = SharedMap()
        shared.set("title", StringCodec.createShared(value.title))
        shared.set("director", PersonCodec.createShared(value.director))
        shared.set("actors", PersonListCodec.createShared(value.actors))
        shared.set("rating", DoubleCodec.createShared(value.rating))
        shared.set("released", IntCodec.createShared(value.released))
        shared.set("genre", GenreCodec.createShared(value.genre))
        shared.set("watched", BooleanCodec.createShared(value.watched))
        return shared
    }

    override fun updateShared(shared: SharedMap, value: Movie) {
        StringCodec.updateShared(shared.getAsShared("title"), value.title)
        PersonCodec.updateShared(shared.getAsShared("director"), value.director)
        PersonListCodec.updateShared(shared.getAsShared("actors"), value.actors)
        DoubleCodec.updateShared(shared.getAsShared("rating"), value.rating)
        IntCodec.updateShared(shared.getAsShared("released"), value.released)
        GenreCodec.updateShared(shared.getAsShared("genre"), value.genre)
        BooleanCodec.updateShared(shared.getAsShared("watched"), value.watched)
    }

    override fun decodeShared(shared: SharedMap) = Movie(
        title = StringCodec.decodeShared(shared.getAsShared("title")),
        director = PersonCodec.decodeShared(shared.getAsShared("director")),
        actors = PersonListCodec.decodeShared(shared.getAsShared("actors")),
        rating = DoubleCodec.decodeShared(shared.getAsShared("rating")),
        released = IntCodec.decodeShared(shared.getAsShared("released")),
        genre = GenreCodec.decodeShared(shared.getAsShared("genre")),
        watched = BooleanCodec.decodeShared(shared.getAsShared("watched")),
    )
}