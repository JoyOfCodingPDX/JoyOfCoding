class Currency {
  char symbol;
  double exchangeRateInDollars;
  double amount;

  String toString() {
    return "${symbol}${amount * exchangeRateInDollars}"
  }
}