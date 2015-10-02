import org.junit.Test

class Ex1 {

  /** Implement the function tail for "removing" the first element
   *  of a List . Notice the function takes constant time.
   *  What are different choices you could make in your implementation if the List is Nil?
   */
  fun <T> List<T>.tail(): List<T> = List.Nil

  @Test fun testTail() {}

  /**
   * Generalize tail to the function drop, which removes
   * the first n elements from a list.
   */
  fun <T> List<T>.drop(n: Int): List<T> = List.Nil

  @Test fun testDrop() {}
  /**
   * Implement dropWhile, which removes elements from the
   * List prefix as long as they match a predicate.
   */
  fun <T> List<T>.dropWhile(f: (T) -> Boolean): List<T> = List.Nil

  @Test fun testDropWhile() {}
  /**
   * Implement the function setHead for replacing the first element
   * of a List with a different value.
   */
  fun <T> List<T>.setHead(head: T): List<T> = List.Nil

  @Test fun testSetHead() {}
  /**
   * Implement the function append to combine two Lists together
   */
  fun <T> List<T>.append(ys: List<T>): List<T> = List.Nil

  @Test fun testAppend() {}
  /**
   * Implement function length using foldLeft
   */
  fun <T> List<T>.length(): Int = 0

  @Test fun testLength() {}

  // ---------------- Help functions ---------------------------

  sealed class List<out T> {
    object Nil : List<Nothing>() {
      override fun toString() = "Nil"
    }

    // constructs memory objects
    class Cons<T>(val head: T, val tail: List<T>) : List<T>() {
      override fun toString() = "[${head} ${tail}]"
    }
  }

  fun listOf<T> (vararg xs: T): List<T> = when {
    xs.isEmpty() -> List.Nil
    xs.size() == 1 -> List.Cons(xs.get(0), List.Nil)
    else -> {
      val tail = xs.copyOfRange(1, xs.size())
      List.Cons(xs.get(0), listOf(*tail))
    }
  }

  tailrec fun <A, B> List<A>.foldLeft(z: B, f: (A, B) -> B): B = when (this) {
    is List.Nil -> z
    is List.Cons -> tail.foldLeft(f(head, z), f)
  }
}