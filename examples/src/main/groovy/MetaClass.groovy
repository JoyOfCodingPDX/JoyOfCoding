Number.metaClass.dollars = { -> new Currency( symbol: '$', exchangeRateInDollars: 1.0, amount: delegate)}

println 4.dollars()

