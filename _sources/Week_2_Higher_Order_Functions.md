# 2 Higher Order Functions

## 2.1 Introduction
- Functional Programming languages treat functions as "first-class values".
    - A function can be passed as a parameter and returned as a result.
- Functions that take other functions as parameters or that you turn them as results are called higher order functions. 
- Example:
    - Consider sum of integers between `a` and `b`.
        ```scala
        def sumInts(a: Int, b: Int): Int =
            if a > b then 0 else a + sumInts(a + 1, b)
        ```
    - What about sum of cubes of these integers?
        ```scala
        def cube(x: Int): Int = x * x * x
        
        def sumCubes(a: Int, b: Int): Int =
            if a > b then 0 else cube(a) + sumCubes(a + 1, b)
        ```
    - What about factorials?
        ```scala
        def sumFactorials(a: Int, b: Int): Int =
            if a > b then 0 else factorial(a) + sumFactorials(a + 1, b)
        ```
    - These are special cases of $\sum\limits_{i=a}^{b}f(i)$ for various $f$.

- Instead, we can define a higher order function as:
    ```scala
    def sum(f: Int => Int, a: Int, b: Int): Int =
        if a > b then 0
        else f(a) + sum(f, a + 1, b)

    def sumInts(a: Int, b: Int) = sum(id, a, b)
    def sumCubes(a: Int, b: Int) = sum(cube, a, b)
    def sumFactorials(a: Int, b: Int) = sum(fact, a, b)
    ```
    where
    ```scala
    def id(x: Int): Int = x
    def cube(x: Int): Int = x * x * x
    def fact(x: Int): Int = if x == 0 then 1 else x * fact(x - 1)   
    ```
- The type A => B is the type of a function that takes an argument of type A and returns a result of type B.

- _Anonymous functions_: we don't always have to define the functions we pass using `def`.
    - Example:
        ```scala
        (x: Int) => x * x * x
        ```
        `(x: Int)` is the parameter of the function and `x * x * x` is the body. The type of parameter can be omitted if it can be inferred by the compiler from the context.
    - If there are several parameters, separate by commas:
        ```scala
        (x: Int, y: Int) => x + y
        ```

- Theory:
    - An anonymous function $(x_1:T_1, \ldots, x_n: T_n) \implies E$ can always be expressed using `def` as follows:
    $$\text{def} ~f(x_1:T_1, \ldots, x_n: T_n) = E; f$$
    - The above is the same as an anonymous function. Thus, they are _syntactic sugar_.

- With anonumous functions:
    ```scala
    def sumInts(a: Int, b: Int) = sum(x => x, a, b)
    def sumCubes(a: Int, b: Int) = sum(x => x * x * x, a, b)
    ```

- Exercise: Tail-recursive version of the sum function?
```scala
def sum(f: Int => Int, a: Int, b: Int): Int =
    def loop(a: Int, acc: Int): Int =
        if a > b then acc
        else loop(a + 1, acc + f(a))
    loop(a, 0)
```

## 2.2 Currying

- Consider:
    ```scala
    def sumInts(a: Int, b: Int) = sum(x => x, a, b)
    def sumCubes(a: Int, b: Int) = sum(x => x * x * x, a, b)
    def sumFactorials(a: Int, b: Int) = sum(fact, a, b)
    ```
    Do we need to specify `a` and `b` in these cases?

- Alternate `sum`:
    ```scala
    def sum(f: Int => Int): (Int, Int) => Int =
        def sumF(a: Int, b: Int): Int =
            if a > b then 0
            else f(a) + sumF(a + 1, b)
    sumF
    ```
    `sum` takes a function as input and returns another function (`sumF`)~
- Notice that the inner `sumF` function takes `a` and `b` as inputs.
- We can redefine:
    ```scala
    def sumInts = sum(x => x)
    def sumCubes = sum(x => x * x * x)
    def sumFactorials = sum(fact)

    sumCubes(1, 10) + sumFactorials(2, 10) // works!
    ```
- We can make this simpler!
    ```scala
    sum(cube)(1, 10)
    ```
    is equivalent to `sumCubes`. Function application associates to the left: `(sum(cube))(1, 10)`.

- Wait, we can make it even simpler.
    ```scala
    def sum(f: Int => Int)(a: Int, b: Int): Int =
        if a > b then 0 else f(a) + sum(f)(a + 1, b)
    ```
    - Notice this is actually very similar to:
        ```scala
        def sum(f: Int => Int, a: Int, b: Int): Int =
            if a > b then 0 else f(a) + sum(f, a + 1, b)
        ```
        but instead we're composing functions.

- Theory:
A definition of a function with multiple parameter lists
$$ \text{def } f(ps_1)\ldots(ps_n) = E $$
where $n>1$ is equivalent to
$$ \text{def } f(ps_1)\ldots(ps_{n-1}) = \{\text{def } g(ps_n) = E; g\} $$
where $g$ is a fresh identifier. For short (the last part is an anonymous function),
$$ \text{def } f(ps_1)\ldots(ps_{n-1}) = ps_n \implies E $$
By repeating $n$ times,
$$ \text{def } f = (ps_1 \implies (ps_2 \implies \ldots (ps_n \implies E)\ldots)) $$
Theoretically, you wouldn't need function parameters in `def` at all, you would only need anonymous functions. This is called "currying".

