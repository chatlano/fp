package state

import org.junit.Test
import java.util.*

  data class Memo(val v: Map<Int, Int>) {
      operator fun get(key: Int): Optional<Int> = if(v.get(key) == null) Optional.empty() else Optional.of(v.get(key) as Int)
      operator fun plus(p: Pair<Int, Int>) = Memo(v + p)
  }

  fun fibmemo1(n: Int): Int {
    fun fibmemoR(z: Int, memo: Memo): Pair<Int, Memo> {
      if (z <= 1) return Pair(1, memo)
      else when (memo.v.get(z)) {
        null -> {
          val (s, memo0) = fibmemoR(z - 2, memo)
          val (r, memo1) = fibmemoR(z - 1, memo0)
          return Pair(r + s, Memo(memo1.v + Pair(z, r+s)))
        }
        else -> return Pair(memo.v.get(z) as Int, memo)
      }
    }
    return fibmemoR(n, Memo(emptyMap())).first
  }

  class fib1Test {
    @Test fun fibmemo1Test() {
      println(fibmemo1(6))
    }
  }

  fun fibmemo3(n: Int): Int {
    fun fibmemoR(z: Int): State<Memo, Int> = when {
      z <= 1 -> State.insert(1)
      else   -> {
        State.get<Memo>().flatMap {
          if(it.get(z).isPresent) {
            State.insert<Memo, Int>(it.get(z).get())
          } else {
            fibmemoR(z - 2).flatMap { r ->
              fibmemoR(z - 1).flatMap { p ->
                val t = r + p
                (State.put<Memo> {
                  it + Pair(z, t)
                }).map {t}
              }
            }
          }
        }
      }
    }

    val s = fibmemoR(n)
    println(s)

    return s.run(Memo(emptyMap())).second

    }

  class fib2Test {
    @Test fun fibmemo1Test() {
      println(fibmemo3(7))
    }
  }
