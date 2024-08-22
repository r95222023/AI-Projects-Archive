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
    			 		(multislot provider)
    					(slot validity (default unknown)))

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



(deftemplate Analysis (slot bid)
    			 	  (slot expected-value)
    				  (slot suggested-bet-amount)
    				  (slot risk))

(deftemplate Remove (slot pid)
    			 	(slot bid))

(deftemplate test-temp (slot sport))