# Automated Sports Betting Advisor

## Domain

The Automated Sports Betting Advisor is an expert system designed to help users manage their betting portfolios effectively. The system aims to maximize expected returns while minimizing risks based on user preferences and risk tolerance. It provides investment advice by analyzing various bets and their associated risks and returns.

This type of expert system is used for tasks such as asset portfolio management, stock portfolio management, and sports betting advice. In particular, it assists users in finding the best combination of bets that aligns with their risk tolerance and investment objectives.

## Task

The primary task of this system is to serve as a simple sports betting advisor. The automated reasoning process involves the following steps:

1. **Read User Preferences:** Collect and interpret the user's betting preferences.
2. **Validate and Filter Bets:** Validate the provided bets and filter out any unwanted ones. Bets are sourced either through web crawlers or provided by sports betting companies via APIs.
3. **Analyze Bets:** Calculate the expected value and risk of each bet according to the user's risk tolerance.
4. **Print Results:** Output the best betting options based on the analysis.

## Examples

1. **Risk Categorization:** Bets are categorized as high, medium, or low risk based on the user-defined risk bounds.
2. **Bet Filtering:** Demonstrates how bets that have expired are excluded from the analysis.
3. **Multi-Sport Bets:** Shows how the system handles bets across different sports and verifies the correctness of the matching process.

## Variables Used

### Common

- **sport:** The name of the sport.
- **bet-type:** The type of bet (e.g., straight wager).
- **bet-type-arg:** Arguments for different types of bets (e.g., Win or Lose).
- **country:** The country where the bet is placed.
- **payment-method:** The payment method used for betting.
- **provider:** The betting company providing the service.

### Preference

- **pid:** ID of the user's preference.
- **time-start/end:** Preferred start and end time (in epoch time).
- **risk-bound-high/mid/low:** User-defined bounds to categorize bets as high, mid, or low risk.
- **currency:** Preferred currency for the bet.
- **bet-amount:** Preferred amount for placing the bet.

### Bet

- **bid:** ID of the bet.
- **odds:** The odds of the bet (should be greater than 1).
- **winning-rate:** The calculated winning rate of the bet.
- **timeslot-start/end:** The start and end time (in epoch time) of the bet.
- **fee:** Fee required by the provider.
- **min/max-amount:** Minimum and maximum amount required to place the bet.
- **currency:** Currency provided by the provider.

### Provider

- **payment-method-available:** Payment methods provided by the provider.
- **currency-available:** Currencies available from the provider.
- **country-available:** Countries available for betting.
- **available:** Indicates whether the provider is currently available.

## Usage

1. Unzip the provided files.
2. Run the desired example batch file in the Jess shell (see `Jess, the Java Expert System Shell - Introduction.html` for more detail). For instance:
   ```
   batch "<path-to-directory>/exampleX.clp"
   ```
   where `X` corresponds to the example number.

## Notes

Ensure that all necessary files are correctly unzipped and paths are properly set to run the examples successfully.