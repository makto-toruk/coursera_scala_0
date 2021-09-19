# 6 Collections

## 6.1 Other Collections

- Vectors: have more evenly balanced access patterns than Lists (where first element is much faster to access than last).
- Vectors support similar functions:
    - `x +: xs` creates a new vector with leading element `x` followed by `xs`
    - `xs :+ x` creates a neew vector with trailing element `x`.
    - `:` always points to the sequence. These are analogous to `x :: xs` for lists.
- Arrays, Lists, and vectors fall under the `Seq` collection hierarchy which in turn falls under `Iterable`.

- Arrays and Strings:
    ```scala
    val xs: Array[Int] = Array(1, 2, 3)
    xs.map(x => 2 * x)

    val ys: String = "Hello world!"
    ys.filter(_.isUpper)
    ```

- Ranges:
    ```scala
    val r: Range = 1 until 5 // exclusive
    val s: Range = 1 to 5 // inclusive
    1 to 10 by 3 // step value
    6 to 1 by -2
    ```

- Sequence operations:
    - `xs.exists(p)`: true if there is an element x of xs such that p(x) holds, false otherwise.
    - `xs.forall(p)`: true if p(x) holds for all elements x of xs, false otherwise.
    - `xs.zip(ys)`: A sequence of pairs drawn from corresponding elements of sequences xs and ys.
    - `xs.unzip`: Splits a sequence of pairs xs into two sequences consisting of the first, respectively second halves of all pairs.
    - `xs.flatMap(f)`: Applies collection-valued function f to all elements of xs and concatenates the results
    - `xs.sum`: The sum of all elements of this numeric collection.
    - `xs.product`: The product of all elements of this numeric collection
    - `xs.max`: The maximum of all elements of this collection (an Ordering must exist)
    - `xs.min`: The minimum of all elements of this collection

- Example: Combinations: all combinations of numbers where x is from 1 to M and y is from 1 to N
    ```scala
    (1 to M).flatMap(x => (1 to N).map(y => (x, y)))
    ```

- Example: Scalar Product
    ```scala
    def scalarProduct(xs: Vector[Double], ys: Vector[Double]): Double =
        xs.zip(ys).map((x, y) => x * y).sum
    ```
    or
    ```scala
    def scalarProduct(xs: Vector[Double], ys: Vector[Double]): Double =
        xs.zip(ys).map(_ * _).sum
    ```

- Example: is Prime
    ```scala
    def isPrime(n: Int): Boolean =
        (2 to n - 1).forall(d => n % d == 0)    
    ```

## 6.2 Combinatorial Search and For-Expressions

- Nested sequences:
    ```scala
    ((1 until n).map(i =>
        (1 until i).map(j => (i, j)))).flatten
    ```
    or
    ```scala
    (1 until n).flatMap(i =>
        (1 until i).map(j => (i, j))) // xs.flatMap(f) = xs.map(f).flatten
    ```

- Can we make this more readable?
    ```scala
    (1 until n)
        .flatMap(i => (1 until i).map(j => (i, j)))
        .filter((x, y) => isPrime(x + y))
    ```
    to
    ```scala
    for
        i <- 1 until n
        j <- 1 until i
        if isPrime(i + j)
    yield (i, j)
    ```

- Revisiting scalar product:
    ```scala
    def scalarProduct(xs: List[Double], ys: List[Double]) : Double =
        (for (x, y) <- xs.zip(ys) yield x * y).sum
    ```

## 6.3 Combinatorial Search Example

- Sets: 
    1. Unordered,
    2. Do not have duplicates
    3. Fundamental operation: contains (e.g. `s.contains(5)`)

- Example: N-Queens
    - The eight queens problem is to place eight queens on a chessboard so that no queen is threatened by another
    - To yield all solutions,
    ```scala
    def queens(n: Int) =
        def placeQueens(k: Int): Set[List[Int]] =
            if k == 0 then Set(List())
            else
                for
                    queens <- placeQueens(k - 1) // if k - 1 queens are already set, how do you set the kth queen?
                    col <- 0 until n
                    if isSafe(col, queens)
                yield col :: queens // each solution is a List[Int]
        placeQueens(n)

    def isSafe(col: Int, queens: List[Int]): Boolean =
        !checks(col, 1, queens)

    def checks(col: Int, delta: Int, queens: List[Int]): Boolean = queens match
        case qcol :: others =>
            qcol == col // vertical check
            || (qcol - col).abs == delta // diagonal check
            || checks(col, delta + 1, others)
        case Nil =>
            false
    ```

## 6.4 Maps

- Maps: associates a key to a value
    ```scala
    val romanNumerals = Map("I" -> 1, "V" -> 5, "X" -> 10)
    val capitalOfCountry = Map("US" -> "Washington", "Switzerland" -> "Bern")
    ```
