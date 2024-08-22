;; ------------------------------------------------------------
;; DEFTEMPLATES
;; ------------------------------------------------------------
(deftemplate Preference (slot pid (default pref-1))
    					(multislot sport) 
    			 		(multislot bet-type)
    					(multislot bet-type-arg-1)
    				    (multislot bet-type-arg-2)
    					(multislot currency)
    					(multislot country)
    			 		(slot time-start)
    			 		(slot time-end)
    					(slot risk-bound-high)
     					(slot risk-bound-mid)
    					(slot risk-bound-low)
    					(slot portion-high)
     					(slot portion-mid)
    					(slot portion-low)
    					(multislot payment-method)
    			 		(slot bet-amount)
    					(slot betAmountFv)
    			 		(multislot provider)
    					(slot validity (default unknown)))

(deftemplate PreferenceFuzzy (slot pid (default pref-1))
    					(slot betAmountFv))

(deftemplate Sport (slot name)
    			   (multislot bet-type-available)
    			   (multislot porvider-available))

(deftemplate Bet-Type (slot name)
    			 	  (multislot bet-type-arg-1-available) 
    				  (multislot bet-type-arg-2-available))


(deftemplate Provider (slot name)
    			 	  (multislot currency-available)
    				  (multislot country-available)
    				  (multislot payment-method-available)	
    				  (slot available)
    				  (slot fee))

(deftemplate Bet (slot bid)
    			 (slot sport) 
    			 (slot bet-type)
    			 (slot bet-type-arg-1)
     			 (slot bet-type-arg-2)
    			 (slot odds)
    ;;
    			 (slot teamW)
    			 (slot teamL)
    			 
    ;;
    			 (slot winning-rate)
    			 (slot timeslot-start)
    			 (slot timeslot-end)
    			 (slot currency)
    			 (slot fee)
    			 (slot min-amount)
    			 (slot max-amount)
    			 (slot provider)
    			 (slot match (default unknown))
   				 (slot validity (default unknown)))

(deftemplate BetAnalysis (slot bid)
    			  (slot match-score)
    			  (slot winningChanceFv)
    			  (slot winning-rate)
    			  (slot expected)
    			  (slot expectedFv))

(deftemplate ExpectedFuzzy (slot bid)
    			  (slot match-score)
    			  (slot expected)
    			  (slot expectedFv))

(deftemplate RiskFuzzy (slot bid)
    				  (slot match-score)
    				  (slot riskFv))

;;
(deftemplate Team (slot sport)
    			  (slot name)
    			  (slot rate))

(deftemplate TeamAnalysis (slot sport)
    			  (slot name)
    			  (slot rate)
    			  (slot rateFv))
;;
(deftemplate Analysis (slot bid)
    			 	  (slot expected-value)
    				  (slot match-score)
    				  (slot suggested-bet-amount)
    				  (slot risk)
    				  (slot riskFv))

(deftemplate Remove (slot pid)
    			 	(slot bid))

(deftemplate test-temp (slot sport))