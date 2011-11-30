def numbers = new ArrayList()
numbers << 1 << 2 << 3 << 4 << 5
numbers.each { println it }
def squares = numbers.collect { it ** 2 }
println squares
println numbers.count { it % 2 == 0 }
println numbers.inject(0) { sum, value -> sum + value }
