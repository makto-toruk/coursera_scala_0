package funsets

/**
 * This class is a test suite for the methods in object FunSets.
 *
 * To run this test suite, start "sbt" then run the "test" command.
 */
class FunSetSuite extends munit.FunSuite:

  import FunSets.*

  test("contains is implemented") {
    assert(contains(x => true, 100))
  }

  /**
   * When writing tests, one would often like to re-use certain values for multiple
   * tests. For instance, we would like to create an Int-set and have multiple test
   * about it.
   *
   * Instead of copy-pasting the code for creating the set into every test, we can
   * store it in the test class using a val:
   *
   *   val s1 = singletonSet(1)
   *
   * However, what happens if the method "singletonSet" has a bug and crashes? Then
   * the test methods are not even executed, because creating an instance of the
   * test class fails!
   *
   * Therefore, we put the shared values into a separate trait (traits are like
   * abstract classes), and create an instance inside each test method.
   *
   */

  trait TestSets:
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = singletonSet(3)

  /**
   * This test is currently disabled (by using @Ignore) because the method
   * "singletonSet" is not yet implemented and the test would fail.
   *
   * Once you finish your implementation of "singletonSet", remove the
   * .ignore annotation.
   */
  test("singleton set one contains one") {

    /**
     * We create a new instance of the "TestSets" trait, this gives us access
     * to the values "s1" to "s3".
     */
    new TestSets:
      /**
       * The string argument of "assert" is a message that is printed in case
       * the test fails. This helps identifying which assertion failed.
       */
      assert(contains(s1, 1), "Singleton")
  }

  test("union contains all elements of each set") {
    new TestSets:
      val s = union(s1, s2)
      assert(contains(s, 1), "Union 1")
      assert(contains(s, 2), "Union 2")
      assert(!contains(s, 3), "Union 3")
  }

  test("intersection contains elements in both sets") {
    new TestSets:
      val sA = union(s1, s2) // (1, 2)
      val sB = union(s1, s3) // (1, 3)
      val s = intersect(sA, sB)
      assert(contains(s, 1), "Intersection 1")
      assert(!contains(s, 2), "Intersection 2")
      assert(!contains(s, 3), "Intersection 3")
  }

  test("difference contains element in set 1 that aren't in set 2") {
    new TestSets:
      val sA = union(s1, s2) // (1, 2)
      val sB = union(s1, s3) // (1, 3)
      val s = diff(sA, sB)
      assert(!contains(s, 1), "Difference 1")
      assert(contains(s, 2), "Difference 2")
      assert(!contains(s, 3), "Difference 3")
  }

  test("Filtering ensures predicate is obeyed") {
    new TestSets:
      val sU = union(union(s1, s2), s3) // (1, 2, 3)
      val s = filter(sU, x => (x <= 2))
      assert(contains(s, 1), "Filter 1")
      assert(contains(s, 2), "Filter 2")
      assert(!contains(s, 3), "Filter 3")
  }

  test("forall function tests whether a given predicate is true for all elements of the set") {
    new TestSets:
      val s = union(union(s1, s2), s3) // (1, 2, 3)
      def p1 = (x: Int) => (x <= 3)
      def p2 = (x: Int) => (x <= 2)
      def p3 = (x: Int) => (x > 4)
      assert(forall(s, p1), "For all 1")
      assert(!forall(s, p2), "For all 2")
      assert(!forall(s, p3), "For all 3")
  }

  test("exists function tests whether a given predicate is true for at least one element in the set") {
    new TestSets:
      val s = union(union(s1, s2), s3) // (1, 2, 3)
      def p1 = (x: Int) => (x <= 3)
      def p2 = (x: Int) => (x <= 2)
      def p3 = (x: Int) => (x > 4)
      assert(exists(s, p1), "Exists 1")
      assert(exists(s, p2), "Exists 2")
      assert(!exists(s, p3), "Exists 3")
  }

  test("contains works for map") {
    new TestSets:
      val s = union(union(s1, s2), s3) // (1, 2, 3)
      def f1 = (x: Int) => x * x
      def f2 = (x: Int) => x * 1000
      assert(contains(map(s, f1), 9), "Map 1")
      assert(!contains(map(s, f1), 8), "Map 2")
      assert(contains(map(s, f2), 1000), "Map 3")
  }



  import scala.concurrent.duration.*
  override val munitTimeout = 10.seconds
