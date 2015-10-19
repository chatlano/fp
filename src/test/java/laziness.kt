import org.junit.Test
import kotlin.test.assertEquals

class Laziness {

  fun <A> if2(cond: Boolean, onTrue: A, onFalse: A): A = if(cond) onTrue else onFalse

  @Test fun testIf() {
    (1..2000).asSequence()

    assertEquals(3, if2(false, println("true"), 3))
  }
}