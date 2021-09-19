# 1 Introduction

## 1.2 Elements of Programming
- What's a REPL? 
    - Read-Eval-Print-Loop is an interactive shell where one can write expressions and the REPL responds with their value.
    - In SBT, type `console` to start a Scala REPL in any direction with a `build.sbt` file.


- How are non-primitive expressions evaluated?
    1. Take the leftmost operator
    2. Evaluate its operands (left before right)
    3. Apply the operator to the operands.

    Example:
    ```scala
    def pi = 3.14159
    def radius = 10
    (2 * pi * radius)
    ```
    is evaluated as:
    ```
    (2 * 3.14159) * radius
    6.28318 * radius
    6.28318 * 10
    62.8318
    ```

- `def`
    - Definitions can have parameters
        ```scala
        def square(x: Double) = x * x
        square(2)
        square(square(4))

        def sumOfSquares(x: Double, y: Double) = square(x) + square(y)
        ```
    - Function types are specified following a colon
    - If a return type is given, it follows the parameter list.

- Primitive types are as in Java:
    - `Int`
    - `Long`
    - `Float`
    - `Double`
    - `Char`
    - `Short`
    - `Byte`
    - `Boolean`

- Application of parameetrized functions:
    1. Evaluate all function arguments, from left to right.
    2. Replace the function application by the function's right-hand side, and, at the same time
    3. Replace the formal parameters of the function by the actual arguments.

    Example:
    ```scala
    sumOfSquares(3, 2+2)
    sumOfSquares(3, 4)
    square(3) + square(4)
    3 * 3 + square(4)
    9 + square(4)
    9 + 4 * 4
    9 + 16
    25
    ```

- The substitution model
    - The scheme of expression evaluation.
    - The idea: all evaluation does is reduce an expression to a value.
    - Does every expression reduce to a value (in a finite number of steps?)
        - No. Counter example
            ```scala
            def loop: Int = loop
            loop
            ```

- An alternative evaluation strtegy.
    - One could instead apply the function to unreduced arguments.

    Example:
    ```scala
    sumOfSquares(3, 2+2)
    square(3) + square(2+2)
    3 * 3 + square(2+2)
    9 + square(2+2)
    9 + (2+2) * (2+2)
    9 + 4 * (2+2)
    9 + 4 * 4
    25
    ```

- The first evaluation strategy is known as _call-by-value_, the second is known as _call-by-name_.
    - Both strategies reduce to the same final values when
        - the reduced expression consists of pure functions, and
        - both evaluations terminate.
    - Call-by-value's advantage: evaluates every function argument only once.
    - Call-by-name's advantage: function argument is not evaluated if the corresponding parameter is unused in the evaluation of the function body.

## 1.3 Evaluation Strategies and Termination

- Do call-by-name and call-by-value strategies terminate in the same expression if termination is not guaranteed?
    - if CNV evaluation terminates, so does CBN.
    - the other direction is not true.
        ```scala
        def first(x: Int, y: Int) = x
        first(1, loop) // does not terminate
        ```
        evaluates under CBN but not under CBV.
    - Scala normally uses CBV, unless type of a function parameter starts with `=>`
        ```scala
        def constOne(x: Int, y: => Int) = 1
        constOne(1, loop) // terminates
        ```

## 1.4 Conditionals and Value Definitions

- Conditionals
    ```scala
    def abs(x: Int) = if x >= 0 then x else -x
    ```
- Boolean expressions
    ```scala
    true false // constants
    !b // negation
    b && b // conjunction
    b || b // disjunction
    ```
- Comparisons: `e <= e, e >= e, e < e, e > e, e == e, e != e`

- Short-circuit evaluation:
    ```scala
    true || e --> true
    false && e --> false
    ```
    The above don't need their right operand to be evaluated. These expressions use "short-circuit evaluation".

- Value definitions
    - the `def` form is "by-name", its right hand side is evaluated on each use.
    - the `val`form is "by-value". Example:
        ```scala
        val x = 2
        val y = square(x)
        ```
        The RHS of a `val` definition is evaluated at the point of the definition.
    - Difference:
        ```scala
        def x = loop // is OK
        val x = loop // not ok, infinite loop
        ```

- Exercise: implement `and(x, y)` and `or(x, y)`
    ```scala
    def and(x: Boolean, y: => Boolean): Boolean = 
        if x then y else false
    ```
    `y` needs to CBN for short-circuit evaluation.

## 1.5 Exammple: Square roots with Newton's method

