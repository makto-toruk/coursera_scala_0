# 5 Lists

## 5.1 A Closer Look At Lists
- Construction:
    ```scala
    val fruits = list("Apple", "Orange", "Banana")
    val nums = 1 :: 2 :: Nil
    ```
- Decomposition:
    ```scala
    fruits.head // "Apple"
    nums.tail // 2 :: Nil
    nums.isEmpty // false
    ```
- Pattern match:
    ```scala
    nums match
        case x :: y :: _ => x + y
    ```
- List methods:
    - xs.length: The number of elements of xs.
    - xs.last: The list’s last element, exception if xs is empty.
    - xs.init: A list consisting of all elements of xs except the last one, exception if xs is empty
    - xs.take(n): A list consisting of the first n elements of xs, or xs itself if it is shorter than n
    - xs.drop(n): The rest of the collection after taking n elements
    - xs(n): The element of xs at index n.
- Creating new lists:
    - xs ++ ys: The list consisting of all elements of xs followed by all elements of ys.
    - xs.reverse: The list containing the elements of xs in reversed order.
    - xs.updated(n, x): The list containing the same elements as xs, except at index n where it contains x.
- Finding elements:
    - xs.indexOf(x): The index of the first element in xs equal to x, or -1 if x does not appear in xs
    - xs.contains(x): same as xs.indexOf(x) >= 0
- Complexity:
    - head: O(1)
    - last: O(n)
- Example implementation of concat
    ```scala
    extension [T](xs: List[T])
        def ++ (ys: List[T]): List[T] = xs match
            case Nil => ys
            case x :: xs1 => x :: (xs1 ++ ys)
    ```

## 5.2 Tuples and Generic Methods
- Insertion sort:
    ```scala
    def msort(xs: List[Int]): List[Int] =
        val n = xs.length / 2
        if n == 0 then xs
        else
            def merge(xs: List[Int], ys: List[Int]) = (xs, ys) match
                case (Nil, ys) => ys
                case (xs, Nil) => xs
                case (x :: xs1, y :: ys1) =>
                    if x < y then x :: merge(xs1, ys)
                    else y :: merge(xs, ys1)
            val (fst, snd) = xs.splitAt(n)
            merge(msort(fst), msort(snd))
    ```
    - Pattern matching can be done on generic tuples
- Tuple construction:
    ```scala
    val pair = ("answer", 42)
    val (label, value) = pair // preferred
    val label = pair._1 // not preferred
    val value = pair._2
    ```

## 5.3 Higher-Order List Functions

- Apply function to elements of list
    ```scala
    def scaleList(xs: List[Double], factor: Double): List[Double] = xs match
        case Nil => Nil
        case y :: ys => y * factor :: scaleList(ys, factor)
    ```
- `map` does just this
    ```scala
    def scaleList(xs: List[Double], factor: Double) = 
        xs.map(x => x * factor)
    def squareList(xs: List[Int]): List[Int] =
        xs.map(x => x * x)
    ```
- Also common: select elements satisfying a given condition:
    ```scala
    def posElems(xs: List[Int]): List[Int]: xs match
        case Nil => xs
        case y :: ys => if y > 0 then y :: posElems(ys) else posElems(ys)
    ```
- `filter` does just this:
    ```scala
    def posElems(xs: List[Int]): List[Int] =
        xs.filter(x => x > 0)
    ```
- Variations of Filter
    - xs.filterNot(p): The list consisting of those elements of xs that do not satisfy the predicate p.
    - xs.partition(p): Same as `(xs.filter(p), xs.filterNot(p))`, but computed in a single traversal of the list xs
    - xs.takeWhile(p): The longest prefix of list xs consisting of elements that all satisfy the predicate p
    - xs.dropWhile(p): The remainder of the list xs after any leading elements satisfying p have been removed
    - xs.span(p): Same as (xs.takeWhile(p), xs.dropWhile(p)) but computed in a single traversal of the list xs
