def pattern = ~/\d+/
def text = "2011-11-27"
def matcher = (text =~ pattern)
println "Matches? ${matcher ? 'Yes' : 'No'}"      // Yes
println "Exact match? ${text ==~ pattern ? 'Yes' : 'No'}"  // No
println "${matcher.size()} matches"
println matcher[0]  // 2011
println matcher[1]  // 11
