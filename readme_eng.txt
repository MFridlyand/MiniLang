And so before you the simplest programming language, originally written for tutoring purposes. The language supports:
    * arithmetical and logical expressions
    * basic algorithmic constructs ( if while )
    * local and global variables of the type double
    * arrays
    * user defined functions and recursion
    * possibility to extend functionality by adding built-in functions
    The main purpose of the writing this language is to have the simplest implementation possible , there is no AST built, no byte code is generated.
The input stream is divided into tokens by the simplest lexer, then recursive descent algorithm is used to perform all the computations.
Let's take a look at a few examples:

Example 1:

function gcd ( m , n )
{
	while  m != n 
	{
		if m > n 
			{ m = m - n }
		else
			{ n = n - m }
	}
	return m
}
print call gcd ( 100 , 40 )

Output: 20

Here we define a function that computes the GCD of two numbers, using the Euclidean algorithm, the arguments are passed by value.
call keyword is specified before function name to perform a function call. Note that the tokens are separated from each other by spaces and line breaks

Example 2:

function sort ( a )
{
	var sz = call array_size ( a )
	if sz < 2
	{
		return 0
	}
	var i = 0
	var j = 0
	var t = 0
	while i < sz
	{
		j = i + 1
		while j < sz
		{
			if a [ j ] < a [ i ]
			{
				t = a [ i ]
				a [ i ] = a [ j ]
				a [ j ] = t
			}
			j = j + 1
		}		
		i = i + 1
	}
}

var data = call make_array ( 0 )
call array_add ( data , 10 )
call array_add ( data , 4 )
call array_add ( data , 5 )
call array_add ( data , -5 )
call array_add ( data , 1 )
call array_add ( data , 2 )
call array_add ( data , 3 )

call printArray ( data )
call sort ( data )
print "sorted:"
call printArray ( data )

This example demonstrates the implementation of sorting an array by a simple selection method. The input array is passed by reference,
var keyword is used to declare a variable. return keyword is used to break function execution and return a value from function.
The following built-in functions are defined to work with arrays in the language:
    * make_array ( size ) - creates an array with a specified size
    * array_size ( array ) - returns array size
    * array_add ( array , value ) - adds an element to the array
    * array_copy ( array ) - copies an array
    * array_free ( array ) - frees the memory allocated for the array

Despite its simplicity, the language is turing complete, you can find other examples in examples folder, in particular, an example of the 
linked list implementation.