# 3 Data and Abstraction

## 3.1 Class Hierarchies

- Abstract classes: can contain members which are missing an implementation.
    ```scala
    abstract class IntSet:
        def incl(x: Int): IntSet
        def contains(x: Int): Boolean
    ```
    - No direct instances of an abstract class can be created. `IntSet()` call would be illegal.

- Consider implementing sets as binary trees. Two types: a tree for the empty set and a tree consiting of an integer and two sub-trees.
- Example: {1, 2, 4, 5}
    ```
              4
             / \
            /   \
           /     \
          /       \
         1         5
        / \       / \
       /   \     /   \
      /     \   e     e
     e       2  
            / \
           e   e
    ```
- Implementation:
    ```scala
    class Empty() extends IntSet:
        def contains(x: Int): Boolean = false
        def incl(x: Int): IntSet = NonEmpty(x, Empty(), Empty())

      x
     / \
    e   e
    ```
    ```scala
    class NonEmpty(elem: Int, left: Intset, right: IntSet) extends IntSet:
        
        def contains(x: Int): Boolean =
            if x < elem then left.contains(x)
            else if x > elem then right.contains(x)
            else true
        
        def incl(x: Int): IntSet =
            if x < elem then NonEmpty(elem, left.incl(x), right)
            else if x > elem then NonEmpty(elem, left, right.incl(x))
            else this

    end NonEmpty
    ```

- Persistent data structures: we can create new data structures with elements of existing data structures. For example, in `incl` adding an `x` less than `elem` would result in a new `IntSet` where `right` would exist in both new and old sets.
    
- Empty and NonEmpty both extend the class IntSet. This implies that the types Empty and NonEmpty conform to the type IntSet.
    - IntSet is called the _superclass_ of Empty and NonEmpty. Empty and NonEmpty are _subclasses_ of IntSet.
    - In Scala, any user-defined class extends another class. If no superclass is given, the standard class Object in the Java package java.lang is assumed.
    - The direct or indirect superclasses of a class C are called _base classes_ of C. So, the base classes of NonEmpty include IntSet and Object.

- Definitions of `contains` and `incl` in the classes `Empty` and `NonEmpty` _implement_ the abstract functions in the base trait `IntSet`.

- One can _redefine_ an existing, non-abstract definition in a subclass using `override`. Example:
    ```scala
    abstract class Base:
        def foo = 1
        def bar: Int

    class Sub extends Base:
        override def foo = 2
        def bar = 3
    ```

- Object definitions: In `IntSet` example, there's only a single empty `IntSet`; overkill to create many instances of it. Instead use an _object definition_.
    ```scala
    object Empty extends IntSet:
        def contains(x: Int): Boolean = false
        def incl(x: Int): IntSet = NonEmpty(x, Empty, Empty)
    end Empty
    ```
    This defines a __singleton object__ `Empty`. No other `Empty` instance can be created. Singleton objects are values.

- An object and class can have the same name (since Scala has two global namespaces: one for types, another for values)
    - Classes live in the type namespace, objects in value namespace.

- Companions: class and object with the same name in the same sourcefile.

- Programs: 
    ```scala
    object Hello:
        def main(args: Array[String]): Unit = println(”hello world!”)
    ```
    - To run from the command line:
        ```
        > scala Hello
        ```
    - Main methods:
        ```scala
        @main def birthday(name: String, age: Int) =
            println(s”Happy birthday, $name! $age years old already!”)
        ```
        ```
        > scala birthday Peter 11
        Happy Birthday, Peter! 11 years old already!
        ```

- Exercise: implement a `union` method for `IntSet`
    ```scala
    class Empty() extends IntSet:
        def contains(x: Int): Boolean = false
        def incl(x: Int): IntSet = NonEmpty(x, Empty(), Empty())
        def union(s: IntSet): IntSet = s

    class NonEmpty(elem: Int, left: Intset, right: IntSet) extends IntSet:
        
        def contains(x: Int): Boolean =
            if x < elem then left.contains(x)
            else if x > elem then right.contains(x)
            else true
        
        def incl(x: Int): IntSet =
            if x < elem then NonEmpty(elem, left.incl(x), right)
            else if x > elem then NonEmpty(elem, left, right.incl(x))
            else this

        def union(s: IntSet): IntSet = 
            left.union(right).union(s).incl(elem)

    end NonEmpty
    ```
    The union definition decomposes left and right to smaller and smaller Intsets on which we can use `incl`.