- What is the type of `sum`?
    ```scala
    def sum(f: Int => Int)(a: Int, b: Int): Int = ...

    (Int => Int) => (Int, Int) => Int
    ```

- Exercise: Write a product function, that calculates the product of the values of a function for the points of a given interval, similar to what we did to see sum function. As a second step, write the factorial function in terms of product. And as a third step, can you may be write a more general function, that generalizes both sum and product? 

```scala
def product(f: Int => Int)(a: Int, b: Int): Int =
    if a > b then 1
    else f(a) * product(f)(a + 1, b)
```

```scala
def factorial(n: Int): Int = product(x => x)(1, n)
```

    
```scala
def mapReduce(f: Int => Int, gather: (Int, Int) => Int, zero: Int)(a: Int, b: Int): Int = 
    def recur(a: Int): Int = 
        if a > b then zero
        else gather(f(a), recur(a + 1))
    recur(a)
```

```scala
def sum(f: Int => Int) = mapReduce(f, (x, y) => x + y, 0)
def product(f: Int => Int) = mapReduce(f, (x, y) => x * y, 1)
```

## 2.3 Example: Finding Fixed Points

- A number $x$ is a "fixed point" of a function $f$ if $f(x) = x$.
- Function for finding a fixed point:
    ```scala
    val tolerance = 0.0001

    def isCloseEnough(x: Double, y: Double) =
        abs((x - y) / x) < tolerance

    def fixedPoint(f: Double => Double)(firstGuess: Double): Double =
        def iterate(guess: Double): Double =
            val next = f(guess)
            if isCloseEnough(guess, next) then next
            else iterate(next)
        iterate(firstGuess)
    ```

- `sqrt(x)` = the number $y$ such that $y = x / y$. So `sqrt(x)` is a fixed point of the function (`y => x/y`).
    ```scala
    def sqrt(x: Double) =
        fixedPoint(y => x / y)(1.0)
    ```
    Unfortunately, this doesn't converge. (Oscillates between 1 and 2)
    ```scala
    def sqrt(x: Double) = fixedPoint(y => (y + x / y) / 2)(1.0)
    ```
    The _averaging_ prevents estimations from varying too much.

- It can be written more clearly:
    ```scala
    def averageDamp(f: Double => Double)(x: Double): Double =
        (x + f(x)) / 2

    def sqrt(x: Double) = fixedPoint (averageDamp (y => x/y)) (1.0)
    ```

- As a programmer, one must look for opportunities to abstract and reuse.

## 2.4 Scala Syntax Summary

- Types: 
    ```
    Type = SimpleType | FunctionType
    FunctionType = SimpleType ‘= > ’ Type
                    | ‘( ’ [ Types ] ‘) ’ ‘= > ’ Type
    SimpleType = Ident
    Types = Type { ‘ , ’ Type }
    ```

- Expressions:
    - An identifier such as x, isGoodEnough,
    - A literal, like 0, 1.0, ”abc”,
    - A function application, like sqrt(x),
    - An operator application, like -x, y + x,
    - A selection, like math.abs,
    - A conditional expression, like if x < 0 then -x else x,
    - A block, like { val x = abs(y) ; x * 2 }
    - An anonymous function, like x => x + 1.

- Definitions: 
    - A function definition, like def square(x: Int) = x * x
    - A value definition, like val y = square(2)

## 2.5 Functions and Data

- Example: package for doing rational arithmetic. 
    ```scala
    def addRationalNumerator(n1: Int, d1: Int, n2: Int, d2: Int): Int
    def addRationalDenominator(n1: Int, d1: Int, n2: Int, d2: Int): Int
    ```
    is a possibility but difficult. Instead, combine in a different data structure.

- In Scala, define a _class_:
    ```scala
    class Rational(x: Int, y: Int):
        def numer = x
        def denom = y
    ```
    Introduces two entities:
    - A new _type_, named `Rational`.
    - A _constructor_ `Rational` to create elements of this type.
    - Scala keeps the names of types and values in different namespaces, so there's no conflict.

- We call the elements of a class type _objects_.
- We select the members of an object with the infix operator ".".
    ```scala
    val x = Rational(1, 2) // x: Rational = Rational@2abe0e27
    x.numer // 1
    x.denom // 2
    ```
- We can then define addition:
    ```scala
    def addRational(r: Rational, s: Rational): Rational =
        Rational(
            r.numer * s.denom + s.numer * r.denom,
            r.denom * s.denom)

    def makeString(r: Rational): String =
        s"${r.numer}/${r.denom}" // interpolated string similar to f-strings

    makeString(addRational(Rational(1, 2), Rational(2, 3))) > 7/6
    ```

