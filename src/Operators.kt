fun <T, U> ((T) -> U).mp(optional: Optional<T>): Optional<U> {
    return optional.map(this)
}

fun <T, U> Optional<(T) -> U>.ap(optional: Optional<T>): Optional<U> {
    return optional.apply(this)
}