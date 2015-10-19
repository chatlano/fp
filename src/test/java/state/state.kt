package state

open class State<S, out A>(val run: (S) -> Pair<S, A>) {
  fun <B> map(f: (A) -> B): State<S, B> = State { s ->
    val (s1, a) = run(s)
    Pair(s1, f(a))
  }

  fun <B> flatMap(f: (A) -> State<S, B>): State<S, B> = State { s ->
    val (s1, a) = run(s)
    f(a).run(s1)
  }

  companion object {
    operator fun <S> get(): State<S, S> = State { s -> Pair(s, s) }
    fun <S, A> insert(a: A): State<S, A> = State { s -> Pair(s, a) }
    fun <S> put(f: (S) -> S): State<S, Unit> = State { s -> Pair(f(s), Unit) }
  }
}