## 3.2 How Classes are Organized

- Packages:
    - Classes and objects are organized in packages.
    - To place them inside a package, use a clause at the top of your source file
        ```scala
        package progfun.examples

        object Hello
            ...
        ```
    - To refer to it: `progfun.examples.Hello`

- Imports
    - Suppose a class `Rational` exists in package `week3`
        ```scala
        val r = week3.Rational(1, 2)

        // or

        import week3.Rational
        val r = Rational(1, 2)
        ```
    - Example imports
        ```scala
        import week3.Rational // imports just Rational
        import week3.{Rational, Hello} // imports both Rational and Hello
        import week3._ // imports everything in package week3
        ```
    - Automatic imports:
        - All members of package scala
        - All members of package java.lang
        - All members of the singleton object scala.Predef

- Traits
    - Similar to classes. A class can only have one superclass.
    - Declared like an abstract class
        ```scala
        trait Planar:
            def height: Int
            def wigth: Int
            def surface = height * width
        ```
    - Classes, objects and traits can inherit from at most one class but arbitrary many traits.
        ```scala
        class Square extends Shape, Planar, Movable ...

- Exceptions
    ```scala
    throw Exc
    ```
    aborts evaluation with the exception Exc. The type of this expression is `Nothing`

## 3.3 Polymorphism

- Linked Lists: constructed from two building blocks
    - Nil: the empty list
    - Cons: a cell containing an element and the remainder of the list

- Outline of a class hierarchy for IntList
    ```scala
    package week3

    trait IntList ...
    class Cons(val head: Int, val tail: IntList) extends IntList ...
    class Nil() extends IntList ...
    ```
    - The `Cons` class above is equivalent to
        ```scala
        class Cons(_head: Int, _tail: IntList) extends IntList:
            val head = _head
            val tail = _tail
        ```
        where `_head` and `_tail` are otherwise unused names.

- We can generalize this to any type using a type parameter.
    ```scala
    package week3

    trait List[T]
    class Cons[T](val head: T, val tail: List[T]) extends List[T]
    class Nil[T] extends List[T]
    ```
    Type parameters are written in square brackets.

- Complete Definition of List
    ```scala
    trait List[T]:
        def isEmpty: Boolean
        def head: T
        def tail: List[T]

    class Cons[T](val head: T, val tail: List[T]) extends List[T]:
        def isEmpty = false

    class Nil[T] extends List[T]:
        def isEmpty = true
        def head = throw new NoSuchElementException("Nil.head")
        def tail = throw new NoSuchElementException("Nil.tail")
    ```

- Functions can also have type parameters
    ```scala
    def singleton[T](elem: T) = Cons[T](elem, Nil[T])

    singleton[Int](1)
    singleton[Boolean](true)

    singleton(1)
    singleton(true) // also work, compiler figures out the type
    ```

- Polymorphism: a function type that comes "in many forms".
    - function can be applied to arguments of many types, or
    - type can have instances of many types

- We have seen two forms of polymorpshism:
    - subtyping: instances of a subclass can be passed to a base class (?)
    - generics: instances of a function or class are created by type parametrization

- Exercise: nth element of a list.
```scala
def nth[T](xs: List[T], n: Int): T = 
    if xs.isEmpty then throw IndexOutOfBoundsException()
    else if n == 0 then xs.head
    else nth(xs.tail, n - 1)
```

## 3.4 Objects Everywhere
- Pure object-oriented language: every value is an object.

I didn't take a lot of notes here. It was mostly references to Scala docs and implementation of Scala types from first principles.

## 3.5 Functions as Objects

- Function values are treated as objects in Scala.
- The function type `A => B` is an abbreviation for the class `scala.Function1[A, B]` defined as 
    ```scala
    package scala
    trait Function1[A, B]:
        def apply(x: A): B
    ```
