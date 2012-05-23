Number.metaClass.dollars = { -> new CurrencyBean( symbol: '$', exchangeRateInDollars: 1.0, amount: delegate)}
Number.metaClass.cents = { -> new CurrencyBean( symbol: '$', exchangeRateInDollars: 0.01, amount: delegate)}

println 4.dollars()
println 5.cents()