- Maps support operations that other Iterables do:
    ```scala
    val countryOfCapital = capitalOfCountry.map((x, y) => (y, x))
    ```
- Maps can be "applied" like functions to key arguments: `capitalOfCountry("US")`
    - Can result in a `NoSuchElementException if key is not found`
    - To query without knowing beforehand whether it has they key, use `get`
        ```scala
        capitalOfCountry.get("US") // Some("Washington")
        capitalOfCountry.get("Andorra") // None
        ```
- `Option` Type: map.get(key) returns
    - `None` if map doesn't contain key
    - `Some(x)` if map associates key with value `x`.
    - `Option` type handles this

- Decomposing Option
    ```scala
    def showCapital(country: String) = capitalOfCountry.get(country) match
        case Some(capital) => capital
        case None => "missing data"
    ```

- Updating maps:
    - `m + (k -> v)`: The map that takes key `k` to value `v` and is otherwise equal to `m`
    - `m ++ kvs`: The map updated via `+` with all pairs in `kvs`
    - Examples:
    ```scala
    val m1 = Map("red" -> 1, "blue" -> 2) // m1 = Map(red -> 1, blue -> 2)
    val m2 = m1 + ("blue" -> 3) // m2 = Map(red -> 1, blue -> 3)
    m1 // Map(red -> 1, blue -> 2)
    ```

- Sorted
    ```scala
    val fruit = List("apple", "pear", "orange", "pineapple")
    fruit.sortWith(_.length < _.length) // List("pear", "apple", "orange", "pineapple")
    fruit.sorted // List("apple", "orange", "pear", "pineapple") lexicographic
    ```
- Groupby
    ```scala
    fruit.groupBy(_.head) //> Map(p -> List(pear, pineapple),
                        //| a -> List(apple),
                        //| o -> List(orange))
    ```

- Default Values
    ```scala
    val cap1 = capitalOfCountry.withDefaultValue("<unknown>")
    cap1("Andorra") // "<unknown>"
    ```

- Map example: polynomial
    - $x^3 - 2x + 5$ can be reperesentated as `Map(0 -> 5, 1 -> -2, 3 -> 1)`

- Suppose we have a class `Polynom`:
    - inconvenient: `Polynom(Map(1 -> 2.0, 3 -> 4.0, 5 -> 6.2))`
    - to handle variable length argument lists"
        ```scala
        def Polynom(bindings: (Int, Double)*) =
            Polynom(bindings.toMap.withDefaultValue(0))
        ```

- Example implementation of `Polynom`:
    ```scala
    class Polynom(nonZeroTerms: Map[Int, Double]):
        def this(bindings: (Int, Double)*) = this(bindings.toMap)

        def terms = nonZeroTerms.withDefaultValue(0.0)
        def + (other: Polynom) =
            Polynom(terms ++ other.terms.map((exp, coeff) => (exp, terms(exp) + coeff)))

        override def toString =
            val termStrings =
                for (exp, coeff) <- terms.toList.sorted.reverse
                yield
                    val exponent = if exp == 0 then "" else s"x^$exp"
                    s"$coeff$exponent"
            if terms.isEmpty then "0" else termStrings.mkString(" + ")
    ```

- foldLeft implementation of `+`
    ```scala
    def + (other: Polynom) =
        Polynom(other.terms.foldLeft(terms)(addTerm))

    def addTerm(terms: Map[Int, Double], term: (Int, Double)) =
        val (exp, coeff) = term
        terms + (exp, coeff + terms(exp))
    ```

## 6.5 Putting the Pieces Together

- Design a method `encode` such that `encode(phoneNumber)` produces all pharses of words that can sere as mnemonics for the phone number.
    ```scala
    val mnemonics = Map(
        '2' -> "ABC", '3' -> "DEF", '4' -> "GHI", '5' -> "JKL",
        '6' -> "MNO", '7' -> "PQRS", '8' -> "TUV", '9' -> "WXYZ")
    ```

- Outline:
```scala
class Coder(words: List[String]):
    val mnemonics = Map(...)
    
    /** Maps a letter to the digit it represents */
    private val charCode: Map[Char, Char] = 
        for (digit, str) <- mnemonics; ltr <- str yield ltr -> digit

    /** Maps a word to the digit string it can represent */
    private def wordCode(word: String): String = 
        word.toUpperCase.map(charCode)

    /** Maps a digit string to all words in the dictionary that represent it */
    private val wordsForNum: Map[String, List[String]] = 
        words.groupBy(wordCode).withDefaultValue(Nil) // words is the dictionary

    /** All ways to encode a number as a list of words */
    def encode(number: String): Set[List[String]] = 
        if number.isEmpty then Set(Nil)
        else
            for
                splitPoint <- (1 to number.length).toSet
                word <- wordsForNum(number.take(splitPoint))
                rest <- encode(number.drop(splitPoint))
            yield word :: rest
        ```