# Automated Investment and Betting Advisor

## Overview

This project provides an automated system for investment advice and sports betting recommendations. The system is designed to help users manage their assets effectively by maximizing expected returns while minimizing risk. It utilizes fuzzy logic to handle the uncertainty associated with predicting outcomes and evaluating risks.

## Features

- **Automated Investment Advice**: Helps users allocate assets based on their risk tolerance, time frame, and investment objectives.
- **Sports Betting Advisor**: Recommends optimal betting combinations based on user preferences and team performance.
- **Fuzzy Logic Analysis**: Handles uncertainty in winning probabilities and risk assessments.

## How It Works

1. **User Preferences**: Read the user's preferences, including risk tolerance and bet amount.
2. **Bet Analysis**: Analyze bets by calculating fuzzy winning chances based on recent team performance and assess risk based on the bet amount and winning chance.
3. **Results**: Print out the recommended bets and their associated risks.

## Example

An example configuration is provided in the `example.clp` file. This file contains:
- Bet information
- User preferences
- Team performance ratings

The system will calculate the risk for each bet based on the given preferences and team ratings.

## Variables and Templates

### Common Variables
- **match-score**: Slot for computed fuzzy match score.
- **bet-type**: Type of bet (e.g., straight wager).
- **bet-type-arg**: Argument for bet type (e.g., Win or Lose).
- **provider**: Betting company providing the service.

### Preference Variables
- **pid**: ID of the preference.
- **sport**: Preferred sport type.
- **currency**: Preferred currency.
- **bet-amount**: Preferred bet amount.

### Bet Variables
- **bid**: ID of the bet.
- **odds**: Decimal or Continental odds (e.g., 6.0, 2.0, 1.5).
- **winning-rate**: Winning rate of the bet (computed).
- **timeslot-start/end**: Start and end time of the bet (in epoch time).
- **currency**: Currency provided by the provider.

### Team Variables
- **name**: Name of the team.
- **rate**: Performance rate of the team.
- **sport**: Sport played by the team.

### Fuzzy Variables
- **riskFvar**: Risk fuzzy variable (0 to 10, where 0 is no risk and 10 is very risky).
- **betAmountFvar**: Bet amount fuzzy variable (0 to 10000 USD).
- **winningChanceFvar**: Winning chance fuzzy variable (0 to 1).
- **expectedFvar**: Expected value fuzzy variable (0 to 2).
- **teamRateFvar**: Team performance rate fuzzy variable (0 to 10).

## Usage

1. **Set Up FuzzyJ**:
   - Set the classpath to where your FuzzyJ library is located. Add the following entry to your `.classpath` file:
     ```xml
     <classpathentry kind="lib" path="<path to fuzzy>/fuzzyJ-2.0.jar"/>
     ```

2. **Configure Jess**:
   - Set the Jess main class to `nrc.fuzzy.jess.FuzzyMain`.

3. **Run the Example**:
   - Unzip the project and run the batch file with the example configuration in the Jess shell:
     ```shell
     <path-to-directory>/exampleX.clp
     ```

4. **Enjoy**: Have fun analyzing bets and managing investments!
