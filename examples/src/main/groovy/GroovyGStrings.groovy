value = 4
message = "The value is ${value}"
println message    // 4
value = 5
println message    // 4 !!!
message = "The value is ${ -> value}"
println message    // 5
value = 6
println message    // 6