- We can define and package functions operating on a data abstraction in the abstraction itself. Such functions are called methods.
    ```scala
    class Rational(x: Int, y: Int):
        def numer = x
        def denom = y
        def add(r: Rational) =
            Rational(numer * r.denom + r.numer * denom,
                    denom * r.denom) // called as x.add(y)
        def mul(r: Rational) = ...
        ...
        override def toString = s"$numer/$denom" // all scala classes support a toString function by default. 
        // Override redefines this. 
        // Braces aren't always necessary if it's a simple expression
    ```
- Exercise: Negative of a rational number
    ```scala
    class Rational(x: Int, y: Int):
        def numer = x
        def denom = y
        def neg = Rational(-numer, denom)
        
        def add(r: Rational) =
            Rational(numer * r.denom + r.numer * denom,
                    denom * r.denom) // called as x.add(y)
        
        def mul(r: Rational) = ...
        
        def sub(r: Rational) = add(r.neg)
        ...
        override def toString = s"$numer/$denom"
    ```

## 2.6 More Fun with Rationals

- Rational numbers weren't always represented in their simplest form.
    ```scala
    class Rational(x: Int, y: Int):
        private def gcd(a: Int, b: Int): Int =
            if b == 0 then a else gcd(b, a % b)
        private val g = gcd(x, y) // private members can be accesed only from inside the class
        def numer = x / g
        def denom = y / g // call-by-value so we can reuse.
        ...
    ```
    Alternatively,
    ```scala
    class Rational(x: Int, y: Int):
        private def gcd(a: Int, b: Int): Int =
            if b == 0 then a else gcd(b, a % b)
        val numer = x / g
        val denom = y / g // advantageous when numer and denom are called often.
        ...
    ```
- Clients observe exactly the same behavior in each case. Ability to choose different implementations: "data abstraction" -> Cornerstone of software engineering.

- `this`: represents the object on which the current method is executed.
    ```scala
    class Rational(x: Int, y: Int):

        def less(that: Rational): Boolean =
            numer * that.denom < that.numer * denom // x1y2 < x2y1 (no need to use this.numer here but is equivalent)

        def max(that: Rational): Rational =
            if this.less(that) then that else this // this is required (can't use numer and denom here conveniently)
    ```

- `require`: predefined function that enfores conditions
    ```scala
    class Rational(x: Int, y: Int):
        require(y > 0, ”denominator must be positive”)
        ...
    ```
    Throws an `IllegalArgumentException` if condition is false.

- `assert`: similar to `require`. Failing an `assert` throws an `Assertion` error.
    - `require` used to enforce a precondition.
    - `assert` used to check the code of the function itself.

- In Scala, a class implicity introduces a constructor. The primary constructor:
    - takes the parameters of the class
    - and executes all statements in the class body (such as `require` or value definitions)

- Scala also allows the declaration of _auxiliary constructors_. These are methods named `this`. Example:
    ```scala
    class Rational(x: Int, y: Int):
        def this(x: Int) = this(x, 1)
    ```

- End markers: (optional) used to identify the end of a class. Example, adding `end Rational`. Can also be used with `def` or with `if`.

## 2.7 Evaluations and Operators

- How is an instantiation of the class $C(e_1, \ldots, e_m)$ evaluated?
    - The expression arguments $e_1, \ldots, e_m$ are evaluated like the arguments of a normal function. The resulting expression $C(v_1, \ldots, v_m)$ is already a value. (?)

- Extension methods:
    - Defining all methods that belong to a class inside the class itself can lead to large classes and isn't modular.
    - Methods that do not need to access the internals of a class can alternatively be defined as extenstion methods.
    - Example:
        ```scala
        extension (r: Rational)
            def min(s: Rational): Boolean = if s.less(r) then s else r
            def abs: Rational = Rational(r.numer.abs, r.denom)
        ```
    - Caveats:
        - Extensions can only add new members, not override existing ones.
        - Extensions cannot refer to other class members via `this`

- Operators: for integers, we write `x + y` but for `Rational` we write `r.add(s)`. In Scala, we can make this natural.
    - Step 1: Relaxed Identifiers. The following are valid identifiers in Scala:
        - `x1 * +?%& vector_++ counter_=x1 * +?%& vector_++ counter_=`
            ```scala
            extension (x: Rational)
                def + (y: Rational): Rational = x.add(y)
                def * (y: Rational): Rational = x.mul(y)
            ```
    - Step 2: Infix Notation: An operator method with a single parameter can be used as an infix operator.
        ```scala
        extension (x: Rational)
            infix def min(that: Rational): Rational = ...

        r + s                   r.+(s)
        r < s /* in place of */ r.<(s)
        r min s                 r.min(s)
        ```

- Precedence Rules for operators: similar to rules we have for integers. From low to high:
    ```scala
    (all letters)
    |
    ^
    &
    < >
    = !
    :
    + -
    * / %
    (all other special characters)
    ```



    
    
