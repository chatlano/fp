import org.junit.Test
import kotlin.test.assertEquals

class StructuresTest {

  // In computer programming, particularly functional programming and type theory,
  // an algebraic data type is a kind of composite type,
  // i.e. a type formed by combining other types.

  sealed class List<out T> {      // List<? extends T>
    object Nil : List<Nothing>() {
      override fun toString() = "Nil"
    }

    // constructs memory objects
    class Cons<T>(val head: T, val tail: List<T>) : List<T>() {
      override fun toString() = "[${head} ${tail}]"
    }
  }

  sealed class TestSealed {
    class First  : TestSealed()
    class Second : TestSealed()
  }

  fun testSealed(t: TestSealed) = when(t) {
    is TestSealed.First  -> 0
    is TestSealed.Second -> 1
  }

  @Test fun toStringTest() {
    println(List.Cons(1, List.Nil))
    println(List.Cons(1, List.Cons(2, List.Cons(3, List.Cons(4, List.Nil)))))
  }


  fun listOf<T> (vararg xs: T): List<T> = when {
    xs.isEmpty()   -> List.Nil
    xs.size() == 1 -> List.Cons(xs.get(0), List.Nil)
    else -> {
      val tail = xs.copyOfRange(1, xs.size())
      List.Cons(xs.get(0), listOf(*tail))
    }
  }

  @Test fun listOfTest() {
    println(listOf<Int>())
    println(listOf(1, 2, 3, 4))
  }

  fun List<Int>.sum(): Int = when(this) {
    is List.Nil  -> 0
    is List.Cons -> this.head + tail.sum()
  }

  @Test fun sumTest() {
    val l  = List.Nil
    assertEquals(0, l.sum())

    val l1 = List.Cons(1, List.Nil)
    assertEquals(1, l1.sum())

    val l2 = List.Cons(1, List.Cons(2, List.Cons(3, List.Nil)))
    assertEquals(6, l2.sum())

    val l3 = listOf(1, 2, 3, 4, 5)
    assertEquals(15, l3.sum())
  }

  fun List<Double>.product(): Double = when(this) {
    is List.Nil  -> 1.0
    is List.Cons -> if (head == 0.0) 0.0
    else head * tail.product()
  }

  @Test fun productTest() {
    val l = listOf(1.0, 2.0, 3.0, 4.0, 5.0)
    println("product = ${l.product()}")
    assertEquals(120.0, l.product())
  }

  fun <T> List<T>.append(ys: List<T>): List<T> = when(this) {
    is List.Nil  -> ys
    is List.Cons -> List.Cons(head, tail.append(ys))
  }

  @Test fun appendTest() {
    val l1 = listOf(1, 2, 3)
    val l2 = listOf(3, 4, 5)
    println(l1.append(l2))
  }

  fun <A,B> List<A>.map(f: (A) -> B): List<B> = when(this) {
    is List.Nil  -> List.Nil
    is List.Cons -> List.Cons(f(head), tail.map(f))
  }

  @Test fun mapTest() {
    println(listOf(1, 2, 3, 4, 5).map { it * 2 })
  }

  fun <A> List<A>.filter(f: (A) -> Boolean): List<A> = when(this) {
    is List.Nil  -> List.Nil
    is List.Cons -> if (f(head)) List.Cons(head, tail.filter(f))
    else tail.filter(f)
  }

  @Test fun filterTest() {
    println(listOf(1, 2, 3, 4, 5).filter { it > 2 })
  }

  tailrec fun <A,B> List<A>.foldLeft(z: B, f: (A, B) -> B): B = when(this) {
    is List.Nil  -> z
    is List.Cons -> tail.foldLeft(f(head, z), f)
  }

  @Test fun sumAndProductTest() {
    assertEquals(15,  listOf(1, 2, 3, 4, 5).foldLeft(0) { x, y -> x + y })
    assertEquals(120, listOf(1, 2, 3, 4, 5).foldLeft(1) { x, y -> x * y })
    /**
     * listOf(1, 2, 3, 4, 5).foldLeft(0) { x, y -> x + y }
     *
     * listOf(1, 2, 3, 4, 5).foldLeft(0) { 1, 0 -> 1 + 0 }
     * listOf(2, 3, 4, 5).foldLeft(1) { 2, 1 -> 2 + 1 }
     * listOf(3, 4, 5).foldLeft(3) { 3, 3 -> 3 + 3 }
     * listOf(4, 5).foldLeft(6) { 4, 6 -> 4 + 6 }
     * listOf(5).foldLeft(10) { 5, 10 -> 5 + 10 }
     * Nil.foldLeft(15)
     * 15
     */
  }

  fun <A,B> List<A>.foldRight(z: B, f: (A, B) -> B): B = when(this) {
    is List.Nil  -> z
    is List.Cons -> f(head, tail.foldRight(z, f))
  }

  @Test fun sum2Test() {
    assertEquals(15,  listOf(1, 2, 3, 4, 5).foldRight(0) { x, y -> x + y })
    assertEquals(120, listOf(1, 2, 3, 4, 5).foldRight(1) { x, y -> x * y })
    /**
     * listOf(1, 2, 3, 4, 5).foldRight(0) { x, y -> x + y }
     *
     * listOf(1, 2, 3, 4, 5).foldRight(0) { 1, 0 -> 1 + (2, 3, 4, 5).foldRight(0) { x, y -> x + y }}
     *
     * { 1, 0 -> 1 + (2, 3, 4, 5).foldRight(0) { x, y -> x + y }}
     * { 1, 0 -> 1 + {2 + (3, 4, 5).foldRight(0) { x, y -> x + y }}}
     * { 1, 0 -> 1 + {2 + {3 + (4, 5).foldRight(0) { x, y -> x + y }}}}
     * { 1, 0 -> 1 + {2 + {3 + {4 + (5).foldRight(0) { x, y -> x + y }}}}}
     * { 1, 0 -> 1 + {2 + {3 + {4 + {5 + Nil.foldRight(0) { x, y -> x + y }}}}}}
     * { 1, 0 -> 1 + {2 + {3 + {4 + {5 + 0}}}}}
     * { 1, 0 -> 1 + {2 + {3 + {4 + 5}}}}
     * { 1, 0 -> 1 + {2 + {3 + 9}}}
     * { 1, 0 -> 1 + {2 + 12}}
     * { 1, 0 -> 1 + 14}
     * { 1, 0 -> 15}
     * 15
     */
  }

}
