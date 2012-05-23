def file = new File("../resources/edu/pdx/cs410J/xml/phonebook.xml")
phonebook = new XmlParser().parse(file)
println phonebook.children()
phonebook.'*'.address.each { println "${it.city}, ${it.zip}"}
println phonebook.children()[1].address.zip
phonebook.children().each { println it.phone.@number }
