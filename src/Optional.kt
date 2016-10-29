sealed class Optional<out T>: Collection<T>, Set<T>, List<T> {
    abstract val value: T

    abstract fun <U> map(transform: (T) -> U): Optional<U>
    abstract fun <U> flatMap(transform: (T) -> Optional<U>): Optional<U>
    abstract fun <U> apply(transform: Optional<(T) -> U>): Optional<U>

    class None<T>(): Optional<T>() {
        override val value: T
            get() = throw NullPointerException()

        override fun <U> map(transform: (T) -> U): Optional<U> = None()
        override fun <U> flatMap(transform: (T) -> Optional<U>): Optional<U> = None()
        override fun <U> apply(transform: Optional<(T) -> U>): Optional<U> = None()

        override val size: Int
            get() = 0
        override fun contains(element: T): Boolean = false
        override fun containsAll(elements: Collection<T>): Boolean = elements.isEmpty()
        override fun isEmpty(): Boolean = true
        override fun iterator(): Iterator<T> = emptySet<T>().iterator()

        override fun get(index: Int): T = throw IndexOutOfBoundsException("Index $index is out of bounds of None.")
        override fun indexOf(element: T): Int = -1
        override fun lastIndexOf(element: T): Int = -1
        override fun listIterator(): ListIterator<T> = emptyList<T>().listIterator()
        override fun listIterator(index: Int): ListIterator<T> = emptyList<T>().listIterator(index)
        override fun subList(fromIndex: Int, toIndex: Int): List<T> = emptyList<T>().subList(fromIndex, toIndex)
    }

    class Some<T>(override val value: T): Optional<T>() {
        override fun <U> map(transform: (T) -> U): Optional<U> = Some(transform(value))
        override fun <U> flatMap(transform: (T) -> Optional<U>): Optional<U> = transform(value)
        override fun <U> apply(transform: Optional<(T) -> U>): Optional<U> = transform.map { it(value) }

        override val size: Int
            get() = 1
        override fun contains(element: T): Boolean = value == element
        override fun containsAll(elements: Collection<T>): Boolean = elements.fold(true) { r, e -> r && e == value}
        override fun isEmpty(): Boolean = false
        override fun iterator(): Iterator<T> = listOf<T>(value).iterator()

        override fun get(index: Int): T = when(index) {
            0 -> value
            else -> throw IndexOutOfBoundsException("Index $index is out of bounds of Some.")
        }
        override fun indexOf(element: T): Int = if(element == value) 0 else -1
        override fun lastIndexOf(element: T): Int = if(element == value) 0 else -1
        override fun listIterator(): ListIterator<T> = listOf(value).listIterator()
        override fun listIterator(index: Int): ListIterator<T> = listOf(value).listIterator(index)
        override fun subList(fromIndex: Int, toIndex: Int): List<T> = listOf(value).subList(fromIndex, toIndex)
    }
}

fun <T> Optional<Optional<T>>.flatten(): Optional<T> {
    return flatMap { it }
}

fun <T> Optional<T>.orElse(value: T): T {
    return when (this) {
        is Optional.None -> value
        is Optional.Some -> this.value
    }
}

fun <T> Optional<T>.orElse(value: Optional<T>): Optional<T> {
    return when (this) {
        is Optional.None -> value
        is Optional.Some -> this
    }
}
