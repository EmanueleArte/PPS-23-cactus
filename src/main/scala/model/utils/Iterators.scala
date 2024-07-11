package model.utils

/** Utility classes for iterators. */
object Iterators:
  /**
   * Iterator that allows to peek the current element.
   *
   * @param iterator
   *   the iterator to wrap.
   * @tparam A
   *   the type of the elements in the iterator.
   */
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  class PeekableIterator[A](iterator: Iterator[A]) extends Iterator[A]:
    private var lookahead: Option[A] = None

    /** Returns the next element without advancing the iterator.
     * 
     * @return the current element.
     */
    def peek: Option[A] =
      if (lookahead.isEmpty)
        if (iterator.hasNext)
          lookahead = Some(iterator.next())
      lookahead

    override def hasNext: Boolean = lookahead.isDefined || iterator.hasNext

    override def next(): A =
      val nextElement = peek.fold(throw new NoSuchElementException("next on empty iterator"))(identity)
      lookahead = None
      nextElement