- Implement `pack(List("a", "a", "a", "b", "c", "c", "a"))` that gives `List(List("a", "a", "a"), List("b"), List("c", "c"), List("a"))`
    ```scala
    def pack[T](xs: List[T]): List[List[T]] = xs match
        case Nil => Nil
        case x :: xs1 => 
            val (more, rest) = xs1.span(y => y == x)
            (x :: more) :: pack(rest)
    ```
- Implement `encode(List("a", "a", "a", "b", "c", "c", "a"))` that gives `List((”a”, 3), (”b”, 1), (”c”, 2), (”a”, 1))`
    ```scala
    def encode[T](xs: List[T]): List[(T, Int)] = 
        pack(xs).map(x => (x.head, x.length))
    ```

## 5.4 Reduction of Lists
- Combine elements with an operator
    ```scala
    def sum(xs: List[Int]): Int = xs match
        case Nil => 0
        case y :: ys => y + sum(ys)
    ```
- An abstraction of the above is `reduceLeft`
    ```scala
    List(x1, ..., xn).reduceLeft(op) = x1.op(x2). ... .op(xn)
    ```
- Examples:
    ```scala
    def sum(xs: List[Int]) = (0 :: xs).reduceLeft((x, y) => x + y)
    def product(xs: List[Int]) = (1 :: xs).reduceLeft((x, y) => x * y)
    ```
- Shorter alternatives
    ```scala
    def sum(xs: List[Int]) = (0 :: xs).reduceLeft(_ + _)
    def product(xs: List[Int]) = (1 :: xs).reduceLeft(_ * _)
    ```
- A more general version of `reduceLeft` is `foldLeft` which also takes an accumulator `z`
    ```scala
    List(x1, ..., xn).foldLeft(z)(op) = z.op(x1).op ... .op(xn)
    ```
- Implementations:
    ```scala
    abstract class List[T]:
        
        def reduceLeft(op: (T, T) => T): T = this match
            case Nil => throw IllegalOperationException(”Nil.reduceLeft”)
            case x :: xs => xs.foldLeft(x)(op)
        
        def foldLeft[U](z: U)(op: (U, T) => U): U = this match
            case Nil => z
            case x :: xs => xs.foldLeft(op(z, x))(op)
    ```
- `foldRight` and `reduceRight` also exist:
    ```scala
    List(x1, ..., x{n-1}, xn).reduceRight(op) = x1.op(x2.op( ... x{n-1}.op(xn) ... ))
    List(x1, ..., xn).foldRight(z)(op ) = x1.op(x2.op( ... xn.op(z) ...))
    ```
- Better implementation of `reverse`:
    ```scala
    def reverse[T](xs: List[T]): List[T] = xs.foldLeft(z?)(op?)
    ```
- Deduction of `z` and `op`:
    ```scala
    Nil
    = reverse(Nil)
    = Nil.foldLeft(z?)(op)
    = z?
    ```
    - Thus `z` is Nil
    ```scala
    List(x)
    = reverse(List(x))
    = List(x).foldLeft(Nil)(op?)
    = op?(Nil, x)
    ```
    which suggests `op` is a cons operator with reversed operands.
- Thus,
    ```scala
    def reverse[a](xs: List[T]): List[T] =
        xs.foldLeft(List[T]())((xs, x) => x :: xs)
    ```
    Type parameter for `z`, `List[T]()`, is necessary for type inference
- Some examples with `foldRight`
    ```scala
    def mapFun[T, U](xs: List[T], f: T => U): List[U] =
        xs.foldRight(List[U]())((y, ys) => f(y) :: ys)

    def lengthFun[T](xs: List[T]): Int =
        xs.foldRight(0)((y, n) => n + 1)
    ```
    - Notice the swap in the operands (y, ys) instead of (ys, y) compared to `foldLeft`. 

## 5.5 Reasoning about lists
- Learned about structural induction
