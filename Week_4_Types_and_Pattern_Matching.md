# 4 Types and Pattern Matching

## 4.1 Decomposition

- How would we decompose an Expression?
    ```scala
    trait Expr:
        def isNumber: Boolean
        def isSum: Boolean
        def numValue: Int
        def leftOp: Expr
        def rightOp: Expr
    
    class Number(n: Int) extends Expr:
        def isNumber = true
        def isSum = false
        def numValue = n
        def leftOp = throw Error("Number.leftOp")
        def rightOp = throw Error("Number.rightOp")
    
    class Sum(e1: Expr, e2: Expr) extends Expr:
        def isNumber = false
        def isSum = true
        def numValue = throw Error("Sum.numValue")
        def leftOp = e1
        def rightOp = e2

    def eval(e: Expr): Int =
        if e.isNumber then e.numValue
        else if e.isSum then eval(e.leftOp) + eval(e.rightOp)
        else throw Error("Unknown expression " + e)
    ```

- Object Oriented Decomposition
    - Decomposition mixes "data" with "operations" on the data. Can be the right thing if we need encapsulation and data abstraction.
    - But increases complexity and adds new dependencies to classes; hard to add new kinds of operations.

## 4.2 Pattern Matching

- Case Classes:
    - Similar to a normal class definition, except that it is preceded by the modifier `case`
        ```scala
        trait Expr
            case class Number(n: Int) extends Expr
            case class Sum(e1: Expr, e2: Expr) extends Expr
        ```

- Pattern matching is a generalization of `switch` to class hierarchies. It's expressed in Scala using `match`
    ```scala
    def eval(e: Expr): Int = e match
        case Number(n) => n
        case Sum(e1, e2) => eval(e1) + eval(e2)
    ```
    - `match`: preceded by a selector expression and followed by a sequence of `cases`, `pat => expr`
    - Each `case` associates an expression `expr` with a pattern `pat`
    - `MatchError` exception if no match.

- Patterns are constructed from:
    - constructors, e.g. Number, Sum,
    - variables, e.g. n, e1, e2,
    - wildcard patterns _,
    - constants, e.g. 1, true.
    - type tests, e.g. n: Number
- Variables always begin with a lowercase letter.
- The same variable name can only appear once in a pattern. So, `Sum(x, x)` is not a legal pattern.
- Names of constants begin with a capital letter, with the exception of the reserved words `null`, `true`, `false`.

## 4.3 Lists

```scala
val fruit = List("apples", "oranges", "pears")
val nums = List(1, 2, 3, 4)
val diag3 = List(List(1, 0, 0), List(0, 1, 0), List(0, 0, 1))
val empty = List()
```
- Lists are immutable
- Lists are recursive, arrays are flat
- Lists are homogeneous: all elements must have same type
    ```scala
    val fruit: List[String] = List("apples", "oranges", "pears")
    val nums : List[Int] = List(1, 2, 3, 4)
    val diag3: List[List[Int]] = List(List(1, 0, 0), List(0, 1, 0), List(0, 0, 1))
    val empty: List[Nothing] = List()
    ```
- All lists are constructed from the empty list `Nil` and construction operation `::` (Cons).
    ```scala
    val nums = 1 :: 2 :: 3 :: 4 :: Nil
    ```
- Fundamental operations: `head`, `tail`, and `isEmpty`.

- Pattern mathing on lists
    - `p :: ps` matches a list with a head matching `p` and a tail matching `ps`
    - `List(p1, ..., pn)` same as `p1 :: ... :: pn :: Nil`
    - Examples:
        - `1 :: 2 :: xs` Lists that start with 1 and then 2
        - `x :: Nil` Lists of length 1
        - `List(x)` Same as `x :: Nil`
        - `List()` empty list
        - `List(2 :: xs)` list that contains one element which is a list that starts with 2

- Insertion Sort:
```scala
def isort(xs: List[Int]): List[Int] = xs match
    case List() => List()
    case y :: ys => insert(y, isort(ys))

def insert(x: Int, xs: List[Int]): List[Int] = xs match
    case List() => List(x)
    case y :: ys => 
        if x < y then x :: xs else y :: insert(x, ys)
```

