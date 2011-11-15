l = [1, 2, 3]
iter = l.iterator()
println "Start ${iter}"
while (iter) {
  println iter++     // This is not what I'd expect.  ++ seems to convert iter into an Integer
  println iter.class
}

println "End"