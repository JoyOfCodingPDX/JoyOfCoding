def empty = [ : ]
def grades = [ 'Dave' : 92, 'Joe' : 85, 'Lyle' : 98 ]
println grades.getClass()
println grades['Dave']
println grades.'Joe'
def name = 'Lyle'
println grades[name]
grades.each { key, value -> println "$key -> $value"}
println grades.inject(0, { sum, entry -> sum + entry.value})