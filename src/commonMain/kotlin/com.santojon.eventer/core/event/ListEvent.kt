package com.santojon.eventer.core.event

import kotlin.reflect.KClass

/**
 * Class used to deal with [ArrayList]s as [Event]s
 * It can validate the type even for empty lists, using [validType] inline function
 *
 * @param kClass: The [KClass] of [T] to validate for empty lists
 */
open class ListEvent<T : Any>(open val kClass: KClass<T>) : MutableList<T>, Event {
    /**
     * Internal [ArrayList] data list
     */
    private var list = arrayListOf<T>()

    /**
     * Validate type of [ListEvent]
     * Used for empty lists validation
     */
    inline fun <reified K : Any> validType() = K::class == kClass

    /**
     * Alternative constructor receiving [List] of [T]
     *
     * @param list: [List] of [T] to put into [ListEvent]
     * @param kClass: empty list validation type
     */
    constructor(list: List<T>?, kClass: KClass<T>) : this(kClass) {
        list?.let { addAll(it) }
    }

    /**
     * Alternative constructor receiving [List] of [T]
     *
     * @param elements: vararg of [T] to put into [ListEvent]
     * @param kClass: empty list validation type
     */
    constructor(kClass: KClass<T>, vararg elements: T?) : this(kClass) {
        elements.forEach { element -> element?.let { add(it) } }
    }

    /**
     * [MutableList] Overrides
     * Pointing [ArrayList] to internal data list
     */
    override val size: Int get() = list.size
    override fun contains(element: T) = list.contains(element)
    override fun containsAll(elements: Collection<T>) = list.containsAll(elements)
    override fun get(index: Int) = list[index]
    override fun indexOf(element: T) = list.indexOf(element)
    override fun isEmpty() = list.isEmpty()
    override fun iterator() = list.iterator()
    override fun lastIndexOf(element: T) = list.lastIndexOf(element)
    override fun add(element: T) = list.add(element)
    override fun add(index: Int, element: T) = list.add(index, element)
    override fun addAll(index: Int, elements: Collection<T>) = list.addAll(index, elements)
    override fun addAll(elements: Collection<T>) = list.addAll(elements)
    override fun clear() = list.clear()
    override fun listIterator() = list.listIterator()
    override fun listIterator(index: Int) = list.listIterator(index)
    override fun remove(element: T) = list.remove(element)
    override fun removeAll(elements: Collection<T>) = list.removeAll(elements)
    override fun removeAt(index: Int) = list.removeAt(index)
    override fun retainAll(elements: Collection<T>) = list.retainAll(elements)
    override fun set(index: Int, element: T) = list.set(index, element)
    override fun subList(fromIndex: Int, toIndex: Int) = list.subList(fromIndex, toIndex)

    /**
     * Override properties to use [ArrayList] ones
     * Necessary to deal with list validations
     */
    override fun toString() = list.toString()
    override fun equals(other: Any?) = list == other
    override fun hashCode() = list.hashCode()
}

/**
 * Extension functions to create [ListEvent]
 */
inline fun <reified T : Any> listEventOf(): ListEvent<T> = ListEvent(T::class)
inline fun <reified T : Any> listEventOf(list: List<T>): ListEvent<T> = ListEvent(list, T::class)
inline fun <reified T : Any> listEventOf(vararg args: T?): ListEvent<T> = ListEvent(T::class, *args)