## 4.4 Enums
- To compose pure data without any associated functions.
```scala
trait Expr
object Expr:
    case class Var(s: String) extends Expr
    case class Number(n: Int) extends Expr
    case class Sum(e1: Expr, e2: Expr) extends Expr
    case class Prod(e1: Expr, e2: Expr) extends Expr
```
- Pure data definitions like these are called algebraic data types or ADTs.
- An alternative is to use `enum`
    ```scala
    enum Expr:
        case Var(s: String)
        case Number(n: Int)
        case Sum(e1: Expr, e2: Expr)
        case Prod(e1: Expr, e2: Expr)
    ```
    equivalent to above but shorter.
- Match expressions can still be used. 
    ```scala
    def show(e: Expr): String = e match
        case Expr.Var(x) => x
        case Expr.Number(n) => n.toString
        case Expr.Sum(a, b) => s"${show(a)} + ${show(a)}}"
        case Expr.Prod(a, b) => s"${showP(a)} * ${showP(a)}"
    
    def showP(e: Expr): String = e match
        case e: Sum => s"(${show(expr)})"
        case _ => show(expr)
    ```
- Can be even simpler
    ```scala
    enum DayOfWeek:
        case Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
    
    import DayOfWeek.*
    
    def isWeekend(day: DayOfWeek) = day match
        case Saturday | Sunday => true
        case _ => false
    ```
- Enums can take parameters and define methods
    ```scala
    enum Direction(val dx: Int, val dy: Int):
        case Right extends Direction( 1, 0)
        case Up extends Direction( 0, 1)
        case Left extends Direction(-1, 0)
        case Down extends Direction( 0, -1)

        def leftTurn = Direction.values((ordinal + 1) % 4)
    end Direction

    val r = Direction.Right
    val u = x.leftTurn // u = Up
    val v = (u.dx, u.dy) // v = (1, 0)
    ```
    - ordinal (order number in the case statements), values are auxiliary functions
    - Enumeration cases that pass parameters have to use an explicit extends clause
    - The expression `e.ordinal` gives the ordinal value of the enum case `e`. Cases start with zero and are numbered consecutively
    - `values` is an immutable array in the companion object of an enum that contains all enum values

- An enum for modeling payment methods:
    ```scala
    enum PaymentMethod:
        case CreditCard(kind: Card, holder: String, number: Long, expires: Date)
        case PayPal(email: String)
        case Cash

    enum Card:
        case Visa, Mastercard, Amex
    ```
    - Enums are
        - a shorthand for hierarchies of case classes
        - a way to define data types accepting alternative values
    - An enum can comprise parametrized and simple cases (see `PaymentMethod`)
    - Typically used for pure data, all operations on data are defined elsewhere.
    
## 4.5 Subtyping and Generics

- Two principal forms of polymorphism:
    - Subtyping
    - Generics.
- Consider a method `assertAllPos` that
    - takes an `IntSet`
    - returns the `IntSet` if all elements are positive
    - throws an exception otherwise
- What should be its type? `def assertAllPos(s: IntSet): IntSet`?
    - Can we be more precise?
    - For example one might want to express that this methods takes Empty sets to Empty and NonEmpty sets to NonEmpty.
    ```scala
    def assertAllPos[S <: IntSet](r: S): S = ...
    ```
    - Here "<: Intset" is an _upper bound_ of the type parameter S. It means that S can be instantiated only to types that conform to IntSet.
    - S <: T means: S is a _subtype_ of T
    - S >: T means: S is a _supertype_ of T, or T is a _subtype_ of S.
    - This makes it clear "what kind" of IntSet will be returned.
    - Supertypes form a lower bound.
- Mixed bounds: `[S >: NonEmpty <:IntSet]`
- Covariance:
    - Does `NonEmpty <: IntSet` imply `List[NonEmpty] <: List[IntSet]`.
    - If this is true, we say the relationship holds _covariant_.
    - Does this make sense for all types, and not just for `List`?
- Liskov Substitution Principle: When a type can be a subtype of another:
    - Let $q(x)$ be a property provable about objects $x$ of type $B$. Then $q(y)$ should be provable for objects $y$ of type $A$ where $A <: B$.
- Which line will lead to a type error?
    ```scala
    val a: Array[NonEmpty] = Array(NonEmpty(1, Empty(), Empty()))
    val b: Array[IntSet] = a
    b(0) = Empty()
    val s: NonEmpty = a(0)
    ```
    - Line 2! Because Arrays are not covariant and so an Array of nonempty cannot be assigned to an array of intset.

## 4.6 Variance
- Skipped