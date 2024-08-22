(batch "rules_fuzzy.clp")
;;;**********************
;;;* INITIAL STATE RULE * 
;;;**********************

(assert (Sport (name NBA)
    		  (bet-type-available Straight-Wagers Player-Propositions Game-Propositions Parlays Teasers Live-Wagers Half-and-Quarter-Wagers Round-Robins Futures Spread-Betting)
    		  (porvider-available Bet365 FanDuel BetRivers DraftKings)))
(assert (Sport (name NFL)
    		  (bet-type-available Straight-Wagers Player-Propositions Game-Propositions Parlays Teasers Live-Wagers Half-and-Quarter-Wagers Round-Robins Futures Spread-Betting)
    		  (porvider-available Bet365 FanDuel BetRivers DraftKings)))
(assert (Sport (name MLB)
    		  (bet-type-available Straight-Wagers Player-Propositions Game-Propositions Parlays Teasers Live-Wagers Half-and-Quarter-Wagers Round-Robins Futures Spread-Betting)
    		  (porvider-available Bet365 FanDuel BetRivers DraftKings)))
;;;;;;;
;NBA Eastern teams
;;;;;;;
(assert (Team (sport NBA)
    			  (name Milwaukee-Bucks)
    			  (rate 7)))
(assert (Team (sport NBA)
    			  (name Chicago-Bulls)
    			  (rate 3)))
;;;;;;;
;NBA Western teams
;;;;;;;
(assert (Team (sport NBA)
    			  (name LA-Lakers)	
    			  (rate 7)))
(assert (Team (sport NBA)
    			  (name Utah-Jazz)
    			  (rate 4)))

(assert (Bet-Type (name Straight-Wagers)
    		  (bet-type-arg-1-available Win Lose)
    		  (bet-type-arg-2-available Empty)))
(assert (Bet-Type (name Game-Propositions)
    		  (bet-type-arg-1-available Win Lose)
    		  (bet-type-arg-2-available Scores-First Strikes)))
(assert (Provider (name Bet365)
    		  (payment-method-available Credit-Card Paypal Bitcoin)
    		  (currency-available USD CAD EUR GBP JPY)
    		  (country-available USA AUS CAN UK JPN FR GER)
        	  (fee 0.01)
    		  (available TRUE)))
(assert (Provider (name FanDuel)
    		  (payment-method-available Credit-Card Paypal Bitcoin)
    		  (currency-available USD CAD EUR GBP JPY)
    		  (country-available USA AUS CAN UK JPN FR GER)
        	  (fee 0.01)
    		  (available TRUE)))