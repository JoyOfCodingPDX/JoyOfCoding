import static java.util.Calendar.*

println 1..5
println "a".."c"
println "aa".."ac"

switch (Calendar.instance[DAY_OF_WEEK]) {
  case MONDAY..FRIDAY:
    println "Rock and roll all night"
    break;
  case SATURDAY..SUNDAY:
    println "Party every day"
    break;
  default:
    println "Huh?"
}
