# Coding Styles

`neovim-clojurescript` keep the following coding styles to deliver the clear meanings of
the code.

  
## Dynamic Symbols

Dynamic symbols have the star mark at the both sides, following the convention of Clojure
community.

```

(def *dynamic-symbol* 100)

(binding [*dynamic-symbol* 200]
  (+ 10 *dynamic-symbol*))
; => 210

*dynamic-symbol*
; => 100

```


## Global Symbols

Global symbols have one star mark at the end side. This makes it easy to identify the
global symbol in the function definitions.
 

```
(def global-symbol* 10)

(defn my-add [a b]
  (+ a b global-symbol*))

(my-add 100 200)
; => 310
```

## Local Reference Symbols

Local reference symbols like an atom have one exclamation mark at the end side.

```
(defn my-add [a b]
  (+ a b))
 
(defn my-add2 [a! b]
  (+ @a! b))

(let [local-reference! (atom 10)]
  (my-add @local-reference! 100))
; => 110

(let [local-reference! (atom 10)]
  (my-add2 local-reference! 100))
; => 110
```


## Global Reference Symbols

Global reference symbols like an atom have one star mark and one exclamation mark at the
end side.

```
(def global-reference*! 100)

(defn my-add [a b]
  (+ a b))
 
(defn my-add2 [a! b]
  (+ @a! b))


(my-add @global-reference*! 200))
; => 300

(my-add2 global-reference*! 200))
; => 300
```


## Global Const Symbols

Global const symbols consist of all capital letters.

```
(def GLOBAL-CONST ^:const 100)

(+ 10 GLOBAL-CONST)
; => 110
```

They are a little different from the global symbols from the viewpoint of the
compiler. The next example shows the difference.

```
(def GLOBAL-CONST ^:const 100)

(+ 10 GLOBAL-CONST)
; => 110

;; Compiler sees the above as the follwing. That is, GLOBAL-CONST expands into 100 before
;; compiling.
(+ 10 100)
```

```
(def global-symbol* 100)

(+ 10 global-symbol*)
; => 110

;; Compiler sees the above as the follwing. That is, global-symbol* doesn't expand into
;; 100 before compiling.

(+ 10 global-symbol*)
```

## Private Functions

The private functions have one star mark at the end, following the convention of `clojure.core`.

```
(defn- send* [] ,,,)

(defn send []
  (send* ,,,))
```
 



