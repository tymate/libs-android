package com.tymate.core

import java.util.*

/**
 * A container object which may or may not contain a non-null value.
 * If a value is present, `isPresent()` will return `true` and
 * `get()` will return the value.
 *
 *
 * Additional methods that depend on the presence or absence of a contained
 * value are provided, such as [orElse()][.orElse]
 * (return a default value if value not present) and
 * [ifPresent()][.ifPresent] (execute a block
 * of code if the value is present).
 *
 * @since 1.8
 */
class Optional<T> {

    /**
     * If non-null, the value; if null, indicates no value is present
     */
    private val value: T?

    /**
     * Return `true` if there is a value present, otherwise `false`.
     *
     * @return `true` if there is a value present, otherwise `false`
     */
    val isPresent: Boolean
        get() = value != null

    /**
     * Constructs an empty instance.
     *
     */
    private constructor() {
        this.value = null
    }

    /**
     * Constructs an instance with the value present.
     *
     * @param value the non-null value to be present
     * @throws NullPointerException if value is null
     */
    private constructor(value: T) {
        this.value = Objects.requireNonNull(value)
    }

    /**
     * If a value is present in this `Optional`, returns the value,
     * otherwise throws `NoSuchElementException`.
     *
     * @return the non-null value held by this `Optional`
     * @throws NoSuchElementException if there is no value present
     *
     * @see Optional.isPresent
     */
    fun get(): T {
        if (value == null) {
            throw NoSuchElementException("No value present")
        }
        return value
    }

    /**
     * returns the value,
     *
     * @return the nullable value held by this `Optional`
     *
     */
    fun getNullable(): T? {
        return value
    }

    /**
     * If a value is present, invoke the specified consumer with the value,
     * otherwise do nothing.
     *
     * @param consumer block to be executed if a value is present
     * @throws NullPointerException if value is present and `consumer` is
     * null
     */
    fun ifPresent(consumer: (T) -> Unit) {
        if (value != null) {
            consumer.invoke(value)
        }
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * return an `Optional` describing the value, otherwise return an
     * empty `Optional`.
     *
     * @param predicate a predicate to apply to the value, if present
     * @return an `Optional` describing the value of this `Optional`
     * if a value is present and the value matches the given predicate,
     * otherwise an empty `Optional`
     * @throws NullPointerException if the predicate is null
     */
    fun filter(predicate: (T?) -> Boolean): Optional<T> {
        Objects.requireNonNull(predicate)
        return if (!isPresent) this else if (predicate.invoke(value)) this else empty()
    }

    /**
     * Indicates whether some other object is "equal to" this Optional. The
     * other object is considered equal if:
     *
     *  * it is also an `Optional` and;
     *  * both instances have no value present or;
     *  * the present values are "equal to" each other via `equals()`.
     *
     *
     * @param other an object to be tested for equality
     * @return {code true} if the other object is "equal to" this object
     * otherwise `false`
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is Optional<*>) {
            return false
        }

        val optional = other as Optional<*>?
        return value == optional!!.value
    }

    /**
     * Returns the hash code value of the present value, if any, or 0 (zero) if
     * no value is present.
     *
     * @return hash code value of the present value or 0 if no value is present
     */
    override fun hashCode(): Int {
        return Objects.hashCode(value)
    }

    /**
     * Returns a non-empty string representation of this Optional suitable for
     * debugging. The exact presentation format is unspecified and may vary
     * between implementations and versions.
     *
     * @implSpec If a value is present the result must include its string
     * representation in the result. Empty and present Optionals must be
     * unambiguously differentiable.
     *
     * @return the string representation of this instance
     */
    override fun toString(): String {
        return if (value != null)
            String.format("Optional[%s]", value)
        else
            "Optional.empty"
    }

    companion object {

        /**
         * Returns an empty `Optional` instance.  No value is present for this
         * Optional.
         *
         * @apiNote Though it may be tempting to do so, avoid testing if an object
         * is empty by comparing with `==` against instances returned by
         * `Option.empty()`. There is no guarantee that it is a singleton.
         * Instead, use [.isPresent].
         *
         * @param <T> Type of the non-existent value
         * @return an empty `Optional`
        </T> */
        fun <T> empty(): Optional<T> {
            return Optional()
        }

        /**
         * Returns an `Optional` with the specified present non-null value.
         *
         * @param <T> the class of the value
         * @param value the value to be present, which must be non-null
         * @return an `Optional` with the value present
         * @throws NullPointerException if value is null
        </T> */
        fun <T> of(value: T): Optional<T> {
            return Optional(value)
        }

        /**
         * Returns an `Optional` describing the specified value, if non-null,
         * otherwise returns an empty `Optional`.
         *
         * @param <T> the class of the value
         * @param value the possibly-null value to describe
         * @return an `Optional` with a present value if the specified value
         * is non-null, otherwise an empty `Optional`
        </T> */
        fun <T> ofNullable(value: T?): Optional<T> {
            return if (value == null) empty() else of(
                value
            )
        }
    }

}

fun <T> T?.optional(): Optional<T> = Optional.ofNullable(this)