```scala
def sqrt(x: Double): Double = ...

/*
newton's method
1. start with initial estimate `y` (pick `y` = 1)
2. improve the estiamte by taking the mean of `y` and `x/y`
*/

def sqrtIter(guess: Double, x: Double): Double = 
    if isGoodEnough(guess, x) then guess
    else sqrtIter(improve(guess, x), x)

def isGoodEnough(guess: Double, x: Double) = 
    abs(guess * guess - x) < 0.001

def improve(guess: Double, x:Double) = 
    (guess + x / guess) / 2

def sqrt(x: Double): Double = sqrtIter(1, x)
```
- Return type is required for recursive functions. For non-recursive, return type is optional.

## 1.6 Blocks and Lexical Scope

```scala
def sqrt(x: Double) = {
    def sqrtIter(guess: Double, x: Double): Double =
        if isGoodEnough(guess, x) then guess
        else sqrtIter(improve(guess, x), x)

    def improve(guess: Double, x: Double) =
        (guess + x / guess) / 2

    def isGoodEnough(guess: Double, x: Double) =
        abs(square(guess) - x) < 0.001
    
    sqrtIter(1.0, x)
}
```
- A block is delimited by braces `{...}`
- Contains a sequence of definitions or expressions
- The last element of a block is an expression that defines its value. This can be preceded by auxiliary definitions.
- Blocks are themselves expressions; a block may appear where an expression can.
- In Scala 3, braces are optional (i.e implied) around a correctly indented expression.

- Blocks and visibility
    - Definitions inside a block are only visible within the block.
    - Definitions inside a block _shadow_ definitions of the same names outside the block. (i.e, outer definitions don't have any impact inside the block if same name exists within the block)
        ```scala
        val x = 0
        def f(y: Int) = y + 1
        val y =
            val x = f(3)
            x * x
        val result = y + x
        ```
        result = 16
    - Definitions of outer blocks are visible inside a block unless they are shadowed. So cleaner sqrt function
        ```scala
        def sqrt(x: Double) =
            def sqrtIter(guess: Double): Double =
                if isGoodEnough(guess) then guess
                else sqrtIter(improve(guess))
            
            def improve(guess: Double) =
                (guess + x / guess) / 2
            
            def isGoodEnough(guess: Double) =
                abs(square(guess) - x) < 0.001
            
            sqrtIter(1.0)
        ```

- Semicolons
    - if there are more than one statments on a line, they need to be separated by semicolons.
        ```scala
        val y = x + 1; y * y
        ```

## 1.7 Tail recursion

- Evaluation a function application:
    - One simple rule: One evaluates a function application f(e1, ..., en)
        - by evaluating the expressions e1, . . . , en resulting in the values v1, ..., vn, then
        - by replacing the application with the body of the function f, in which
        - the actual parameters v1, ..., vn replace the formal parameters of f
    ```scala
    def f(x1, ..., xn) = B; ... f(v1, ..., vn)
    ```
    is the same as
    ```scala
    def f(x1, ..., xn) = B; ... [v1/x1, ..., vn/xn] B
    ```
    where `[v1/x1, ..., vn/xn] B` means the expression `B` in which all occurrences of `xi` have been repaced by `vi`. This is called a __substitution__.

- Example:
    ```scala
    def gcd(a: Int, b: Int): Int =
        if b == 0 then a else gcd(b, a % b)
    ```
    `gcd(14, 21)` is evaluated as 
    ```scala
    → if 21 == 0 then 14 else gcd(21, 14 % 21)
    → if false then 14 else gcd(21, 14 % 21)
    → gcd(21, 14 % 21)
    → gcd(21, 14)
    → if 14 == 0 then 21 else gcd(14, 21 % 14)
    →→ gcd(14, 7)
    →→ gcd(7, 0)
    → if 0 == 0 then 7 else gcd(0, 7 % 0)
    ```

- Another example:
    ```scala
    def factorial(n: Int): Int =
        if n == 0 then 1 else n * factorial(n - 1)
    ```
    `factorial(4)` is evaluated as
    ```scala
    → if 4 == 0 then 1 else 4 * factorial(4 - 1) 3-> →→ 4 * factorial(3)
    →→ 4 * (3 * factorial(2))
    →→ 4 * (3 * (2 * factorial(1)))
    →→ 4 * (3 * (2 * (1 * factorial(0)))
    →→ 4 * (3 * (2 * (1 * 1)))
    →→ 24
    ```
    What's the difference between the two?

- If a function calls itself as its last action, the function's stack frame can be reused. This is called _tail recursion_. (Implementation consideration)
    - Tail recursive functions are iterative processes.

- In scala one can require that a function is tail-recursive using `@tailrec` annotation
    ```scala
    import scala.annotation.tailrec

    @tailrec
    def gcd(a: Int, b: Int): Int = ...
    ```

- Exercise: Tail-recursive version of `factorial`
    ```scala
    def fact(n: Int) = 
        def factIter(n: Int, mult: Int): Int = 
            if n == 1 then mult else factIter(n - 1, mult * n)

        factIter(n, 1)
    ```
