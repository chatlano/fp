package state

import org.junit.Test
import java.util.*

class RandomizeTest {

  @Test fun javaWay() {
    val rnd = Random()
    println(rnd.nextDouble())
    println(rnd.nextDouble())
    println(rnd.nextInt())
  }

  interface RND {
    fun nextInt(): Pair<Int, RND>
  }

  fun simple(seed: Long): RND = object : RND {
    override fun nextInt(): Pair<Int, RND> {
      val seed2 = (seed * 0x5DEECE66DL + 0xBL) and ((1L shl 48) - 1)
      return Pair((seed2 ushr 16).toInt(), simple(seed2))
    }
  }

  @Test fun testSimple() {
    val rnd = simple(1L)
    println(rnd.nextInt())
    println(rnd.nextInt())
    val (next, rnd2) = rnd.nextInt()
    println(next)
    println(rnd2.nextInt())
  }

  fun randomPair(rnd: RND): Pair<Pair<Int, Int>, RND> {
    val (i1, rnd1) = rnd.nextInt()
    val (i2, rnd2) = rnd1.nextInt()
    return Pair(Pair(i1, i2), rnd2)
  }

  @Test fun randomPairTest() {
    val rnd = simple(1L)
    val (vals, rnd1) = randomPair(rnd);
    val (i1, i2) = vals

    println("${i1} ${i2}")
  }

  fun positiveInt(rnd: RND): Pair<Int, RND> {
    val (v, rnd2) = rnd.nextInt()
    return Pair(Math.abs(v), rnd2)
  }

  @Test fun positiveTest() {
    val rnd = simple(1L)
    println(positiveInt(rnd))
  }

  fun ints(count: Int): (rnd: RND) -> Pair<List<Int>, RND> {
    tailrec fun calc (r: RND, cnt: Int, res: List<Int>): Pair<List<Int>, RND> = when (cnt) {
      0 -> Pair(res, r)
      else -> {
        val (v, r2) = r.nextInt()
        calc(r2, cnt - 1, res + v)
      }
    }
    return { calc(it, count, emptyList()) }
  }

  @Test fun intsTest() {
    println(ints(10)(simple(1L)))
  }

  class Rand(f: (Long) -> Pair<Long, Int> = { seed ->
    val seed2 = (seed * 0x5DEECE66DL + 0xBL) and ((1L shl 48) - 1)
    Pair(seed2, (seed2 ushr 16).toInt())}) : State<Long, Int>(f) {
  }

  @Test fun testNewRand() {
    val rnd = Rand()

    println(rnd.run(1).second)

    val rnd1 = rnd.map { if(it != Int.MIN_VALUE) Math.abs(it) else Math.abs(it + 1) }
    println(Rand().run(rnd1.run(1).first))

    println(rnd.run(rnd.run(rnd.run(1).first).first))
  }


  fun State<Long, Int>.getNext(): State<Long, Int> = State.get<Long>().flatMap {
    val (newS, a) = run(it)
    Rand { Rand().run(newS) }
  }

  @Test fun testGetNext() {
    println(Rand().getNext().getNext().getNext().run(1))
  }

  fun intsState(count: Int): (rnd: State<Long, Int>) -> List<State<Long, Int>> {
    fun calc (r: State<Long, Int>, cnt: Int, res: List<State<Long, Int>>): List<State<Long, Int>> = when (cnt) {
      0 -> res
      else -> {
        val r2 = r.getNext()
        calc(r2, cnt - 1, res+r2)
      }
    }
    return { calc(it, count, emptyList()) }
  }

  @Test fun testIntsState() {
    val rnds = intsState(10)(Rand().getNext())
    println(rnds.map { it.run(1).second })
  }

}
